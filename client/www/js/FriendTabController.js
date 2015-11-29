var App = angular.module('App');

App.controller('FriendsController', function($scope, UserDataService, API, $ionicPopup) {
  
  $scope.data = {};
  $scope.userService = UserDataService;

  $scope.model = {};
  $scope.model.friends = UserDataService.getFriends();

  UserDataService.loadFriends();

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
    API.post('user/addFriend', request, 
      function(response) {
        UserDataService.refresh();
        $ionicPopup.alert({
          title: 'Success',
          template: 'New friend added.'
        });
      },
      function(response) {
        $ionicPopup.alert({
          title: 'Error',
          template: 'Please try again later.'
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
          template: 'Friend removed.'
        });
      },
      function(response) {
        $ionicPopup.alert({
          title: 'Error',
          template: 'Please try again later.'
        });
      }
    );
  };
});