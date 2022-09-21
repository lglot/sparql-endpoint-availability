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




## Running develop enviroment with Maven

Sparql endpoint availability is a Spring Boot application built using Maven.

Clone repo:

```bash
git clone https://github.com/lglot/sparql-endpoint-availability.git
cd sparql-endpoint-availability
```
- Run the application: <br>

```console
./mvnw spring-boot:run 
```


### Viewing the running application

To view the running application, visit [http://localhost:8080](http://localhost:8080) in your browser

### Accessing the application with admin privileges

The application by default has a admin user with the following credentials: <br>
- username: admin <br>
- password: admin <br>

You can change the credentials in the `src/main/resources/application.properties` file <br>

## Installation with Docker

The application image is available on [Docker Hub](https://hub.docker.com/r/lglot/sparql_endpoint_availability) with the latest updates. <br>

The application requires a running database. <br>
Actually, the application is configured to use a **PostgreSQL**, **MySQL** or H2 **databases**. <br>
The easiest way to install the stack is to use docker-compose. <br>

Copy `docker-compose.yml.example` in `docker` folder to `docker-compose.yml` <br>
The default configuration is for MySQL database. <br>
Run the following command to start the application:

```bash
docker-compose -f docker/docker-compose.yml up -d
```
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
      - SPRING_PROFILES_ACTIVE=mysql          # or postgres or h2
      - SPRING_DATASOURCE_URL=jdbc:mysql://db:3306/sparql_endpoint_availability
      - SPRING_DATASOURCE_USERNAME=sparql     # database username
      - SPRING_DATASOURCE_PASSWORD=sparql     # database password
      - APP_ADMIN_USERNAME=admin              # admin username
      - APP_ADMIN_PASSWORD=admin              # admin password
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
### Recommended Variables

| Variable                      | Default            | Value                         | Description                                                                                                                                                                                                                                                                         |                                                                                                                                                                                                                                                                      
|-------------------------------|--------------------|-------------------------------|-------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|
| `SPRING_PROFILE_ACTIVE`       | h2                 | `<"mysql","postgresql","h2">` | Set database type                                                                                                                                                                                                                                                                   |
| `SPRING_DATASOURCE_URL`       | jdbc:h2:mem:db     | `jdbc:{dbms}:{database_url}`  | Set database URL, as: <br> `jdbc:mysql://localhost:3306/db` for MySQL database with hostname `localhost` and port `3306` and database name `db` <br> `jdbc:postgresql://db:5432/sparql_db` for PostgreSQL database with hostname `db` and port `5432` and database name `sparql_db` |
 | `SPRING_DATASOURCE_USERNAME`  | root               | `<Database username>`         | Set database username                                                                                                                                                                                                                                                               |
| `SPRING_DATASOURCE_PASSWORD`  | root               | `<Database password>`         | Set database password                                                                                                                                                                                                                                                               |
| `APP_ADMIN_USERNAME`          | admin              | `<Admin username>`            | Set admin username                                                                                                                                                                                                                                                                  |
| `APP_ADMIN_PASSWORD`          | admin              | `<Admin password>`            | Set admin password, it's recommended to change the default one                                                                                                                                                                                                                      |

### Optional Variables

| Variable                      | Default            | Value            | Description                                                                    |
|-------------------------------|--------------------|------------------|--------------------------------------------------------------------------------|
| `SERVER_SERVLET_CONTEXT_PATH` | unset              | `<Context path>` | Set context path, it's required for reverse proxy                              |
| `SERVER_PORT`                 | 8080               | `<Port>`         | Set web port, change also the port in the ports section in docker-compose file |
| `JWT_TOKEN_SECRET`            | a_very_long_secret | `<Secret>`       | Set secret for JWT token, it's recommended to change the default one           |

-----

# Building and testing

The test suite includes UI tests with Selenium. <br>
Browser support is limited to **Chrome, Firefox and Edge**. <br>

Default configuration is for **Firefox** browser. <br>

  
```console
 ./mvnw clean package -Dbrowser=your_browser    
```

## CI/CD

The application is configured to use GitHub Actions for CI/CD. <br>
The workflow is triggered on every push and pull request. <br>
The workflow includes the following steps:

- Build the application
- Run the test suite
- Build the docker image
- Push the docker image to Docker Hub

