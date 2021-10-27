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
		<h3>Bilan des hospitalisation au ${model.lastDayOfData}</h3>
		Il y a actuellement <span class="nombre"><fmt:formatNumber value="${model.dernierHosp}" maxFractionDigits="3"/></span> patients hospitalisés.<br>
		En 24h, il y a eu <span class="nombre" id="lastAdmissions"></span> admissions
		et un bilan de <span class="nombre" id="solde"></span>  patients en tenant compte des sorties.<br>
		En une semaine, il y a eu en moyenne <span class="nombre" id="avgLastAdmissions"></span> hospitalisations par jour
		et un bilan de <span class="nombre" id="avgSolde"></span> patients en tenant compte des sorties.
		</div>
	</div>
	<br>
	<label for="select_period">Selectionner une période:</label>
	<select id="select_period" onChange="javascript:redessine();">
		<option value="1" selected="x">Tout</option>
		<option value="2">Deuxième vague</option>
		<option value="3">Troisième vague</option>
	</select>
	<div class="row">
		<div class="col-xl tuile"><div id="chart1"></div></div>
		<div class="col-xl tuile"><div id="chart2"></div></div>
	</div>
	<br>
	<div class="row">
		<div class="col-xl tuile"><div id="chart3"></div></div>
		<div class="col-xl tuile"><div id="chart4"></div></div>
	</div>
	<br>
	<div class="row">
		<div class="col-xl tuile"><div id="chart5"></div></div>
		<div class="col-xl"></div>
	</div>
</div>

<script>

var dh = [];
<c:forEach items="${model.dh}" var="entry">
	dh.push(['${entry.key}', ${entry.value.hosp}]);
</c:forEach>
var barCharts = tsCreateBarChartArray(dh);
var avgs = tsMoyenneMobile(dh, 7)

var deltas = tsDeltaValues(dh);
var barChartsDeltas = tsCreateBarChartArray(deltas);
var avgDeltas = tsMoyenneMobile(deltas, 7)

var nouveaux = [];
<c:forEach items="${model.nouveaux}" var="entry">
	nouveaux.push(['${entry.key}', ${entry.value.hosp}]);
</c:forEach>
var barChartsNouveaux = tsCreateBarChartArray(nouveaux);
var avgNouveaux = tsMoyenneMobile(nouveaux, 7)

document.getElementById('lastAdmissions').innerHTML = nouveaux[nouveaux.length-1][1];
document.getElementById('solde').innerHTML = deltas[deltas.length-1][1];
document.getElementById('avgLastAdmissions').innerHTML = Math.trunc(avgNouveaux[avgNouveaux.length-1][1]);
document.getElementById('avgSolde').innerHTML = Math.trunc(avgDeltas[avgDeltas.length-1][1]);

var serieCA = [];
<c:forEach items="${model.cumulClasseAges}" var="entry">
	serieCA.push(${entry.value.hosp});
</c:forEach>

var projection = [];
<c:forEach items="${model.proj2}" var="entry">
	projection.push(['${entry.key}', ${entry.value}]);
</c:forEach>

// Intervalle complet des datas
var dataDateMin = "${model.dateMin}";
var dataDateMax = "${model.dateMax}";
// Projection
var dateMinProj = "${model.dateMinProj}";
var dateMaxProj = "${model.dateMaxProj}";

var ticksClasseAges = ['0-9', '10-19', '20-29', '30-39', '40-49', '50-59', '60-69', '70-79', '80-89', '> 90'];

var resizablePlots = [];

function dessine() {
	
	var value = $('#select_period').val();
	setCookie('select_period', value, 0);
	
	// Intervalle affiché:
	var dateMin = dataDateMin;
	var dateMax = dataDateMax;

	if (value == "2") {
		dateMin = secondeVague;
	}

	if (value == "3") {
		dateMin = troisiemeVague;
	}

	<%@ include file="include_plot_style.jsp"%>
	
	resizablePlots = [];
	
	resizablePlots.push($.jqplot('chart1', [selectValues(dh, dateMin, dateMax)], {
		title:'Hospitalisations', 
		cursor:standard_cursor,
		grid: standard_grid,
		axes:standard_axes,
		series:[standard_stock_series],
	}));
	
	resizablePlots.push($.jqplot("chart2", [selectValues(barChartsNouveaux, dateMin, dateMax), selectValues(avgNouveaux, dateMin, dateMax)], {
		title:'Nouvelles Admissions', 
		cursor:{zoom:true, looseZoom: true},
		grid: standard_grid,
		axes:standard_axes,
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
	
	//----------------------------------------------------------
	var bilan = {
		title:'Bilan avec les sorties', 
		cursor:standard_cursor,
		grid: standard_grid,
		axes:standard_axes,
		legend: standard_legend,
		series: [
			{
				renderer:$.jqplot.OHLCRenderer,
				rendererOptions:{candleStick:true},
				label: 'Variation des hospitalisations',
				color: 'lightskyblue'
			},
			{
				lineWidth:1,
				markerOptions:{style:'circle', size:1},
				pointLabels: {show: false},
				label: 'Moyenne mobile sur une semaine',
				color: 'navy'
			}
		],
	};
	delete bilan.axes.yaxis.min;
	delete bilan.axes.y2axis.min;
	resizablePlots.push($.jqplot("chart3", [selectValues(barChartsDeltas, dateMin, dateMax), selectValues(avgDeltas, dateMin, dateMax)], bilan));
	
	resizablePlots.push($.jqplot('chart4', [selectValues(dh, dateMinProj, dateMaxProj), projection], {
		title:'Tendance', 
		cursor:standard_cursor,
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
		seriesDefaults: standard_seriesDefaults,
		series:[standard_stock_series, standard_line_series],
		grid: standard_grid
	}));

	resizablePlots.push($.jqplot('chart5', [serieCA], {
		title:'Hospitalisations actuelles par classe d\'ages', 
		grid: standard_grid,
		seriesDefaults:{
            renderer:$.jqplot.BarRenderer,
            pointLabels: { show: true }
        },
		axes: {
			xaxis: {
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
</body>