package ca.ubc.cs.cpsc410.data;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
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
        if (event.getStartDate() == null) {
            throw new RuntimeException(String.format("Error: Event %s does not have a start date!", event.getName()));
        }
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

    /**
     * Finds a free time in between 8 am - 8 pm on any given day. These values are hardcoded in the implementation.
     *
     * @param event the event to find time for
     * @return an event object with the start and end dates modified with a suggested time duration
     */
    @Override
    public Event findTime(Event event) {
        List<User> existingUsers = userRepository.findAll();
        Event existingEvent = eventRepository.findOne(event.getId());
        if (existingEvent == null) {
            throw new RuntimeException(String.format(
                    "Error: Event %d could not be found!", event.getId()));
        }
        Date startTime = existingEvent.getStartDate();
        //TODO existingEvent.getDuration();
        int duration = 60;
        if (startTime == null) {
            startTime = new Date();
        }
        User host = null;
        for (User existingUser : existingUsers) {
            if (existingUser.getUsername().equals(existingEvent.getHost())) {
                host = existingUser;
            }
        }
        if (host == null) {
            throw new RuntimeException(String.format(
                    "Error: User %s does not exist!", existingEvent.getHost()));
        }
        List<Event> eventsOfHost = getAllSortedUserEvents(host);
        // now we have a sorted event list and we need to find a time slot
        // 1. find a time past 8am on the startTime date and ensure the host doesn't have an event in from 8am - 8am + duration
        // 2. run the same check with all of the guests if the host doesn't conflict
        // 3. if it fails, we need to define where it conflicted so we can begin searching from there and then repeat 1 and 2
        // 4. if it passes then we update the event's start and end date and return it


        // TODO because Ryan wants me to - old code below
        /*List<Event> existingEvents = eventRepository.findAll();
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
        }*/

        return existingEvent;
    }

    private List<Event> getAllSortedUserEvents(User user) {
        List<Integer> eventIdsOfUser = user.getEvents();
        eventIdsOfUser.addAll(user.getPendingEvents());
        List<Event> eventsOfUser = new ArrayList<>();
        for (int eventId : eventIdsOfUser) {
            Event event = eventRepository.findOne(eventId);
            if (event != null) {
                eventsOfUser.add(event);
            }
        }
        eventsOfUser.sort(new Comparator<Event>() {
            @Override
            public int compare(Event o1, Event o2) {
                // We can assume that events do not have null start times
                return o1.getStartDate().compareTo(o2.getStartDate());
            }
        });
        return eventsOfUser;
    }

}
