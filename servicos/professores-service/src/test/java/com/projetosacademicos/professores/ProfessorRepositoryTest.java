package com.projetosacademicos.professores;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

@DataJpaTest(properties = {
    "spring.datasource.url=jdbc:h2:mem:professores-test;DB_CLOSE_DELAY=-1",
    "spring.datasource.driver-class-name=org.h2.Driver",
    "spring.jpa.database-platform=org.hibernate.dialect.H2Dialect",
    "spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.H2Dialect",
    "spring.jpa.hibernate.ddl-auto=create-drop"
})
class ProfessorRepositoryTest {
    @Autowired private ProfessorRepository repository;

    @Test
    void salvaProfessorComEndereco() {
        Endereco endereco = new Endereco();
        endereco.setRua("Avenida Central");
        Professor professor = new Professor();
        professor.setMatricula(1001);
        professor.setNome("Carlos Almeida");
        professor.setEndereco(endereco);

        Professor salvo = repository.save(professor);

        assertThat(salvo.getId()).isNotNull();
        assertThat(repository.findById(salvo.getId()).get().getEndereco().getRua()).isEqualTo("Avenida Central");
    }
}
