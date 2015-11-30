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

it('presentOption should show a popup', inject( function($q) {
    var mockTempEvent = {
        'name': 'testEvent',
        'description': 'testDescription',
        'location': 'testLocation',
        'startDate': new Date(10, 10, 10),
        'endDate': new Date(10, 10, 10),
        'duration': 60,
        'host': 'napon',
        'invitees': [],
        'confirmedInvitees': [] 
    };

    var returnedPromise = $q.defer();
    spyOn(UserDataService, 'finalizeEvent');
    spyOn(ionicPopup, 'confirm').and.returnValue(returnedPromise.promise);
    $scope.presentOption(mockTempEvent);
    expect(ionicPopup.confirm).toHaveBeenCalled();
}));

it('should load MapPicker with a valid modal', inject( function($q, $ionicModal) {
    var mockLocation = "123.2, 321.2";

    var returnedPromise = $q.defer();
    returnedPromise.promise.then(function (value) {
        expect(value).toBeDefined();
        expect($scope.modal).toBeDefined();
        expect($scope.modal.show).toHaveBeenCalled();
    });
    spyOn($ionicModal, 'fromTemplateUrl').and.returnValue(returnedPromise.promise);
    $scope.loadMapPicker(mockLocation);
    returnedPromise.resolve();
}));

it('should close map by removing modal', inject( function($q, $ionicModal) {
    var mockLocation = "123.2, 321.2";

    var returnedPromise = $q.defer();
    returnedPromise.promise.then(function (value) {
        $scope.closeModal();
        expect($scope.modal).toBeDefined();
        expect($scope.modal.remove).toHaveBeenCalled();        
    });
    spyOn($ionicModal, 'fromTemplateUrl').and.returnValue(returnedPromise.promise);
    $scope.loadMapPicker(mockLocation);
    returnedPromise.resolve();
}));

it('should call findTime and make API call with the correct path', function() {
    spyOn(API, 'post');
    $scope.findTime(13);
    expect(API.post).toBeDefined();
    expect(API.post).toHaveBeenCalledWith('event/findTime', jasmine.any(Object), jasmine.any(Function), jasmine.any(Function));
});

it('should call declineInvite by calling UserDataService with correct parameter', function() {
    var mockTempEvent = {
        'id': 10,
        'name': 'testEvent',
        'description': 'testDescription',
        'location': 'testLocation',
        'startDate': new Date(10, 10, 10),
        'endDate': new Date(10, 10, 10),
        'duration': 60,
        'host': 'napon',
        'invitees': [],
        'confirmedInvitees': [] 
    };

    spyOn(UserDataService, 'declineInvite');
    $scope.declineInvite(mockTempEvent);
    expect(UserDataService.declineInvite).toBeDefined();
    expect(UserDataService.declineInvite).toHaveBeenCalledWith(10);
});

it('should call acceptInvite by calling UserDataService with correct parameter', function() {
    var mockTempEvent = {
        'id': 10,
        'name': 'testEvent',
        'description': 'testDescription',
        'location': 'testLocation',
        'startDate': new Date(10, 10, 10),
        'endDate': new Date(10, 10, 10),
        'duration': 60,
        'host': 'napon',
        'invitees': [],
        'confirmedInvitees': [] 
    };

    spyOn(UserDataService, 'acceptInvite');
    $scope.acceptInvite(mockTempEvent);
    expect(UserDataService.acceptInvite).toBeDefined();
    expect(UserDataService.acceptInvite).toHaveBeenCalledWith(10);
});

it('should call refresh by calling UserDataService with correct parameter', function() {
    spyOn(UserDataService, 'refresh');
    $scope.refresh();
    expect(UserDataService.refresh).toBeDefined();
    expect(UserDataService.refresh).toHaveBeenCalled();
});

});