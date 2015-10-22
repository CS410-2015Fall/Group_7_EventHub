package ca.ubc.cs.cpsc410.data;

import org.springframework.data.repository.query.Param;

/**
 * Created by vincent on 20/10/15.
 * <p>
 * Interface used by JPA/database layer.
 */
public interface UserService {

    User createUser(User user);

    User validateUser(User user);

}