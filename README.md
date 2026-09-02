# Discord Vagas

Bot Java/Spring que coleta vagas e outros conteudos e os publica por webhooks do Discord.

## Publicacao segura

Endpoints de busca, URLs de canais do Telegram e o `User-Agent` sao informacoes publicas e podem permanecer no repositorio. Nao publique chaves de API, URLs de webhook do Discord, senhas ou arquivos `.env`.

1. Copie `.env.example` para `.env` e preencha os valores apenas na sua maquina.
2. Para executar localmente com Docker, use `docker compose up --build`.
3. Para chamar os endpoints manuais, inicie explicitamente com o perfil `dev` e defina `APP_SECURITY_API_KEY`. Esses endpoints nao existem no perfil `prod`.

## Deploy no Render

Este projeto e um Spring Boot com agendamentos (`@EnableScheduling`) e tambem expoe um endpoint de saude em `/health`. Portanto, ele pode ser executado como um Web Service no Render sem alterar a logica de negocio.

1. Envie este repositorio ao GitHub sem o arquivo `.env`.
2. No Render, crie um Blueprint a partir do repositorio. O arquivo `render.yaml` define um `web` service e um PostgreSQL privado.
3. Informe, quando solicitado, `GEMINI_API_KEY`, os `DISCORD_WEBHOOK_URL_*` usados e as chaves dos buscadores. Esses valores ficam apenas no Render.
4. Confirme que o perfil ativo e `prod`, acompanhe os logs e verifique as primeiras execucoes agendadas.
5. O Render usa a variavel `PORT` para expor a aplicacao, e o app ja faz `server.address=0.0.0.0` e `server.port=${PORT:8080}` para atender essa regra.

O banco fica sem acesso publico (`ipAllowList: []`) e as credenciais dele sao repassadas ao servico somente pela rede interna do Render.

> O plano configurado para o servico e `0.5c-512mb`; ele e cobrado pelo Render. Ajuste `plan` em `render.yaml` se necessario antes do primeiro deploy.
