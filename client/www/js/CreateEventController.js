var App = angular.module('App');

App.controller('CreateEventCtrl', function($scope, $ionicPopup, $ionicModal, UserDataService, API, CalendarSync) {
  $scope.friends = UserDataService.getFriends();
  $scope.formLocation = "Pick a Location";
  $scope.data = {};

  UserDataService.loadFriends();

  (function () {
    $scope.$watch(function () {
      return UserDataService.getFriends();
    }, function (newValue, oldValue) {
      if ( newValue !== oldValue ) {
          $scope.friends = newValue;
      }
    });
  }());

  $scope.$on('modal.shown', function() {
    if (!$scope.lat) $scope.lat = 49.2827;
    if (!$scope.lon) $scope.lon = -123.1207;

    var vancouver = new google.maps.LatLng($scope.lat, $scope.lon);

    var mapOptions = {
        center: vancouver,
        zoom: 16,
        mapTypeId: google.maps.MapTypeId.ROADMAP
    };

    var map = new google.maps.Map(document.getElementById("map"), mapOptions);

    $scope.data.marker = new google.maps.Marker({
        position: vancouver,
        map: map,
        title: "Event Location"
    });

    // Add a click event handler to the map
    google.maps.event.addListener(map, "mousedown", function(event) {
        $scope.data.marker.setMap(null);
        $scope.data.marker = new google.maps.Marker({
            position: event.latLng,
            map: map,
            title: "Event Location"
        });

        $scope.lat = event.latLng.lat();
        $scope.lon = event.latLng.lng();
    });
  });  

  $scope.loadMapPicker = function() {
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
    $scope.formLocation = $scope.lat + ', ' + $scope.lon;
    $scope.modal.remove();
  };

  $scope.goBack = function() {
    window.history.back();
  };

  $scope.createEvent = function() {
    var data = this.data;
    var request = {
      'name': data.eventName,
      'description': data.eventDescription,
      'location': $scope.formLocation,
      'startDate': (1900 + data.eventDate.getYear()) + '-' + (data.eventDate.getMonth() + 1) + '-' + data.eventDate.getDate(),
      'duration': data.eventDuration,
      'host': UserDataService.getUsername(),
      'invitees': data.guests,
      'confirmedInvitees': []
    };

    API.post('event/createEvent', request,
      function(response) {
        window.history.back();
        $ionicPopup.alert({
          title: 'Success',
          template: 'Event created!'
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