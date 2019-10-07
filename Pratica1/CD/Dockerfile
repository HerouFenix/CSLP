FROM ubuntu

#Get python3
RUN apt-get update && apt-get install -y python3 \
                                        procps

WORKDIR /project
COPY . /project

CMD [ "./run.sh" ]