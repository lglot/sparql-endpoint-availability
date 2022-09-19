# Sparql Enpdoint Availability
![Build Status](https://github.com/lglot/sparql-endpoint-availability/actions/workflows/build_test_deploy.yml/badge.svg)

This service checks the availability of a list of public SPARQL endpoints. 

For this purpose, using the <a href="https://jena.apache.org/">Jena library</a> this query is sent to each endpoint every hour: <br>
`ASK {?s ?p ?o}` <br>
If the SPARQL endpoint gives a response then it is active, otherwise it's not.

![Spring](https://img.shields.io/badge/Spring_Boot-F2F4F9?style=for-the-badge&logo=spring-boot)
![Selenium](https://img.shields.io/badge/Selenium-43B02A?style=for-the-badge&logo=Selenium&logoColor=white)
![Bootstrap](https://img.shields.io/badge/Bootstrap-563D7C?style=for-the-badge&logo=bootstrap&logoColor=white)
![Docker](https://img.shields.io/badge/docker-%230db7ed.svg?style=for-the-badge&logo=docker&logoColor=white)


### Requirements

- Java 8 or higher
- Maven (optional)
- FirefoxDriver (for selenium testing)
- Docker and docker-compose (for deployment)

## Installation

### Local

1. Clone the repository
2. Run `mvn clean install` to build the project
3. Run `java -jar target/sparql-endpoint-availability-0.0.1-SNAPSHOT.jar` to start the application
4. Open `http://localhost:8080/` in your browser
5. Run `mvn test` to run the selenium tests
6. Run `mvn clean install -Pprod` to build the project for production
7. Run `java -jar target/sparql-endpoint-availability-0.0.1-SNAPSHOT.jar --spring.profiles.active=prod` to start the application in production mode
8. Open `http://localhost:8080/` in your browser
9. Run `mvn clean install -Pprod -DskipTests` to build the project for production without running the tests

### Docker

1. Clone the repository
2. Run `docker-compose up` to start the application
3. Open `http://localhost:8080/` in your browser

## Instruction for running develop enviroment

- Clone repo:

```console
   git clone https://bitbucket.org/machinelearningunife/sparql-endpoint-availability
```

- Run the application:

```console
   export $(cat .env | xargs)
   ./mvnw spring-boot:run 
```

## Viewing the running application

To view the running application, visit [http://localhost:8080](http://localhost:8080) in your browser

## Accessing the H2 database

The application in development uses H2 database.
H2 has an embedded GUI console for browsing the contents of a database.  
After starting the application, we can navigate to [http://localhost:8080/h2-console](http://localhost:8080/h2-console).  
On the login page, we'll insert the same credential that we used in the `application.properties`.  
Default ones are:

- JDBC URL : `jdbc:h2:file:./data/h2_db`
- User name : `root`
- password : `develop`

# Deploy application with docker-compose (MySQL databse)

**Secret**:

1. Duplicate `.env.dist` e rename to `.env`

1. Insert a password for db in `MYSQL_ROOT_PASSWORD` field

## Build and Run

```console
    ./mvnw clean package
    mkdir -p target/dependency && (cd target/dependency; jar -xf ../*.jar)
    docker-compose -f docker-compose.yml up -d --build       
```
