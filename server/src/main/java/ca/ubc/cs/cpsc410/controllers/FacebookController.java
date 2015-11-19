package ca.ubc.cs.cpsc410.controllers;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.social.facebook.api.Facebook;
import org.springframework.social.facebook.api.Invitation;
import org.springframework.social.facebook.api.PagedList;
import org.springframework.social.facebook.api.impl.FacebookTemplate;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import ca.ubc.cs.cpsc410.data.*;

/**
 * Created by vincent on 20/10/15.
 * <p>
 * Represents controller to handle all Facebook-related API operations
 */
@RestController
public class FacebookController {

    private final UserRepository userRepository;
    private final EventRepository eventRepository;

    @Autowired
    public FacebookController(final UserRepository userRepository, final EventRepository eventRepository) {
        this.userRepository = userRepository;
        this.eventRepository = eventRepository;
    }
    
    @RequestMapping(value = "/facebook/getFacebookEvents", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE)
    public List<Event> getFacebookEvents(@RequestBody @Valid final User user) {
        List<User> existingUsers = userRepository.findAll();
        User userToModify = null;
        for (User existingUser : existingUsers) {
            if (existingUser.getUsername().equals(user.getUsername())) {
                userToModify = existingUser;
                break;
            }
        }
        if (userToModify == null) {
            throw new RuntimeException(String.format("Error: User %s does not exist!", user.getUsername()));
        }
        String accessToken = userToModify.getFacebookToken();
        if (accessToken.isEmpty()) {
            throw new RuntimeException(String.format("Error: User %s does not have a Facebook access token!", userToModify.getUsername()));
        }
        Facebook facebook = new FacebookTemplate(accessToken);
        if (!facebook.isAuthorized()) {
            throw new RuntimeException(String.format("Error: User %s does not have a valid Facebook access token!", userToModify.getUsername()));
        }
        PagedList<Invitation> facebookEvents = facebook.eventOperations().getAttending();
        if (facebookEvents.isEmpty()) {
            return null;
        }
        List<Integer> userEventsCopy = new ArrayList<>(userToModify.getEvents());
        for (int eventIDToRemove : userEventsCopy) {
            Event eventToRemove = eventRepository.findOne(eventIDToRemove);
            if (eventToRemove != null && eventToRemove.getType().equals("facebook")) {
                eventRepository.delete(eventIDToRemove);
                userToModify.getEvents().remove(userToModify.getEvents().indexOf(eventIDToRemove));  
            }
        }
        List<Event> returnEvents = new ArrayList<>();
        for (Invitation facebookEvent : facebookEvents) {
            if (facebookEvent.getStartTime().after(new Date())) {
                Event wesyncEvent = new Event();
                wesyncEvent.setHost(userToModify.getUsername());
                wesyncEvent.setName(facebookEvent.getName());
                wesyncEvent.setType("facebook");
                wesyncEvent.setIsFinalized(true);
                wesyncEvent.setStartDate(facebookEvent.getStartTime());
                wesyncEvent.setEndDate(facebookEvent.getEndTime());
                wesyncEvent.setLocation(facebookEvent.getLocation());
                Event savedWesyncEvent = eventRepository.save(wesyncEvent);
                userToModify.getEvents().add(savedWesyncEvent.getId());
                returnEvents.add(savedWesyncEvent);
            }
        }
        userRepository.save(userToModify);
        return returnEvents;
    }

}
