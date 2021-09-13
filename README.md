# E-Commerce migration application

This is for e-commerce company to migrate their older order details into new platform project. This system aids to achieve their goal by providing a REST API to import the csv into database and retrieve the loaded data and render it to front-end

## To build this application on terminal

    mvn clean install

## To run this application on terminal

    mvn spring-boot:run

## Creating Docker container for SpringBoot and MySQL to run it on Docker Image

### Command to create MySQL container

    docker run --name paloIt-mysql -e MYSQL_ROOT_PASSWORD=admin123 -e MYSQL_DATABASE=palo_it -e MYSQL_PASSWORD=admin123 -d mysql:5.6

### In the Spring Boot Application, use the same container name of the mysql instance in the application.properties

    spring.datasource.url= jdbc:mysql://paloIt-mysql:3306/palo_it?createDatabaseIfNotExist=true

### Command to run from the Dockerfile directory to create Docker image for spring boot application

    docker  build  . -t springboot-ecom-migration

### Command to run SpringBoot application and MySQL on the Docker container

    docker run -p 8080:8080 --name springboot-ecom-migration --link paloIt-mysql:mysql -d springboot-ecom-migration

# REST API

The REST API to the migration app is described below.


## POST the file

### Request

`POST /ecom/csv/upload`

    curl -i -X POST -F "csvFile=@filePath;type=text/csv" http://localhost:8080/ecom/csv/upload

## GET the data

### Request

`GET /ecom/csv/retrieve`

    curl -i -H 'Accept: application/json' http://localhost:8080/ecom/csv/retrieve
