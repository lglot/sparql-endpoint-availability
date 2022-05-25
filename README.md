# Sparql Enpdoint Availability

### Requirements
- Maven and jdk 8 or later (for development)
- Docker (for deployment)

## Instruction for running develop enviroment
* Clone repo:
```console
   git clone https://bitbucket.org/machinelearningunife/sparql-endpoint-availability
```
* Run the application:
```console
mvn spring-boot:run 
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
   - password : `root`

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
