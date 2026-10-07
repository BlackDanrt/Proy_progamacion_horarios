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

@Entity
@Table(name = "Asignatura")
public class Asignatura {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private long id;
	
	@Column(unique = true, nullable = false)
	@NotBlank(message = "El nombre de la asignatura no puede estar vacío")
	@Size(max = 100, message = "El nombre de la asignatura no puede superar los 100 caracteres")
	private String nombre;
	
	@Min(value = 1, message = "Debe tener al menos 1 crédito")
	@Max(value = 16, message = "No puede superar los 16 créditos")
	private int numCreditos;
	
	@Min(value = 1, message = "La frecuencia semanal debe ser al menos 1 día")
	@Max(value = 7, message = "No puede exceder los 7 días de la semana")
	private int frecuenciaSemanal;
	
	@Column(name = "requiere_computadores")
	private boolean requiereComputadores;
	
	@Column(name = "requiere_sillas_moviles")
	private boolean requiereSillasMoviles;
	
	public Asignatura() {
		// TODO Auto-generated constructor stub
	}

	public Asignatura(String nombre, int numCreditos, int frecuenciaSemanal, boolean requiereComputadores,
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
		return Objects.hash(Integer.valueOf(frecuenciaSemanal), Long.valueOf(id), nombre, Integer.valueOf(numCreditos),
				Boolean.valueOf(requiereComputadores), Boolean.valueOf(requiereSillasMoviles));
	}

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

	@Override
	public String toString() {
		return "Asignatura [id=" + id + ", nombre=" + nombre + ", numCreditos=" + numCreditos + ", frecuenciaSemanal="
				+ frecuenciaSemanal + ", requiereComputadores=" + requiereComputadores + ", requiereSillasMoviles="
				+ requiereSillasMoviles + "]";
	}
	
}
