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

    User findByUsername(User user);

    User findByEmail(User user);

    List<User> getAllUsers();

}