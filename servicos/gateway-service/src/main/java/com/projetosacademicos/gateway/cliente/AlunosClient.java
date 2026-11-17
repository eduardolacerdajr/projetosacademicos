package com.projetosacademicos.gateway.cliente;

import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
public class AlunosClient extends RecursoClient {
    public AlunosClient(RestTemplate restTemplate, @Value("${alunos.service.url}") String baseUrl) {
        super(restTemplate, baseUrl, "/api/v1/alunos");
    }

    public List<Map<String, Object>> porMatricula(String matricula) {
        return listarPor("/matricula", matricula);
    }
}
