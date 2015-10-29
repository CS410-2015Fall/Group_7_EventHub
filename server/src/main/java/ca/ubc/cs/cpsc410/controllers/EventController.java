package ca.ubc.cs.cpsc410.controllers;

import org.springframework.beans.factory.annotation.Autowired;

import ca.ubc.cs.cpsc410.data.EventService;

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
}
