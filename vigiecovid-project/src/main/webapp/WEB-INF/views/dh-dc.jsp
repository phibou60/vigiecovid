<%@ page contentType="text/html; charset=UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jstl/core_rt" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jstl/fmt_rt" %>

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
		<h3>Décès en millieu hospitalier au ${model.lastDayOfData}</h3>
		La France cumule <span class="nombre"><fmt:formatNumber value="${model.totalDc}" maxFractionDigits="3"/></span> décès en millieu hospitalier.<br>
		Il y a eu <span class="nombre" id="lastDc"></span> décès dans les dernières 24 heures
		et une moyenne de <span class="nombre" id="lastSemaineMoyDc"></span> décès par jour
		dans la dernières semaine.
		</div>
	</div>
	<br>

	<script>
		//Intervalle complet des datas
		var dataDateMin = "${model.dateMin}";
		var dataDateMax = "${model.dateMax}";
	</script>
		
	<%@ include file="include_period_selection.jsp"%>

</div>
<div class="container">
	<div class="row">
		<div class="col-xl tuile"><div id="chartDc"></div></div>
		<div class="col-xl tuile"><div id="chart2"></div></div>
	</div>
	<div class="row">
		<div class="col-xl tuile"><div id="chart3"></div></div>
		<div class="col-xl tuile"><div id="chart4"></div></div>
	</div>
</div>

<script>

var dh = [<c:forEach items="${model.dh}" var="entry" varStatus="loop">['${entry.key}', ${entry.value.dc}]<c:if test="${not loop.last}">,</c:if></c:forEach>];
var deltas = [<c:forEach items="${model.variations}" var="entry" varStatus="loop">['${entry.key}', ${entry.value.dc}]<c:if test="${not loop.last}">,</c:if></c:forEach>];
var serieCA = [<c:forEach items="${model.cumulClasseAges}" var="entry" varStatus="loop">${entry.value.dc}<c:if test="${not loop.last}">,</c:if></c:forEach>];
var projection = [<c:forEach items="${model.proj}" var="entry" varStatus="loop">['${entry.key}', ${entry.value}]<c:if test="${not loop.last}">,</c:if></c:forEach>];

var barChartsDeltas = tsCreateBarChartArray(deltas);
var avgDeltas = tsMoyenneMobile(deltas, 7)

document.getElementById('lastDc').innerHTML = deltas[deltas.length-1][1];
document.getElementById('lastSemaineMoyDc').innerHTML = Math.trunc(avgDeltas[avgDeltas.length-1][1]);

// Projection
var dateMinProj = "${model.dateMinProj}";
var dateMaxProj = "${model.dateMaxProj}";

var ticksClasseAges = ['0-9', '10-19', '20-29', '30-39', '40-49', '50-59', '60-69', '70-79', '80-89', '> 90'];

var resizablePlots = [];

function dessine() {

	setCookie('select_period', getPeriod(), 0);
	
	var dateMin = getPeriodStart();
	var dateMax = getPeriodEnd();
	console.log("dateMin: " + dateMin + ", dateMax: " + dateMax);

	<%@ include file="include_plot_style.jsp"%>
	
	resizablePlots = [];
	
	resizablePlots.push($.jqplot('chartDc', [selectValues(dh, dateMin, dateMax)], {
		title:'Décès cumulés (<fmt:formatNumber value="${model.totalDc}" maxFractionDigits="3"/>)', 
		cursor:standard_cursor,
		grid: standard_grid,
		axes:standard_axes,
		series:[standard_stock_series],
	}));
	
	resizablePlots.push($.jqplot("chart2", [selectValues(barChartsDeltas, dateMin, dateMax), selectValues(avgDeltas, dateMin, dateMax)], {
		title:'Décès par jour', 
		cursor:standard_cursor,
		grid: standard_grid,
		axes: standard_axes,
		series: [
			{
				renderer:$.jqplot.OHLCRenderer,
				rendererOptions:{candleStick:true},
				color: 'orange'
			},
			{
				lineWidth:2,
				markerOptions:{style:'circle', size:2},
				pointLabels: {show: false},
				label: 'Moyenne mobile sur une semaine',
				color: 'red'
			}
		],
	}));

	resizablePlots.push($.jqplot('chart3', [selectValues(barChartsDeltas, dateMinProj, dateMaxProj), projection], {
		title:'Tendance décès par jour', 
		cursor: standard_cursor,
	  seriesDefaults: standard_seriesDefaults,
	  grid: standard_grid,
		axes:{
			xaxis:{
				renderer:$.jqplot.DateAxisRenderer,
				tickOptions:{formatString:"%Y/%#m/%#d"},
				min:dateMinProj,
				max:dateMaxProj,
				drawMajorGridlines : false,
			},
			yaxis:{
				min:0,
				rendererOptions: {forceTickAt0: true},				
				tickOptions: { formatString: "%'i" }
			},
			y2axis: {
				min:0,
				rendererOptions: {
					// align the ticks on the y2 axis with the y axis.
					alignTicks: true,
					forceTickAt0: true
				},
				tickOptions: { formatString: "%'i" }
			}
		},
		series: [
      {
        renderer:$.jqplot.OHLCRenderer,
        rendererOptions:{candleStick:true},
        color: 'orange'
      },
      {
        lineWidth:2,
        markerOptions:{style:'circle', size:2},
        pointLabels: {show: false},
        color: 'navy'
      }
		]
	}));

	resizablePlots.push($.jqplot('chart4', [serieCA], {
		title:'Répartition des décès par classe d\'ages', 
		grid: standard_grid,
		seriesDefaults:{
      renderer:$.jqplot.BarRenderer,
      pointLabels: { show: true }
    },
		axes: {
			xaxis:{
        renderer: $.jqplot.CategoryAxisRenderer,
        ticks: ticksClasseAges
      },
			yaxis:{
				min:0,
				rendererOptions: {forceTickAt0: true},				
				tickOptions: { formatString: "%'i" }
			},
		},
		highlighter: { show: false }
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