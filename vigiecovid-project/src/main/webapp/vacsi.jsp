<%@ page errorPage="error.jsp" %> 
<%@ page contentType="text/html; charset=UTF-8" %>
<%@ page import="java.util.*, java.io.*, org.apache.log4j.Logger" %>
<%@ page import="java.time.LocalDate" %>
<%@ page import="org.apache.commons.math3.stat.StatUtils" %>
<%@ page import="org.apache.commons.math3.stat.regression.SimpleRegression" %>
<%@ page import="org.apache.commons.math3.fitting.PolynomialCurveFitter" %>
<%@ page import="org.apache.commons.math3.fitting.WeightedObservedPoint" %>
<%@ page import="org.apache.commons.math3.analysis.polynomials.PolynomialFunction" %>
<%@ page import="vigiecovid.domain.vacsi.VacsiDAO" %>
<%@ page import="vigiecovid.domain.vacsi.Vacsi" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jstl/core_rt" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jstl/fmt_rt" %>

<%
Logger logger = Logger.getLogger(this.getClass());
ajoutPassage(request.getServletContext(), "vacsi");

/////////////////////////////////////////////////////////////
//////////////////// Début calcul modele ////////////////////
/////////////////////////////////////////////////////////////

Map<String, Object> model = new HashMap<>();
request.setAttribute("model", model);
{
	VacsiDAO vacsiDAO = new VacsiDAO(request.getServletContext());
	TreeMap<LocalDate, Vacsi> franceByDay= vacsiDAO.getVacsiFranceByDay();

	LocalDate lastDayOfData = franceByDay.lastKey();
	LocalDate dateMin = franceByDay.firstKey().minusDays(1);
	LocalDate dateMax = lastDayOfData.plusDays(1);

	//---- Alimentation du modèle

	model.put("lastDayOfData", lastDayOfData);

	model.put("dateMin", dateMin);
	model.put("dateMax", dateMax);

	model.put("franceByDay", franceByDay);
}
/////////////////////////////////////////////////////////////
////////////////////  Fin calcul modele  ////////////////////
/////////////////////////////////////////////////////////////
%>

<!doctype html>
<html lang="fr">
<head>
	<%@ include file="include_head.jsp"%>
</head>
<body>
<%@ include file="include_top.jsp"%>

<div class="container">
	<div class="row">
		<div class="col-xl">
		  <h3>Bilan des vaccinations au ${model.lastDayOfData}</h3>
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
<c:forEach items="${model.franceByDay}" var="entry">
    vaccinJour.push(['${entry.key}', ${entry.value.dose1}+${entry.value.complet}]);
    cumComplet.push(['${entry.key}', ${entry.value.cumComplet}]);
    cumDose1.push(['${entry.key}', ${entry.value.cumDose1}]);
</c:forEach>

var barCharts = tsCreateBarChartArray(vaccinJour);

// Intervalle complet des datas
var dateMin = "${model.dateMin}";
var dateMax = "${model.dateMax}";

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
<%@ include file="include_trt_passages.jsp"%>