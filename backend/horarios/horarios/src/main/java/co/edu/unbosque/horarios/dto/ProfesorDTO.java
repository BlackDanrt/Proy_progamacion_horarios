package co.edu.unbosque.horarios.dto;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import co.edu.unbosque.horarios.entity.Persona;
import co.edu.unbosque.horarios.util.enums.Escalafon;
import co.edu.unbosque.horarios.util.enums.Especialidad;
import co.edu.unbosque.horarios.util.enums.TipoVinculacion;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;

/**
 * Representa los datos de un profesor, incluyendo su información personal,
 * vinculación, escalafón, especialidades, disponibilidad y horas semanales.
 *
 * @author Prisma Web
 */
public class ProfesorDTO extends Persona {

	/**
	 * Tipo de vinculación laboral del profesor.
	 */
	private TipoVinculacion tipoVinculacion;

	/**
	 * Escalafón académico del profesor.
	 */
	private Escalafon escalafon;

	/**
	 * Lista de especialidades del profesor.
	 */
	private List<Especialidad> especialidades = new ArrayList<>();

	/**
	 * Lista de franjas horarias en las que el profesor está disponible.
	 */
	private List<FranjaHorariaDTO> disponibilidadHoraria = new ArrayList<>();

	/**
	 * Número máximo de horas que el profesor puede trabajar por semana.
	 */
	@Min(value = 1, message = "Debe tener al menos 1 hora semanal")
	@Max(value = 40, message = "No puede superar las 40 horas semanales")
	private int horasMaximasSemanales;

	/**
	 * Constructor vacío de la clase.
	 */
	public ProfesorDTO() {
	}

	/**
	 * Construye un profesor con sus datos personales.
	 *
	 * @param nombre      nombre del profesor.
	 * @param apellido    apellido del profesor.
	 * @param documento   número de documento del profesor.
	 * @param correo      correo electrónico del profesor.
	 * @param contrasenia contraseña del profesor.
	 */
	public ProfesorDTO(@NotBlank(message = "El nombre no puede estar vacío") String nombre,
			@NotBlank(message = "El apellido no puede estar vacío") String apellido, long documento,
			@NotBlank(message = "El correo no puede estar vacío") @Email(message = "El correo no es válido") String correo,
			@NotBlank(message = "La contraseña no puede estar vacía") String contrasenia) {
		super(nombre, apellido, documento, correo, contrasenia);
	}

	/**
	 * Construye un profesor con sus datos laborales y disponibilidad.
	 *
	 * @param tipoVinculacion       tipo de vinculación laboral.
	 * @param escalafon             escalafón académico.
	 * @param especialidades        lista de especialidades del profesor.
	 * @param disponibilidadHoraria lista de franjas horarias disponibles.
	 * @param horasMaximasSemanales máximo de horas de trabajo por semana.
	 */
	public ProfesorDTO(TipoVinculacion tipoVinculacion, Escalafon escalafon, List<Especialidad> especialidades,
			List<FranjaHorariaDTO> disponibilidadHoraria,
			@Min(value = 1, message = "Debe tener al menos 1 hora semanal") @Max(value = 40, message = "No puede superar las 40 horas semanales") int horasMaximasSemanales) {
		super();
		this.tipoVinculacion = tipoVinculacion;
		this.escalafon = escalafon;
		this.especialidades = especialidades;
		this.disponibilidadHoraria = disponibilidadHoraria;
		this.horasMaximasSemanales = horasMaximasSemanales;
	}

