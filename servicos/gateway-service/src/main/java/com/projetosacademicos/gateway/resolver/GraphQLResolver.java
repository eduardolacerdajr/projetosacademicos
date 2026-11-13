package com.projetosacademicos.gateway.resolver;

import graphql.schema.idl.RuntimeWiring;

public interface GraphQLResolver {
    void registrar(RuntimeWiring.Builder wiring);
}
