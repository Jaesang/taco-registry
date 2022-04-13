FROM centos:7 AS build

# Install JAVA
RUN yum install -y \
       java-1.8.0-openjdk \
       java-1.8.0-openjdk-devel
ENV JAVA_HOME /etc/alternatives/jre

RUN yum update -y

# Install Maven
RUN yum install -y git maven

# Install nodejs
RUN curl --silent --location https://rpm.nodesource.com/setup_6.x | bash -
RUN yum install -y nodejs npm

WORKDIR /app/portal

# add project
ADD ./ /app/portal/

# ui install & build
WORKDIR /app/portal/registry-ui
RUN npm install
RUN npm run build

# server mvn install
WORKDIR /app/portal
RUN mvn clean package -Dmaven.test.skip=true -U

###

FROM openjdk:8 AS app

WORKDIR /app
COPY --from=build /app/portal/registry-server/target/registry-1.0.0-RELEASE.jar .
COPY --from=build /app/portal/registry-server/src/main/resources/application.prod.yaml ./application.yaml

EXPOSE 8080

# server run
ENTRYPOINT ["java"]
CMD ["-jar", "registry-1.0.0-RELEASE.jar"]