package co.edu.unbosque.horarios.entity;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

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

/**
 * Representa un profesor dentro del sistema de gestión de horarios.
 * Contiene información sobre su tipo de vinculación, escalafón,
 * especialidades, disponibilidad horaria y horas máximas semanales.
 * Hereda los datos personales y las credenciales de la clase Persona.
 *
 * @author Prisma Web
 */
@Entity
@Table(name = "Profesor")
public class Profesor extends Persona {

	/**
	 * Tipo de vinculación laboral del profesor.
	 */
	@Enumerated(EnumType.STRING)
	@Column(nullable = false)
	private TipoVinculacion tipoVinculacion;

	/**
	 * Escalafón académico del profesor.
	 */
	@Enumerated(EnumType.STRING)
	@Column(nullable = false)
	private Escalafon escalafon;

	/**
	 * Lista de especialidades académicas del profesor.
	 */
	@ElementCollection(targetClass = Especialidad.class, fetch = FetchType.EAGER)
	@CollectionTable(name = "profesor_especialidad", joinColumns = @JoinColumn(name = "profesor_id"))
	@Enumerated(EnumType.STRING)
	@Column(name = "especialidad", nullable = false)
	private List<Especialidad> especialidades = new ArrayList<>();

	/**
	 * Lista de franjas horarias en las que el profesor tiene disponibilidad.
	 */
	@ManyToMany
	@JoinTable(name = "profesor_franja", joinColumns = @JoinColumn(name = "profesor_id"), inverseJoinColumns = @JoinColumn(name = "franja_id"))
	private List<FranjaHoraria> disponibilidadHoraria = new ArrayList<>();

	/**
	 * Número máximo de horas semanales que puede trabajar el profesor.
	 */
	@Min(value = 1, message = "Debe tener al menos 1 hora semanal")
	@Max(value = 40, message = "No puede superar las 40 horas semanales")
	private int horasMaximasSemanales;

	/**
	 * Constructor vacío de la clase Profesor.
	 */
	public Profesor() {}

	/**
	 * Construye un profesor con sus datos personales y credenciales de acceso.
	 *
	 * @param nombre nombre del profesor.
	 * @param apellido apellido del profesor.
	 * @param documento número de documento del profesor.
	 * @param correo correo electrónico del profesor.
	 * @param contrasenia contraseña de acceso al sistema.
	 */
	public Profesor(@NotBlank(message = "El nombre no puede estar vacío") String nombre,
			@NotBlank(message = "El apellido no puede estar vacío") String apellido, long documento,
			@NotBlank(message = "El correo no puede estar vacío") @Email(message = "El correo no es válido") String correo,
			@NotBlank(message = "La contraseña no puede estar vacía") String contrasenia) {
		super(nombre, apellido, documento, correo, contrasenia);
	}

	/**
	 * Construye un profesor con su información laboral, especialidades,
	 * disponibilidad horaria y límite de horas semanales.
	 *
	 * @param tipoVinculacion tipo de vinculación laboral del profesor.
	 * @param escalafon escalafón académico del profesor.
	 * @param especialidades lista de especialidades del profesor.
	 * @param disponibilidadHoraria lista de franjas horarias disponibles.
	 * @param horasMaximasSemanales número máximo de horas semanales.
	 */
	public Profesor(TipoVinculacion tipoVinculacion, Escalafon escalafon, List<Especialidad> especialidades,
			List<FranjaHoraria> disponibilidadHoraria,
			@Min(value = 1, message = "Debe tener al menos 1 hora semanal") @Max(value = 40, message = "No puede superar las 40 horas semanales") int horasMaximasSemanales) {
		super();
		this.tipoVinculacion = tipoVinculacion;
		this.escalafon = escalafon;
		this.especialidades = especialidades;
		this.disponibilidadHoraria = disponibilidadHoraria;
		this.horasMaximasSemanales = horasMaximasSemanales;
	}

