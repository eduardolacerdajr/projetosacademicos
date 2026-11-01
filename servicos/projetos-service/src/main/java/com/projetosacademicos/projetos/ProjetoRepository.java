package com.projetosacademicos.projetos;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProjetoRepository extends JpaRepository<Projeto, Long> {
    List<Projeto> findByTitulo(String titulo);
    List<Projeto> findByProfessorResponsavelId(Long professorResponsavelId);
    List<Projeto> findDistinctByAlunosIdsContaining(Long alunoId);
}
