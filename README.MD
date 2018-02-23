Simple backend for a twitter-like messenger

To run execute
mvn clean spring-boot:run
or
mvn clean package
and next
java -jar target/backend-0.0.1-SNAPSHOT.jar


By default runs on 8080 port. To change update application.properties:
server.port = 8081 
or pass it in a command line:
-Dserver.port = 8081


  
Rest API description:

POST /post/{user} BODY: message 
create a post; returns a new post

GET /wall/{user}  
get messages of user in a descending chronological order 

GET /timeline/{user}  
get messages of following user of passed user in a descending chronological order 

GET /timeline/{user}  
get messages of following user of passed user in a descending chronological order 

GET /users
get list of users 

POST /follow/{follower}/{following}
Follow follower user of following user

DELETE /follow/{follower}/{following}
Un-follow follower user of following user

GET /followers/{user}
Return a set of users which follow passed user 

GET /followings/{user}
Return a set of followed users of passed user 

