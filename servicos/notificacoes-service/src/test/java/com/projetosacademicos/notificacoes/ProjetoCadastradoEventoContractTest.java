package com.projetosacademicos.notificacoes;

import static org.assertj.core.api.Assertions.assertThat;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import java.nio.charset.StandardCharsets;
import org.junit.jupiter.api.Test;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.amqp.support.converter.MessageConverter;

class ProjetoCadastradoEventoContractTest {
    @Test
    void converteAliasDoEventoPublicadoParaContratoDoConsumidor() {
        String json = "{\"eventoId\":\"evento-1\","
            + "\"tipoEvento\":\"ProjetoCadastrado\","
            + "\"projetoId\":101,"
            + "\"titulo\":\"Sistema Acadêmico\","
            + "\"professorId\":12,"
            + "\"alunosIds\":[21,22,23],"
            + "\"dataEvento\":\"2026-10-28T10:00:00\"}";
        MessageProperties propriedades = new MessageProperties();
        propriedades.setContentType(MessageProperties.CONTENT_TYPE_JSON);
        propriedades.setHeader("__TypeId__", ProjetoCadastradoEvento.TIPO_EVENTO);
        Message mensagem = new Message(json.getBytes(StandardCharsets.UTF_8), propriedades);
        ObjectMapper objectMapper = new ObjectMapper().registerModule(new JavaTimeModule());
        MessageConverter converter = new RabbitTopology().rabbitMessageConverter(objectMapper);

        Object convertido = converter.fromMessage(mensagem);

        assertThat(convertido).isInstanceOf(ProjetoCadastradoEvento.class);
        ProjetoCadastradoEvento evento = (ProjetoCadastradoEvento) convertido;
        assertThat(evento.getEventoId()).isEqualTo("evento-1");
        assertThat(evento.getProjetoId()).isEqualTo(101L);
        assertThat(evento.getProfessorId()).isEqualTo(12L);
        assertThat(evento.getAlunosIds()).containsExactly(21L, 22L, 23L);
    }
}
