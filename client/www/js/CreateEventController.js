var App = angular.module('App');

App.controller('CreateEventCtrl', function($scope, $http, $ionicPopup, $ionicModal, UserDataService, AuthService, API) {
  $scope.friends = UserDataService.getFriends();
  $scope.formLocation = "Pick a Location";
  $scope.data = {};

  (function () {
    $scope.$watch(function () {
      return UserDataService.getFriends();
    }, function (newValue, oldValue) {
      if ( newValue !== oldValue ) {
          $scope.friends = newValue;
      }
    });
  }());

  $ionicModal.fromTemplateUrl('map-picker-modal.html', {
    scope: $scope,
    animation: 'slide-in-up'
  }).then(function(modal) {
    $scope.modal = modal;
  })

  $scope.$on('modal.shown', function() {
    var vancouver = new google.maps.LatLng(49.2827, -123.1207);

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
    google.maps.event.addListener(map, "click", function(event) {
      console.log('yo');
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
    $scope.modal.show();
  };

  $scope.closeModal = function() {
    $scope.formLocation = $scope.lat + ', ' + $scope.lon;
    $scope.modal.hide();
  };

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