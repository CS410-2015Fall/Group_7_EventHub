package ca.ubc.cs.cpsc410;

import ca.ubc.cs.cpsc410.data.*;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertTrue;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = WesyncApplication.class)
@WebAppConfiguration
public class UserServiceTests {

    @Autowired
    private UserRepository userRepository;
    private UserService service;
    @Autowired
    private EventRepository eventRepository;

    @Before
    public void setUp() throws Exception {
        service = new UserServiceImpl(userRepository, eventRepository);
    }

    @Test
    public void createSingleUserAndValidate() throws Exception {
        try {
            User mockUserParams = createUserParams(100, "mockUser", "mockPassword", "mock@mock.mock", new ArrayList<String>());

            User mockUser = service.createUser(mockUserParams);

            service.validateUser(mockUserParams);
            assertTrue(mockUser.getUsername().equals("mockUser"));
            assertTrue(mockUser.getPassword().equals("mockPassword"));
            assertTrue(mockUser.getEmail().equals("mock@mock.mock"));

            mockUserParams.setPassword("thewrongpassword");
            service.validateUser(mockUserParams);
        } catch (RuntimeException re) {
            assertTrue(re.getMessage().contains("Error: Password incorrect!"));
        }
    }

    @Test
    public void createUserWithUnavailableUsername() throws Exception {
        try {
            User mockUserParams = createUserParams(100, "mockUser1", "mockPassword", "mock1@mock.mock", new ArrayList<String>());

            User mockUser = service.createUser(mockUserParams);

            mockUserParams.setId(102);
            mockUserParams.setEmail("mockAgain@mock.mock");

            User mockUserClone = service.createUser(mockUserParams);

        } catch (RuntimeException re) {
            User mockUser = createUserParams(100, "mockUser1", "mockPassword", "mock1@mock.mock", new ArrayList<String>());
            assertTrue(re.getMessage().contains(String.format(
                    "Error: User %s already exists!", mockUser.getUsername())));
        }
    }

    @Test
    public void createUserWithEmptyUsername() throws Exception {
        try {
            User mockUserParams = createUserParams(100, "", "mockPassword", "emptyUser@mock.mock", new ArrayList<String>());

            User mockUser = service.createUser(mockUserParams);

        } catch (RuntimeException re) {
            assertTrue(re.getMessage().contains("User has an empty username"));
        }
    }

    @Test
    public void createUserWithEmptyEmail() throws Exception {
        try {
            User mockUserParams = createUserParams(100, "emptyEmail", "mockPassword", "", new ArrayList<String>());

            User mockUser = service.createUser(mockUserParams);

        } catch (RuntimeException re) {
            assertTrue(re.getMessage().contains("User has an empty email"));
        }
    }

    @Test
    public void createUserWithUnavailableEmail() throws Exception {
        try {
            User mockUserParams = createUserParams(100, "mockUser2", "mockPassword", "mock2@mock.mock", new ArrayList<String>());

            User mockUser = service.createUser(mockUserParams);

            mockUserParams.setId(104);
            mockUserParams.setUsername("mockUserRandom");

            User mockUserClone = service.createUser(mockUserParams);
        } catch (RuntimeException re) {
            assertTrue(re.getMessage().contains("Error: User with email"));
        }
    }

    @Test
    public void createUserWithInvalidEmail() throws Exception {
        try {
            User mockUserParams = createUserParams(100, "emailIsWrong", "mockPassword", "INVALIDEMAIL", new ArrayList<String>());

            User mockUser = service.createUser(mockUserParams);
        } catch (RuntimeException re) {
            assertTrue(re.getMessage().contains("Error: Email invalid!"));
        }
    }

    @Test
    public void addFriendIncorrectParameter() throws Exception {
        try {
            List<User> incorrectParam = new ArrayList<User>();

            User mockUserParams = createUserParams(100, "mockUser3", "mockPassword", "mock3@mock.mock", new ArrayList<String>());

            User mockUser = service.createUser(mockUserParams);

            incorrectParam.add(mockUser);

            service.addFriend(incorrectParam);

        } catch (RuntimeException re) {
            assertTrue(re.getMessage().contains("API expects a list of 2 users, where the 1st user is the user to modify and the 2nd user is the user to add as a friend"));
        }
    }

