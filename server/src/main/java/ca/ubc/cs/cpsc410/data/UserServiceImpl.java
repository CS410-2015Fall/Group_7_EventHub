package ca.ubc.cs.cpsc410.data;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by vincent on 20/10/15.
 * <p>
 * Implementation of UserService interface
 */
@Service
@Transactional
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final EventRepository eventRepository;

    @Autowired
    public UserServiceImpl(final UserRepository userRepository, EventRepository eventRepository) {
        this.userRepository = userRepository;
        this.eventRepository = eventRepository;
    }

    @Override
    public User createUser(final User user) {
        if (user.getUsername() == null || user.getUsername().isEmpty()) {
            throw new RuntimeException("Error: User has an empty username!");
        }
        if (user.getEmail() == null || user.getEmail().isEmpty()) {
            throw new RuntimeException("Error: User has an empty email!");
        }
        try {
            InternetAddress emailAddr = new InternetAddress(user.getEmail());
            emailAddr.validate();
        } catch (AddressException e) {
            throw new RuntimeException("Error: Email invalid!");
        }
        List<User> existingUsers = userRepository.findAll();
        for (User existingUser : existingUsers) {
            if (existingUser.getUsername().equals(user.getUsername())) {
                throw new RuntimeException(String.format(
                        "Error: User %s already exists!", user.getUsername()));
            }
            if (existingUser.getEmail().equals(user.getEmail())) {
                throw new RuntimeException(String.format(
                        "Error: User with email %s already exists!", user.getEmail()));
            }
        }
        return userRepository.save(user);
    }

    @Override
    public User validateUser(User user) {
        List<User> existingUsers = userRepository.findAll();
        for (User existingUser : existingUsers) {
            if (existingUser.getUsername().equals(user.getUsername())) {
                if (existingUser.getPassword().equals(user.getPassword())) {
                    return existingUser;
                }
                throw new RuntimeException("Error: Password incorrect!");
            }
        }
        throw new RuntimeException(String.format(
                "Error: User %s does not exist!", user.getUsername()));
    }

    @Override
    public User findById(User user) {
        User existingUser = userRepository.findOne(user.getId());
        if (existingUser != null) {
            return existingUser;
        }
        throw new RuntimeException(String.format(
                "Error: User ID %d does not exist!", user.getId()));
    }

    @Override
    public User findByUsername(User user) {
        List<User> existingUsers = userRepository.findAll();
        for (User existingUser : existingUsers) {
            if (existingUser.getUsername().equals(user.getUsername())) {
                return existingUser;
            }
        }
        throw new RuntimeException(String.format(
                "Error: User %s does not exist!", user.getUsername()));
    }

    @Override
    public User findByEmail(User user) {
        List<User> existingUsers = userRepository.findAll();
        for (User existingUser : existingUsers) {
            if (existingUser.getEmail().equals(user.getEmail())) {
                return existingUser;
            }
        }
        throw new RuntimeException(String.format(
                "Error: No user with email %s!", user.getEmail()));
    }

    @Override
    public User addFriend(List<User> users) {
        if (users.size() != 2) {
            throw new RuntimeException("API expects a list of 2 users, where the 1st user is the user to modify and the 2nd user is the user to add as a friend");
        }
        User userToModify = users.get(0);
        User userToAddAsFriend = users.get(1);
        if (userToModify.getUsername().equals(userToAddAsFriend.getUsername())) {
            throw new RuntimeException("Error: Cannot add yourself as a friend!");
        }
        List<User> existingUsers = userRepository.findAll();
        if (!existingUsers.contains(userToAddAsFriend)) {
            throw new RuntimeException(String.format("Error: Friend %s does not exist!", userToAddAsFriend));
        }
        for (User existingUser : existingUsers) {
            if (existingUser.getUsername().equals(userToModify.getUsername())) {
                if (existingUser.getFriends().contains(userToAddAsFriend.getUsername())) {
                    throw new RuntimeException(String.format(
                            "Error: User %s is already a friend of user %s!", userToAddAsFriend, userToModify));
                }
                existingUser.getFriends().add(userToAddAsFriend.getUsername());
                User userToReturn = userRepository.save(existingUser);
                for (User existingUserFriend : existingUsers) {
                    if (existingUserFriend.getUsername().equals(userToAddAsFriend.getUsername())) {
                        existingUserFriend.getFriends().add(userToModify.getUsername());
                        userRepository.save(existingUserFriend);
                    }
                }
                return userToReturn;
            }
        }
        throw new RuntimeException(String.format(
                "Error: User %s does not exist!", userToModify.getUsername()));
    }

    @Override
    public User removeFriend(List<User> users) {
        if (users.size() != 2) {
            throw new RuntimeException("API expects a list of 2 users, where the 1st user is the user to modify and the 2nd user is the user to remove as a friend");
        }
        User userToModify = users.get(0);
        User userToRemoveAsFriend = users.get(1);
        List<User> existingUsers = userRepository.findAll();
        if (!existingUsers.contains(userToRemoveAsFriend)) {
            throw new RuntimeException(String.format("Error: Friend %s does not exist!", userToRemoveAsFriend));
        }
        for (User existingUser : existingUsers) {
            if (existingUser.getUsername().equals(userToModify.getUsername())) {
                if (!existingUser.getFriends().contains(userToRemoveAsFriend.getUsername())) {
                    throw new RuntimeException(String.format(
                            "Error: User %s is not a friend of user %s!", userToRemoveAsFriend, userToModify));
                }
                existingUser.getFriends().remove(userToRemoveAsFriend.getUsername());
                User userToReturn = userRepository.save(existingUser);
                for (User existingUserFriend : existingUsers) {
                    if (existingUserFriend.getUsername().equals(userToRemoveAsFriend.getUsername())) {
                        existingUserFriend.getFriends().remove(userToModify.getUsername());
                        userRepository.save(existingUserFriend);
                    }
                }
                return userToReturn;
            }
        }
        throw new RuntimeException(String.format(
                "Error: User %s does not exist!", userToModify.getUsername()));
    }

    @Override
    public List<User> getAllFriends(User user) {
        List<User> existingUsers = userRepository.findAll();
        List<String> friendsAsStrings = new ArrayList<>();
        List<User> friends = new ArrayList<>();
        for (User existingUser : existingUsers) {
            if (existingUser.getUsername().equals(user.getUsername())) {
                friendsAsStrings = existingUser.getFriends();
            }
        }
        for (String friend : friendsAsStrings) {
            for (User existingUser : existingUsers) {
                if (existingUser.getUsername().equals(friend)) {
                    friends.add(existingUser);
                }
            }
        }
        return friends;
    }

    @Override
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    @Override
    public List<Event> getAllEvents(User user) {
        List<User> existingUsers = userRepository.findAll();
        List<Event> eventsToReturn = new ArrayList<>();
        for (User existingUser : existingUsers) {
            if (existingUser.getUsername().equals(user.getUsername())) {
                List<Integer> eventsAsInts = existingUser.getEvents();
                for (int eventAsInt : eventsAsInts) {
                    Event existingEvent = eventRepository.findOne(eventAsInt);
                    if (existingEvent != null) {
                        eventsToReturn.add(existingEvent);
                    }
                }
                return eventsToReturn;
            }
        }
        throw new RuntimeException(String.format(
                "Error: User %s does not exist!", user.getUsername()));
    }

    @Override
    public List<Event> getPendingEvents(User user) {
        List<User> existingUsers = userRepository.findAll();
        List<Event> eventsToReturn = new ArrayList<>();
        // TODO: check that event exists
        for (User existingUser : existingUsers) {
            if (existingUser.getUsername().equals(user.getUsername())) {
                List<Integer> pendingEvents = existingUser.getPendingEvents();
                for (int eventAsInt : pendingEvents) {
                    Event existingEvent = eventRepository.findOne(eventAsInt);
                    if (existingEvent != null) {
                        eventsToReturn.add(existingEvent);
                    }
                }

                return eventsToReturn;
            }
        }
        throw new RuntimeException(String.format(
                "Error: User %s does not exist!", user.getUsername()));
    }

    @Override
    public void acceptPendingEvent(Guest guest) {
        List<User> existingUsers = userRepository.findAll();
        Event existingEvent = eventRepository.findOne(guest.getEventId());
        if (existingEvent == null) {
            throw new RuntimeException(String.format("Error: Event id %d does not exist!", guest.getEventId()));
        }
        if (guest.getUsername().equals(existingEvent.getHost())) {
            throw new RuntimeException(String.format("Error: Username %s is the host of the event and so cannot accept or reject an invite to this event", existingEvent.getHost()));
        }
        for (User existingUser : existingUsers) {
            if (existingUser.getUsername().equals(guest.getUsername())) {
                existingUser.getEvents().add(guest.getEventId());
                removeAndSaveEventId(existingUser, guest);
            }
        }
        if (existingEvent.getId() == guest.getEventId()) {
            existingEvent.getConfirmedInvitees().add(guest.getUsername());
            eventRepository.save(existingEvent);
        }
    }

    @Override
    public void rejectPendingEvent(Guest guest) {
        List<User> existingUsers = userRepository.findAll();
        Event existingEvent = eventRepository.findOne(guest.getEventId());
        if (existingEvent == null) {
            throw new RuntimeException(String.format("Error: Event id %d does not exist!", guest.getEventId()));
        }
        if (guest.getUsername().equals(existingEvent.getHost())) {
            throw new RuntimeException(String.format("Error: Username %s is the host of the event and so cannot accept or reject an invite to this event", existingEvent.getHost()));
        }
        for (User existingUser : existingUsers) {
            if (existingUser.getUsername().equals(guest.getUsername())) {
                removeAndSaveEventId(existingUser, guest);
            }
        }
    }

    @Override
    public User addFacebookToken(User user) {
        if (user.getFacebookToken() == null || user.getFacebookToken().isEmpty()) {
            throw new RuntimeException(String.format("Error: User %s has a null or empty Facebook Token!", user.getUsername()));
        }
        List<User> existingUsers = userRepository.findAll();
        for (User existingUser : existingUsers) {
            if (user.getUsername().equals(existingUser.getUsername())) {
                existingUser.setFacebookToken(user.getFacebookToken());
                return userRepository.save(existingUser);
            }
        }
        throw new RuntimeException(String.format(
                "Error: User %s does not exist!", user.getUsername()));
    }

    @Override
    public User addGoogleEvents(List<GoogleEvent> googleEvents) {
        List<User> existingUsers = userRepository.findAll();
        if (googleEvents.isEmpty()) {
            throw new RuntimeException("Error: Empty list of google events as input!");
        }
        String username = googleEvents.get(0).getUsername();
        for (GoogleEvent googleEvent : googleEvents) {
            if (googleEvent.getUsername() == null || googleEvent.getUsername().isEmpty()) {
                throw new RuntimeException("Error: One or more events are missing an username!");
            }
            if (!googleEvent.getUsername().equals(username)) {
                throw new RuntimeException("Error: One or more events do not have the same username!");
            }
        }
        User userToModify = null;
        for (User existingUser : existingUsers) {
            if (existingUser.getUsername().equals(username)) {
                userToModify = existingUser;
                break;
            }
        }
        if (userToModify == null) {
            throw new RuntimeException(String.format(
                    "Error: User %s does not exist!", username));
        }
        // Delete all current google events from the event repository (we'll add them back later)
        for (int existingEventId : userToModify.getEvents()) {
            if (eventRepository.findOne(existingEventId).getType().equals("google")) {
                eventRepository.delete(existingEventId);
                userToModify.getEvents().remove(existingEventId);
            }
        }
        for (GoogleEvent googleEvent : googleEvents) {
            Event newlyCreatedEvent = new Event();
            newlyCreatedEvent.setType("google");
            newlyCreatedEvent.setHost(username);
            newlyCreatedEvent.setStartDate(googleEvent.getDtstart());
            newlyCreatedEvent.setEndDate(googleEvent.getDtend());
            newlyCreatedEvent.setName(googleEvent.getTitle());
            newlyCreatedEvent.setLocation(googleEvent.getEventLocation());
            Event savedEventWithAutoGeneratedId = eventRepository.save(newlyCreatedEvent);
            userToModify.getEvents().add(savedEventWithAutoGeneratedId.getId());
        }
        return userRepository.save(userToModify);
    }

    private void removeAndSaveEventId(User existingUser, Guest guest) {
        int indexToRemove = existingUser.getPendingEvents().indexOf(guest.getEventId());
        if (indexToRemove != -1) { // -1: index does not exist, will throw ArrayIndexOutOfBoundsException
            existingUser.getPendingEvents().remove(indexToRemove);
            userRepository.save(existingUser);
        }
    }

}