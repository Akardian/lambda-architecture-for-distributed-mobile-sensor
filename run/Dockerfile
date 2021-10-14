FROM openjdk:11-jdk

ENV DEBIAN_FRONTEND noninteractive
ENV SBT_VERSION 1.4.4

RUN \
    mkdir /working/ && \
    cd /working/ && \
    apt-get clean && \
    apt-get update && \
    curl -L -o sbt-$SBT_VERSION.deb https://scala.jfrog.io/native/debian/sbt-$SBT_VERSION.deb && \
    dpkg -i sbt-$SBT_VERSION.deb && \
    rm sbt-$SBT_VERSION.deb && \
    apt-get update && \
    apt-get install sbt && \
    cd && \
    rm -r /working/ && \
    sbt sbtVersion && \
    apt-get update && \
    apt-get install -y \
        build-essential \
        gosu

COPY entrypoint.sh /entrypoint.sh
RUN \
    mkdir -p /project \
    && chmod +x /entrypoint.sh
WORKDIR /
ENTRYPOINT [ "/entrypoint.sh" ]
CMD [ "bash" ]
