package co.edu.unbosque.horarios.security;

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
								.requestMatchers("/auth/**", "/swagger-ui/**", "/v3/api-docs/**", "/error").permitAll()
								.anyRequest().access((authentication, context) -> {
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

									boolean isAdminOnly = path.startsWith("/administrador") || path.contains("/crear")
											|| path.contains("/actualizar") || path.contains("/eliminar");

									if (isAdminOnly) {
										return new AuthorizationDecision(false);
									}

									boolean isUsuarioAllowed = path.startsWith("/grupo")
											|| path.startsWith("/franjahoraria") || path.startsWith("/asignatura")
											|| path.startsWith("/salon") || path.startsWith("/profesor");

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
				.setAllowedOrigins(List.of("http://localhost:4200", "http://localhost:3000", "http://localhost:5173", "http://localhost:8080", "https://ubiquitous-monstera-1bcf49.netlify.app"));
		configuration.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
		configuration
				.setAllowedHeaders(List.of("Authorization", "Content-Type", "Accept", "Origin", "X-Requested-With"));
		configuration.setAllowCredentials(true);
		UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
		source.registerCorsConfiguration("/**", configuration);
		return source;
	}
}