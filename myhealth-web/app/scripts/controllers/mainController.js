'use strict';

/**
 * @ngdoc function
 * @name myhealthWebApp.controller:MainCtrl
 * @description
 * 
 * MainCtrl - controller
 * Contains severals global data used in different view
 *
 */
function ProfileCtrl($scope, $state, $rootScope) {
	$scope.BASE_PATH = BASE_PATH;
	
	$scope.changeProfile = function(ihi){
		localStorage["ihi"] = ihi;
		$rootScope.myrecord = undefined;
		$state.go('app.profile', {}, { reload: true });
	}
};

function MainCtrl($scope, $state, recordService, $rootScope, Principal) {
	$scope.BASE_PATH = BASE_PATH;
	
	if($rootScope.myrecord == undefined){
		//this block is used when the user refresh the page.
		Principal.identity().then(function(account) {
	        $scope.account = account;
	        $scope.isAuthenticated = Principal.isAuthenticated;
	        
	        if($scope.isAuthenticated && account && !account.status){
	        	$rootScope.myrecords = [];
				var tmprecords = account.entry;
				for (var i = 0; i < tmprecords.length; i++) {
					//search for the main record and remove it from the list.
					var record = tmprecords[i].resource;
					if(record.identifier[0] && record.identifier[0].value == localStorage["ihi"]){						
						$rootScope.myrecord = record;
						localStorage["ihi"] = record.identifier[0].value;
						localStorage["birthDate"] = record.birthDate;
						localStorage["gender"] = record.gender;
					}else{
						$rootScope.myrecords.push(record);
					}
				}
	        }
	    });
	}
}

/**
 * CalendarCtrl - Controller for Calendar
 * Store data events for calendar
 */
function CalendarCtrl($scope) {

    var date = new Date();
    var d = date.getDate();
    var m = date.getMonth();
    var y = date.getFullYear();

    // Events
    $scope.events = [
        {title: 'All Day Event',start: new Date(y, m, 1)},
        {title: 'Long Event',start: new Date(y, m, d - 5),end: new Date(y, m, d - 2)},
        {id: 999,title: 'Repeating Event',start: new Date(y, m, d - 3, 16, 0),allDay: false},
        {id: 999,title: 'Repeating Event',start: new Date(y, m, d + 4, 16, 0),allDay: false},
        {title: 'Birthday Party',start: new Date(y, m, d + 1, 19, 0),end: new Date(y, m, d + 1, 22, 30),allDay: false},
        {title: 'Click for Google',start: new Date(y, m, 28),end: new Date(y, m, 29),url: 'http://google.com/'}
    ];


    /* message on eventClick */
    $scope.alertOnEventClick = function( event, allDay, jsEvent, view ){
        $scope.alertMessage = (event.title + ': Clicked ');
    };
    /* message on Drop */
    $scope.alertOnDrop = function(event, dayDelta, minuteDelta, allDay, revertFunc, jsEvent, ui, view){
        $scope.alertMessage = (event.title +': Droped to make dayDelta ' + dayDelta);
    };
    /* message on Resize */
    $scope.alertOnResize = function(event, dayDelta, minuteDelta, revertFunc, jsEvent, ui, view ){
        $scope.alertMessage = (event.title +': Resized to make dayDelta ' + minuteDelta);
    };

    /* config object */
    $scope.uiConfig = {
        calendar:{
            height: 450,
            editable: true,
            header: {
                left: 'prev,next',
                center: 'title',
                right: 'month,agendaWeek,agendaDay'
            },
            eventClick: $scope.alertOnEventClick,
            eventDrop: $scope.alertOnDrop,
            eventResize: $scope.alertOnResize
        }
    };

    /* Event sources array */
    $scope.eventSources = [$scope.events];
}

function translateCtrl($translate, $scope) {
    $scope.changeLanguage = function (langKey) {
        $translate.use(langKey);
    };
}

/**
 * Pass all functions into module
 */
angular
    .module('myhealthWebApp')
    .controller('MainCtrl', MainCtrl)
    .controller('CalendarCtrl', CalendarCtrl)
    .controller('translateCtrl', translateCtrl)
    .controller('ProfileCtrl', ProfileCtrl)