# Simulate CPU-Load (java-docker-app)

This app shows how easy it is to run a java-app with jdk-11 in a docker-container without compiling.

With this app it is possible to simulate cpu-load in a docker-container and also check how docker-compose can limit the cpus per container.

## Build and run

### Preconditions
Only docker and optional docker-compose to use the compose.yml file.

### Build image

    docker build -t docker-cpu-load-app .

### Run container

    docker run docker-cpu-load-app

## Reduce CPU per container

Reduce container cpus with docker only

   docker run --cpus="1.0" docker-cpu-load-app

To reduce cpus with docker-compose see "cpus" in docker-compose.yml
