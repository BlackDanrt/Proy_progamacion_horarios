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

@Entity
@Table(name = "Franja_horaria")
public class FranjaHoraria {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private long id;
	
	@Enumerated(EnumType.STRING)
	@Column(nullable = false)
	private Dia dia;
	
	@Column(nullable = false)
	@NotNull(message = "La hora de inicio es obligatoria")
	private LocalTime horaInicio;
	
	@Column(nullable = false)
	@NotNull(message = "La hora de fin es obligatoria")
	private LocalTime horaFin;
	
	public FranjaHoraria() {
		// TODO Auto-generated constructor stub
	}

	public FranjaHoraria(Dia dia, LocalTime horaInicio, LocalTime horaFin) {
		super();
		this.dia = dia;
		this.horaInicio = horaInicio;
		this.horaFin = horaFin;
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public Dia getDia() {
		return dia;
	}

	public void setDia(Dia dia) {
		this.dia = dia;
	}

	public LocalTime getHoraInicio() {
		return horaInicio;
	}

	public void setHoraInicio(LocalTime horaInicio) {
		this.horaInicio = horaInicio;
	}

	public LocalTime getHoraFin() {
		return horaFin;
	}

	public void setHoraFin(LocalTime horaFin) {
		this.horaFin = horaFin;
	}

	@Override
	public int hashCode() {
		return Objects.hash(dia, horaFin, horaInicio, Long.valueOf(id));
	}

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

	@Override
	public String toString() {
		return "FranjaHoraria [id=" + id + ", dia=" + dia + ", horaInicio=" + horaInicio + ", horaFin=" + horaFin + "]";
	}
	
	
}
