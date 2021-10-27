package vigiecovid.domain;

import javax.servlet.ServletContext;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class ServletContextWrapper {

	private final Logger LOGGER = Logger.getLogger(ServletContextWrapper.class);

	private ServletContext context;
	
	public ServletContextWrapper(@Autowired ServletContext context) {
		super();
		LOGGER.debug("Instanciate with "+context);
		this.context = context;
	}

	public ServletContext getServletContext() throws Exception {
		return context;
	}

}
