package vigiecovid.config;

import java.util.Date;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletRegistration;

import org.apache.logging.log4j.Logger;
import org.springframework.web.WebApplicationInitializer;
import org.springframework.web.context.ContextLoaderListener;
import org.springframework.web.context.support.AnnotationConfigWebApplicationContext;
import org.springframework.web.context.support.GenericWebApplicationContext;
import org.springframework.web.servlet.DispatcherServlet;

/**
 * Ajout de Spring au web.xml par la programmation.
 *
 */
public class MainWebAppInitializer implements WebApplicationInitializer {
	
	private static final Logger LOGGER = org.apache.logging.log4j.LogManager.getFormatterLogger(MainWebAppInitializer.class);
	
    @Override
    public void onStartup(final ServletContext servletContext) throws ServletException {
    	LOGGER.info("Startup");
    	
        AnnotationConfigWebApplicationContext springContext
        		= new AnnotationConfigWebApplicationContext();
        
        springContext.register(AppConfig.class);
        springContext.scan("vigiecovid");
        servletContext.addListener(new ContextLoaderListener(springContext));

        GenericWebApplicationContext webApplicationContext = new GenericWebApplicationContext();
		ServletRegistration.Dynamic appDispatcher
				= servletContext.addServlet("mvc", new DispatcherServlet(webApplicationContext));
        appDispatcher.setLoadOnStartup(1);
        appDispatcher.addMapping("/app/*");

        servletContext.setAttribute("version", new Date().getTime());
        
    }
    
}