# README

## Build and Run with Docker Compose

To build and run the Docker container for the Java application using Docker Compose, run the following command:

```bash
docker compose up
```

This command will build the Docker image if it does not already exist and start the container.

## Build the Docker Image

To build the Docker image without starting the container, run the following command:

```bash
docker compose build
```

This command will build the Docker image if it does not already exist.

## Stop the Docker Container

To stop and remove the Docker container, run the following command:

```bash
docker compose down
```

This command will stop the running container and remove it along with the associated networks.

## Remove Docker Images

To stop the Docker container and remove all associated images, run the following command:

```bash
docker compose down --rmi all
```

This command will stop the running container, remove it, and remove all images created by `docker compose`.
