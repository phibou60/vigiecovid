<%@ page contentType="text/html; charset=UTF-8" %>
<%@ page import="java.util.*, java.io.*, org.apache.log4j.Logger" %>
<script>
function decodeBase64(s) {
	//console.log("decodeBase64: "+s+", "+s.length);
	if (s.length == 0) return 0;
	
	var base = [256];
	for (var i=0; i<26; i++) {
		base[i+65] = 0+i;
		base[i+97] = 26+i;
	}
	for (var i=0; i<10; i++) {
		base[i+48] = 52+i;
	}
	base[43] = 62;
	base[47] = 63;
	
	var ret = 0;
	for (var i=0; i<s.length; i++) {
		var k = s.length-1-i;
		//console.log("charAt: "+s.charAt(k)+", "+s.charCodeAt(k));
		var code = s.charCodeAt(k);
		//console.log("code: "+code+", "+base[code]);
		ret = (ret*64)+ base[code];
	}
	//console.log("ret: "+ret);
	if (isNaN(ret)) {
		console.log("isNaN: "+s);
		ret = 0;
	}
	return ret;
}

function decodeSimpleTimeSerie(s) {
	var dataTable = s.split('=');
	var ret = [];
	
	for (var i=0; i<dataTable.length-1; i+=2) {
		var t = decodeBase64(dataTable[i]);
		var v = decodeBase64(dataTable[i+1]);
		//console.log("ret.push(["+t+", "+v+"])");
		ret.push([t, v]);
	}
	return ret;
}
</script>

<%!
String encodeBase64(Long value) {
	String CHARS = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789+/";
	String PAD = "=";
	
	StringBuilder ret = new StringBuilder();
	
	long temp = value;
	while (temp > 0) {
		long rest = temp % 64;
		ret.append(CHARS.charAt((int) rest));
		temp = temp / 64;
	}
	ret.append(PAD);
	return ret.toString();
}
%>