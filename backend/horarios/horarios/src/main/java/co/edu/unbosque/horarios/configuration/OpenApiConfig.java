package co.edu.unbosque.horarios.configuration;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.examples.Example;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.media.Content;
import io.swagger.v3.oas.models.media.MediaType;
import io.swagger.v3.oas.models.responses.ApiResponse;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Clase de configuración de OpenAPI y Swagger.
 * 
 * Define la documentación interactiva de la API REST del sistema de horarios,
 * incluyendo: autenticación JWT, descripción de módulos y permisos de roles.
 * 
 * @version 1.0
 */
@Configuration
public class OpenApiConfig {

	@Bean
	public OpenAPI customOpenAPI() {

		String descripcionPrincipal = """
				# API REST - Sistema de Horarios

				Esta API permite administrar asignaturas, grupos, profesores, salones y franjas horarias utilizando autenticación JWT y control de acceso basado en roles.

				## Roles y permisos
				| Rol | Descripción de Permisos |
				| :--- | :--- |
				| **PROFESOR** | Solo lectura. Puede visualizar grupos, salones, asignaturas y franjas horarias. |
				| **ADMINISTRADOR** | Control total. Puede crear, actualizar, eliminar y gestionar absolutamente todo. |

				## Endpoints protegidos
				### Autenticación
				* **LOGIN / ADMIN / PROFESOR** → Público (Sin token)

				### Consultas (GET)
				* **VER Asignaturas, Grupos, Salones, Franjas Horarias** → PROFESOR y ADMINISTRADOR

				### Gestión y Escritura (POST, PUT, DELETE)
				* **CREAR / ACTUALIZAR / ELIMINAR en cualquier módulo** → Solo ADMINISTRADOR
				""";

		String descripcionSeguridad = """
				Autenticación basada en JWT (JSON Web Token).

				### ¿Cómo iniciar?
				1. Inicia sesión en `/auth/login/admin` o `/auth/login/profesor`.
				2. Obtén el token JWT de la respuesta.
				3. Haz clic en el botón **Authorize** (candado verde).
				4. Escribe exactamente: `Bearer tu_token_jwt`
				5. Presiona **Authorize** y luego **Close**.
				""";

		Info info = new Info().title("API REST - Sistema de Horarios").version("1.0").description(descripcionPrincipal)
				.contact(new Contact().name("Equipo de Desarrollo").email("gssanchez@unbosque.edu.co")
						.url("https://github.com/sanchezgina0312"))
				.license(new License().name("MIT").url("https://opensource.org/licenses/MIT"));

		return new OpenAPI().info(info).addSecurityItem(new SecurityRequirement().addList("bearerAuth"))
				.components(new Components()

						.addSecuritySchemes("bearerAuth",
								new SecurityScheme().name("JWT Authentication").type(SecurityScheme.Type.HTTP)
										.scheme("bearer").bearerFormat("JWT").description(descripcionSeguridad))

						.addResponses("UnauthorizedError",
								createResponse("Token inválido", "Unauthorized", "Token JWT inválido o expirado"))

						.addResponses("ForbiddenError",
								createResponse("Sin permisos", "Forbidden",
										"No tienes permisos (Ej: un profesor intentando editar)"))

						.addResponses("NotFoundError",
								createResponse("No encontrado", "Not Found", "El recurso no existe"))

						.addResponses("ConflictError",
								createResponse("Conflicto", "Conflict", "Error en los datos o ya existe")));
	}

	private ApiResponse createResponse(String d, String e, String m) {

		return new ApiResponse().description(d).content(new Content().addMediaType("application/json", new MediaType()
				.addExamples("error", new Example().value("{\"error\": \"" + e + "\", \"message\": \"" + m + "\"}"))));
	}
}