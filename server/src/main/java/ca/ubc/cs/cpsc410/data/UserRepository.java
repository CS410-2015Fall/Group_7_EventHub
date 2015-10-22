package ca.ubc.cs.cpsc410.data;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;

/**
 * Created by vincent on 20/10/15.
 * <p>
 * Spring Data JPA repository, providing generic database containing
 * User objects (with each User object having a primary key "id" of
 * type Integer).
 */
public interface UserRepository extends JpaRepository<User, Integer> {

    User findByUsername(@Param("username") String username);

    User findByEmail(@Param("email") String email);

}