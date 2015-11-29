package ca.ubc.cs.cpsc410.data;

import javax.persistence.*;

/**
 * Created by vincent on 01/11/15.
 */
@Entity
public class Guest {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    @Column(name = "id", unique = true, nullable = false, updatable = false)
    private int id;

    @Column(name = "username")
    private String username;
    
    @Column(name = "eventId")
    private int eventId;

    @Column(name = "response")
    private String response;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setEventId(int eventId) {
        this.eventId = eventId;
    }

    public String getUsername() {
        return username;
    }
    
    public int getEventId() {
        return eventId;
    }


}