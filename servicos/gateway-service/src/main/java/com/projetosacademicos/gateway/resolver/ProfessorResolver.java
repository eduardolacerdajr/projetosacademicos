package com.projetosacademicos.gateway.resolver;

import com.projetosacademicos.gateway.cliente.ProfessoresClient;
import com.projetosacademicos.gateway.cliente.ProjetosClient;
import graphql.schema.idl.RuntimeWiring;
import java.util.Map;
import org.springframework.stereotype.Component;

@Component
public class ProfessorResolver implements GraphQLResolver {
    private final ProfessoresClient professores;
    private final ProjetosClient projetos;

    public ProfessorResolver(ProfessoresClient professores, ProjetosClient projetos) {
        this.professores = professores;
        this.projetos = projetos;
    }

    @Override
    public void registrar(RuntimeWiring.Builder wiring) {
        wiring
            .type("Query", query -> query
                .dataFetcher("professores", env -> professores.listar())
                .dataFetcher("professor", env -> professores.buscarPorId(env.getArgument("id")).orElse(null))
                .dataFetcher("professoresPorMatricula", env -> professores.porMatricula(env.getArgument("matricula"))))
            .type("Mutation", mutation -> mutation
                .dataFetcher("criarProfessor", env -> professores.criar(env.getArgument("input")))
                .dataFetcher("atualizarProfessor", env -> professores.atualizar(env.getArgument("id"), env.getArgument("input")).orElse(null))
                .dataFetcher("removerProfessor", env -> professores.remover(env.getArgument("id"))))
            .type("Professor", professor -> professor
                .dataFetcher("projetos", env -> {
                    Map<String, Object> origem = env.getSource();
                    return projetos.porProfessor(origem.get("id"));
                }));
    }
}
