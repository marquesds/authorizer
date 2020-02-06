FROM openjdk:11

RUN \
  curl -L -o sbt-1.3.7.deb http://dl.bintray.com/sbt/debian/sbt-1.3.7.deb && \
  dpkg -i sbt-1.3.7.deb && \
  rm sbt-1.3.7.deb && \
  apt-get update && \
  apt-get install sbt && \
  sbt sbtVersion

RUN mkdir /usr/authorizer
COPY . /usr/authorizer
RUN cd /usr/authorizer

WORKDIR /usr/authorizer

CMD ["sbt", "test"]
CMD ["sbt", "it:test"]