package com.projetosacademicos.notificacoes;

import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/notificacoes")
public class NotificacoesController {
    private final NotificacaoRepository repository;

    public NotificacoesController(NotificacaoRepository repository) {
        this.repository = repository;
    }

    @GetMapping
    public List<Notificacao> listar(@RequestParam(required = false) Long projetoId) {
        if (projetoId != null) {
            return repository.findByProjetoIdOrderByDataCriacaoAscIdAsc(projetoId);
        }
        return repository.findAllByOrderByDataCriacaoAscIdAsc();
    }

    @GetMapping("/projeto/{projetoId}")
    public List<Notificacao> porProjeto(@PathVariable Long projetoId) {
        return repository.findByProjetoIdOrderByDataCriacaoAscIdAsc(projetoId);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Notificacao> obter(@PathVariable Long id) {
        return repository.findById(id)
            .map(ResponseEntity::ok)
            .orElseGet(() -> ResponseEntity.notFound().build());
    }
}
