package co.edu.unbosque.horarios.dto;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import co.edu.unbosque.horarios.entity.FranjaHoraria;
import co.edu.unbosque.horarios.entity.Persona;
import co.edu.unbosque.horarios.util.enums.Escalafon;
import co.edu.unbosque.horarios.util.enums.Especialidad;
import co.edu.unbosque.horarios.util.enums.TipoVinculacion;
import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.persistence.JoinColumn;

@Entity
@Table(name = "Profesor")
public class ProfesorDTO extends Persona {

	@Enumerated(EnumType.STRING)
	@Column(nullable = false)
	private TipoVinculacion tipoVinculacion;

	@Enumerated(EnumType.STRING)
	@Column(nullable = false)
	private Escalafon escalafon;

	@ElementCollection(targetClass = Especialidad.class, fetch = FetchType.EAGER)
	@CollectionTable(name = "profesor_especialidad", joinColumns = @JoinColumn(name = "profesor_id"))
	@Enumerated(EnumType.STRING)
	@Column(name = "especialidad", nullable = false)
	private List<Especialidad> especialidades = new ArrayList<>();

	@ManyToMany
	@JoinTable(name = "profesor_franja", joinColumns = @JoinColumn(name = "profesor_id"), inverseJoinColumns = @JoinColumn(name = "franja_id"))
	private List<FranjaHoraria> disponibilidadHoraria = new ArrayList<>();

	@Min(value = 1, message = "Debe tener al menos 1 hora semanal")
	@Max(value = 40, message = "No puede superar las 40 horas semanales")
	private int horasMaximasSemanales;

	public ProfesorDTO() {}
	
	public ProfesorDTO(@NotBlank(message = "El nombre no puede estar vacío") String nombre,
			@NotBlank(message = "El apellido no puede estar vacío") String apellido, long documento,
			@NotBlank(message = "El correo no puede estar vacío") @Email(message = "El correo no es válido") String correo,
			@NotBlank(message = "La contraseña no puede estar vacía") String contrasenia) {
		super(nombre, apellido, documento, correo, contrasenia);
	}

	public ProfesorDTO(TipoVinculacion tipoVinculacion, Escalafon escalafon, List<Especialidad> especialidades,
			List<FranjaHoraria> disponibilidadHoraria,
			@Min(value = 1, message = "Debe tener al menos 1 hora semanal") @Max(value = 40, message = "No puede superar las 40 horas semanales") int horasMaximasSemanales) {
		super();
		this.tipoVinculacion = tipoVinculacion;
		this.escalafon = escalafon;
		this.especialidades = especialidades;
		this.disponibilidadHoraria = disponibilidadHoraria;
		this.horasMaximasSemanales = horasMaximasSemanales;
	}
	
	public ProfesorDTO(@NotBlank(message = "El nombre no puede estar vacío") String nombre,
			@NotBlank(message = "El apellido no puede estar vacío") String apellido, long documento,
			@NotBlank(message = "El correo no puede estar vacío") @Email(message = "El correo no es válido") String correo,
			@NotBlank(message = "La contraseña no puede estar vacía") String contrasenia,
			TipoVinculacion tipoVinculacion, Escalafon escalafon, List<Especialidad> especialidades,
			List<FranjaHoraria> disponibilidadHoraria,
			@Min(value = 1, message = "Debe tener al menos 1 hora semanal") @Max(value = 40, message = "No puede superar las 40 horas semanales") int horasMaximasSemanales) {
		super(nombre, apellido, documento, correo, contrasenia);
		this.tipoVinculacion = tipoVinculacion;
		this.escalafon = escalafon;
		this.especialidades = especialidades;
		this.disponibilidadHoraria = disponibilidadHoraria;
		this.horasMaximasSemanales = horasMaximasSemanales;
	}

	public TipoVinculacion getTipoVinculacion() {
		return tipoVinculacion;
	}

	public void setTipoVinculacion(TipoVinculacion tipoVinculacion) {
		this.tipoVinculacion = tipoVinculacion;
	}

	public Escalafon getEscalafon() {
		return escalafon;
	}

	public void setEscalafon(Escalafon escalafon) {
		this.escalafon = escalafon;
	}

	public List<Especialidad> getEspecialidades() {
		return especialidades;
	}

	public void setEspecialidades(List<Especialidad> especialidades) {
		this.especialidades = especialidades;
	}

	public List<FranjaHoraria> getDisponibilidadHoraria() {
		return disponibilidadHoraria;
	}

	public void setDisponibilidadHoraria(List<FranjaHoraria> disponibilidadHoraria) {
		this.disponibilidadHoraria = disponibilidadHoraria;
	}

	public int getHorasMaximasSemanales() {
		return horasMaximasSemanales;
	}

	public void setHorasMaximasSemanales(int horasMaximasSemanales) {
		this.horasMaximasSemanales = horasMaximasSemanales;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + Objects.hash(disponibilidadHoraria, escalafon, especialidades, horasMaximasSemanales,
				tipoVinculacion);
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
		ProfesorDTO other = (ProfesorDTO) obj;
		return Objects.equals(disponibilidadHoraria, other.disponibilidadHoraria) && escalafon == other.escalafon
				&& Objects.equals(especialidades, other.especialidades)
				&& horasMaximasSemanales == other.horasMaximasSemanales && tipoVinculacion == other.tipoVinculacion;
	}

	@Override
	public String toString() {
		return "ProfesorDTO [tipoVinculacion=" + tipoVinculacion + ", escalafon=" + escalafon + ", especialidades="
				+ especialidades + ", disponibilidadHoraria=" + disponibilidadHoraria + ", horasMaximasSemanales="
				+ horasMaximasSemanales + "]";
	}

}
