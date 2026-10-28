package co.edu.unbosque.horarios.dto;

import java.util.Objects;

/**
 * Representa los datos de una asignatura para su transferencia entre las
 * diferentes capas de la aplicación.
 *
 * @author Prisma Web
 */
public class AsignaturaDTO {

	/**
	 * Identificador único de la asignatura.
	 */
	private long id;

	/**
	 * Nombre de la asignatura.
	 */
	private String nombre;

	/**
	 * Número de créditos de la asignatura.
	 */
	private int numCreditos;

	/**
	 * Número de sesiones semanales de la asignatura.
	 */
	private int frecuenciaSemanal;

	/**
	 * Indica si la asignatura requiere computadores.
	 */
	private boolean requiereComputadores;

	/**
	 * Indica si la asignatura requiere sillas móviles.
	 */
	private boolean requiereSillasMoviles;

	/**
	 * Constructor vacío de la clase.
	 */
	public AsignaturaDTO() {
		// TODO Auto-generated constructor stub
	}

	/**
	 * Construye una asignatura con sus características principales.
	 *
	 * @param nombre nombre de la asignatura.
	 * @param numCreditos número de créditos de la asignatura.
	 * @param frecuenciaSemanal número de sesiones semanales.
	 * @param requiereComputadores indica si requiere computadores.
	 * @param requiereSillasMoviles indica si requiere sillas móviles.
	 */
	public AsignaturaDTO(String nombre, int numCreditos, int frecuenciaSemanal, boolean requiereComputadores,
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
	 * Establece el identificador de la asignatura.
	 *
	 * @param id identificador de la asignatura.
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
	 * Establece el nombre de la asignatura.
	 *
	 * @param nombre nombre de la asignatura.
	 */
	public void setNombre(String nombre) {
		this.nombre = nombre;
	}

	/**
	 * Obtiene el número de créditos de la asignatura.
	 *
	 * @return número de créditos.
	 */
	public int getNumCreditos() {
		return numCreditos;
	}

	/**
	 * Establece el número de créditos de la asignatura.
	 *
	 * @param numCreditos número de créditos.
	 */
	public void setNumCreditos(int numCreditos) {
		this.numCreditos = numCreditos;
	}

	/**
	 * Obtiene la frecuencia semanal de la asignatura.
	 *
	 * @return número de sesiones semanales.
	 */
	public int getFrecuenciaSemanal() {
		return frecuenciaSemanal;
	}

	/**
	 * Establece la frecuencia semanal de la asignatura.
	 *
	 * @param frecuenciaSemanal número de sesiones semanales.
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
	 * Establece si la asignatura requiere computadores.
	 *
	 * @param requiereComputadores true si requiere computadores; false en caso contrario.
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
	 * Establece si la asignatura requiere sillas móviles.
	 *
	 * @param requiereSillasMoviles true si requiere sillas móviles; false en caso contrario.
	 */
	public void setRequiereSillasMoviles(boolean requiereSillasMoviles) {
		this.requiereSillasMoviles = requiereSillasMoviles;
	}

	/**
	 * Calcula el código hash de la asignatura.
	 *
	 * @return código hash basado en los atributos de la asignatura.
	 */
	@Override
	public int hashCode() {
		return Objects.hash(frecuenciaSemanal, id, nombre, numCreditos, requiereComputadores, requiereSillasMoviles);
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
		AsignaturaDTO other = (AsignaturaDTO) obj;
		return frecuenciaSemanal == other.frecuenciaSemanal && id == other.id && Objects.equals(nombre, other.nombre)
				&& numCreditos == other.numCreditos && requiereComputadores == other.requiereComputadores
				&& requiereSillasMoviles == other.requiereSillasMoviles;
	}

	/**
	 * Devuelve una representación en texto de la asignatura.
	 *
	 * @return cadena de texto con los atributos de la asignatura.
	 */
	@Override
	public String toString() {
		return "AsignaturaDTO [id=" + id + ", nombre=" + nombre + ", numCreditos=" + numCreditos
				+ ", frecuenciaSemanal=" + frecuenciaSemanal + ", requiereComputadores=" + requiereComputadores
				+ ", requiereSillasMoviles=" + requiereSillasMoviles + "]";
	}

}