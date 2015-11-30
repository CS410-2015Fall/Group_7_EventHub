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
      syncGoogle: syncGoogle,
      removeFacebookToken: removeFacebookToken,
      setUser: setUser,
      isFacebookLinked: isFacebookLinked,
      isGoogleLinked: isGoogleLinked,
      loadFriends: loadFriends
    };

    return service;

    // Username.

    function getUsername() {
      return _user.username;
    }

    function isFacebookLinked() {
      return _user.facebookToken != null && _user.facebookToken != 'N/A';
    }

    function isGoogleLinked() {
      return _user.googleToken != null && _user.googleToken != 'N/A';
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
          // On success, create a calendar entry on the device and refresh.
          var data = response.data;
          CalendarSync.createCalendarEntry('[WeSync] ' + data.name, data.location, data.description, data.startDate, data.endDate, function(s) {
            console.log('createSuccess');
          }, function(f) {
            console.log(f);
          });
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
      API.post('user/acceptPendingEvent', request, 
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
      API.post('user/rejectPendingEvent', request, 
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
          if (current.type === 'wesync') {
            current.avatar = IMG.wesync;
            
            // Check if this is finalized if so add to cal.
            if (current.isFinalized) {
              CalendarSync.syncFinalizedEvent(current);
            }
          }
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
      API.post('user/getPendingEvents', request, function(data) {
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
      API.post('user/findByUsername', request, function(s) {
        _user = s.data; 
      }, function(e) {
        console.log(e);
      });
    }

    function syncFacebook(success, fail) {
      var succeed = success;
      CalendarSync.fetchFacebookToken(
        function (token) {
          API.commitFBAccessToken(_user.username, token, function(success){
            refreshUserSettings();
            API.post('facebook/getFacebookEvents', {'username': _user.username}, function(s) {
              loadAllEvents();
              succeed();
            }, function(e) {
              fail();
            });
          }, function(e) {
            fail();
          });
        }, function (err) {
          fail();
        }
      );  
    }

    function syncGoogle(success, fail) {
      CalendarSync.fetchGoogleEvents(
        function (gEvents) {
          API.uploadGoogleCalendarEvents(_user.username, gEvents, function(s) {
            API.post('user/addGoogleToken', {"username": _user.username, "googleToken":"true"}, function() {}, function() {});
            loadAllEvents();
            success();
          }, function(e) { fail(); });
        }, function (err) {
          fail();
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
      API.commitFBAccessToken(_user.username, '', function(s){ refreshUserSettings(); }, function(e) {});
    }

}]);