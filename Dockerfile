# Usa uma imagem oficial e enxuta do Java 17
FROM eclipse-temurin:17-jdk-alpine

# Cria uma pasta de trabalho dentro do contêiner
WORKDIR /app

# Copia o arquivo .jar compilado para dentro do contêiner
COPY target/DiscordVagas-0.0.1-SNAPSHOT.jar app.jar

# Define as variáveis de ambiente necessárias
ENV DATASOURCE_URL=
ENV DATASOURCE_USERNAME=
ENV DATASOURCE_PASSWORD=
ENV GEMINI_API_KEY=
ENV DISCORD_WEBHOOK_URL=

# Define o comando mestre que vai rodar assim que o contêiner ligar
ENTRYPOINT ["java", "-jar", "app.jar"]