# ---- Build stage ----
# Maven + JDK only needed to produce the jar; not present in the final image.
FROM maven:3.9-eclipse-temurin-21 AS build

WORKDIR /build

# Copy pom first so dependency resolution is cached across builds when only
# source files change (standard Docker layer-caching pattern).
COPY pom.xml .
RUN mvn dependency:go-offline -B

COPY src ./src
RUN mvn clean package -DskipTests -B

# ---- Runtime stage ----
# JRE only (not full JDK) - smaller image, and a build toolchain has no
# business existing in a production runtime container.
FROM eclipse-temurin:21-jre-alpine

# Run as a non-root user - a real production requirement, and directly
# relevant to the Kubernetes securityContext (runAsNonRoot) we'll set in Phase 7.
RUN addgroup -S novabank && adduser -S novabank -G novabank
USER novabank

WORKDIR /app
COPY --from=build /build/target/*.jar app.jar

EXPOSE 8082

ENTRYPOINT ["java", "-jar", "app.jar"]
