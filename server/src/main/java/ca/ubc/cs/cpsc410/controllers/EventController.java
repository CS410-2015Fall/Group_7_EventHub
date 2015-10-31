package ca.ubc.cs.cpsc410.controllers;


import ca.ubc.cs.cpsc410.data.Event;
import ca.ubc.cs.cpsc410.data.EventService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;


/**
 * Created by ryan on 28/10/15.
 * <p>
 * Represents controller that performs Event operations, e.g. create, cancel
 */

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
    public Event cancelEvent(@RequestBody @Valid final Event event) {
        return eventService.cancelEvent(event);
    }
    @RequestMapping(value = "/event/finalizeEvent", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE)
    public Event finalizeEvent(@RequestBody @Valid final Event event) {
        return eventService.finalizeEvent(event);
    }
    @RequestMapping(value = "/event/getEvent", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE)
    public Event getEvent(@RequestBody @Valid final Event event) {
        return eventService.getEvent(event);
    }
    @RequestMapping(value = "/event/updateEvent", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE)
    public Event updateEvent(@RequestBody @Valid final Event event) {
        return eventService.updateEvent(event);
    }
}
