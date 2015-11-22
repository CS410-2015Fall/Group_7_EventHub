var App = angular.module('App');

App.controller('SettingsController', function($scope, UserDataService, CalendarSync) {
  $scope.settings = {};
  $scope.settings.fb = UserDataService.isFacebookLinked();
  $scope.settings.google = UserDataService.isGoogleLinked();

  // $scope.$watch('settings.fb', function(newValue, oldValue) {
  //   if (newValue == false) {
  //     UserDataService.removeFBToken();
  //     console.log('A');
  //   } else {
  //     UserDataService.syncFacebook();
  //     console.log('B');
  //   }
  // });

  // $scope.$watch('settings.google', function(newValue, oldValue) {
  //   alert('TODO');
  // });
});