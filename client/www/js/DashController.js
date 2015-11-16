var App = angular.module('App');

App.controller('DashCtrl', function($scope, $state, AuthService, UserDataService, $ionicPopup, $ionicLoading, API) {

  $scope.model = {};
  $scope.model.events = UserDataService.getEvents();
  $scope.model.invites = UserDataService.getInvites();

  (function () {
    $scope.$watch(function () {
      return UserDataService.getEvents();
    }, function (newValue, oldValue) {
      if ( newValue !== oldValue ) {
          $scope.model.events = newValue;
      }
    });
  }());

  (function () {
    $scope.$watch(function () {
      return UserDataService.getInvites();
    }, function (newValue, oldValue) {
      if ( newValue !== oldValue ) {
          $scope.model.invites = newValue;
      }
    });
  }());

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

  $scope.acceptInvite = function(eventId) {
    UserDataService.acceptInvite(eventId);
  };

  $scope.declineInvite = function(eventId) {
    UserDataService.declineInvite(eventId);
  };

  $scope.findTime = function(eventId) {
    $ionicLoading.show({
      content: 'Loading',
      animation: 'fade-in',
      showBackdrop: true,
      maxWidth: 200,
      showDelay: 0
    });

    var request = {'id': eventId};
    API.post('event/findTime', request, 
      function (response) {
        $ionicLoading.hide();
        $scope.presentOption(response.data);
      }, function (response) {
        console.log(response);
      }
    );
  };

  $scope.presentOption = function(tempEvent) {
    var eventStart = new Date(tempEvent.startDate);
    var eventEnd = new Date(tempEvent.endDate);
    var time = '' + eventStart.toDateString() + ' from ' + eventStart.getHours() + ':' + eventStart.getMinutes() + ' to ' + eventEnd.getHours() + ':' + eventEnd.getMinutes();

    $ionicPopup.confirm({
      title: 'Free time found!',
      template: 'Everyone is free on ' + time + '.<br>Finalize this event? This will remove any pending invitations.'
    }).then(function (agree) {
      if (agree) { 
        UserDataService.finalizeEvent(tempEvent.id);
      }
    });
  };
});