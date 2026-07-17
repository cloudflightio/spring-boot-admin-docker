FROM docker.io/library/eclipse-temurin:17-jdk AS builder

WORKDIR /src
COPY . /src
RUN /src/gradlew build

FROM docker.io/library/eclipse-temurin:17-jre

WORKDIR /deployments
COPY --from=builder /src/build/libs/*.jar /deployments/application.jar

CMD ["java","-jar","/deployments/application.jar"]
