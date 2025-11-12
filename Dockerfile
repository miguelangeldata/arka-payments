
FROM gradle:8.5-jdk21-alpine AS build
WORKDIR /app


COPY gradlew ./
COPY gradlew.bat ./
COPY gradle ./gradle


COPY build.gradle settings.gradle ./

RUN chmod +x gradlew

RUN ./gradlew build --dry-run --no-daemon || true

COPY src ./src

RUN ./gradlew clean build -x test --no-daemon

FROM eclipse-temurin:21-jre-alpine
WORKDIR /app

RUN apk add --no-cache ca-certificates && \
    rm -rf /var/cache/apk/*

EXPOSE 8083

COPY --from=build /app/build/libs/*.jar app.jar

ENTRYPOINT ["java", "-jar", "app.jar"]