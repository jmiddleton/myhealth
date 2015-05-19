'use strict';

angular.module('myhealthWebApp')
    .factory('Auth', function Auth($rootScope, $state, $q, $translate, Principal, AuthServiceProvider) {
        return {
        	login: function (credentials, callback) {
        	    var cb = callback || angular.noop;
        	    var deferred = $q.defer();

        	    if(credentials.username && credentials.password){
	        	    AuthServiceProvider.login(credentials).then(function (data) {
	        	        deferred.resolve(data);
	        	        return cb();
	        	    }).catch(function (err) {
	        	        this.logout();
	        	        deferred.reject(err);
	        	        return cb(err);
	        	    }.bind(this));
	
	        	    return deferred.promise;
	        	    
	        	}else{
	    	    	var err = {error: 'Username and Password are required.'};
	    	    	deferred.reject(err);
	    	    	return deferred.promise;
	    	    }
        	},
        	loginSecretAnswer: function (credentials, callback) {
        	    var cb = callback || angular.noop;
        	    var deferred = $q.defer();
        	    
        	    if(credentials.secretAnswer){
	        	    AuthServiceProvider.loginSecretAnswer(credentials).then(function (data) {
	        	        // retrieve the logged account information
	        	    	if(data !== undefined){
		        	        Principal.identity(true).then(function(account) {
		        	            // After the login the language will be changed to
		        	            // the language selected by the user during his
								// registration
		        	            //$translate.use(account.langKey);
		        	        	deferred.resolve(account);
		        	        });
	        	    	}else{
	        	    		deferred.reject({error: 'Invalid secret answer.'});
	        	    	}
	
	        	        return cb();
	        	    }).catch(function (err) {
	        	        this.logout();
	        	        deferred.reject(err);
	        	        return cb(err);
	        	    }.bind(this));
	
	        	    return deferred.promise;
        	    }else{
        	    	var err = {error: 'Secret answer is required.'};
        	    	deferred.reject(err);
        	    	return deferred.promise;
        	    }
        	},

        	logout: function () {
        		AuthServiceProvider.logout();
        	    Principal.authenticate(null);
        	},

            authorize: function(force) {
                return Principal.identity(force)
                    .then(function() {
                        var isAuthenticated = Principal.isAuthenticated();

//                        if ($rootScope.toState.data.roles && $rootScope.toState.data.roles.length > 0 && !Principal.isInAnyRole($rootScope.toState.data.roles)) {
//                            if (isAuthenticated) {
//                                // user is signed in but not authorized for
//								// desired state
//                                //$state.go('accessdenied');
//                            }
//                            else {
//                                // user is not authenticated. stow the state
//								// they wanted before you
//                                // send them to the signin state, so you can
//								// return them when you're done
//                                $rootScope.returnToState = $rootScope.toState;
//                                $rootScope.returnToStateParams = $rootScope.toStateParams;
//
//                                // now, send them to the signin state so they
//								// can log in
//                                $state.go('login');
//                            }
//                        }
                    });
            }
        };
    });
