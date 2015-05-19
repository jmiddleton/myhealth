'use strict';

/**
 * ChartCtrl - controller Contains severals methods to work with growth charts
 * 
 */
var visibleChartsByRefData = [ [ 'CDC0-36', 'Height', 'Weight', 'HeadCircumference' ], 
                               [ 'CDC2-20', 'Height', 'Weight', 'BMI' ], 
                               [ 'WHO0-2', 'Height', 'BMI' ],
                               [ 'WHO2-5', 'Height' ],
                               [ 'WHO0-5', 'Weight', 'HeadCircumference' ] ];

function ChartCtrl($scope, documentService, viewService, xmlFilter) {
	$scope.BASE_PATH = BASE_PATH;

	var dateOfBirth = new Date(localStorage['birthDate'].replace(/(\d{4})-(\d{2})-(\d{2})/, '$1-$2-$3'));

	var chartCtrl = this;
	chartCtrl.refData = 'CDC';
	chartCtrl.period = '0-36';
	chartCtrl.isTableVisible = false;

	chartCtrl.refreshCharts = function() {
		if (chartCtrl.refData == 'CDC' && (chartCtrl.period == '0-2' || chartCtrl.period == '0-5')) {
			chartCtrl.period = '0-36';
		} else if (chartCtrl.refData == 'WHO' && (chartCtrl.period == '0-36' || chartCtrl.period == '2-20')) {
			chartCtrl.period = '0-2';
		}

		//all the charts are deleted before creating again.
		$('#growthChart').empty();
		if (chartCtrl.growthCharts) {
			for (var i = 0; i < chartCtrl.growthCharts.length; i++) {
				chartCtrl.growthCharts[i].destroy();
			}
		}

		chartCtrl.growthCharts = [];
		chartCtrl.tableRows = [];
		
		for (var i = 0; i < visibleChartsByRefData.length; i++) {
			var chartType = (chartCtrl.refData + chartCtrl.period);
			if (visibleChartsByRefData[i][0] == chartType) {
				var row = visibleChartsByRefData[i];
				for (var j = 1; j < row.length; j++) {
					var type = row[j];
					if (chartType == 'CDC0-36' && type == 'Height') {
						type = 'Length';
					}
					chartCtrl.drawChart(chartCtrl.refData, type, chartCtrl.period, chartCtrl);
				}
			}
		}
	}

	chartCtrl.showHideTable = function() {
		chartCtrl.isTableVisible = !chartCtrl.isTableVisible;
	}

	chartCtrl.drawChart = function(refData, type, period, chartCtrl) {
		var renderToDivId = $('<div class="col-lg-6"><div class="ibox float-e-margins"><div class="ibox-content"><div></div></div></div></div>').appendTo('#growthChart')[0];
		
		// default setting for reference data
		Highcharts.theme = {
			chart: {
	            zoomType: 'x'
	        },
			legend : {
				enabled : false
			},
			tooltip : {
				enabled : true,
				crosshairs : {
					color : 'green',
					dashStyle : 'solid',
					width : 2
				}
			}
		};
		Highcharts.setOptions(Highcharts.theme);

		// get the reference data filename
		var referenceUrl = getReferenceDataUrl(refData, localStorage["gender"], type, period);
		var params = getObservationParams(refData, localStorage["gender"], type);

		var refDataInterval = 12;
		if (refData == 'WHO' || type == 'HEADCIRCUMFERENCE') {
			refDataInterval = 1;
		}

		if (period == '0-36') {
			refDataInterval = 1;
		}

		if (period == '0-5') {
			refDataInterval = 2;
		}

		// get the reference data
		viewService.getGrowthChartReferenceData(referenceUrl).then(
				function(result) {
					if (result.isError == true) {
						// TODO: error
					} else {
						var xmlDoc = xmlFilter(result.xml);

						var unit = xmlDoc.find('unit').text();
						var ageUnit = xmlDoc.find('ageUnit').text();
						var minAge = parseInt(xmlDoc.find('minAge').text());
						var maxAge = parseInt(xmlDoc.find('maxAge').text());
						var subtitle = xmlDoc.find('title').text();
						var title = xmlDoc.find('type').text() + ' (' + unit + ')';

						var options = {
							chart : {
								renderTo : renderToDivId,
								type : 'arearange'
							},
							title : {
								text : title
							},
							subtitle : {
								text : subtitle
							},
							xAxis : {
								categories : [],
								tickInterval : refDataInterval,
								minorTickInterval : 5,
								gridLineWidth : 1,
								title : {
									text : ageUnit
								}
							},
							yAxis : {
								tickInterval : 5,
								minorTickInterval : 5,
								title : {
									text : unit
								}
							},
							series : []
						};

						xmlDoc.find('referenceData percentile').each(function(j, percentile) {
							var percentileOptions = {
								name : $(percentile).attr('displayValue'),
								color : getColorRefDataType(type),
								enableMouseTracking : false,
								lineWidth : 1,
								data : []
							};

							$(percentile).find('item').each(function(z, item) {
								var value = parseFloat($(item).attr('value'));
								var point = parseInt($(item).attr('age'));
								var base = 0;
								if (j > 0) {
									base = options.series[j - 1].data[z][2];
								} else {
									base = value;
								}

								// TODO: point/12 permite poner las X en
								// anios en vez de meses
								percentileOptions.data.push([ point, base, value ]);
							});
							options.series.push(percentileOptions);
						});

						// initialise chart's config
						var chart = new Highcharts.Chart(options);
						chartCtrl.growthCharts.push(chart);

						// /////////// load line points ///////////////
						var percentileOptions = {
							name : 'Reference',
							color : '#336699',
							data : [],
							marker : {
								enabled : true
							},
							tooltip : {
								shared : true,
								useHTML : true,
								headerFormat : '<table>',
								pointFormat : '<tr><td style="color: blue">{point.x} ' + ageUnit + '</td>' + '<td style="text-align: right"><b> {point.y} ' + unit
										+ '</b><br/></td></tr>' + '<tr><td rowspan="2"><span style="font-size: 10px">Data collected: {point.dateCollected}</span></td></tr>',
								footerFormat : '</table>',
								valueDecimals : 2
							},
							type : 'spline',
							point : {
								events : {
									mouseOver : function() {
										var charts = chartCtrl.growthCharts;
										for (var int = 0; int < charts.length; int++) {
											if (charts[int] && charts[int].series[0]) {
												var index = charts[int].series.length - 1;
												var data = charts[int].series[index].data;
												for (var j = 0; j < data.length; j++) {
													if (data[j].category == this.x) {
														data[j].select();
														charts[int].tooltip.refresh(data[j]);
														break;
													}
												}
											}
										}
									}
								}
							}
						};

						viewService.getView(params, unit).then(function(result) {
							if (result.isError == true) {
								$scope.error = result.error;
								checkError(result);
							} else {
								var rows = result.rows;
								// In case of BMI, the server
								// sends height,weight and bmi
								// observation so we filter only
								// the one we need to draw bmi
								// chart
								if (type == 'BMI') {
									rows = _.filter(rows, function(row) {
										return row.resource.valueQuantity.code == '60621009';
									});
								}

								// we store the rows to render
								// the measurement in a table

								$.each(rows, function(i, row) {
									chartCtrl.tableRows.push(row);
									var point = row.resource;
									var dateCollected = new Date(point.issued.replace(/(\d{4})-(\d{2})-(\d{2})T(\d{2}):(\d{2}):(\d{2})/, '$1-$2-$3T$4:$5:$6'));
									var x = monthsBetween(dateOfBirth, dateCollected);

									if (point.valueQuantity && point.valueQuantity.value) {
										if (x >= minAge && x <= maxAge) {
											var color = '#660033';
											if (point.subject.reference == 1) {
												color = '#336699';
											}

											var point = {
												x : x,
												y : point.valueQuantity.value,
												marker : {
													fillColor : color
												},
												type : (point.subject.reference == 1 ? 'Consumer' : 'Provider'),
												dateCollected : Highcharts.dateFormat('%d/%m/%Y', dateCollected)
											};
											percentileOptions.data.push(point);
										}
									}
								});

								chartCtrl.tableRows = _.sortBy(chartCtrl.tableRows, function(entry) {
									return entry.resource.issued;
								});
								chart.addSeries(percentileOptions, true);
							}
						});

						// ///////////////////////////////////////////
					}
				});
	};

	// initial charts
	chartCtrl.refreshCharts();

};

