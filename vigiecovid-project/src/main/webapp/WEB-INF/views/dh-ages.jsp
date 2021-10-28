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
		<h3>Analyses par tranches d'ages</h3>
		</div>
	</div>
	<br>

	<script>
		//Intervalle complet des datas
		var dataDateMin = "${model.dataDateMin}";
		var dataDateMax = "${model.dataDateMax}";
	</script>
		
	<%@ include file="include_period_selection.jsp"%>

	<div class="row">
		<h3 style="margin-top:20px;">Décès</h3>
	</div>
	<div class="row">
		<div id="chartd0" class="col-sm" style="height:200px"></div>
		<div id="chartd1" class="col-sm" style="height:200px"></div>
		<div id="chartd2" class="col-sm" style="height:200px"></div>
		<div id="chartd3" class="col-sm" style="height:200px"></div>
		<div id="chartd4" class="col-sm" style="height:200px"></div>
	</div>
	<div class="row">
		<div id="chartd5" class="col-sm" style="height:200px"></div>
		<div id="chartd6" class="col-sm" style="height:200px"></div>
		<div id="chartd7" class="col-sm" style="height:200px"></div>
		<div id="chartd8" class="col-sm" style="height:200px"></div>
		<div id="chartd9" class="col-sm" style="height:200px"></div>
	</div>
	<div class="row">
		<h3 style="margin-top:20px;">Hospitalisation</h3>
	</div>
	<div class="row">
		<div id="charth0" class="col-sm" style="height:200px"></div>
		<div id="charth1" class="col-sm" style="height:200px"></div>
		<div id="charth2" class="col-sm" style="height:200px"></div>
		<div id="charth3" class="col-sm" style="height:200px"></div>
		<div id="charth4" class="col-sm" style="height:200px"></div>
	</div>
	<div class="row">
		<div id="charth5" class="col-sm" style="height:200px"></div>
		<div id="charth6" class="col-sm" style="height:200px"></div>
		<div id="charth7" class="col-sm" style="height:200px"></div>
		<div id="charth8" class="col-sm" style="height:200px"></div>
		<div id="charth9" class="col-sm" style="height:200px"></div>
	</div>
	
	<div class="row">
		<h3 style="margin-top:20px;">Réanimations</h3>
	</div>
	<div class="row">
		<div id="chartr0" class="col-sm" style="height:200px"></div>
		<div id="chartr1" class="col-sm" style="height:200px"></div>
		<div id="chartr2" class="col-sm" style="height:200px"></div>
		<div id="chartr3" class="col-sm" style="height:200px"></div>
		<div id="chartr4" class="col-sm" style="height:200px"></div>
	</div>
	<div class="row">
		<div id="chartr5" class="col-sm" style="height:200px"></div>
		<div id="chartr6" class="col-sm" style="height:200px"></div>
		<div id="chartr7" class="col-sm" style="height:200px"></div>
		<div id="chartr8" class="col-sm" style="height:200px"></div>
		<div id="chartr9" class="col-sm" style="height:200px"></div>
	</div>

</div>
<script>

console.log("debut script: "+new Date());

var ticksClasseAges = ['0-9', '10-19', '20-29', '30-39', '40-49', '50-59', '60-69', '70-79', '80-89', '> 90'];

var resizablePlots = [];

//---- Classes d'age
var db = {};

//Récup des données
//-----------------
//Le tableau db sera de la forme :
//{
//'2021-01-01' : [{dc: 1, hosp:2, rea: 3}, {dc: 10, hosp:20, rea: 30}, ...],
//'2021-01-02' : [{dc: 1, hosp:2, rea: 3}, {dc: 10, hosp:20, rea: 30}, ...],
//...
//}

<c:forEach items="${model.cumulParDatesEtClasseAges}" var="entry">
	var a = [];
	<c:forEach items="${entry.value}" var="values">
		a.push({dc: ${values.dc}, hosp: ${values.hosp}, rea: ${values.rea}});
	</c:forEach>
	db['${entry.key}'] = a;
