<%@ page contentType="text/html; charset=UTF-8" %>

// Enable plugins like cursor and highlighter by default.
$.jqplot.config.enablePlugins = true;
$.jqplot.sprintf.thousandsSeparator = '.';
$.jqplot.sprintf.decimalMark = ',';
	
var standard_grid = {
	background : 'white',
	borderColor : '#a9cce3',
	gridLineColor : '#a9cce3',
	shadow: false
};
var standard_axes = {
	xaxis:{
		renderer:$.jqplot.DateAxisRenderer,
		tickOptions:{formatString:"%Y/%#m/%#d"},
		min:dateMin,
		max:dateMax,
		drawMajorGridlines : false,
	},
	yaxis:{
		min:0,
		rendererOptions: {forceTickAt0: true},
		tickOptions: { formatString: "%'i" }
	},
	y2axis: {
		min:0,
		rendererOptions: {
			// align the ticks on the y2 axis with the y axis.
			alignTicks: true,
			forceTickAt0: true
		},
		tickOptions: { formatString: "%'i" }
	}
};

var standard_line_series = {
	lineWidth:1,
	markerOptions:{style:'circle', size:2},
	shadowOffset: 10

};

var standard_stock_series = {
	lineWidth: 1,
	fill: true
};

var standard_seriesDefaults = {
	shadow: false,
	markerOptions:{shadow: false}
};

var	standard_legend = {
	show: true,
	placement: 'outsideGrid',
	location: 's',
	rowSpacing: '0px'
};

var	standard_cursor = {
	zoom:true,
	show:false,
	looseZoom: true
};

