package vigiecovid.config;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletRegistration;

import org.apache.log4j.Logger;
import org.springframework.web.WebApplicationInitializer;
import org.springframework.web.context.ContextLoaderListener;
import org.springframework.web.context.support.AnnotationConfigWebApplicationContext;
import org.springframework.web.context.support.GenericWebApplicationContext;
import org.springframework.web.servlet.DispatcherServlet;

import vigiecovid.InitWebApp;

/**
 * Ajout de Spring au web.xml par la programmation.
 *
 */
public class MainWebAppInitializer implements WebApplicationInitializer {
	
	static private final Logger LOGGER = Logger.getLogger(MainWebAppInitializer.class);
	
    @Override
    public void onStartup(final ServletContext sc) throws ServletException {
    	System.out.println("***** Spring Initializer *****");
    	LOGGER.info("Startup");
    	
        AnnotationConfigWebApplicationContext root = 
          new AnnotationConfigWebApplicationContext();
        
        root.scan("vigiecovid");
        sc.addListener(new ContextLoaderListener(root));

        ServletRegistration.Dynamic appDispatcher = 
        		sc.addServlet("mvc", new DispatcherServlet(new GenericWebApplicationContext()));
        appDispatcher.setLoadOnStartup(1);
        appDispatcher.addMapping("/app/*");
        
        ServletRegistration.Dynamic appServlet =
        		sc.addServlet("app", new InitWebApp());
        appServlet.setLoadOnStartup(1);
        
    }
    
}