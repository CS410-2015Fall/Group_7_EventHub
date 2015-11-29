package ca.ubc.cs.cpsc410;

import ca.ubc.cs.cpsc410.data.*;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
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

        assertTrue(eventOne.getInvitees().size() == expectedEventInvitees.size());
        assertTrue(eventTwo.getInvitees().size() == expectedEventInvitees.size());
        assertTrue(eventThree.getInvitees().size() == expectedEventInvitees.size());
        
        assertTrue(userOne.getEvents().isEmpty());
        assertTrue(userTwo.getEvents().isEmpty());

        assertTrue(userOne.getPendingEvents().size() == expectedUserPendingEvents.size());
        assertTrue(userTwo.getPendingEvents().size() == expectedUserPendingEvents.size());

        for (String invitee : eventOne.getInvitees()) {
            assertTrue(expectedEventInvitees.contains(invitee));
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

        List<String> expectedEventOneConfirmedInvitees = new ArrayList<String>();
        expectedEventOneConfirmedInvitees.add(userOne.getUsername());

        List<String> expectedEventTwoConfirmedInvitees = new ArrayList<String>();
        expectedEventTwoConfirmedInvitees.add(userOne.getUsername());
        expectedEventTwoConfirmedInvitees.add(userTwo.getUsername());

        List<String> expectedEventThreeConfirmedInvitees = new ArrayList<String>();
        expectedEventThreeConfirmedInvitees.add(userTwo.getUsername());

        List<Integer> expectedUserOneAcceptedEvents = new ArrayList<Integer>();
        expectedUserOneAcceptedEvents.add(eventOne.getId());
        expectedUserOneAcceptedEvents.add(eventTwo.getId());

        List<Integer> expectedUserTwoAcceptedEvents = new ArrayList<Integer>();
        expectedUserTwoAcceptedEvents.add(eventTwo.getId());
        expectedUserTwoAcceptedEvents.add(eventThree.getId());

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

        for (String invitee : expectedEventOneConfirmedInvitees) {
            assertTrue(eventOne.getConfirmedInvitees().contains(invitee));
        }

        for (String invitee : expectedEventTwoConfirmedInvitees) {
            assertTrue(eventTwo.getConfirmedInvitees().contains(invitee));
        }

        for (String invitee : expectedEventThreeConfirmedInvitees) {
            assertTrue(eventThree.getConfirmedInvitees().contains(invitee));
        }

    }
    
    @Test
    public void acceptNonexistantPendingEvent() {
    	try {
    		User mockUserParams = createUserParams(100, "theLoner", "mockPassword", "acceptnothing@validemail.com", new ArrayList<String>());
    		User user = userService.createUser(mockUserParams);
    		
    		Guest acceptParam = createGuest(user.getUsername(), 9999999);
    		userService.acceptPendingEvent(acceptParam);
    	} catch (RuntimeException re) {
    		assertTrue(re.getMessage().contains(String.format("Error: Event id %d does not exist!", 9999999)));
    	}
    }
    
    @Test
    public void rejectNonexistantPendingEvent() {
    	try {
    		User mockUserParams = createUserParams(100, "theSavage", "mockPassword", "rejectnothing@validemail.com", new ArrayList<String>());
    		User user = userService.createUser(mockUserParams);
    		
    		Guest rejectParam = createGuest(user.getUsername(), 9999999);
    		userService.rejectPendingEvent(rejectParam);
    	} catch (RuntimeException re) {
    		assertTrue(re.getMessage().contains(String.format("Error: Event id %d does not exist!", 9999999)));
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
