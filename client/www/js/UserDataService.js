var App = angular.module('App');

App.factory('UserDataService', ['API', 'IMG', 'CalendarSync', function(API, IMG, CalendarSync) {

    var _friends = [];
    var _events = [];
    var _invites = [];
    var _user = {};

    var service = {
      getUsername: getUsername,
      acceptInvite: acceptInvite,
      declineInvite: declineInvite,
      finalizeEvent: finalizeEvent,
      getEvents: getEvents,
      getInvites: getInvites,
      getFriends: getFriends,
      refresh: refresh,
      syncExternalCalendars: syncExternalCalendars,
      syncFacebook: syncFacebook,
      setUser: setUser,
      isFacebookLinked: isFacebookLinked,
      isGoogleLinked: isGoogleLinked,
    };

    return service;

    // Username.

    function getUsername() {
      return _user.username;
    }

    function isFacebookLinked() {
      return _user.facebookToken != null;
    }

    function isGoogleLinked() {
      return true;
    }

    // Events.

    function getEvents() {
      return _events;
    }

    function setUser(user) {
      _user = user;
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
      var request = {'eventId': eventId, 'username': _user.username};
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
      var request = {'eventId': eventId, 'username': _user.username};
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
      var username = _user.username;
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
      var username = _user.username;
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
      var username = _user.username;
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

    function refreshUserSettings() {
      var request = {'username': _user.username};
      var scope = this;
      API.post('/user/findByUsername', request, function(s) {
        scope.setUser(s.data); 
      }, function(e) {
        console.log(e);
      });
    }

    function syncFacebook() {
      CalendarSync.fetchFacebookToken(
        function (token) {
          API.commitFBAccessToken(_user.username, token, function(s){}, function(e) {});
        }, function (err) {
          console.log(err);
        }
      );  
    }

    function syncExternalCalendars() {
      // If user has FB linked.
      if (isFacebookLinked()) {
        syncFacebook();
      }
      // If user has Google linked.
      if (isGoogleLinked()) {
        CalendarSync.fetchGoogleEvents(
          function (gEvents) {
            API.uploadGoogleCalendarEvents(_user.username, gEvents, function(s) {}, function(e) {});
          }, function (err) {
            console.log(err);
          }
        );
      }
    }

    function removeFacebookToken() {
      API.commitFBAccessToken(_user.username, null, function(s){}, function(e) {});
    }

}]);