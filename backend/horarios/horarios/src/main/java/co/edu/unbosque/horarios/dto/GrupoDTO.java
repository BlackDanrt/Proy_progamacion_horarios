package co.edu.unbosque.horarios.dto;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import co.edu.unbosque.horarios.entity.Asignatura;
import co.edu.unbosque.horarios.entity.FranjaHoraria;
import co.edu.unbosque.horarios.entity.Profesor;
import co.edu.unbosque.horarios.util.enums.EstadoGrupo;

public class GrupoDTO {

	private long id;
	private Asignatura asignatura;
	private int numeroGrupo;
	private int capacidadMinima;
	private int capacidadMaxima;
	private Profesor profesor;
	private List<FranjaHoraria> sesiones = new ArrayList<>();
	private EstadoGrupo estado;

	public GrupoDTO() {
		// TODO Auto-generated constructor stub
	}

	public GrupoDTO(Asignatura asignatura, int numeroGrupo, int capacidadMinima, int capacidadMaxima, Profesor profesor,
			EstadoGrupo estado) {
		super();
		this.asignatura = asignatura;
		this.numeroGrupo = numeroGrupo;
		this.capacidadMinima = capacidadMinima;
		this.capacidadMaxima = capacidadMaxima;
		this.profesor = profesor;
		this.estado = estado;
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public Asignatura getAsignatura() {
		return asignatura;
	}

	public void setAsignatura(Asignatura asignatura) {
		this.asignatura = asignatura;
	}

	public int getNumeroGrupo() {
		return numeroGrupo;
	}

	public void setNumeroGrupo(int numeroGrupo) {
		this.numeroGrupo = numeroGrupo;
	}

	public int getCapacidadMinima() {
		return capacidadMinima;
	}

	public void setCapacidadMinima(int capacidadMinima) {
		this.capacidadMinima = capacidadMinima;
	}

	public int getCapacidadMaxima() {
		return capacidadMaxima;
	}

	public void setCapacidadMaxima(int capacidadMaxima) {
		this.capacidadMaxima = capacidadMaxima;
	}

	public Profesor getProfesor() {
		return profesor;
	}

	public void setProfesor(Profesor profesor) {
		this.profesor = profesor;
	}

	public List<FranjaHoraria> getSesiones() {
		return sesiones;
	}

	public void setSesiones(List<FranjaHoraria> sesiones) {
		this.sesiones = sesiones;
	}

	public EstadoGrupo getEstado() {
		return estado;
	}

	public void setEstado(EstadoGrupo estado) {
		this.estado = estado;
	}

	@Override
	public int hashCode() {
		return Objects.hash(asignatura, capacidadMaxima, capacidadMinima, estado, id, numeroGrupo, profesor, sesiones);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		GrupoDTO other = (GrupoDTO) obj;
		return Objects.equals(asignatura, other.asignatura) && capacidadMaxima == other.capacidadMaxima
				&& capacidadMinima == other.capacidadMinima && estado == other.estado && id == other.id
				&& numeroGrupo == other.numeroGrupo && Objects.equals(profesor, other.profesor)
				&& Objects.equals(sesiones, other.sesiones);
	}

	@Override
	public String toString() {
		return "GrupoDTO [id=" + id + ", asignatura=" + asignatura + ", numeroGrupo=" + numeroGrupo
				+ ", capacidadMinima=" + capacidadMinima + ", capacidadMaxima=" + capacidadMaxima + ", profesor="
				+ profesor + ", sesiones=" + sesiones + ", estado=" + estado + "]";
	}

}
