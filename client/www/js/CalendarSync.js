var App = angular.module('App');

App.service('CalendarSync', function($cordovaFacebook, $cordovaCalendar) {

  function fetchGoogleEvents(success, fail) {
    var from = new Date();
    var to = new Date();
    to.setMonth(from.getMonth() + 1);
    
    $cordovaCalendar.listEventsInRange(from, to).then(
      function (result) {
        success(result);
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

  return {
    fetchFacebookToken: fetchFacebookToken,
    fetchGoogleEvents: fetchGoogleEvents
  };
});