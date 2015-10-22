package ca.ubc.cs.cpsc410.controllers;

import ca.ubc.cs.cpsc410.data.User;
import ca.ubc.cs.cpsc410.data.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

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

    @RequestMapping(value = "/user/createUser", method = RequestMethod.POST)
    public User createUser(@RequestBody @Valid final User user) {
        return userService.createUser(user);
    }

    @RequestMapping(value = "/user/validateUser", method = RequestMethod.POST)
    public User validateUser(@RequestBody @Valid final User user) {
        return userService.validateUser(user);
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.CONFLICT)
    public String handleException(Exception e) {
        return e.getMessage();
    }

}