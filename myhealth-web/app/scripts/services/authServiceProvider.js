'use strict';

angular.module('myhealthWebApp')
    .factory('AuthServiceProvider', function loginService($http, $resource, localStorageService) {
        return {
        	login: function(credentials) {
    			var postData = {
    				'username' : credentials.username,
    				'password' : credentials.password
    			};

    			return $http({
    				method : 'POST',
    				url : BASE_PATH + '/authenticate',
    				data : postData
    			}).success(function(response) {
    				return response;
    			});
        	},
        	loginSecretAnswer: function(credentials) {
    			var postData = {
    				'username' : credentials.username,
    				'secretQuestion': credentials.secretQuestion,
    				'secretAnswer' : credentials.secretAnswer
    			};

    			return $http({
    				method : 'POST',
    				url : BASE_PATH + '/authenticateSecret',
    				data : postData
    			}).success(function(data) {
    				localStorageService.set('token', data);
    				return response;
    			}).error(function(data) {
    				localStorageService.clearAll();
    				return data;
    			});
        	},
            logout: function() {
                //Stateless API : No server logout
                localStorageService.clearAll();
                localStorage.removeItem("ihi");
				localStorage.removeItem("birthDate");
				localStorage.removeItem("gender");
            },
            getToken: function () {
                return localStorageService.get('token');
            },
            hasValidToken: function () {
                var token = this.getToken();
                return token && token.expires && token.expires > new Date().getTime();
            },
            account: function () {
            	return $http({
    				method : 'GET',
    				url : BASE_PATH + '/Patient',
    				data : {}
    			}).success(function(response) {
    				return response;
    			});
            }
        };
    });
