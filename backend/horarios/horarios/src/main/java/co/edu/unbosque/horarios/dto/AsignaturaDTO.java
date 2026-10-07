package co.edu.unbosque.horarios.dto;

import java.util.Objects;

public class AsignaturaDTO {

	private long id;
	private String nombre;
	private int numCreditos;
	private int frecuenciaSemanal;
	private boolean requiereComputadores;
	private boolean requiereSillasMoviles;

	public AsignaturaDTO() {
		// TODO Auto-generated constructor stub
	}

	public AsignaturaDTO(String nombre, int numCreditos, int frecuenciaSemanal, boolean requiereComputadores,
			boolean requiereSillasMoviles) {
		super();
		this.nombre = nombre;
		this.numCreditos = numCreditos;
		this.frecuenciaSemanal = frecuenciaSemanal;
		this.requiereComputadores = requiereComputadores;
		this.requiereSillasMoviles = requiereSillasMoviles;
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getNombre() {
		return nombre;
	}

	public void setNombre(String nombre) {
		this.nombre = nombre;
	}

	public int getNumCreditos() {
		return numCreditos;
	}

	public void setNumCreditos(int numCreditos) {
		this.numCreditos = numCreditos;
	}

	public int getFrecuenciaSemanal() {
		return frecuenciaSemanal;
	}

	public void setFrecuenciaSemanal(int frecuenciaSemanal) {
		this.frecuenciaSemanal = frecuenciaSemanal;
	}

	public boolean isRequiereComputadores() {
		return requiereComputadores;
	}

	public void setRequiereComputadores(boolean requiereComputadores) {
		this.requiereComputadores = requiereComputadores;
	}

	public boolean isRequiereSillasMoviles() {
		return requiereSillasMoviles;
	}

	public void setRequiereSillasMoviles(boolean requiereSillasMoviles) {
		this.requiereSillasMoviles = requiereSillasMoviles;
	}

	@Override
	public int hashCode() {
		return Objects.hash(frecuenciaSemanal, id, nombre, numCreditos, requiereComputadores, requiereSillasMoviles);
	}

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

	@Override
	public String toString() {
		return "AsignaturaDTO [id=" + id + ", nombre=" + nombre + ", numCreditos=" + numCreditos
				+ ", frecuenciaSemanal=" + frecuenciaSemanal + ", requiereComputadores=" + requiereComputadores
				+ ", requiereSillasMoviles=" + requiereSillasMoviles + "]";
	}

}
