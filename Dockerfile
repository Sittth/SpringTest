FROM gradle:8.8-jdk21 AS build
WORKDIR /app

COPY . .

RUN chmod +x gradlew
RUN ./gradlew :main-service:bootJar --no-daemon --configure-on-demand
RUN --mount=type=cache,target=/root/.gradle \
    ./gradlew :second-service:bootJar --no-daemon --configure-on-demand

FROM eclipse-temurin:21-jre-jammy
WORKDIR /app

COPY --from=build /app/main-service/build/libs/*.jar app.jar

RUN addgroup --system --gid 1000 appgroup && \
    adduser --system --uid 1000 --gid 1000 appuser

USER appuser

ENTRYPOINT ["java", "-jar", "app.jar"]