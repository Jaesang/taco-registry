FROM centos:7

# Install JAVA
RUN yum install -y \
       java-1.8.0-openjdk \
       java-1.8.0-openjdk-devel
ENV JAVA_HOME /etc/alternatives/jre

# Install Maven
RUN yum install -y git maven

# Install nodejs
RUN curl --silent --location https://rpm.nodesource.com/setup_6.x | bash -
RUN yum install -y nodejs npm

WORKDIR /app

# git clone project
RUN git clone https://starlkj:'dlrudwls78!'@tde.sktelecom.com/stash/scm/oreotools/taco-registry-app.git

WORKDIR taco-registry-app/registry-ui
RUN npm install
RUN npm run build

WORKDIR ../registry-server

RUN mvn -U clean install

ENTRYPOINT ["mvn"]
CMD ["spring-boot:run"]