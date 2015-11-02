package ca.ubc.cs.cpsc410.data;

import java.util.List;

/**
 * Created by vincent on 20/10/15.
 * <p>
 * Interface used by JPA/database layer.
 */
public interface UserService {

    User createUser(User user);

    User validateUser(User user);

    User findById(User user);

    User findByUsername(User user);

    User findByEmail(User user);

    /**
     * @param user A list of 2 users, where the 1st user is the user to modify
     *             and the 2nd user is the user to add as a friend
     */
    User addFriend(List<User> users);

    /**
     * @param user A list of 2 users, where the 1st user is the user to modify
     *             and the 2nd user is the user to remove as a friend
     */
    User removeFriend(List<User> users);

    List<User> getAllFriends(User user);

    List<User> getAllUsers();

    //List<Event> sendEvents(List<Event> events);

    List<Event> getAllEvents(User user);
    
    List<Event> getPendingEvents(User user);
    
    void acceptPendingEvent(Guest guest);
    
    void rejectPendingEvent(Guest guest);

}