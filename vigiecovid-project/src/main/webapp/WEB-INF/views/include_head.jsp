<%@ page contentType="text/html; charset=UTF-8" %>

	<!-- Required meta tags -->
	<meta charset="utf-8">
	<title>Vigie Covid</title>
	
	<meta name="viewport" content="width=device-width, initial-scale=1, shrink-to-fit=no">
	<meta name="description" content="Le site de surveillance de l'épidémie de Covid-19 en France, mis à jour quotidiennement.">
	<meta name="author" content="Philippe Bouvart">
	<meta name="keywords" content="coronavirus, covid, covid19, france, surveillance, sante">
	<meta name="robots" content="index, follow, archive" />

	<meta name="og:title" content="Vigie Covid">
	<meta name="og:description" content="Le site de surveillance de l'épidémie de Covid-19 en France, mis à jour quotidiennement.">
	<meta name="og:image" content="https://vigie-covid.azurewebsites.net/vigiecovid/theme/thumbnail.png">
	<meta name="og:url" content="https://vigie-covid.azurewebsites.net/">
	<meta name="twitter:card" content="summary_large_image">

	<!-- Bootstrap CSS -->
	<link href="../modules/bootstrap-4.5.2-dist/css/bootstrap.min.css" rel="stylesheet"/>
	
	<!-- JQuery -->
	<script src="../modules/jquery/jquery.min.js?version=${applicationScope.version}"></script>
	
	<!-- Bootstrap JS -->
	<script src="../modules/popper/popper.min.js?version=${applicationScope.version}"></script>
	<script src="../modules/bootstrap-4.5.2-dist/js/bootstrap.min.js"></script>
	 
	<!-- JQPlot -->
	<script src="../modules/jqplot/jquery.jqplot.min.js?version=${applicationScope.version}"></script>
	<script src="../modules/jqplot/plugins/jqplot.dateAxisRenderer.js?version=${applicationScope.version}"></script>
	<script src="../modules/jqplot/plugins/jqplot.categoryAxisRenderer.js?version=${applicationScope.version}"></script>
	<script src="../modules/jqplot/plugins/jqplot.cursor.js?version=${applicationScope.version}"></script>
	<script src="../modules/jqplot/plugins/jqplot.highlighter.js?version=${applicationScope.version}"></script>
	<script src="../modules/jqplot/plugins/jqplot.barRenderer.js?version=${applicationScope.version}"></script>
	<script src="../modules/jqplot/plugins/jqplot.ohlcRenderer.js?version=${applicationScope.version}"></script>
	<script src="../modules/jqplot/plugins/jqplot.canvasOverlay.js?version=${applicationScope.version}"></script>
	<link  href="../modules/jqplot/jquery.jqplot.min.css?version=${applicationScope.version}" rel="stylesheet">
	
	<!-- Site style -->
	<link href="../theme/style.css?version=${applicationScope.version}" rel="stylesheet"/>
	<link href="../theme/favicon.ico" rel="icon"/>

	<script src="../scripts/timeseries_tools.js?version=${applicationScope.version}"></script>
	<script src="../scripts/cookies_tool.js?version=${applicationScope.version}"></script>
	
	