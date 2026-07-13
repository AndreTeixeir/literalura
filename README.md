# LiterAlura — Catálogo de Livros

> Catálogo de livros por console construído com **Java 21 e Spring Boot**, que consome a API pública **Gutendex** e persiste livros e autores em **PostgreSQL**.
> Desafio do programa Oracle Next Education (ONE) em parceria com a Alura.

---

## O que é

Aplicação de linha de comando que busca livros na API Gutendex (baseada no Project Gutenberg), salva os resultados em um banco PostgreSQL e oferece um menu interativo para consultar e analisar o acervo registrado — listagens, filtros por idioma, autores vivos em um ano, estatísticas e ranking de mais baixados.

## Problema que resolve

Centraliza a busca e a organização de livros de domínio público em um catálogo local persistente. Em vez de consultar a API repetidamente, o usuário constrói sua própria base pesquisável e extrai informações agregadas (totais, médias, rankings) diretamente do banco.

## Funcionalidades

O menu interativo (`Principal`) oferece as opções:

1. Buscar livro pelo título (consulta a Gutendex e salva no banco, sem duplicar)
2. Listar livros registrados
3. Listar autores registrados (com seus livros)
4. Listar autores vivos em um determinado ano
5. Listar livros por idioma (es, en, fr, pt)
6. Gerar estatísticas do banco (totais, média de downloads, livro mais e menos baixado)
7. Listar o Top 10 livros mais baixados
8. Buscar autor por nome

## Arquitetura

Aplicação Spring Boot em camadas que integra API externa e banco relacional:

```
Usuário (console)
      │
      ▼
Principal  (menu interativo)
      │
      ├──► ApiConsumer ──► Gutendex API ──► DataConverter (JSON → DTOs)
      │
      └──► BookRepository / AuthorRepository  (Spring Data JPA)
                                   │
                                   ▼
                             PostgreSQL
```

- **`Principal`** — orquestra o menu e as operações.
- **`ApiConsumer`** — cliente HTTP (`java.net.http.HttpClient`) que busca dados na Gutendex.
- **`DataConverter` / DTOs** — desserializam o JSON da API em objetos.
- **`Book` / `Author`** — entidades JPA persistidas no PostgreSQL.
- **`BookRepository` / `AuthorRepository`** — repositórios Spring Data JPA, incluindo consultas derivadas e JPQL (média de downloads, autores vivos em um ano, Top 10).

## Stack

| Camada | Tecnologias |
|---|---|
| Linguagem | Java 21 |
| Framework | Spring Boot 3.5.4 |
| Persistência | Spring Data JPA / Hibernate |
| Banco | PostgreSQL |
| Build | Maven (Maven Wrapper incluído) |
| API externa | Gutendex |

## Como rodar

Pré-requisitos: JDK 21 e uma instância do PostgreSQL em execução.

```bash
# 1. Clonar o repositório
git clone https://github.com/AndreTeixeir/literalura.git
cd literalura

# 2. Criar o banco de dados no PostgreSQL
#    CREATE DATABASE literalura_db;

# 3. Ajustar as credenciais em src/main/resources/application.properties
#    (spring.datasource.username / spring.datasource.password)

# 4. Executar a aplicação com o Maven Wrapper
./mvnw spring-boot:run
```

Por padrão, `application.properties` aponta para `jdbc:postgresql://localhost:5432/literalura_db` com usuário `postgres`. O Hibernate está com `ddl-auto=update`, criando/atualizando as tabelas automaticamente.

## Demonstração

Vídeo de demonstração da aplicação: https://youtu.be/YsaiJOJ07uE

## Licença

Distribuído sob a licença MIT. Ver arquivo [LICENSE](LICENSE).
