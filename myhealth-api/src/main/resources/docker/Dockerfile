FROM dgageot/java8
VOLUME /tmp

MAINTAINER jorge.middleton@gmail.com

EXPOSE 9080

ADD myhealth-api-0.0.3-SNAPSHOT.jar /data/myhealth-api.jar
RUN touch /data/myhealth-api.jar

CMD ["--port 9080"]
ENTRYPOINT ["java", "-Dspring.profiles.active=cloud", "-Djava.security.egd=file:/dev/./urandom","-jar","/data/myhealth-api.jar", "--cache.memcached.ip=myhealth-cache.kzjygv.cfg.apse2.cache.amazonaws.com"]

#CMD java -jar -Dspring.profiles.active=cloud myhealth-api.jar --cache.memcached.ip=myhealth-cache.kzjygv.cfg.apse2.cache.amazonaws.com