var App = angular.module('App');

App.controller('SettingsController', function($scope, UserDataService, $ionicPopup) {
  $scope.settings = {};

  $scope.toggleFb = function(type) {
    UserDataService.syncFacebook(function() {
      $ionicPopup.alert({
          title: 'Success',
          template: 'Facebook events updated!'
        });
    }, function() {
      $ionicPopup.alert({
          title: 'Oops',
          template: 'Please try again later.'
        });
    });
  };

  $scope.toggleGoogle = function(type) {
    UserDataService.syncGoogle(function() {
      $ionicPopup.alert({
          title: 'Success',
          template: 'Google events updated!'
        });
    }, function() {
      $ionicPopup.alert({
          title: 'Oops',
          template: 'Please try again later.'
        });
    });
  };
});

App.directive('socialBtn', function() {
  return {
    restrict: 'E',
    template: '<i class="icon ion-link"></i>' 
  };
});