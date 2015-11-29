package ca.ubc.cs.cpsc410.data;

import javax.persistence.*;
import java.util.Date;
import java.util.Set;

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

    @Column(name = "name", length = 65535)
    private String name;

    @Column(name = "description", length = 65535)
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

    @Column(name = "duration")
    private int duration;

    @Column(name = "location")
    private String location;

    @ElementCollection(fetch = FetchType.EAGER, targetClass = String.class)
    @Column(name = "confirmedInvitees")
    private Set<String> confirmedInvitees;

    @ElementCollection(fetch = FetchType.EAGER, targetClass = String.class)
    @Column(name = "invitees")
    private Set<String> invitees;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        // max length for description field is set to 65535 bytes in our Event class
        this.name = name.substring(0, Math.min(description.toString().length(), 65535));
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        // max length for description field is set to 65535 bytes in our Event class
        this.description = description.substring(0, Math.min(description.toString().length(), 65535));
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public boolean getIsFinalized() {
        return isFinalized;
    }

    public void setIsFinalized(boolean isFinalized) {
        this.isFinalized = isFinalized;
    }

    public Date getStartDate() {
        return startDate;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    public Date getEndDate() {
        return endDate;
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }

    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public Set<String> getInvitees() {
        return invitees;
    }

    public void setInvitees(Set<String> invitees) {
        this.invitees = invitees;
    }

    public Set<String> getConfirmedInvitees() {
        return confirmedInvitees;
    }

    public void setConfirmedInvitees(Set<String> confirmedInvitees) {
        this.confirmedInvitees = confirmedInvitees;
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
