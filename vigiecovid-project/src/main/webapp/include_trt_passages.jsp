<%@ page contentType="text/html; charset=UTF-8" %>
<%@ page import="java.util.*, java.io.*, org.apache.log4j.Logger" %>

<%!

public void ajoutPassage(javax.servlet.ServletContext context, String page) throws Exception {
	Logger logger = Logger.getLogger("ajoutPassage");

	Hashtable<String, Integer> passages = null;
	if (context.getAttribute("passages") == null) {
		passages = new Hashtable<String, Integer>();
		context.setAttribute("passages", passages);
	} else {
		passages = (Hashtable<String, Integer>) context.getAttribute("passages");
	}
	
	java.text.SimpleDateFormat dateFormat = new java.text.SimpleDateFormat("yyyy-MM-dd hh");
	String key = dateFormat.format(new Date());
	
	int count = 0;
	if (passages.get(key) != null) {
		count = passages.get(key);
	}
	
	passages.put(key, new Integer(count+1));
}

%>