package com.projetosacademicos.alunos;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

@DataJpaTest(properties = {
    "spring.datasource.url=jdbc:h2:mem:alunos-test;DB_CLOSE_DELAY=-1",
    "spring.datasource.driver-class-name=org.h2.Driver",
    "spring.jpa.database-platform=org.hibernate.dialect.H2Dialect",
    "spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.H2Dialect",
    "spring.jpa.hibernate.ddl-auto=create-drop"
})
class AlunoRepositoryTest {
    @Autowired private AlunoRepository repository;

    @Test
    void salvaAlunoComEndereco() {
        Endereco endereco = new Endereco();
        endereco.setRua("Rua das Flores");
        Aluno aluno = new Aluno();
        aluno.setMatricula("202401");
        aluno.setNome("Maria Souza");
        aluno.setEndereco(endereco);

        Aluno salvo = repository.save(aluno);

        assertThat(salvo.getId()).isNotNull();
        assertThat(repository.findById(salvo.getId()).get().getEndereco().getRua()).isEqualTo("Rua das Flores");
    }
}
