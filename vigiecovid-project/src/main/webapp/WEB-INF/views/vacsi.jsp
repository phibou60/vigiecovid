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

var vaccinJour = [<c:forEach items="${franceByDay}" var="entry" varStatus="loop">['${entry.key}', ${entry.value.dose1 + entry.value.complet + entry.value.rappel}]<c:if test="${not loop.last}">,</c:if></c:forEach>];
var cumComplet = [<c:forEach items="${franceByDay}" var="entry" varStatus="loop">['${entry.key}', ${entry.value.cumComplet}]<c:if test="${not loop.last}">,</c:if></c:forEach>];
var cumDose1 = [<c:forEach items="${franceByDay}" var="entry" varStatus="loop">['${entry.key}', ${entry.value.cumDose1}]<c:if test="${not loop.last}">,</c:if></c:forEach>];
var cumRappel = [<c:forEach items="${franceByDay}" var="entry" varStatus="loop">['${entry.key}', ${entry.value.cumRappel}]<c:if test="${not loop.last}">,</c:if></c:forEach>];

// Intervalle complet des datas
var dateMin = "${dateMin}";
var dateMax = "${dateMax}";

var resizablePlots = [];

function dessine() {

	<%@ include file="include_plot_style.jsp"%>
	
	resizablePlots = [];
	
	const seriesDefaults = {...standard_seriesDefaults};
	seriesDefaults.lineWidth = 1;
  seriesDefaults.markerOptions = {style: 'circle', size: 1};
	
	resizablePlots.push($.jqplot('chart1', [cumComplet, cumDose1, cumRappel], {
		title:'Cumul Vaccination',
		legend: standard_legend,
		cursor:standard_cursor,
		grid: standard_grid,
		axes:standard_axes,
		seriesDefaults: seriesDefaults,
		series: [
			{label: 'Vaccination complête'},
			{label: 'Première dose'},
		  {label: 'Rappel'}
		],
	}));
	
	resizablePlots.push($.jqplot("chart2", [tsCreateBarChartArray(vaccinJour)], {
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
<%@ include file="include_footer.jsp"%>
</body>
