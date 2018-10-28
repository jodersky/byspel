# Note: this image can only be built after a root filesystem has
# been created with `sbt fhsDist`.

FROM debian:buster

RUN \
    export DEBIAN_FRONTEND=noninteractive \
    && apt-get --quiet update && apt-get --quiet install --yes \
       openjdk-11-jdk-headless \
       libdbd-sqlite3-perl \
       nano \
       sqitch \
       sqlite3 \
    && apt-get clean \
    && rm -rf /var/lib/apt/lists/*

COPY target/dist/ /

RUN \
    adduser \
    --system \
    --home=/var/lib/byspel \
    --group \
    byspel
RUN chown byspel:byspel /var/lib/byspel
USER byspel

EXPOSE 8555
CMD ["byspel", "/etc/byspel"]
