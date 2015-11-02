# Build

The server component is written in [Java](http://java.com) and uses the [Spring
Framework](https://spring.io). To build it, you must have JDK >= 1.8 and maven
\>= 3.0 installed. From the top-level "server" directory, run the following
commands:

    $ mvn clean package
    
You may also import the server component directly into your IDE of choice (all
popular Java IDEs such as Eclipse, Netbeans, and Intellij IDEA have built-in
support for maven).

# Run

    $ mvn spring-boot:run
    
You can also run it from your IDE by executing the "spring-boot:run" target.
After running the server component, it will be accessible from
http://localhost:8080. Vincent will also have a remotely accessible version
of the server running at http://vcheng.org:8080.

# Development tips

Instead of using System.out.println() to print arbitrary strings in your code,
use the built-in logging system that Spring configures by default (logback),
i.e.

```
org.slf4j.Logger logger = org.slf4j.LoggerFactory.getLogger(this.getClass());
logger.warn("this shows up in your console as a warning message");
logger.info("and this is an info message");
logger.debug("and this is a debug message");
```

To run the server in debug mode:

    $ mvn spring-boot:run -Drun.jvmArguments="-Xdebug -Xrunjdwp:transport=dt_socket,server=y,suspend=y,address=5005"
    
Alternatively, add the following snippet to the pom.xml spring-boot-maven-plugin
stanza:

```
<project>
  ...
  <build>
    ...
    <plugins>
      ...
      <plugin>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-maven-plugin</artifactId>
        <version>1.2.4.RELEASE</version>
        <configuration>
          <jvmArguments>
            -Xdebug -Xrunjdwp:transport=dt_socket,server=y,suspend=y,address=5005
          </jvmArguments>
        </configuration>
        ...
      </plugin>
      ...
    </plugins>
    ...
  </build>
  ...
</project>
```

Once your server is running in debug mode on port 5005 as above, configure your
IDE with a remote run configuration/target to connect to that port and launch
your IDE's debugger.

# Usage examples

All POST/PUT/PATCH requests to the server must be made with a
```Content-Type: application/json``` header set, otherwise an error will be
thrown.

## User data

You can explore the API from http://localhost:8080/users.

The server guarantees that:

* All users will have an unique, non-null, non-updateable, auto-generated ID (integer).
* All users will have an unique, non-null, non-updateable username (string).

The intent is that the username field can serve as the key to identify a
specific user. Spring Data JPA forces us to add an integer-type primary key,
however.

### Creating an user:

    $ curl -X POST -d '{"username":"vincent","password":"foobar","email":"foo@bar.com"}' -H 'Content-Type: application/json' http://localhost:8080/user/createUser
    
Output:

    {"id":10,"username":"vincent","password":"foobar","email":"foo@bar.com","friends":[]}
    
### Validating an user:

    $ curl -X POST -d '{"username":"vincent","password":"foobar"}' -H 'Content-Type: application/json' http://localhost:8080/user/validateUser
    
Output if user exists: 

    {"id":10,"username":"vincent","password":"foobar","email":"foo@bar.com","friends":[]}
    
Output if user does not exist:

    Error: User vincent does not exist!

Output if incorrect password received:

    Error: Password incorrect!
    
### Retrieving all data about a specific user (by ID):

    $ curl -X POST -d '{"id":10}' -H 'Content-Type: application/json' http://localhost:8080/user/findById
    
Output:

    {"id":10,"username":"vincent","password":"foobar","email":"foo@bar.com","friends":[]}

### Retrieving all data about a specific user (by username):

    $ curl -X POST -d '{"username":"vincent"}' -H 'Content-Type: application/json' http://localhost:8080/user/findByUsername
    
Output:

    {"id":10,"username":"vincent","password":"foobar","email":"foo@bar.com","friends":[]}

### Retrieving all data about a specific user (by email):

    $ curl -X POST -d '{"email":"foo@bar.com"}' -H 'Content-Type: application/json' http://localhost:8080/user/findByEmail
    
Output:

    {"id":10,"username":"vincent","password":"foobar","email":"foo@bar.com","friends":[]}
    
### Adding a friend:

This endpoint consumes a list of 2 users, where the 1st user is the user to
modify and the 2nd user is the user to add as a friend.

    $ curl -X POST -d '[{"username":"vincent2"},{"username":"vincent"}]' -H 'Content-Type: application/json' http://localhost:8080/user/addFriend
    
Output:

    {"id":11,"username":"vincent2","password":"foobar","email":"foo2@bar.com","friends":["vincent"]}
    
Output if user is already a friend:

    Error: User User{username='vincent'} is already a friend of user User{username='vincent2'}!
    
### Removing a friend:

This endpoint consumes a list of 2 users, where the 1st user is the user to
modify and the 2nd user is the user to remove as a friend.

    $ curl -X POST -d '[{"username":"vincent2"},{"username":"vincent"}]' -H 'Content-Type: application/json' http://localhost:8080/user/removeFriend
    
Output:
    
    {"id":11,"username":"vincent2","password":"foobar","email":"foo2@bar.com","friends":[]}
    
Output if user is not a friend:

    Error: User User{username='vincent'} is not a friend of user User{username='vincent2'}!
    
### Get a user's friends:

After adding users vincent and vincent3 as friends of vincent2, find
vincent2's friends:

    $ curl -X POST -d '{"username":"vincent2"}' -H 'Content-Type: application/json' http://localhost:8080/user/getAllFriends
    
Output:

    [{"id":10,"username":"vincent","password":"foobar","email":"foo@bar.com","friends":[]},{"id":12,"username":"vincent3","password":"foobar","email":"foo3@bar.com","friends":[]}]
    
### Get all users

    $ curl -X GET http://localhost:8080/user/getAllUsers
    
Output:

    [{"id":10,"username":"vincent","password":"foobar","email":"foo@bar.com","friends":[]},{"id":11,"username":"vincent2","password":"foobar","email":"foo2@bar.com","friends":["vincent","vincent3"]},{"id":12,"username":"vincent3","password":"foobar","email":"foo3@bar.com","friends":[]}]
    
### Get all (accepted) events of an user

    $ curl -X POST -d '{"username":"vincent"}' -H 'Content-Type:application/json' http://localhost:8080/user/getAllEvents
    
Output:
    [

### Updating user data

This example changes user id 10's password; all other data remains the same.
Intended for use to change fields that don't have an explicit endpoint.

    $ curl -X PATCH -d '{"password":"hunter2"}' -H 'Content-Type:application/json' http://localhost:8080/users/10

## Event data

You can explore the API from http://localhost:8080/events.

The server guarantees that:

* All events will have an unique, non-null, non-updateable, auto-generated ID (integer).
* All date fields will be represented as milliseconds since the Unix epoch.

Event "type" should be one of: "wesync", "facebook", or "google".
When creating events using this API, the event type will always be "wesync".

### Creating an event

    $ curl -X POST -d '{"name":"event1","type":"wesync","description":"this event is awesome","location":"somewhere on Earth","isFinalized":false,"startDate":"2016-07-04","endDate":"2017-07-04","host":"vincent","invitees":["vincent2","vincent3"]}' -H 'Content-Type: application/json' http://localhost:8080/event/createEvent

Output:

    {"id":21,"host":"vincent","name":"event1","description":"this event is awesome","type":"wesync","isFinalized":false,"startDate":1467590400000,"endDate":1499126400000,"location":"somewhere on Earth","invitees":["vincent2","vincent3"]}
    
### Cancelling an event

TODO

### Finalizing an event

TODO

### Getting an event

    $ 

### Updating an event

## Guest data

TODO

## Facebook/Google calendar event upload endpoints

TODO

Let Vincent know if you have any questions about API usage.
