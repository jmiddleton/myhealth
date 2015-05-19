function config($translateProvider) {
	
	// Initialize angular-translate
    $translateProvider.useLoader('$translatePartialLoader', {
        urlTemplate: 'i18n/{lang}/{part}.json'
    });

	$translateProvider.preferredLanguage('en');
	$translateProvider.useCookieStorage();
}

angular.module('myhealthWebApp').config(config)
