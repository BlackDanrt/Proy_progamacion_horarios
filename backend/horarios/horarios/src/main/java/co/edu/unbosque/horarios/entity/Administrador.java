package co.edu.unbosque.horarios.entity;

import java.util.Objects;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

/**
 * Representa un administrador del sistema de gestión de horarios.
 * Hereda los atributos personales de la clase Persona y agrega
 * el código del administrador y su estado.
 *
 * @author Prisma Web
 */
@Entity
@Table(name = "Administrador")
public class Administrador extends Persona {

	/**
	 * Código único del administrador.
	 */
	@Column(unique = true, nullable = false, name = "codigo_administrador")
	@NotBlank(message = "El código del administrador no peude estar vacío")
	private String codigoAdministrador;

	/**
	 * Indica si el administrador se encuentra activo.
	 */
	@Column(nullable = false)
	private boolean estado;

	/**
	 * Constructor vacío de la clase Administrador.
	 */
	public Administrador() {
		// TODO Auto-generated constructor stub
	}

	/**
	 * Construye un administrador con su código y estado.
	 *
	 * @param codigoAdministrador código del administrador.
	 * @param estado estado del administrador.
	 */
	public Administrador(String codigoAdministrador, boolean estado) {
		super();
		this.codigoAdministrador = codigoAdministrador;
		this.estado = estado;
	}

	/**
	 * Construye un administrador con sus datos personales,
	 * código y estado.
	 *
	 * @param nombre nombre del administrador.
	 * @param apellido apellido del administrador.
	 * @param documento número de documento del administrador.
	 * @param correo correo electrónico del administrador.
	 * @param contrasenia contraseña del administrador.
	 * @param codigoAdministrador código del administrador.
	 * @param estado estado del administrador.
	 */
	public Administrador(@NotBlank(message = "El nombre no puede estar vacío") String nombre,
			@NotBlank(message = "El apellido no puede estar vacío") String apellido, long documento,
			@NotBlank(message = "El correo no puede estar vacío") @Email(message = "El correo no es válido") String correo,
			@NotBlank(message = "La contraseña no puede estar vacía") String contrasenia, String codigoAdministrador,
			boolean estado) {
		super(nombre, apellido, documento, correo, contrasenia);
		this.codigoAdministrador = codigoAdministrador;
		this.estado = estado;
	}

	/**
	 * Construye un administrador con sus datos personales.
	 *
	 * @param nombre nombre del administrador.
	 * @param apellido apellido del administrador.
	 * @param documento número de documento del administrador.
	 * @param correo correo electrónico del administrador.
	 * @param contrasenia contraseña del administrador.
	 */
	public Administrador(@NotBlank(message = "El nombre no puede estar vacío") String nombre,
			@NotBlank(message = "El apellido no puede estar vacío") String apellido, long documento,
			@NotBlank(message = "El correo no puede estar vacío") @Email(message = "El correo no es válido") String correo,
			@NotBlank(message = "La contraseña no puede estar vacía") String contrasenia) {
		super(nombre, apellido, documento, correo, contrasenia);
		// TODO Auto-generated constructor stub
	}

	/**
	 * Obtiene el código del administrador.
	 *
	 * @return código del administrador.
	 */
	public String getCodigoAdministrador() {
		return codigoAdministrador;
	}

	/**
	 * Modifica el código del administrador.
	 *
	 * @param codigoAdministrador nuevo código del administrador.
	 */
	public void setCodigoAdministrador(String codigoAdministrador) {
		this.codigoAdministrador = codigoAdministrador;
	}

	/**
	 * Indica si el administrador está activo.
	 *
	 * @return true si el administrador está activo; false en caso contrario.
	 */
	public boolean isEstado() {
		return estado;
	}

	/**
	 * Modifica el estado del administrador.
	 *
	 * @param estado nuevo estado del administrador.
	 */
	public void setEstado(boolean estado) {
		this.estado = estado;
	}

	/**
	 * Calcula el código hash del administrador.
	 *
	 * @return código hash basado en los atributos heredados y propios.
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + Objects.hash(codigoAdministrador, estado);
		return result;
	}

	/**
	 * Compara este administrador con otro objeto para determinar
	 * si ambos son iguales.
	 *
	 * @param obj objeto con el que se compara el administrador.
	 * @return true si ambos objetos son iguales; false en caso contrario.
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (!super.equals(obj))
			return false;
		if (getClass() != obj.getClass())
			return false;
		Administrador other = (Administrador) obj;
		return Objects.equals(codigoAdministrador, other.codigoAdministrador) && estado == other.estado;
	}

	/**
	 * Devuelve una representación en texto del administrador.
	 *
	 * @return cadena con los datos heredados, el código y el estado.
	 */
	@Override
	public String toString() {
		return super.toString() + ", Administrador [codigoAdminsitrador=" + codigoAdministrador + ", estado=" + estado + "]";
	}

}