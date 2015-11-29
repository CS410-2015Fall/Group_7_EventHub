var App = angular.module('App');

App.controller('SettingsController', function($scope, UserDataService) {
  $scope.settings = {};

  $scope.toggleFb = function(type) {
    UserDataService.syncFacebook();
  };

  $scope.toggleGoogle = function(type) {
    UserDataService.syncGoogle();
  };
});

App.directive('socialBtn', function() {
  return {
    restrict: 'E',
    template: '<i class="icon ion-link"></i>' 
  };
});