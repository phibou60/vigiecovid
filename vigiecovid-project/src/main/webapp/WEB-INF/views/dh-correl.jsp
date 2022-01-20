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
			<h3>Corrélation entre le nombre de réanimations et d'hospitalisations</h3>
			Meilleure corrélation obtenue avec ${decallMsg}.
			<br>
			Calcul à partir du ${dateMin}. La première vague a été écartée car la prise en charge des
			patients a été différente par rapport à la suite.
		</div>
	</div>
	<br>
	<div class="row">
		<div class="col-xl tuile"><div id="chart1"></div></div>
	</div>
	<div class="row">
    <table class="table">
		  <thead>
		    <tr>
		      <th scope="col">Réa/Hosp</th>
		      <th scope="col">Taux de correlation</th>
		    </tr>
		  </thead>
		  <tbody>	 
	      <c:forEach items="${scores}" var="score">
	        <tr><td>${score.key}</td><td>${score.value}</td></tr>
	      </c:forEach>
	    <tbody> 
	  </table>
	</div>
  <div class="row">
     <h3>Corrélation entre le nombre de réanimations et l'incidence</h3>
  </div>
  <div class="row">
    <div class="col-xl tuile"><div id="chart2"></div></div>
  </div>
</div>
<script>

var model = ${model};

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
				label: 'Hospitalisations'
			},
      {
          lineWidth:2,
          markerOptions:{style:'circle', size:2},
          pointLabels: {show: false},
          yaxis: 'y2axis',
          label: 'Réanimations'
        }			
		],
	}));
	  
	resizablePlots.push($.jqplot('chart2', [model.line2, model.incidences], {
    title:'Réanimations vs Incidences', 
    axes:standard_axes,
    grid: standard_grid,
    cursor: standard_cursor,
    legend: standard_legend,
    series: [
      {
        lineWidth:2,
        markerOptions:{style:'circle', size:2},
        pointLabels: {show: false},
        label: 'Réanimations'
      },
      {
          lineWidth:2,
          markerOptions:{style:'circle', size:2},
          pointLabels: {show: false},
          yaxis: 'y2axis',
          label: 'Incidence'
        }     
    ],
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