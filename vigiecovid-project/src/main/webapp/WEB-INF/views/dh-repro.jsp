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
		<h3>Taux de reproduction (R0)</h3>
		<p>
		Le principe est simplement de faire le ratio de cas d'une semaine à l'autre.<br>
		Ici, le calcul se fait à partir des chiffres d'incidence.
		</p>
		<p>
		Données du <span class="badge badge-primary">${dateMax}</span>
		Taux de reproduction actuel :
		<span class="badge badge-primary" id="lastRepro"></span>
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
</div>

<script>
var reproductionTestVirByWeeks = [
<c:forEach items="${reproductionTestVirByWeeks}" var="entry" varStatus="loop">['${entry.key}', ${entry.value}]<c:if test="${not loop.last}">,</c:if></c:forEach>];

let lastRepro = reproductionTestVirByWeeks[reproductionTestVirByWeeks.length-1][1];
let lastReproS = new Intl.NumberFormat().format(lastRepro);
document.querySelector("#lastRepro").innerHTML =lastReproS;

var resizablePlots = [];

function dessine() {
	
	setCookie('select_period', getPeriod(), 0);
	
	var dateMin = getPeriodStart();
	var dateMax = getPeriodEnd();
	console.log("dateMin: " + dateMin + ", dateMax: " + dateMax);

	<%@ include file="include_plot_style.jsp"%>
	
	resizablePlots = [];
	var reproSelected = selectValues(reproductionTestVirByWeeks, dateMin, dateMax)
	
	resizablePlots.push($.jqplot("chart1", [reproSelected], {
		cursor: {zoom:true, looseZoom: true},
		grid: standard_grid,
		axes:{
			xaxis:{
				renderer: $.jqplot.DateAxisRenderer,
				tickOptions: {formatString: "%Y/%#m/%#d"},
		    pad: 1.01,
				drawMajorGridlines : false,
			},
			yaxis:{
				min: 0,
				max: 3,
				drawMajorGridlines: true,
			  drawMinorGridlines: false,
				tickInterval: 0.5,
				rendererOptions: {forceTickAt0: true},
				tickOptions: { formatString: '%.2f' }
			}
		},
		series: [
			{
				lineWidth:2,
				markerOptions:{style:'circle', size:2},
				pointLabels: {show: false},
				label: 'A partir des tests positifs',
				pointLabels: {
			    show: true
				}
			}
		],
	  canvasOverlay: {
	    show: true,
	    objects: [
	      {
	    	  horizontalLine: {
		         name: 'Limite',
	           y: 1.0,
	           lineWidth: 6,
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