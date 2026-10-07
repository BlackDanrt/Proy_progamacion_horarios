package co.edu.unbosque.horarios.dto;

import java.util.Objects;

import co.edu.unbosque.horarios.util.enums.Bloque;

public class SalonDTO {

	private long id;
	private Bloque bloque;
	private int numeroSalon;
	private int capacidad;
	private boolean tieneComputadores;
	private boolean tieneSillasMoviles;

	public SalonDTO() {
		// TODO Auto-generated constructor stub
	}

	public SalonDTO(Bloque bloque, int numeroSalon, int capacidad, boolean tieneComputadores,
			boolean tieneSillasMoviles) {
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
		return Objects.hash(bloque, capacidad, id, numeroSalon, tieneComputadores, tieneSillasMoviles);
	}

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

	@Override
	public String toString() {
		return "SalonDTO [id=" + id + ", bloque=" + bloque + ", numeroSalon=" + numeroSalon + ", capacidad=" + capacidad
				+ ", tieneComputadores=" + tieneComputadores + ", tieneSillasMoviles=" + tieneSillasMoviles + "]";
	}

}
