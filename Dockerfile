FROM docker.io/library/eclipse-temurin:17-jdk AS builder

ARG SPRING_BOOT_ADMIN_VERSION=2.7.3

WORKDIR /src
COPY . /src
RUN /src/gradlew build -PspringBootAdminVersion=$SPRING_BOOT_ADMIN_VERSION

FROM docker.io/library/eclipse-temurin:17-jre

WORKDIR /deployments
COPY --from=builder /src/build/libs/*.jar /deployments/application.jar

CMD ["java","-jar","/deployments/application.jar"]
