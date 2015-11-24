package ca.ubc.cs.cpsc410.data;

import java.util.List;

/**
 * Created by vincent on 24/11/15.
 * <p>
 * Interface used by Facebook API implementation
 */
public interface FacebookService {

    List<Event> getFacebookEvents(User user);

}
