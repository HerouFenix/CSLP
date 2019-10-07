# Use an official Debian runtime as a parent image
FROM debian:buster

# Set the working directory to /game
WORKDIR /game

# Copy the current directory contents into the container at /game
COPY . /game

# Install pip
RUN apt-get update && apt install -y python3-pip

# Install numPy
RUN pip3 install numpy

# Install pyGame
RUN pip3 install pygame

# Run the game
CMD ["python3", "main.py"]
