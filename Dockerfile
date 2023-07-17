FROM adoptopenjdk/openjdk11:ubi
LABEL email=imbb1352@gmail.com
COPY . /app
CMD java -jar -DSpring.profile.active=prod /app/build/libs/marklog-0.0.1-SNAPSHOT.jar