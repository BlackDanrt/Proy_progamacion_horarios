package co.edu.unbosque.horarios.security;

// --------------- PENDIENTE POR MODIFICAR
import java.util.List;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.authorization.AuthorizationDecision;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import co.edu.unbosque.horarios.security.JwtAuthenticationFilter;

@Configuration
public class SecurityConfig {

	private final JwtAuthenticationFilter jwtAuthFilter;
	private final UserDetailsService userDetailsService;

	public SecurityConfig(JwtAuthenticationFilter jwtAuthFilter, UserDetailsService userDetailsService) {
		this.jwtAuthFilter = jwtAuthFilter;
		this.userDetailsService = userDetailsService;
	}

	@Bean
	public AuthenticationProvider authenticationProvider() {
		DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider(userDetailsService);
		authProvider.setPasswordEncoder(passwordEncoder());
		return authProvider;
	}

	@Bean
	public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
		return config.getAuthenticationManager();
	}

	@Bean
	public PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}

	@Bean
	public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
		http.csrf(csrf -> csrf.disable()).cors(Customizer.withDefaults())
				.authorizeHttpRequests(
						auth -> auth.requestMatchers(org.springframework.http.HttpMethod.OPTIONS, "/**").permitAll()
								.requestMatchers("/pokemon/auth/**", "/usuario/login", "/swagger-ui/**",
										"/v3/api-docs/**", "/usuario/traducir", "/pokemon/auth/enviar-codigo", "/error")
								.permitAll().anyRequest().access((authentication, context) -> {
									var authObj = authentication.get();

									if (authObj == null || !authObj.isAuthenticated()
											|| authObj instanceof org.springframework.security.authentication.AnonymousAuthenticationToken) {
										return new AuthorizationDecision(false);
									}

									boolean isAdmin = authObj.getAuthorities().stream()
											.anyMatch(a -> a.getAuthority().equals("ROLE_ADMINISTRADOR")
													|| a.getAuthority().equals("ADMINISTRADOR")
													|| a.getAuthority().contains("ADMINISTRADOR"));

									if (isAdmin) {
										return new AuthorizationDecision(true);
									}

									String path = context.getRequest().getServletPath();
									boolean isAdminOnly = path.equals("/ataque/banear")
											|| path.equals("/pokemon/cargar") || path.equals("/pokemon/cargarbd")
											|| path.equals("/usuario/mostrartodo")
											|| path.equals("/usuario/buscarpornombre")
											|| path.equals("/usuario/buscarporcorreo")
											|| path.equals("/usuario/buscarporrol") || path.equals("/usuario/crear")
											|| path.equals("/usuario/actualizar") || path.equals("/usuario/eliminar")
											|| path.equals("/auditoria/mostrartodo")
											|| path.equals("/pokemon/actualizar-configuracion")
											|| path.equals("/inventario/listar-todo");

									if (isAdminOnly) {
										return new AuthorizationDecision(false);
									}

									boolean isUsuarioAllowed = path.startsWith("/captura/")
											|| path.startsWith("/combate/") || path.startsWith("/centropokemon/")
											|| path.startsWith("/tienda/") || path.startsWith("/inventario/")
											|| path.startsWith("/item/") || path.startsWith("/pokemon/")
											|| path.startsWith("/usuario/genero") || path.startsWith("/centropokemon");

									return new AuthorizationDecision(isUsuarioAllowed);
								}))
				.sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
				.authenticationProvider(authenticationProvider())
				.addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);

		return http.build();
	}

	@Bean
	public CorsConfigurationSource corsConfigurationSource() {
		CorsConfiguration configuration = new CorsConfiguration();
		configuration
				.setAllowedOrigins(List.of("http://localhost:4200", "https://ubiquitous-monstera-1bcf49.netlify.app"));
		configuration.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
		configuration
				.setAllowedHeaders(List.of("Authorization", "Content-Type", "Accept", "Origin", "X-Requested-With"));
		configuration.setAllowCredentials(true);
		UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
		source.registerCorsConfiguration("/**", configuration);
		return source;
	}
}