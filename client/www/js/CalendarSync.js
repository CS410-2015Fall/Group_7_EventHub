var App = angular.module('App');

App.service('CalendarSync', function($cordovaFacebook, $cordovaCalendar) {

  function fetchGoogleEvents(success, fail) {
    var from = new Date();
    var to = new Date();
    to.setMonth(from.getMonth() + 1);
    
    $cordovaCalendar.listEventsInRange(from, to).then(
      function (result) {
        var gEvents = [];
        for (var i = 0; i < result.length; i++){
          var current = result[i];
          if (current.title.indexOf('[WeSync]') === -1){
            gEvents.push(current);
          }
        }
        success(gEvents);
      }, function (bad) {
        fail(bad);
      }
    );
  }

  function fetchFacebookToken(success, fail) {
    $cordovaFacebook.login(['user_events']).then( 
      function (user) {
        if (user.status === "connected") {
          var token = user.authResponse.accessToken;
          success(token);
        }
      }, function (error) {
        fail(error);
      });
  }

  function createCalendarEntry(name, location, description, start, end, success, fail) {
    $cordovaCalendar.createEvent({
      title: name,
      location: location,
      notes: description,
      startDate: new Date(start),
      endDate: new Date(end)
    }).then(success, fail);
  }

  // Check if wEvent is in google calendar, if it is not then add to it.
  function syncFinalizedEvent(wEvent) {
    $cordovaCalendar.findEvent({
      title: '[WeSync] ' + wEvent.name,
      location: wEvent.location,
      notes: wEvent.description,
      startDate: new Date(wEvent.startDate),
      endDate: new Date(wEvent.endDate)
    }).then(function (result) {
      if (result.length == 0) {
        createCalendarEntry('[WeSync] ' + wEvent.name, wEvent.location, wEvent.description,
         wEvent.startDate, wEvent.endDate, function () {}, function () {});
      }
    }, function (err) {
      console.log(err);
    });
  }

  return {
    fetchFacebookToken: fetchFacebookToken,
    fetchGoogleEvents: fetchGoogleEvents,
    createCalendarEntry: createCalendarEntry,
    syncFinalizedEvent: syncFinalizedEvent
  };
});