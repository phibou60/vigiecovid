<%@ page contentType="text/html; charset=UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jstl/core_rt" %>
<!doctype html>

<html lang="fr">
<head>
	<%@ include file="include_head.jsp"%>
</head>
<body>
<%@ include file="include_menu.jsp"%>

<div class="container">
	<div class="row">
		<div class="col-xl">
		  <h3>Bilan des vaccinations au ${lastDayOfData}</h3>
		</div>
	</div>
	<br>
	<div class="row">
		<div class="col-xl tuile"><div id="chart1"></div></div>
		<div class="col-xl tuile"><div id="chart2"></div></div>
	</div>
</div>

<script>

var vaccinJour = [];
var cumComplet = [];
var cumDose1 = [];
<c:forEach items="${franceByDay}" var="entry">
    vaccinJour.push(['${entry.key}', ${entry.value.dose1}+${entry.value.complet}]);
    cumComplet.push(['${entry.key}', ${entry.value.cumComplet}]);
    cumDose1.push(['${entry.key}', ${entry.value.cumDose1}]);
</c:forEach>

var barCharts = tsCreateBarChartArray(vaccinJour);

// Intervalle complet des datas
var dateMin = "${dateMin}";
var dateMax = "${dateMax}";

var resizablePlots = [];

function dessine() {

	<%@ include file="include_plot_style.jsp"%>
	
	resizablePlots = [];
	
	resizablePlots.push($.jqplot('chart1', [cumComplet, cumDose1], {
		title:'Total Vaccination',
		legend: standard_legend,
		cursor:standard_cursor,
		grid: standard_grid,
		axes:standard_axes,
		seriesDefaults: {
			shadow: false,
			markerOptions:{shadow: false},
			lineWidth:2,
			markerOptions:{style:'circle', size:2},
			pointLabels: {show: false}
		},
		series: [
			{
				label: 'Vaccination complête',
				color: 'blue'
			},
			{
				lineWidth:2,
				label: 'Première dose',
				color: 'navy'
			}
		],
	}));
	
	resizablePlots.push($.jqplot("chart2", [barCharts], {
		title:'Vaccinations par jour', 
		cursor:standard_cursor,
		grid: standard_grid,
		axes:standard_axes,
		series: [
			{
				renderer:$.jqplot.OHLCRenderer,
				rendererOptions:{candleStick:true},
				color: 'orange'
			}
		],
	}));

}

var redessineTimer;
	
function redessine() {
	resizablePlots.forEach(plot => plot.destroy( { resetAxes: true } ));
	dessine();
}

$(window).resize(function() {
	clearTimeout(redessineTimer);
	redessineTimer = setTimeout(function(){ redessine()}, 500);
});

$(document).ready(function(){
	var value = getCookie('select_period');
	if (value.length > 0) {
		$('#select_period').val(value);
	}
	dessine();
});

</script>
</body>
