<%@ page contentType="text/html; charset=UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jstl/core_rt" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jstl/fmt_rt" %>

<!doctype html>
<html lang="fr">
<head>
	<%@ include file="include_head.jsp"%>

  <link href="../modules/france_departements/jqvmap.css" media="screen" rel="stylesheet" type="text/css" />
	<script src="../modules/france_departements/jquery.vmap.js" type="text/javascript"></script>
  <script src="../modules/france_departements/jquery.vmap.france.js" type="text/javascript"></script>
	
</head>
<body>
<%@ include file="include_menu.jsp"%>

<div class="container">

	<h2>Taux d'incidence par département</h2>
	<p>
		Mis à jour le ${model.day}.
	</p>
	<p>
		L'incidence est le nombre total de cas positifs sur 7 jours rapportés à 100.000 personnes.
	</p>

	<div class="row">
		<div class="col-sm">		
		<c:choose>
			<c:when test="${not empty model.jourPrec}">
				<a href="testvir-dep?day=${model.jourPrec}">&lt;&lt;</a>
			</c:when>
		</c:choose>
		${model.day}
		<c:choose>
			<c:when test="${not empty model.jourSuivant}">
				<a href="testvir-dep?day=${model.jourSuivant}">&gt;&gt;</a>
			</c:when>
		</c:choose>
		</div>
		<div class="col-sm">
		<form action="testvir-dep" metyhod="get">
		<select class="form-select" name="day" onchange="this.form.submit()">
		  <option selected>Selection d'une journée</option>
			<c:forEach items="${model.cumulByDay}" var="entry">
				<option value='${entry.key}'>${entry.key}</option>
			</c:forEach>
		</select>
		</form>
		</div>
	</div>

	<script>
		var colorIncids = ["LimeGreen", "greenyellow", "#feff31", "#ffdb33", "#ffb734", "#ff9234", "#ff6933", "#fe3231", "crimson"];
		var incidLevels = [	              10,            50,        100,       250,       500,       1000,       2000,       5000];
	</script>
	
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

	<br>
	<br>
	<h2>Variation de l'incidence</h2>

	<script>
		var levelsColorsV = ["LimeGreen", "greenyellow", "#feff31", "#ffdb33", "#ffb734", "#ff9234", "#ff6933", "#fe3231", "crimson"];
		var levelsValuesV = [	       -10,           0,         10,        20,        50,        100,       500,       1000];
	</script>
	
	<div class="row">
		<div id="francemapv" style="width: 100%; max-width: 700px"></div>
	</div>
	<br>
	<div class="row">
		<script>
			for (var i=0; i<levelsColorsV.length; i++) {
				document.write("<span class='badge' style='background:"+levelsColorsV[i]+";'>");
				if (i == 0) {
					document.write(" &lt; "+formatDeltaInteger(levelsValuesV[i]));
				}
				if (i > 0 && i < levelsValuesV.length) {
					document.write(" de "+formatDeltaInteger(levelsValuesV[i-1])
							+" à "+formatDeltaInteger(levelsValuesV[i]));
				}
				if (i == levelsColorsV.length-1) {
					document.write(" &gt; "+formatDeltaInteger(levelsValuesV[i-1]));
				}
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
		<th scope="col" style='text-align:right;'>% Variation</th>
		<th scope="col" style='text-align:right;'>Incidence</th>
	</tr></thead>
	<tbody>
		<c:forEach items="${model.depStats}" var="entry">
			<tr>
				<td style='text-align:center;'><a href='testvir-day?dep=${entry.key}&lib=${entry.value.lib}'>${entry.key}</a></td>
				<td style='text-align:left;'>${entry.value.lib}</td>
				<td style='text-align:right;'><fmt:formatNumber value="${entry.value.tests}" maxFractionDigits="3"/></td>
				<td style='text-align:right;'><fmt:formatNumber value="${entry.value.positifs}" maxFractionDigits="3"/></td>
				<td style='text-align:right;'>
					<span class='badge' id='depv${entry.key}'><fmt:formatNumber value="${model.variations[entry.key]}" pattern="+0.0;-0.0"/> %</span>
				</td>
				<td style='text-align:right;'>
					<span class='badge' id='dep${entry.key}'><fmt:formatNumber value="${entry.value.incid}" maxFractionDigits="3"/></span>
				</td>
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

var variations = {};
<c:forEach items="${model.variations}" var="entry">
    variations['${entry.key}'] = ${entry.value};
</c:forEach>

var couleurs = {};
for (depId in incids) {
	couleurs[depId] = colorIncids[colorIncids.length-1];
	var incidence = incids[depId];
	for (var j = incidLevels.length-1; j>=0; j--) {
		if (incidence < incidLevels[j]) couleurs[depId] = colorIncids[j];
	}
}

var couleursv = {};
for (depId in variations) {
	couleursv[depId] = levelsColorsV[levelsColorsV.length-1];
	var variation = variations[depId];
	for (var j = levelsValuesV.length-1; j>=0; j--) {
		if (variation < levelsValuesV[j]) couleursv[depId] = levelsColorsV[j];
	}
}

$(document).ready(function() {
	// Colorisation de l'incidence et du taux de variation du tableau
	for (depId in couleurs) {
		let id = '#dep' + depId;
		$(id).css({backgroundColor: couleurs[depId]});
	}
	for (depId in couleurs) {
		let id = '#depv' + depId;
		$(id).css({backgroundColor: couleursv[depId]});
	}
	
	// On retaille la carte en fonctione de la définition du terminal
	var cw = Math.trunc($('#francemap').width() * 0.8);
	$('#francemap').css({'height':cw+'px'});
	$('#francemapv').css({'height':cw+'px'});
	
	// Affichage des cartes
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
			location.assign('testvir-day?dep='+code+'&lib='+region);
		},
		onLabelShow: function(element, label, region){
			label.html(labels[region] + ": " + incids[region]);
		}
	});
	
	var pcFormateur = new Intl.NumberFormat('fr-FR', { maximumFractionDigits: 1 });
			
	$('#francemapv').vectorMap({
		map: 'france_fr',
		hoverOpacity: 1,
		hoverColor: false,
		backgroundColor: "#ffffff",
		colors: couleursv,
		borderColor: "#000000",
		enableZoom: false,
		showTooltip: true,
		onRegionClick: function(element, code, region) {
			location.assign('testvir-day?dep='+code+'&lib='+region);
		},
		onLabelShow: function(element, label, region){
			label.html(labels[region] + ": " + pcFormateur.format(variations[region]) + "%");
		}
	});
	
});
</script>
<%@ include file="include_footer.jsp"%>
</body>