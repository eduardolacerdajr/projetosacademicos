package com.projetosacademicos.gateway.cliente;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

public abstract class RecursoClient {
    private static final ParameterizedTypeReference<Map<String, Object>> OBJETO = new ParameterizedTypeReference<Map<String, Object>>() { };
    private static final ParameterizedTypeReference<List<Map<String, Object>>> LISTA = new ParameterizedTypeReference<List<Map<String, Object>>>() { };

    private final RestTemplate restTemplate;
    private final String recursoUrl;

    protected RecursoClient(RestTemplate restTemplate, String baseUrl, String caminho) {
        this.restTemplate = restTemplate;
        this.recursoUrl = baseUrl + caminho;
    }

    public List<Map<String, Object>> listar() {
        return restTemplate.exchange(recursoUrl, HttpMethod.GET, null, LISTA).getBody();
    }

    public Optional<Map<String, Object>> buscarPorId(Object id) {
        try {
            return Optional.ofNullable(restTemplate.exchange(recursoUrl + "/{id}", HttpMethod.GET, null, OBJETO, id).getBody());
        } catch (HttpClientErrorException.NotFound naoEncontrado) {
            return Optional.empty();
        }
    }

    public Map<String, Object> criar(Map<String, Object> corpo) {
        return restTemplate.exchange(recursoUrl, HttpMethod.POST, new HttpEntity<>(corpo), OBJETO).getBody();
    }

    public Optional<Map<String, Object>> atualizar(Object id, Map<String, Object> corpo) {
        try {
            return Optional.ofNullable(restTemplate.exchange(recursoUrl + "/{id}", HttpMethod.PUT, new HttpEntity<>(corpo), OBJETO, id).getBody());
        } catch (HttpClientErrorException.NotFound naoEncontrado) {
            return Optional.empty();
        }
    }

    public boolean remover(Object id) {
        try {
            restTemplate.delete(recursoUrl + "/{id}", id);
            return true;
        } catch (HttpClientErrorException.NotFound naoEncontrado) {
            return false;
        }
    }

    protected List<Map<String, Object>> listarPor(String subcaminho, Object valor) {
        return restTemplate.exchange(recursoUrl + subcaminho + "/{valor}", HttpMethod.GET, null, LISTA, valor).getBody();
    }
}
