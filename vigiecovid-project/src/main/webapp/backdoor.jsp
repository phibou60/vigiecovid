<%@ page contentType="text/html; charset=UTF-8" %>
<%@ page import="java.util.*, java.io.*, org.apache.log4j.Logger" %>
<%@ page import="vigiecovid.Cumul" %>
<!doctype html>
<html lang="fr">
<head>
	<%@ include file="include_head.jsp"%>
</head>
<body>
	<%@ include file="include_top.jsp"%>
<%
Hashtable<String, Integer> passages = (Hashtable<String, Integer>) request.getServletContext().getAttribute("passages");
%>
<div class="container">
	<div class="row">
		<div class="col-xl">
		<h3>Statistiques de passages</h3>
		</div>
	</div>
	<br>
	<div class="row">
		<div id="chartPassages" class="col-xl"></div>
	</div>
</div>
<script>

var resizablePlots = [];

var l1 = [];

<%
String dateMin = "9999";
String dateMax = "0";

for(String key : passages.keySet()) {
	out.println("l1.push(['"+key+":00:00', "+passages.get(key)+"]);");
	if (key.compareTo(dateMin) < 0) dateMin = key;
	if (key.compareTo(dateMax) > 0) dateMax = key;
}
%>

var dateMin = "<%=dateMin %>";
var dateMax = "<%=dateMax %>";

<%@ include file="include_plot_style.jsp"%>

var axes = {
	xaxis:{
		renderer:$.jqplot.DateAxisRenderer,
		tickOptions:{formatString:"%#m/%#d %H:%M"},
		drawMajorGridlines : false,
	},
	yaxis:{
		min:0,
		rendererOptions: {forceTickAt0: true},
	},
	y2axis: {
		min:0,
		rendererOptions: {
			// align the ticks on the y2 axis with the y axis.
			alignTicks: true,
			forceTickAt0: true
		}
	}
};
		
function dessine() {

	resizablePlots.push($.jqplot('chartPassages', [l1], {
		title:'Passages', 
		axes:axes,
		series:[standard_line_series],
		grid: standard_grid
	}));


}

var redessineTimer;
var seq = 0;
seq++;
	
function redessine() {
	//console.log("Resize: "+seq+", plots: "+resizablePlots.length);
	//seq++;
	resizablePlots.forEach(plot => plot.destroy( { resetAxes: true } ));
	dessine();
}

$(window).resize(function() {
	//console.log("clearTimeout: "+seq);
	clearTimeout(redessineTimer);
	redessineTimer = setTimeout(function(){ redessine()}, 500);
});

$(document).ready(function(){
	dessine();
})

</script>
	<%@ include file="include_bottom.jsp"%>
</body>

<%@ include file="include_trt_dh.jsp"%>