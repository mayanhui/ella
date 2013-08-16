#!/usr/bin/env bash


yum install wget

JDK6_URL="http://42.96.141.99/jdk/jdk-6u45-linux-amd64.rpm"

#install JDK
wget $JDK6_URL
rpm -ivh jdk-6u45-linux-amd64.rpm

java -version

#install/start/init mysql
yum install mysql mysql-devel mysql-server

/sbin/service mysqld start

mysql -hlocalhost -uroot --default-character-set=utf8 < src/main/resources/ella-hbase.sql


#install maven2
wget http://mirrors.cnnic.cn/apache/maven/maven-2/2.2.1/binaries/apache-maven-2.2.1-bin.zip
mkdir /opt/modules
mv apache-maven-2.2.1-bin.zip /opt/modules
unzip apache-maven-2.2.1-bin.zip

echo "export JAVA_HOME=/usr/java/default
MAVEN_HOME=/opt/modules/apache-maven-2.2.1
M2_HOME=/opt/modules/apache-maven-2.2.1
export MAVEN_HOME
export M2_HOME
PATH=$JAVA_HOME/bin:${MAVEN_HOME}/bin:$PATH
export PATH" >> /etc/profile

source /etc/profile

mvn -version

