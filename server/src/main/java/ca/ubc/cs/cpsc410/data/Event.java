package ca.ubc.cs.cpsc410.data;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.Date;

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
    
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "startDate")
    private Date startDate;
    
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "endDate")
    private Date endDate;
    
    @Column(name = "location")
    private String location;
    
    // guest list
    
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
    
    public Date getStartDate() {
        return startDate;
    }
    
    public Date getEndDate() {
        return endDate;
    }
    
    public String getLocation() {
        return location;
    }
    
    // guest list
    
}
