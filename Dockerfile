FROM openjdk:11
COPY ./src/cpuload /usr/src/myapp
CMD [ "java", "/usr/src/myapp/Load.java" ]
