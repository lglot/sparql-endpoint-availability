# Maven - Java 11 image
FROM ubuntu:18.04


RUN apt update -y
RUN apt install -y \
	openjdk-11-jdk \
	vim \
	git \
	maven \
	mysql-server


WORKDIR /web-app 
