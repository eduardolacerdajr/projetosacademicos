package com.projetosacademicos.projetos;

import java.util.List;
import java.util.Optional;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/projetos")
public class ProjetosController {
    private final ProjetoRepository repository;
    private final ProjetoService projetoService;
    public ProjetosController(ProjetoRepository repository, ProjetoService projetoService) {
        this.repository = repository;
        this.projetoService = projetoService;
    }

    @GetMapping public List<Projeto> listar() { return repository.findAll(); }
    @GetMapping("/{id}") public ResponseEntity<Projeto> obter(@PathVariable Long id) {
        return repository.findById(id).map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }
    @GetMapping("/titulo/{titulo}") public List<Projeto> porTitulo(@PathVariable String titulo) {
        return repository.findByTitulo(titulo);
    }
    @GetMapping("/professor/{id}") public List<Projeto> porProfessor(@PathVariable Long id) {
        return repository.findByProfessorResponsavelId(id);
    }
    @GetMapping("/aluno/{id}") public List<Projeto> porAluno(@PathVariable Long id) {
        return repository.findDistinctByAlunosIdsContaining(id);
    }
    @PostMapping public ResponseEntity<Projeto> cadastrar(@RequestBody Projeto projeto) {
        projeto.setId(null);
        return ResponseEntity.status(HttpStatus.CREATED).body(projetoService.cadastrar(projeto));
    }
    @PutMapping("/{id}") public ResponseEntity<Projeto> atualizar(@PathVariable Long id, @RequestBody Projeto projeto) {
        Optional<Projeto> atual = repository.findById(id);
        if (!atual.isPresent()) return ResponseEntity.notFound().build();
        projeto.setId(id);
        return ResponseEntity.ok(repository.save(projeto));
    }
    @DeleteMapping("/{id}") public ResponseEntity<Void> remover(@PathVariable Long id) {
        if (!repository.existsById(id)) return ResponseEntity.notFound().build();
        repository.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
