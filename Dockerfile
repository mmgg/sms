FROM eclipse-temurin:11-jre
LABEL authors="wuchunlong"


WORKDIR /app

COPY target/sms-0.0.1-SNAPSHOT.jar sms.jar

EXPOSE 8082

ENTRYPOINT ["java", "-jar", "sms.jar"]
