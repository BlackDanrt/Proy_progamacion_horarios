package co.edu.unbosque.horarios.entity;

import java.util.Collection;
import java.util.List;
import java.util.Objects;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import jakarta.persistence.Column;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.MappedSuperclass;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

/**
 * Representa la clase base abstracta de las personas del sistema de gestión de
 * horarios. Contiene los datos personales y las credenciales de acceso.
 * Implementa UserDetails para permitir la integración con Spring Security.
 *
 * @author Prisma Web
 */
@MappedSuperclass
@JsonIgnoreProperties({ "password", "username", "authorities", "enabled", "accountNonExpired", "accountNonLocked",
		"credentialsNonExpired" })
public abstract class Persona implements UserDetails {

	/**
	 * Identificador de serialización de la clase.
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * Identificador único de la persona.
	 */
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private long id;

	/**
	 * Nombre de la persona.
	 */
	@Column(nullable = false)
	@NotBlank(message = "El nombre no puede estar vacío")
	private String nombre;

	/**
	 * Apellido de la persona.
	 */
	@Column(nullable = false)
	@NotBlank(message = "El apellido no puede estar vacío")
	private String apellido;

	/**
	 * Número de documento de identidad de la persona.
	 */
	@Column(unique = true, nullable = false)
	private long documento;

	/**
	 * Correo electrónico único utilizado para identificar a la persona.
	 */
	@Column(nullable = false, unique = true)
	@NotBlank(message = "El correo no puede estar vacío")
	@Email(message = "El correo no es válido")
	private String correo;

	/**
	 * Contraseña utilizada para acceder al sistema.
	 */
	@Column(nullable = false)
	@NotBlank(message = "La contraseña no puede estar vacía")
	@JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
	private String contrasenia;

	/**
	 * Constructor vacío de la clase Persona.
	 */
	public Persona() {
		// TODO Auto-generated constructor stub
	}

	/**
	 * Construye una persona con sus datos personales y credenciales de acceso.
	 *
	 * @param nombre      nombre de la persona.
	 * @param apellido    apellido de la persona.
	 * @param documento   número de documento de identidad.
	 * @param correo      correo electrónico de la persona.
	 * @param contrasenia contraseña de acceso al sistema.
	 */
	public Persona(@NotBlank(message = "El nombre no puede estar vacío") String nombre,
			@NotBlank(message = "El apellido no puede estar vacío") String apellido, long documento,
			@NotBlank(message = "El correo no puede estar vacío") @Email(message = "El correo no es válido") String correo,
			@NotBlank(message = "La contraseña no puede estar vacía") String contrasenia) {
		super();
		this.nombre = nombre;
		this.apellido = apellido;
		this.documento = documento;
		this.correo = correo;
		this.contrasenia = contrasenia;
	}

	/**
	 * Obtiene el identificador de la persona.
	 *
	 * @return identificador único de la persona.
	 */
	public long getId() {
		return id;
	}

	/**
	 * Modifica el identificador de la persona.
	 *
	 * @param id nuevo identificador de la persona.
	 */
	public void setId(long id) {
		this.id = id;
	}

	/**
	 * Obtiene el nombre de la persona.
	 *
	 * @return nombre de la persona.
	 */
	public String getNombre() {
		return nombre;
	}

	/**
	 * Modifica el nombre de la persona.
	 *
	 * @param nombre nuevo nombre de la persona.
	 */
	public void setNombre(String nombre) {
		this.nombre = nombre;
	}

	/**
	 * Obtiene el apellido de la persona.
	 *
	 * @return apellido de la persona.
	 */
	public String getApellido() {
		return apellido;
	}

	/**
	 * Modifica el apellido de la persona.
	 *
	 * @param apellido nuevo apellido de la persona.
	 */
	public void setApellido(String apellido) {
		this.apellido = apellido;
	}

	/**
	 * Obtiene el número de documento de la persona.
	 *
	 * @return número de documento de identidad.
	 */
	public long getDocumento() {
		return documento;
	}

