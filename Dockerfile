FROM openjdk:17-alpine
RUN addgroup -S spring && adduser -S spring -G spring
USER spring:spring
ARG DEPENDENCY=target/dependency
COPY ${DEPENDENCY}/BOOT-INF/lib /app/lib
COPY ${DEPENDENCY}/META-INF /app/META-INF
COPY ${DEPENDENCY}/BOOT-INF/classes /app
RUN chown -R spring:spring /app
ENTRYPOINT ["java","-cp","app:app/lib/*","it.unife.sparql_endpoint_availability.SparqlEndpointAvailabilityApplication"]
