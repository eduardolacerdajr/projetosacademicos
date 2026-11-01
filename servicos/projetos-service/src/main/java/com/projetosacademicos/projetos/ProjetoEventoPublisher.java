package com.projetosacademicos.projetos;

import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageDeliveryMode;
import org.springframework.amqp.core.MessagePostProcessor;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

@Component
public class ProjetoEventoPublisher {
    private final RabbitTemplate rabbitTemplate;

    public ProjetoEventoPublisher(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    public void publicar(Projeto projeto) {
        ProjetoCadastradoEvento evento = ProjetoCadastradoEvento.de(projeto);
        MessagePostProcessor persistente = (Message mensagem) -> {
            mensagem.getMessageProperties().setDeliveryMode(MessageDeliveryMode.PERSISTENT);
            return mensagem;
        };
        rabbitTemplate.convertAndSend(
            RabbitTopology.EXCHANGE,
            RabbitTopology.ROUTING_KEY,
            evento,
            persistente
        );
    }
}
