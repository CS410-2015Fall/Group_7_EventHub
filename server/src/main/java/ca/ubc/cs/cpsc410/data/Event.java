package ca.ubc.cs.cpsc410.data;

import javax.persistence.*;

import java.util.Date;
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
    
    @Column(name = "isFinalized")
    private boolean isFinalized;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "startDate")
    private Date startDate;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "endDate")
    private Date endDate;

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

    public boolean getIsFinalized() {
        return isFinalized;
    }
    
    public void setId(int id) {
        this.id = id;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setType(String type) {
        this.type = type;
    }

    public void setIsFinalized(boolean isFinalized) {
        this.isFinalized = isFinalized;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public void setInvitees(List<String> invitees) {
        this.invitees = invitees;
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

    public List<String> getInvitees() {
        return invitees;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + id;
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        Event other = (Event) obj;
        if (id != other.id)
            return false;
        return true;
    }

    @Override
    public String toString() {
        return "Event [id=" + id + ", host=" + host + ", name=" + name + "]";
    }
    
}
