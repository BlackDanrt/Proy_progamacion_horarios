package co.edu.unbosque.horarios.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import co.edu.unbosque.horarios.entity.Administrador;
import co.edu.unbosque.horarios.entity.Profesor;
import co.edu.unbosque.horarios.security.JwtUtil;

/**
 * Controlador REST que gestiona la autenticación de los usuarios del sistema de
 * horarios.
 * <p>
 * Proporciona endpoints de inicio de sesión para profesores y administradores.
 * Si las credenciales son correctas, devuelve un token JWT junto con los datos
 * del usuario.
 * </p>
 *
 * @author Prisma Web
 * @version 1.0
 */
@RestController
@RequestMapping("/auth")
@CrossOrigin(origins = { "http://localhost:8080", "*" })
public class AuthController {

	/** Gestor encargado de la autenticación de los usuarios. */
	private final AuthenticationManager authenticationManager;

	/** Utilidad para la generación y validación de tokens JWT. */
	private final JwtUtil jwtUtil;

	/**
	 * Constructor de la clase.
	 *
	 * @param authenticationManager gestor encargado de la autenticación.
	 * @param jwtUtil               utilidad para la generación de tokens JWT.
	 */
	public AuthController(AuthenticationManager authenticationManager, JwtUtil jwtUtil) {
		this.authenticationManager = authenticationManager;
		this.jwtUtil = jwtUtil;
	}

	/**
	 * Inicia sesión como profesor.
	 *
	 * @param loginRequest correo y contraseña del profesor.
	 * @return token JWT y datos del profesor con estado 200, 403 si la cuenta no es
	 *         de profesor o 401 si las credenciales son incorrectas.
	 */
	@PostMapping("/login/profesor")
	public ResponseEntity<?> loginProfesor(@RequestBody LoginRequest loginRequest) {
		try {
			Authentication authentication = authenticationManager.authenticate(
					new UsernamePasswordAuthenticationToken(loginRequest.getCorreo(), loginRequest.getContrasenia()));

			UserDetails userDetails = (UserDetails) authentication.getPrincipal();
			String jwt = jwtUtil.generateToken(userDetails);

			if (userDetails instanceof Profesor profesor) {
				return ResponseEntity.ok(new AuthResponse(jwt, "PROFESOR", profesor.getId(), profesor.getNombre(),
						profesor.getApellido()));
			}

			return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Error: Esta cuenta no es de profesor.");

		} catch (AuthenticationException e) {
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Error: Correo o contraseña incorrectos.");
		}
	}

	/**
	 * Inicia sesión como administrador.
	 *
	 * @param loginRequest correo y contraseña del administrador.
	 * @return token JWT y datos del administrador con estado 200, 403 si la cuenta
	 *         no es de administrador o 401 si las credenciales son incorrectas.
	 */
	@PostMapping("/login/admin")
	public ResponseEntity<?> loginAdmin(@RequestBody LoginRequest loginRequest) {
		try {
			Authentication authentication = authenticationManager.authenticate(
					new UsernamePasswordAuthenticationToken(loginRequest.getCorreo(), loginRequest.getContrasenia()));

			UserDetails userDetails = (UserDetails) authentication.getPrincipal();
			String jwt = jwtUtil.generateToken(userDetails);

			if (userDetails instanceof Administrador admin) {
				return ResponseEntity.ok(
						new AuthResponse(jwt, "ADMINISTRADOR", admin.getId(), admin.getNombre(), admin.getApellido()));
			}

			return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Error: Esta cuenta no es de administrador.");

		} catch (AuthenticationException e) {
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Error: Correo o contraseña incorrectos.");
		}
	}

	/**
	 * Datos de inicio de sesión enviados por el usuario.
	 */
	public static class LoginRequest {
		/**
		 * Correo electrónico del usuario.
		 */
		private String correo;
		/**
		 * Contraseña del usuario.
		 */
		private String contrasenia;

		/** @return el correo electrónico del usuario */
		public String getCorreo() {
			return correo;
		}

		/** @param correo el correo electrónico a establecer */
		public void setCorreo(String correo) {
			this.correo = correo;
		}

		/** @return la contraseña del usuario */
		public String getContrasenia() {
			return contrasenia;
		}

		/** @param contrasenia la contraseña a establecer */
		public void setContrasenia(String contrasenia) {
			this.contrasenia = contrasenia;
		}
	}

	/**
	 * Respuesta de autenticación que contiene el token JWT y datos del usuario.
	 */
	public static class AuthResponse {
		/**
		 * Token JWT generado para el usuario.
		 */
		private String token;
		/**
		 * Rol del usuario (PROFESOR o ADMINISTRADOR).
		 */
		private String role;
		/**
		 * Identificador del usuario.
		 */
		private long id;
		/**
		 * Nombre del usuario.
		 */
		private String nombre;
		/**
		 * Apellido del usuario.
		 */
		private String apellido;

		/**
		 * Constructor de la respuesta de autenticación.
		 * 
		 * @param token    token generado
		 * @param role     rol del usuario
		 * @param id       identificador del usuario
		 * @param nombre   nombre del usuario
		 * @param apellido apellido del usuario
		 */
		public AuthResponse(String token, String role, long id, String nombre, String apellido) {
			this.token = token;
			this.role = role;
			this.id = id;
			this.nombre = nombre;
			this.apellido = apellido;
		}

		/**
		 * Obtiene el token JWT.
		 *
		 * @return token JWT.
		 */
		public String getToken() {
			return token;
		}

		/**
		 * Establece el token JWT.
		 *
		 * @param token token JWT.
		 */
		public void setToken(String token) {
			this.token = token;
		}

		/**
		 * Obtiene el rol del usuario.
		 *
		 * @return rol del usuario.
		 */
		public String getRole() {
			return role;
		}

		/**
		 * Establece el rol del usuario.
		 *
		 * @param role rol del usuario.
		 */
		public void setRole(String role) {
			this.role = role;
		}

		/**
		 * Obtiene el identificador del usuario.
		 *
		 * @return identificador del usuario.
		 */
		public long getId() {
			return id;
		}

		/**
		 * Establece el identificador del usuario.
		 *
		 * @param id identificador del usuario.
		 */
		public void setId(long id) {
			this.id = id;
		}

		/**
		 * Obtiene el nombre del usuario.
		 *
		 * @return nombre del usuario.
		 */
		public String getNombre() {
			return nombre;
		}

		/**
		 * Establece el nombre del usuario.
		 *
		 * @param nombre nombre del usuario.
		 */
		public void setNombre(String nombre) {
			this.nombre = nombre;
		}

		/**
		 * Obtiene el apellido del usuario.
		 *
		 * @return apellido del usuario.
		 */
		public String getApellido() {
			return apellido;
		}

		/**
		 * Establece el apellido del usuario.
		 *
		 * @param apellido apellido del usuario.
		 */
		public void setApellido(String apellido) {
			this.apellido = apellido;
		}
	}
}