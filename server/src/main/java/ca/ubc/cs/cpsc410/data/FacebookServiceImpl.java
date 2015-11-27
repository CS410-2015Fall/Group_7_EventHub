package ca.ubc.cs.cpsc410.data;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.social.facebook.api.Facebook;
import org.springframework.social.facebook.api.Invitation;
import org.springframework.social.facebook.api.PagedList;
import org.springframework.social.facebook.api.impl.FacebookTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * Created by vincent on 24/11/15.
 * <p>
 * Implementation of Facebook API calls that we use
 */
@Service
@Transactional
public class FacebookServiceImpl implements FacebookService {

    private final UserRepository userRepository;
    private final EventRepository eventRepository;

    @Autowired
    public FacebookServiceImpl(final UserRepository userRepository, final EventRepository eventRepository) {
        this.userRepository = userRepository;
        this.eventRepository = eventRepository;
    }

    @Override
    public List<Event> getFacebookEvents(User user) {
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
        for (Invitation invitation : facebookEvents) {
            if (invitation.getStartTime() == null) {
                throw new RuntimeException(String.format("Error: One or more Facebook events retrieved for user %s does not have a start date!", userToModify.getUsername()));
            }
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
                Map<String, Object> extraData = facebookEvent.getExtraData();
                // null checks for fields that can be potentially empty/null
                if (extraData != null && !extraData.isEmpty()) {
                    Object place = extraData.get("place");
                    if (place != null) {
                        wesyncEvent.setLocation(((Map<String, String>) place).get("name"));
                    }
                    Object description = extraData.get("description");
                    if (description != null) {
                        // max length for description field is set to 65535 bytes in our Event class
                        wesyncEvent.setDescription(description.toString().substring(0, Math.min(description.toString().length(), 65535)));
                    }
                }
                Event savedWesyncEvent = eventRepository.save(wesyncEvent);
                userToModify.getEvents().add(savedWesyncEvent.getId());
                returnEvents.add(savedWesyncEvent);
            }
        }
        userRepository.save(userToModify);
        return returnEvents;
    }
}
