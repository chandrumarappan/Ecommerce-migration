FROM openjdk:8
ADD target/springboot-ecom-migration.jar springboot-ecom-migration.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "springboot-ecom-migration.jar"]