package com.projetosacademicos.gateway.resolver;

import com.projetosacademicos.gateway.cliente.AlunosClient;
import com.projetosacademicos.gateway.cliente.ProfessoresClient;
import com.projetosacademicos.gateway.cliente.ProjetosClient;
import graphql.schema.idl.RuntimeWiring;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;
import org.springframework.stereotype.Component;

@Component
public class ProjetoResolver implements GraphQLResolver {
    private final ProjetosClient projetos;
    private final AlunosClient alunos;
    private final ProfessoresClient professores;

    public ProjetoResolver(ProjetosClient projetos, AlunosClient alunos, ProfessoresClient professores) {
        this.projetos = projetos;
        this.alunos = alunos;
        this.professores = professores;
    }

    @Override
    public void registrar(RuntimeWiring.Builder wiring) {
        wiring
            .type("Query", query -> query
                .dataFetcher("projetos", env -> projetos.listar())
                .dataFetcher("projeto", env -> projetos.buscarPorId(env.getArgument("id")).orElse(null))
                .dataFetcher("projetosPorTitulo", env -> projetos.porTitulo(env.getArgument("titulo")))
                .dataFetcher("projetosPorProfessor", env -> projetos.porProfessor(env.getArgument("professorId")))
                .dataFetcher("projetosPorAluno", env -> projetos.porAluno(env.getArgument("alunoId"))))
            .type("Mutation", mutation -> mutation
                .dataFetcher("criarProjeto", env -> projetos.criar(env.getArgument("input")))
                .dataFetcher("atualizarProjeto", env -> projetos.atualizar(env.getArgument("id"), env.getArgument("input")).orElse(null))
                .dataFetcher("removerProjeto", env -> projetos.remover(env.getArgument("id"))))
            .type("Projeto", projeto -> projeto
                .dataFetcher("professorResponsavel", env -> {
                    Map<String, Object> origem = env.getSource();
                    Object professorId = origem.get("professorResponsavelId");
                    return professorId == null ? null : professores.buscarPorId(professorId).orElse(null);
                })
                .dataFetcher("alunos", env -> {
                    Map<String, Object> origem = env.getSource();
                    return idsDeAlunos(origem).stream()
                        .map(id -> alunos.buscarPorId(id).orElse(null))
                        .filter(Objects::nonNull)
                        .collect(Collectors.toList());
                }));
    }

    @SuppressWarnings("unchecked")
    private List<Object> idsDeAlunos(Map<String, Object> projeto) {
        Object ids = projeto.get("alunosIds");
        return ids == null ? Collections.emptyList() : (List<Object>) ids;
    }
}
