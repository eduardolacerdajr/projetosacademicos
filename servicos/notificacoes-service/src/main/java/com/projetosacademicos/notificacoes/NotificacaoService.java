package com.projetosacademicos.notificacoes;

import java.time.LocalDateTime;
import java.util.LinkedHashSet;
import java.util.Set;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class NotificacaoService {
    private final NotificacaoRepository repository;

    public NotificacaoService(NotificacaoRepository repository) {
        this.repository = repository;
    }

    @Transactional
    public void processar(ProjetoCadastradoEvento evento) {
        validar(evento);
        criarSeAusente(evento, evento.getProfessorId(), TipoDestinatario.PROFESSOR);

        Set<Long> alunosUnicos = new LinkedHashSet<>();
        if (evento.getAlunosIds() != null) {
            alunosUnicos.addAll(evento.getAlunosIds());
        }
        for (Long alunoId : alunosUnicos) {
            criarSeAusente(evento, alunoId, TipoDestinatario.ALUNO);
        }
    }

    private void validar(ProjetoCadastradoEvento evento) {
        if (evento == null
                || !ProjetoCadastradoEvento.TIPO_EVENTO.equals(evento.getTipoEvento())
                || evento.getEventoId() == null
                || evento.getProjetoId() == null
                || evento.getTitulo() == null) {
            throw new IllegalArgumentException("Evento ProjetoCadastrado inválido");
        }
    }

    private void criarSeAusente(ProjetoCadastradoEvento evento, Long destinatarioId,
            TipoDestinatario tipoDestinatario) {
        if (destinatarioId == null || repository.existsByEventoIdAndDestinatarioIdAndTipoDestinatario(
                evento.getEventoId(), destinatarioId, tipoDestinatario)) {
            return;
        }

        Notificacao notificacao = new Notificacao();
        notificacao.setEventoId(evento.getEventoId());
        notificacao.setProjetoId(evento.getProjetoId());
        notificacao.setDestinatarioId(destinatarioId);
        notificacao.setTipoDestinatario(tipoDestinatario);
        notificacao.setMensagem(mensagemPara(evento, tipoDestinatario));
        notificacao.setDataCriacao(LocalDateTime.now());
        repository.save(notificacao);
    }

    private String mensagemPara(ProjetoCadastradoEvento evento, TipoDestinatario tipoDestinatario) {
        if (tipoDestinatario == TipoDestinatario.PROFESSOR) {
            return "O projeto " + evento.getTitulo()
                + " foi cadastrado com você como professor responsável.";
        }
        return "Você foi associado ao projeto " + evento.getTitulo() + ".";
    }
}
