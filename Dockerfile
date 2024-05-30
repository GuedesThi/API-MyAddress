# Use a imagem oficial do Maven para a fase de build
FROM maven:3.8.4-openjdk-17 AS build
WORKDIR /app

# Copie o arquivo pom.xml e as dependências do projeto
COPY pom.xml .
COPY src ./src

# Execute o build do projeto
RUN mvn clean package -DskipTests

# Use a imagem oficial do OpenJDK para a fase de runtime
FROM openjdk:17-jdk-slim
WORKDIR /app

# Copie o jar da fase de build para a fase de runtime
COPY --from=build /app/target/api-myadress-0.0.1-SNAPSHOT.jar app.jar

# Exponha a porta que a aplicação irá rodar
EXPOSE 8080

# Comando para rodar a aplicação
ENTRYPOINT ["java", "-jar", "app.jar"]
