package ca.ubc.cs.cpsc410.data;

/**
 * Created by ryan on 28/10/15.
 * <p>
 * Interface used by JPA/database layer.
 */

public interface EventService {
    
    Event createEvent(Event event);
    
    Event cancelEvent(Event event);
    
    Event finalizeEvent(Event event);
    
    //List<Guest> updateEventGuest(Guest guest);
    
}
