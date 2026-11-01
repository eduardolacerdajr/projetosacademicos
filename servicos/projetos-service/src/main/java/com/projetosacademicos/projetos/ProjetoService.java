package com.projetosacademicos.projetos;

import org.springframework.stereotype.Service;

@Service
public class ProjetoService {
    private final ProjetoRepository repository;
    private final ProjetoEventoPublisher publisher;

    public ProjetoService(ProjetoRepository repository, ProjetoEventoPublisher publisher) {
        this.repository = repository;
        this.publisher = publisher;
    }

    public Projeto cadastrar(Projeto projeto) {
        Projeto salvo = repository.save(projeto);
        publisher.publicar(salvo);
        return salvo;
    }
}
