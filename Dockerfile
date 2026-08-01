# Usa uma imagem oficial e enxuta do Java 17
FROM eclipse-temurin:17-jdk-alpine

# Cria uma pasta de trabalho dentro do contêiner
WORKDIR /app

# Copia o arquivo .jar compilado para dentro do contêiner
# Usar *.jar é melhor, pois se a versão do app mudar, ele não quebra
COPY target/*.jar app.jar

# Define o fuso horário para São Paulo (ESSENCIAL para o @Scheduled rodar na hora certa)
ENV TZ=America/Sao_Paulo

# Comando mestre que vai rodar assim que o contêiner ligar
# AQUI ativamos o modo 'prod' para esconder a classe TestController
ENTRYPOINT ["java", "-jar", "app.jar", "--spring.profiles.active=prod"]