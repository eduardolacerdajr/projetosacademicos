package com.projetosacademicos.projetos;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.QueueBuilder;
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
    public Binding projetoCadastradoBinding(Queue projetoCadastradoQueue,
            DirectExchange projetosExchange) {
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
            Queue projetoCadastradoDeadLetterQueue,
            DirectExchange projetosDeadLetterExchange) {
        return BindingBuilder.bind(projetoCadastradoDeadLetterQueue)
            .to(projetosDeadLetterExchange)
            .with(DEAD_LETTER_ROUTING_KEY);
    }
}
