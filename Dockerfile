FROM openjdk:17-alpine
RUN addgroup -S spring && adduser -S spring -G spring
USER spring:spring
ARG DEPENDENCY=target/dependency
COPY --chown=spring:spring ${DEPENDENCY}/BOOT-INF/lib /app/lib
COPY --chown=spring:spring ${DEPENDENCY}/META-INF /app/META-INF
COPY --chown=spring:spring ${DEPENDENCY}/BOOT-INF/classes /app
USER root
RUN chown spring:spring /app \
    && mkdir /logs \
    && chown spring:spring /logs
USER spring:spring
ENTRYPOINT ["java","-cp","app:app/lib/*","it.unife.sparql_endpoint_availability.SparqlEndpointAvailabilityApplication"]