function getReferenceDataUrl(refData, gender, type, period) {
	var today = new Date();

	if (gender == 'male') {
		gender = 'Male';
	} else {
		gender = 'Female';
	}

	return (refData + gender + type + period);
}
function getObservationParams(refData, gender, type) {
	var today = new Date();

	if (gender == 'male') {
		gender = 'Male';
	} else {
		gender = 'Female';
	}

	var obsType = type.indexOf('Weight') >= 0 ? 'WEIGHT' : type.indexOf('Height') >= 0 || type.indexOf('Length') >= 0 ? 'HEIGHT' : type.indexOf('BMI') >= 0 ? 'BMI' : type
			.indexOf('Head') >= 0 ? 'HEADCIRCUMFERENCE' : '';

	var month = (parseInt(today.getMonth()) + 1);
	month = month < 10 ? '0' + month : month;
	var day = (today.getDate() < 10 ? '0' + today.getDate() : today.getDate());

	var params = {
		fromDate : '1910-04-10',
		toDate : today.getFullYear() + '-' + month + '-' + day,
		observationType : obsType,
		referenceData : refData,
		ihi : localStorage['ihi'],
		code: '100.16872_v1.0' //view type
	};

	return params;
}

function getColorRefDataType(type) {
	if (type == 'Height' || type == 'Length') {
		return '#5cb6d2';
	} else if (type == 'Weight') {
		return '#dda750';
	} else if (type == 'BMI') {
		return '#FFCC33';
	} else {
		return '#a387d4';
	}
}

function monthsBetween(date1, date2) {
	var one_day = 1000 * 60 * 60 * 24;

	var date1_ms = date1.getTime();
	var date2_ms = date2.getTime();

	var difference_ms = date2_ms - date1_ms;

	return Math.round(difference_ms / one_day / 30);
};

function checkError(result) {
	console.log("Error: " + result)
	if (result.error.message == 'User not logged.') {
		window.location = 'index.html';
	}
}

/**
 * 
 * Pass all functions into module
 */
angular.module('myhealthWebApp').controller('ChartCtrl', ChartCtrl)