	/**
	 * Construye un profesor con sus datos personales, credenciales,
	 * información laboral, especialidades y disponibilidad horaria.
	 *
	 * @param nombre nombre del profesor.
	 * @param apellido apellido del profesor.
	 * @param documento número de documento del profesor.
	 * @param correo correo electrónico del profesor.
	 * @param contrasenia contraseña de acceso al sistema.
	 * @param tipoVinculacion tipo de vinculación laboral del profesor.
	 * @param escalafon escalafón académico del profesor.
	 * @param especialidades lista de especialidades del profesor.
	 * @param disponibilidadHoraria lista de franjas horarias disponibles.
	 * @param horasMaximasSemanales número máximo de horas semanales.
	 */
	public Profesor(@NotBlank(message = "El nombre no puede estar vacío") String nombre,
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

	/**
	 * Obtiene el tipo de vinculación laboral del profesor.
	 *
	 * @return tipo de vinculación del profesor.
	 */
	public TipoVinculacion getTipoVinculacion() {
		return tipoVinculacion;
	}

	/**
	 * Modifica el tipo de vinculación laboral del profesor.
	 *
	 * @param tipoVinculacion nuevo tipo de vinculación.
	 */
	public void setTipoVinculacion(TipoVinculacion tipoVinculacion) {
		this.tipoVinculacion = tipoVinculacion;
	}

	/**
	 * Obtiene el escalafón académico del profesor.
	 *
	 * @return escalafón académico.
	 */
	public Escalafon getEscalafon() {
		return escalafon;
	}

	/**
	 * Modifica el escalafón académico del profesor.
	 *
	 * @param escalafon nuevo escalafón académico.
	 */
	public void setEscalafon(Escalafon escalafon) {
		this.escalafon = escalafon;
	}

	/**
	 * Obtiene la lista de especialidades del profesor.
	 *
	 * @return lista de especialidades académicas.
	 */
	public List<Especialidad> getEspecialidades() {
		return especialidades;
	}

	/**
	 * Modifica la lista de especialidades del profesor.
	 *
	 * @param especialidades nueva lista de especialidades.
	 */
	public void setEspecialidades(List<Especialidad> especialidades) {
		this.especialidades = especialidades;
	}

	/**
	 * Obtiene la disponibilidad horaria del profesor.
	 *
	 * @return lista de franjas horarias disponibles.
	 */
	public List<FranjaHoraria> getDisponibilidadHoraria() {
		return disponibilidadHoraria;
	}

	/**
	 * Modifica la disponibilidad horaria del profesor.
	 *
	 * @param disponibilidadHoraria nueva lista de franjas horarias disponibles.
	 */
	public void setDisponibilidadHoraria(List<FranjaHoraria> disponibilidadHoraria) {
		this.disponibilidadHoraria = disponibilidadHoraria;
	}

	/**
	 * Obtiene el número máximo de horas semanales del profesor.
	 *
	 * @return número máximo de horas semanales.
	 */
	public int getHorasMaximasSemanales() {
		return horasMaximasSemanales;
	}

	/**
	 * Modifica el número máximo de horas semanales del profesor.
	 *
	 * @param horasMaximasSemanales nuevo límite de horas semanales.
	 */
	public void setHorasMaximasSemanales(int horasMaximasSemanales) {
		this.horasMaximasSemanales = horasMaximasSemanales;
	}

	/**
	 * Calcula el código hash del profesor a partir de sus atributos
	 * y los atributos heredados de Persona.
	 *
	 * @return código hash del profesor.
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + Objects.hash(disponibilidadHoraria, escalafon, especialidades, horasMaximasSemanales,
				tipoVinculacion);
		return result;
	}

	/**
	 * Compara este profesor con otro objeto para determinar si son iguales.
	 *
	 * @param obj objeto con el que se compara el profesor.
	 * @return true si ambos objetos cumplen las condiciones de igualdad;
	 *         false en caso contrario.
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
		Profesor other = (Profesor) obj;
		return Objects.equals(disponibilidadHoraria, other.disponibilidadHoraria) && escalafon == other.escalafon
				&& Objects.equals(especialidades, other.especialidades)
				&& horasMaximasSemanales == other.horasMaximasSemanales && tipoVinculacion == other.tipoVinculacion;
	}

	/**
	 * Devuelve una representación en texto del profesor con su información
	 * laboral, especialidades, disponibilidad y horas máximas semanales.
	 *
	 * @return cadena con los datos específicos del profesor.
	 */
	@Override
	public String toString() {
		return "Profesor [tipoVinculacion=" + tipoVinculacion + ", escalafon=" + escalafon + ", especialidades="
				+ especialidades + ", disponibilidadHoraria=" + disponibilidadHoraria + ", horasMaximasSemanales="
				+ horasMaximasSemanales + "]";
	}

}