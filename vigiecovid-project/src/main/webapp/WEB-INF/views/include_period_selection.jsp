    
<label for="select_period">Selectionner une période:</label>
<select id="select_period" onChange="javascript:redessine();">
	<option value="1" selected="x">Tout</option>
	<option value="5">Deux derniers mois</option>
	<option value="4">Six derniers mois</option>
	<option value="2">Deuxième vague</option>
	<option value="3">Troisième vague</option>
</select>

<script>

var secondeVague = "2020-10-01";
var troisiemeVague = "2020-12-10";

var DAY_TIME = 24*60*60*1000;

var sixMois = new Date(new Date().getTime() - DAY_TIME*183).toISOString().substring(0, 10);
var deuxMois = new Date(new Date().getTime() - DAY_TIME*62).toISOString().substring(0, 10);

function getPeriodStart() {

	var period = getPeriod();
	
	if (period == "1") {
		return dataDateMin;
	}

	if (period == "2") {
		return secondeVague;
	}

	if (period == "3") {
		return troisiemeVague;
	}

	if (period == "4") {
		return sixMois;
	}

	if (period == "5") {
		return deuxMois;
	}

}

function getPeriodEnd() {
	return dataDateMax;
}

function getPeriod() {
	return $('#select_period').val();
}

</script>