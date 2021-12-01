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
		<h3>Nombre de reproduction (R0)</h3>
		<p>
		Le principe est ici de calculer le nombre de personne qu'un malade contamine.<br>
		En théorie, le calcul devrait prendre en compte la période d'incubation
		et la durée de la phase contagieuse mais on a simplifié ces principes dynamiques, en 
		considérant que les malades d'une semaine ont été contaminés par les malades de la
		semaine précédente. Le R0 est alors le ratio de l'incidence d'une semaine à l'autre. 
		</p>
		<p>
		Dernières données <span class="badge badge-primary">${dateMax}</span>
		Un malade contamine <span class="badge badge-primary" id="lastRepro"></span> autres personnes.
    </p> 
    <p>
    Les cas doublent tous les <span class="badge badge-primary" id="doubleDesCas"></span> jours.
		</p>
		</div>
	</div>
	<br>

	<script>
		//Intervalle complet des datas
		var dataDateMin = "${dateMin}";
		var dataDateMax = "${dateMax}";
	</script>
		
	<%@ include file="include_period_selection.jsp"%>

	<div class="row">
		<div class="col-xl tuile"><div id="chart1" style="height:400px"></div></div>
	</div>
	
	<p>
    Voir l'article excellent de Wikipedia pour avoir plus d'explication :
    <a href="https://fr.wikipedia.org/wiki/Nombre_de_reproduction_de_base" target="_BLANK">
      https://fr.wikipedia.org/wiki/Nombre_de_reproduction_de_base
    </a>
  </p>
  	
</div>

<script>
var reproductionTestVirByWeeks = [
<c:forEach items="${reproductionTestVirByWeeks}" var="entry" varStatus="loop">['${entry.key}', ${entry.value}]<c:if test="${not loop.last}">,</c:if></c:forEach>];

let lastRepro = ${tvDernierRatio};
let lastReproS = new Intl.NumberFormat().format(lastRepro);
document.querySelector("#lastRepro").innerHTML = lastReproS;

let tvDoubleDesCas = ${tvDoubleDesCas};
let tvDoubleDesCasS = new Intl.NumberFormat().format(tvDoubleDesCas);
document.querySelector("#doubleDesCas").innerHTML = tvDoubleDesCasS;

var resizablePlots = [];

function dessine() {
	
	setCookie('select_period', getPeriod(), 0);
	
	var dateMin = getPeriodStart();
	var dateMax = getPeriodEnd();
	console.log("dateMin: " + dateMin + ", dateMax: " + dateMax);

	<%@ include file="include_plot_style.jsp"%>
	
	resizablePlots = [];
	var reproSelected = selectValues(reproductionTestVirByWeeks, dateMin, dateMax)
	
	const axes = {...standard_axes};
	axes.xaxis.min = tsAddDays(dateMin, -1);
	axes.xaxis.max = tsAddDays(dateMax, 1);
	axes.yaxis.min = 0;
	axes.yaxis.max = 3;
	axes.yaxis.tickInterval = 0.5;
	axes.yaxis.tickOptions = { formatString: '%.2f' }
					
	resizablePlots.push($.jqplot("chart1", [reproSelected], {
		cursor: standard_cursor,
		grid: standard_grid,
		axes: axes,
		seriesDefaults: standard_seriesDefaults,
		series: [standard_line_series],
	  canvasOverlay: {
	    show: true,
	    objects: [
	      {
	    	  horizontalLine: {
		         name: 'Limite',
	           y: 1.0,
	           lineWidth: 5,
	           color: 'red',
	           shadow: false
		      }
	      }],		
	  }
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