#!/bin/sh
set -e -u

apt-get -q -y install openjdk-8-jdk-headless
# Installing maven at same time as openjdk-8 brings in openjdk11 as dependency,
# so use two separate steps:
apt-get -q -y install maven

mvn spring-boot:run
