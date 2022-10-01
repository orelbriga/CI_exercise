FROM gradle:jdk11
RUN mkdir /hello-world-app
WORKDIR /hello-world-app
COPY build/libs/*SNAPSHOT.jar app.jar
EXPOSE 8080
CMD ["java", "-jar", "app.jar"]