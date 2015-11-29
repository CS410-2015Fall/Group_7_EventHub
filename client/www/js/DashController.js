var App = angular.module('App');

App.controller('DashCtrl', function($scope, $state, AuthService, UserDataService, $ionicPopup, $ionicLoading, API, $ionicModal, CalendarSync) {

  $scope.username = AuthService.username();

  $scope.model = {};
  $scope.model.events = UserDataService.getEvents();
  $scope.model.invites = UserDataService.getInvites();

  $scope.data = {};

  UserDataService.refresh();

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

  $scope.acceptInvite = function(wEvent) {
    UserDataService.acceptInvite(wEvent.id);
  };

  $scope.declineInvite = function(wEvent) {
    UserDataService.declineInvite(wEvent.id);
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

  $scope.$on('modal.shown', function() {
    var location = new google.maps.LatLng($scope.lat, $scope.lon);

    var mapOptions = {
        center: location,
        zoom: 16,
        mapTypeId: google.maps.MapTypeId.ROADMAP
    };

    $scope.data.map = new google.maps.Map(document.getElementById("map"), mapOptions);

    $scope.data.marker = new google.maps.Marker({
        position: location,
        map: $scope.data.map,
        title: "Event Location"
    });
  });  

  $scope.loadMapPicker = function(location) {
    if (!location) return;
    var latlng = location.split(', ');

    $scope.lat = parseFloat(latlng[0]);
    $scope.lon = parseFloat(latlng[1]);

    if (!$scope.lat || !$scope.lon) return;

    $ionicModal.fromTemplateUrl('templates/map-picker-modal.html', {
      scope: $scope,
      animation: 'slide-in-up',
      backdropClickToClose: false,
      hardwareBackButtonClose: false
    }).then(function(modal) {
      $scope.modal = modal;
      $scope.modal.show();
    });
  };

  $scope.closeModal = function() {
    $scope.modal.remove();
  };
});