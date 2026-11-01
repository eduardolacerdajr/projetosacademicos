package com.projetosacademicos.projetos;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Arrays;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

@DataJpaTest(properties = {
    "spring.datasource.url=jdbc:h2:mem:projetos-test;DB_CLOSE_DELAY=-1",
    "spring.datasource.driver-class-name=org.h2.Driver",
    "spring.jpa.database-platform=org.hibernate.dialect.H2Dialect",
    "spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.H2Dialect",
    "spring.jpa.hibernate.ddl-auto=create-drop"
})
class ProjetoRepositoryTest {
    @Autowired private ProjetoRepository repository;

    @Test
    void salvaProjetoComIdsDeAlunosEConsultaPorAluno() {
        Projeto projeto = new Projeto();
        projeto.setTitulo("Pesquisa acadêmica");
        projeto.setProfessorResponsavelId(10L);
        projeto.setAlunosIds(Arrays.asList(20L, 21L));
        repository.save(projeto);

        assertThat(repository.findDistinctByAlunosIdsContaining(21L))
            .extracting(Projeto::getTitulo)
            .containsExactly("Pesquisa acadêmica");
    }
}
