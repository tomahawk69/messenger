# Simple backend for a twitter-like messenger

## Running

To run execute
_mvn clean spring-boot:run_
or
_mvn clean package_
and next
_java -jar target/backend-0.0.1-SNAPSHOT.jar_


By default runs on 8080 port. To change update application.properties:
_server.port = 8081_ 
or pass it in a command line when running jar:
_-Dserver.port = 8081_

  
## Rest API description:

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


## Note that there are:

- no "delete user" function

- user is not an entity, just String

- no unit tests, only end-to-end

- no swagger or rest docs to expose api  
