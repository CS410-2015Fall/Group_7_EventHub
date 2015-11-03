package ca.ubc.cs.cpsc410.data;

import javax.persistence.*;

import org.hibernate.annotations.IndexColumn;

import java.util.List;

/**
 * Created by vincent on 20/10/15.
 * <p>
 * Represents User data type used to store users in database.
 */
@Entity
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    @Column(name = "id", unique = true, nullable = false, updatable = false)
    private int id;

    @Column(name = "username", unique = true, nullable = false, updatable = false)
    private String username;

    @Column(name = "password")
    private String password;

    @Column(name = "email", unique = true, nullable = false)
    private String email;


    @Column(name = "friends")
    @ElementCollection(fetch = FetchType.EAGER, targetClass = String.class)
    private List<String> friends;

    @ElementCollection(targetClass = Integer.class)
    @Column(name = "events")
    private List<Integer> events;

    @ElementCollection(targetClass = Integer.class)
    @Column(name = "pendingEvents")
    private List<Integer> pendingEvents; 

    public int getId() {
        return id;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public String getEmail() {
        return email;
    }

    public List<String> getFriends() {
        return friends;
    }

    public List<Integer> getEvents() {
        return events;
    }
    
    public List<Integer> getPendingEvents() {
        return pendingEvents;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        User user = (User) o;

        return !(username != null ? !username.equals(user.username) : user.username != null);

    }

    @Override
    public int hashCode() {
        return username != null ? username.hashCode() : 0;
    }

    @Override
    public String toString() {
        return "User{" +
                "username='" + username + '\'' +
                '}';
    }
    
    // Setters for testing purposes
    public void setId(int id) {
    	this.id = id;
    }
    
    public void setUsername(String username) {
        this.username = username;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setFriends(List<String> friends) {
        this.friends = friends;
    }
    
    public void setEvents(List<Integer> events) {
        this.events = events;
    }

    public void setPendingEvents(List<Integer> pendingEvents) {
        this.pendingEvents = pendingEvents;
    }

}