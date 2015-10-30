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

    private final UserRepository repository;

    @Autowired
    public UserServiceImpl(final UserRepository repository) {
        this.repository = repository;
    }

    @Override
    public User createUser(final User user) {
        List<User> existingUsers = repository.findAll();
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
        return repository.save(user);
    }

    @Override
    public User validateUser(User user) {
        List<User> existingUsers = repository.findAll();
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
        List<User> existingUsers = repository.findAll();
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
        List<User> existingUsers = repository.findAll();
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
        List<User> existingUsers = repository.findAll();
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
        List<User> existingUsers = repository.findAll();
        for (User existingUser : existingUsers) {
            if (existingUser.equals(userToModify)) {
                existingUser.getFriends().add(userToAddAsFriend.getUsername());
                return repository.save(existingUser);
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
        List<User> existingUsers = repository.findAll();
        for (User existingUser : existingUsers) {
            if (existingUser.equals(userToModify)) {
                existingUser.getFriends().remove(userToRemoveAsFriend.getUsername());
                return repository.save(existingUser);
            }
        }
        throw new RuntimeException(String.format(
                "Error: User %s does not exist!", userToModify.getUsername()));
    }

    @Override
    public List<User> getAllFriends(User user) {
        List<User> existingUsers = repository.findAll();
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
        return repository.findAll();
    }

}