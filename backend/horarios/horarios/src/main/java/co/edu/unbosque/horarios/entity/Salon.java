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


@Entity
@Table(name = "Salon", 
		uniqueConstraints = {
				@UniqueConstraint(columnNames = {"bloque", "numero_salon"})
})
public class Salon {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private long id;
	
	@Enumerated(EnumType.STRING)
	@Column(nullable = false)
	private Bloque bloque;
	
	@Min(value = 100, message = "El número del salón no es válido")
	@Max(value = 999, message = "El número del salón no es válido")
	private int numeroSalon;
	
	@Min(value = 1, message = "La capacidad del salón debe ser de al menos 1")
	@Max(value = 40, message = "la capacidad del salón no puede ser más de 40")
	private int capacidad;
	
	@Column(name = "tiene_computadores")
	private boolean tieneComputadores;
	
	@Column(name = "tiene_sillas_moviles")
	private boolean tieneSillasMoviles;
	
	public Salon() {
		// TODO Auto-generated constructor stub
	}

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

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public Bloque getBloque() {
		return bloque;
	}

	public void setBloque(Bloque bloque) {
		this.bloque = bloque;
	}

	public int getNumeroSalon() {
		return numeroSalon;
	}

	public void setNumeroSalon(int numeroSalon) {
		this.numeroSalon = numeroSalon;
	}

	public int getCapacidad() {
		return capacidad;
	}

	public void setCapacidad(int capacidad) {
		this.capacidad = capacidad;
	}

	public boolean isTieneComputadores() {
		return tieneComputadores;
	}

	public void setTieneComputadores(boolean tieneComputadores) {
		this.tieneComputadores = tieneComputadores;
	}

	public boolean isTieneSillasMoviles() {
		return tieneSillasMoviles;
	}

	public void setTieneSillasMoviles(boolean tieneSillasMoviles) {
		this.tieneSillasMoviles = tieneSillasMoviles;
	}

	@Override
	public int hashCode() {
		return Objects.hash(bloque, Long.valueOf(id), Integer.valueOf(numeroSalon));
	}

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

	@Override
	public String toString() {
		return "Salon [id=" + id + ", bloque=" + bloque + ", numeroSalon=" + numeroSalon + ", capacidad=" + capacidad
				+ ", tieneComputadores=" + tieneComputadores + ", tieneSillasMoviles=" + tieneSillasMoviles + "]";
	}

	
}
