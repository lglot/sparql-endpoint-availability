# Sparql Enpdoint Availability

### Requirements
- Docker 
- Maven and jdk 8 or later (only for development)

## Instruction for running develop enviroment
* Clone repo:
```console
   git clone https://lotitolg@bitbucket.org/machinelearningunife/sparql-endpoint-availability.git
```
* **Create database**:
```console
   docker volume create db_volume
   
   docker run --name mysql -p 3306:3306 \
        -e MYSQL_ROOT_PASSWORD=your-password \
        -e MYSQL_DATABASE=sparql_endpoint_availability \
        -v db_volume:/var/lib/mysql \
        -d mysql:latest
```
* **NOTE**: replace `your-password` with a password for your database 

* Edit `spring.datasource.password` field in `src/main/resources/application.properties` with the same password

* Run the application:
```console
./mvnw spring-boot:run 
``` 


## Deploy application with docker-compose

**Secret**:

1. Duplicate `.env.dist` e rename to `.env`

1. Insert a password for db in `MYSQL_ROOT_PASSWORD` field

```console
    ./mvnw clean package
    mkdir -p target/dependency && (cd target/dependency; jar -xf ../*.jar)
    docker-compose -f docker-compose.yml up -d --build       
```
