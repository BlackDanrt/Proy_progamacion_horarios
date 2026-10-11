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

@RestController
@RequestMapping("/auth")
@CrossOrigin(origins = {"*"})
public class AuthController {

	/** Gestor encargado de la autenticación de los usuarios. */
	private final AuthenticationManager authenticationManager;

	/** Utilidad para la generación y validación de tokens JWT. */
	private final JwtUtil jwtUtil;
	
	public AuthController(AuthenticationManager authenticationManager, JwtUtil jwtUtil) {
		this.authenticationManager = authenticationManager;
		this.jwtUtil = jwtUtil;
	}

	@PostMapping("/login/profesor")
	public ResponseEntity<?> loginProfesor(@RequestBody LoginRequest loginRequest) {
		try {
			Authentication authentication = authenticationManager.authenticate(
					new UsernamePasswordAuthenticationToken(loginRequest.getCorreo(), loginRequest.getContrasenia()));

			UserDetails userDetails = (UserDetails) authentication.getPrincipal();
			String jwt = jwtUtil.generateToken(userDetails);

			if (userDetails instanceof Profesor profesor) {
				return ResponseEntity.ok(
						new AuthResponse(jwt, "PROFESOR", profesor.getId(), profesor.getNombre(), profesor.getApellido()));
			}

			return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Error: Esta cuenta no es de profesor.");

		} catch (AuthenticationException e) {
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Error: Correo o contraseña incorrectos.");
		}
	}
	
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
	
	public static class LoginRequest {
		private String correo;
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
		private String token;
		private String role;
		private long id;
		private String nombre;
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

		public String getToken() {
			return token;
		}

		public void setToken(String token) {
			this.token = token;
		}

		public String getRole() {
			return role;
		}

		public void setRole(String role) {
			this.role = role;
		}

		public long getId() {
			return id;
		}

		public void setId(long id) {
			this.id = id;
		}

		public String getNombre() {
			return nombre;
		}

		public void setNombre(String nombre) {
			this.nombre = nombre;
		}

		public String getApellido() {
			return apellido;
		}

		public void setApellido(String apellido) {
			this.apellido = apellido;
		}
	}
}
