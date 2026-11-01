package com.projetosacademicos.notificacoes;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
public class ProjetoCadastradoListener {
    private static final Logger LOGGER = LoggerFactory.getLogger(ProjetoCadastradoListener.class);
    private final NotificacaoService notificacaoService;

    public ProjetoCadastradoListener(NotificacaoService notificacaoService) {
        this.notificacaoService = notificacaoService;
    }

    @RabbitListener(queues = RabbitTopology.QUEUE)
    public void receber(ProjetoCadastradoEvento evento) {
        LOGGER.info("Evento ProjetoCadastrado recebido: {}", evento.getEventoId());
        notificacaoService.processar(evento);
    }
}
