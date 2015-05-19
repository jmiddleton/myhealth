'use strict';

/**
 * Login controller.
 * 
 * @param $scope
 * @param $http
 */
function LoginCtrl($rootScope, $scope, $http, $state, Auth, Principal, recordService) {
	var loginCtrl = this;
	
	loginCtrl.loginUser = function(user) {
		loginCtrl.errorMsg = '';
		
		if(user === undefined || user.username === undefined || user.password === undefined){
			loginCtrl.errorMsg = 'Please enter Username and/or Password.';
			return;
		}
		
		$scope.loginLoading = true;
		Auth.login({
            username: user.username,
            password: user.password
        }).then(function (response) {
        	$scope.loginLoading = false;

        	if (response.status == 200 && response.data.status == 'SUCCESS') {
				loginCtrl.secretQuestion = response.data.secretQuestion;
			} else if(response.status == 0) {
				loginCtrl.errorMsg = 'Failed to connect to the server. Please try again later.';
			}else{
				loginCtrl.errorMsg = response.data.detail;
			}
        }).catch(function (error) {
        	$scope.loginLoading = false;
        	loginCtrl.errorMsg = error.error;
        });
	}
	
	loginCtrl.closeError = function(){
		loginCtrl.errorMsg = null;
	}

	loginCtrl.loginSecretAnswer = function(user) {
		loginCtrl.errorMsg = '';
		$scope.loginSQLoading = true;
		
		if(loginCtrl.secretQuestion === undefined){
			loginCtrl.errorMsg = 'Please enter the Secret Answer.';
		}
		
		Auth.loginSecretAnswer({
			'username' : user.username,
			'secretQuestion': loginCtrl.secretQuestion,
			'secretAnswer' : user.secretAnswer
		}).then(function (response) {
			$scope.loginSQLoading = false;
	        $scope.isAuthenticated = Principal.isAuthenticated;
	        
	        if($scope.isAuthenticated && response && !response.status){
	        	$scope.account = response;
	        	$rootScope.myrecords = [];
				var tmprecords = response.entry;
				for (var i = 0; i < tmprecords.length; i++) {
					//search for the main record and remove it from the list.
					var record = tmprecords[i].resource;
					if(record.name[0] && record.name[0].use == 'official'){						
						$rootScope.myrecord = record;
						localStorage["ihi"] = record.identifier[0].value;
						localStorage["birthDate"] = record.birthDate;
						localStorage["gender"] = record.gender;
					}else{
						$rootScope.myrecords.push(record);
					}
				}
				$state.go('app.profile', {}, { reload: true });
	        }else{
	        	loginCtrl.errorMsg = 'Failed to validate the answer. Please try again later.';
	        }
        }).catch(function (error) {
        	$scope.loginSQLoading = false;
        	if(response.status == 0) {
        		loginCtrl.errorMsg = 'Failed to connect to the server. Please try again later.';
        	}else if(error.error !== undefined){
        		loginCtrl.errorMsg = error.error;
        	}else{
        		loginCtrl.errorMsg = error;
        	}
        });
	};

	loginCtrl.loginCancel = function(user) {
		loginCtrl.errorMsg = '';
		loginCtrl.secretQuestion = null;
	};
	
	loginCtrl.logout = function() {
		Auth.logout();
		$state.go('login');
	};
}

/**
 * 
 * Pass all functions into module
 */
angular.module('myhealthWebApp').controller('LoginCtrl', LoginCtrl)
