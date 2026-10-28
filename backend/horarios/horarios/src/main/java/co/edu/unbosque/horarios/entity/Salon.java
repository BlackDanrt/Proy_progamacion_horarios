package co.edu.unbosque.horarios.entity;

import java.util.Objects;

import co.edu.unbosque.horarios.util.enums.Bloque;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;

/**
 * Representa un salón dentro de la institución educativa.
 * Contiene información sobre su bloque, número, capacidad y características.
 *
 * @author Prisma Web
 */
@Entity
@Table(name = "Salon", 
		uniqueConstraints = {
				@UniqueConstraint(columnNames = {"bloque", "numero_salon"})
})
public class Salon {

	/**
	 * Identificador único del salón.
	 */
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private long id;

	/**
	 * Bloque al que pertenece el salón.
	 */
	@Enumerated(EnumType.STRING)
	@Column(nullable = false)
	private Bloque bloque;

	/**
	 * Número asignado al salón.
	 */
	@Min(value = 100, message = "El número del salón no es válido")
	@Max(value = 999, message = "El número del salón no es válido")
	private int numeroSalon;

	/**
	 * Capacidad máxima de personas permitida en el salón.
	 */
	@Min(value = 1, message = "La capacidad del salón debe ser de al menos 1")
	@Max(value = 44, message = "la capacidad del salón no puede ser más de 44")
	private int capacidad;

	/**
	 * Indica si el salón cuenta con computadores.
	 */
	@Column(name = "tiene_computadores")
	private boolean tieneComputadores;

	/**
	 * Indica si el salón cuenta con sillas móviles.
	 */
	@Column(name = "tiene_sillas_moviles")
	private boolean tieneSillasMoviles;

	/**
	 * Constructor vacío de la clase.
	 */
	public Salon() {
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
	public Salon(Bloque bloque, int numeroSalon,
			@Min(value = 1, message = "La capacidad del salón debe ser de al menos 1") @Max(value = 40, message = "la capacidad del salón no puede ser más de 40") int capacidad,
			boolean tieneComputadores, boolean tieneSillasMoviles) {
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
	 * Obtiene el bloque del salón.
	 *
	 * @return bloque al que pertenece el salón.
	 */
	public Bloque getBloque() {
		return bloque;
	}

	/**
	 * Establece el bloque del salón.
	 *
	 * @param bloque bloque al que pertenece el salón.
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
	 * @param numeroSalon número asignado al salón.
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
	 * @return código hash basado en el bloque, el identificador y el número del salón.
	 */
	@Override
	public int hashCode() {
		return Objects.hash(bloque, Long.valueOf(id), Integer.valueOf(numeroSalon));
	}

	/**
	 * Compara este salón con otro objeto para determinar si son iguales.
	 *
	 * @param obj objeto con el que se compara el salón.
	 * @return true si ambos objetos representan el mismo salón; false en caso contrario.
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Salon other = (Salon) obj;
		return bloque == other.bloque && id == other.id && numeroSalon == other.numeroSalon;
	}

	/**
	 * Devuelve una representación en texto del salón y sus características.
	 *
	 * @return cadena de texto con los atributos del salón.
	 */
	@Override
	public String toString() {
		return "Salon [id=" + id + ", bloque=" + bloque + ", numeroSalon=" + numeroSalon + ", capacidad=" + capacidad
				+ ", tieneComputadores=" + tieneComputadores + ", tieneSillasMoviles=" + tieneSillasMoviles + "]";
	}

}