package co.edu.unbosque.horarios.dto;

import java.time.LocalTime;
import java.util.Objects;

import co.edu.unbosque.horarios.util.enums.Dia;

public class FranjaHorariaDTO {

	private long id;
	private Dia dia;
	private LocalTime horaInicio;
	private LocalTime horaFin;

	public FranjaHorariaDTO() {
		// TODO Auto-generated constructor stub
	}

	public FranjaHorariaDTO(Dia dia, LocalTime horaInicio, LocalTime horaFin) {
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
		return Objects.hash(dia, horaFin, horaInicio, id);
	}

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

	@Override
	public String toString() {
		return "FranjaHorariaDTO [id=" + id + ", dia=" + dia + ", horaInicio=" + horaInicio + ", horaFin=" + horaFin
				+ "]";
	}

}
