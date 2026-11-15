FROM tomcat:9-jdk17-temurin-jammy AS build

WORKDIR /app
COPY src/main/java /app/src/main/java
COPY src/main/webapp /app/src/main/webapp

RUN mkdir -p /app/classes /app/staging/WEB-INF/classes \
    && find /app/src/main/java -name '*.java' -print0 \
       | xargs -0 javac --release 17 \
           -cp '/usr/local/tomcat/lib/servlet-api.jar:/app/src/main/webapp/WEB-INF/lib/*' \
           -d /app/classes \
    && cp -R /app/src/main/webapp/. /app/staging/ \
    && cp -R /app/classes/. /app/staging/WEB-INF/classes/ \
    && rm -f /app/staging/WEB-INF/lib/servlet-api.jar \
    && cd /app/staging \
    && jar cf /app/student-management.war .

FROM tomcat:9-jdk17-temurin-jammy

COPY --from=build /app/student-management.war /usr/local/tomcat/webapps/ROOT.war

ENV PORT=10000
EXPOSE 10000

CMD ["sh", "-c", "sed -i \"s/port=\\\"8080\\\"/port=\\\"${PORT}\\\"/\" /usr/local/tomcat/conf/server.xml && catalina.sh run"]
