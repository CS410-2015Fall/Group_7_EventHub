package ca.ubc.cs.cpsc410.data;

import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Created by ryan on 28/10/15.
 * <p>
 * Spring Data JPA repository, providing generic database containing
 * Event objects (with each Event object having a primary key "id" of
 * type Integer).
 */

public interface EventRepository extends JpaRepository<Event, Integer> {

}
