var App = angular.module("App", ["ionic", "ngMockE2E"]);

App.config(function ($stateProvider, $urlRouterProvider) {
  $stateProvider
  .state('login', {
    url: '/login',
    templateUrl: 'templates/login.html',
    controller: 'LoginCtrl'
  })
  .state('register', {
    url: '/register',
    templateUrl: 'templates/register.html',
    controller: 'RegisterCtrl'
  })
  .state('main', {
    url: '/',
    abstract: true,
    templateUrl: 'templates/main.html'
  })
  .state('main.dash', {
    url: 'main/dash',
    views: {
        'dash-tab': {
          templateUrl: 'templates/dashboard.html',
          controller: 'DashCtrl'
        }
    }
  })
  .state('main.friends', {
    url: 'main/friends',
    views: {
        'friends-tab': {
          templateUrl: 'templates/friends.html'
        }
    }
  })
  .state('main.settings', {
    url: 'main/settings',
    views: {
        'settings-tab': {
          templateUrl: 'templates/settings.html'
        }
    }
  });
  $urlRouterProvider.otherwise('/main/dash');
});

App.run(function($httpBackend){
  $httpBackend.whenGET(/templates\/\w+.*/).passThrough();
});

App.run(function ($rootScope, $state, AuthService, AUTH_EVENTS) {
  $rootScope.$on('$stateChangeStart', function (event,next, nextParams, fromState) {
    if (!AuthService.isAuthenticated()) {
      if (next.name !== 'login' && next.name !== 'register') {
        event.preventDefault();
        $state.go('login');
      }
    }
  });
});