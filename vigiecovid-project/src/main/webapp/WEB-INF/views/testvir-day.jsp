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

	<h2>Tests Virologiques et taux d'incidence par jour
	<c:choose>
		<c:when test="${not empty param.dep}">
			pour le département ${param.dep} -  ${param.lib}</span><br>
		</c:when>
		<c:otherwise>
			pour la métropole
		</c:otherwise>
	</c:choose>
	au ${model.lastDayOfData}
	</h2>
	<p>
		Il y a eu <span class="nombre" id="lastPositifs"></span> cas positifs dans les dernières 24h.
		<br>
		Sur une semaine, il y a en moyenne <span class="nombre" id="lastAvgPositifsSemaine"></span>
		cas positifs par jour.<br>
		L'incidence est actuellement de <span class="nombre" id="lastIncid"></span>
		(cas positifs par semaine pour 100.000 personnes)</span>
    <br>
		Sur la dernière semaine, il y a eu <span class="nombre" id="lastTestsSemaine"></span>
		tests effectués et <span class="nombre" id="lastPositifsSemaine"></span> personnes positives.
	</p>

	<script>
		//Intervalle complet des datas
		var dataDateMin = "${model.dateMin}";
		var dataDateMax = "${model.dateMax}";
	</script>
		
	<%@ include file="include_period_selection.jsp"%>
			
</div>
<div class="container">
	<div class="row">
		<div class="col-xl tuile"><div id="chartIncid"></div></div>
		<div class="col-xl tuile"><div id="chartProj"></div></div>
	</div>
	<div class="row">
		<div class="col-xl tuile"><div id="chartPos"></div></div>
		<div class="col-xl tuile"><div id="chartReal"></div></div>
	</div>
  <div class="row">
    <div class="col-xl tuile"><div id="chart4"></div></div>
    <div class="col-xl tuile"><div id="chart5"></div></div>
  </div>
</div>

<script>

var tests = [<c:forEach items="${model.byDays}" var="entry" varStatus="loop">['${entry.key}', ${entry.value.tests}]<c:if test="${not loop.last}">,</c:if></c:forEach>];
var positifs = [<c:forEach items="${model.byDays}" var="entry" varStatus="loop">['${entry.key}', ${entry.value.positifs}]<c:if test="${not loop.last}">,</c:if></c:forEach>];
var pc = [<c:forEach items="${model.byDays}" var="entry" varStatus="loop">['${entry.key}', ${entry.value.pc}]<c:if test="${not loop.last}">,</c:if></c:forEach>];

var avgPositifs = [<c:forEach items="${model.byWeeks}" var="entry" varStatus="loop">['${entry.key}', ${entry.value.positifs}/7]<c:if test="${not loop.last}">,</c:if></c:forEach>];
var testsSemaine = [<c:forEach items="${model.byWeeks}" var="entry" varStatus="loop">['${entry.key}', ${entry.value.tests}]<c:if test="${not loop.last}">,</c:if></c:forEach>];
var incidences = [<c:forEach items="${model.incidences}" var="entry" varStatus="loop">['${entry.key}', ${entry.value}]<c:if test="${not loop.last}">,</c:if></c:forEach>];
var projection = [<c:forEach items="${model.proj}" var="entry" varStatus="loop">['${entry.key}', ${entry.value}]<c:if test="${not loop.last}">,</c:if></c:forEach>];

var testsCharts = tsCreateBarChartArray(tests);
var positifsCharts = tsCreateBarChartArray(positifs);

document.getElementById('lastPositifs').innerHTML = formatInteger(positifs[positifs.length-1][1]);
document.getElementById('lastIncid').innerHTML = formatInteger(Math.round(incidences[incidences.length-1][1]));
document.getElementById('lastAvgPositifsSemaine').innerHTML = formatInteger(Math.round(avgPositifs[avgPositifs.length-1][1]));
document.getElementById('lastTestsSemaine').innerHTML = formatInteger(testsSemaine[testsSemaine.length-1][1]);
document.getElementById('lastPositifsSemaine').innerHTML = formatInteger(${model.byWeeks[model.lastDayOfData].positifs});

