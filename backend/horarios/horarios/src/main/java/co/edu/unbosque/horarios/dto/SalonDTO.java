package co.edu.unbosque.horarios.dto;

import java.util.Objects;

import co.edu.unbosque.horarios.util.enums.Bloque;

/**
 * Representa los datos de un salón para su transferencia entre
 * las diferentes capas de la aplicación.
 *
 * @author Prisma Web
 */
public class SalonDTO {

	/**
	 * Identificador único del salón.
	 */
	private long id;

	/**
	 * Bloque al que pertenece el salón.
	 */
	private Bloque bloque;

	/**
	 * Número asignado al salón.
	 */
	private int numeroSalon;

	/**
	 * Capacidad máxima de personas permitida en el salón.
	 */
	private int capacidad;

	/**
	 * Indica si el salón cuenta con computadores.
	 */
	private boolean tieneComputadores;

	/**
	 * Indica si el salón cuenta con sillas móviles.
	 */
	private boolean tieneSillasMoviles;

	/**
	 * Constructor vacío de la clase.
	 */
	public SalonDTO() {
		// TODO Auto-generated constructor stub
	}

	/**
	 * Construye un salón con sus características principales.
	 *
	 * @param bloque bloque al que pertenece el salón.
	 * @param numeroSalon número asignado al salón.
	 * @param capacidad capacidad máxima del salón.
	 * @param tieneComputadores indica si cuenta con computadores.
	 * @param tieneSillasMoviles indica si cuenta con sillas móviles.
	 */
	public SalonDTO(Bloque bloque, int numeroSalon, int capacidad, boolean tieneComputadores,
			boolean tieneSillasMoviles) {
		super();
		this.bloque = bloque;
		this.numeroSalon = numeroSalon;
		this.capacidad = capacidad;
		this.tieneComputadores = tieneComputadores;
		this.tieneSillasMoviles = tieneSillasMoviles;
	}

	/**
	 * Obtiene el identificador del salón.
	 *
	 * @return identificador del salón.
	 */
	public long getId() {
		return id;
	}

	/**
	 * Establece el identificador del salón.
	 *
	 * @param id identificador del salón.
	 */
	public void setId(long id) {
		this.id = id;
	}

	/**
	 * Obtiene el bloque al que pertenece el salón.
	 *
	 * @return bloque del salón.
	 */
	public Bloque getBloque() {
		return bloque;
	}

	/**
	 * Establece el bloque al que pertenece el salón.
	 *
	 * @param bloque bloque del salón.
	 */
	public void setBloque(Bloque bloque) {
		this.bloque = bloque;
	}

	/**
	 * Obtiene el número del salón.
	 *
	 * @return número del salón.
	 */
	public int getNumeroSalon() {
		return numeroSalon;
	}

	/**
	 * Establece el número del salón.
	 *
	 * @param numeroSalon número del salón.
	 */
	public void setNumeroSalon(int numeroSalon) {
		this.numeroSalon = numeroSalon;
	}

	/**
	 * Obtiene la capacidad del salón.
	 *
	 * @return capacidad máxima del salón.
	 */
	public int getCapacidad() {
		return capacidad;
	}

	/**
	 * Establece la capacidad del salón.
	 *
	 * @param capacidad capacidad máxima del salón.
	 */
	public void setCapacidad(int capacidad) {
		this.capacidad = capacidad;
	}

	/**
	 * Indica si el salón cuenta con computadores.
	 *
	 * @return true si tiene computadores; false en caso contrario.
	 */
	public boolean isTieneComputadores() {
		return tieneComputadores;
	}

	/**
	 * Establece si el salón cuenta con computadores.
	 *
	 * @param tieneComputadores true si tiene computadores; false en caso contrario.
	 */
	public void setTieneComputadores(boolean tieneComputadores) {
		this.tieneComputadores = tieneComputadores;
	}

	/**
	 * Indica si el salón cuenta con sillas móviles.
	 *
	 * @return true si tiene sillas móviles; false en caso contrario.
	 */
	public boolean isTieneSillasMoviles() {
		return tieneSillasMoviles;
	}

	/**
	 * Establece si el salón cuenta con sillas móviles.
	 *
	 * @param tieneSillasMoviles true si tiene sillas móviles; false en caso contrario.
	 */
	public void setTieneSillasMoviles(boolean tieneSillasMoviles) {
		this.tieneSillasMoviles = tieneSillasMoviles;
	}

	/**
	 * Calcula el código hash del salón.
	 *
	 * @return código hash basado en los atributos del salón.
	 */
	@Override
	public int hashCode() {
		return Objects.hash(bloque, capacidad, id, numeroSalon, tieneComputadores, tieneSillasMoviles);
	}

	/**
	 * Compara este salón con otro objeto para determinar si son iguales.
	 *
	 * @param obj objeto con el que se compara el salón.
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
		SalonDTO other = (SalonDTO) obj;
		return bloque == other.bloque && capacidad == other.capacidad && id == other.id
				&& numeroSalon == other.numeroSalon && tieneComputadores == other.tieneComputadores
				&& tieneSillasMoviles == other.tieneSillasMoviles;
	}

	/**
	 * Devuelve una representación en texto del salón.
	 *
	 * @return cadena de texto con los atributos del salón.
	 */
	@Override
	public String toString() {
		return "SalonDTO [id=" + id + ", bloque=" + bloque + ", numeroSalon=" + numeroSalon + ", capacidad=" + capacidad
				+ ", tieneComputadores=" + tieneComputadores + ", tieneSillasMoviles=" + tieneSillasMoviles + "]";
	}

}