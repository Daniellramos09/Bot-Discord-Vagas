# Code Review — DiscordVagas Bot

## 1. Violações do SRP (Single Responsibility Principle)

### `GeminiApiService` — Responsabilidades Mistas
**Localização:** `service/GeminiApiService.java:30-80, 83-130, 132-152, 155-170`

O `GeminiApiService` acumula duas responsabilidades distintas:
- Montar prompts específicos para cada entidade (Vaga, Curso, Hackathon, OpenSource)
- Executar a requisição HTTP e parsear a resposta da API Gemini

Isso viola o SRP. A classe deveria ser dividida em: um `PromptBuilder` (ou múltiplos builders por domínio) e um `GeminiClient` que apenas executa a chamada HTTP e retorna a resposta.

### `DiscordWebhookService` — 4 Métodos Quase Idênticos
**Localização:** `service/DiscordWebhookService.java:41-74, 77-113, 115-146, 149-180`

Cada método (`enviarVaga`, `enviarOpenSource`, `enviarCurso`, `enviarHackathon`) repete o mesmo ciclo:
1. Montar `HttpHeaders`
2. Formatar string com template hardcoded
3. Truncar em 2000 caracteres
4. Montar request body
5. Enviar via `RestTemplate`
6. Logar erro

Isso é uma clara violação do DRY e do SRP. O formato da mensagem e o webhook URL deveriam ser separados da lógica de envio. O padrão Strategy ou Template Method se encaixa aqui — uma interface `MessageFormatter` com implementações para cada tipo, e um único método `enviar(String webhookUrl, String username, String formattedContent)`.

### Scrapers com Acoplamento Demais (`HackathonScraper`, `CursoScraper`, `GithubOpenSourceScraper`)
**Localização:** `scraper/HackathonScraper.java:21-120`, `scraper/CursoScraper.java:21-154`, `scraper/GithubOpenSourceScraper.java:17-97`

Cada scraper injeta `Repository` + `DiscordWebhookService` + `GeminiApiService`. Eles misturam:
- Parsing de dados (Jsoup/JSON)
- Filtragem de negócios
- Chamada à IA (Gemini)
- Persistência (Repository)
- Envio para Discord

Isso cria uma teia de acoplamento onde o scraper conhece todos os downstream. O correto seria: o scraper retorna os dados brutos, uma camada de serviço/orquestração aplica filtros e chama o Gemini, e uma outra camada persiste e envia ao Discord.

---

## 2. Violações do OCP (Open/Closed Principle)

### `DiscordWebhookService` — Hardcoded para 4 Tipos
**Localização:** `service/DiscordWebhookService.java:22-26`

A classe é fechada para extensão — adicionar um novo tipo de conteúdo (ex: "Eventos") requer modificar esta classe, adicionando novo construtor parameter, novo webhook URL e novo método `enviar*`. Deveria ser aberta para extensão via um modelo genérico de configuração (ex: um mapa de `ContentType -> WebhookConfig` ou uma interface `WebhookNotifier`).

---

## 3. Code Smells Identificados

### Entidades Duplicadas (`Vaga`, `Curso`, `Hackathon`)
**Localização:** `entity/Vaga.java`, `entity/Curso.java`, `entity/Hackathon.java`

Três entidades com estrutura praticamente idêntica:
- `id`, `titulo`, `url`, `resumoAi`, `dataPublicacao`, `enviado`

A única diferença é que `Vaga` tem `descricaoBruta` e não usa Lombok. Isso viola o DRY a nível de modelo. A solução é criar uma classe base abstrata (ex: `Conteudo`) com os campos comuns, e as subclasses adicionam apenas o que é específico.

### `Vaga.java` — Estilo Inconsistente
**Localização:** `entity/Vaga.java:8-68`

Enquanto `Hackathon`, `Curso` e `OpenSource` usam Lombok (`@Data`, `@Builder`), `Vaga` usa getters/setters manuais. Isso é inconsistência de código que dificulta manutenção.

### Hard-coded Constants Espalhadas

| Local | Constante |
|---|---|
| `HackathonScraper.java:28-29` | URL do Google News RSS |
| `CursoScraper.java:31-32` | URL do Google News RSS (diferente) |
| `GithubOpenSourceScraper.java:20` | URL da API GitHub |
| `TelegramCafeinaScraper.java:51-93` | Listas de palavras-chave (30+ constants) |
| `GeminiApiService.java:21` | URL da API Gemini |
| `DiscordWebhookService.java:56,96,129,164` | Limite de 2000 chars |

