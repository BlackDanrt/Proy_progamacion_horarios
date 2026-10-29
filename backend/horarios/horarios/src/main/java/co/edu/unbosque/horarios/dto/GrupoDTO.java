package co.edu.unbosque.horarios.dto;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import co.edu.unbosque.horarios.util.enums.EstadoGrupo;

/**
 * Representa los datos de un grupo académico, incluyendo su asignatura,
 * profesor, sesiones, capacidad y salón asignado.
 *
 * @author Prisma Web
 */
public class GrupoDTO {

	/**
	 * Identificador único del grupo.
	 */
	private long id;

	/**
	 * Asignatura asociada al grupo.
	 */
	private AsignaturaDTO asignatura;

	/**
	 * Número que identifica al grupo de una asignatura.
	 */
	private int numeroGrupo;

	/**
	 * Número mínimo de estudiantes permitidos en el grupo.
	 */
	private int capacidadMinima;

	/**
	 * Número máximo de estudiantes permitidos en el grupo.
	 */
	private int capacidadMaxima;

	/**
	 * Profesor encargado del grupo.
	 */
	private ProfesorDTO profesor;

	/**
	 * Lista de franjas horarias correspondientes a las sesiones del grupo.
	 */
	private List<FranjaHorariaDTO> sesiones = new ArrayList<>();

	/**
	 * Estado actual del grupo.
	 */
	private EstadoGrupo estado;

	/**
	 * Salón asignado al grupo.
	 */
	private SalonDTO salon;

	/**
	 * Constructor vacío de la clase.
	 */
	public GrupoDTO() {
		// TODO Auto-generated constructor stub
	}

	/**
	 * Construye un grupo con su asignatura, capacidades, profesor, sesiones, estado
	 * y salón asignado.
	 *
	 * @param asignatura      asignatura asociada al grupo.
	 * @param numeroGrupo     número del grupo.
	 * @param capacidadMinima cantidad mínima de estudiantes.
	 * @param capacidadMaxima cantidad máxima de estudiantes.
	 * @param profesor        profesor encargado del grupo.
	 * @param sesiones        lista de sesiones del grupo.
	 * @param estado          estado actual del grupo.
	 * @param salon           salón asignado al grupo.
	 */
	public GrupoDTO(AsignaturaDTO asignatura, int numeroGrupo, int capacidadMinima, int capacidadMaxima,
			ProfesorDTO profesor, List<FranjaHorariaDTO> sesiones, EstadoGrupo estado, SalonDTO salon) {
		super();
		this.asignatura = asignatura;
		this.numeroGrupo = numeroGrupo;
		this.capacidadMinima = capacidadMinima;
		this.capacidadMaxima = capacidadMaxima;
		this.profesor = profesor;
		this.sesiones = sesiones;
		this.estado = estado;
		this.salon = salon;
	}

	/**
	 * Obtiene el identificador del grupo.
	 *
	 * @return identificador del grupo.
	 */
	public long getId() {
		return id;
	}

	/**
	 * Establece el identificador del grupo.
	 *
	 * @param id identificador del grupo.
	 */
	public void setId(long id) {
		this.id = id;
	}

	/**
	 * Obtiene la asignatura asociada al grupo.
	 *
	 * @return asignatura del grupo.
	 */
	public AsignaturaDTO getAsignatura() {
		return asignatura;
	}

	/**
	 * Establece la asignatura asociada al grupo.
	 *
	 * @param asignatura asignatura del grupo.
	 */
	public void setAsignatura(AsignaturaDTO asignatura) {
		this.asignatura = asignatura;
	}

	/**
	 * Obtiene el número del grupo.
	 *
	 * @return número del grupo.
	 */
	public int getNumeroGrupo() {
		return numeroGrupo;
	}

	/**
	 * Establece el número del grupo.
	 *
	 * @param numeroGrupo número del grupo.
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
	 * Establece la capacidad mínima del grupo.
	 *
	 * @param capacidadMinima cantidad mínima de estudiantes.
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
	 * Establece la capacidad máxima del grupo.
	 *
	 * @param capacidadMaxima cantidad máxima de estudiantes.
	 */
	public void setCapacidadMaxima(int capacidadMaxima) {
		this.capacidadMaxima = capacidadMaxima;
	}

	/**
	 * Obtiene el profesor encargado del grupo.
	 *
	 * @return profesor del grupo.
	 */
	public ProfesorDTO getProfesor() {
		return profesor;
	}

	/**
	 * Establece el profesor encargado del grupo.
	 *
	 * @param profesor profesor del grupo.
	 */
	public void setProfesor(ProfesorDTO profesor) {
		this.profesor = profesor;
	}

	/**
	 * Obtiene las sesiones del grupo.
	 *
	 * @return lista de franjas horarias del grupo.
	 */
	public List<FranjaHorariaDTO> getSesiones() {
		return sesiones;
	}

	/**
	 * Establece las sesiones del grupo.
	 *
	 * @param sesiones lista de franjas horarias del grupo.
	 */
	public void setSesiones(List<FranjaHorariaDTO> sesiones) {
		this.sesiones = sesiones;
	}

	/**
	 * Obtiene el estado del grupo.
	 *
	 * @return estado actual del grupo.
	 */
	public EstadoGrupo getEstado() {
		return estado;
	}

	/**
	 * Establece el estado del grupo.
	 *
	 * @param estado estado actual del grupo.
	 */
	public void setEstado(EstadoGrupo estado) {
		this.estado = estado;
	}

	/**
	 * Obtiene el salón asignado al grupo.
	 *
	 * @return salón del grupo.
	 */
	public SalonDTO getSalon() {
		return salon;
	}

	/**
	 * Establece el salón asignado al grupo.
	 *
	 * @param salon salón del grupo.
	 */
	public void setSalon(SalonDTO salon) {
		this.salon = salon;
	}

	/**
	 * Calcula el código hash del grupo.
	 *
	 * @return código hash basado en los atributos del grupo.
	 */
	@Override
	public int hashCode() {
		return Objects.hash(asignatura, Integer.valueOf(capacidadMaxima), Integer.valueOf(capacidadMinima), estado,
				Long.valueOf(id), Integer.valueOf(numeroGrupo), profesor, salon, sesiones);
	}

	/**
	 * Compara este grupo con otro objeto para determinar si son iguales.
	 *
	 * @param obj objeto con el que se compara el grupo.
	 * @return true si ambos objetos tienen los mismos atributos; false en caso
	 *         contrario.
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		GrupoDTO other = (GrupoDTO) obj;
		return Objects.equals(asignatura, other.asignatura) && capacidadMaxima == other.capacidadMaxima
				&& capacidadMinima == other.capacidadMinima && estado == other.estado && id == other.id
				&& numeroGrupo == other.numeroGrupo && Objects.equals(profesor, other.profesor)
				&& Objects.equals(salon, other.salon) && Objects.equals(sesiones, other.sesiones);
	}

	/**
	 * Devuelve una representación en texto del grupo.
	 *
	 * @return cadena de texto con los datos principales del grupo.
	 */
	@Override
	public String toString() {
		return "GrupoDTO [id=" + id + ", asignatura=" + asignatura + ", numeroGrupo=" + numeroGrupo
				+ ", capacidadMinima=" + capacidadMinima + ", capacidadMaxima=" + capacidadMaxima + ", profesor="
				+ (profesor != null ? profesor.getId() : null) + ", sesiones="
				+ (sesiones != null ? sesiones.size() : 0) + ", estado=" + estado + ", salon="
				+ (salon != null ? salon.getId() : null) + "]";
	}

}