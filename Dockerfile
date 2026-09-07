# ---------- Build stage ----------
FROM amazoncorretto:21 AS builder

WORKDIR /app

COPY gradlew .
COPY gradle gradle
COPY build.gradle .
COPY settings.gradle .
COPY lombok.config .

RUN chmod +x gradlew

RUN ./gradlew dependencies --no-daemon

COPY src src

RUN ./gradlew clean shadowJar --no-daemon


# ---------- Runtime stage ----------
FROM eclipse-temurin:21-jre

WORKDIR /app

COPY --from=builder /app/build/libs/*-all.jar app.jar

ENTRYPOINT ["java", "-jar", "app.jar"]