</c:forEach>

function dessine() {
	
	setCookie('select_period', getPeriod(), 0);
	
	var dateMin = getPeriodStart();
	var dateMax = getPeriodEnd();
	console.log("dateMin: " + dateMin + ", dateMax: " + dateMax);
	
	<%@ include file="include_plot_style.jsp"%>
	
	var select_cumul = $('#select_cumul').val();
	
	resizablePlots = [];

	// Faire le cumul dc, hosp et réa par tranches d'age
	// Déclarations de tableau et init
	
	var dcAges = [10]; var hospAges = [10]; var reaAges = [10];
	for (var i=0; i<10; i++) {
		dcAges[i] = [];
		hospAges[i] = [];
		reaAges[i] = [];
	}
	
	var maxHosp = -1;
	var maxRea = -1;
	
	for (jour in db) { // Boucle sur les jours
		if (jour >= dateMin && jour <= dateMax) {
			var cumuls = db[jour];
			for (var i=1; i<11; i++) { // Boucle sur les tranches d'age
				dcAges[i-1].push([jour, cumuls[i].dc]);
				hospAges[i-1].push([jour, cumuls[i].hosp]);
				reaAges[i-1].push([jour, cumuls[i].rea]);
				
				if (cumuls[i].hosp > maxHosp) maxHosp = cumuls[i].hosp;
				if (cumuls[i].rea > maxRea)   maxRea = cumuls[i].rea;
			}
		}
	}
	
	maxHosp = tsArronditAuDessus(maxHosp);
	maxRea = tsArronditAuDessus(maxRea);
	
	// Calcul du delta de dc
	var dcDeltaAges = [10]; var dcMoyAges = [10];
	var maxDc = -1; 
	for (var i=0; i<10; i++) {
		dcDeltaAges[i] = tsDeltaValues(dcAges[i]);
		var max = tsHighWaterMark(dcDeltaAges[i]);
		if (max > maxDc) maxDc = max;
		dcMoyAges[i] = tsMoyenneMobile(dcDeltaAges[i], 7);
	}
	
	maxDc = tsArronditAuDessus(maxDc);
		
	// Affichage des 3 blocs de 10 tranches d'age
	for (var i=0; i<10; i++) {
		resizablePlots.push($.jqplot("chartd"+i, [dcDeltaAges[i], dcMoyAges[i]], {
			title:ticksClasseAges[i],
			cursor:{zoom:true, looseZoom: true},
			grid: standard_grid,
			axes: {xaxis: standard_axes.xaxis, yaxis:{min:0, max: maxDc, rendererOptions: {forceTickAt0: true}}},
			series: [
				{lineWidth:1, markerOptions:{style:'circle', size:0}},
				{lineWidth:1, markerOptions:{style:'circle', size:0}}
			]
		}));
		resizablePlots.push($.jqplot("chartr"+i, [reaAges[i]], {
			title:ticksClasseAges[i],
			cursor:{zoom:true, looseZoom: true},
			grid: standard_grid,
			axes: {xaxis: standard_axes.xaxis, yaxis:{min:0, max: maxRea, rendererOptions: {forceTickAt0: true}}},
			series: [{lineWidth:1, markerOptions:{style:'circle', size:0}}]
		}));
		resizablePlots.push($.jqplot("charth"+i, [hospAges[i]], {
			title:ticksClasseAges[i],
			cursor:{zoom:true, looseZoom: true},
			grid: standard_grid,
			axes: {xaxis: standard_axes.xaxis, yaxis:{min:0, max: maxHosp, rendererOptions: {forceTickAt0: true}}},
			series: [{lineWidth:1, markerOptions:{style:'circle', size:0}}]
		}));
		console.log("Ajout age ("+i+") : "+new Date());
	}

	console.log("Fin dessine: "+new Date());

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
