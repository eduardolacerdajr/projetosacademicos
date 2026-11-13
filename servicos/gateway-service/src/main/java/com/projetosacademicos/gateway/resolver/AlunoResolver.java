package com.projetosacademicos.gateway.resolver;

import com.projetosacademicos.gateway.cliente.AlunosClient;
import com.projetosacademicos.gateway.cliente.ProjetosClient;
import graphql.schema.idl.RuntimeWiring;
import java.util.Map;
import org.springframework.stereotype.Component;

@Component
public class AlunoResolver implements GraphQLResolver {
    private final AlunosClient alunos;
    private final ProjetosClient projetos;

    public AlunoResolver(AlunosClient alunos, ProjetosClient projetos) {
        this.alunos = alunos;
        this.projetos = projetos;
    }

    @Override
    public void registrar(RuntimeWiring.Builder wiring) {
        wiring
            .type("Query", query -> query
                .dataFetcher("alunos", env -> alunos.listar())
                .dataFetcher("aluno", env -> alunos.buscarPorId(env.getArgument("id")).orElse(null))
                .dataFetcher("alunosPorMatricula", env -> alunos.porMatricula(env.getArgument("matricula"))))
            .type("Mutation", mutation -> mutation
                .dataFetcher("criarAluno", env -> alunos.criar(env.getArgument("input")))
                .dataFetcher("atualizarAluno", env -> alunos.atualizar(env.getArgument("id"), env.getArgument("input")).orElse(null))
                .dataFetcher("removerAluno", env -> alunos.remover(env.getArgument("id"))))
            .type("Aluno", aluno -> aluno
                .dataFetcher("projetos", env -> {
                    Map<String, Object> origem = env.getSource();
                    return projetos.porAluno(origem.get("id"));
                }));
    }
}
