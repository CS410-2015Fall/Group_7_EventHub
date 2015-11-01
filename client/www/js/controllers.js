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

App.controller('LoginCtrl', function($scope, $state, $ionicPopup, AuthService) {
  $scope.data = {};

  $scope.login = function(data) {
    AuthService.login(data.username, data.password).then(function(authenticated) {
      $state.go('main.dash', {}, {reload: true});
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

App.controller('CreateEventCtrl', function($scope, $http, $ionicPopup, UserDataService, AuthService) {
  $scope.friends = UserDataService.getCurrentFriends();
  $scope.data = {};
  $scope.goBack = function() {
    window.history.back();
  };

  $scope.createEvent = function() {
    var data = this.data;
    var request = {
      'name': data.eventName,
      'description': data.eventDescription,
      'location': data.eventLocation,
      'startDate': (1900 + data.eventDate.getYear()) + '-' + data.eventDate.getMonth() + '-' + data.eventDate.getDate(),
      'host': AuthService.username(),
      'invitees': data.guests
    };

    $http.defaults.headers.post['Content-Type'] = 'application/json;charset=UTF-8';
    $http({
      method: 'POST',
      url: 'http://vcheng.org:8080/event/createEvent',
      data: request
    })
    .then(function(response) {
      window.history.back();
      $ionicPopup.alert({
        title: 'Success',
        template: 'Event created!'
      });
    }, function(response) {
      $ionicPopup.alert({
        title: 'Error',
        template: 'Please try again later.'
      });
    });
  };
});

App.controller('DashCtrl', function($scope, $state, $http, $ionicPopup, AuthService, UserDataService) {
  $scope.logout = function() {
    AuthService.logout();
    $state.go('login');
  };

  $scope.refresh = function() {
  	UserDataService.refresh();
  };

  $scope.createEvent = function() {
    $state.go('create');
  };
});

App.controller('FriendsController', function($scope, UserDataService, AuthService, $state, $http, $ionicPopup) {
  
  $scope.data = {};
  $scope.username = AuthService.username();
  $scope.userService = UserDataService;

  $scope.model = {};
  $scope.model.friends = UserDataService.getCurrentFriends();

  $scope.deleteContact = function(item) {
    console.log('deleting a friend');
    console.log(item);
  };

  $scope.addFriend = function() {
    var username = AuthService.username();
    $ionicPopup.show({
      template: '<input type="text" ng-model="data.friendUsername">',
      title: 'Add friend by username',
      scope: $scope,
      buttons: [
        { text: 'Cancel' },
        {
          text: '<b>Add</b>',
          type: 'button-positive',
          onTap: function(e) {
            if (!$scope.data.friendUsername) {
              //don't allow the user to close unless he enters friend username
              e.preventDefault();
            } else {
              var request = [{'username': username}, {'username': $scope.data.friendUsername}];
              console.log(request);
              $http.defaults.headers.post['Content-Type'] = 'application/json;charset=UTF-8';
              $http({
                method: 'POST',
                url: 'http://vcheng.org:8080/user/addFriend',
                data: request
              })
              .then(function(response) {
                window.history.back();
                console.log(response);
                $ionicPopup.alert({
                  title: 'Success',
                  template: response
                });
              }, function(response) {
                console.log(response);
                $ionicPopup.alert({
                  title: 'Error',
                  template: response
                });
              });
              return $scope.data.friendUsername;
            }
          }
        }
      ]
    });
  };

  $scope.removeFriend = function(friend) {
    var username = AuthService.username();
    var request = [{'username': username}, {'username': friend.username}];
    console.log(request);
    $http.defaults.headers.post['Content-Type'] = 'application/json;charset=UTF-8';
    $http({
      method: 'POST',
      url: 'http://vcheng.org:8080/user/removeFriend',
      data: request
    })
    .then(function(response) {
      window.history.back();
      console.log(response);
      $ionicPopup.alert({
        title: 'Success',
        template: response
      });
    }, function(response) {
      console.log(response);
      $ionicPopup.alert({
        title: 'Error',
        template: response
      });
    });
  };
});