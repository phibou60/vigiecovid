<%@ page errorPage="error.jsp" %> 
<%@ page contentType="text/html; charset=UTF-8" %>
<%@ page import="java.util.*, java.io.*, org.apache.log4j.Logger" %>
<%@ page import="java.time.LocalDate" %>
<%@ page import="vigiecovid.domain.sursaud.Sursaud" %>
<%@ page import="vigiecovid.domain.sursaud.SursaudsDAO" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jstl/core_rt" %>

<%
Logger logger = Logger.getLogger(this.getClass());
ajoutPassage(request.getServletContext(), "sursauday");

/////////////////////////////////////////////////////////////
//////////////////// Début calcul modele ////////////////////
/////////////////////////////////////////////////////////////

Map<String, Object> model = new HashMap<>();
request.setAttribute("model", model);
{
	String dep = request.getParameter("dep");
	String lib = request.getParameter("lib");
	
	SursaudsDAO sursaudsDAO = new SursaudsDAO(request.getServletContext());

	TreeMap<LocalDate, Sursaud> cumul = sursaudsDAO.cumulSursaudByDay(dep);
	
	TreeMap<String, Map> cumulByDates = new TreeMap<>(); 
	
	for (Map.Entry<LocalDate, Sursaud> entry : cumul.entrySet()) {
		Map<String, Object> element = new HashMap<>();
		Sursaud sursaud = entry.getValue();
		
		double pc_pass_corona = 0.0d;
		double pc_hospit_corona = 0.0d;
		if (sursaud.getNbrePassTot() > 0) {
			pc_pass_corona = 100.0d * sursaud.getNbrePassCorona() / sursaud.getNbrePassTot();
			pc_hospit_corona = 100.0d * sursaud.getNbreHospitCorona() / sursaud.getNbrePassTot();
		}
		element.put("pc_pass_corona", pc_pass_corona);
		element.put("pc_hospit_corona", pc_hospit_corona);
		
		double pc_acte_corona = 0.0d;
		if (sursaud.getNbreActeTot() > 0) {
			pc_acte_corona = 100.0d * sursaud.getNbreActeCorona() / sursaud.getNbreActeTot();
		}
		element.put("pc_acte_corona", pc_acte_corona);
		
		cumulByDates.put(entry.getKey().toString(), element);
	}
	
	//---- Alimentation du modèle

	model.put("cumulByDates", cumulByDates);
	model.put("lastDayOfData", cumul.lastKey().toString());
	model.put("dataDateMin", LocalDate.parse(cumul.firstKey().toString()).minusDays(1));
	model.put("dataDateMax", LocalDate.parse(cumul.lastKey().toString()).plusDays(1));
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
	<h2>Données des urgences hospitalières et de SOS médecins par jour
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

	<script>
		//Intervalle complet des datas
		var dataDateMin = "${model.dataDateMin}";
		var dataDateMax = "${model.dataDateMax}";
	</script>
		
	<%@ include file="include_period_selection.jsp"%>
	
</div>
<br>
<div class="container">
	<div class="row">
		<div class="col-xl tuile"><div id="chart_pc_pass_corona"></div></div>
		<div class="col-xl tuile"><div id="chart_pc_hospit_corona"></div></div>
	</div>
	<br>
	<div class="row">
		<div class="col-xl tuile"><div id="chart_pc_acte_corona"></div></div>
		<div class="col-xl"><div id="chart4"></div></div>
	</div>
</div>	
<script>

var resizablePlots = [];

var line_pc_pass_corona = [];
var line_pc_hospit_corona = [];
var line_pc_acte_corona = [];

<c:forEach items="${model.cumulByDates}" var="entry">		
	line_pc_pass_corona.push(['${entry.key}', ${entry.value.pc_pass_corona}]);
	line_pc_hospit_corona.push(['${entry.key}', ${entry.value.pc_hospit_corona}]);
	line_pc_acte_corona.push(['${entry.key}', ${entry.value.pc_acte_corona}]);
</c:forEach>

function dessine() {

	setCookie('select_period', getPeriod(), 0);
	
	var dateMin = getPeriodStart();
	var dateMax = getPeriodEnd();
	console.log("dateMin: " + dateMin + ", dateMax: " + dateMax);
	
	<%@ include file="include_plot_style.jsp"%>

	resizablePlots = [];

	resizablePlots.push($.jqplot('chart_pc_pass_corona', [line_pc_pass_corona], {
		title:'% passages aux urgences pour suspicion de Covid-19', 
		axes:standard_axes,
		series:[standard_line_series],
		grid: standard_grid
	}));

	resizablePlots.push($.jqplot('chart_pc_hospit_corona', [line_pc_hospit_corona], {
		title:'% hospitalisations pour Covid-19 suite passage aux urgences', 
		axes:standard_axes,
		series:[standard_line_series],
		grid: standard_grid
	}));

	resizablePlots.push($.jqplot('chart_pc_acte_corona', [line_pc_acte_corona], {
		title:'% actes SOS Médecins pour suspicion de Covid-19', 
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
	dessine();
})

</script>
</body>
<%@ include file="include_trt_passages.jsp"%>