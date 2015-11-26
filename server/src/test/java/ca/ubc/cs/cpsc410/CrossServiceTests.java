package ca.ubc.cs.cpsc410;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;

import java.util.List;
import java.util.ArrayList;
import java.util.Date;

import ca.ubc.cs.cpsc410.data.Event;
import ca.ubc.cs.cpsc410.data.EventRepository;
import ca.ubc.cs.cpsc410.data.EventService;
import ca.ubc.cs.cpsc410.data.EventServiceImpl;
import ca.ubc.cs.cpsc410.data.Guest;
import ca.ubc.cs.cpsc410.data.User;
import ca.ubc.cs.cpsc410.data.UserRepository;
import ca.ubc.cs.cpsc410.data.UserService;
import ca.ubc.cs.cpsc410.data.UserServiceImpl;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = WesyncApplication.class)
@WebAppConfiguration
public class CrossServiceTests {
	@Autowired
	private UserRepository userRepository;
	private UserService userService;
	@Autowired
	private EventRepository eventRepository;
	private EventService eventService;
	
	@Before
	public void setUp() throws Exception {
        userService = new UserServiceImpl(userRepository, eventRepository);
        eventService = new EventServiceImpl(eventRepository, userRepository);
    }
	
	@Test
	public void verifyEventsAndPendingEvents() {
		// TODO: Might need to change this test later on
		List<Event> expectedUserEvents = new ArrayList<Event>();
		List<Event> expectedUserPendingEvents = new ArrayList<Event>();
		
		User mockUserParams = createUserParams(100, "mrPopular", "mockPassword", "popular@validemail.com", new ArrayList<String>());
		User mockHostParams = createUserParams(100, "host", "mockPassword", "host@validemail.com", new ArrayList<String>());
		
		User host = userService.createUser(mockHostParams);
		
		Event mockEventOne = new Event();
		mockEventOne.setIsFinalized(true);
		mockEventOne.setName("SomeEvent");
		mockEventOne.setHost(host.getUsername());
		mockEventOne.setStartDate(new Date(20500815));
		mockEventOne.setDuration(1);
		mockEventOne.setInvitees(new ArrayList<String>());
		mockEventOne.setConfirmedInvitees(new ArrayList<String>());
		
		Event mockEventTwo = new Event();
		mockEventTwo.setIsFinalized(true);
		mockEventTwo.setName("SomeOtherEvent");
		mockEventTwo.setHost(host.getUsername());
		mockEventTwo.setStartDate(new Date(20500816));
		mockEventTwo.setDuration(1);
		mockEventTwo.setInvitees(new ArrayList<String>());
		mockEventTwo.setConfirmedInvitees(new ArrayList<String>());
		
		
		Event eventOne = eventService.createEvent(mockEventOne);
		Event eventTwo = eventService.createEvent(mockEventTwo);
		
		expectedUserEvents.add(eventOne);
		expectedUserEvents.add(eventTwo);
		
		Event mockPendingEventOne = new Event();
		mockPendingEventOne.setIsFinalized(false);
		mockPendingEventOne.setName("Undetermined");
		mockPendingEventOne.setHost(host.getUsername());
		mockPendingEventOne.setStartDate(new Date(20500817));
		mockPendingEventOne.setDuration(1);
		mockPendingEventOne.setInvitees(new ArrayList<String>());
		mockPendingEventOne.setConfirmedInvitees(new ArrayList<String>());
		
		Event mockPendingEventTwo = new Event();
		mockPendingEventTwo.setIsFinalized(false);
		mockPendingEventTwo.setName("I don't know");
		mockPendingEventTwo.setHost(host.getUsername());
		mockPendingEventTwo.setStartDate(new Date(20500818));
		mockPendingEventTwo.setDuration(1);
		mockPendingEventTwo.setInvitees(new ArrayList<String>());
		mockPendingEventTwo.setConfirmedInvitees(new ArrayList<String>());
		
		Event pendingEventOne = eventService.createEvent(mockPendingEventOne);
		Event pendingEventTwo = eventService.createEvent(mockPendingEventTwo);
		
		expectedUserPendingEvents.add(pendingEventOne);
		expectedUserPendingEvents.add(pendingEventTwo);
		
		List<Integer> userEvents = new ArrayList<Integer>();
		List<Integer> pendingEvents = new ArrayList<Integer>();
		
		userEvents.add(eventOne.getId());
		userEvents.add(eventTwo.getId());
		pendingEvents.add(pendingEventOne.getId());
		pendingEvents.add(pendingEventTwo.getId());
		
		mockUserParams.setEvents(userEvents);
		mockUserParams.setPendingEvents(pendingEvents);
		User user = userService.createUser(mockUserParams);
		
		List<Event> actualUserEvents = userService.getAllEvents(userService.findByUsername(user));
		List<Event> actualUserPendingEvents = userService.getPendingEvents(userService.findByUsername(user));
		
		assertTrue(actualUserEvents.size() == expectedUserEvents.size());
		assertTrue(actualUserPendingEvents.size() == expectedUserPendingEvents.size());
		
		for (int i = 0; i < actualUserEvents.size(); i++) {
			assertTrue(actualUserEvents.get(i).getName().equals(expectedUserEvents.get(i).getName()));
			assertTrue(actualUserEvents.get(i).getHost().equals(expectedUserEvents.get(i).getHost()));
			assertTrue(actualUserEvents.get(i).getIsFinalized() == expectedUserEvents.get(i).getIsFinalized());
		}
		
		for (int i = 0; i < actualUserPendingEvents.size(); i++) {
			assertTrue(actualUserPendingEvents.get(i).getName().equals(expectedUserPendingEvents.get(i).getName()));
			assertTrue(actualUserPendingEvents.get(i).getHost().equals(expectedUserPendingEvents.get(i).getHost()));
			assertTrue(actualUserPendingEvents.get(i).getIsFinalized() == expectedUserPendingEvents.get(i).getIsFinalized());
		}
		
	}
	
	private User createUserParams(int id, String username, String password, String email, List<String> friends) {
		User mockUserParams = new User();
		mockUserParams.setId(id);
		mockUserParams.setUsername(username);
		mockUserParams.setPassword(password);
		mockUserParams.setEmail(email);
		mockUserParams.setFriends(friends);
		
		return mockUserParams;
	}

}
