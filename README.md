# 🤖 DiscordVagas: Automação e IA para Curadoria de Vagas

## 🎯 O Problema
A busca por oportunidades de estágio na área de tecnologia pode ser exaustiva. Ficar navegando de site em site, filtrando vagas manualmente e lidando com a sobrecarga de informações estava a tornar-se num processo confuso e que consumia muito tempo produtivo. Ao conversar com amigos, percebi que esta era uma dor partilhada: perdíamos mais tempo a procurar as vagas do que efetivamente a candidatar-nos a elas.

## 💡 A Solução
Para otimizar o meu tempo e o dos meus colegas, desenvolvi este bot. A aplicação atua como um curador automático de oportunidades: Todos os dias, exatamente à meia-noite, o sistema entra em ação. Ele mapeia grupos e canais do Telegram focados em tecnologia, filtra rigorosamente apenas oportunidades de Estágio em São Paulo (Capital e região) e utiliza a inteligência artificial do Google Gemini para ler, processar e extrair as informações mais cruciais de cada vaga. Por fim, o bot envia um resumo limpo e formatado diretamente para um servidor do Discord através de um Webhook.



## 🛠️ Tecnologias e Arquitetura (Por que as escolhi?)
O projeto foi construído com foco em resiliência, automação de ponta a ponta e boas práticas de engenharia de software, simulando um ambiente de produção real:

- **Java 17 & Spring Boot**: Escolhidos pela robustez no ecossistema backend e facilidade em criar rotinas agendadas (@Scheduled) de forma confiável.
- **PostgreSQL**: Banco de dados relacional utilizado para persistir o histórico de vagas encontradas e evitar notificações duplicadas.
- **Google Gemini API**: A inteligência artificial foi integrada para resolver o problema de dados não estruturados. Em vez de enviar textos longos e confusos do Telegram, a IA sumariza os requisitos, o link de candidatura e a descrição da vaga.
- **Docker & Docker Hub**: A aplicação e o banco de dados foram conteinerizados. Isso garante que o sistema funcione de maneira idêntica em qualquer ambiente, facilitando a portabilidade e o isolamento dos processos.
- **AWS (Amazon Web Services)**: O deploy em produção foi realizado numa instância EC2 (Ubuntu). A escolha de subir a infraestrutura na nuvem demonstra o ciclo completo de desenvolvimento (CI/CD prático), tirando o código do "localhost" e garantindo disponibilidade 24/7.

## 📦 Dependências Principais (Spring Ecosystem)
- **Spring Web**: Para a comunicação HTTP com APIs externas (Gemini e Discord).
- **Spring Data JPA & Hibernate**: Para mapeamento objeto-relacional (ORM) e abstração inteligente das queries no banco de dados.
- **PostgreSQL Driver**: Para a conexão nativa com o banco.
- **Spring Boot Starter JSON**: Para a serialização e desserialização dos payloads trocados com os Webhooks.

## 🚀 Impacto e Aprendizado
Desenvolver este sistema permitiu-me aplicar conceitos vitais para um estagiário ou desenvolvedor júnior em cenários do mundo real:

- **Integração de APIs de terceiros**: Consumo eficiente e tratamento de respostas da IA.
- **Infraestrutura e Cloud**: Configuração de servidores Linux, gestão de chaves SSH e deploy de contentores na AWS.
- **Segurança**: Gestão de variáveis de ambiente (.env) para proteger chaves de API, credenciais de banco de dados e URLs de Webhooks em produção.

---

## 📝 Configuração (Para Desenvolvedores)

Este projeto usa variáveis de ambiente para gerenciar credenciais sensíveis. Não suba o arquivo `application.properties` com dados reais para o GitHub.

### Variáveis de Ambiente
Crie um arquivo `src/main/resources/application.properties` baseado no `application.properties.example` e configure as seguintes variáveis:

- `DATASOURCE_URL` - URL do banco de dados PostgreSQL
- `DATASOURCE_USERNAME` - Usuário do banco de dados
- `DATASOURCE_PASSWORD` - Senha do banco de dados
- `GEMINI_API_KEY` - API Key do Google Gemini para geração de resumos
- `DISCORD_WEBHOOK_URL` - URL do webhook do Discord para postagem das vagas

### Execução com Docker
```bash
# Build
docker build -t discordvagas .

# Run com variáveis de ambiente
docker run -e DATASOURCE_URL="jdbc:postgresql://host:5432/db" \
           -e DATASOURCE_USERNAME="user" \
           -e DATASOURCE_PASSWORD="password" \
           -e GEMINI_API_KEY="your-api-key" \
           -e DISCORD_WEBHOOK_URL="your-webhook-url" \
           discordvagas
```

### Execução Local
```bash
# Exportar variáveis de ambiente
export DATASOURCE_URL="jdbc:postgresql://localhost:5432/db"
export DATASOURCE_USERNAME="user"
export DATASOURCE_PASSWORD="password"
export GEMINI_API_KEY="your-api-key"
export DISCORD_WEBHOOK_URL="your-webhook-url"

# Executar
./mvnw spring-boot:run
```

## 🔒 Segurança
- O arquivo `application.properties` está no `.gitignore` e não deve ser commitado
- Use `application.properties.example` como template para configuração
- Nunca compartilhe suas credenciais reais no repositório
