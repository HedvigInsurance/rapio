FROM amazoncorretto:11

ADD build/libs/rapio-0.0.1-SNAPSHOT.jar /

ENTRYPOINT java -jar rapio-0.0.1-SNAPSHOT.jar