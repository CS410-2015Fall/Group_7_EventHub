package ca.ubc.cs.cpsc410.controllers;


import java.util.List;

import ca.ubc.cs.cpsc410.data.Event;
import ca.ubc.cs.cpsc410.data.EventService;
import ca.ubc.cs.cpsc410.data.Guest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;


/**
 * Created by ryan on 28/10/15.
 * <p>
 * Represents controller that performs Event operations, e.g. create, cancel
 */
@RestController
public class EventController {

    private final EventService eventService;

    @Autowired
    public EventController(final EventService eventService) {
        this.eventService = eventService;
    }

    @RequestMapping(value = "/event/createEvent", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE)
    public Event createEvent(@RequestBody @Valid final Event event) {
        return eventService.createEvent(event);
    }

    @RequestMapping(value = "/event/cancelEvent", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE)
    public void cancelEvent(@RequestBody @Valid final Event event) {
        eventService.cancelEvent(event);
    }

    @RequestMapping(value = "/event/finalizeEvent", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE)
    public Event finalizeEvent(@RequestBody @Valid final Event event) {
        return eventService.finalizeEvent(event);
    }

    @RequestMapping(value = "/event/getEvent", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE)
    public Event getEvent(@RequestBody @Valid final Event event) {
        return eventService.getEvent(event);
    }

    @RequestMapping(value = "/event/addInvitees", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE)
    public Event addInvitees(@RequestBody @Valid final List<Guest> guests) {
        return eventService.addInvitees(guests);
    }
    
    @RequestMapping(value = "/event/findTime", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE)
    public Event findTime(@RequestBody @Valid final Event event) {
        return eventService.findTime(event);
    }
    
    @ExceptionHandler
    @ResponseStatus(HttpStatus.CONFLICT)
    public String handleException(Exception e) {
        return e.getMessage();
    }
}
