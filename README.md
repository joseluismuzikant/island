# Sample application modeling an island reservation system with spring boot 

## Steps to Setup

**1. Clone the application**

	git clone https://github.com/joseluismuzikant/island.git

**2. Start the docker database**
 		
 	sudo docker-compose up
 	
 	OR
 	
 	docker build -t my-mysql .
    docker run -d -p 3306:3306 --name my-mysql   my-mysql


**2.2 You can change to hsqldb comments these values in src/main/resources/application.properties **
        
        #spring.datasource.url = jdbc:mysql://	:3306/island_reservation?allowPublicKeyRetrieval=true&useSSL=false
        #spring.datasource.username=root
        #spring.datasource.password=supersecret
        #spring.jpa.properties.hibernate.dialect = org.hibernate.dialect.MySQL5InnoDBDialect

**3. Package and star the application**

	mvn package
	java -jar target/island-reservation-0.0.1-SNAPSHOT.jar


	Alternatively, you can run the app without packaging it using:

	mvn spring-boot:run

	The app will start running at <http://localhost:8080>.

**4. Explore Rest APIs**

	#Test if the application is OK:
	curl http://localhost:8080/actuator/health
	
	#Get a reservation detail
	curl  -X GET http://localhost:8080/api/v1/reservations/{id}



	#Post a new reservation
    curl -d '{"cancelled": false,
               "reservedFrom": "2019-12-05T00:00:00",
               "reservedTo": "2019-12-07T10:00:00",
               "userArrival": "2019-11-25T00:00:00",
               "userDeparture": "2019-12-07T12:00:00",
               "userEmail": "jose.luis@mycompany.com",
               "userName": "jose luis"
             }' -H "Content-Type: application/json" -X POST http://localhost:8080/api/v1/reservations

    
    #Modify a reservation
    curl -d '{"cancelled": true,
               "reservedFrom": "2019-12-05T00:00:00",
               "reservedTo": "2019-12-07T10:00:00",
               "userArrival": "2019-11-25T00:00:00",
               "userDeparture": "2019-12-07T12:00:00",
               "userEmail": "jose.luis@mycompany.com",
               "userName": "jose luis muzikant"
             }' -H "Content-Type: application/json" -X PUT http://localhost:8080/api/v1/reservations/{id}
             
**5. Documentation and Api rest**

	http://localhost:8080/v2/api-docs
	http://localhost:8080/swagger-ui.html
 	
