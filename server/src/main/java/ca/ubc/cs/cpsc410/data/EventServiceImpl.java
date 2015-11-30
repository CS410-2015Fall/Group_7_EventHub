package ca.ubc.cs.cpsc410.data;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

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
    private final JavaMailSender javaMailSender;

    @Autowired
    public EventServiceImpl(final EventRepository eventRepository, final UserRepository userRepository, final JavaMailSender javaMailSender) {
        this.eventRepository = eventRepository;
        this.userRepository = userRepository;
        this.javaMailSender = javaMailSender;
    }

    @Override
    public Event createEvent(Event event) {
        if (event.getName() == null) {
            throw new RuntimeException("Error creating event: There is no event name specified!");
        }
        if (event.getStartDate() == null) {
            throw new RuntimeException(String.format("Error: Event %s does not have a start date!", event.getName()));
        }
        if (event.getDuration() == 0) {
            throw new RuntimeException(String.format("Error: Event %s does not have a duration!", event.getName()));
        }
        event.setEndDate(calculateEndTime(event.getStartDate(), event.getDuration()));
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
                userRepository.save(existingUser);
            }
            if (event.getConfirmedInvitees().contains(existingUser.getUsername())) {
                existingUser.getEvents().add(newEvent.getId());
                userRepository.save(existingUser);
            }
        }
        return newEvent;
    }

    @Override
    public void cancelEvent(Event event) {
        List<User> existingUsers = userRepository.findAll();
        Event existingEvent = eventRepository.findOne(event.getId());
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
            sendEmail(existingEvent);
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
     * Finds a free time in between 8 am - 10 pm on any given day. These values are hardcoded in the implementation.
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
        if (existingEvent.getIsFinalized()) {
            return existingEvent;
        }
        int duration = existingEvent.getDuration();
        Date startTime = existingEvent.getStartDate();
        if (startTime == null) {
            startTime = new Date();
        }
        // Every time findTime is called we will push the event's start time 30 minutes forward to ensure a new time is sent back
        startTime = setNewStartTime(new Date(startTime.getTime() + 30 * 60000), duration);
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
        // TODO: verify all events have an start and end date in google and facebook

        List<User> confirmedInvitees = new ArrayList<>();
        for (String confirmedInvitee : existingEvent.getConfirmedInvitees()) {
            for (User existingUser : existingUsers) {
                if (existingUser.getUsername().equals(confirmedInvitee)) {
                    confirmedInvitees.add(existingUser);
                }
            }
        }
        // Populate list of all confirmed and pending events of the host and all guests
        List<Event> allUserEvents = new ArrayList<>();
        for (User confirmedInvitee : confirmedInvitees) {
            allUserEvents.addAll(getAllUserEvents(confirmedInvitee));
        }
        allUserEvents.addAll(getAllUserEvents(host));
        allUserEvents.sort(new Comparator<Event>() {
            @Override
            public int compare(Event o1, Event o2) {
                // We can assume that events do not have null start times
                return o1.getStartDate().compareTo(o2.getStartDate());
            }
        });

        // now we a list of the host's and confirmed guests sorted event lists in one list and we need to find a time slot
        // 1. find a time past the startTime date and ensure the host or guests don't have an event from conflicting
        // 2. if it fails, we need to define where it conflicted so we can begin searching from there and then repeat 1 and 2
        // 3. if it passes then we update the event's start and end date and return it
        for (Event userEvent : allUserEvents) {
            // we don't want to look at the same event in the host's list of events
            if (userEvent.getId() == existingEvent.getId()) {
                continue;
            }
            // if startTime is after both the event's start and end time
            // we continue to the check the next event since our timeslot is past this event
            if (startTime.after(userEvent.getStartDate()) && startTime.after(userEvent.getEndDate())) {
                continue;
            }
            // if startTime is in the middle of an event
            // we have to adjust the startTime to the end of the current event and then continue to check the next event
            if (startTime.after(userEvent.getStartDate()) && startTime.before(userEvent.getEndDate())) {
                startTime = setNewStartTime(userEvent.getEndDate(), duration);
                continue;
            }
            // we now know that AT LEAST startTime is before the current event, what about endTime?
            // if endTime is after the event's start time, we know the event starts in the middle of our timeslot
            // we have to adjust the startTime to the end of the current event and then continue to check the next event
            if (calculateEndTime(startTime, duration).after(userEvent.getStartDate())) {
                startTime = setNewStartTime(userEvent.getEndDate(), duration);
                continue;
            }
            // we now know we have a valid timeslot for the host!!
            // our timeslot starts after all the previous events
            // our timeslot ends before the current event
            break;
        }
        existingEvent.setStartDate(startTime);
        existingEvent.setEndDate(calculateEndTime(startTime, duration));
        return eventRepository.save(existingEvent);
    }

    private List<Event> getAllUserEvents(User user) {
        List<Integer> eventIdsOfUser = user.getEvents();
        eventIdsOfUser.addAll(user.getPendingEvents());
        List<Event> eventsOfUser = new ArrayList<>();
        for (int eventId : eventIdsOfUser) {
            Event event = eventRepository.findOne(eventId);
            if (event != null) {
                eventsOfUser.add(event);
            }
        }
        return eventsOfUser;
    }

    /**
     * Calculates the end time given a startTime date object and a duration in minutes
     */
    private Date calculateEndTime(Date startTime, int duration) {
        Date endTime = new Date(startTime.getTime() + (duration * 60000));
        return endTime;
    }

    private Date setNewStartTime(Date startTime, int duration) {
        // if the startTime is before 8am we set the time to 8:00 am
        Calendar startTimeCal = Calendar.getInstance();
        startTimeCal.setTime(startTime);
        startTimeCal.setTimeZone(TimeZone.getTimeZone("America/Los_Angeles"));
        if (startTimeCal.get(Calendar.HOUR_OF_DAY) < 8) {
            startTimeCal.set(Calendar.HOUR_OF_DAY, 8);
            startTimeCal.set(Calendar.MINUTE, 0);
            startTimeCal.set(Calendar.SECOND, 0);
        }
        Calendar endTimeCal = Calendar.getInstance();
        endTimeCal.setTime(calculateEndTime(startTime, duration));
        endTimeCal.setTimeZone(TimeZone.getTimeZone("America/Los_Angeles"));
        // if the endTime is after 10pm we set the day to the next day and set the time to 8:00 am
        if (endTimeCal.get(Calendar.HOUR_OF_DAY) > 22) {
            startTimeCal.add(Calendar.DATE, 1);
            startTimeCal.set(Calendar.HOUR_OF_DAY, 8);
            startTimeCal.set(Calendar.MINUTE, 0);
            startTimeCal.set(Calendar.SECOND, 0);
        }
        Date returnTime = startTimeCal.getTime();
        return returnTime;
    }

    /**
     * Sends email to all confirmed invitees and host of a given event.
     */
    public void sendEmail(Event event) {
        List<User> existingUsers = userRepository.findAll();
        Event existingEvent = eventRepository.findOne(event.getId());
        for (User existingUser : existingUsers) {
            if (existingEvent.getHost().equals(existingUser.getUsername()) || existingEvent.getConfirmedInvitees().contains(existingUser.getUsername())) {
                SimpleMailMessage mailMessage = new SimpleMailMessage();
                mailMessage.setTo(existingUser.getEmail());
                mailMessage.setSubject("Event notification");
                mailMessage.setText(String.format("Hello %s,\nEvent %s has been finalized!\nPlease check WeSync for details.\n", existingUser.getUsername(), existingEvent.getName()));
                javaMailSender.send(mailMessage);
            }
        }
    }

}
