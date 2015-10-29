package ca.ubc.cs.cpsc410.data;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Created by ryan on 28/10/15.
 * <p>
 * Implementation of EventService interface
 */

@Service
@Transactional
public class EventServiceImpl implements EventService {

    private final EventRepository repository;
    
    @Autowired
    public EventServiceImpl(final EventRepository repository) {
        this.repository = repository;
    }

    @Override
    public Event createEvent(Event event) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Event cancelEvent(Event event) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Event finalizeEvent(Event event) {
        // TODO Auto-generated method stub
        return null;
    }

}
