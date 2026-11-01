package com.projetosacademicos.notificacoes;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface NotificacaoRepository extends JpaRepository<Notificacao, Long> {
    boolean existsByEventoIdAndDestinatarioIdAndTipoDestinatario(
        String eventoId, Long destinatarioId, TipoDestinatario tipoDestinatario);
    List<Notificacao> findAllByOrderByDataCriacaoAscIdAsc();
    List<Notificacao> findByProjetoIdOrderByDataCriacaoAscIdAsc(Long projetoId);
}
