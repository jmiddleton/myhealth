'use strict';

/**
 * DocumentCtrl - controller Contains severals methods to work with documents
 * 
 */
function DocumentCtrl($state, $scope, documentService) {
	$scope.BASE_PATH = BASE_PATH;
	var documentCtrl = this;

	this.findDocumentsByType = function(type) {
		documentService.getDocumentList(localStorage["ihi"], type).then(
				function(result) {
					if (result.isError == true) {
						$scope.error = result.error;
					} else {
						documentCtrl.documentResult = result.documents;
						$scope.error = null;
					}
				});
	}

	if (localStorage["ihi"]) {
		this.findDocumentsByType($state.current.data.type);
	}
};

function NotesCtrl($state, $scope, documentService) {
	$scope.BASE_PATH = BASE_PATH;
	var notesCtrl = this;
	
	this.findNotes = function() {
		documentService.getDocumentList(localStorage["ihi"], '100.16681').then(
				function(result) {
					if (result.isError == true) {
						$scope.error = result.error;
					} else {
						notesCtrl.documentResult = result.documents;
						$scope.error = null;
					}
				});
	}

	if (localStorage["ihi"]) {
		notesCtrl.findNotes();
	}
};

function TimelineCtrl($scope, documentService) {
	$scope.BASE_PATH = BASE_PATH;

	this.loadTimelineDocuments = function() {
		documentService.getDocumentList(localStorage["ihi"], '').then(function(result) {
			if (result.isError == true) {
				$scope.error = result.error;
			} else {
				$scope.timelineList = _.first(result.documents, 5);
				$scope.error = null;
			}
		});
	}

	this.getPatientId = function() {
		return localStorage["ihi"];
	}

	if (localStorage["ihi"]) {
		this.loadTimelineDocuments();
	}
};

function PHSCtrl($scope, documentService) {
	$scope.BASE_PATH = BASE_PATH;

	this.getPHS = function() {
		documentService.getDocumentList(localStorage["ihi"], '100.16685').then(
				function(result) {
					if (result.isError == true) {
						$scope.error = result.error;
					} else {
						var tmpPHS = result.documents[0].resource;
						$scope.phsmetadata= tmpPHS;

						documentService.getDocument('100.16685_v1.0',
								localStorage["ihi"],
								tmpPHS.identifier[0].value,
								tmpPHS.masterIdentifier.value).then(
								function(result) {
									if (result.isError == true) {
										$scope.error = result.error;
									} else {
										$scope.phsDocument = result.document;
										$scope.error = null;
									}
								});
						}
				});
	}

	if (localStorage["ihi"]) {
		this.getPHS();
	}
};

function ViewDocumentCtrl($scope, $stateParams, $sce, documentService) {
	$scope.BASE_PATH = BASE_PATH;
	var viewCtrl = this;

	this.openCdaDocument = function(repoId, docId, title) {
		// load the external html
		var html = $sce.trustAsResourceUrl($scope.BASE_PATH
				+ '/Binary/?repoId=' + repoId + "&docId=" + docId + '&patient='
				+ localStorage['ihi']);

		viewCtrl.document = {
			title : title,
			content : html,
			active : 'true'
		};
	}

	this.openCdaDocument($stateParams.repoId, $stateParams.docId, $stateParams.title);
}

function DocumentSearchCtrl($scope, $state, $sce, documentService,
		DTOptionsBuilder) {
	$scope.BASE_PATH = BASE_PATH;
	var searchCtrl = this;

	searchCtrl.tabs = [];
	searchCtrl.persons = [];
	searchCtrl.dtOptions = DTOptionsBuilder.newOptions().withPaginationType(
			'full_numbers').withDisplayLength(9);

	this.findDocumentsByType = function(type) {
		documentService.getDocumentList(localStorage["ihi"], type).then(
			function(result) {
				if (result.isError == true) {
					$scope.error = result.error;
				} else {
					searchCtrl.documentResult = result.documents;
				}
			});
	}

	this.selectDocument = function(repoId, docId, title) {
		var found = false;

		for (var i = 0; i < searchCtrl.tabs.length; i++) {
			if (searchCtrl.tabs[i].title == title) {
				searchCtrl.tabs[i].active = 'true';
				found = true;
				break;
			}
		}

		// load the external html
		var html = $sce.trustAsResourceUrl($scope.BASE_PATH
				+ '/Binary/?repoId=' + repoId + "&docId=" + docId + '&patient='
				+ localStorage['ihi']);

		if (found) {
			searchCtrl.tabs[i] = {
				title : title,
				content : html,
				active : 'true'
			};
		} else {
			searchCtrl.tabs.push({
				title : title,
				content : html,
				active : 'true'
			});
		}
	}

	if (localStorage["ihi"]) {
		this.findDocumentsByType($state.current.data.type);
	}
}

/**
 * 
 * Pass all functions into module
 */
angular.module('myhealthWebApp').controller('DocumentCtrl', DocumentCtrl)
		.controller('TimelineCtrl', TimelineCtrl)
		.controller('PHSCtrl', PHSCtrl).controller('DocumentSearchCtrl',
				DocumentSearchCtrl).controller('NotesCtrl', NotesCtrl)
		.controller('ViewDocumentCtrl', ViewDocumentCtrl)
