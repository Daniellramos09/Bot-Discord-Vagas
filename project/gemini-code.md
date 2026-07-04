# Especificação do Projeto: Bot de Estágios TI (Discord/Telegram)

## 1. Visão Geral
Este é um projeto em Java com Spring Boot. O objetivo é criar um bot automatizado (sem interface gráfica) que roda diariamente, busca vagas de estágio na área de TI em sites específicos, resume a descrição da vaga usando a API de Inteligência Artificial do Google Gemini, verifica no banco de dados se a vaga já foi enviada e, se for inédita, envia para um canal do Discord via Webhook.

## 2. Stack Tecnológica
* **Linguagem:** Java 17 (ou superior)
* **Framework Principal:** Spring Boot 3.x
* **Web Scraping:** Jsoup
* **Integração de IA:** Google Gemini API (via chamadas HTTP REST)
* **Mensageria:** Discord Webhook (via chamadas HTTP REST)
* **Banco de Dados:** MySQL (ou H2 Database para testes iniciais em memória)
* **Persistência:** Spring Data JPA
* **Agendamento:** Spring Boot `@EnableScheduling`

## 3. Fluxo de Execução Diária
1. O `@Scheduled` dispara às 09h00 da manhã.
2. O sistema itera sobre uma lista de `VagaScraper` (Scrapers de diferentes sites).
3. Para cada vaga encontrada (extraindo Título, URL e Descrição Bruta):
   * O sistema consulta o banco de dados (pela URL) para ver se a vaga já existe.
   * Se já existir, ignora e passa para a próxima.
   * Se for nova, envia a *Descrição Bruta* para o `GeminiApiService`.
4. O `GeminiApiService` retorna um resumo formatado da vaga (Requisitos, Missão/Valores).
5. O `DiscordService` recebe a vaga processada e envia o JSON formatado para a URL do Webhook.
6. A vaga é salva no banco de dados para não ser enviada novamente nos dias seguintes.

## 4. Arquitetura e Modelagem Esperada

### 4.1. Entidade (Entity)
A IA deve gerar uma entidade JPA chamada `Vaga` com os seguintes campos:
* `Long id` (Primary Key, Auto Increment)
* `String titulo`
* `String url` (Unique - não podem existir URLs repetidas)
* `String descricaoBruta` (Tamanho longo - texto original da página)
* `String resumoAi` (Tamanho longo - texto formatado pelo Gemini)
* `LocalDateTime dataDescoberta`

### 4.2. Repository
* `VagaRepository` (extends JpaRepository).
* Deve conter o método: `boolean existsByUrl(String url);`

### 4.3. Interface de Scraping (Strategy Pattern)
Para permitir múltiplos sites sem acoplar o código, crie:
```java
public interface VagaScraper {
    List<Vaga> buscarVagas();
    String getNomeDoSite();
}