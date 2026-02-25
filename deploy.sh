#!/bin/bash

# Variables
IMAGE_NAME="springboot3withfirebaseauth_back_image"
CONTAINER_NAME="springboot3withfirebaseauth_back_container"
DOCKER_COMPOSE_FILE="docker-compose.yml"
PROFILE="prod"  # Change to your desired Spring profile if needed

# Pull latest code or artifacts if required (e.g., git pull, download JAR, etc.)
echo "git pull latest code or artifacts..."
git pull origin develop

# Uncomment if you have a build step outside of Docker:
#echo "if you have a build step outside of Docker..."
#./gradlew clean build

# Step 1: Cleanup unused Docker resources(Remove All Unused Resources (Containers, Networks, Volumes, Images))
echo "Cleaning up unused Docker resources(Remove All Unused Resources (Containers, Networks, Volumes, Images)..."
docker system prune -f

#Removes all stopped containers that are not in use.
#echo "Removes all stopped containers that are not in use..."
#docker container prune -f

#Removes images that are not tagged or used by any container.
#echo "Removes images that are not tagged or used by any container..."
#docker image prune -f

#Removes unused volumes (volumes that aren't associated with any container).
#echo "Removes unused volumes (volumes that aren't associated with any container)..."
#docker volume prune -f

#Cleans up unused Docker networks.
#echo "Cleans up unused Docker networks..."
#docker network prune -f

# Step 2: Check if the container is already running
echo "Checking if container is already running..."

# Check if the container is running
if [ $(docker ps -q -f name=$CONTAINER_NAME) ]; then
    echo "Container is already running. Stopping and removing the container..."

    # Step 3: Stop and remove the running container
    docker-compose -f $DOCKER_COMPOSE_FILE down
else
    echo "Container is not running."
fi

# Step 4: Build the Docker image
echo "Building Docker image..."
docker-compose -f $DOCKER_COMPOSE_FILE build

# Step 5: Deploy the container with Docker Compose
echo "Starting application with Docker Compose..."
docker-compose -f $DOCKER_COMPOSE_FILE up -d

# Check if the container started successfully
if [ $? -eq 0 ]; then
    echo "Deployment successful. Application is running."
else
    echo "Deployment failed. Check Docker logs for more details."
    exit 1
fi

# Optional: View container logs
# docker logs -f $CONTAINER_NAME

# Optional: Add cleanup commands if needed
# docker-compose -f $DOCKER_COMPOSE_FILE down -v
