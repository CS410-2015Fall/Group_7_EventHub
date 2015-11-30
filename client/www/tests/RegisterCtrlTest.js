describe('TestRegisterCtrl', function() {

var $controller, $scope, API, $state, httpBackend;

beforeEach(module('App'));

beforeEach(inject(function(_$controller_, $rootScope, $httpBackend, _API_) {
    $scope = $rootScope.$new();
    API = _API_;
    httpBackend = $httpBackend;
    $controller = _$controller_('RegisterCtrl', {
        $scope: $scope,
        $http: httpBackend,
        API: API,
    });
}));

it('should have a valid controller scope', function() {
    expect($scope).toBeDefined();
}); 

it('should make the correct API POST call with correct parameters', function() {
    // Mock some stuff
    var mockData = {
        'username': 'napon',
        'password': 'qwerty',
        'email': 'abc@def.com'
    };

    spyOn(API, 'post');
    $scope.register(mockData);
    expect(API.post).toHaveBeenCalledWith(
        'user/createUser', 
        {
            'username': 'napon',
            'password': 'qwerty',
            'email': 'abc@def.com',
        },
        jasmine.any(Function), jasmine.any(Function)
    );
});
});