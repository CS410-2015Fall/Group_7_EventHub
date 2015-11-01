package ca.ubc.cs.cpsc410.data;

import javax.persistence.*;
import java.util.List;

/**
 * Created by ryan on 28/10/15
 * <p>
 * Represents Event data type used to store events in database
 */

@Entity
public class Event {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    @Column(name = "id", unique = true, nullable = false, updatable = false)
    private int id;

    @Column(name = "host")
    private String host;

    @Column(name = "name")
    private String name;

    @Column(name = "description")
    private String description;

    @Column(name = "type")
    private String type;

    @Column(name = "startDate")
    private long startDate;

    @Column(name = "endDate")
    private long endDate;

    @Column(name = "location")
    private String location;

    @ElementCollection(targetClass = String.class)
    @Column(name = "invitees")
    private List<String> invitees;

    public int getId() {
        return id;
    }

    public String getHost() {
        return host;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public String getType() {
        return type;
    }

    public long getStartDate() {
        return startDate;
    }

    public long getEndDate() {
        return endDate;
    }

    public String getLocation() {
        return location;
    }

    public List<String> getInvitees() {
        return invitees;
    }

}
