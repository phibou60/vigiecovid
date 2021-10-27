
// Recréer un tableau en selectionnant les points entre 2 dates

function selectValues(tableau, dateMin, dateMax) {
	var ret = [];
	for (i = 0; i < tableau.length; i++) {
		var values = tableau[i];
		if (values[0] >= dateMin & values[0] <= dateMax) ret.push(values);
	}
	return ret;
}

// Recréer un tableau qui cumule à chaque date les valeurs des dates précédentes

function cumulValues(tableau) {
	var ret = [];
	var cumul = 0;
	for (i = 0; i < tableau.length; i++) {
		var values = tableau[i];
		cumul += values[1];
		ret.push([values[0], cumul]);
	}
	return ret;
}

// Recréer un tableau qui fait le delta à chaque date avec la date précédente

function tsDeltaValues(tableau) {
	var ret = [];
	var previous = 0;
	for (i = 0; i < tableau.length; i++) {
		var values = tableau[i];
		if (i > 0) {
			var delta = values[1] - previous;
			ret.push([values[0], delta]);
		}
		previous = values[1];
	}
	return ret;
}

// Recréer un tableau avec la moyenne mobile

function tsMoyenneMobile(tableau, fenetre) {
	var ret = [];
	for (i = 0; i < tableau.length; i++) {
		var values = tableau[i];
		if (i >= fenetre -1) {
			var sum = 0;
			for (k = i-fenetre+1; k <= i; k++) {
				sum += tableau[k][1];
			}
			moy = sum / 7;
			ret.push([values[0], moy]);
		}
	}
	return ret;
}

//Recréer un tableau la somme des précdents.
// fenetre = nb de jour de la somme,

function tsSomme(tableau, fenetre, scale, trunc) {

	if (scale === undefined) {
		scale = 1;
	  } 

	if (trunc === undefined) {
		trunc = false;
	  } 
	
	var ret = [];
	for (i = 0; i < tableau.length; i++) {
		var values = tableau[i];
		if (i >= fenetre -1) {
			var sum = 0;
			for (k = i-fenetre+1; k <= i; k++) {
				sum += tableau[k][1];
			}
			var result = sum / scale;
			if (trunc) {
				result = Math.trunc(result);
			}
			ret.push([values[0], result]);
		}
	}
	return ret;
}

// Rend la plus grande valeur du tableau

function tsHighWaterMark(tableau) {
	var max;
	for (i = 0; i < tableau.length; i++) {
		if (max === undefined) max = tableau[i][1];
		if (tableau[i][1] > max) max = tableau[i][1];
	}
	return max;
}

//Recréer un tableau pour etre utilisé dans les bar chart

function tsCreateBarChartArray(tableau) {
	var ret = [];
	for (i = 0; i < tableau.length; i++) {
		var values = tableau[i];
		var value = values[1];

		if (value > 0) {
			ret.push([values[0], value, value, 0, 0]);
		} else {
			ret.push([values[0], 0, 0, value, value]);
		}
	}
	return ret;
}

// Fonction qui trouve une valeur supérieur arrondie

function tsArronditAuDessus(valeur) {
	var graduation = 10 ** (Math.trunc(Math.log10(valeur)));
	var nbGraduations = Math.trunc(valeur / graduation)+1;
	
	if (nbGraduations < 4) {
		nbGraduations = nbGraduations * 2;
		graduation = graduation / 2;
	} else if (nbGraduations > 6) {
		nbGraduations = nbGraduations / 2;
		graduation = graduation * 2;
	}
	
	var max = (Math.trunc(valeur / graduation) + 1) * graduation;
	return max;
}

// Add or subtract days to a Date

function tsAddDays(date, days) {
	var result = new Date(date);
	result.setDate(result.getDate() + days);
	return result;
}
