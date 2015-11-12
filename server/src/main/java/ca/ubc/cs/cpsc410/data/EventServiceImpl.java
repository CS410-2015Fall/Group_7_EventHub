package ca.ubc.cs.cpsc410.data;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

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
        // These checks are unnecessary
        /*Date currentDate = new Date();
        if (event.getStartDate() == 0 || new Date(event.getStartDate()).before(currentDate)) {
            throw new RuntimeException("Error creating event: The start date has already passed or doesn't exist!");
        }
        if (event.getEndDate() == 0 || new Date(event.getEndDate()).before(new Date(event.getStartDate()))) {
            throw new RuntimeException("Error creating event: The event ends before it starts or doesn't exist!");
        }*/
        Event newEvent = null;
        event.setType("wesync");
        List<User> existingUsers = userRepository.findAll();
        for (User existingUser : existingUsers) {
            if (existingUser.getUsername().equals(event.getHost())) {
                newEvent = eventRepository.save(event);
                existingUser.getEvents().add(newEvent.getId());
                userRepository.save(existingUser);
                break;
            }
        }
        if (newEvent == null) {
            throw new RuntimeException(String.format("Error creating event: Host %s does not match an existing user!", event.getHost()));
        }
        for (User existingUser : existingUsers) {
            if (event.getInvitees().contains(existingUser.getUsername())) {
                existingUser.getPendingEvents().add(newEvent.getId());
            }
            if (event.getConfirmedInvitees().contains(existingUser.getUsername())) {
                existingUser.getEvents().add(newEvent.getId());
            }
        }
        return newEvent;
    }

    @Override
    public void cancelEvent(Event event) {
        List<User> existingUsers = userRepository.findAll();
        Event existingEvent = eventRepository.getOne(event.getId());
        if (existingEvent != null) {
            for (User existingUser : existingUsers) {
                if (existingEvent.getInvitees().contains(existingUser.getUsername())) {
                    int indexToRemove = existingUser.getPendingEvents().indexOf(existingEvent.getId());
                    if (indexToRemove != -1) { // -1: index does not exist, will throw ArrayIndexOutOfBoundsException
                        existingUser.getPendingEvents().remove(indexToRemove);
                    }
                }
                if (existingUser.getEvents().contains(existingEvent.getId())) {
                    int indexToRemove = existingUser.getEvents().indexOf(existingEvent.getId());
                    if (indexToRemove != -1) { // -1: index does not exist, will throw ArrayIndexOutOfBoundsException
                        existingUser.getEvents().remove(indexToRemove);
                    }
                }
                userRepository.save(existingUser);
            }
            existingEvent.setInvitees(null);
            eventRepository.delete(existingEvent);
            return;
        }
        throw new RuntimeException(String.format(
                "Error: Event %d could not be found!", event.getId()));
    }

    @Override
    public Event finalizeEvent(Event event) {
        List<User> existingUsers = userRepository.findAll();
        Event existingEvent = eventRepository.findOne(event.getId());
        if (existingEvent != null) {
            for (User existingUser : existingUsers) {
                if (existingEvent.getInvitees().contains(existingUser.getUsername())) {
                    int indexToRemove = existingUser.getPendingEvents().indexOf(existingEvent.getId());
                    if (indexToRemove != -1) { // -1: index does not exist, will throw ArrayIndexOutOfBoundsException
                        existingUser.getPendingEvents().remove(indexToRemove);
                    }
                    userRepository.save(existingUser);
                }
            }
            existingEvent.setInvitees(null);
            existingEvent.setIsFinalized(true);
            return eventRepository.save(existingEvent);
        }
        throw new RuntimeException(String.format(
                "Error: Event %d could not be found!", event.getId()));

    }

    @Override
    public Event getEvent(Event event) {
        Event existingEvent = eventRepository.findOne(event.getId());
        if (existingEvent != null) {
            return existingEvent;
        }
        /*
        for (Event existingEvent : existingEvents) {
            if (existingEvent.getId() == event.getId()) {
                return existingEvent;
            }
        }
        */
        throw new RuntimeException(String.format("Error getting event: Event %s does not exist!", event.getId()));
    }

    @Override
    public Event addInvitees(List<Guest> guests) {
        // TODO: security check!!
        List<User> existingUsers = userRepository.findAll();
        int eventId = guests.get(0).getEventId();
        Event eventToModify = eventRepository.findOne(eventId);
        for (Guest guest : guests) {
            if (eventToModify.getHost().equals(guest.getUsername())) {
                throw new RuntimeException(String.format("Error: Username %s is the host of the event and so cannot accept or reject an invite to this event", guest.getUsername()));
            }
            if (guest.getEventId() != eventId) {
                throw new RuntimeException("Error: Guests must have the same event ID!");
            }
        }
        if (eventToModify != null) {
            for (Guest guest : guests) {
                for (User existingUser : existingUsers) {
                    if (!eventToModify.getInvitees().contains(guest.getUsername())) {
                        if (existingUser.getUsername().equals(guest.getUsername())) {
                            existingUser.getPendingEvents().add(guest.getEventId());
                            userRepository.save(existingUser);
                            eventToModify.getInvitees().add(guest.getUsername());
                        }
                    }
                }
            }
            return eventRepository.save(eventToModify);
        }
        throw new RuntimeException(String.format(
                "Error: Event %d does not exist!", eventId));
    }

    @Override
    public List<User> getInvitees(Event event) {
        List<User> existingUsers = userRepository.findAll();
        Event existingEvent = eventRepository.findOne(event.getId());
        if (existingEvent == null) {
            throw new RuntimeException(String.format("Error: Event id %d cannot be found!", event.getId()));
        }
        List<User> usersToReturn = new ArrayList<>();
        for (String invitee : existingEvent.getInvitees()) {
            for (User user : existingUsers) {
                if (user.getUsername().equals(invitee)) {
                    usersToReturn.add(user);
                }
            }
        }
        return usersToReturn;
    }

    @Override
    public List<User> getConfirmedInvitees(Event event) {
        List<User> existingUsers = userRepository.findAll();
        Event existingEvent = eventRepository.findOne(event.getId());
        if (existingEvent == null) {
            throw new RuntimeException(String.format("Error: Event id %d cannot be found!", event.getId()));
        }
        List<User> usersToReturn = new ArrayList<>();
        for (String confirmedInvitee : existingEvent.getConfirmedInvitees()) {
            for (User user : existingUsers) {
                if (user.getUsername().equals(confirmedInvitee)) {
                    usersToReturn.add(user);
                }
            }
        }
        return usersToReturn;
    }

    @Override
    public Event findTime(Event event) {
        // TODO: THIS IS MOCKED!! Write algorithm to find dates.
        List<Event> existingEvents = eventRepository.findAll();
        for (Event existingEvent : existingEvents) {
            if (existingEvent.getId() == event.getId()) {
                SimpleDateFormat format = new SimpleDateFormat("yyyy/MM/dd/HH/mm/ss");
                try {
                    existingEvent.setStartDate(format.parse("2015/11/05/10/00/00"));
                    existingEvent.setEndDate(format.parse("2015/11/05/11/00/00"));
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                return eventRepository.save(existingEvent);
            }
        }
        throw new RuntimeException(String.format(
                "Error: Event %d could not be found!", event.getId()));

    }

}
