package ca.ubc.cs.cpsc410.data;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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

}