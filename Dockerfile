# Estágio 1: Build (Maven 3.8.5 com Java 11)
FROM maven:3.8.5-openjdk-11 AS build
WORKDIR /app

# Cache de dependências
COPY pom.xml .
RUN mvn dependency:go-offline

# Compilação
COPY src ./src
RUN mvn clean package -DskipTests

# Estágio 2: Runtime (Java 11 - Eclipse Temurin)
FROM eclipse-temurin:11-jre
WORKDIR /app

# Copia o JAR gerado
COPY --from=build /app/target/*.jar app.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]