    @Test
    public void removeFriendIncorrectParameter() throws Exception {
        try {
            List<User> incorrectParam = new ArrayList<User>();

            User mockUserParams = createUserParams(100, "mockUser4", "mockPassword", "mock4@mock.mock", new ArrayList<String>());

            User mockUser = service.createUser(mockUserParams);

            incorrectParam.add(mockUser);

            service.removeFriend(incorrectParam);

        } catch (RuntimeException re) {
            assertTrue(re.getMessage().contains("API expects a list of 2 users, where the 1st user is the user to modify and the 2nd user is the user to remove as a friend"));
        }
    }

    @Test
    public void addFriendAndAddAlreadyExistingFriend() throws Exception {
        try {
            List<User> addFriendParam = new ArrayList<User>();

            User mockUserParams = createUserParams(100, "mockUser5", "mockPassword", "mock5@mock.mock", new ArrayList<String>());
            User mockFriendParams = createUserParams(101, "friend", "mockPassword", "friend@mock.mock", new ArrayList<String>());

            User mockUser = service.createUser(mockUserParams);
            User friendUser = service.createUser(mockFriendParams);

            addFriendParam.add(mockUser);
            addFriendParam.add(friendUser);

            service.addFriend(addFriendParam);

            mockUser = service.findByEmail(mockUserParams);

            assertTrue(!mockUser.getFriends().isEmpty());
            assertTrue(mockUser.getFriends().get(0).equals("friend"));

            service.addFriend(addFriendParam);
        } catch (RuntimeException re) {
            assertTrue(re.getMessage().contains("is already a friend of user"));
        }

    }

    @Test
    public void removeNonExistingFriend() throws Exception {
        try {
            List<User> removeFriendParam = new ArrayList<User>();

            User mockUserParams = createUserParams(100, "mockUser6", "mockPassword", "mock6@mock.mock", new ArrayList<String>());
            User mockFriendParams = createUserParams(101, "friend2", "mockPassword", "friend2@mock.mock", new ArrayList<String>());

            User mockUser = service.createUser(mockUserParams);
            User friendUser = service.createUser(mockFriendParams);

            removeFriendParam.add(mockUser);
            removeFriendParam.add(friendUser);

            service.removeFriend(removeFriendParam);
        } catch (RuntimeException re) {
            assertTrue(re.getMessage().contains("is not a friend of user"));
        }
    }

    @Test
    public void addAndRemoveFriends() throws Exception {
        try {
            List<User> friendParam = new ArrayList<User>();
            List<String> friendsList = new ArrayList<String>();
            List<User> addSelfParam = new ArrayList<User>();

            User mockUserParams = createUserParams(10, "mockUser7", "mockPassword", "mock7@mock.mock", friendsList);
            User mockFriendParams = createUserParams(11, "friend3", "mockPassword", "friend3@mock.mock", friendsList);
            User mockOtherFriendParams = createUserParams(12, "friend4", "mockPassword", "friend4@mock.mock", friendsList);

            User mockUser = service.createUser(mockUserParams);
            User friendUser = service.createUser(mockFriendParams);
            User otherFriendUser = service.createUser(mockOtherFriendParams);

            friendParam.add(mockUser);
            friendParam.add(friendUser);

            service.addFriend(friendParam);

            friendParam.remove(friendUser);
            friendParam.add(otherFriendUser);

            service.addFriend(friendParam);

            mockUser = service.findByUsername(mockUserParams);

            assertTrue(mockUser.getFriends().size() == 2);
            assertTrue(mockUser.getFriends().get(0).equals("friend3"));
            assertTrue(mockUser.getFriends().get(1).equals("friend4"));

            friendParam.remove(otherFriendUser);
            friendParam.add(friendUser);

            service.removeFriend(friendParam);

            mockUser = service.findByUsername(mockUserParams);

            assertTrue(mockUser.getFriends().size() == 1);
            assertTrue(mockUser.getFriends().get(0).equals("friend4"));

            addSelfParam.add(mockUser);
            addSelfParam.add(mockUser);

            service.addFriend(addSelfParam);
        } catch (RuntimeException re) {
            assertTrue(re.getMessage().contains("Error: Cannot add yourself as a friend!"));
        }

    }

    @Test
    public void addNonExistingUser() {
        try {
            List<User> removeFriendParam = new ArrayList<User>();

            User mockUserParams = createUserParams(100, "mockUser8", "mockPassword", "mock8@mock.mock", new ArrayList<String>());
            User nonExistingUser = createUserParams(101, "notAUser", "mockPassword", "notAUser@mock.mock", new ArrayList<String>());

            User mockUser = service.createUser(mockUserParams);

            removeFriendParam.add(mockUser);
            removeFriendParam.add(nonExistingUser);

            service.addFriend(removeFriendParam);
        } catch (RuntimeException re) {
            assertTrue(re.getMessage().contains("does not exist!"));
        }
    }

