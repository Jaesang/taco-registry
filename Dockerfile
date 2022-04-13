FROM debian:stretch

RUN apt-get update && apt-get install -y mariadb-server=10.1.45-0+deb9u1 mariadb-client=10.1.45-0+deb9u1

ENTRYPOINT ["sh"]