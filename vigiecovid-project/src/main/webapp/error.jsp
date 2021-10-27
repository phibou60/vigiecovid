<%@ page isErrorPage="true" %>  
<%@ page contentType="text/html; charset=UTF-8" %>
<!doctype html>
<html lang="fr">
<head>
	<%@ include file="include_head.jsp"%>
</head>
<body>
<%@ include file="include_top.jsp"%>

<div class="container">
	<div class="row">
		<div class="col-xl">
		<h2>Désolé, il y a eu une exception</h2>
		Exception is: <%=exception %><br>
		<code>
		<% exception.printStackTrace(System.out); %>
		</code>  
		</div>
	</div>
</div>
</body>
</html>
