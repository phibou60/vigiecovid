<%@ page errorPage="error.jsp" %> 
<%@ page contentType="text/html; charset=UTF-8" %>
<%@ page import="java.util.*, java.io.*, org.apache.log4j.Logger" %>
<%@ page import="javax.json.*, javax.json.stream.*" %>
<%@ page import="java.time.LocalDate" %>
<%@ page import="vigiecovid.domain.sursaud.Sursaud" %>
<%@ page import="vigiecovid.domain.sursaud.SursaudsDAO" %>

<%
Logger logger = Logger.getLogger(this.getClass());
ajoutPassage(request.getServletContext(), "sursaudep");

/////////////////////////////////////////////////////////////
//////////////////// Début calcul modele ////////////////////
/////////////////////////////////////////////////////////////

{
	JsonBuilderFactory factory = Json.createBuilderFactory(new HashMap<String, Object>());
	JsonObjectBuilder root = factory.createObjectBuilder();
	
	SursaudsDAO sursaudsDAO = new SursaudsDAO(request.getServletContext());
	TreeMap<LocalDate, Sursaud> cumulByDay = sursaudsDAO.cumulSursaudByDay(null);
	
	LocalDate lastDay = cumulByDay.lastKey();
	root.add("lastDay", lastDay.toString());
	LocalDate fromDate = lastDay.minusDays(6);
	
	TreeMap<String, Sursaud> cumulByDep = sursaudsDAO.cumulSursaudByDep(fromDate, lastDay);
	
	JsonObjectBuilder deps = factory.createObjectBuilder();
	
	for (Map.Entry<String, Sursaud> entry : cumulByDep.entrySet()) {
		if (entry.getValue().getNbreActeTot() > 0) {
			double pc = (100.0d *entry.getValue().getNbreActeCorona() / entry.getValue().getNbreActeTot());
			deps.add(entry.getKey(), pc);
		}
	}
	root.add("deps", deps);
	
	JsonObject model = root.build();
	request.setAttribute("model", model);
}

/////////////////////////////////////////////////////////////
////////////////////  Fin calcul modele  ////////////////////
/////////////////////////////////////////////////////////////
%>

<!doctype html>
<html lang="fr">
<head>
	<%@ include file="include_head.jsp"%>

    <link href="modules/france_departements/jqvmap.css" media="screen" rel="stylesheet" type="text/css" />
    <script src="modules/france_departements/jquery.vmap.js" type="text/javascript"></script>
    <script src="modules/france_departements/jquery.vmap.france.js" type="text/javascript"></script>
	<script src="modules/france_departements/jquery.vmap.colorsFrance.js" type="text/javascript"></script>
	
</head>
<body>
<%@ include file="include_top.jsp"%>

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
var model = <%
JsonObject jsonObject = (JsonObject) request.getAttribute("model");
JsonWriter jsonWriter = Json.createWriter(out);
jsonWriter.writeObject(jsonObject);
%>;

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
			location.assign('sursauday.jsp?dep='+code+'&lib='+region);
		}
	});
});
</script>
</body>
<%@ include file="include_trt_passages.jsp"%>