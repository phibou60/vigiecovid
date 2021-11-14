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
		Le principe est simplement de faire le ratio de cas d'une semaine à l'autre. 
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
		<div class="col-xl tuile"><div id="chart1"></div></div>
	</div>
</div>

<script>
var reproductionTestVirByWeeks = [<
	c:forEach items="${reproductionTestVirByWeeks}" var="entry" varStatus="loop"
		>['${entry.key}', ${entry.value}]<c:if test="${not loop.last}">,</c:if
	></c:forEach>];

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
		title:'A partir des tests positifs', 
		cursor:{zoom:true, looseZoom: true},
		grid: standard_grid,
		axes:{
			xaxis:{
				renderer:$.jqplot.DateAxisRenderer,
				tickOptions:{formatString:"%Y/%#m/%#d"},
				min:dateMin,
				max:dateMax,
				drawMajorGridlines : false,
			},
			yaxis:{
				min:0,
				max:4,
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
				color: 'red'
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