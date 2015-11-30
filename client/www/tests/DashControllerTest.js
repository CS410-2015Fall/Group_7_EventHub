describe('TestDashCtrl', function() {

var $controller, $scope, API, ionicPopup;

beforeEach(module('App'));

beforeEach(inject(function(_$controller_, $rootScope, _UserDataService_, _API_, _CalendarSync_, _$ionicPopup_) {
    $scope = $rootScope.$new();
    UserDataService = _UserDataService_;
    API = _API_;
    CalendarSync = _CalendarSync_;
    ionicPopup = _$ionicPopup_;
    spyOn(UserDataService, 'getUsername').and.returnValue("napon");
    $controller = _$controller_('DashCtrl', {
        $scope: $scope,
        ionicPopup: ionicPopup,
        UserDataService: UserDataService,
        CalendarSync: CalendarSync,
        API: API
    });
}));

it('should have a valid controller scope', function() {
    expect($scope).toBeDefined();
});

// it('presentOption should call finalize event if user presses agree', function() {
//     var mockTempEvent = {
//         'name': 'testEvent',
//         'description': 'testDescription',
//         'location': 'testLocation',
//         'startDate': new Date(10, 10, 10),
//         'endDate': new Date(10, 10, 10),
//         'duration': 60,
//         'host': 'napon',
//         'invitees': [],
//         'confirmedInvitees': [] 
//     };
//     spyOn(ionicPopup, 'confirm').and.returnValue(true);
//     $scope.presentOption(tempEvent);
// });

});