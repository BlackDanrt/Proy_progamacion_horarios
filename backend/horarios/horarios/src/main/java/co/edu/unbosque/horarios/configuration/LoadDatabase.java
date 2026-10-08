package co.edu.unbosque.horarios.configuration;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

//import co.edu.unbosque.horarios.entity.Usuario;
//import co.edu.unbosque.horarios.entity.Usuario.Role;
//import co.edu.unbosque.horarios.repository.UsuarioRepository;

/**
 * Clase de configuración encargada de inicializar
 * datos predeterminados en la base de datos.
 * 
 * En este caso, se asegura de crear o actualizar
 * un usuario administrador al iniciar la aplicación.
 * 
 * @version 1.0
 */
@Configuration
public class LoadDatabase {

	/**
	 * Logger utilizado para registrar mensajes
	 * relacionados con la carga de datos iniciales.
	 */
	private static final Logger log = LoggerFactory.getLogger(LoadDatabase.class);

	/**
	 * Contraseña predeterminada obtenida
	 * desde el archivo de configuración.
	 */
	@Value("${app.default.user.password}")
	private String defaultPassword;

	/**
	 * Inicializa la base de datos con un usuario administrador.
	 * 
	 * Si el usuario administrador no existe,
	 * se crea automáticamente. Si ya existe,
	 * se actualizan sus datos principales.
	 * 
	 * @param userRepo repositorio de usuarios.
	 * @param passwordEncoder codificador de contraseñas.
	 * @return CommandLineRunner que ejecuta la inicialización.
	 */
//	@Bean
//	CommandLineRunner initDatabase(UsuarioRepository userRepo, PasswordEncoder passwordEncoder) {
//		return args -> {
//
//			/**
//			 * Busca el usuario administrador por correo.
//			 * Si no existe, crea una nueva instancia.
//			 */
//			Usuario admin = userRepo.findByCorreo("administrador@gmail.com").orElse(new Usuario());
//
//			admin.setNombre("AdministradorPokedes");
//			admin.setCorreo("administrador@gmail.com");
//			admin.setContrasenia(passwordEncoder.encode(defaultPassword));
//			admin.setRol(Role.ADMINISTRADOR);
//			admin.setActivado(true);
//
//			/**
//			 * Guarda o actualiza el usuario administrador
//			 * en la base de datos.
//			 */
//			userRepo.save(admin);
//
//			/**
//			 * Registra un mensaje indicando que el administrador
//			 * fue inicializado correctamente.
//			 */
//			log.info("ADMINISTRADOR listo.");
//		};
//	}
}