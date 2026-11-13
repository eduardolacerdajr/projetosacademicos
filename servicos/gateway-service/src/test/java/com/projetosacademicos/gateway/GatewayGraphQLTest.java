package com.projetosacademicos.gateway;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.projetosacademicos.gateway.cliente.AlunosClient;
import com.projetosacademicos.gateway.cliente.ProfessoresClient;
import com.projetosacademicos.gateway.cliente.ProjetosClient;
import com.projetosacademicos.gateway.resolver.AlunoResolver;
import com.projetosacademicos.gateway.resolver.ProfessorResolver;
import com.projetosacademicos.gateway.resolver.ProjetoResolver;
import graphql.ExecutionResult;
import graphql.GraphQL;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class GatewayGraphQLTest {
    private final AlunosClient alunos = mock(AlunosClient.class);
    private final ProfessoresClient professores = mock(ProfessoresClient.class);
    private final ProjetosClient projetos = mock(ProjetosClient.class);
    private GraphQL graphQL;

    @BeforeEach
    void configurar() throws Exception {
        graphQL = new GraphQLConfig().graphQL(Arrays.asList(
            new AlunoResolver(alunos, projetos),
            new ProfessorResolver(professores, projetos),
            new ProjetoResolver(projetos, alunos, professores)));
    }

    @Test
    @SuppressWarnings("unchecked")
    void projetoResolveProfessorEAlunosNosServicosCorrespondentes() {
        Map<String, Object> projeto = new HashMap<>();
        projeto.put("id", 1);
        projeto.put("titulo", "Pesquisa acadêmica");
        projeto.put("professorResponsavelId", 10);
        projeto.put("alunosIds", Arrays.asList(20, 21));
        when(projetos.buscarPorId("1")).thenReturn(Optional.of(projeto));
        when(professores.buscarPorId(10)).thenReturn(Optional.of(mapa("id", 10, "nome", "Carlos Almeida")));
        when(alunos.buscarPorId(20)).thenReturn(Optional.of(mapa("id", 20, "nome", "Maria Souza")));
        when(alunos.buscarPorId(21)).thenReturn(Optional.empty());

        ExecutionResult resultado = graphQL.execute(
            "{ projeto(id: \"1\") { titulo professorResponsavel { nome } alunos { nome } } }");

        assertThat(resultado.getErrors()).isEmpty();
        Map<String, Object> dados = resultado.getData();
        Map<String, Object> dadosProjeto = (Map<String, Object>) dados.get("projeto");
        assertThat(dadosProjeto.get("titulo")).isEqualTo("Pesquisa acadêmica");
        assertThat(((Map<String, Object>) dadosProjeto.get("professorResponsavel")).get("nome")).isEqualTo("Carlos Almeida");
        List<Map<String, Object>> alunosDoProjeto = (List<Map<String, Object>>) dadosProjeto.get("alunos");
        assertThat(alunosDoProjeto).hasSize(1);
        assertThat(alunosDoProjeto.get(0).get("nome")).isEqualTo("Maria Souza");
    }

    @Test
    @SuppressWarnings("unchecked")
    void mutationEncaminhaInputAoServicoDeAlunos() {
        when(alunos.criar(argThat(corpo -> "Maria Souza".equals(corpo.get("nome")))))
            .thenReturn(mapa("id", 1, "nome", "Maria Souza"));

        ExecutionResult resultado = graphQL.execute(
            "mutation { criarAluno(input: {matricula: \"202401\", nome: \"Maria Souza\"}) { id nome } }");

        assertThat(resultado.getErrors()).isEmpty();
        Map<String, Object> dados = resultado.getData();
        assertThat(((Map<String, Object>) dados.get("criarAluno")).get("id")).isEqualTo("1");
    }

    @Test
    @SuppressWarnings("unchecked")
    void consultaInexistenteRetornaNulo() {
        when(alunos.buscarPorId("99")).thenReturn(Optional.empty());

        ExecutionResult resultado = graphQL.execute("{ aluno(id: \"99\") { nome } }");

        assertThat(resultado.getErrors()).isEmpty();
        assertThat(((Map<String, Object>) resultado.getData()).get("aluno")).isNull();
    }

    @Test
    @SuppressWarnings("unchecked")
    void alunoListaSeusProjetos() {
        when(alunos.listar()).thenReturn(Collections.singletonList(mapa("id", 20, "nome", "Maria Souza")));
        when(projetos.porAluno(20)).thenReturn(Collections.singletonList(mapa("id", 1, "titulo", "Pesquisa acadêmica")));

        ExecutionResult resultado = graphQL.execute("{ alunos { nome projetos { titulo } } }");

        assertThat(resultado.getErrors()).isEmpty();
        Map<String, Object> dados = resultado.getData();
        List<Map<String, Object>> lista = (List<Map<String, Object>>) dados.get("alunos");
        List<Map<String, Object>> projetosDoAluno = (List<Map<String, Object>>) lista.get(0).get("projetos");
        assertThat(projetosDoAluno.get(0).get("titulo")).isEqualTo("Pesquisa acadêmica");
    }

    private static Map<String, Object> mapa(Object... paresChaveValor) {
        Map<String, Object> mapa = new HashMap<>();
        for (int i = 0; i < paresChaveValor.length; i += 2) {
            mapa.put((String) paresChaveValor[i], paresChaveValor[i + 1]);
        }
        return mapa;
    }
}
