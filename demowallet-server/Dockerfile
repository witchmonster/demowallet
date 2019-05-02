FROM openjdk:12
VOLUME /tmp
ADD build/libs/demowallet-server-0.0.1.jar demowallet-server.jar
EXPOSE 8081
ENTRYPOINT ["java","-jar","demowallet-server.jar"]