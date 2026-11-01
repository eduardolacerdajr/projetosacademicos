package com.projetosacademicos.projetos;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import org.junit.jupiter.api.Test;
import org.mockito.InOrder;

class ProjetoServiceTest {
    @Test
    void persisteProjetoAntesDePublicarEvento() {
        ProjetoRepository repository = org.mockito.Mockito.mock(ProjetoRepository.class);
        ProjetoEventoPublisher publisher = org.mockito.Mockito.mock(ProjetoEventoPublisher.class);
        ProjetoService service = new ProjetoService(repository, publisher);
        Projeto projeto = new Projeto();
        projeto.setTitulo("Sistema Acadêmico");
        projeto.setProfessorResponsavelId(12L);
        projeto.setAlunosIds(Arrays.asList(21L, 22L, 23L));

        Projeto salvo = new Projeto();
        salvo.setId(101L);
        salvo.setTitulo(projeto.getTitulo());
        salvo.setProfessorResponsavelId(projeto.getProfessorResponsavelId());
        salvo.setAlunosIds(projeto.getAlunosIds());
        when(repository.save(projeto)).thenReturn(salvo);

        assertThat(service.cadastrar(projeto)).isSameAs(salvo);
        InOrder ordem = inOrder(repository, publisher);
        ordem.verify(repository).save(projeto);
        ordem.verify(publisher).publicar(salvo);
    }

    @Test
    void eventoIncluiIdsDosDestinatariosNoMomentoDoCadastro() {
        Projeto projeto = new Projeto();
        projeto.setId(101L);
        projeto.setTitulo("Sistema Acadêmico");
        projeto.setProfessorResponsavelId(12L);
        projeto.setAlunosIds(Arrays.asList(21L, 22L, 23L));

        ProjetoCadastradoEvento evento = ProjetoCadastradoEvento.de(projeto);

        assertThat(evento.getEventoId()).isNotBlank();
        assertThat(evento.getTipoEvento()).isEqualTo("ProjetoCadastrado");
        assertThat(evento.getProjetoId()).isEqualTo(101L);
        assertThat(evento.getProfessorId()).isEqualTo(12L);
        assertThat(evento.getAlunosIds()).containsExactly(21L, 22L, 23L);
        assertThat(evento.getDataEvento()).isNotNull();
    }
}
