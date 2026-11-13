package com.projetosacademicos.gateway;

import graphql.ExecutionInput;
import graphql.GraphQL;
import java.util.Collections;
import java.util.Map;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class GraphQLController {
    private final GraphQL graphQL;

    public GraphQLController(GraphQL graphQL) { this.graphQL = graphQL; }

    @PostMapping(value = "/graphql", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public Map<String, Object> executar(@RequestBody GraphQLRequest request) {
        ExecutionInput input = ExecutionInput.newExecutionInput()
            .query(request.getQuery())
            .operationName(request.getOperationName())
            .variables(request.getVariables() == null ? Collections.emptyMap() : request.getVariables())
            .build();
        return graphQL.execute(input).toSpecification();
    }

    public static class GraphQLRequest {
        private String query;
        private String operationName;
        private Map<String, Object> variables;

        public String getQuery() { return query; }
        public void setQuery(String query) { this.query = query; }
        public String getOperationName() { return operationName; }
        public void setOperationName(String operationName) { this.operationName = operationName; }
        public Map<String, Object> getVariables() { return variables; }
        public void setVariables(Map<String, Object> variables) { this.variables = variables; }
    }
}
