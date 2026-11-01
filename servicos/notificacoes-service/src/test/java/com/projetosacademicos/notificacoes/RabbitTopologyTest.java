package com.projetosacademicos.notificacoes;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;

class RabbitTopologyTest {
    private final RabbitTopology topology = new RabbitTopology();

    @Test
    void filaPrincipalDuravelEncaminhaFalhasParaFilaDlqDuravel() {
        Queue fila = topology.projetoCadastradoQueue();
        Queue filaFalhas = topology.projetoCadastradoDeadLetterQueue();

        assertThat(fila.isDurable()).isTrue();
        assertThat(fila.getName()).isEqualTo(RabbitTopology.QUEUE);
        assertThat(fila.getArguments())
            .containsEntry("x-dead-letter-exchange", RabbitTopology.DEAD_LETTER_EXCHANGE)
            .containsEntry("x-dead-letter-routing-key", RabbitTopology.DEAD_LETTER_ROUTING_KEY);
        assertThat(filaFalhas.isDurable()).isTrue();
        assertThat(filaFalhas.getName()).isEqualTo(RabbitTopology.DEAD_LETTER_QUEUE);
    }

    @Test
    void bindingsUsamExchangeEChavesDeRoteamentoDeclaradas() {
        Queue fila = topology.projetoCadastradoQueue();
        Queue filaFalhas = topology.projetoCadastradoDeadLetterQueue();
        DirectExchange exchange = topology.projetosExchange();
        DirectExchange exchangeFalhas = topology.projetosDeadLetterExchange();
        Binding binding = topology.projetoCadastradoBinding(fila, exchange);
        Binding bindingFalhas = topology.projetoCadastradoDeadLetterBinding(filaFalhas, exchangeFalhas);

        assertThat(exchange.isDurable()).isTrue();
        assertThat(binding.getDestination()).isEqualTo(RabbitTopology.QUEUE);
        assertThat(binding.getRoutingKey()).isEqualTo(RabbitTopology.ROUTING_KEY);
        assertThat(exchangeFalhas.isDurable()).isTrue();
        assertThat(bindingFalhas.getDestination()).isEqualTo(RabbitTopology.DEAD_LETTER_QUEUE);
        assertThat(bindingFalhas.getRoutingKey()).isEqualTo(RabbitTopology.DEAD_LETTER_ROUTING_KEY);
    }
}
