<%@ page errorPage="error.jsp" %> 
<%@ page contentType="text/html; charset=UTF-8" %>
<%@ page import="java.util.*, java.io.*, org.apache.log4j.Logger" %>
<%@ page import="java.time.LocalDate" %>
<%@ page import="org.apache.commons.math3.stat.StatUtils" %>
<%@ page import="vigiecovid.domain.TestVir" %>
<%@ page import="chamette.datasets.Datasets" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jstl/core_rt" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jstl/fmt_rt" %>

<%
Logger logger = Logger.getLogger(this.getClass());
ajoutPassage(request.getServletContext(), "testvirday");

/////////////////////////////////////////////////////////////
//////////////////// Début calcul modele ////////////////////
/////////////////////////////////////////////////////////////

Map<String, Object> model = new HashMap<>();
request.setAttribute("model", model);
{
	String dep = request.getParameter("dep");
	String lib = request.getParameter("lib");

	TreeMap<LocalDate, TestVir> cumul = TestVir.cumulTestVirByDay(application, dep, true);
	LocalDate lastDayOfData = cumul.lastKey();
	LocalDate dateMin = cumul.firstKey().minusDays(1);
	LocalDate dateMax = lastDayOfData.plusDays(1);

	long population = 67000000l;

	if (dep != null) {
		Datasets datasets = (Datasets) application.getAttribute("datasets");

		HashMap<String, HashMap> departements = (HashMap<String, HashMap>) datasets.get("departements").getData();
		if (departements.get(dep) != null) {
			HashMap<String, Object> departement = (HashMap<String, Object>) departements.get(dep);
			population = (long) departement.get("PTOT");
		}
	}
	
	//---- Alimentation du modèle

	model.put("cumul", cumul);
	model.put("lastDayOfData", lastDayOfData);
	model.put("dateMin", dateMin);
	model.put("dateMax", dateMax);
	model.put("population", population);
}

/////////////////////////////////////////////////////////////
////////////////////  Fin calcul modele  ////////////////////
/////////////////////////////////////////////////////////////
%>

<!doctype html>
<html lang="fr">
<head>
	<%@ include file="include_head.jsp"%>
	<script src="scripts/jqplot/plugins/jqplot.ohlcRenderer.js"></script>
</head>
<body>
<%@ include file="include_top.jsp"%>

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
	Il y a eu <span class="nombre" id="lastPositifs"></span> cas positifs dans les dernières 24h.<br>
	Sur une semaine, il y a en moyenne <span class="nombre" id="lastPositifsSemaine"></span> cas positifs par jour.<br>
	L'incidence est actuellement de <span class="nombre" id="lastIncid"> (cas positifs par semaine pour 100.000 personnes)</span><br>
	<br>	
	Sur la dernière semaine, il y a eu <span class="nombre" id="lastTestsSemaine"></span> tests effectués.<br>
	Pour rappel : le gouvernement avait prévu de pouvoir faire 700.000 tests par semaine.
	<br>
	<br>		
	<label for="select_period">Selectionner une période:</label>
	<select id="select_period" onChange="javascript:redessine();">
		<option value="1" selected="x">Tout</option>
		<option value="2">Deuxième vague</option>
		<option value="3">Troisième vague</option>
	</select>
</div>
<div class="container">
	<div class="row">
		<div class="col-xl tuile"><div id="chartIncid"></div></div>
		<div class="col-xl tuile"><div id="chartPos"></div></div>
	</div>
	<div class="row">
		<div class="col-xl tuile"><div id="chartReal"></div></div>
		<div class="col-xl tuile"><div id="chart4"></div></div>
	</div>
</div>

<script>

var tests = [];
var positifs = [];

<c:forEach items="${model.cumul}" var="entry">
	tests.push(['${entry.key}', ${entry.value.tests}]);
	positifs.push(['${entry.key}', ${entry.value.positifs}]);
</c:forEach>

var pc = [];
for (i = 0; i < tests.length; i++) {
	pc.push([tests[i][0], positifs[i][1]*100/tests[i][1]]);
}
	
var testsCharts = tsCreateBarChartArray(tests);
var positifsCharts = tsCreateBarChartArray(positifs);

var population = ${model.population};
var incid = tsSomme(positifs, 7, population/100000);
var avgPositifs = tsMoyenneMobile(positifs, 7);
var testsSemaine = tsSomme(tests, 7);

document.getElementById('lastPositifs').innerHTML = new Intl.NumberFormat().format(positifs[positifs.length-1][1]);
document.getElementById('lastIncid').innerHTML = new Intl.NumberFormat().format(Math.round(incid[incid.length-1][1]));
document.getElementById('lastPositifsSemaine').innerHTML = new Intl.NumberFormat().format(Math.round(avgPositifs[avgPositifs.length-1][1]));
document.getElementById('lastTestsSemaine').innerHTML = new Intl.NumberFormat().format(testsSemaine[testsSemaine.length-1][1]);

//Intervalle complet des datas
var dataDateMin = "${model.dateMin}";
var dataDateMax = "${model.dateMax}";

var resizablePlots = [];

function dessine() {

	var value = $('#select_period').val();
	setCookie('select_period', value, 0);
	
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
	
	resizablePlots.push($.jqplot("chartIncid", [selectValues(incid, dateMin, dateMax)], {
		title:'Incidence', 
		cursor:standard_cursor,
		grid: standard_grid,
		axes: standard_axes,
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
		series:[standard_line_series],
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
</body>
<%@ include file="include_trt_passages.jsp"%>