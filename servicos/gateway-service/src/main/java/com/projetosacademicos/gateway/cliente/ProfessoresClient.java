package com.projetosacademicos.gateway.cliente;

import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
public class ProfessoresClient extends RecursoClient {
    public ProfessoresClient(RestTemplate restTemplate, @Value("${professores.service.url}") String baseUrl) {
        super(restTemplate, baseUrl, "/api/v1/professores");
    }

    public List<Map<String, Object>> porMatricula(Integer matricula) {
        return listarPor("/matricula", matricula);
    }
}
