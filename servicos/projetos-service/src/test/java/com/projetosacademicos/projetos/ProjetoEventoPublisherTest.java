package com.projetosacademicos.projetos;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageDeliveryMode;
import org.springframework.amqp.core.MessagePostProcessor;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.MessageConverter;

class ProjetoEventoPublisherTest {
    @Test
    void publicaEventoJsonComTipoEstavelEMensagemPersistente() {
        RabbitTemplate rabbitTemplate = mock(RabbitTemplate.class);
        ProjetoEventoPublisher publisher = new ProjetoEventoPublisher(rabbitTemplate);
        Projeto projeto = new Projeto();
        projeto.setId(101L);
        projeto.setTitulo("Sistema Acadêmico");
        projeto.setProfessorResponsavelId(12L);
        projeto.setAlunosIds(Arrays.asList(21L, 22L, 23L));

        publisher.publicar(projeto);

        ArgumentCaptor<ProjetoCadastradoEvento> eventoCaptor =
            ArgumentCaptor.forClass(ProjetoCadastradoEvento.class);
        ArgumentCaptor<MessagePostProcessor> processorCaptor =
            ArgumentCaptor.forClass(MessagePostProcessor.class);
        verify(rabbitTemplate).convertAndSend(
            eq(RabbitTopology.EXCHANGE),
            eq(RabbitTopology.ROUTING_KEY),
            eventoCaptor.capture(),
            processorCaptor.capture()
        );

        ObjectMapper objectMapper = new ObjectMapper().registerModule(new JavaTimeModule());
        MessageConverter converter = new RabbitTopology().rabbitMessageConverter(objectMapper);
        Message mensagem = converter.toMessage(eventoCaptor.getValue(), new MessageProperties());
        assertThat(mensagem.getMessageProperties().getHeaders().get("__TypeId__"))
            .isEqualTo("ProjetoCadastrado");
        String json = new String(mensagem.getBody(), StandardCharsets.UTF_8);
        assertThat(json)
            .contains("\"tipoEvento\":\"ProjetoCadastrado\"")
            .contains("\"projetoId\":101")
            .contains("\"professorId\":12")
            .contains("\"alunosIds\":[21,22,23]");

        Message mensagemPersistente = processorCaptor.getValue().postProcessMessage(mensagem);
        assertThat(mensagemPersistente.getMessageProperties().getDeliveryMode())
            .isEqualTo(MessageDeliveryMode.PERSISTENT);
    }
}
