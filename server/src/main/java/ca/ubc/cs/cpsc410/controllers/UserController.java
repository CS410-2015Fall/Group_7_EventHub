package ca.ubc.cs.cpsc410.controllers;

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

    @RequestMapping(value = "/user/findByUsername", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE)
    public User findByUsername(@RequestBody @Valid final User user) {
        return userService.findByUsername(user);
    }

    @RequestMapping(value = "/user/findByEmail", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE)
    public User findByEmail(@RequestBody @Valid final User user) {
        return userService.findByEmail(user);
    }

    @RequestMapping(value = "/user/getAllUsers", method = RequestMethod.GET)
    public List<User> getAllUsers() {
        return userService.getAllUsers();
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.CONFLICT)
    public String handleException(Exception e) {
        return e.getMessage();
    }

}