package co.edu.unbosque.horarios.dto;

import java.time.LocalTime;
import java.util.Objects;

import co.edu.unbosque.horarios.util.enums.Dia;

/**
 * Representa los datos de una franja horaria para su transferencia entre
 * las diferentes capas de la aplicación.
 *
 * @author Prisma Web
 */
public class FranjaHorariaDTO {

	/**
	 * Identificador único de la franja horaria.
	 */
	private long id;

	/**
	 * Día de la semana en el que se realiza la franja horaria.
	 */
	private Dia dia;

	/**
	 * Hora de inicio de la franja horaria.
	 */
	private LocalTime horaInicio;

	/**
	 * Hora de finalización de la franja horaria.
	 */
	private LocalTime horaFin;

	/**
	 * Constructor vacío de la clase.
	 */
	public FranjaHorariaDTO() {
		// TODO Auto-generated constructor stub
	}

	/**
	 * Construye una franja horaria con el día y las horas correspondientes.
	 *
	 * @param dia día de la semana de la franja horaria.
	 * @param horaInicio hora de inicio de la franja horaria.
	 * @param horaFin hora de finalización de la franja horaria.
	 */
	public FranjaHorariaDTO(Dia dia, LocalTime horaInicio, LocalTime horaFin) {
		super();
		this.dia = dia;
		this.horaInicio = horaInicio;
		this.horaFin = horaFin;
	}

	/**
	 * Obtiene el identificador de la franja horaria.
	 *
	 * @return identificador de la franja horaria.
	 */
	public long getId() {
		return id;
	}

	/**
	 * Establece el identificador de la franja horaria.
	 *
	 * @param id identificador de la franja horaria.
	 */
	public void setId(long id) {
		this.id = id;
	}

	/**
	 * Obtiene el día de la semana de la franja horaria.
	 *
	 * @return día de la semana.
	 */
	public Dia getDia() {
		return dia;
	}

	/**
	 * Establece el día de la semana de la franja horaria.
	 *
	 * @param dia día de la semana.
	 */
	public void setDia(Dia dia) {
		this.dia = dia;
	}

	/**
	 * Obtiene la hora de inicio de la franja horaria.
	 *
	 * @return hora de inicio.
	 */
	public LocalTime getHoraInicio() {
		return horaInicio;
	}

	/**
	 * Establece la hora de inicio de la franja horaria.
	 *
	 * @param horaInicio hora de inicio.
	 */
	public void setHoraInicio(LocalTime horaInicio) {
		this.horaInicio = horaInicio;
	}

	/**
	 * Obtiene la hora de finalización de la franja horaria.
	 *
	 * @return hora de finalización.
	 */
	public LocalTime getHoraFin() {
		return horaFin;
	}

	/**
	 * Establece la hora de finalización de la franja horaria.
	 *
	 * @param horaFin hora de finalización.
	 */
	public void setHoraFin(LocalTime horaFin) {
		this.horaFin = horaFin;
	}

	/**
	 * Calcula el código hash de la franja horaria.
	 *
	 * @return código hash basado en el día, las horas y el identificador.
	 */
	@Override
	public int hashCode() {
		return Objects.hash(dia, horaFin, horaInicio, id);
	}

	/**
	 * Compara esta franja horaria con otro objeto para determinar si son iguales.
	 *
	 * @param obj objeto con el que se compara la franja horaria.
	 * @return true si ambos objetos tienen los mismos atributos; false en caso contrario.
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		FranjaHorariaDTO other = (FranjaHorariaDTO) obj;
		return dia == other.dia && Objects.equals(horaFin, other.horaFin)
				&& Objects.equals(horaInicio, other.horaInicio) && id == other.id;
	}

	/**
	 * Devuelve una representación en texto de la franja horaria.
	 *
	 * @return cadena de texto con el identificador, el día y las horas.
	 */
	@Override
	public String toString() {
		return "FranjaHorariaDTO [id=" + id + ", dia=" + dia + ", horaInicio=" + horaInicio + ", horaFin=" + horaFin
				+ "]";
	}

}