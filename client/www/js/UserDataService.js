var App = angular.module('App');

App.factory('UserDataService', ['API', 'AuthService', 'IMG', function(API, AuthService, IMG) {

    var _friends = [];
    var _events = [];
    var _invites = [];

    var service = {
      getUsername: getUsername,
      acceptInvite: acceptInvite,
      declineInvite: declineInvite,
      finalizeEvent: finalizeEvent,
      getEvents: getEvents,
      getInvites: getInvites,
      getFriends: getFriends,
      refresh: refresh
    };

    return service;

    // Username.

    function getUsername() {
      return AuthService.username();
    }

    // Events.

    function getEvents() {
      return _events;
    }

    function finalizeEvent(eventId) {
      var request = {'id': eventId};
      API.post('event/finalizeEvent', request, 
        function (response) {
          loadAllEvents();
        }, function (response) {
          console.log(response);
        }
      );
    }

    function getInvites() {
      return _invites;
    }

    function acceptInvite(eventId) {
      var request = {'eventId': eventId, 'username': AuthService.username()};
      API.post('/user/acceptPendingEvent', request, 
        function (response) {
          refresh();
        },
        function (response) {
          console.log(response);
        }
      );
    }

    function declineInvite(eventId) {
      var request = {'eventId': eventId, 'username': AuthService.username()};
      API.post('/user/rejectPendingEvent', request, 
        function (response) {
          refresh();
        },
        function (response) {
          console.log(response);
        }
      );
    }

    function loadAllEvents() {
      var username = AuthService.username();
      var request = {'username': username};
      API.post('/user/getAllEvents', request, function(data) {
        var events = data.data;
        for (var i = 0; i < events.length; i++) {
          var current = events[i];
          if (current.type === 'wesync')
            current.avatar = IMG.wesync;
          else if (current.type === 'facebook')
            current.avatar = IMG.facebook;
          else if (current.type === 'google') 
            current.avatar = IMG.google;
        }
        _events = events;
      }, function (err) {
        console.log(err);
      });
    }

    function loadAllInvites() {
      var username = AuthService.username();
      var request = {'username': username};
      API.post('/user/getPendingEvents', request, function(data) {
      _invites = data.data;
      }, function (err) {
        console.log(err);
      });
    }

    // Friends.

    function getFriends() {
      return _friends;
    }

    function loadFriends() {
      var username = AuthService.username();
      API.getAllFriends(username).then(function(data) {
        _friends = data;
      });
    }

    // Re-fetches all data associated with the user.
    function refresh() {
      loadFriends();
      loadAllEvents();
      loadAllInvites();
    }

}]);