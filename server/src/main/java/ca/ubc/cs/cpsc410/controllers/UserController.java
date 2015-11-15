package ca.ubc.cs.cpsc410.controllers;

import ca.ubc.cs.cpsc410.data.Event;
import ca.ubc.cs.cpsc410.data.Guest;
import ca.ubc.cs.cpsc410.data.User;
import ca.ubc.cs.cpsc410.data.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

/**
 * Created by vincent on 20/10/15.
 * <p>
 * Represents controller that performs user operations, e.g. create, remove, clear
 */
@RestController
public class UserController {

    private final UserService userService;

    @Autowired
    public UserController(final UserService userService) {
        this.userService = userService;
    }

    @RequestMapping(value = "/user/createUser", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE)
    public User createUser(@RequestBody @Valid final User user) {
        return userService.createUser(user);
    }

    @RequestMapping(value = "/user/validateUser", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE)
    public User validateUser(@RequestBody @Valid final User user) {
        return userService.validateUser(user);
    }

    @RequestMapping(value = "/user/findById", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE)
    public User findById(@RequestBody @Valid final User user) {
        return userService.findById(user);
    }

    @RequestMapping(value = "/user/findByUsername", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE)
    public User findByUsername(@RequestBody @Valid final User user) {
        return userService.findByUsername(user);
    }

    @RequestMapping(value = "/user/findByEmail", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE)
    public User findByEmail(@RequestBody @Valid final User user) {
        return userService.findByEmail(user);
    }

    @RequestMapping(value = "/user/addFriend", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE)
    public User addFriend(@RequestBody @Valid final List<User> users) {
        return userService.addFriend(users);
    }

    @RequestMapping(value = "/user/removeFriend", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE)
    public User removeFriend(@RequestBody @Valid final List<User> users) {
        return userService.removeFriend(users);
    }

    @RequestMapping(value = "/user/getAllFriends", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE)
    public List<User> getAllFriends(@RequestBody @Valid final User user) {
        return userService.getAllFriends(user);
    }

    @RequestMapping(value = "/user/getAllUsers", method = RequestMethod.GET)
    public List<User> getAllUsers() {
        return userService.getAllUsers();
    }

    @RequestMapping(value = "/user/getAllEvents", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE)
    public List<Event> getAllEvents(@RequestBody @Valid final User user) {
        return userService.getAllEvents(user);
    }

    @RequestMapping(value = "/user/getPendingEvents", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE)
    public List<Event> getPendingEvents(@RequestBody @Valid final User user) {
        return userService.getPendingEvents(user);
    }

    @RequestMapping(value = "/user/acceptPendingEvent", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE)
    public void acceptPendingEvent(@RequestBody @Valid final Guest guest) {
        userService.acceptPendingEvent(guest);
    }

    @RequestMapping(value = "/user/rejectPendingEvent", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE)
    public void rejectPendingEvent(@RequestBody @Valid final Guest guest) {
        userService.rejectPendingEvent(guest);
    }

    @RequestMapping(value = "/user/addFacebookToken", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE)
    public User addFacebookToken(@RequestBody @Valid final User user) {
        return userService.addFacebookToken(user);
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.CONFLICT)
    public String handleException(Exception e) {
        return e.getMessage();
    }

}