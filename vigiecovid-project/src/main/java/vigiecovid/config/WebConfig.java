package vigiecovid.config;

import org.apache.log4j.Logger;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.ViewResolver;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.view.InternalResourceViewResolver;
import org.springframework.web.servlet.view.JstlView;

/**
 * Configuration de la View du modèle MVC
 *
 */
@EnableWebMvc
@Configuration
public class WebConfig implements WebMvcConfigurer {

	private static final Logger LOGGER = Logger.getLogger(WebConfig.class);

	/**
	 * Déclaration du mapping applicable à la view.
	 * 
	 */
   @Override
   public void addViewControllers(ViewControllerRegistry registry) {
      registry.addViewController("/");
      LOGGER.info("addViewController(\"/\")");
   }

   /**
    * Déclaration du type JSP comme view.
    * @return
    */
   @Bean
   public ViewResolver viewResolver() {
	   LOGGER.info("ViewResolver");
	   
      InternalResourceViewResolver bean = new InternalResourceViewResolver();

      bean.setViewClass(JstlView.class);
      bean.setPrefix("/WEB-INF/views/");
      bean.setSuffix(".jsp");

      return bean;
   }
}