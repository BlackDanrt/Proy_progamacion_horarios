package co.edu.unbosque.horarios.entity;

import java.time.LocalDateTime;
import java.util.Objects;

import co.edu.unbosque.horarios.util.enums.Servicio;
import co.edu.unbosque.horarios.util.enums.TipoAccion;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

/**
 * Entidad de persistencia mapeada a la tabla "usuarioavion" (según definición
 * de tabla) encargada de registrar los eventos y acciones críticas del sistema
 * para fines de control y auditoría.
 * @author Prisma Web
 * @version 1.0
 */
@Entity
@Table(name = "auditoria")
public class Auditoria {

	/** Identificador único autoincremental del registro de auditoría. */
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private long id;

	/** Identificador numérico del usuario que ejecutó la acción. */
	private Long idUsuario;

	/** Nombre de cuenta o alias del usuario en el momento de la transacción. */
	private String nombreUsuario;

	/** Tipo de operación o acción realizada. */
	@Enumerated(EnumType.STRING)
	private TipoAccion accion;

	private boolean esAdmin;

	/**
	 * Módulo o servicio específico del ecosistema que fue invocado durante la
	 * operación.
	 */
	@Enumerated(EnumType.STRING)
	private Servicio servicioUsado;

	/** Marca de tiempo exacta (fecha y hora) en la que se efectuó el evento. */
	private LocalDateTime fecha;

	/**
	 * Constructor por defecto de Auditoria.
	 */
	public Auditoria() {
		// TODO Auto-generated constructor stub
	}

	/**
	 * Constructor con parámetros completo para inicializar todos los campos de
	 * trazabilidad. * @param idUsuario ID del usuario actor
	 * 
	 * @param nombreUsuario Nombre del usuario actor
	 * @param accion        Naturaleza de la operación realizada
	 * @param rolUsuario    Rol del usuario ejecutor
	 * @param servicioUsado Componente de software utilizado
	 * @param fecha         Instante de tiempo del suceso
	 */
	public Auditoria(Long idUsuario, String nombreUsuario, TipoAccion accion, boolean esAdmin, Servicio servicioUsado,
			LocalDateTime fecha) {
		super();
		this.idUsuario = idUsuario;
		this.nombreUsuario = nombreUsuario;
		this.accion = accion;
		this.esAdmin = esAdmin;
		this.servicioUsado = servicioUsado;
		this.fecha = fecha;
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public Long getIdUsuario() {
		return idUsuario;
	}

	public void setIdUsuario(Long idUsuario) {
		this.idUsuario = idUsuario;
	}

	public String getNombreUsuario() {
		return nombreUsuario;
	}

	public void setNombreUsuario(String nombreUsuario) {
		this.nombreUsuario = nombreUsuario;
	}

	public TipoAccion getAccion() {
		return accion;
	}

	public void setAccion(TipoAccion accion) {
		this.accion = accion;
	}

	public LocalDateTime getFecha() {
		return fecha;
	}

	public void setFecha(LocalDateTime fecha) {
		this.fecha = fecha;
	}

	public boolean isEsAdmin() {
		return esAdmin;
	}

	public void setEsAdmin(boolean esAdmin) {
		this.esAdmin = esAdmin;
	}

	public Servicio getServicioUsado() {
		return servicioUsado;
	}

	public void setServicioUsado(Servicio servicioUsado) {
		this.servicioUsado = servicioUsado;
	}

	@Override
	public String toString() {
		return "Auditoria [id=" + id + ", idUsuario=" + idUsuario + ", nombreUsuario=" + nombreUsuario + ", accion="
				+ accion + ", esAdmin=" + esAdmin + ", servicioUsado=" + servicioUsado + ", fecha=" + fecha + "]";
	}

	@Override
	public int hashCode() {
		return Objects.hash(accion, fecha, id, idUsuario, nombreUsuario, esAdmin, servicioUsado);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Auditoria other = (Auditoria) obj;
		return accion == other.accion && Objects.equals(fecha, other.fecha) && id == other.id
				&& Objects.equals(idUsuario, other.idUsuario) && Objects.equals(nombreUsuario, other.nombreUsuario)
				&& esAdmin == other.esAdmin && servicioUsado == other.servicioUsado;
	}

}
