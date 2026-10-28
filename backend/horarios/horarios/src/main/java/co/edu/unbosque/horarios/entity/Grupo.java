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
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;

/**
 * Representa un grupo de una asignatura dentro del sistema de gestión
 * de horarios. Contiene información sobre el profesor, el salón, las
 * capacidades, las sesiones programadas y el estado del grupo.
 *
 * @author Prisma Web
 */
@Entity
@Table(name = "Grupo")
public class Grupo {

	/**
	 * Identificador único del grupo.
	 */
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private long id;

	/**
	 * Asignatura a la que pertenece el grupo.
	 */
	@ManyToOne
	@JoinColumn(name = "asignatura_id", nullable = false)
	private Asignatura asignatura;

	/**
	 * Número que identifica al grupo dentro de la asignatura.
	 */
	@Column(nullable = false)
	private int numeroGrupo;

	/**
	 * Cantidad mínima de estudiantes permitida para el grupo.
	 */
	@Min(value = 1, message = "La capacidad mínima debe ser al menos 1")
	private int capacidadMinima;

	/**
	 * Cantidad máxima de estudiantes permitida para el grupo.
	 */
	@Max(value = 44, message = "La capacidad máxima es de 44")
	private int capacidadMaxima;

	/**
	 * Profesor encargado de impartir la asignatura al grupo.
	 */
	@ManyToOne
	@JoinColumn(name = "profesor_id", nullable = false)
	private Profesor profesor;

	/**
	 * Salón asignado para las sesiones del grupo.
	 */
	@ManyToOne
	@JoinColumn(name = "salon_id", nullable = false)
	private Salon salon;

	/**
	 * Lista de franjas horarias asignadas a las sesiones del grupo.
	 */
	@ManyToMany
	@JoinTable(name = "grupo_franja", joinColumns = @JoinColumn(name = "grupo_id"), inverseJoinColumns = @JoinColumn(name = "franja_id"))
	private List<FranjaHoraria> sesiones = new ArrayList<>();

	/**
	 * Estado actual del grupo.
	 */
	@Enumerated(EnumType.STRING)
	@Column(nullable = false)
	private EstadoGrupo estado;

	/**
	 * Constructor vacío de la clase Grupo.
	 */
	public Grupo() {
		// TODO Auto-generated constructor stub
	}

	/**
	 * Construye un grupo con su asignatura, número, capacidades,
	 * profesor, salón, sesiones y estado.
	 *
	 * @param asignatura asignatura a la que pertenece el grupo.
	 * @param numeroGrupo número identificador del grupo.
	 * @param capacidadMinima cantidad mínima de estudiantes.
	 * @param capacidadMaxima cantidad máxima de estudiantes.
	 * @param profesor profesor encargado del grupo.
	 * @param salon salón asignado al grupo.
	 * @param sesiones lista de franjas horarias del grupo.
	 * @param estado estado actual del grupo.
	 */
	public Grupo(Asignatura asignatura, int numeroGrupo,
			@Min(value = 1, message = "La capacidad mínima debe ser al menos 1") int capacidadMinima,
			@Min(value = 1, message = "La capacidad máxima debe ser al menos 1") int capacidadMaxima, Profesor profesor,
			Salon salon, List<FranjaHoraria> sesiones, EstadoGrupo estado) {
		super();
		this.asignatura = asignatura;
		this.numeroGrupo = numeroGrupo;
		this.capacidadMinima = capacidadMinima;
		this.capacidadMaxima = capacidadMaxima;
		this.profesor = profesor;
		this.salon = salon;
		this.sesiones = sesiones;
		this.estado = estado;
	}

	/**
	 * Obtiene el identificador del grupo.
	 *
	 * @return identificador único del grupo.
	 */
	public long getId() {
		return id;
	}

	/**
	 * Modifica el identificador del grupo.
	 *
	 * @param id nuevo identificador del grupo.
	 */
	public void setId(long id) {
		this.id = id;
	}

	/**
	 * Obtiene la asignatura del grupo.
	 *
	 * @return asignatura asociada al grupo.
	 */
	public Asignatura getAsignatura() {
		return asignatura;
	}

	/**
	 * Modifica la asignatura del grupo.
	 *
	 * @param asignatura nueva asignatura del grupo.
	 */
	public void setAsignatura(Asignatura asignatura) {
		this.asignatura = asignatura;
	}