	/**
	 * Construye un profesor con sus datos personales, laborales y disponibilidad.
	 *
	 * @param nombre                nombre del profesor.
	 * @param apellido              apellido del profesor.
	 * @param documento             número de documento del profesor.
	 * @param correo                correo electrónico del profesor.
	 * @param contrasenia           contraseña del profesor.
	 * @param tipoVinculacion       tipo de vinculación laboral.
	 * @param escalafon             escalafón académico.
	 * @param especialidades        lista de especialidades del profesor.
	 * @param disponibilidadHoraria lista de franjas horarias disponibles.
	 * @param horasMaximasSemanales máximo de horas de trabajo por semana.
	 */
	public ProfesorDTO(@NotBlank(message = "El nombre no puede estar vacío") String nombre,
			@NotBlank(message = "El apellido no puede estar vacío") String apellido, long documento,
			@NotBlank(message = "El correo no puede estar vacío") @Email(message = "El correo no es válido") String correo,
			@NotBlank(message = "La contraseña no puede estar vacía") String contrasenia,
			TipoVinculacion tipoVinculacion, Escalafon escalafon, List<Especialidad> especialidades,
			List<FranjaHorariaDTO> disponibilidadHoraria,
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
	 * @return tipo de vinculación.
	 */
	public TipoVinculacion getTipoVinculacion() {
		return tipoVinculacion;
	}

	/**
	 * Establece el tipo de vinculación laboral del profesor.
	 *
	 * @param tipoVinculacion tipo de vinculación.
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
	 * Establece el escalafón académico del profesor.
	 *
	 * @param escalafon escalafón académico.
	 */
	public void setEscalafon(Escalafon escalafon) {
		this.escalafon = escalafon;
	}

	/**
	 * Obtiene las especialidades del profesor.
	 *
	 * @return lista de especialidades.
	 */
	public List<Especialidad> getEspecialidades() {
		return especialidades;
	}

	/**
	 * Establece las especialidades del profesor.
	 *
	 * @param especialidades lista de especialidades.
	 */
	public void setEspecialidades(List<Especialidad> especialidades) {
		this.especialidades = especialidades;
	}

	/**
	 * Obtiene la disponibilidad horaria del profesor.
	 *
	 * @return lista de franjas horarias disponibles.
	 */
	public List<FranjaHorariaDTO> getDisponibilidadHoraria() {
		return disponibilidadHoraria;
	}

	/**
	 * Establece la disponibilidad horaria del profesor.
	 *
	 * @param disponibilidadHoraria lista de franjas horarias disponibles.
	 */
	public void setDisponibilidadHoraria(List<FranjaHorariaDTO> disponibilidadHoraria) {
		this.disponibilidadHoraria = disponibilidadHoraria;
	}

	/**
	 * Obtiene el máximo de horas semanales del profesor.
	 *
	 * @return máximo de horas de trabajo por semana.
	 */
	public int getHorasMaximasSemanales() {
		return horasMaximasSemanales;
	}

	/**
	 * Establece el máximo de horas semanales del profesor.
	 *
	 * @param horasMaximasSemanales máximo de horas de trabajo por semana.
	 */
	public void setHorasMaximasSemanales(int horasMaximasSemanales) {
		this.horasMaximasSemanales = horasMaximasSemanales;
	}

	/**
	 * Calcula el código hash del profesor.
	 *
	 * @return código hash basado en los atributos heredados y propios.
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
	 * @return true si ambos objetos tienen los mismos atributos; false en caso
	 *         contrario.
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
		ProfesorDTO other = (ProfesorDTO) obj;
		return Objects.equals(disponibilidadHoraria, other.disponibilidadHoraria) && escalafon == other.escalafon
				&& Objects.equals(especialidades, other.especialidades)
				&& horasMaximasSemanales == other.horasMaximasSemanales && tipoVinculacion == other.tipoVinculacion;
	}

	/**
	 * Devuelve una representación en texto del profesor.
	 *
	 * @return cadena con los datos laborales y la disponibilidad del profesor.
	 */
	@Override
	public String toString() {
		return super.toString() + "ProfesorDTO [tipoVinculacion=" + tipoVinculacion + ", escalafon=" + escalafon + ", especialidades="
				+ especialidades + ", disponibilidadHoraria=" + disponibilidadHoraria + ", horasMaximasSemanales="
				+ horasMaximasSemanales + "]";
	}

}