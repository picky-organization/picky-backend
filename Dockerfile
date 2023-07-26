FROM eclipse-temurin:17-jdk-alpine
COPY ./build/libs/picky-0.0.1-SNAPSHOT.jar picky.jar
ENTRYPOINT ["java","-jar","picky.jar", "--server.port=9090"]