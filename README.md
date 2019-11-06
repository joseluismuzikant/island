# island
Island campsite reservation system

URLS

curl http://localhost:8080/actuator/health

curl -d '{"cancelled": false,
           "reservedFrom": "2019-12-05T00:00:00",
           "reservedTo": "2019-12-07T10:00:00",
           "userArrival": "2019-11-25T00:00:00",
           "userDeparture": "2019-12-07T12:00:00",
           "userEmail": "jose.luis@mycompany.com",
           "userName": "jose luis"
         }' -H "Content-Type: application/json" -X POST http://localhost:8080/api/v1/reservations

curl  -X GET http://localhost:8080/api/v1/reservations/1

curl -d '{"cancelled": true,
           "reservedFrom": "2019-12-05T00:00:00",
           "reservedTo": "2019-12-07T10:00:00",
           "userArrival": "2019-11-25T00:00:00",
           "userDeparture": "2019-12-07T12:00:00",
           "userEmail": "jose.luis@mycompany.com",
           "userName": "jose luis muzikant"
         }' -H "Content-Type: application/json" -X PUT http://localhost:8080/api/v1/reservations/6


documentation

http://localhost:8080/swagger-ui.html

Start the mysql database

$ docker build -t my-mysql .
$ docker run -d -p 3306:3306 --name my-mysql   my-mysql

OR
$ docker-compose up

