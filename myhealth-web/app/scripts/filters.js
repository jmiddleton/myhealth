var months = [ '', 'Jan', 'Feb', 'Mar', 'Apr', 'May', 'Jun', 'Jul', 'Aug', 'Sep', 'Oct', 'Nov', 'Dec' ];
var one_day = 1000 * 60 * 60 * 24;

var filtersModule = angular.module('FiltersModule', []);

filtersModule.filter('strtodate', function() {
	return function(input) {
		var newDate = "";
		if (input) {
			var dateLength = input.length;

			// Have days
			if (dateLength > 7) {
				newDate = newDate + input.substring(6, 8) + '-';
			}
			// Have months
			if (dateLength > 5) {
				newDate = newDate + months[(input.substring(4, 6) * 1)] + '-';
			}
			// Have years
			if (dateLength > 3) {
				newDate = newDate + input.substring(0, 4);
			}

			return new Date(newDate);
		}
		return null;
	};
});

filtersModule.filter('monthsBetween', function() {
	return function(input, dateOfBirth) {
		if (input && dateOfBirth) {
			// Convert both dates to milliseconds
			var date1_ms = input.getTime();
			var date2_ms = dateOfBirth;
			if (angular.isDate(dateOfBirth)) {
				date2_ms = dateOfBirth.getTime();
			}

			// Calculate the difference in milliseconds
			var difference_ms = date2_ms - date1_ms;

			// Convert back to days and return
			return (-1) * Math.round(difference_ms / one_day / 30);
		}
		return;
	};
});

filtersModule.filter('timeAgo', function() {
	return function(input) {
		if (input) {
			var date1 = new Date(input);
			var date2 = new Date();
			// Convert both dates to milliseconds
			var timeDiff = Math.abs(date2.getTime() - date1.getTime());
			var diffHours = Math.ceil(timeDiff / (1000 * 3600 * 24));
			
			if(diffHours <= 24){
				diffHours = Math.ceil(timeDiff / (1000 * 3600));
			}
			
			// Convert back to days and return
			return diffHours + ' ' + (diffHours <= 24 ? 'hours' : 'days');
		}
		return;
	};
});