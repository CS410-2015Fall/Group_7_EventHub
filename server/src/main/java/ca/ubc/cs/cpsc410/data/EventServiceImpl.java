package ca.ubc.cs.cpsc410.data;

import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Created by ryan on 28/10/15.
 * <p>
 * Implementation of EventService interface
 */

@Service
@Transactional
public class EventServiceImpl implements EventService {

    private final EventRepository eventRepository;
    private final UserRepository userRepository;
    
    @Autowired
    public EventServiceImpl(final EventRepository eventRepository, final UserRepository userRepository) {
        this.eventRepository = eventRepository;
        this.userRepository = userRepository;
    }

    @Override
    public Event createEvent(Event event) {
        if (event.getName() == null) {
            throw new RuntimeException("Error creating event: There is no event name specified!");
        }
        Date currentDate = new Date();
        if (event.getStartDate().before(currentDate)) {
            throw new RuntimeException("Error creating event: The start date has already passed!");
        }
        if (event.getEndDate().before(event.getStartDate())) {
            throw new RuntimeException("Error creating event: The event ends before it starts!");
        }
        List<User> existingUsers = userRepository.findAll();
        for (User existingUser : existingUsers) {
            if (existingUser.getUsername().equals(event.getHost())) {
                return eventRepository.save(event);
            }
        }
        throw new RuntimeException(String.format("Error creating event: Host %s does not match an existing user!", event.getHost()));
    }

    @Override
    public Event cancelEvent(Event event) {
        // TODO delete event and all pending invites
        return null;
    }

    @Override
    public Event finalizeEvent(Event event) {
        // TODO remove all pending invites
        return null;
    }

    @Override
    public Event getEvent(Event event) {
        List<Event> existingEvents = eventRepository.findAll();
        for (Event existingEvent : existingEvents) {
            if (existingEvent.getId() == event.getId()) {
                return existingEvent;
            }
        }
        throw new RuntimeException(String.format("Error getting event: Event %s does not exist!", event.getId()));
    }

    @Override
    public Event updateEvent(Event event) {
        if (eventRepository.exists(event.getId())) {
            return eventRepository.save(event);
        }
        throw new RuntimeException(String.format("Error updating event: Event %s does not exist!", event.getId()));
    }

}
