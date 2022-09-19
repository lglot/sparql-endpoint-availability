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

## Running locally with Maven

Sparql endpoint availability is a Spring Boot application built using Maven.

Clone repo:

```bash
   git clone https://github.com/lglot/sparql-endpoint-availability.git
   cd sparql-endpoint-availability
```

- Run the application:

```console
   ./mvnw spring-boot:run 
```

### Viewing the running application

To view the running application, visit [http://localhost:8080](http://localhost:8080) in your browser

### Accessing the application with admin privileges

The application by default has a admin user with the following credentials: <br>
- username: admin <br>
- password: admin <br>

You can change the credentials in the `application.properties` file.

### Accessing the H2 database

The application by default uses H2 database (in memory database). <br>
H2 has an embedded GUI console for browsing the contents of a database.  
After starting the application, we can navigate to [http://localhost:8080/h2-console](http://localhost:8080/h2-console).  
On the login page, we'll insert the same credential that we used in the `application.properties`.  
Default ones are:

- JDBC URL : `jdbc:h2:mem:devdb`
- username : `root`
- password : `root`




## Installation with Docker

The application image is available on [Docker Hub](https://hub.docker.com/repository/docker/lglot/sparql-endpoint-availability) with the latest updates. <br>

The application requires a running database. <br>
Actually, the application is configured to use a **PostgreSQL**, **MySQL** or H2 **databases**. <br>
The easiest way to install the stack is to use docker-compose. <br>

Example of docker-compose file for MySQL database:

```yaml
version: '3.0'

services:
  app:
    image: lglot/sparql_endpoint_availability:v0.1
    container_name: app
    ports:
      - "8080:8080"
    depends_on:
      - db
    environment:
      - SPRING_PROFILES_ACTIVE=mysql
      - SPRING_DATASOURCE_URL=jdbc:mysql://db:3306/sparql_endpoint_availability
      - SPRING_DATASOURCE_USERNAME=sparql
      - SPRING_DATASOURCE_PASSWORD=sparql
      - APP_ADMIN_PASSWORD=admin              # if you want to change the default admin password
      - SERVER_SERVLET_CONTEXT_PATH=/sparql   # required for reverse proxy
      
      - SERVER_PORT=8080                      # optional, if you want to change the default port
                                              # but remember to change the port in the ports section
      - JWT_TOKEN_SECRET=a_very_long_secret   # secret for JWT token 

  db:
    image: mysql:latest
    container_name: db
    environment:
      MYSQL_ROOT_PASSWORD: root
      MYSQL_USER: sparql
      MYSQL_PASSWORD: sparql
      MYSQL_DATABASE: sparql_endpoint_availability   #is the same as SPRING_DATASOURCE_URL
    volumes:
      - db_volume:/var/lib/mysql
    restart: unless-stopped

volumes:
  db_volume:
    external: false

```










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
