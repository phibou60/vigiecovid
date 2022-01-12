package vigiecovid;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.Enumeration;
import java.util.Locale;
import java.util.Map;

import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import org.apache.log4j.Logger;

import chamette.datasets.DataGouvFrDownloader;
import chamette.datasets.Datasets;

public class InitWebApp extends HttpServlet { 
    
	private static final Logger LOGGER = Logger.getLogger(InitWebApp.class);
	
	@Override
	public void init(ServletConfig config) throws ServletException {
		
		LOGGER.info("********* Webapp initialization at " + LocalDateTime.now() + " *********");
		
		Locale.setDefault(new Locale("fr", "FR"));
		
		try {
			ServletContext context = config.getServletContext(); 
			dumpConfig(context);			
			downloadDatasets(context);
			
			context.setAttribute("version", new Date().getTime());
		
			LOGGER.info("Servlet initialization terminated");
			
		} catch (Exception e) {
			LOGGER.error("Exception in InitServlet: ", e);
		}
		
	}
	
	//--------------------------------------------------
	// ---- Dump Config properties
	//--------------------------------------------------

	private void dumpConfig(ServletContext context) {

		LOGGER.debug("- context.getMajorVersion() = "+context.getMajorVersion());
		LOGGER.debug("- context.getMinorVersion() = "+context.getMinorVersion());
		LOGGER.debug("- context.getServerInfo() = "+context.getServerInfo());
		LOGGER.debug("- context.getServletContextName() = "+context.getServletContextName());
		LOGGER.debug("- java version = "+System.getProperty("java.version"));

		Enumeration<String> e = context.getAttributeNames();
		while (e.hasMoreElements()) {
			String key = e.nextElement();
			String value = context.getAttribute(key).toString();
			if (value.length() > 40) value = value.substring(0, 40); 
			LOGGER.debug("- context attribute: "+key+"="+value);
		}

		for (Map.Entry<String, String> envProps : System.getenv().entrySet()) {
			LOGGER.debug("- env. variable: "+envProps.getKey()+"="+envProps.getValue());
		}
		
	}

	//--------------------------------------------------
	// ---- Datasets
	//--------------------------------------------------

	private void downloadDatasets(ServletContext context) {
	
		Datasets datasets = new Datasets();
		context.setAttribute("datasets", datasets);
		
		String mode = System.getenv("VIGIECOVID_MODE");
		if (mode == null) {
			mode = "connected";
		}
	
		String folder = System.getenv("VIGIECOVID_FOLDER");
	
		datasets.add(new DataGouvFrDownloader("donnees-hospitalieres-covid19",
				"5e7e104ace2080d9162b61d8", "63352e38-d353-4b54-bfd1-f1b3ee1cabd7", mode, folder));
		datasets.add(new DataGouvFrDownloader("donnees-hospitalieres-nouveaux-covid19",
				"5e7e104ace2080d9162b61d8", "6fadff46-9efd-4c53-942a-54aca783c30c", mode, folder));
		datasets.add(new DataGouvFrDownloader("donnees-hospitalieres-classe-age-covid19",
				"5e7e104ace2080d9162b61d8", "08c18e08-6780-452d-9b8c-ae244ad529b3", mode, folder));
		datasets.add(new DataGouvFrDownloader("sp-pos-quot-dep", "5ed117db6c161bd5baf070be",
				"406c6a23-e283-4300-9484-54e78c8ae675", mode, folder));
		datasets.add(new DataGouvFrDownloader("sp-pos-quot-fra", "5ed117db6c161bd5baf070be",
				"dd0de5d9-b5a5-4503-930a-7b08dc0adc7c", mode, folder));
		datasets.add(new DataGouvFrDownloader("sursaud-covid19-quotidien-departement",
				"5e74ecf52eb7514f2d3b8845", "eceb9fb4-3ebc-4da3-828d-f5939712600a", mode, folder));
	
		datasets.add(new DataGouvFrDownloader("vacsi-a-fra", "6010206e7aa742eb447930f7",
				"54dd5f8d-1e2e-4ccb-8fb8-eac68245befd", mode, folder));
		
		datasets.startRefreshEngine();
		
	}
	
}