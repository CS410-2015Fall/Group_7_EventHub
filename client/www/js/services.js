var App = angular.module('App');

App.service('AuthService', function($q, $http, $ionicPopup) {
  var LOCAL_TOKEN_KEY = 'LocalTokenKey';
  var username = 'admin';
  var isAuthenticated = false;
  var authToken;

  function loadUserCredentials() {
    var token = window.localStorage.getItem(LOCAL_TOKEN_KEY);
    if (token) {
      useCredentials(token);
    }
  }

  function storeUserCredentials(token) {
    window.localStorage.setItem(LOCAL_TOKEN_KEY, token);
    useCredentials(token);
  }

  function useCredentials(token) {
    username = token.split('.')[0];
    isAuthenticated = true;
    authToken = token;

    $http.defaults.headers.common['X-Auth-Token'] = token;
  }

  function destroyUserCredentials() {
    authToken = undefined;
    username = '';
    isAuthenticated = false;
    $http.defaults.headers.common['X-Auth-Token'] = undefined;
    window.localStorage.removeItem(LOCAL_TOKEN_KEY);
  }

  var login = function(name, pw) {
    return $q(function(resolve, reject) {
      $http.defaults.headers.common['Content-Type'] = 'application/json;charset=UTF-8';
      $http({
        method: 'POST',
        url: 'http://vcheng.org:8080/user/validateUser',
        data: {'username': name, 'password': pw}
      })
      .then(function(response) {
        storeUserCredentials(response.username + 'ServerToken');
        resolve('Login success');
      }, function(response) {
        $ionicPopup.alert({
          title: 'Problem logging in',
          template: 'Invalid credentials.'
        });
      });
    });
  };

  var logout = function() {
    destroyUserCredentials();
  };

  loadUserCredentials();

  return {
    login: login,
    logout: logout,
    isAuthenticated: function() {return isAuthenticated;},
    username: function() {return username;}
  };
});


App.factory('AuthInterceptor', function ($rootScope, $q, AUTH_EVENTS) {
  return {
    responseError: function (response) {
      $rootScope.$broadcast({
        401: AUTH_EVENTS.notAuthenticated,
        403: AUTH_EVENTS.notAuthorized
      }[response.status], response);
      return $q.reject(response);
    }
  };
});

App.config(function ($httpProvider) {
  $httpProvider.interceptors.push('AuthInterceptor');
});