    @Test
    public void removeNonExistingUser() {
        try {
            List<User> removeFriendParam = new ArrayList<User>();

            User mockUserParams = createUserParams(102, "mockUser9", "mockPassword", "mock9@mock.mock", new ArrayList<String>());
            User nonExistingUser = createUserParams(103, "nonFriend", "mockPassword", "nonFriend@mock.mock", new ArrayList<String>());

            User mockUser = service.createUser(mockUserParams);

            removeFriendParam.add(mockUser);
            removeFriendParam.add(nonExistingUser);

            service.removeFriend(removeFriendParam);
        } catch (RuntimeException re) {
            assertTrue(re.getMessage().contains("does not exist!"));
        }
    }

    @Test
    public void verifyAllFriends() {
        List<User> addFriendOneParam = new ArrayList<User>();
        List<User> addFriendTwoParam = new ArrayList<User>();
        List<User> addFriendThreeParam = new ArrayList<User>();
        List<User> expectedFriendsList = new ArrayList<User>();
        User friendOneParams = createUserParams(100, "theSidekick", "mockPassword", "sidekick@validemail.com", new ArrayList<String>());
        User friendTwoParams = createUserParams(100, "theLackey", "mockPassword", "lackey@validemail.com", new ArrayList<String>());
        User friendThreeParams = createUserParams(100, "theAntagonist", "mockPassword", "antagonist@validemail.com", new ArrayList<String>());
        User mockUserParams = createUserParams(100, "theMainProtagonist", "mockPassword", "protagonist@validemail.com", new ArrayList<String>());

        User friendOne = service.createUser(friendOneParams);
        User friendTwo = service.createUser(friendTwoParams);
        User friendThree = service.createUser(friendThreeParams);
        User mockUser = service.createUser(mockUserParams);

        addFriendOneParam.add(mockUser);
        addFriendOneParam.add(friendOne);
        service.addFriend(addFriendOneParam);

        mockUser = service.findByUsername(mockUserParams);

        addFriendTwoParam.add(mockUser);
        addFriendTwoParam.add(friendTwo);
        service.addFriend(addFriendTwoParam);

        mockUser = service.findByUsername(mockUserParams);

        addFriendThreeParam.add(mockUser);
        addFriendThreeParam.add(friendThree);
        service.addFriend(addFriendThreeParam);

        expectedFriendsList.add(friendOne);
        expectedFriendsList.add(friendTwo);
        expectedFriendsList.add(friendThree);

        List<User> actualFriendsList = service.getAllFriends(mockUser);

        assertTrue(actualFriendsList.size() == expectedFriendsList.size());

        for (int i = 0; i < actualFriendsList.size(); i++) {
            assertTrue(actualFriendsList.get(i).getUsername().equals(expectedFriendsList.get(i).getUsername()));
            assertTrue(actualFriendsList.get(i).getPassword().equals(expectedFriendsList.get(i).getPassword()));
            assertTrue(actualFriendsList.get(i).getEmail().equals(expectedFriendsList.get(i).getEmail()));
        }
    }
    
    @Test
    public void acceptNonexistantPendingEvent() {
    	try {
    		User mockUserParams = createUserParams(100, "theLoner", "mockPassword", "acceptnothing@validemail.com", new ArrayList<String>());
    		User user = service.createUser(mockUserParams);
    		
    		Guest acceptParam = createGuest(user.getUsername(), 9999999);
    		service.acceptPendingEvent(acceptParam);
    	} catch (RuntimeException re) {
    		assertTrue(re.getMessage().contains(String.format("Error: Event id %d does not exist!", 9999999)));
    	}
    }
    
    @Test
    public void rejectNonexistantPendingEvent() {
    	try {
    		User mockUserParams = createUserParams(100, "theSavage", "mockPassword", "rejectnothing@validemail.com", new ArrayList<String>());
    		User user = service.createUser(mockUserParams);
    		
    		Guest rejectParam = createGuest(user.getUsername(), 9999999);
    		service.rejectPendingEvent(rejectParam);
    	} catch (RuntimeException re) {
    		assertTrue(re.getMessage().contains(String.format("Error: Event id %d does not exist!", 9999999)));
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
    
    private Guest createGuest(String username, int eventId) {
        Guest guest = new Guest();
        guest.setUsername(username);
        guest.setEventId(eventId);
        return guest;
    }

}
