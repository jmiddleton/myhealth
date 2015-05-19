'use strict';

var app = angular.module('viewService', [ 'ngResource' ]);

app.factory('viewService', function($q, $http) {
	var viewService = {};
	
	viewService.getView = function(queryString, unit) {
		var deferred = $q.defer();
		var regexDate = /(\d{4})(\d{2})(\d{2})(\d{2})(\d{2})/;

		$http.get(BASE_PATH + '/Observation/_search', { params : queryString, cache : true }).then(function(result) {
			if (result.status == "200") {
				var data = result.data;
				if (data) {
					var entries = data.entry; 
					var sortedEntries = _.sortBy(entries, function(entry) {
						var dateCollected = new Date(entry.resource.issued.replace(/(\d{4})-(\d{2})-(\d{2})T(\d{2}):(\d{2}):(\d{2})/, '$1-$2-$3T$4:$5:$6'));
					    return dateCollected;
					});
					
					deferred.resolve({ isError:false, rows: sortedEntries });
				} else {
					deferred.resolve(handleError(result));
				}
			} else {
				var error = new Object();
				error.message = "Error contacting the server. Please try again.";
				error.title = "Error";
				deferred.resolve({ isError : true,
				error : error
				});
			}
		}, function(errorResponse) {
			deferred.resolve(handleError(errorResponse));
		});
		return deferred.promise;
	};
	
	viewService.getGrowthChartReferenceData = function(refDataFilename) {
		var deferred = $q.defer();
		
		var referenceUrl= "/reference-data/" + refDataFilename + ".xml";

		$http.get(referenceUrl, { cache : true }).then(function(result) {
			if (result.status == "200") {
				var data = result.data;					
				deferred.resolve({ isError:false, xml: data });
			} else {
				var error = new Object();
				error.message = "Error contacting the server. Please try again.";
				error.title = "Error";
				deferred.resolve({ isError : true,
				error : error
				});
			}
		}, function(errorResponse) {
			deferred.resolve(handleError(errorResponse));
		});
		return deferred.promise;
	};
	return viewService;
});

function handleError(errorResponse) {
	var error = new Object();
	if (errorResponse.status == "500") {
		error.message = "Error contacting the server. Please try again.";
		error.title = "Server Error";
	} else if (errorResponse.status == 401){
		error.message = "Unauthorized to access the server. Please log in first.";
		error.title = "Unauthorized";
	} else if (errorResponse.status == 404){
		error.message = "Resource not found. Please try a different url.";
		error.title = "Resource not found";
	} else {
		//error.message = errorResponse.data.detail;
		error.message = "Unknown error. Please try later.";
		error.title = "Error";
	}
	return { isError : true,
	error : error
	};
}