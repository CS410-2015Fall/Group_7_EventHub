var App = angular.module('App');

App.controller('FriendsController', function($scope, UserDataService, API, $ionicPopup) {
  
  $scope.data = {};
  $scope.userService = UserDataService;

  $scope.model = {};
  $scope.model.friends = UserDataService.getFriends();

  (function () {
    $scope.$watch(function () {
      return UserDataService.getFriends();
    }, function (newValue, oldValue) {
      if ( newValue !== oldValue ) {
        $scope.model.friends = newValue;
      }
    });
  }());

  $scope.addFriend = function(friend) {
    var username = UserDataService.getUsername();
    var request = [{'username': username}, {'username': friend}];
    API.post('/user/addFriend', request, 
      function(response) {
        UserDataService.refresh();
        $ionicPopup.alert({
          title: 'Success',
          template: response
        });
      },
      function(response) {
        $ionicPopup.alert({
          title: 'Error',
          template: response
        });
      }
    );
  }

  $scope.removeFriend = function(friend) {
    var username = UserDataService.getUsername();
    var request = [{'username': username}, {'username': friend}];
    API.post('user/removeFriend', request, 
      function(response) {
        UserDataService.refresh();
        $ionicPopup.alert({
          title: 'Success',
          template: response
        });
      },
      function(response) {
        $ionicPopup.alert({
          title: 'Error',
          template: response
        });
      }
    );
  };
});