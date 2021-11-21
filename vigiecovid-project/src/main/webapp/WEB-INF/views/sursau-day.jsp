<%@ page errorPage="error.jsp" %> 
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
<%@ include file="include_footer.jsp"%>
</body>