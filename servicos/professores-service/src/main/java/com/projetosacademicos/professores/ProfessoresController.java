package com.projetosacademicos.professores;

import java.util.List;
import java.util.Optional;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/professores")
public class ProfessoresController {
    private final ProfessorRepository repository;
    public ProfessoresController(ProfessorRepository repository) { this.repository = repository; }

    @GetMapping public List<Professor> listar() { return repository.findAll(); }
    @GetMapping("/{id}") public ResponseEntity<Professor> obter(@PathVariable Long id) {
        return repository.findById(id).map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }
    @GetMapping("/matricula/{matricula}") public List<Professor> porMatricula(@PathVariable Integer matricula) {
        return repository.findByMatricula(matricula);
    }
    @PostMapping public ResponseEntity<Professor> cadastrar(@RequestBody Professor professor) {
        professor.setId(null);
        return ResponseEntity.status(HttpStatus.CREATED).body(repository.save(professor));
    }
    @PutMapping("/{id}") public ResponseEntity<Professor> atualizar(@PathVariable Long id, @RequestBody Professor professor) {
        Optional<Professor> atual = repository.findById(id);
        if (!atual.isPresent()) return ResponseEntity.notFound().build();
        professor.setId(id);
        return ResponseEntity.ok(repository.save(professor));
    }
    @DeleteMapping("/{id}") public ResponseEntity<Void> remover(@PathVariable Long id) {
        if (!repository.existsById(id)) return ResponseEntity.notFound().build();
        repository.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
