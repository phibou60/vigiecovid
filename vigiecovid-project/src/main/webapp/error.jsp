<%@ page isErrorPage="true" %>  
<%@ page contentType="text/html; charset=UTF-8" %>
<!doctype html>
<html lang="fr">
<head>
	<title>Vigie Covid</title>
</head>
<body>

<div class="container">
	<div class="row">
		<div class="col-xl">
			<h2>Désolé, il y a eu une exception</h2>
			Exception is: <%=exception %><br>
			<code>
			<% exception.printStackTrace(System.out); %>
			</code>
			<a href="<%=request.getServletContext().getContextPath() %>">HOME</a>
		</div>
	</div>
</div>

</body>
</html>