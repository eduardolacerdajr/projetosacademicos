package com.projetosacademicos.alunos;

import java.util.List;
import java.util.Optional;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/alunos")
public class AlunosController {
    private final AlunoRepository repository;
    public AlunosController(AlunoRepository repository) { this.repository = repository; }

    @GetMapping public List<Aluno> listar() { return repository.findAll(); }
    @GetMapping("/todos") public List<Aluno> todos() { return repository.findAll(); }
    @GetMapping("/{id}") public ResponseEntity<Aluno> obter(@PathVariable Long id) {
        return repository.findById(id).map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }
    @GetMapping("/matricula/{matricula}") public List<Aluno> porMatricula(@PathVariable String matricula) {
        return repository.findByMatricula(matricula);
    }
    @PostMapping public ResponseEntity<Aluno> cadastrar(@RequestBody Aluno aluno) {
        aluno.setId(null);
        return ResponseEntity.status(HttpStatus.CREATED).body(repository.save(aluno));
    }
    @PutMapping("/{id}") public ResponseEntity<Aluno> atualizar(@PathVariable Long id, @RequestBody Aluno aluno) {
        Optional<Aluno> atual = repository.findById(id);
        if (!atual.isPresent()) return ResponseEntity.notFound().build();
        aluno.setId(id);
        return ResponseEntity.ok(repository.save(aluno));
    }
    @DeleteMapping("/{id}") public ResponseEntity<Void> remover(@PathVariable Long id) {
        if (!repository.existsById(id)) return ResponseEntity.notFound().build();
        repository.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
