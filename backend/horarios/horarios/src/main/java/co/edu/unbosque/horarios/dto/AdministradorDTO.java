package co.edu.unbosque.horarios.dto;

import java.util.Objects;

import co.edu.unbosque.horarios.entity.Persona;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public class AdministradorDTO extends Persona {

	private String codigoAdministrador;
	private boolean estado;

	public AdministradorDTO() {
		// TODO Auto-generated constructor stub
	}

	public AdministradorDTO(String codigoAdministrador, boolean estado) {
		super();
		this.codigoAdministrador = codigoAdministrador;
		this.estado = estado;
	}

	public AdministradorDTO(@NotBlank(message = "El nombre no puede estar vacío") String nombre,
			@NotBlank(message = "El apellido no puede estar vacío") String apellido, long documento,
			@NotBlank(message = "El correo no puede estar vacío") @Email(message = "El correo no es válido") String correo,
			@NotBlank(message = "La contraseña no puede estar vacía") String contrasenia, String codigoAdministrador,
			boolean estado) {
		super(nombre, apellido, documento, correo, contrasenia);
		this.codigoAdministrador = codigoAdministrador;
		this.estado = estado;
	}

	public AdministradorDTO(@NotBlank(message = "El nombre no puede estar vacío") String nombre,
			@NotBlank(message = "El apellido no puede estar vacío") String apellido, long documento,
			@NotBlank(message = "El correo no puede estar vacío") @Email(message = "El correo no es válido") String correo,
			@NotBlank(message = "La contraseña no puede estar vacía") String contrasenia) {
		super(nombre, apellido, documento, correo, contrasenia);
		// TODO Auto-generated constructor stub
	}

	public String getCodigoAdministrador() {
		return codigoAdministrador;
	}

	public void setCodigoAdministrador(String codigoAdministrador) {
		this.codigoAdministrador = codigoAdministrador;
	}

	public boolean isEstado() {
		return estado;
	}

	public void setEstado(boolean estado) {
		this.estado = estado;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + Objects.hash(codigoAdministrador, estado);
		return result;
	}

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
		AdministradorDTO other = (AdministradorDTO) obj;
		return Objects.equals(codigoAdministrador, other.codigoAdministrador) && estado == other.estado;
	}

	@Override
	public String toString() {
		return super.toString() + ", AdministradorDTO [codigoAdministrador=" + codigoAdministrador + ", estado="
				+ estado + "]";
	}

}
