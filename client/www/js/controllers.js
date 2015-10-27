var App = angular.module('App');

App.controller('AppCtrl', function($scope, $state, $ionicPopup, AuthService, AUTH_EVENTS) {
  $scope.username = AuthService.username();

  $scope.$on(AUTH_EVENTS.notAuthenticated, function(event) {
    AuthService.logout();
    $state.go('login');
    var alertPopup = $ionicPopup.alert({
      title: 'Session Lost!',
      template: 'Sorry, You have to login again.'
    });
  });

  $scope.setCurrentUsername = function(name) {
    $scope.username = name;
  };
});

App.controller('LoginCtrl', function($scope, $state, $ionicPopup, AuthService) {
  $scope.data = {};

  $scope.login = function(data) {
    AuthService.login(data.username, data.password).then(function(authenticated) {
      $state.go('main.dash', {}, {reload: true});
      $scope.setCurrentUsername(data.username);
    }, function(err) {
      var alertPopup = $ionicPopup.alert({
        title: 'Login failed!',
        template: 'Please check your credentials!'
      });
    });
  };
});

App.controller('RegisterCtrl', function($scope, $state, $http, $ionicPopup) {
  $scope.register = function(data) {
    $http.defaults.headers.post['Content-Type'] = 'application/json;charset=UTF-8';
      $http({
        method: 'POST',
        url: 'http://vcheng.org:8080/user/createUser',
        data: {'username': data.username, 'password': data.password, 'email': data.email}
      })
      .then(function(response) {
        window.history.back();
        $ionicPopup.alert({
          title: 'Success',
          template: 'Please login with your username and password.'
        });
      }, function(response) {
        $ionicPopup.alert({
          title: 'Error',
          template: 'Problem creating a user. Please Try again later.'
        });
      });
  };

  $scope.goBack = function() {
    window.history.back();
  };
});

App.controller('DashCtrl', function($scope, $state, $http, $ionicPopup, AuthService) {
  $scope.logout = function() {
    AuthService.logout();
    $state.go('login');
  };

  $scope.refresh = function() {
  	$ionicPopup.alert({
  		title: 'Refresh',
  		template: 'Not yet implemented! ;)'
  	});
  };
});

App.controller('FriendsController', function($scope, $state, $http, $ionicPopup) {
  
  $scope.friends = [
    {
      'first': 'John',
      'last': 'Doe'
    },
    {
      'first': 'Vincent',
      'last': 'Cheng'
    }
  ];

  // $http.get('').success(function(data) {
  //   friendsController.list = data;
  // });
});