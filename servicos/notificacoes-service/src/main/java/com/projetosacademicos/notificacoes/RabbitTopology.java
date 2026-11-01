package com.projetosacademicos.notificacoes;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.Collections;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.QueueBuilder;
import org.springframework.amqp.rabbit.config.SimpleRabbitListenerContainerFactory;
import org.springframework.amqp.rabbit.config.RetryInterceptorBuilder;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.retry.RejectAndDontRequeueRecoverer;
import org.springframework.amqp.support.converter.DefaultClassMapper;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.amqp.SimpleRabbitListenerContainerFactoryConfigurer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitTopology {
    public static final String EXCHANGE = "projetos.events";
    public static final String ROUTING_KEY = "projeto.cadastrado";
    public static final String QUEUE = "projeto_cadastrado";
    public static final String DEAD_LETTER_EXCHANGE = "projetos.events.dlx";
    public static final String DEAD_LETTER_ROUTING_KEY = "projeto.cadastrado.dlq";
    public static final String DEAD_LETTER_QUEUE = "projeto_cadastrado.dlq";

    @Bean
    public DirectExchange projetosExchange() {
        return new DirectExchange(EXCHANGE, true, false);
    }

    @Bean
    public Queue projetoCadastradoQueue() {
        return QueueBuilder.durable(QUEUE)
            .withArgument("x-dead-letter-exchange", DEAD_LETTER_EXCHANGE)
            .withArgument("x-dead-letter-routing-key", DEAD_LETTER_ROUTING_KEY)
            .build();
    }

    @Bean
    public Binding projetoCadastradoBinding(
            @Qualifier("projetoCadastradoQueue") Queue projetoCadastradoQueue,
            @Qualifier("projetosExchange") DirectExchange projetosExchange) {
        return BindingBuilder.bind(projetoCadastradoQueue)
            .to(projetosExchange)
            .with(ROUTING_KEY);
    }

    @Bean
    public DirectExchange projetosDeadLetterExchange() {
        return new DirectExchange(DEAD_LETTER_EXCHANGE, true, false);
    }

    @Bean
    public Queue projetoCadastradoDeadLetterQueue() {
        return QueueBuilder.durable(DEAD_LETTER_QUEUE).build();
    }

    @Bean
    public Binding projetoCadastradoDeadLetterBinding(
            @Qualifier("projetoCadastradoDeadLetterQueue") Queue projetoCadastradoDeadLetterQueue,
            @Qualifier("projetosDeadLetterExchange") DirectExchange projetosDeadLetterExchange) {
        return BindingBuilder.bind(projetoCadastradoDeadLetterQueue)
            .to(projetosDeadLetterExchange)
            .with(DEAD_LETTER_ROUTING_KEY);
    }

    @Bean
    public MessageConverter rabbitMessageConverter(ObjectMapper objectMapper) {
        Jackson2JsonMessageConverter converter = new Jackson2JsonMessageConverter(objectMapper);
        DefaultClassMapper classMapper = new DefaultClassMapper();
        classMapper.setIdClassMapping(Collections.singletonMap(
            ProjetoCadastradoEvento.TIPO_EVENTO, ProjetoCadastradoEvento.class));
        classMapper.afterPropertiesSet();
        converter.setClassMapper(classMapper);
        return converter;
    }

    @Bean
    public SimpleRabbitListenerContainerFactory rabbitListenerContainerFactory(
            SimpleRabbitListenerContainerFactoryConfigurer configurer,
            ConnectionFactory connectionFactory) {
        SimpleRabbitListenerContainerFactory factory = new SimpleRabbitListenerContainerFactory();
        configurer.configure(factory, connectionFactory);
        factory.setAdviceChain(RetryInterceptorBuilder.stateless()
            .maxAttempts(3)
            .backOffOptions(1000, 2.0, 10000)
            .recoverer(new RejectAndDontRequeueRecoverer())
            .build());
        return factory;
    }
}
