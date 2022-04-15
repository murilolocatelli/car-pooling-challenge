FROM openjdk:12.0.1-jdk

WORKDIR /

ADD target/*.jar car-pooling-challenge.jar

EXPOSE 8080

CMD java -jar car-pooling-challenge.jar