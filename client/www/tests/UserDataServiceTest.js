describe('TestUserDataService', function() {

var userDataService, mockAPI;

beforeEach(function(){
  module('App');
});

beforeEach(inject(function(API, UserDataService){
  mockAPI = API;
  userDataService = UserDataService;
  userDataService.setUser({
    username: 'napon',
    password: 'napon',
    email: 'foo@bar.biz',
    facebookToken: '12Fd40V',
    googleToken: 'N/A',
    friends: ['vincent', 'ryan', 'jeff'],
    events: [10, 11, 12],
    pendingEvents: [20, 21, 22]
  });
}));

it('should have a valid userDataService', function() {
  expect(userDataService).toBeDefined();
});

it('should return the right username', function() {
  var testUsername = userDataService.getUsername();
  expect(testUsername).toEqual('napon');
});

it('should return the right validity of FB token', function() {
  var fbToken = userDataService.isFacebookLinked();
  expect(fbToken).toEqual(true);
});

it('should return the right validity of Google token', function() {
  var gToken = userDataService.isGoogleLinked();
  expect(gToken).toEqual(false);
});

it('should return the right friend list', function() {
  var testFriends = userDataService.getFriends();
  expect(testFriends).toEqual(['vincent', 'ryan', 'jeff']);
});

it('should acceptInvite by making correct API endpoint call', function() {
  spyOn(mockAPI, 'post');
  userDataService.acceptInvite(20);
  expect(mockAPI.post).toHaveBeenCalledWith(
    'user/acceptPendingEvent',
   {eventId: 20, username: 'napon'}, 
   jasmine.any(Function), 
   jasmine.any(Function));
});

it('should declineInvite by making correct API endpoint call', function() {
  spyOn(mockAPI, 'post');
  userDataService.declineInvite(21);
  expect(mockAPI.post).toHaveBeenCalledWith(
    'user/rejectPendingEvent',
   {eventId: 21, username: 'napon'}, 
   jasmine.any(Function), 
   jasmine.any(Function));
});

});