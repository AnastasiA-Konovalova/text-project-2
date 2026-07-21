FROM amazoncorretto:21-alpine AS builder

WORKDIR /app

COPY settings.gradle settings.gradle
COPY gradle gradle/
COPY gradlew gradlew
RUN chmod +x gradlew

COPY build.gradle build.gradle

COPY bookstore-service bookstore-service/
COPY bookstore-schemas bookstore-schemas/

RUN ./gradlew :bookstore-service:dependencies --no-daemon --info || \
    ./gradlew :bookstore-service:dependencies --no-daemon --info || \
    ./gradlew :bookstore-service:dependencies --no-daemon --info

RUN ./gradlew :bookstore-service:build -x test --no-daemon

FROM amazoncorretto:21-alpine

WORKDIR /app

COPY --from=builder /app/bookstore-service/build/libs/*.jar app.jar

RUN addgroup -g 1000 appuser && \
    adduser -D -u 1000 -G appuser appuser

USER appuser

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]