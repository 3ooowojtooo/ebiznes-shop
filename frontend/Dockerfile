FROM ubuntu:18.04

ENV TZ=Europe/Warsaw

RUN apt-get update && apt-get upgrade -y
RUN apt install npm -y

RUN useradd -ms /bin/bash quary
RUN adduser quary sudo

USER quary
WORKDIR /home/quary/

RUN mkdir frontend
WORKDIR /home/quary/frontend/

ADD . /home/quary/frontend/

RUN npm install --save
RUN npm run build

EXPOSE 3000

CMD bash -c "npm start"

# docker build . -t ebiznes-frontend:latest