	/**
	 * Obtiene el número del grupo.
	 *
	 * @return número identificador del grupo.
	 */
	public int getNumeroGrupo() {
		return numeroGrupo;
	}

	/**
	 * Modifica el número del grupo.
	 *
	 * @param numeroGrupo nuevo número del grupo.
	 */
	public void setNumeroGrupo(int numeroGrupo) {
		this.numeroGrupo = numeroGrupo;
	}

	/**
	 * Obtiene la capacidad mínima del grupo.
	 *
	 * @return cantidad mínima de estudiantes.
	 */
	public int getCapacidadMinima() {
		return capacidadMinima;
	}

	/**
	 * Modifica la capacidad mínima del grupo.
	 *
	 * @param capacidadMinima nueva capacidad mínima.
	 */
	public void setCapacidadMinima(int capacidadMinima) {
		this.capacidadMinima = capacidadMinima;
	}

	/**
	 * Obtiene la capacidad máxima del grupo.
	 *
	 * @return cantidad máxima de estudiantes.
	 */
	public int getCapacidadMaxima() {
		return capacidadMaxima;
	}

	/**
	 * Modifica la capacidad máxima del grupo.
	 *
	 * @param capacidadMaxima nueva capacidad máxima.
	 */
	public void setCapacidadMaxima(int capacidadMaxima) {
		this.capacidadMaxima = capacidadMaxima;
	}

	/**
	 * Obtiene el profesor encargado del grupo.
	 *
	 * @return profesor asignado al grupo.
	 */
	public Profesor getProfesor() {
		return profesor;
	}

	/**
	 * Modifica el profesor encargado del grupo.
	 *
	 * @param profesor nuevo profesor del grupo.
	 */
	public void setProfesor(Profesor profesor) {
		this.profesor = profesor;
	}

	/**
	 * Obtiene las sesiones programadas para el grupo.
	 *
	 * @return lista de franjas horarias asignadas.
	 */
	public List<FranjaHoraria> getSesiones() {
		return sesiones;
	}

	/**
	 * Modifica las sesiones programadas para el grupo.
	 *
	 * @param sesiones nueva lista de franjas horarias.
	 */
	public void setSesiones(List<FranjaHoraria> sesiones) {
		this.sesiones = sesiones;
	}

	/**
	 * Obtiene el estado actual del grupo.
	 *
	 * @return estado del grupo.
	 */
	public EstadoGrupo getEstado() {
		return estado;
	}

	/**
	 * Modifica el estado del grupo.
	 *
	 * @param estado nuevo estado del grupo.
	 */
	public void setEstado(EstadoGrupo estado) {
		this.estado = estado;
	}

	/**
	 * Obtiene el salón asignado al grupo.
	 *
	 * @return salón del grupo.
	 */
	public Salon getSalon() {
		return salon;
	}

	/**
	 * Modifica el salón asignado al grupo.
	 *
	 * @param salon nuevo salón del grupo.
	 */
	public void setSalon(Salon salon) {
		this.salon = salon;
	}

	/**
	 * Calcula el código hash del grupo a partir de sus atributos.
	 *
	 * @return código hash del grupo.
	 */
	@Override
	public int hashCode() {
		return Objects.hash(asignatura, Integer.valueOf(capacidadMaxima), Integer.valueOf(capacidadMinima), estado,
				Long.valueOf(id), Integer.valueOf(numeroGrupo), profesor, salon);
	}

	/**
	 * Compara este grupo con otro objeto para determinar si son iguales.
	 *
	 * @param obj objeto con el que se compara el grupo.
	 * @return true si ambos objetos tienen los mismos atributos comparados;
	 *         false en caso contrario.
	 */
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
				&& Objects.equals(salon, other.salon);
	}

	/**
	 * Devuelve una representación en texto del grupo, incluyendo los
	 * identificadores del profesor y del salón, y la cantidad de sesiones.
	 *
	 * @return cadena con la información del grupo.
	 */
	@Override
	public String toString() {
		return "Grupo [id=" + id + ", asignatura=" + asignatura + ", numeroGrupo=" + numeroGrupo + ", capacidadMinima="
				+ capacidadMinima + ", capacidadMaxima=" + capacidadMaxima + ", profesor=" + profesor.getId()
				+ ", salon=" + salon.getId() + ", sesiones=" + sesiones.size() + ", estado=" + estado + "]";
	}

}