Essas constantes deveriam estar em arquivos de configuração (`.properties`/`.yml`) ou em uma classe de configuração centralizada, não hardcoded no código-fonte.

### `System.err.println` em vez de `Logger`
**Localização:** `service/DiscordWebhookService.java:178`

O método `enviarHackathon` usa `System.err.println` enquanto todos os outros métodos usam `Logger`. Isso é inconsistência.

### Retorno de String de Erro em vez de Exceção
**Localização:** `service/GeminiApiService.java:74, 78, 124, 128`

Quando a API Gemini falha, o método retorna `"Erro ao gerar resumo:..."` como String. Isso é um code smell porque:
- O chamador não consegue distinguir erro de sucesso via tipo (precisa fazer `startsWith("Erro")`)
- Erros são persistidos no banco como se fossem dados válidos (ex: `resumoAi = "Erro ao gerar resumo..."`)

Deveria lançar uma exceção customizada (ex: `GeminiApiException`) que o chamador trata adequadamente.

### `Thread.sleep()` Bloqueando o Thread
**Localização:** `scraper/HackathonScraper.java:113`, `scraper/CursoScraper.java:147`

O `Thread.sleep(15000)` e `Thread.sleep(2000)` bloqueiam o thread do scheduler. Em um cenário de múltiplos scrapers rodando, isso pode consumir threads do pool do Spring. Deveria usar `@Async` ou rate limiting via Semaphore/Bucket (Bucket4j).

### `RestTemplate` sem Configuração
**Localização:** `config/RestTemplateConfig.java:11-13`

O `RestTemplate` é criado sem timeouts, retry policy, ou connection pooling. Se a API Gemini ou Discord demorar, o thread fica bloqueado indefinidamente. Deveria configurar `ConnectTimeout`, `ReadTimeout`, e um `RetryTemplate`.

---

## 4. Acoplamento entre Camadas

**O que acontece hoje:**

```
Scheduler → Scraper → Repository + GeminiApiService + DiscordWebhookService
```

O scraper conhece todas as dependências downstream. Isso cria uma teia de acoplamento onde:
- Testar o scraper exige mockar 3 dependências
- Mudar a API do Gemini afeta diretamente o scraper
- Mudar o formato do Discord afeta o scraper

**Arquitetura sugerida (sem código):**

```
Scheduler → Orchestrator → Scraper (retorna dados brutos)
                          → ContentCurator (usa Gemini)
                          → PersistenceService (usa Repository)
                          → NotificationService (usa Discord)
```

Cada camada tem uma única responsabilidade e pode ser testada isoladamente.

---

## 5. Outros Problemas

### `TestController` Expondo Métodos Internos
**Localização:** `controller/TestController.java:28-65`

O controller chama diretamente os métodos dos schedulers (`rotinaCursos()`, `rotinaHackathons()`). Isso expõe a lógica de agendamento via HTTP sem autenticação. Em produção, qualquer pessoa pode executar os scrapers manualmente. Deveria ter pelo menos um token de autenticação.

### Hardcoded `"language:java"` no GitHub Scraper
**Localização:** `scraper/GithubOpenSourceScraper.java:20`

A query do GitHub está fixa para `language:java`. Se você quiser expandir para outras linguagens, precisa modificar o código. Deveria ser configurável via property.

### Campo `enviado` com Semântica Enganosa
**Localização:** `entity/Hackathon.java:33`, `entity/Curso.java:33`

O campo `enviado = true` é setado mesmo quando o Gemini retorna `"IGNORAR"` ou `"EXPIRADO"`. Ou seja, `enviado` não significa "enviado ao Discord" — significa "processado". Isso é confusão semântica que dificulta queries futuras. Deveria se chamar `processado` ou ter dois campos: `processado` e `enviado`.

---

## Resumo dos Problemas Críticos

| Prioridade | Problema | Princípio Violado |
|---|---|---|
| **Alta** | Scrapers acoplados a 3 dependências downstream | SRP, DRY |
| **Alta** | `DiscordWebhookService` com 4 métodos duplicados | DRY, OCP |
| **Alta** | `GeminiApiService` mistura prompts + HTTP client | SRP |
| Média | Retorno de String de erro em vez de exceção | Clean Code |
| Média | Entidades duplicadas (Vaga/Curso/Hackathon) | DRY |
| Média | `RestTemplate` sem timeout/retry | Boas Práticas |
| Média | `Thread.sleep` bloqueando threads | Escalabilidade |
| Baixa | Hardcoded constants em classes | Manutenibilidade |
| Baixa | `TestController` sem autenticação | Segurança |
| Baixa | Campo `enviado` com semântica errada | Clean Code |
