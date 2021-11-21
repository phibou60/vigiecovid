<%@ page errorPage="error.jsp" %> 
<%@ page contentType="text/html; charset=UTF-8" %>

<!doctype html>
<html lang="fr">
<head>
	<%@ include file="include_head.jsp"%>

    <link href="../modules/france_departements/jqvmap.css" media="screen" rel="stylesheet" type="text/css" />
    <script src="../modules/france_departements/jquery.vmap.js" type="text/javascript"></script>
    <script src="../modules/france_departements/jquery.vmap.france.js" type="text/javascript"></script>
	<script src="../modules/france_departements/jquery.vmap.colorsFrance.js" type="text/javascript"></script>
	
</head>
<body>
<%@ include file="include_menu.jsp"%>

<script>
var colorLevels = ["greenyellow", "yellow", "orange", "red"];
var levels = [                     3.0,	     5.0,      10.0];
</script>

<div class="container">
	<h2>Appels SOS médecins pour suspicions de covid-19 par département sur les 7 derniers jours</h2>
	Mis à jour le <span class="nombre" id="lastDay"></span>.
</div>
<div class="container">
	<div class="row">
		<div id="francemap" style="width: 700px; height: 600px;"></div>
	</div>
	<br><br>
	<div class="row">
	<script>
		for (var i=0; i<colorLevels.length; i++) {
			document.write("<span class='badge' style='background:"+colorLevels[i]+";'>");
			if (i == 0) document.write(" &lt; "+levels[i]);
			if (i > 0 && i < levels.length) document.write(" de "+levels[i-1]+" à "+levels[i]);
			if (i == colorLevels.length-1) document.write(" &gt; "+levels[i-1]);
			document.write("</span>&nbsp;");
		}
	</script>
	</div>
</div>	
<br>
	
<script type="text/javascript">
var model = ${model};

document.getElementById('lastDay').innerHTML = model.lastDay;

var couleurs = {};
for (depId in model.deps) {
	couleurs[depId] = colorLevels[colorLevels.length-1];
	var incidence = model.deps[depId];
	for (var j = levels.length-1; j>=0; j--) {
		if (incidence < levels[j]) couleurs[depId] = colorLevels[j];
	}
}

$(document).ready(function() {

	var cw = Math.trunc($('#francemap').width() * 0.8);
	$('#francemap').css({'height':cw+'px'});
	
	$('#francemap').vectorMap({
		map: 'france_fr',
		hoverOpacity: 1,
		hoverColor: false,
		backgroundColor: "#ffffff",
		colors: couleurs,
		borderColor: "#000000",
//		selectedColor: "#EC0000",
		enableZoom: false,
		showTooltip: true,
		onRegionClick: function(element, code, region)
		{
			var message = 'Département : "'
				+ region 
				+ '" || Code : "'
				+ code
				+ '"';
			//alert(message);
			location.assign('sursau-day?dep='+code+'&lib='+region);
		}
	});
});
</script>
<%@ include file="include_footer.jsp"%>
</body>