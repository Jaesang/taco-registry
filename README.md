#Taco-Registry App Project

##1. Language Setting
### postgresql
    $ psql postgres
    postgres$ create database registry encoding 'utf-8';
    CREATE DATABASE
    postgres$ create user registry password 'registry1234$$';
    CREATE ROLE
    postgres$ grant all on database registry to registry with grant option;
    GRANT

##2. IDE Setting (IntelliJ)
    menu > File > Project Structure.. > Project SDK > 1.8 (java version) 선택
    Maven Projects > Add Maven Project > pom.xml 선택
    
##3. Run Configuration
### server
    $ mvn spring-boot:run
### ui
    $ npm start