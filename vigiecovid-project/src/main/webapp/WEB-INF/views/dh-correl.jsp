<%@ page contentType="text/html; charset=UTF-8" %>

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
			<h3>Corrélation <span id="correlMsg"></span></h3>
			Meilleure corrélation obtenue avec <span style="font-weight: bold;" id="decallMsg"></span>.
			<br>
			<br>
			<form action="">
			<label for="correlId">Nouvelle corrélation:</label>
			<select id="correlId" name="correlId" onChange="javascript:this.form.submit();">
				<option value="" selected="x">Sélectionner une corrélation ...</option>
				<option value="total_hosp_rea">Total hospitalisations/Réanimations</option>
				<option value="adm_hosp_rea">Admissions hospitalisations/Réanimations</option>
			</select>
			</form>
			
		</div>
	</div>
	<br>
	<div class="row">
		<div class="col-xl tuile"><div id="chart1"></div></div>
	</div>
	<br>
	<div class="row">
		<div class="col-xl tuile"><div id="chart2"></div></div>
	</div>
</div>
<script>

var model = ${model};

document.getElementById('correlMsg').innerHTML = model.correlMsg;
document.getElementById('decallMsg').innerHTML = model.decallMsg;

//Intervalle complet des datas
var dateMin = model.dateMin;
var dateMax = model.dateMax;

<%@ include file="include_plot_style.jsp"%>

function dessine() {
	
	resizablePlots = [];
	
	resizablePlots.push($.jqplot('chart1', [model.line1, model.line2], {
		title:'Hospitalisations vs Réanimations', 
		axes:standard_axes,
		grid: standard_grid,
		cursor: standard_cursor,
		legend: standard_legend,
		series: [
			{
				lineWidth:2,
				markerOptions:{style:'circle', size:2},
				pointLabels: {show: false},
				yaxis: 'y2axis',
				label: 'Hospitalisations'
			},
			{
				lineWidth:2,
				markerOptions:{style:'circle', size:2},
				pointLabels: {show: false},
				label: 'Réanimations',
				color: 'orange'
			}
		],

	}));

	resizablePlots.push($.jqplot('chart2', [model.nuage], {
	    title: 'Nuages de points (correlation = '+model.correl+')',
	    grid: standard_grid,
	    cursor: standard_cursor,
	    seriesDefaults: {
	      showMarker:true,
	      showLine:false,
	      markerOptions:{style:'circle', size:2},
	      shadowOffset: 10
	    }
	  }));

};

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