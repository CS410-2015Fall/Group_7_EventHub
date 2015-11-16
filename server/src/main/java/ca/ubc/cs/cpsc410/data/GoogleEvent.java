package ca.ubc.cs.cpsc410.data;

import javax.persistence.*;
import java.util.Date;

/**
 * Created by vincent on 14/11/15.
 * <p>
 * Represents event object retrieved from Google Calendar
 */
@Entity
public class GoogleEvent {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    @Column(name = "id", unique = true, nullable = false, updatable = false)
    private int id;

    // "username" should map to an existing User object in UserRepository

    @Column(name = "username")
    private String username;

    // all other fields below are a 1-to-1 mapping to fields in GCal's JSON payloads

    @Column(name = "allDay")
    private int allDay;

    @Column(name = "calendar_id")
    private String calendar_id;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "dtend")
    private Date dtend;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "dtstart")
    private Date dtstart;

    @Column(name = "eventLocation")
    private String eventLocation;

    @Column(name = "title")
    private String title;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public int getAllDay() {
        return allDay;
    }

    public void setAllDay(int allDay) {
        this.allDay = allDay;
    }

    public String getCalendar_id() {
        return calendar_id;
    }

    public void setCalendar_id(String calendar_id) {
        this.calendar_id = calendar_id;
    }

    public Date getDtend() {
        return dtend;
    }

    public void setDtend(Date dtend) {
        this.dtend = dtend;
    }

    public Date getDtstart() {
        return dtstart;
    }

    public void setDtstart(Date dtstart) {
        this.dtstart = dtstart;
    }

    public String getEventLocation() {
        return eventLocation;
    }

    public void setEventLocation(String eventLocation) {
        this.eventLocation = eventLocation;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

}