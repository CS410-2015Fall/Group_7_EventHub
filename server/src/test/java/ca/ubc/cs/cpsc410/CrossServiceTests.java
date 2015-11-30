package ca.ubc.cs.cpsc410;

import ca.ubc.cs.cpsc410.data.*;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;

import java.util.*;

import static org.junit.Assert.assertTrue;

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
    @Autowired
    private JavaMailSender javaMailSender;

    @Before
    public void setUp() throws Exception {
        userService = new UserServiceImpl(userRepository, eventRepository);
        eventService = new EventServiceImpl(eventRepository, userRepository, javaMailSender);
    }

    @Test
    public void verifyEventsAndPendingEvents() {
        List<Event> expectedUserEvents = new ArrayList<Event>();
        List<Event> expectedUserPendingEvents = new ArrayList<Event>();

        User mockUserParams = createUserParams(100, "mrPopular", "mockPassword", "popular@validemail.com", new ArrayList<String>());
        User mockHostParams = createUserParams(100, "host", "mockPassword", "host@validemail.com", new ArrayList<String>());

        User host = userService.createUser(mockHostParams);

        Event mockEventOne = createEventParams("SomeEvent", host.getUsername(), new Date(20500815), 1, true, new HashSet<String>(), new HashSet<String>());
        Event mockEventTwo = createEventParams("SomeOtherEvent", host.getUsername(), new Date(20500816), 1, true, new HashSet<String>(), new HashSet<String>());

        Event eventOne = eventService.createEvent(mockEventOne);
        Event eventTwo = eventService.createEvent(mockEventTwo);

        expectedUserEvents.add(eventOne);
        expectedUserEvents.add(eventTwo);

        Event mockPendingEventOne = createEventParams("Undetermined", host.getUsername(), new Date(20500817), 1, false, new HashSet<String>(), new HashSet<String>());
        Event mockPendingEventTwo = createEventParams("I don't know", host.getUsername(), new Date(20500818), 1, false, new HashSet<String>(), new HashSet<String>());

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

    @Test
    public void addInviteesAndHaveUsersAcceptAndRejectPendingEvents() {
        User mockUserParamsOne = createUserParams(100, "aManWithADecision", "mockPassword", "manofthehour@validemail.com", new ArrayList<String>());
        User mockUserParamsTwo = createUserParams(100, "aWomanWithADecision", "mockPassword", "womanofthehour@validemail.com", new ArrayList<String>());
        User mockHostParams = createUserParams(100, "desperateHost", "mockPassword", "ineedfriends@validemail.com", new ArrayList<String>());

        User host = userService.createUser(mockHostParams);
        User userOne = userService.createUser(mockUserParamsOne);
        User userTwo = userService.createUser(mockUserParamsTwo);

        Event mockPendingEventOne = createEventParams("RandomEvent1", host.getUsername(), new Date(20500810), 1, false, new HashSet<String>(), new HashSet<String>());
        Event mockPendingEventTwo = createEventParams("RandomEvent2", host.getUsername(), new Date(20500811), 1, false, new HashSet<String>(), new HashSet<String>());
        Event mockPendingEventThree = createEventParams("RandomEvent3", host.getUsername(), new Date(20500812), 1, false, new HashSet<String>(), new HashSet<String>());

        Event eventOne = eventService.createEvent(mockPendingEventOne);
        Event eventTwo = eventService.createEvent(mockPendingEventTwo);
        Event eventThree = eventService.createEvent(mockPendingEventThree);

        Guest userOneEventOne = createGuest(userOne.getUsername(), eventOne.getId());
        Guest userOneEventTwo = createGuest(userOne.getUsername(), eventTwo.getId());
        Guest userOneEventThree = createGuest(userOne.getUsername(), eventThree.getId());
        Guest userTwoEventOne = createGuest(userTwo.getUsername(), eventOne.getId());
        Guest userTwoEventTwo = createGuest(userTwo.getUsername(), eventTwo.getId());
        Guest userTwoEventThree = createGuest(userTwo.getUsername(), eventThree.getId());

        List<Guest> eventOneGuests = new ArrayList<Guest>();
        eventOneGuests.add(userOneEventOne);
        eventOneGuests.add(userTwoEventOne);

        List<Guest> eventTwoGuests = new ArrayList<Guest>();
        eventTwoGuests.add(userOneEventTwo);
        eventTwoGuests.add(userTwoEventTwo);

        List<Guest> eventThreeGuests = new ArrayList<Guest>();
        eventThreeGuests.add(userOneEventThree);
        eventThreeGuests.add(userTwoEventThree);

        eventOne = eventService.addInvitees(eventOneGuests);
        eventTwo = eventService.addInvitees(eventTwoGuests);
        eventThree = eventService.addInvitees(eventThreeGuests);

        userOne = userService.findByUsername(userOne);
        userTwo = userService.findByUsername(userTwo);

        List<String> expectedEventInvitees = new ArrayList<String>();
        expectedEventInvitees.add(userOne.getUsername());
        expectedEventInvitees.add(userTwo.getUsername());

        List<Integer> expectedUserPendingEvents = new ArrayList<Integer>();
        expectedUserPendingEvents.add(eventOne.getId());
        expectedUserPendingEvents.add(eventTwo.getId());
        expectedUserPendingEvents.add(eventThree.getId());

        List<User> actualEventOneInvitees = eventService.getInvitees(eventOne);
        List<User> actualEventTwoInvitees = eventService.getInvitees(eventTwo);
        List<User> actualEventThreeInvitees = eventService.getInvitees(eventThree);
        
        assertTrue(actualEventOneInvitees.size() == expectedEventInvitees.size());
        assertTrue(actualEventTwoInvitees.size() == expectedEventInvitees.size());
        assertTrue(actualEventThreeInvitees.size() == expectedEventInvitees.size());
        
        assertTrue(userOne.getEvents().isEmpty());
        assertTrue(userTwo.getEvents().isEmpty());

        assertTrue(userOne.getPendingEvents().size() == expectedUserPendingEvents.size());
        assertTrue(userTwo.getPendingEvents().size() == expectedUserPendingEvents.size());

        for (User invitee : actualEventOneInvitees) {
            assertTrue(expectedEventInvitees.contains(invitee.getUsername()));
        }

        for (int i = 0; i < expectedUserPendingEvents.size(); i++) {
            assertTrue(userOne.getPendingEvents().get(i) == expectedUserPendingEvents.get(i));
            assertTrue(userTwo.getPendingEvents().get(i) == expectedUserPendingEvents.get(i));
        }

        userService.acceptPendingEvent(userOneEventOne);
        userService.acceptPendingEvent(userOneEventTwo);
        userService.rejectPendingEvent(userOneEventThree);

        userService.rejectPendingEvent(userTwoEventOne);
        userService.acceptPendingEvent(userTwoEventTwo);
        userService.acceptPendingEvent(userTwoEventThree);

        userOne = userService.findByUsername(userOne);
        userTwo = userService.findByUsername(userTwo);

        eventOne = eventService.getEvent(eventOne);
        eventTwo = eventService.getEvent(eventTwo);
        eventThree = eventService.getEvent(eventThree);

        List<User> expectedEventOneConfirmedInvitees = new ArrayList<User>();
        expectedEventOneConfirmedInvitees.add(userOne);

        List<User> expectedEventTwoConfirmedInvitees = new ArrayList<User>();
        expectedEventTwoConfirmedInvitees.add(userOne);
        expectedEventTwoConfirmedInvitees.add(userTwo);

        List<User> expectedEventThreeConfirmedInvitees = new ArrayList<User>();
        expectedEventThreeConfirmedInvitees.add(userTwo);

        List<Integer> expectedUserOneAcceptedEvents = new ArrayList<Integer>();
        expectedUserOneAcceptedEvents.add(eventOne.getId());
        expectedUserOneAcceptedEvents.add(eventTwo.getId());

        List<Integer> expectedUserTwoAcceptedEvents = new ArrayList<Integer>();
        expectedUserTwoAcceptedEvents.add(eventTwo.getId());
        expectedUserTwoAcceptedEvents.add(eventThree.getId());
        
        List<User> actualEventOneConfirmedInvitees = eventService.getConfirmedInvitees(eventOne);
        List<User> actualEventTwoConfirmedInvitees = eventService.getConfirmedInvitees(eventTwo);
        List<User> actualEventThreeConfirmedInvitees = eventService.getConfirmedInvitees(eventThree);

        assertTrue(userOne.getPendingEvents().isEmpty());
        assertTrue(userTwo.getPendingEvents().isEmpty());

        assertTrue(userOne.getEvents().size() == expectedUserOneAcceptedEvents.size());
        assertTrue(userTwo.getEvents().size() == expectedUserTwoAcceptedEvents.size());

        assertTrue(eventOne.getConfirmedInvitees().size() == expectedEventOneConfirmedInvitees.size());
        assertTrue(eventTwo.getConfirmedInvitees().size() == expectedEventTwoConfirmedInvitees.size());
        assertTrue(eventThree.getConfirmedInvitees().size() == expectedEventThreeConfirmedInvitees.size());

        for (int i = 0; i < expectedUserOneAcceptedEvents.size(); i++) {
            assertTrue(userOne.getEvents().get(i) == expectedUserOneAcceptedEvents.get(i));
        }

        for (int i = 0; i < expectedUserTwoAcceptedEvents.size(); i++) {
            assertTrue(userTwo.getEvents().get(i) == expectedUserTwoAcceptedEvents.get(i));
        }

        for (User invitee : expectedEventOneConfirmedInvitees) {
            assertTrue(actualEventOneConfirmedInvitees.contains(invitee));
        }

        for (User invitee : expectedEventTwoConfirmedInvitees) {
            assertTrue(actualEventTwoConfirmedInvitees.contains(invitee));
        }

        for (User invitee : expectedEventThreeConfirmedInvitees) {
            assertTrue(actualEventThreeConfirmedInvitees.contains(invitee));
        }

    }
    
    @Test
    public void acceptPendingEventAsHost() {
    	try {
    		User mockUserParams = createUserParams(100, "oneManArmy", "mockPassword", "acceptself@validemail.com", new ArrayList<String>());
    		Event mockEventParams = createEventParams("Special Day", mockUserParams.getUsername(), new Date(20500810), 1, false, new HashSet<String>(), new HashSet<String>());
    		
    		User user = userService.createUser(mockUserParams);
    		Event event = eventService.createEvent(mockEventParams);
    		
    		Guest acceptParam = createGuest(user.getUsername(), event.getId());
    		userService.acceptPendingEvent(acceptParam);
    	} catch (RuntimeException re) {
    		assertTrue(re.getMessage().contains("is the host of the event and so cannot accept or reject an invite to this event"));
    	}
    }
    
    @Test
    public void rejectPendingEventAsHost() {
    	try {
    		User mockUserParams = createUserParams(100, "selfMockery", "mockPassword", "rejectself@validemail.com", new ArrayList<String>());
    		Event mockEventParams = createEventParams("Same", mockUserParams.getUsername(), new Date(20500810), 1, false, new HashSet<String>(), new HashSet<String>());
    		
    		User user = userService.createUser(mockUserParams);
    		Event event = eventService.createEvent(mockEventParams);
    		
    		Guest rejectParam = createGuest(user.getUsername(), event.getId());
    		userService.rejectPendingEvent(rejectParam);
    	} catch (RuntimeException re) {
    		assertTrue(re.getMessage().contains("is the host of the event and so cannot accept or reject an invite to this event"));
    	}
    }
    
    @Test
    public void confirmAutoAddingInviteesEventsAndCancellingEvent() {
    	User mockUserParamsOne = createUserParams(100, "inviteeGuy", "mockPassword", "inviteeguy@validemail.com", new ArrayList<String>());
    	User mockUserParamsTwo = createUserParams(100, "inviteeGal", "mockPassword", "inviteegal@validemail.com", new ArrayList<String>());
    	User mockHostParams = createUserParams(100, "someKindOfHost", "mockPassword", "somekindofhost@validemail.com", new ArrayList<String>());
    	
    	User userOne = userService.createUser(mockUserParamsOne);
    	User userTwo = userService.createUser(mockUserParamsTwo);
    	User host = userService.createUser(mockHostParams);
    	
    	Set<String> invitees = new HashSet<String>();
    	invitees.add(userOne.getUsername());
    	
    	Set<String> confirmedInvitees = new HashSet<String>();
    	confirmedInvitees.add(userTwo.getUsername());
    	
    	Event mockEventParams = createEventParams("Special Day", host.getUsername(), new Date(20500810), 1, false, invitees, confirmedInvitees);
    	Event event = eventService.createEvent(mockEventParams);
    	
    	userOne = userService.findByUsername(userOne);
    	userTwo = userService.findByUsername(userTwo);
    	host = userService.findByUsername(host);
    	
    	assertTrue(userOne.getPendingEvents().get(0) == event.getId());
    	assertTrue(userTwo.getEvents().get(0) == event.getId());
    	assertTrue(host.getEvents().get(0) == event.getId());
    	
    	eventService.cancelEvent(event);
    	
    	userOne = userService.findByUsername(userOne);
    	userTwo = userService.findByUsername(userTwo);
    	host = userService.findByUsername(host);
    	
    	assertTrue(userOne.getPendingEvents().isEmpty());
    	assertTrue(userTwo.getEvents().isEmpty());
    	assertTrue(host.getEvents().isEmpty());
    }
    
    @Test
    public void finalizeEventWithInvitees() {
    	User mockUserParamsOne = createUserParams(100, "firstInvitee", "mockPassword", "firstinvitee@validemail.com", new ArrayList<String>());
    	User mockUserParamsTwo = createUserParams(100, "secondInvitee", "mockPassword", "secondinvitee@validemail.com", new ArrayList<String>());
    	User mockHostParams = createUserParams(100, "theOnlyHost", "mockPassword", "theonlyhost@validemail.com", new ArrayList<String>());
    	
    	User userOne = userService.createUser(mockUserParamsOne);
    	User userTwo = userService.createUser(mockUserParamsTwo);
    	User host = userService.createUser(mockHostParams);
    	
    	Set<String> invitees = new HashSet<String>();
    	invitees.add(userOne.getUsername());
    	invitees.add(userTwo.getUsername());
    	
    	Event mockEventParams = createEventParams("Special Day", host.getUsername(), new Date(20500810), 1, false, invitees, new HashSet<String>());
    	Event event = eventService.createEvent(mockEventParams);
    	
    	eventService.finalizeEvent(event);
    	
    	event = eventService.getEvent(event);
    	userOne = userService.findByUsername(userOne);
    	userTwo = userService.findByUsername(userTwo);
    	host = userService.findByUsername(host);
    	
    	assertTrue(userOne.getPendingEvents().isEmpty());
    	assertTrue(userTwo.getPendingEvents().isEmpty());
    	assertTrue(userOne.getEvents().isEmpty());
    	assertTrue(userTwo.getEvents().isEmpty());
    	assertTrue(host.getEvents().contains(event.getId()));
    	assertTrue(event.getIsFinalized());
    	assertTrue(event.getInvitees().isEmpty());
    	assertTrue(event.getConfirmedInvitees().isEmpty());
    }
    
    @Test 
    public void findingTimeForEventWithUsersWithEvents() {
    	User mockUserParamsOne = createUserParams(100, "guyWithEvents", "mockPassword", "guywithevents@validemail.com", new ArrayList<String>());
    	User mockUserParamsTwo = createUserParams(100, "girlWithEvents", "mockPassword", "girlwithevents@validemail.com", new ArrayList<String>());
    	User mockUserParamsThree = createUserParams(100, "hostWithEvents", "mockPassword", "hostwithevents@validemail.com", new ArrayList<String>());
    	
    	User userOne = userService.createUser(mockUserParamsOne);
    	User userTwo = userService.createUser(mockUserParamsTwo);
    	User userThree = userService.createUser(mockUserParamsThree);
    	
    	HashSet<String> confirmedInvitees = new HashSet<String>();
    	confirmedInvitees.add(userTwo.getUsername());
    	confirmedInvitees.add(userThree.getUsername());
    	
    	// 12/10/2015, 8:00:00 AM
    	Event userOneEventParams = createEventParams("User One's Event", userOne.getUsername(), new Date(1449763200000L), 120, true, new HashSet<String>(), new HashSet<String>());
    	// 12/10/2015, 10:59:00 AM
    	Event userTwoEventParams = createEventParams("User Two's Event", userTwo.getUsername(), new Date(1449773940000L), 121, true, new HashSet<String>(), new HashSet<String>());
    	// 12/10/2015, 4:00:00 PM
    	Event userThreeEventParams = createEventParams("User Three's Event", userThree.getUsername(), new Date(1449792000000L), 120, true, new HashSet<String>(), new HashSet<String>());
    	// 12/10/2015, 12:00:00 AM
    	Event eventToFindTimeParams = createEventParams("The one and only", userOne.getUsername(), new Date(1449734400000L), 121, false, new HashSet<String>(), confirmedInvitees);
    	// 12/10/2015, 10:58:00 AM
    	Event eventToFindTimeTwoParams = createEventParams("The two and only", userOne.getUsername(), new Date(1449773880000L), 60, false, new HashSet<String>(), confirmedInvitees);
    	
    	eventService.createEvent(userOneEventParams);
    	eventService.createEvent(userTwoEventParams);
    	eventService.createEvent(userThreeEventParams);
    	
    	Event eventToFindTime = eventService.createEvent(eventToFindTimeParams);
    	Event eventToFindTimeTwo = eventService.createEvent(eventToFindTimeTwoParams);
    	
    	eventToFindTime = eventService.findTime(eventToFindTime);	
    	eventToFindTimeTwo = eventService.findTime(eventToFindTimeTwo);
    	
    	// 12/10/2015, 1:00:00 PM
    	assertTrue(eventToFindTime.getStartDate().equals(new Date(1449781200000L)));
    	// 12/10/2015, 3:01:00 PM
    	assertTrue(eventToFindTime.getEndDate().equals(new Date(1449788460000L)));
    	// 12/10/2015, 6:00:00 PM
    	assertTrue(eventToFindTimeTwo.getStartDate().equals(new Date(1449799200000L)));
    	// 12/10/2015, 7:00:00 PM
    	assertTrue(eventToFindTimeTwo.getEndDate().equals(new Date(1449802800000L)));
    }
    
    @Test
    public void findTimeForFinalizedEvent() {
    	User mockHostParams = createUserParams(100, "thatOneHost", "mockPassword", "thatonehost@validemail.com", new ArrayList<String>());
    	User host = userService.createUser(mockHostParams);
    	
    	Event eventParams = createEventParams("that one event", host.getUsername(), new Date(), 1, true, new HashSet<String>(), new HashSet<String>());
    	Event event = eventService.createEvent(eventParams);
    	
    	Event eventAfterFindTime = eventService.findTime(event);
    	
    	assertTrue(event.equals(eventAfterFindTime));
    }
    
    @Test
    public void findTimeForNonExistentEvent() {
    	try {
    		Event eventParams = createEventParams("WHERE'D I GO?!", "dudHost", new Date(), 1, true, new HashSet<String>(), new HashSet<String>());
    		eventParams.setId(9999999);
    		
    		eventService.findTime(eventParams);
    	} catch (RuntimeException re) {
    		assertTrue(re.getMessage().equals("Error: Event 9999999 could not be found!"));
    	}
    	
    }
    
    @Test
    public void addHostAsInvitee() {
    	try {
    		User mockHostParams = createUserParams(100, "inviteMyself", "mockPassword", "invitemyself@validemail.com", new ArrayList<String>());
    		User host = userService.createUser(mockHostParams);
    		
    		Event mockEventParams = createEventParams("Special Day", host.getUsername(), new Date(20500810), 1, false, new HashSet<String>(), new HashSet<String>());
        	Event event = eventService.createEvent(mockEventParams);
        	
        	Guest guest = createGuest(host.getUsername(), event.getId());
        	List<Guest> inviteeParam = new ArrayList<Guest>();
        	inviteeParam.add(guest);
        	
        	eventService.addInvitees(inviteeParam);
    	} catch (RuntimeException re) {
    		assertTrue(re.getMessage().contains("is the host of the event and so cannot accept or reject an invite to this event"));
    	}
    }
    
    @Test
    public void inviteeGuestsHaveDifferentEventIds() {
    	try {
    		User mockUserParams = createUserParams(100, "randomDummy", "mockPassword", "randomdummy@validemail.com", new ArrayList<String>());
    		User mockHostParams = createUserParams(100, "messyHost", "mockPassword", "messyhost@validemail.com", new ArrayList<String>());
    		
    		User host = userService.createUser(mockHostParams);
    		User user = userService.createUser(mockUserParams);
    		
    		Event mockEventParamsOne = createEventParams("Different Event #1", host.getUsername(), new Date(20500812), 1, false, new HashSet<String>(), new HashSet<String>());
    		Event mockEventParamsTwo = createEventParams("Different Event #2", host.getUsername(), new Date(20500811), 1, false, new HashSet<String>(), new HashSet<String>());
        	
    		Event eventOne = eventService.createEvent(mockEventParamsOne);
        	Event eventTwo = eventService.createEvent(mockEventParamsTwo);
        	
        	Guest guestOne = createGuest(user.getUsername(), eventOne.getId());
        	Guest guestTwo = createGuest(user.getUsername(), eventTwo.getId());
        	
        	List<Guest> inviteeParam = new ArrayList<Guest>();
        	inviteeParam.add(guestOne);
        	inviteeParam.add(guestTwo);
        	
        	eventService.addInvitees(inviteeParam);
    	} catch (RuntimeException re) {
    		assertTrue(re.getMessage().contains("Error: Guests must have the same event ID!"));
    	}
    }

    private User createUserParams(int id, String username, String password, String email, List<String> friends) {
        User mockUserParams = new User();
        // Do NOT change the id field of a User entity; id is a primary key automatically generated
        // when the user is inserted into a JPARepository
        //mockUserParams.setId(id);
        mockUserParams.setUsername(username);
        mockUserParams.setPassword(password);
        mockUserParams.setEmail(email);
        mockUserParams.setFriends(friends);

        return mockUserParams;
    }

    private Event createEventParams(String name, String host, Date startDate, int duration, boolean isFinalized, Set<String> invitees, Set<String> confirmedInvitees) {
        Event mockEventParams = new Event();
        mockEventParams.setName(name);
        mockEventParams.setHost(host);
        mockEventParams.setStartDate(startDate);
        mockEventParams.setDuration(duration);
        mockEventParams.setIsFinalized(isFinalized);
        mockEventParams.setInvitees(invitees);
        mockEventParams.setConfirmedInvitees(confirmedInvitees);
        return mockEventParams;
    }

    private Guest createGuest(String username, int eventId) {
        Guest guest = new Guest();
        guest.setUsername(username);
        guest.setEventId(eventId);
        return guest;
    }

}
