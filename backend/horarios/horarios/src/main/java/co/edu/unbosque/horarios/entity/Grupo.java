package co.edu.unbosque.horarios.entity;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import co.edu.unbosque.horarios.util.enums.EstadoGrupo;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Min;

@Entity
@Table(name = "Grupo")
public class Grupo {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private long id;

	@ManyToOne
	@JoinColumn(name = "asignatura_id", nullable = false)
	private Asignatura asignatura;

	@Column(nullable = false)
	private int numeroGrupo;

	@Min(value = 1, message = "La capacidad mínima debe ser al menos 1")
	private int capacidadMinima;

	@Min(value = 1, message = "La capacidad máxima debe ser al menos 1")
	private int capacidadMaxima;

	@ManyToOne
	@JoinColumn(name = "profesor_id", nullable = false)
	private Profesor profesor;

	@ManyToMany
	@JoinTable(name = "grupo_franja", joinColumns = @JoinColumn(name = "grupo_id"), inverseJoinColumns = @JoinColumn(name = "franja_id"))
	private List<FranjaHoraria> sesiones = new ArrayList<>();

	@Enumerated(EnumType.STRING)
	@Column(nullable = false)
	private EstadoGrupo estado;

	public Grupo() {
		// TODO Auto-generated constructor stub
	}

	public Grupo(Asignatura asignatura, int numeroGrupo,
			@Min(value = 1, message = "La capacidad mínima debe ser al menos 1") int capacidadMinima,
			@Min(value = 1, message = "La capacidad máxima debe ser al menos 1") int capacidadMaxima, Profesor profesor,
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
		return Objects.hash(asignatura, Integer.valueOf(capacidadMaxima), Integer.valueOf(capacidadMinima), estado,
				Long.valueOf(id), Integer.valueOf(numeroGrupo), profesor, sesiones);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Grupo other = (Grupo) obj;
		return Objects.equals(asignatura, other.asignatura) && capacidadMaxima == other.capacidadMaxima
				&& capacidadMinima == other.capacidadMinima && estado == other.estado && id == other.id
				&& numeroGrupo == other.numeroGrupo && Objects.equals(profesor, other.profesor)
				&& Objects.equals(sesiones, other.sesiones);
	}

	@Override
	public String toString() {
		return "Grupo [id=" + id + ", asignatura=" + asignatura + ", numeroGrupo=" + numeroGrupo + ", capacidadMinima="
				+ capacidadMinima + ", capacidadMaxima=" + capacidadMaxima + ", profesor=" + profesor + ", sesiones="
				+ sesiones + ", estado=" + estado + "]";
	}

}
