package ca.ubc.cs.cpsc410.data;

import java.util.List;

/**
 * Created by ryan on 28/10/15.
 * <p>
 * Interface used by JPA/database layer.
 */

public interface EventService {
    
    Event createEvent(Event event);
    
    void cancelEvent(Event event);
    
    Event finalizeEvent(Event event);
    
    Event getEvent(Event event);
    
    Event addInvitees(List<Guest> guests);
    
    Event findTime(Event event);
    
    //List<Guest> updateEventGuest(Guest guest);
    
}
