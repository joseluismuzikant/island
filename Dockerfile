# Derived from official mysql image (our base image)
FROM mysql
# Add a database
ENV MYSQL_DATABASE=island_reservation   MYSQL_ALLOW_EMPTY_PASSWORD=true