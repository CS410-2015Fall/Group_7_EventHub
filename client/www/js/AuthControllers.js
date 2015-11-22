var App = angular.module('App');

App.controller('AppCtrl', function($scope, $state, $ionicPopup, AuthService, AUTH_EVENTS, UserDataService) {
  $scope.username = AuthService.username();

  $scope.$on(AUTH_EVENTS.notAuthenticated, function(event) {
    AuthService.logout();
    $state.go('login');
    var alertPopup = $ionicPopup.alert({
      title: 'Session Lost!',
      template: 'Sorry, You have to login again.'
    });
  });
});

App.controller('LoginCtrl', function($scope, $state, $ionicPopup, AuthService, UserDataService) {
  $scope.data = {};

  $scope.login = function(data) {
    AuthService.login(data.username, data.password).then(
      function(authenticated) {
        UserDataService.setUser(authenticated.data);
        UserDataService.refresh();
        if (ionic.Platform.isAndroid()) {
          UserDataService.syncExternalCalendars(authenticated);
        }
        $state.go('main.dash', {}, {reload: true});
      }, function(err) {
        var alertPopup = $ionicPopup.alert({
          title: 'Login failed!',
          template: 'Please check your credentials!'
        });
      }
    );
  };
});

App.controller('RegisterCtrl', function($scope, $state, $http, $ionicPopup, API) {
  $scope.register = function(data) {
    var request = {'username': data.username, 'password': data.password, 'email': data.email};
    API.post('user/createUser', request, 
      function(response) {
        window.history.back();
        $ionicPopup.alert({
          title: 'Success',
          template: 'Please login with your username and password.'
        });
      }, 
      function(response) {
        $ionicPopup.alert({
          title: 'Error',
          template: 'Problem creating a user. Please Try again later.'
        });
      }
    );
  };

  $scope.goBack = function() {
    window.history.back();
  };
});
