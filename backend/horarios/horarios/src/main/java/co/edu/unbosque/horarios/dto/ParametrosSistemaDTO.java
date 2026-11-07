package co.edu.unbosque.horarios.dto;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.LocalTime;
import java.util.Objects;

public class ParametrosSistemaDTO {

	private Long id;

	@Column(nullable = false)
	private LocalTime horaInicioEntreSemana;

	@Column(nullable = false)
	private LocalTime horaFinEntreSemana;

	@Column(nullable = false)
	private LocalTime horaInicioSabado;

	@Column(nullable = false)
	private LocalTime horaFinSabado;

	@Column(nullable = false)
	private LocalTime horaInicioAlmuerzo;

	@Column(nullable = false)
	private LocalTime horaFinAlmuerzo;

	@Column(nullable = false)
	private int maxSesionesPorSemana;

	@Column(nullable = false)
	private int duracionSesionMinutos;

	@Column(nullable = false)
	private int capacidadMaximaGrupo;

	@Column(nullable = false)
	private int toleranciaCapacidad;

	@Column(nullable = false)
	private int minEstudiantesParaAbrir;

	@Column(nullable = false)
	private boolean exigirDiasNoConsecutivos = true;

	public ParametrosSistemaDTO() {
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public boolean isExigirDiasNoConsecutivos() {
		return exigirDiasNoConsecutivos;
	}

	public void setExigirDiasNoConsecutivos(boolean exigirDiasNoConsecutivos) {
		this.exigirDiasNoConsecutivos = exigirDiasNoConsecutivos;
	}

	public LocalTime getHoraInicioEntreSemana() {
		return horaInicioEntreSemana;
	}

	public void setHoraInicioEntreSemana(LocalTime horaInicioEntreSemana) {
		this.horaInicioEntreSemana = horaInicioEntreSemana;
	}

	public LocalTime getHoraFinEntreSemana() {
		return horaFinEntreSemana;
	}

	public void setHoraFinEntreSemana(LocalTime horaFinEntreSemana) {
		this.horaFinEntreSemana = horaFinEntreSemana;
	}

	public LocalTime getHoraInicioSabado() {
		return horaInicioSabado;
	}

	public void setHoraInicioSabado(LocalTime horaInicioSabado) {
		this.horaInicioSabado = horaInicioSabado;
	}

	public LocalTime getHoraFinSabado() {
		return horaFinSabado;
	}

	public void setHoraFinSabado(LocalTime horaFinSabado) {
		this.horaFinSabado = horaFinSabado;
	}

	public LocalTime getHoraInicioAlmuerzo() {
		return horaInicioAlmuerzo;
	}

	public void setHoraInicioAlmuerzo(LocalTime horaInicioAlmuerzo) {
		this.horaInicioAlmuerzo = horaInicioAlmuerzo;
	}

	public LocalTime getHoraFinAlmuerzo() {
		return horaFinAlmuerzo;
	}

	public void setHoraFinAlmuerzo(LocalTime horaFinAlmuerzo) {
		this.horaFinAlmuerzo = horaFinAlmuerzo;
	}

	public int getMaxSesionesPorSemana() {
		return maxSesionesPorSemana;
	}

	public void setMaxSesionesPorSemana(int maxSesionesPorSemana) {
		this.maxSesionesPorSemana = maxSesionesPorSemana;
	}

	public int getDuracionSesionMinutos() {
		return duracionSesionMinutos;
	}

	public void setDuracionSesionMinutos(int duracionSesionMinutos) {
		this.duracionSesionMinutos = duracionSesionMinutos;
	}

	public int getCapacidadMaximaGrupo() {
		return capacidadMaximaGrupo;
	}

	public void setCapacidadMaximaGrupo(int capacidadMaximaGrupo) {
		this.capacidadMaximaGrupo = capacidadMaximaGrupo;
	}

	public int getToleranciaCapacidad() {
		return toleranciaCapacidad;
	}

	public void setToleranciaCapacidad(int toleranciaCapacidad) {
		this.toleranciaCapacidad = toleranciaCapacidad;
	}

	public int getMinEstudiantesParaAbrir() {
		return minEstudiantesParaAbrir;
	}

	public void setMinEstudiantesParaAbrir(int minEstudiantesParaAbrir) {
		this.minEstudiantesParaAbrir = minEstudiantesParaAbrir;
	}

	@Override
	public String toString() {
		return "ParametrosSistemaDTO [id=" + id + ", horaInicioEntreSemana=" + horaInicioEntreSemana
				+ ", horaFinEntreSemana=" + horaFinEntreSemana + ", horaInicioSabado=" + horaInicioSabado
				+ ", horaFinSabado=" + horaFinSabado + ", horaInicioAlmuerzo=" + horaInicioAlmuerzo
				+ ", horaFinAlmuerzo=" + horaFinAlmuerzo + ", maxSesionesPorSemana=" + maxSesionesPorSemana
				+ ", duracionSesionMinutos=" + duracionSesionMinutos + ", capacidadMaximaGrupo=" + capacidadMaximaGrupo
				+ ", toleranciaCapacidad=" + toleranciaCapacidad + ", minEstudiantesParaAbrir="
				+ minEstudiantesParaAbrir + ", exigirDiasNoConsecutivos=" + exigirDiasNoConsecutivos + "]";
	}

	@Override
	public int hashCode() {
		return Objects.hash(Integer.valueOf(capacidadMaximaGrupo), Integer.valueOf(duracionSesionMinutos),
				Boolean.valueOf(exigirDiasNoConsecutivos), horaFinAlmuerzo, horaFinEntreSemana, horaFinSabado,
				horaInicioAlmuerzo, horaInicioEntreSemana, horaInicioSabado, id, Integer.valueOf(maxSesionesPorSemana),
				Integer.valueOf(minEstudiantesParaAbrir), Integer.valueOf(toleranciaCapacidad));
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		ParametrosSistemaDTO other = (ParametrosSistemaDTO) obj;
		return capacidadMaximaGrupo == other.capacidadMaximaGrupo
				&& duracionSesionMinutos == other.duracionSesionMinutos
				&& exigirDiasNoConsecutivos == other.exigirDiasNoConsecutivos
				&& Objects.equals(horaFinAlmuerzo, other.horaFinAlmuerzo)
				&& Objects.equals(horaFinEntreSemana, other.horaFinEntreSemana)
				&& Objects.equals(horaFinSabado, other.horaFinSabado)
				&& Objects.equals(horaInicioAlmuerzo, other.horaInicioAlmuerzo)
				&& Objects.equals(horaInicioEntreSemana, other.horaInicioEntreSemana)
				&& Objects.equals(horaInicioSabado, other.horaInicioSabado) && Objects.equals(id, other.id)
				&& maxSesionesPorSemana == other.maxSesionesPorSemana
				&& minEstudiantesParaAbrir == other.minEstudiantesParaAbrir
				&& toleranciaCapacidad == other.toleranciaCapacidad;
	}

}