//Projection
var dateMinProj = projection[0][0];
var dateMaxProj = projection[projection.length - 1][0];

var resizablePlots = [];

function dessine() {

	setCookie('select_period', getPeriod(), 0);
	
	var dateMin = getPeriodStart();
	var dateMax = getPeriodEnd();
	
	<%@ include file="include_plot_style.jsp"%>

	resizablePlots = [];
	
	resizablePlots.push($.jqplot("chartIncid", [selectValues(incidences, dateMin, dateMax)], {
		title:'Incidence', 
		cursor:standard_cursor,
		grid: standard_grid,
		axes: standard_axes,
		seriesDefaults: standard_seriesDefaults,
		series: [
			{
				lineWidth:2,
				markerOptions:{style:'circle', size:2},
				pointLabels: {show: false},
				color: 'navy'
			}
		],
	}));
	
	resizablePlots.push($.jqplot("chartReal", [selectValues(testsCharts, dateMin, dateMax), selectValues(pc, dateMin, dateMax)], {
		title:'Tests quotidien et % de retours positifs', 
		cursor:standard_cursor,
		grid: standard_grid,
		seriesDefaults: standard_seriesDefaults,
		axes: {
			xaxis:{
				renderer:$.jqplot.DateAxisRenderer,
				tickOptions:{formatString:"%Y/%#m/%#d"},
				min:dateMin,
				max:dateMax,
				drawMajorGridlines : false,
			},
			yaxis: {
				min:0,
				rendererOptions: {forceTickAt0: true},
				tickOptions: { formatString: "%'i" }
			},
			y2axis:{
				min:0,
				tickOptions:{suffix: '%'},
				rendererOptions: {
					// align the ticks on the y2 axis with the y axis.
					alignTicks: true,
					forceTickAt0: true
				}
			}
		},
		series: [
			{
				renderer:$.jqplot.OHLCRenderer,
				rendererOptions:{candleStick:true},
				pointLabels: {show: false},
				label: 'Tests réalisés'
			},
			{
				lineWidth:2,
				markerOptions:{style:'circle', size:2},
				pointLabels: {show: false},
				label: '% retours positifs',
				yaxis: 'y2axis',
				color: 'orange'
			}
		],
	}));	
	
	resizablePlots.push($.jqplot("chartPos", [selectValues(positifsCharts, dateMin, dateMax), selectValues(avgPositifs, dateMin, dateMax)], {
		title:'Cas positifs par jour', 
		cursor:standard_cursor,
		grid: standard_grid,
		axes: standard_axes,
		seriesDefaults: standard_seriesDefaults,
		series: [
			{
				renderer:$.jqplot.OHLCRenderer,
				rendererOptions:{candleStick:true},
				pointLabels: {show: false},
				label: 'Retours positifs',
				color: 'orange'
			},
			{
				lineWidth:2,
				markerOptions:{style:'circle', size:2},
				pointLabels: {show: false},
				label: 'Moyenne dernière semaine',
				color: 'red'
			}
		],
	}));

	resizablePlots.push($.jqplot('chart4', [testsSemaine], {
		title:'Tests par semaine', 
		cursor:standard_cursor,
		axes:standard_axes,
		seriesDefaults: standard_seriesDefaults,
		series:[standard_line_series],
		grid: standard_grid
	}));
	
	let projFrom = new Date(tsAddDays(dateMax, -62)).toISOString().split('T')[0];
	
  resizablePlots.push($.jqplot('chartProj',
		  [selectValues(incidences, projFrom, dateMax), projection], {
    title:'Tendance', 
    cursor:standard_cursor,
    axes:{
      xaxis:{
        renderer:$.jqplot.DateAxisRenderer,
        tickOptions:{formatString:"%Y/%#m/%#d"},
      //  min:dateMinProj,
        max:dateMaxProj
      },
      yaxis:{
        min:0,
        rendererOptions: {forceTickAt0: true},        
        tickOptions: { formatString: "%'i" }
      }
    },
    seriesDefaults: standard_seriesDefaults,
    series:[standard_line_series, standard_line_series],
    grid: standard_grid
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
})

</script>
<%@ include file="include_footer.jsp"%>
</body>