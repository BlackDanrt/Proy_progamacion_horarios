package co.edu.unbosque.horarios.entity;

import java.util.Objects;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * Representa una asignatura dentro del sistema de gestión de horarios.
 * Contiene información sobre su nombre, número de créditos, frecuencia
 * semanal y requerimientos de infraestructura.
 *
 * @author Prisma Web
 */
@Entity
@Table(name = "Asignatura")
public class Asignatura {

	/**
	 * Identificador único de la asignatura.
	 */
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private long id;

	/**
	 * Nombre único de la asignatura.
	 */
	@Column(unique = true, nullable = false)
	@NotBlank(message = "El nombre de la asignatura no puede estar vacío")
	@Size(max = 100, message = "El nombre de la asignatura no puede superar los 100 caracteres")
	private String nombre;

	/**
	 * Número de créditos académicos de la asignatura.
	 */
	@Min(value = 1, message = "Debe tener al menos 1 crédito")
	@Max(value = 16, message = "No puede superar los 16 créditos")
	private int numCreditos;

	/**
	 * Número de días por semana en los que se programa la asignatura.
	 */
	@Min(value = 1, message = "La frecuencia semanal debe ser al menos 1 día")
	@Max(value = 7, message = "No puede exceder los 7 días de la semana")
	private int frecuenciaSemanal;

	/**
	 * Indica si la asignatura requiere computadores.
	 */
	@Column(name = "requiere_computadores")
	private boolean requiereComputadores;

	/**
	 * Indica si la asignatura requiere sillas móviles.
	 */
	@Column(name = "requiere_sillas_moviles")
	private boolean requiereSillasMoviles;

	/**
	 * Constructor vacío de la clase Asignatura.
	 */
	public Asignatura() {
		// TODO Auto-generated constructor stub
	}

	/**
	 * Construye una asignatura con sus datos y requerimientos de infraestructura.
	 *
	 * @param nombre nombre de la asignatura.
	 * @param numCreditos número de créditos académicos.
	 * @param frecuenciaSemanal frecuencia semanal de la asignatura.
	 * @param requiereComputadores indica si requiere computadores.
	 * @param requiereSillasMoviles indica si requiere sillas móviles.
	 */
	public Asignatura(String nombre, int numCreditos, int frecuenciaSemanal, boolean requiereComputadores,
			boolean requiereSillasMoviles) {
		super();
		this.nombre = nombre;
		this.numCreditos = numCreditos;
		this.frecuenciaSemanal = frecuenciaSemanal;
		this.requiereComputadores = requiereComputadores;
		this.requiereSillasMoviles = requiereSillasMoviles;
	}

	/**
	 * Obtiene el identificador de la asignatura.
	 *
	 * @return identificador de la asignatura.
	 */
	public long getId() {
		return id;
	}

	/**
	 * Modifica el identificador de la asignatura.
	 *
	 * @param id nuevo identificador de la asignatura.
	 */
	public void setId(long id) {
		this.id = id;
	}

	/**
	 * Obtiene el nombre de la asignatura.
	 *
	 * @return nombre de la asignatura.
	 */
	public String getNombre() {
		return nombre;
	}

	/**
	 * Modifica el nombre de la asignatura.
	 *
	 * @param nombre nuevo nombre de la asignatura.
	 */
	public void setNombre(String nombre) {
		this.nombre = nombre;
	}

	/**
	 * Obtiene el número de créditos de la asignatura.
	 *
	 * @return número de créditos académicos.
	 */
	public int getNumCreditos() {
		return numCreditos;
	}

	/**
	 * Modifica el número de créditos de la asignatura.
	 *
	 * @param numCreditos nuevo número de créditos.
	 */
	public void setNumCreditos(int numCreditos) {
		this.numCreditos = numCreditos;
	}

	/**
	 * Obtiene la frecuencia semanal de la asignatura.
	 *
	 * @return número de días por semana.
	 */
	public int getFrecuenciaSemanal() {
		return frecuenciaSemanal;
	}

	/**
	 * Modifica la frecuencia semanal de la asignatura.
	 *
	 * @param frecuenciaSemanal nueva frecuencia semanal.
	 */
	public void setFrecuenciaSemanal(int frecuenciaSemanal) {
		this.frecuenciaSemanal = frecuenciaSemanal;
	}

	/**
	 * Indica si la asignatura requiere computadores.
	 *
	 * @return true si requiere computadores; false en caso contrario.
	 */
	public boolean isRequiereComputadores() {
		return requiereComputadores;
	}

	/**
	 * Modifica el requerimiento de computadores de la asignatura.
	 *
	 * @param requiereComputadores indica si requiere computadores.
	 */
	public void setRequiereComputadores(boolean requiereComputadores) {
		this.requiereComputadores = requiereComputadores;
	}

	/**
	 * Indica si la asignatura requiere sillas móviles.
	 *
	 * @return true si requiere sillas móviles; false en caso contrario.
	 */
	public boolean isRequiereSillasMoviles() {
		return requiereSillasMoviles;
	}

	/**
	 * Modifica el requerimiento de sillas móviles de la asignatura.
	 *
	 * @param requiereSillasMoviles indica si requiere sillas móviles.
	 */
	public void setRequiereSillasMoviles(boolean requiereSillasMoviles) {
		this.requiereSillasMoviles = requiereSillasMoviles;
	}

	/**
	 * Calcula el código hash de la asignatura a partir de sus atributos.
	 *
	 * @return código hash de la asignatura.
	 */
	@Override
	public int hashCode() {
		return Objects.hash(Integer.valueOf(frecuenciaSemanal), Long.valueOf(id), nombre, Integer.valueOf(numCreditos),
				Boolean.valueOf(requiereComputadores), Boolean.valueOf(requiereSillasMoviles));
	}

	/**
	 * Compara esta asignatura con otro objeto para determinar si son iguales.
	 *
	 * @param obj objeto con el que se compara la asignatura.
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
		Asignatura other = (Asignatura) obj;
		return frecuenciaSemanal == other.frecuenciaSemanal && id == other.id && Objects.equals(nombre, other.nombre)
				&& numCreditos == other.numCreditos && requiereComputadores == other.requiereComputadores
				&& requiereSillasMoviles == other.requiereSillasMoviles;
	}

	/**
	 * Devuelve una representación en texto de la asignatura.
	 *
	 * @return cadena con los atributos de la asignatura.
	 */
	@Override
	public String toString() {
		return "Asignatura [id=" + id + ", nombre=" + nombre + ", numCreditos=" + numCreditos + ", frecuenciaSemanal="
				+ frecuenciaSemanal + ", requiereComputadores=" + requiereComputadores + ", requiereSillasMoviles="
				+ requiereSillasMoviles + "]";
	}

}