# Use an official Ubuntu runtime as a parent image
FROM pygame/manylinux1_base_i686

# Set the working directory to /game
WORKDIR /game

# Copy the current directory contents into the container at /game
COPY . /game

# Install pip
RUN sudo apt-get update && sudo apt install python3-pip

# Install numPy
RUN pip install numpy

# Run the game
CMD ["python3", "main.py"]
