FROM ubuntu:18.04

ARG google_redirect_url
ARG google_client_id
ARG google_client_secret
ARG app_cookies_domain

ENV SBT_VERSION 1.5.1
ENV SCALA_VERSION 2.12.12

ENV TZ=Europe/Warsaw

ENV GOOGLE_REDIRECT_URL=$google_redirect_url
ENV GOOGLE_CLIENT_ID=$google_client_id
ENV GOOGLE_CLIENT_SECRET=$google_client_secret
ENV APP_COOKIES_DOMAIN=$app_cookies_domain

RUN apt-get update
RUN rm /bin/sh && ln -s /bin/bash /bin/sh
RUN apt-get -qq -y install curl wget unzip zip
RUN apt-get -qq -y install openjdk-8-jdk

RUN curl -s "https://get.sdkman.io" | bash
RUN source "$HOME/.sdkman/bin/sdkman-init.sh" && sdk install sbt $SBT_VERSION
RUN source "$HOME/.sdkman/bin/sdkman-init.sh" && sdk install scala $SCALA_VERSION

RUN mkdir backend
ADD . /backend/
WORKDIR /backend/

RUN source "$HOME/.sdkman/bin/sdkman-init.sh" && sbt clean compile

EXPOSE 9000

CMD source "$HOME/.sdkman/bin/sdkman-init.sh" && sbt -Dsbt.rootdir=true run

# docker build . -t ebiznes-backend:latest --build-arg google_redirect_url=${GOOGLE_REDIRECT_URL} --build-arg google_client_id=${GOOGLE_CLIENT_ID} --build-arg google_client_secret=${GOOGLE_CLIENT_SECRET} --build-arg app_cookies_domain=${APP_COOKIES_DOMAIN}
