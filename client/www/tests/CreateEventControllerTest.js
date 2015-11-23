describe('TestCreateEventController', function() {

var $controller, $scope, UserDataService, API, CalendarSync;

beforeEach(module('App'));

beforeEach(inject(function(_$controller_, $rootScope, _UserDataService_, _API_, _CalendarSync_) {
    $scope = $rootScope.$new();
    UserDataService = _UserDataService_;
    API = _API_;
    CalendarSync = _CalendarSync_;
    spyOn(UserDataService, 'getUsername').and.returnValue("napon");
    $controller = _$controller_('CreateEventCtrl', {
        $scope: $scope,
        UserDataService: UserDataService,
        API: API,
        CalendarSync: CalendarSync
    });
}));

it('should have a valid controller scope', function() {
    expect($scope).toBeDefined();
}); 

it('should create an event by calling API post with correct params', function() {
    // Mock some stuff
    $scope.data = {
        'eventName': 'testEvent',
        'eventDescription': 'testDescription',
        'eventDate': new Date(1995, 11, 17),
        'guests': []
    };
    $scope.formLocation = 'testLocation';

    spyOn(API, 'post');
    $scope.createEvent();
    expect(UserDataService.getUsername).toHaveBeenCalled();
    expect(API.post).toHaveBeenCalledWith(
        'event/createEvent', 
        {
            'name': 'testEvent',
            'description': 'testDescription',
            'location': 'testLocation',
            'startDate': '1995-11-17',
            'host': 'napon',
            'invitees': [],
            'confirmedInvitees': [] 
        },
        jasmine.any(Function), jasmine.any(Function)
    );
});
});