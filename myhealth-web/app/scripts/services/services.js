'use strict';

var app = angular.module('mipatojoServices', [ 'ngResource' ]);

app.factory('recordService', function($q, $http) {
	var recordService = {};

	recordService.getRecords = function() {
		// create your deferred promise.
		var deferred = $q.defer();

		$http.get(BASE_PATH + '/records', { cache : true
		}).then(function(result) {
			if (result && result.status == "200") {
				var data = result.data;
				if (data && data.entry) {
					deferred.resolve({ isError : false,
					data : data.entry
					});
				} else {
					var error = new Object();
					error.message = "Error contacting the authentication server";
					error.title = "Error";
					deferred.resolve({ isError : true,
					error : error
					});
				}
			} else {
				var error = new Object();
				error.message = "Error contacting the authentication server";
				error.title = "Error";
				deferred.resolve({ isError : true,
				error : error
				});
			}
		});
		return deferred.promise;
	};
	
	recordService.getRecord = function(ihi) {
		// create your deferred promise.
		var deferred = $q.defer();

		$http.get(BASE_PATH + '/records', { cache : true
		}).then(function(result) {
			if (result.status == "200") {
				var data = result.data;
				if (data && data.entry) {
					var record = _.find(data.entry, function(r) {
						return r.resource.patient.reference == 'Patient/'+ihi;
					});
					
					deferred.resolve({ isError:false, record:record });
				} else {
					var error = new Object();
					error.message = "Error contacting the authentication server";
					error.title = "Error";
					deferred.resolve({ isError : true,
					error : error
					});
				}
			} else {
				var error = new Object();
				error.message = "Error contacting the authentication server";
				error.title = "Error";
				deferred.resolve({ isError : true,
				error : error
				});
			}
		});
		return deferred.promise;
	};

	return recordService;
});

