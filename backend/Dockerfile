FROM ubuntu:18.04

ARG google_redirect_url
ARG google_client_id
ARG google_client_secret

ENV SBT_VERSION 1.5.1
ENV SCALA_VERSION 2.12.12

ENV TZ=Europe/Warsaw

ENV GOOGLE_REDIRECT_URL=$google_redirect_url
ENV GOOGLE_CLIENT_ID=$google_client_id
ENV GOOGLE_CLIENT_SECRET=$google_client_secret

ARG USER_UID="1000"
ARG USER_GID="1000"

RUN apt-get update && apt-get upgrade -y
RUN rm /bin/sh && ln -s /bin/bash /bin/sh
RUN apt-get install -y openjdk-8-jdk
RUN apt-get install -y build-essential wget curl zip unzip

RUN groupadd -g $USER_GID quary && useradd -m -g $USER_GID -u $USER_UID quary

USER quary
WORKDIR /home/quary/

RUN curl -s "https://get.sdkman.io" | bash
RUN source "$HOME/.sdkman/bin/sdkman-init.sh" && sdk install sbt $SBT_VERSION && sdk install scala $SCALA_VERSION
#RUN bash -c "source /home/quary/.sdkman/bin/sdkman-init.sh && sdk install sbt $SBT_VERSION"
#RUN bash -c "source /home/quary/.sdkman/bin/sdkman-init.sh && sdk install scala $SCALA_VERSION"

RUN mkdir backend

ADD . /home/quary/backend/
WORKDIR /home/quary/backend/

EXPOSE 9000

#CMD ["source", "/home/quary/.sdkman/bin/sdkman-init.sh", "&&", "sbt", "run"]
CMD bash -c "source $HOME/.sdkman/bin/sdkman-init.sh && sbt run"

# docker build . -t ebiznes-backend:latest --build-arg google_redirect_url=${GOOGLE_REDIRECT_URL} --build-arg google_client_id=${GOOGLE_CLIENT_ID} --build-arg google_client_secret=${GOOGLE_CLIENT_SECRET}
