<%@ page errorPage="error.jsp" %> 
<%@ page contentType="text/html; charset=UTF-8" %>
<%@ page import="java.util.*, java.io.*, org.apache.log4j.Logger" %>
<%@ page import="java.time.LocalDate" %>
<%@ page import="vigiecovid.domain.TestVir" %>
<%@ page import="chamette.datasets.Datasets" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jstl/core_rt" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jstl/fmt_rt" %>

<%
Logger logger = Logger.getLogger(this.getClass());
ajoutPassage(request.getServletContext(), "testvirdep");

/////////////////////////////////////////////////////////////
//////////////////// Début calcul modele ////////////////////
/////////////////////////////////////////////////////////////

Map<String, Object> model = new HashMap<>();
request.setAttribute("model", model);
{
	LocalDate day = null;
	if (request.getParameter("day") != null) {
		day = LocalDate.parse(request.getParameter("day"));
	}

	TreeMap<LocalDate, TestVir> cumulByDay = TestVir.cumulTestVirByDay(request.getServletContext(), null, false);
	model.put("cumulByDay", cumulByDay);
	
	if (day == null) {
		day = cumulByDay.lastKey();
	}
	model.put("day", day);
	
	TestVir testVir = cumulByDay.get(day);
	model.put("testVir", testVir);
	model.put("incid", Math.round(((double)testVir.getPositifs() * 100) / testVir.getTests()));
	
	TreeMap<String, TestVir> cumulTestVirByDepLastWeek = TestVir.cumulTestVirByDepLastWeek(request.getServletContext(), day);
	Datasets datasets = (Datasets) application.getAttribute("datasets");
	HashMap<String, HashMap> departements = (HashMap<String, HashMap>) datasets.get("departements").getData();
	
	TreeMap<String, Map> depStats = new TreeMap<>(); 
	
	for (Map.Entry<String, TestVir> entry : cumulTestVirByDepLastWeek.entrySet()) {
		Map<String, Object> dep = new HashMap<>();

		dep.put("tests", entry.getValue().getTests());
		dep.put("positifs",	entry.getValue().getPositifs());
		dep.put("pc", entry.getValue().getPc());
		
		if (departements.containsKey(entry.getKey())) {
			Map<String, Object> departement = departements.get(entry.getKey());
			dep.put("lib", departement.get("DEP"));
			long population = (Long) departement.get("PTOT");
			dep.put("incid", Math.round((double) entry.getValue().getPositifs() * 100_000 / population));
			depStats.put(entry.getKey(), dep);
		}
	}
	model.put("depStats", depStats);
	
	//---- Recherche jours précédent et suivant le jour en cours
	LocalDate jourSuivant = cumulByDay.higherKey(day);
	if (jourSuivant != null) {
		model.put("jourSuivant", jourSuivant);
	}
	LocalDate jourPrec = cumulByDay.lowerKey(day);
	if (jourPrec != null) {
		model.put("jourPrec", jourPrec);
	}
	
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
	
</head>
<body>
<%@ include file="include_top.jsp"%>

<script>
var colorIncids = ["greenyellow", "#feff31", "#ffdb33", "#ffb734", "#ff9234", "#ff6933", "#fe3231", "crimson"];
var incidLevels = [	               50,			100,		150,		250,		400,	700,		1000];
</script>

<div class="container">

	<h2>Taux d'incidence par département sur les 7 derniers jours</h2>
	<br>
	Mis à jour le ${model.day}.<br>
	Sur une semaine, un total de
	<span class="nombre"><fmt:formatNumber value="${model.testVir.tests}" maxFractionDigits="3"/></span> tests
	pour <span class="nombre"><fmt:formatNumber value="${model.testVir.positifs}" maxFractionDigits="3"/></span>
	retours positfs soit 
	<span class="nombre"><fmt:formatNumber value="${model.testVir.pc}" maxFractionDigits="3"/>%</span>.
	<br>
	<br>

<div class="row">
	<div class="col-sm">		
	<c:choose>
		<c:when test="${not empty model.jourPrec}">
			<a href="testvirdep.jsp?day=${model.jourPrec}">&lt;&lt;</a>
		</c:when>
	</c:choose>
	${model.day}
	<c:choose>
		<c:when test="${not empty model.jourSuivant}">
			<a href="testvirdep.jsp?day=${model.jourSuivant}">&gt;&gt;</a>
		</c:when>
	</c:choose>
	</div>
	<div class="col-sm">
	<form action="testvirdep.jsp" metyhod="get">
	<select class="form-select" name="day" onchange="this.form.submit()">
	  <option selected>Selection d'une journée</option>
		<c:forEach items="${model.cumulByDay}" var="entry">
			<option value='${entry.key}'>${entry.key}</option>
		</c:forEach>
	</select>
	</form>
	</div>
</div>

<div class="row">
	<div id="francemap" style="width: 100%; max-width: 700px"></div>
</div>
<br>
<div class="row">
	<script>
		for (var i=0; i<colorIncids.length; i++) {
			document.write("<span class='badge' style='background:"+colorIncids[i]+";'>");
			if (i == 0) document.write(" &lt; "+incidLevels[i]);
			if (i > 0 && i < incidLevels.length) document.write(" de "+incidLevels[i-1]+" à "+incidLevels[i]);
			if (i == colorIncids.length-1) document.write(" &gt; "+incidLevels[i-1]);
			document.write("</span>&nbsp;");
		}
	</script>
</div>
</div>	
<br>
<div class="container">
	<table class="table table-hover table-responsive-md" style="max-width: 1000px">
	<thead><tr>
		<th scope="col" style='text-align:center;'>Département</th>
		<th scope="col">&nbsp;</th>
		<th scope="col" style='text-align:right;'>Tests</th>
		<th scope="col" style='text-align:right;'>Retours Positifs</th>
		<th scope="col" style='text-align:right;'>Cas pour 100.000</th>
	</tr></thead>
	<tbody>
		<c:forEach items="${model.depStats}" var="entry">
			<tr>
				<td style='text-align:center;'><a href='testvirday.jsp?dep=${entry.key}&lib=${entry.value.lib}'>${entry.key}</a></td>
				<td style='text-align:left;'>${entry.value.lib}</td>
				<td style='text-align:right;'><fmt:formatNumber value="${entry.value.tests}" maxFractionDigits="3"/></td>
				<td style='text-align:right;'><fmt:formatNumber value="${entry.value.positifs}" maxFractionDigits="3"/></td>
				<td style='text-align:right;'><span class='badge' id='dep${entry.key}'><fmt:formatNumber value="${entry.value.incid}" maxFractionDigits="3"/></span></td>
			</tr>
		</c:forEach>
	</tbody>
	</table>
</div>
	
<script type="text/javascript">
var incids = {};
var labels = {};
<c:forEach items="${model.depStats}" var="entry">
	incids['${entry.key}'] = ${entry.value.incid};
	labels['${entry.key}'] = "${entry.value.lib}";
</c:forEach>

var couleurs = {};
for (depId in incids) {
	couleurs[depId] = colorIncids[colorIncids.length-1];
	var incidence = incids[depId];
	for (var j = incidLevels.length-1; j>=0; j--) {
		if (incidence < incidLevels[j]) couleurs[depId] = colorIncids[j];
	}
}

$(document).ready(function() {

	for (depId in couleurs) {
		let id = '#dep' + depId;
		$(id).css({backgroundColor: couleurs[depId]});
	}
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
		onRegionClick: function(element, code, region) {
			location.assign('testvirday.jsp?dep='+code+'&lib='+region);
		},
		onLabelShow: function(element, label, region){
			label.html(region+" - "+labels[region]+" ("+incids[region]+")");
		}
	});
});
</script>
</body>
<%@ include file="include_trt_passages.jsp"%>