var App = angular.module('App');

// This class communicates with the server.

App.factory('API', ['$http', function($http) {
  
  var service = {
    getAllFriends: getAllFriends,
    post: post,
    commitFBAccessToken: commitFBAccessToken,
    uploadGoogleCalendarEvents: uploadGoogleCalendarEvents
  };

  return service;

  function getAllFriends(username) {
    $http.defaults.headers.post['Content-Type'] = 'application/json;charset=UTF-8';
    return $http({
        method: 'POST',
        url: 'http://vcheng.org:8080/user/getAllFriends',
        data: {'username': username}
      })
      .then(function(friendJSONArray) {
        var updatedFriends = [];
        for (var i = 0; i < friendJSONArray.data.length; i++) {
          var friendObject = friendJSONArray.data[i];
          updatedFriends.push(friendObject.username);
        }
        return updatedFriends;
      }, function(response) {
        console.log('error fetching friends');
        console.log(response);
        return [];
      });
  }

  function post(endpoint, request, success, fail) {
  	$http.defaults.headers.post['Content-Type'] = 'application/json;charset=UTF-8';
    $http({
      method: 'POST',
      url: 'http://vcheng.org:8080/' + endpoint,
      data: request
    })
    .then(function(response) { success(response); }, function(response) { fail(response); });
  }
  
  function commitFBAccessToken(username, accessToken, success, fail) {
    var request = {'username': username, 'facebookToken': accessToken};
    $http.defaults.headers.post['Content-Type'] = 'application/json;charset=UTF-8';
    $http({
      method: 'POST',
      url: 'http://vcheng.org:8080/user/addFacebookToken',
      data: request
    })
    .then(function(response) { success(response); }, function(response) { fail(response); });
  }

  function uploadGoogleCalendarEvents(username, events, success, fail) {
    for (var i = 0; i < events.length; i++) {
      events[i].username = username;
    }
    console.log(events);
    $http.defaults.headers.post['Content-Type'] = 'application/json;charset=UTF-8';
    $http({
      method: 'POST',
      url: 'http://vcheng.org:8080/user/addGoogleEvents',
      data: events
    })
    .then(function(response) { success(response); }, function(response) { fail(response); });
  }
}]);