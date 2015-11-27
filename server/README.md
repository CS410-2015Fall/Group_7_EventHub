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
    
Output if email is not valid as per RFC 822 (we use the [JavaMail API](http://www.oracle.com/technetwork/java/javamail/index.html) to validate it):

    Error: Email invalid!
    
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

    [{"id":20,"host":"vincent","name":"event1","description":"this event is awesome","type":"wesync","isFinalized":false,"startDate":1467590400000,"endDate":1499126400000,"location":"somewhere on Earth","invitees":["vincent2","vincent3"]}]
    
### Get all (pending, i.e. invited but not yet accepted/rejected) events of an user

    $ curl -X POST -d '{"username":"vincent2"}' -H 'Content-Type: application/json' http://localhost:8080/user/getPendingEvents
    
Output:
    
    [{"id":20,"host":"vincent","name":"event1","description":"this event is awesome","type":"wesync","isFinalized":false,"startDate":1467590400000,"endDate":1499126400000,"location":"somewhere on Earth","invitees":["vincent2","vincent3"]}]
    
### Accept pending event

    $ curl -X POST -d '{"username":"vincent3","eventId":20}' -H 'Content-Type: application/json' http://localhost:8080/user/acceptPendingEvent
    
(No output.)

### Reject pending event

    $ curl -X POST -d '{"username":"vincent2","eventId":20}' -H 'Content-Type: application/json' http://localhost:8080/user/rejectPendingEvent
    
(No output.)

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

Mandatory fields: name, startDate, duration

    $ curl -X POST -d '{"name":"event1","type":"wesync","description":"this event is awesome","location":"somewhere on Earth","isFinalized":false,"startDate":"2016-07-04","duration":60,"host":"vincent","confirmedInvitees":[],"invitees":["vincent2","vincent3"]}' -H 'Content-Type: application/json' http://localhost:8080/event/createEvent

Output:

    {"id":21,"host":"vincent","name":"event1","description":"this event is awesome","type":"wesync","isFinalized":false,"startDate":1467590400000,"endDate":1499126400000,"location":"somewhere on Earth","confirmedInvitees":[],"invitees":["vincent2","vincent3"]}
    
### Cancelling an event

    $ curl -X POST -d '{"id":20}' -H 'Content-Type: application/json' http://localhost:8080/event/cancelEvent
    
(No output.)

### Finalizing an event

    $ curl -X POST -d '{"id":21}' -H 'Content-Type: application/json' http://localhost:8080/event/finalizeEvent

Output:

    {"id":21,"host":"vincent","name":"event1","description":"this event is awesome","type":"wesync","isFinalized":true,"startDate":1467590400000,"endDate":1499126400000,"location":"somewhere on Earth","invitees":null}

### Getting an event

    $ curl -X POST -d '{"id":21}' -H 'Content-Type: application/json' http://localhost:8080/event/getEvent
    
Output:

    {"id":21,"host":"vincent","name":"event1","description":"this event is awesome","type":"wesync","isFinalized":true,"startDate":1467590400000,"endDate":1499126400000,"location":"somewhere on Earth","invitees":[]}

### Adding invitees

    $ curl -X POST -d '[{"username":"vincent","eventId":21}]' -H 'Content-Type: application/json' http://localhost:8080/event/addInvitees
    
Output:
    
    {"id":21,"host":"vincent","name":"event1","description":"this event is awesome","type":"wesync","isFinalized":true,"startDate":1467590400000,"endDate":1499126400000,"location":"somewhere on Earth","invitees":["vincent"]}
    
### Getting invitees

    $ curl -X POST -d '{"id":20}' -H 'Content-Type: application/json' http://localhost:8080/event/getInvitees
    
Output: 

    [{"id":11,"username":"vincent2","password":"foobar","email":"foo2@bar.com","friends":["vincent","vincent3"],"events":[],"pendingEvents":[]},{"id":12,"username":"vincent3","password":"foobar","email":"foo3@bar.com","friends":["vincent2"],"events":[20],"pendingEvents":[]}]
    
### Getting confirmed invitees (i.e. users who have accepted an invite to the event)

    $ curl -X POST -d '{"id":20}' -H 'Content-Type: application/json' http://localhost:8080/event/getConfirmedInvitees
    
Output:

    [{"id":12,"username":"vincent3","password":"foobar","email":"foo3@bar.com","friends":["vincent2"],"events":[20],"pendingEvents":[]}]

### Finding time

Hardcoded: Valid time interval is from 8 am to 10 pm and the offset every subsequent findTime call is 30 minutes

    $ curl -X POST -d '{"id":21}' -H 'Content-Type: application/json' http://localhost:8080/event/findTime
    
Output:
    
    {"id":21,"host":"vincent","name":"event1","description":"this event is awesome","type":"wesync","isFinalized":true,"startDate":1446746400000,"endDate":1446750000000,"location":"somewhere on Earth","invitees":["vincent"]}


## Facebook/Google calendar event upload endpoints

### Add a Facebook authentication token to an user

    $ curl -X POST -d '{"username":"vincent","facebookToken":"45asad65465as113"}' -H 'Content-Type: application/json' http://localhost:8080/user/addFacebookToken
    
Output:
    
    {"id":10,"username":"vincent","password":"foobar","email":"foo@bar.com","facebookToken":"45asad65465as113","friends":[],"events":[],"pendingEvents":[]}

### Add a Google authentication token to an user

    $ curl -X POST -d '{"username":"vincent","googleToken":"asf21afs6af5acfr"}' -H 'Content-Type: application/json' http://localhost:8080/user/addGoogleToken

Output:

    {"id":10,"username":"vincent","password":"foobar","email":"foo@bar.com","googleToken":"45asad65465as113","friends":[],"events":[],"pendingEvents":[]}

### Get Facebook Events

    $ curl -X POST -d '{"username":"vincent"}' -H 'Content-Type: application/json' http://localhost:8080/facebook/getFacebookEvents

Output:
    

    [{"id":20,"host":"vincent","name":"Wesync Test Event","description":null,"type":"facebook","isFinalized":true,"startDate":1448953200000,"endDate":1449050400000,"location":null,"confirmedInvitees":null,"invitees":null},{"id":21,"host":"vincent","name":"CPSC 410 Wesync test event 2","description":null,"type":"facebook","isFinalized":true,"startDate":1448524800000,"endDate":1448622000000,"location":null,"confirmedInvitees":null,"invitees":null}]

### Upload a Google calendar event

Events will be added to the given user's list of confirmed events, with a type
value set to "google" instead of "wesync".

Input consists of a list of Google events identical to the JSON payload expected
from Google calendar with the addition of "username", which must be an already
existing username in the repository. This username must be identical in all 
Google event objects in the input list.

    $ curl -X POST -d '[{"username":"vincent","allDay":0,"calendar_id":"9","dtend":1448567354000,"dtstart":1447567354,"eventLocation":"Vincents house","title":"Vincent is sleeping, do not bother"},{"username":"vincent","allDay":0,"calendar_id":"9","dtend":1448567354000,"dtstart":1447567354,"eventLocation":"Vincents house","title":"Vincent is eating food, do not bother"}]' -H 'Content-Type: application/json' http://localhost:8080/user/addGoogleEvents
    
Output:

    {"id":10,"username":"vincent","password":"foobar","email":"foo@bar.com","facebookToken":null,"friends":[],"events":[20,21],"pendingEvents":[]}

Let Vincent know if you have any questions about API usage.
