package co.edu.unbosque.horarios.configuration;

// POR MODIFICAR
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
 * Define la documentación interactiva de la API REST del sistema de batallas
 * Pokémon, incluyendo: autenticación JWT, descripción de módulos, permisos de
 * roles y respuestas personalizadas.
 * 
 * @version 1.0
 */
@Configuration
public class OpenApiConfig {

	/**
	 * Configura la documentación principal de OpenAPI.
	 * 
	 * Define información general de la API, configuración de seguridad JWT,
	 * respuestas globales y documentación de módulos del sistema.
	 * 
	 * @return configuración personalizada de OpenAPI.
	 */
	@Bean
	public OpenAPI customOpenAPI() {

		/**
		 * Descripción principal mostrada en Swagger.
		 */
		String descripcionPrincipal = """
				# API REST - Sistema de Batallas Pokémon

				Esta API permite administrar entrenadores, Pokémon, combates, capturas y la tienda del mundo Pokémon utilizando autenticación JWT y control de acceso basado en roles.

				## Módulos del sistema
				* **Autenticación**: Login y registro de Entrenadores vía correo electrónico.
				* **Pokédex**: Carga, consulta e historias de Pokémon desde la PokeAPI.
				* **Aventura**: Capturas, Combates y Centro Pokémon.
				* **Economía**: Tienda de objetos e Inventario (Mochila).
				* **Gestión**: Administración de usuarios y ataques (solo ADMINISTRADOR).

				## Roles y permisos
				| Rol | Descripción de Permisos |
				| :--- | :--- |
				| **USUARIO** | Jugar (Capturar, combatir, comprar, curar, ver catálogo). |
				| **ADMINISTRADOR** | Control total (Cargar API, banear ataques, gestionar usuarios). |

				## Endpoints protegidos
				### Autenticación
				* **LOGIN / REGISTRO** → Público (Sin token) — usar correo electrónico como identificador

				### Pokémon y Sistema
				* **CARGAR POKEAPI / BANEAR ATAQUES** → ADMINISTRADOR
				* **VER CATÁLOGO / ESCUCHAR GRITOS / HISTORIAS / SPRITES** → USUARIO y ADMINISTRADOR

				### Aventura (Combates, Capturas, Tienda, Centro Pokémon)
				* **TODAS LAS ACCIONES DE JUEGO** → USUARIO y ADMINISTRADOR

				### Gestión de Entrenadores (Usuarios)
				* **CREAR / LOGIN** → Público (Sin token)
				* **LISTAR / BUSCAR / ACTUALIZAR / ELIMINAR** → ADMINISTRADOR

				## Códigos HTTP comunes
				* **200**: Operación exitosa | **201**: Recurso creado | **202**: Operación aceptada
				* **400**: Error en la solicitud | **401**: No autenticado | **403**: Equipo Rocket (Acceso denegado)
				* **404**: Recurso no encontrado | **409**: Conflicto (nombre o correo ya en uso)
				""";

		/**
		 * Descripción de la autenticación JWT.
		 */
		String descripcionSeguridad = """
				Autenticación basada en JWT (JSON Web Token).

				### ¿Cómo iniciar tu aventura?
				1. Registra tu entrenador en `/pokemon/auth/register` o inicia sesión en `/pokemon/auth/login`.
				2. Usa tu **correo electrónico** como identificador (no el nombre de usuario).
				3. Obtén el token JWT de la respuesta.
				4. Haz clic en el botón **Authorize** (candado verde).
				5. Escribe exactamente: `Bearer tu_token_jwt`
				6. Presiona **Authorize** y luego **Close**.

				¡Una vez autenticado podrás consumir los endpoints según tu rol!
				""";

		/**
		 * Información general de la API.
		 */
		Info info = new Info().title("API REST - Batalla Pokémon").version("1.0").description(descripcionPrincipal)
				.contact(new Contact().name("Equipo de Desarrollo Pokémon").email("gssanchez@unbosque.edu.co")
						.url("https://github.com/sanchezgina0312/pokemon-battle-backend"))
				.license(new License().name("MIT").url("https://opensource.org/licenses/MIT"));

		/**
		 * Construcción y configuración final de OpenAPI.
		 */
		return new OpenAPI().info(info).addSecurityItem(new SecurityRequirement().addList("bearerAuth"))
				.components(new Components()

						/**
						 * Configuración del esquema de seguridad JWT.
						 */
						.addSecuritySchemes("bearerAuth",
								new SecurityScheme().name("JWT Authentication").type(SecurityScheme.Type.HTTP)
										.scheme("bearer").bearerFormat("JWT").description(descripcionSeguridad))

						/**
						 * Respuesta personalizada para token inválido.
						 */
						.addResponses("UnauthorizedError",
								createResponse("Token inválido", "Unauthorized", "Token JWT inválido o expirado"))

						/**
						 * Respuesta personalizada para acceso denegado.
						 */
						.addResponses("ForbiddenError",
								createResponse("Sin permisos", "Forbidden",
										"El Equipo Rocket no te deja pasar (Acceso denegado)"))

						/**
						 * Respuesta personalizada para recursos no encontrados.
						 */
						.addResponses("NotFoundError",
								createResponse("No encontrado", "Not Found", "El recurso o Pokémon no existe"))

						/**
						 * Respuesta personalizada para conflictos de datos.
						 */
						.addResponses("ConflictError",
								createResponse("Conflicto", "Conflict", "El nombre o correo ya está registrado"))

						/**
						 * Respuesta personalizada para falta de dinero.
						 */
						.addResponses("PaymentError",
								createResponse("Sin Pokédolares", "Payment Required", "No tienes suficiente dinero")));
	}

	/**
	 * Crea una respuesta personalizada reutilizable para OpenAPI.
	 * 
	 * @param d descripción de la respuesta.
	 * @param e nombre del error.
	 * @param m mensaje detallado del error.
	 * @return objeto ApiResponse configurado.
	 */
	private ApiResponse createResponse(String d, String e, String m) {

		return new ApiResponse().description(d).content(new Content().addMediaType("application/json", new MediaType()
				.addExamples("error", new Example().value("{\"error\": \"" + e + "\", \"message\": \"" + m + "\"}"))));
	}
}