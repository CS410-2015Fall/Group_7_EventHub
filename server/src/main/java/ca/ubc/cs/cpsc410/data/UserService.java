package ca.ubc.cs.cpsc410.data;

/**
 * Created by vincent on 20/10/15.
 * <p>
 * Interface used by JPA/database layer.
 */
public interface UserService {

    User save(User user);

}