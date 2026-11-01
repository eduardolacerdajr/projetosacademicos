# Projetos Acadêmicos — microsserviços

O projeto foi separado em três aplicações Spring Boot independentes. Cada aplicação tem seu próprio `pom.xml`, porta e arquivo SQLite; as referências entre agregados são armazenadas como IDs, sem chaves estrangeiras entre serviços.

| Serviço | Diretório | Porta padrão | Banco padrão | Responsabilidade |
|---|---|---:|---|---|
| alunos-service | `servicos/alunos-service` | 8083 | `alunos.db` | Cadastro, consulta e manutenção de alunos e endereços |
| professores-service | `servicos/professores-service` | 8084 | `professores.db` | Cadastro, consulta e manutenção de professores e endereços |
| projetos-service | `servicos/projetos-service` | 8082 | `projetos.db` | Projetos, vínculos de alunos/professor e páginas Thymeleaf |

## Executar

Com Java 11 ou superior, a partir da raiz, inicie cada serviço em seu próprio terminal:

```sh
sh mvnw -f servicos/alunos-service/pom.xml spring-boot:run
sh mvnw -f servicos/professores-service/pom.xml spring-boot:run
sh mvnw -f servicos/projetos-service/pom.xml spring-boot:run
```

A interface web fica em `http://localhost:8082/home`. Os endereços dos serviços de alunos e professores podem ser sobrescritos por `ALUNOS_SERVICE_URL` e `PROFESSORES_SERVICE_URL`. As variáveis `ALUNOS_DATABASE_URL`, `PROFESSORES_DATABASE_URL` e `PROJETOS_DATABASE_URL` configuram cada banco SQLite separadamente; `SERVER_PORT` altera a porta da aplicação iniciada.

Para compilar todos os módulos pelo agregador Maven da raiz: `sh mvnw verify`.

## APIs

- Alunos: `/api/v1/alunos`, `/api/v1/alunos/{id}`, `/api/v1/alunos/matricula/{matricula}`
- Professores: `/api/v1/professores`, `/api/v1/professores/{id}`, `/api/v1/professores/matricula/{matricula}`
- Projetos: `/api/v1/projetos`, `/api/v1/projetos/{id}`, `/api/v1/projetos/titulo/{titulo}`, `/api/v1/projetos/professor/{id}`, `/api/v1/projetos/aluno/{id}`

No serviço de projetos, use `professorResponsavelId` e `alunosIds` (lista de IDs) para referenciar entidades gerenciadas pelos demais serviços. O formulário do serviço de projetos consulta os serviços de alunos e professores via HTTP para montar as opções.

## Dados existentes

Os serviços usam bancos separados por padrão para manter o isolamento de persistência. O banco monolítico `projetos_academicos.db` não é importado automaticamente; faça backup e migre os dados explicitamente antes de descartar o banco antigo. O esquema inicial em `etc/` é legado e não é executado pelos novos serviços.
