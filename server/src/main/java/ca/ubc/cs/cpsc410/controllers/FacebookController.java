package ca.ubc.cs.cpsc410.controllers;

import ca.ubc.cs.cpsc410.data.Event;
import ca.ubc.cs.cpsc410.data.FacebookService;
import ca.ubc.cs.cpsc410.data.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

/**
 * Created by vincent on 20/10/15.
 * <p>
 * Represents controller to handle all Facebook-related API operations
 */
@RestController
public class FacebookController {

    private final FacebookService facebookService;

    @Autowired
    public FacebookController(final FacebookService facebookService) {
        this.facebookService = facebookService;
    }

    @RequestMapping(value = "/facebook/getFacebookEvents", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE)
    public List<Event> getFacebookEvents(@RequestBody @Valid final User user) {
        return facebookService.getFacebookEvents(user);
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.CONFLICT)
    public String handleException(Exception e) {
        return e.getMessage();
    }

}
