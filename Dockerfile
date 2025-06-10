# Stage 1: Build the application using Maven and JDK
FROM eclipse-temurin:21-jdk AS builder
WORKDIR /workspace/app

# Copy Maven wrapper and pom.xml first for better caching
COPY mvnw .
COPY .mvn .mvn
COPY pom.xml .

# Make Maven wrapper executable and download dependencies
RUN chmod +x ./mvnw
RUN ./mvnw dependency:go-offline -B

COPY src src
RUN ./mvnw package -DskipTests

# Extracting the JAR for layered approach
RUN mkdir -p target/dependency
RUN cd target/dependency && jar -xf ../*.jar

# Stage 2: Creating the final runtime image using JRE
FROM eclipse-temurin:21-jre AS runtime
VOLUME /tmp

ARG DEPENDENCY=/workspace/app/target/dependency
WORKDIR /app

# Copying the extracted JAR contents
COPY --from=builder ${DEPENDENCY}/BOOT-INF/lib ./lib
COPY --from=builder ${DEPENDENCY}/META-INF ./META-INF
COPY --from=builder ${DEPENDENCY}/BOOT-INF/classes .

EXPOSE 8080

ENTRYPOINT ["java", "-cp", ".:./lib/*", "com.example.bankmanagement.BankManagementApplication"]