package com.projetosacademicos.gateway;

import com.projetosacademicos.gateway.resolver.GraphQLResolver;
import graphql.GraphQL;
import graphql.schema.idl.RuntimeWiring;
import graphql.schema.idl.SchemaGenerator;
import graphql.schema.idl.SchemaParser;
import graphql.schema.idl.TypeDefinitionRegistry;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.util.List;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;

@Configuration
public class GraphQLConfig {
    private static final String SCHEMA = "graphql/schema.graphqls";

    @Bean
    public GraphQL graphQL(List<GraphQLResolver> resolvers) throws IOException {
        TypeDefinitionRegistry registry;
        try (Reader schema = new InputStreamReader(new ClassPathResource(SCHEMA).getInputStream(), StandardCharsets.UTF_8)) {
            registry = new SchemaParser().parse(schema);
        }
        RuntimeWiring.Builder wiring = RuntimeWiring.newRuntimeWiring();
        resolvers.forEach(resolver -> resolver.registrar(wiring));
        return GraphQL.newGraphQL(new SchemaGenerator().makeExecutableSchema(registry, wiring.build())).build();
    }
}
