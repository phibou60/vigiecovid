<%@ page isErrorPage="true" %>  
<%@ page contentType="text/html; charset=UTF-8" %>
<!doctype html>
<html lang="fr">
<head>
	<%@ include file="include_head.jsp"%>
</head>
<body>

<div class="container">
	<div class="row">
	  <div class="col-xl">
		<h2>404 : Page inconnue</h2>
		<p>
			La page que vous demandez a peut être été renommée.
		</p>
		<p>
			<a href="<%=request.getServletContext().getContextPath() %>">HOME</a>
		</p>
	  </div>
	</div>
</div>
</body>
</html>