app.factory('documentService', function($q, $http) {
	var documentService = {};

	documentService.getDocument = function(docType, ihi, repoId, docId) {
		// create your deferred promise.
		var deferred = $q.defer();
		
		$http.get(BASE_PATH + '/ClinicalDocument/', { params : { _format: 'json', repoId: repoId, docId : docId, patient: ihi, type: docType }}, { cache : true
		}).then(
				function(result) {
					if(result === undefined){
						var error = new Object();
						error.message = "No information available.";
						error.title = "Warning";
						deferred.resolve({ isError : true, error : error});
					}
					if (result.status == "200") {
						var data = result.data;
						if (data) {
							deferred.resolve({ isError : false, document : data });
						} else {
							var error = new Object();
							error.message = "No information available.";
							error.title = "Warning";
							deferred.resolve({ isError : true, error : error});
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
	
	documentService.getDocumentList = function(ihi, type) {
		var deferred = $q.defer();

		$http.get(BASE_PATH + '/DocumentReference/',{ params : { _format: 'json', type: type, patient: ihi }}, { cache : true
		}).then(
				function(result) {
					if(result === undefined){
						var error = new Object();
						error.message = "No information available.";
						error.title = "Warning";
						deferred.resolve({ isError : true, error : error});
					}
					if (result.status == "200") {
						var data = result.data;
						if (data && data.entry) {
							deferred.resolve({ isError : false, documents : data.entry });
						} else {
							var error = new Object();
							error.message = "No information available.";
							error.title = "Warning";
							deferred.resolve({ isError : true, error : error});
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

	return documentService;
});

app.factory('notesService', function($q, $http) {
					var notesService = {};

					notesService.createNote = function(ihi, note) {
						var deferred = $q.defer();
						$http.post(BASE_PATH + '/notes/add/' + ihi, note).success(function(result) {
							if (result.status == "200") {
								console.log('grabo nota');
							}
							console.log('error: ' + result.detail);
							deferred.reject(result);
						}).error(function(error) {
							console.log(error);
						});
						return deferred.promise;
					};

					notesService.getNotes = function(ihi) {
						var deferred = $q.defer();

						$http
								.get(BASE_PATH + '/documents/phn/' + ihi, { cache : true
								})
								.then(
										function(result) {
											if (result.status == "200") {
												var data = result.data;
												if (data && data.AdhocQueryResponse && data.AdhocQueryResponse.RegistryObjectList
														&& data.AdhocQueryResponse.RegistryObjectList.ExtrinsicObject) {

													if (_.isArray(data.AdhocQueryResponse.RegistryObjectList.ExtrinsicObject)) {
														var notes = _
																.map(
																		data.AdhocQueryResponse.RegistryObjectList.ExtrinsicObject,
																		function(note) {
																			var docId = note.ExternalIdentifier[1].value;
																			var repoId = note.Slot[3].ValueList.Value;
																			var deferred = $q.defer();

																			$http
																					.get(BASE_PATH + '/document/' + ihi, { cache : true,
																					params : { documentId : docId,
																					documentRepo : repoId
																					}
																					})
																					.then(
																							function(result) {
																								if (result.status == "200") {
																									var data = result.data;
																									if (data
																											&& data.RetrieveDocumentSetResponse
																											&& data.RetrieveDocumentSetResponse.DocumentResponse.Document) {
																										var cda = data.RetrieveDocumentSetResponse.DocumentResponse.Document.ClinicalDocument;
																										var text = cda.component.structuredBody.component[1].section.text;
																										var title = cda.component.structuredBody.component[1].section.title;
																										var datetime = cda.effectiveTime.value;
																										var note = {
																											documentId : docId,
																											repositoryId : repoId,
																											text : text,
																											title : title,
																											datetime : datetime
																										};
																										deferred.resolve(note);
																									}
																								}
																							});
																			return deferred.promise;
																		});
														deferred.resolve({ isError : false,
														data : notes
														});
													} else {
														/*
														 * var docId =
														 * data.AdhocQueryResponse.RegistryObjectList.ExtrinsicObject.ExternalIdentifier[1].value;
														 * var repoId =
														 * data.AdhocQueryResponse.RegistryObjectList.ExtrinsicObject.Slot[3].ValueList.Value;
														 * 
														 * if (data &&
														 * data.RetrieveDocumentSetResponse) {
														 * var cda =
														 * data.DocumentResponse.Document.ClinicalDocument;
														 * var text=
														 * cda.component.structuredBody.component[1].section.text;
														 * var title=
														 * cda.component.structuredBody.component[1].section.title;
														 * 
														 * var notes={};
														 * notes.push({documentId:
														 * docId, repositoryId:
														 * repoId, text: text,
														 * title: title});
														 * deferred.resolve({isError:false,
														 * data:notes}); }
														 */
													}
												} else {
													deferred.resolve(handleError(result));
												}
											}
										}, function(errorResponse) {
											deferred.resolve(handleError(errorResponse));
										});

						return deferred.promise;
					};

					return notesService;
				});

app
		.factory(
				'questService',
				function($q, $http) {
					var questService = {};

					questService.getQuestionList = function(ihi) {
						var deferred = $q.defer();

						$http
								.get(BASE_PATH + '/documents/cpq/' + ihi, { cache : true
								})
								.then(
										function(result) {
											if (result.status == "200") {
												var data = result.data;
												if (data && data.AdhocQueryResponse && data.AdhocQueryResponse.RegistryObjectList
														&& data.AdhocQueryResponse.RegistryObjectList.ExtrinsicObject) {

													if (_.isArray(data.AdhocQueryResponse.RegistryObjectList.ExtrinsicObject)) {
														var questionList = _
																.map(
																		data.AdhocQueryResponse.RegistryObjectList.ExtrinsicObject,
																		function(quest) {
																			var docId = quest.ExternalIdentifier[1].value;
																			var status = quest.ExternalIdentifier[1].status == 'urn:oasis:names:tc:ebxml-regrep:StatusType:Submitted' ? 'Completed'
																					: 'Not Started';
																			var repoId = quest.Slot[3].ValueList.Value;
																			var name = quest.Name.LocalizedString.value;
																			var datetime = '' + quest.Slot[0].ValueList.Value;
																			var author = '';

																			var classif = _
																					.find(
																							quest.Classification,
																							function(cls) {
																								if (cls.classificationScheme == 'urn:uuid:93606bcf-9494-43ec-9b4e-a7748d1a838d') {
																									return true;
																								}
																								;
																								return false;
																							});

																			if (_.isArray(classif.Slot)) {
																				author = classif.Slot[1].ValueList.Value;
																				author = author.split('^')[1] + ' '
																						+ author.split('^')[2];
																			} else {
																				author = 'Unknown';
																			}

																			return { documentId : docId,
																			repositoryId : repoId,
																			name : name,
																			datetime : datetime,
																			status : status,
																			author : author
																			};
																		});
														deferred.resolve({ isError : false,
														data : questionList
														});
													}
												} else {
													deferred.resolve(handleError(result));
												}
											} else {
												deferred.resolve(handleError(result));
											}
										}, function(errorResponse) {
											deferred.resolve(handleError(errorResponse));
										});

						return deferred.promise;
					};

					questService.getQuestionDetail = function(ihi, docId, repoId) {
						var deferred = $q.defer();

						$http
								.get(BASE_PATH + '/document/' + ihi, { cache : true,
								params : { documentId : docId,
								documentRepo : repoId
								}
								})
								.then(
										function(result) {
											if (result.status == "200") {
												var data = result.data;
												if (data && data.RetrieveDocumentSetResponse
														&& data.RetrieveDocumentSetResponse.DocumentResponse.Document) {
													var cda = data.RetrieveDocumentSetResponse.DocumentResponse.Document.ClinicalDocument;
													var section = cda.component.structuredBody.component[1].section;
													var title = section.code.displayName;

													var questions = _
															.map(
																	section.entry.organizer.component,
																	function(question) {
																		var qid = question.observation.code.code;
																		var q = question.observation.code.displayName;
																		var code = question.observation.entryRelationship.observation.code.code;
																		var type = question.observation.entryRelationship.observation.code.displayName;
																		var value = question.observation.entryRelationship.observation.value;

																		var answer = '';
																		if (value) {
																			if (code == 3) {
																				answer = value.content;
																			} else {
																				answer = value.displayName;
																			}
																		}

																		return { id: qid, question : q,
																		answer : answer,
																		type : type,
																		code : code
																		};
																	});
													var question = { title : title,
													questions : questions
													};
													deferred.resolve(question);
												}
											} else {
												deferred.resolve(handleError(result));
											}
										}, function(errorResponse) {
											deferred.resolve(handleError(errorResponse));
										});

						return deferred.promise;
					};

					return questService;
				});