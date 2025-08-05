# LiterAlura 📖

![Badge Concluído](http://img.shields.io/static/v1?label=STATUS&message=CONCLUÍDO&color=GREEN&style=for-the-badge)

## 📄 Descrição do Projeto
O LiterAlura é um desafio do programa de formação em Java da Alura. O projeto consiste em desenvolver um catálogo de livros interativo que funciona via console. A aplicação consome a API gratuita Gutendex para buscar livros e autores, e persiste esses dados em um banco de dados PostgreSQL para futuras consultas.

## 🛠️ Tecnologias Utilizadas
- **Java 21:** Linguagem de programação principal.
- **Spring Boot:** Framework para criação de aplicações Java robustas.
- **Spring Data JPA:** Para persistência de dados e comunicação com o banco de dados.
- **PostgreSQL:** Banco de dados relacional para armazenamento dos livros e autores.
- **Maven:** Gerenciador de dependências e build do projeto.
- **API Gutendex:** Fonte externa dos dados dos livros.

## ✨ Funcionalidades
A aplicação oferece um menu interativo com as seguintes opções:
1.  **Buscar livro pelo título:** Realiza uma busca na API Gutendex e salva o livro no banco de dados, evitando duplicatas.
2.  **Listar livros registrados:** Mostra todos os livros salvos no banco de dados.
3.  **Listar autores registrados:** Mostra todos os autores salvos, com seus dados e a lista de livros de cada um.
4.  **Listar autores vivos em determinado ano:** Filtra e exibe autores que estavam vivos no ano informado.
5.  **Listar livros em um determinado idioma:** Mostra os livros registrados em um idioma específico (espanhol, inglês, francês ou português).

### Funcionalidades Extras
- **Gerar estatísticas:** Exibe um resumo com o total de livros e autores, média de downloads, e os livros mais e menos baixados.
- **Top 10 Livros:** Lista os 10 livros com o maior número de downloads.
- **Buscar autor por nome:** Permite encontrar um autor específico já registrado no banco de dados.

## 🚀 Como Executar o Projeto
Para executar este projeto localmente, siga os passos abaixo:
1.  **Clone o repositório:**
    ```bash
    git clone [https://github.com/AndreTeixeir/literalura.git](https://github.com/AndreTeixeir/literalura.git)
    ```
2.  **Configure o Banco de Dados:**
    * Tenha uma instância do PostgreSQL rodando.
    * Crie um banco de dados chamado `literalura_db`.
    * No arquivo `src/main/resources/application.properties`, altere a linha `spring.datasource.password` para a sua senha do PostgreSQL.
3.  **Execute a Aplicação:**
    * Abra o projeto em sua IDE Java (ex: IntelliJ IDEA).
    * Execute a classe `LiteraluraApplication.java`.

## 📸 Demonstração

Assista a um vídeo de demonstração da aplicação em funcionamento:

[**Vídeo de Demonstração do LiterAlura**](https://youtu.be/YsaiJOJ07uE)

## 👨‍💻 Autor
[André Teixeira](https://github.com/AndreTeixeir)