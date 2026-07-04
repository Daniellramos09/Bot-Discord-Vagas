# DiscordVagas

Bot do Discord para postagem automática de vagas de TI.

## Configuração

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

## Segurança

- O arquivo `application.properties` está no `.gitignore` e não deve ser commitado
- Use `application.properties.example` como template para configuração
- Nunca compartilhe suas credenciais reais no repositório
