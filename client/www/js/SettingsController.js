var App = angular.module('App');

App.controller('SettingsController', function($scope, $cordovaFacebook, $cordovaCalendar) {
  $scope.linkFB = function() {
    $cordovaFacebook.login(['user_events']).then( function (user) {
      console.log(user);
    }, function (error) {
      console.log(error);
    });
    console.log('linked with fb!');
  };

  $scope.getStatus = function() {
    $cordovaFacebook.getLoginStatus().then(
      function (success) {
        console.log('user is currently logged in');
        console.log(success);
        console.log('getting credentials');
        $cordovaFacebook.getAccessToken().then(
          function (token) {
            console.log('userToken is');
            console.log(token);
          }, function (failure) {
            console.log('couldnt retrieve token');
            console.log(failure);
          }
        );
      }, function (failure) {
        console.log('user is NOT logged in');
        console.log(failure);
      }
    );
  };

  $scope.linkNative = function() {
    var from = new Date();
    var to = new Date();
    to.setMonth(from.getMonth() + 1);
    $cordovaCalendar.listCalendars().then(function (result) {
      console.log('here are my calendars');
      console.log(result);
      $cordovaCalendar.listEventsInRange(from, to)
      .then(function (result) {
        console.log('fetched native events successfully');
        console.log(result);
      }, function (err) {
        console.log('error!');
        console.log(err);
      });
    }, function (err) {
      console.log(err);
    });
  }
});