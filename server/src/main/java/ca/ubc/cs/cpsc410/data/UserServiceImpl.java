package ca.ubc.cs.cpsc410.data;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
        List<User> existingUsers = userRepository.findAll();
        for (User existingUser : existingUsers) {
            if (existingUser.getId() == user.getId()) {
                return existingUser;
            }
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
                return userRepository.save(existingUser);
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
                return userRepository.save(existingUser);
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
        List<Event> existingEvents = eventRepository.findAll();
        for (User existingUser : existingUsers) {
            if (existingUser.getUsername().equals(user.getUsername())) {
                List<Integer> eventsAsInts = existingUser.getEvents();
                List<Event> eventsToReturn = new ArrayList<>();
                for (Event event : existingEvents) {
                    for (int eventAsInt : eventsAsInts) {
                        if (event.getId() == eventAsInt) {
                            eventsToReturn.add(event);
                        }
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
        List<Event> existingEvents = eventRepository.findAll();
        List<Event> returnedEvents = new ArrayList<>();
        // TODO: check that event exists
        for (User existingUser : existingUsers) {
            if (existingUser.getUsername().equals(user.getUsername())) {
                List<Integer> pendingEvents = existingUser.getPendingEvents();
                for (Event existingEvent : existingEvents) {
                    if (pendingEvents.contains(existingEvent.getId())) {
                        returnedEvents.add(existingEvent);
                    }
                }
                return returnedEvents;
            }
        }
        throw new RuntimeException(String.format(
                "Error: User %s does not exist!", user.getUsername()));
    }

    @Override
    public void acceptPendingEvent(Guest guest) {
        List<User> existingUsers = userRepository.findAll();
        List<Event> existingEvents = eventRepository.findAll();
        // TODO: check that event exists
        for (User existingUser : existingUsers) {
            if (existingUser.getUsername().equals(guest.getUsername())) {
                existingUser.getEvents().add(guest.getEventId());
                removeAndSaveEventId(existingUser, guest);
            }
        }
    }

    @Override
    public void rejectPendingEvent(Guest guest) {
        List<User> existingUsers = userRepository.findAll();
        List<Event> existingEvents = eventRepository.findAll();
        // TODO: check that event exists
        for (User existingUser : existingUsers) {
            if (existingUser.getUsername().equals(guest.getUsername())) {
                removeAndSaveEventId(existingUser, guest);
            }
        }    
    }
    
    private void removeAndSaveEventId(User existingUser, Guest guest) {
        int indexToRemove = existingUser.getPendingEvents().indexOf(guest.getEventId());
        existingUser.getPendingEvents().remove(indexToRemove);
        userRepository.save(existingUser);
    }

}