	/**
	 * Modifica el número de documento de la persona.
	 *
	 * @param documento nuevo número de documento.
	 */
	public void setDocumento(long documento) {
		this.documento = documento;
	}

	/**
	 * Obtiene el correo electrónico de la persona.
	 *
	 * @return correo electrónico registrado.
	 */
	public String getCorreo() {
		return correo;
	}

	/**
	 * Modifica el correo electrónico de la persona.
	 *
	 * @param correo nuevo correo electrónico.
	 */
	public void setCorreo(String correo) {
		this.correo = correo;
	}

	/**
	 * Obtiene la contraseña de la persona.
	 *
	 * @return contraseña de acceso al sistema.
	 */
	public String getContrasenia() {
		return contrasenia;
	}

	/**
	 * Modifica la contraseña de la persona.
	 *
	 * @param contrasenia nueva contraseña de acceso.
	 */
	public void setContrasenia(String contrasenia) {
		this.contrasenia = contrasenia;
	}

	/**
	 * Calcula el código hash de la persona utilizando el correo electrónico y el
	 * número de documento.
	 *
	 * @return código hash de la persona.
	 */
	@Override
	public int hashCode() {
		return Objects.hash(correo, Long.valueOf(documento));
	}

	/**
	 * Compara esta persona con otro objeto para determinar si son iguales.
	 *
	 * @param obj objeto con el que se compara la persona.
	 * @return true si ambos objetos tienen el mismo correo y documento y pertenecen
	 *         a la misma clase; false en caso contrario.
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Persona other = (Persona) obj;
		return Objects.equals(correo, other.correo) && documento == other.documento;
	}

	/**
	 * Devuelve una representación en texto de la persona con sus datos personales y
	 * su correo electrónico.
	 *
	 * @return cadena con la información de la persona.
	 */
	@Override
	public String toString() {
		return "Persona [nombre=" + nombre + ", apellido=" + apellido + ", documento=" + documento + ", correo="
				+ correo + "]";
	}

	/**
	 * Obtiene los permisos de acceso correspondientes al tipo de persona. Asigna el
	 * rol PROFESOR si la persona es una instancia de Profesor; en caso contrario,
	 * asigna el rol ADMINISTRADOR.
	 *
	 * @return colección de autoridades asignadas a la persona.
	 */
	@Override
	public Collection<? extends GrantedAuthority> getAuthorities() {
		String rol = (this instanceof Profesor) ? "PROFESOR" : "ADMINISTRADOR";
		return List.of(new SimpleGrantedAuthority("ROLE_" + rol));
	}

	/**
	 * Obtiene la contraseña utilizada por Spring Security para autenticar a la
	 * persona.
	 *
	 * @return contraseña de la persona.
	 */
	@Override
	public String getPassword() {
		return this.contrasenia;
	}

	/**
	 * Obtiene el nombre de usuario utilizado para la autenticación. En este sistema
	 * corresponde al correo electrónico.
	 *
	 * @return correo electrónico de la persona.
	 */
	@Override
	public String getUsername() {
		return this.correo;
	}

	/**
	 * Indica si la cuenta de la persona no ha expirado.
	 *
	 * @return true, porque la cuenta no se considera expirada.
	 */
	@Override
	public boolean isAccountNonExpired() {
		return true;
	}

	/**
	 * Indica si la cuenta de la persona no está bloqueada.
	 *
	 * @return true, porque la cuenta no se considera bloqueada.
	 */
	@Override
	public boolean isAccountNonLocked() {
		return true;
	}

	/**
	 * Indica si las credenciales de la persona no han expirado.
	 *
	 * @return true, porque las credenciales no se consideran expiradas.
	 */
	@Override
	public boolean isCredentialsNonExpired() {
		return true;
	}

	/**
	 * Indica si la cuenta de la persona está habilitada.
	 *
	 * @return true, porque la cuenta se considera habilitada.
	 */
	@Override
	public boolean isEnabled() {
		return true;
	}
}