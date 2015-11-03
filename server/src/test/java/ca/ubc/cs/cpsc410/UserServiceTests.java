package ca.ubc.cs.cpsc410;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;

import java.util.List;
import java.util.ArrayList;

import static org.mockito.Mockito.*;

import ca.ubc.cs.cpsc410.data.EventRepository;
import ca.ubc.cs.cpsc410.data.User;
import ca.ubc.cs.cpsc410.data.UserRepository;
import ca.ubc.cs.cpsc410.data.UserService;
import ca.ubc.cs.cpsc410.data.UserServiceImpl;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = WesyncApplication.class)
@WebAppConfiguration
public class UserServiceTests {

	@Autowired
	private UserRepository userRepository;
	private UserService service;
	private EventRepository eventRepository;
	
	@Before
	public void setUp() throws Exception {
        service = new UserServiceImpl(userRepository, eventRepository);
    }
	
	@Test
	public void createSingleUserAndValidate() throws Exception{
		User mockUserParams = createUserParams(100, "mockUser", "mockPassword", "mock@mock.mock", new ArrayList<String>());
		
		User mockUser = service.createUser(mockUserParams);
		
		service.validateUser(mockUser);
		assertTrue(mockUser.getUsername().equals("mockUser"));
		assertTrue(mockUser.getPassword().equals("mockPassword"));
		assertTrue(mockUser.getEmail().equals("mock@mock.mock"));
	}
	
	@Test 
	public void createUserWithUnavailableUsername() throws Exception{
		try {
			User mockUserParams = createUserParams(100, "mockUser1", "mockPassword", "mock1@mock.mock", new ArrayList<String>());
		
				User mockUser = service.createUser(mockUserParams);
				
				mockUserParams.setId(102);
				mockUserParams.setEmail("mock2@mock.mock");
				
				User mockUserClone = service.createUser(mockUserParams);
			} catch (RuntimeException re) {
				assertTrue(re.getMessage().contains("Error: User") && re.getMessage().contains("already exists!"));
			}
	}
	
	@Test
	public void createUserWithUnavailableEmail() throws Exception{
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
	public void addFriendIncorrectParameter() throws Exception{
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
	public void removeFriendIncorrectParameter() throws Exception{
		try {
			List<User> incorrectParam = new ArrayList<User>();
			
			User mockUserParams = createUserParams(100, "mockUser4", "mockPassword", "mock4@mock.mock", new ArrayList<String>());
	
			User mockUser = service.createUser(mockUserParams);
			
			incorrectParam.add(mockUser);
			
			service.addFriend(incorrectParam);
			
		} catch (RuntimeException re) {
			assertTrue(re.getMessage().contains("API expects a list of 2 users, where the 1st user is the user to modify and the 2nd user is the user to add as a friend"));
		}
	}
	
	@Test 
	public void addFriendAndAddAlreadyExistingFriend() throws Exception{
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
		} catch(RuntimeException re) {
			assertTrue(re.getMessage().contains("is already a friend of user"));
		}
		
	}
	
	@Test 
	public void removeNonExistingFriend() throws Exception{
		try {
			List<User> removeFriendParam = new ArrayList<User>();
			
			User mockUserParams = createUserParams(100, "mockUser6", "mockPassword", "mock6@mock.mock", new ArrayList<String>());
			User mockFriendParams = createUserParams(101, "friend2", "mockPassword", "friend2@mock.mock", new ArrayList<String>());
			
			User mockUser = service.createUser(mockUserParams);
			User friendUser = service.createUser(mockFriendParams);
			
			removeFriendParam.add(mockUser);
			removeFriendParam.add(friendUser);
			
			service.removeFriend(removeFriendParam);
		} catch(RuntimeException re) {
			assertTrue(re.getMessage().contains("is not a friend of user"));
		}
	}
	
	@Test
	public void addAndRemoveFriends() throws Exception{
		List<User> friendParam = new ArrayList<User>();
		List<String> friendsList = new ArrayList<String>();
		
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
