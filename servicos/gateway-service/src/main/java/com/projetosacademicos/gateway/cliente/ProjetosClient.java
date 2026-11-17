package com.projetosacademicos.gateway.cliente;

import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
public class ProjetosClient extends RecursoClient {
    public ProjetosClient(RestTemplate restTemplate, @Value("${projetos.service.url}") String baseUrl) {
        super(restTemplate, baseUrl, "/api/v1/projetos");
    }

    public List<Map<String, Object>> porTitulo(String titulo) {
        return listarPor("/titulo", titulo);
    }

    public List<Map<String, Object>> porProfessor(Object professorId) {
        return listarPor("/professor", professorId);
    }

    public List<Map<String, Object>> porAluno(Object alunoId) {
        return listarPor("/aluno", alunoId);
    }
}
