'use strict';

var showLoading = true;
//TODO: cuando se genere el build, asignar el BASE_PATH
var BASE_PATH = window.location.href.indexOf(':9000') >0 ? '//localhost:9080/api' : '//myhealth-api.elasticbeanstalk.com/api';

/**
 * @ngdoc overview
 * @name myhealthWebApp
 * @description # myhealthWebApp
 * 
 * Main module of the application.
 */
angular.module('myhealthWebApp', [ 'ngAnimate', 'ngResource', 'ngSanitize', 'mipatojoServices', 'pascalprecht.translate',
		'ui.router', 'oc.lazyLoad', 'ui.bootstrap', 'ngCookies', 'FiltersModule', 'datatables', 'highcharts-ng',
		'viewService', 'xml', 'LocalStorageModule', 'angularSpinner', 'angular-ladda'

		]).config(function($stateProvider, $urlRouterProvider, $httpProvider, $ocLazyLoadProvider) {
			$urlRouterProvider.otherwise("/login");
			$httpProvider.interceptors.push('authInterceptor');

			$ocLazyLoadProvider.config({
				debug : true
			});
			
			$stateProvider.state('login', {
		        url: '/login',
		        templateUrl: 'views/login.html',
		        data: {
		            pageTitle: 'login.title'
		        },
		        resolve: {
		            translatePartialLoader: ['$translate', '$translatePartialLoader', 
		              function ($translate, $translatePartialLoader) {
		                $translatePartialLoader.addPart('login');
		                return $translate.refresh();
		            }]
		        }
		    }).state('charts',{
				abstract : true,
				url : "/charts",
				templateUrl : "views/common/content.html",
				resolve : {
					mainTranslatePartialLoader : [ '$translate', '$translatePartialLoader',
						function($translate, $translatePartialLoader) {
							$translatePartialLoader.addPart('main');
							$translatePartialLoader.addPart('growthchart');
							return $translate.refresh();
						} ]
				}
			}).state('charts.growthcharts', {
				url : "/growthcharts",
				templateUrl : "views/growth_charts.html",
				data : {
					pageTitle : 'Growth Charts'
				}
			}).state('app',{
				abstract : true,
				url : "/app",
				templateUrl : "views/common/content.html",
				resolve : {
					mainTranslatePartialLoader : [ '$translate', '$translatePartialLoader',
							function($translate, $translatePartialLoader) {
								$translatePartialLoader.addPart('main');
								$translatePartialLoader.addPart('profile');
								return $translate.refresh();
							} ]
				}
			}).state('app.profile', {
				url : "/profile",
				templateUrl : "views/profile.html",
				data : {
					pageTitle : 'Profile'
				}
			}).state('notes',{
				abstract : true,
				url : "/notes",
				templateUrl : "views/common/content.html",
				resolve : {
					mainTranslatePartialLoader : [ '$translate', '$translatePartialLoader',
						function($translate, $translatePartialLoader) {
							$translatePartialLoader.addPart('main');
							//$translatePartialLoader.addPart('notes');
							return $translate.refresh();
						} ]
				}
			}).state('notes.phn', {
				url : "/phn",
				templateUrl : "views/phn.html",
				data : {
					pageTitle : 'Personal Health Notes',
					type : '100.16681'
				}
			}).state('documents', {
				abstract : true,
				url : "/documents",
				templateUrl : "views/common/content.html",
				resolve : {
					mainTranslatePartialLoader : [ '$translate', '$translatePartialLoader',
						function($translate, $translatePartialLoader) {
							$translatePartialLoader.addPart('main');
							return $translate.refresh();
						} ]
				}
			}).state('documents.search', {
				url : "/search",
				templateUrl : "views/document_search.html",
				data : {
					pageTitle : 'Search',
					type : ''
				}
			}).state('documents.mbs', {
				url : "/mbs",
				templateUrl : "views/mbs.html",
				data : {
					pageTitle : 'Medicare Benefits',
					type : '100.16644'
				}
			}).state('documents.pbs', {
				url : "/pbs",
				templateUrl : "views/pbs.html",
				data : {
					pageTitle : 'Pharmaceutical Benefits',
					type : '100.16650'
				}
			}).state('views', {
				abstract : true,
				url : "/views",
				templateUrl : "views/common/content.html",
				resolve : {
					mainTranslatePartialLoader : [ '$translate', '$translatePartialLoader',
						function($translate, $translatePartialLoader) {
							$translatePartialLoader.addPart('main');
							return $translate.refresh();
						} ]
				}
			}).state('views.view', {
				url : "/view?repoId&docId",
				templateUrl : "views/view_document.html",
				data : {
					pageTitle : 'View Document'
				}
			});
		}).run(function($rootScope, $state, Principal, Auth) {
	$rootScope.$state = $state;
	
	$rootScope.$on('$stateChangeStart', function (event, toState, toStateParams) {
		var isAuthenticated = Principal.isAuthenticated();
		if(!isAuthenticated){
			$state.go('login', {}, {notify: false});
		}
    });
	
	$rootScope.$on('$stateChangeSuccess',  function(event, toState, toParams, fromState, fromParams) {
        $rootScope.previousStateName = fromState.name;
        $rootScope.previousStateParams = fromParams;
        
    });
	
}).factory('authInterceptor', function ($rootScope, $q, $location, localStorageService, usSpinnerService) {
    return {
        // Add authorization token to headers
        request: function (config) {
        	usSpinnerService.spin('spinner-loading');
        	$rootScope.errorConnection= undefined;
        	
            config.headers = config.headers || {};
            var token = localStorageService.get('token');
            
            if (token && token.expires && token.expires > new Date().getTime()) {
              config.headers['x-auth-token'] = token.token;
            }
            return config;
        },
        responseError: function (response) {
        	usSpinnerService.stop('spinner-loading');
            if (response.status === 401) {
            	// handle the case where the user is not authenticated
            	$rootScope.errorConnection= undefined;
            	
            	$location.path('index.html#/login');
            }else if(response.status === 0 || response.status === 502){
            	var error = new Object();
				error.message = "Error contacting the server";
				error.title = "Error";
				$rootScope.errorConnection= error;
				return;
            }else{
            	var error = new Object();
				error.message = "Unknown error. Please retry later.";
				error.title = "Error";
				$rootScope.errorConnection= error;
				return;
            }
            return response || $q.when(response);
        },
        response: function (response) {
        	usSpinnerService.stop('spinner-loading');
            return response || $q.when(response);
        }

    };
});
