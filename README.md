# Discord Vagas

Bot Java/Spring que coleta vagas e outros conteudos e os publica por webhooks do Discord.

## Publicacao segura

Endpoints de busca, URLs de canais do Telegram e o `User-Agent` sao informacoes publicas e podem permanecer no repositorio. Nao publique chaves de API, URLs de webhook do Discord, senhas ou arquivos `.env`.

1. Copie `.env.example` para `.env` e preencha os valores apenas na sua maquina.
2. Para executar localmente com Docker, use `docker compose up --build`.
3. Para chamar os endpoints manuais, inicie explicitamente com o perfil `dev` e defina `APP_SECURITY_API_KEY`. Esses endpoints nao existem no perfil `prod`.

## Deploy no Render

O `render.yaml` cria um *Background Worker*, nao um Web Service. Assim, o bot permanece em execucao para os agendamentos, mas nao recebe trafego publico e o `TestController` fica desabilitado.

1. Envie este repositorio ao GitHub sem o arquivo `.env`.
2. No Render, crie um Blueprint a partir do repositorio. O arquivo `render.yaml` cria o worker e o PostgreSQL privado.
3. Informe, quando solicitado, `GEMINI_API_KEY`, os `DISCORD_WEBHOOK_URL_*` usados e as chaves dos buscadores. Esses valores ficam apenas no Render.
4. Confirme que o perfil ativo e `prod`, acompanhe os logs e verifique as primeiras execucoes agendadas.

O banco fica sem acesso publico (`ipAllowList: []`) e as credenciais dele sao repassadas ao worker somente pela rede interna do Render.

> O plano configurado para o worker e `0.5c-512mb`; ele e cobrado pelo Render. Ajuste `plan` em `render.yaml` se necessario antes do primeiro deploy.
