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

# Usage examples

All POST/PUT/PATCH requests to the server must be made with a
```Content-Type: application/json``` header set, otherwise an error will be
thrown.

## User data

You can explore the API from http://localhost:8080/users.

Creating an user:

    $ curl -X POST -d '{"username":"vincent","password":"foobar","email":"foo@bar.com"}' -H 'Content-Type: application/json' http://localhost:8080/user/createUser
    
Output:

    {"id":10,"username":"vincent","password":"foobar","email":"foo@bar.com"}
    
Validating an user:

    $ curl -X POST -d '{"username":"vincent","password":"foobar"}' -H 'Content-Type: application/json' http://localhost:8080/user/validateUser
    
Output if user exists: 

    {"id":10,"username":"vincent","password":"foobar","email":"foo@bar.com"}
    
Output if user does not exist:

    Error: User vincent does not exist!

Output if incorrect password received:

    Error: Password incorrect!
    
Retrieving all data about a specific user (by ID):

    $ curl -X GET http://localhost:8080/users/10
    
Output:

```
{
  "username" : "vincent",
  "password" : "foobar",
  "email" : "foo@bar.com",
  "_links" : {
    "self" : {
      "href" : "http://localhost:8080/users/10"
    }
  }
}
```

Finding user by username or by email:

    $ curl -X GET http://localhost:8080/users/search/findByUsername?username=vincent
    
    $ curl -X GET http://localhost:8080/users/search/findByEmail?username=foo@bar.com
    
Output (same for both cases above):
    
```
{
  "_embedded" : {
    "users" : [ {
      "username" : "vincent",
      "password" : "foobar",
      "email" : "foo@bar.com",
      "_links" : {
        "self" : {
          "href" : "http://localhost:8080/users/10"
        }
      }
    } ]
  }
}
```

Updating user data (example below changes user id 10's password; all other data
remains the same):

    $ curl -X PATCH -d '{"password":"hunter2"}' -H 'Content-Type:application/json' http://localhost:8080/users/10
    
Let Vincent know if you have any questions about API usage.