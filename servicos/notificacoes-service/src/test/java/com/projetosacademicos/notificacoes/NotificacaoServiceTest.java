package com.projetosacademicos.notificacoes;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Arrays;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;

@DataJpaTest(properties = {
    "spring.datasource.url=jdbc:h2:mem:notificacoes-test;DB_CLOSE_DELAY=-1",
    "spring.datasource.driver-class-name=org.h2.Driver",
    "spring.jpa.database-platform=org.hibernate.dialect.H2Dialect",
    "spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.H2Dialect",
    "spring.jpa.hibernate.ddl-auto=create-drop"
})
@Import(NotificacaoService.class)
class NotificacaoServiceTest {
    @Autowired private NotificacaoService service;
    @Autowired private NotificacaoRepository repository;

    @Test
    void persisteUmaNotificacaoParaProfessorEParaCadaAluno() {
        ProjetoCadastradoEvento evento = evento("evento-1");

        service.processar(evento);

        List<Notificacao> notificacoes = repository.findByProjetoIdOrderByDataCriacaoAscIdAsc(101L);
        assertThat(notificacoes)
            .hasSize(4)
            .extracting(Notificacao::getDestinatarioId)
            .containsExactly(12L, 21L, 22L, 23L);
        assertThat(notificacoes.get(0).getTipoDestinatario()).isEqualTo(TipoDestinatario.PROFESSOR);
        assertThat(notificacoes.get(0).getMensagem()).contains("professor responsável");
        for (Notificacao notificacao : notificacoes) {
            assertThat(notificacao.getDataCriacao()).isNotNull();
            if (notificacao.getTipoDestinatario() == TipoDestinatario.ALUNO) {
                assertThat(notificacao.getMensagem()).contains("Sistema de Gestão Acadêmica");
            }
        }
    }

    @Test
    void reprocessarMesmoEventoNaoDuplicaNotificacoes() {
        ProjetoCadastradoEvento evento = evento("evento-repetido");

        service.processar(evento);
        service.processar(evento);

        assertThat(repository.findByProjetoIdOrderByDataCriacaoAscIdAsc(101L)).hasSize(4);
    }

    private ProjetoCadastradoEvento evento(String eventoId) {
        ProjetoCadastradoEvento evento = new ProjetoCadastradoEvento();
        evento.setEventoId(eventoId);
        evento.setTipoEvento("ProjetoCadastrado");
        evento.setProjetoId(101L);
        evento.setTitulo("Sistema de Gestão Acadêmica");
        evento.setProfessorId(12L);
        evento.setAlunosIds(Arrays.asList(21L, 22L, 22L, 23L));
        return evento;
    }
}
