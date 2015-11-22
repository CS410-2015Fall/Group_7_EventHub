
describe("CreateEventCtrl Unit Tests", function () {

    var $scope, ctrl, $timeout, $timeout, $http; //, $location;

    beforeEach(function () {
        module('App');

        inject(function ($rootScope, $controller, $q, _$timeout_) {
            $scope = $rootScope.$new();
            $timeout = _$timeout_;

            ctrl = $controller('CreateEventCtrl', {
                $scope: $scope
            });
        });
    });

    it("should have a $scope variable", function() {
        expect($scope).toBeDefined();
    });

    it("Modal should be loaded", function() {
        $scope.loadMapPicker(function(m) {
            expect(false).toBe(true);
        });
    });
});