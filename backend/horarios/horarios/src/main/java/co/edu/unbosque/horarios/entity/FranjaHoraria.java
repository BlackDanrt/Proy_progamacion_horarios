package co.edu.unbosque.horarios.entity;

import java.time.LocalTime;
import java.util.Objects;

import co.edu.unbosque.horarios.util.enums.Dia;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;

/**
 * Representa una franja horaria dentro del sistema de gestión de horarios.
 * Contiene el día de la semana y las horas de inicio y finalización.
 *
 * @author Prisma Web
 */
@Entity
@Table(name = "Franja_horaria")
public class FranjaHoraria {

	/**
	 * Identificador único de la franja horaria.
	 */
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private long id;

	/**
	 * Día de la semana en el que se encuentra la franja horaria.
	 */
	@Enumerated(EnumType.STRING)
	@Column(nullable = false)
	private Dia dia;

	/**
	 * Hora en la que comienza la franja horaria.
	 */
	@Column(nullable = false)
	@NotNull(message = "La hora de inicio es obligatoria")
	private LocalTime horaInicio;

	/**
	 * Hora en la que finaliza la franja horaria.
	 */
	@Column(nullable = false)
	@NotNull(message = "La hora de fin es obligatoria")
	private LocalTime horaFin;

	/**
	 * Constructor vacío de la clase FranjaHoraria.
	 */
	public FranjaHoraria() {
		// TODO Auto-generated constructor stub
	}

	/**
	 * Construye una franja horaria con el día y las horas correspondientes.
	 *
	 * @param dia día de la semana de la franja horaria.
	 * @param horaInicio hora de inicio de la franja horaria.
	 * @param horaFin hora de finalización de la franja horaria.
	 */
	public FranjaHoraria(Dia dia, LocalTime horaInicio, LocalTime horaFin) {
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
	 * Modifica el identificador de la franja horaria.
	 *
	 * @param id nuevo identificador de la franja horaria.
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
	 * Modifica el día de la semana de la franja horaria.
	 *
	 * @param dia nuevo día de la semana.
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
	 * Modifica la hora de inicio de la franja horaria.
	 *
	 * @param horaInicio nueva hora de inicio.
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
	 * Modifica la hora de finalización de la franja horaria.
	 *
	 * @param horaFin nueva hora de finalización.
	 */
	public void setHoraFin(LocalTime horaFin) {
		this.horaFin = horaFin;
	}

	/**
	 * Calcula el código hash de la franja horaria a partir de sus atributos.
	 *
	 * @return código hash de la franja horaria.
	 */
	@Override
	public int hashCode() {
		return Objects.hash(dia, horaFin, horaInicio, Long.valueOf(id));
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
		FranjaHoraria other = (FranjaHoraria) obj;
		return dia == other.dia && Objects.equals(horaFin, other.horaFin)
				&& Objects.equals(horaInicio, other.horaInicio) && id == other.id;
	}

	/**
	 * Devuelve una representación en texto de la franja horaria.
	 *
	 * @return cadena con el identificador, el día y las horas de la franja horaria.
	 */
	@Override
	public String toString() {
		return "FranjaHoraria [id=" + id + ", dia=" + dia + ", horaInicio=" + horaInicio + ", horaFin=" + horaFin + "]";
	}

}