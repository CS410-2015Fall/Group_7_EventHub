describe('TestFriendsController', function() {

var $controller, $scope, UserDataService, API;

beforeEach(module('App'));

beforeEach(inject(function(_$controller_, $rootScope, _UserDataService_, _API_) {
    $scope = $rootScope.$new();
    UserDataService = _UserDataService_;
    API = _API_;
    spyOn(UserDataService, 'getFriends').and.returnValue([{"name": "bob"}, {"name": "vincent"}]);
    spyOn(UserDataService, 'getUsername').and.returnValue("napon");
    $controller = _$controller_('FriendsController', {
        $scope: $scope,
        UserDataService: UserDataService,
        API: API
    });
}));

it('should set $scope.model.friends to list of friends when UserDataService.getFriends is called', function() {
    expect(UserDataService.getFriends).toHaveBeenCalled();
    expect($scope.model.friends).toEqual([{"name": "bob"}, {"name": "vincent"}]);
}); 

it('should call API.post and UserDataService.getUsername to add friend', function() {
    spyOn(API, 'post');
    $scope.addFriend('dummy');
    expect(UserDataService.getUsername).toHaveBeenCalled();
    expect(API.post).toHaveBeenCalledWith('user/addFriend', 
        [{ username: 'napon' }, { username: 'dummy' }],
        jasmine.any(Function), jasmine.any(Function));
}); 

it('should call API.post and UserDataService.getUsername to remove friend', function() {
    spyOn(API, 'post');
    $scope.removeFriend('dummy');
    expect(UserDataService.getUsername).toHaveBeenCalled();
    expect(API.post).toHaveBeenCalledWith('user/removeFriend', 
        [{ username: 'napon' }, { username: 'dummy' }],
        jasmine.any(Function), jasmine.any(Function));
});
});