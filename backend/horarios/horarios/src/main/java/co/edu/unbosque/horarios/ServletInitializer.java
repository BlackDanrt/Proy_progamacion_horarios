package co.edu.unbosque.horarios;

import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;

/**
 * Configura la aplicación Spring Boot para su despliegue en un servidor de aplicaciones.
 *
 * @author Prisma Web
 */
public class ServletInitializer extends SpringBootServletInitializer {

	/**
	 * Configura la clase principal que inicia la aplicación Spring Boot.
	 *
	 * @param application constructor utilizado para configurar la aplicación.
	 * @return configuración de la aplicación con la clase principal HorariosApplication.
	 */
	@Override
	protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
		return application.sources(HorariosApplication.class);
	}

}