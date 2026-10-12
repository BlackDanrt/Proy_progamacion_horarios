package co.edu.unbosque.horarios.entity;

import java.util.Collection;
import java.util.List;
import java.util.Objects;

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

@MappedSuperclass
public abstract class Persona implements UserDetails {

	/**
	 * Identificador de serialización de la clase.
	 */
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private long id;

	@Column(nullable = false)
	@NotBlank(message = "El nombre no puede estar vacío")
	private String nombre;

	@Column(nullable = false)
	@NotBlank(message = "El apellido no puede estar vacío")
	private String apellido;

	@Column(unique = true, nullable = false)
	private long documento;

	@Column(nullable = false, unique = true)
	@NotBlank(message = "El correo no puede estar vacío")
	@Email(message = "El correo no es válido")
	private String correo;

	@Column(nullable = false)
	@NotBlank(message = "La contraseña no puede estar vacía")
	private String contrasenia;

	public Persona() {
		// TODO Auto-generated constructor stub
	}

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

	public long getDocumento() {
		return documento;
	}

	public void setDocumento(long documento) {
		this.documento = documento;
	}

	public String getCorreo() {
		return correo;
	}

	public void setCorreo(String correo) {
		this.correo = correo;
	}

	public String getContrasenia() {
		return contrasenia;
	}

	public void setContrasenia(String contrasenia) {
		this.contrasenia = contrasenia;
	}

	@Override
	public int hashCode() {
		return Objects.hash(correo, Long.valueOf(documento));
	}

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

	@Override
	public String toString() {
		return "Persona [nombre=" + nombre + ", apellido=" + apellido + ", documento=" + documento + ", correo="
				+ correo + "";
	}

	// Métodos de UserDetails
	
	@Override
	public Collection<? extends GrantedAuthority> getAuthorities() {
		String rol = (this instanceof Profesor) ? "PROFESOR" : "ADMINISTRADOR";
		return List.of(new SimpleGrantedAuthority("ROLE_" + rol));
	}

	@Override
	public String getPassword() {
		return this.contrasenia;
	}

	@Override
	public String getUsername() {
		return this.correo;
	}

	@Override
	public boolean isAccountNonExpired() {
		return true;
	}

	@Override
	public boolean isAccountNonLocked() {
		return true;
	}

	@Override
	public boolean isCredentialsNonExpired() {
		return true;
	}

	@Override
	public boolean isEnabled() {
		return true;
	}
}