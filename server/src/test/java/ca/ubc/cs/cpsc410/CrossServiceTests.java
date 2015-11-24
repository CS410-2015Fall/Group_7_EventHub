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

import ca.ubc.cs.cpsc410.data.Event;
import ca.ubc.cs.cpsc410.data.EventRepository;
import ca.ubc.cs.cpsc410.data.EventService;
import ca.ubc.cs.cpsc410.data.EventServiceImpl;
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
	private EventRepository eventRepository;
	private EventService eventService;
	
	@Before
	public void setUp() throws Exception {
        userService = new UserServiceImpl(userRepository, eventRepository);
        eventService = new EventServiceImpl(eventRepository, userRepository);
    }
	
	@Test
	public void verifyEventsAndPendingEvents() {
		// TODO: Finish this test which checks a user's events (they have to be created)
		List<Event> mockUserEvents = new ArrayList<Event>();
		List<Event> mockUserPendingEvents = new ArrayList<Event>();
		
		Event mockEventOne = new Event();
		mockEventOne.setIsFinalized(true);
		mockEventOne.setId(1);
		mockEventOne.setName("SomeEvent");
		
		Event mockEventTwo = new Event();
		mockEventTwo.setIsFinalized(true);
		mockEventTwo.setId(2);
		mockEventTwo.setName("SomeOtherEvent");
		
		mockUserEvents.add(mockEventOne);
		mockUserEvents.add(mockEventTwo);
		
		Event mockPendingEventOne = new Event();
		mockPendingEventOne.setIsFinalized(false);
		mockPendingEventOne.setId(3);
		mockPendingEventOne.setName("Undetermined");
		
		Event mockPendingEventTwo = new Event();
		mockPendingEventTwo.setIsFinalized(false);
		mockPendingEventTwo.setId(4);
		mockPendingEventTwo.setName("I don't know");
		
		mockUserPendingEvents.add(mockPendingEventOne);
		mockUserPendingEvents.add(mockPendingEventTwo);
		
	}
}
