package co.edu.unbosque.horarios.util;

import java.time.LocalTime;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import co.edu.unbosque.horarios.dto.AdministradorDTO;
import co.edu.unbosque.horarios.dto.AsignaturaDTO;
import co.edu.unbosque.horarios.dto.FranjaHorariaDTO;
import co.edu.unbosque.horarios.dto.GrupoDTO;
import co.edu.unbosque.horarios.dto.ProfesorDTO;
import co.edu.unbosque.horarios.dto.SalonDTO;
import co.edu.unbosque.horarios.entity.Asignatura;
import co.edu.unbosque.horarios.entity.FranjaHoraria;
import co.edu.unbosque.horarios.entity.Profesor;

import co.edu.unbosque.horarios.exception.AdministradorInvalidoException;
import co.edu.unbosque.horarios.exception.AsignaturaInvalidaException;
import co.edu.unbosque.horarios.exception.BloqueInvalidoException;
import co.edu.unbosque.horarios.exception.CapacidadInvalidaException;
import co.edu.unbosque.horarios.exception.CapacidadMaximaInvalidaException;
import co.edu.unbosque.horarios.exception.CapacidadMinimaInvalidaException;
import co.edu.unbosque.horarios.exception.CapacidadesInvalidasException;
import co.edu.unbosque.horarios.exception.CodigoAdministradorInvalidoException;
import co.edu.unbosque.horarios.exception.ContraseniaInvalidaException;
import co.edu.unbosque.horarios.exception.CorreoInvalidoException;
import co.edu.unbosque.horarios.exception.DiaInvalidoException;
import co.edu.unbosque.horarios.exception.DisponibilidadHorariaInvalidaException;
import co.edu.unbosque.horarios.exception.DocumentoInvalidoException;
import co.edu.unbosque.horarios.exception.EscalafonInvalidoException;
import co.edu.unbosque.horarios.exception.EspecialidadesInvalidasException;
import co.edu.unbosque.horarios.exception.EstadoGrupoInvalidoException;
import co.edu.unbosque.horarios.exception.FranjaHorariaInvalidaException;
import co.edu.unbosque.horarios.exception.FrecuenciaSemanalInvalidaException;
import co.edu.unbosque.horarios.exception.GrupoInvalidoException;
import co.edu.unbosque.horarios.exception.HoraFinInvalidaException;
import co.edu.unbosque.horarios.exception.HoraInicioInvalidaException;
import co.edu.unbosque.horarios.exception.HorarioInvalidoException;
import co.edu.unbosque.horarios.exception.HorasMaximasSemanalesInvalidasException;
import co.edu.unbosque.horarios.exception.IdInvalidoException;
import co.edu.unbosque.horarios.exception.NombreInvalidoException;
import co.edu.unbosque.horarios.exception.NumeroCreditosInvalidoException;
import co.edu.unbosque.horarios.exception.NumeroGrupoInvalidoException;
import co.edu.unbosque.horarios.exception.NumeroSalonInvalidoException;
import co.edu.unbosque.horarios.exception.ProfesorInvalidoException;
import co.edu.unbosque.horarios.exception.SalonDuplicadoException;
import co.edu.unbosque.horarios.exception.SalonInvalidoException;
import co.edu.unbosque.horarios.exception.SesionesInvalidasException;
import co.edu.unbosque.horarios.exception.TipoVinculacionInvalidaException;
import co.edu.unbosque.horarios.util.enums.Bloque;
import co.edu.unbosque.horarios.util.enums.Dia;
import co.edu.unbosque.horarios.util.enums.Escalafon;
import co.edu.unbosque.horarios.util.enums.EstadoGrupo;
import co.edu.unbosque.horarios.util.enums.TipoVinculacion;

/**
 * Clase utilitaria que centraliza la validación de datos del
 * sistema de horarios.
 *
 * @version 1.0
 */
public class LanzadorDeException {

	public static void verificarNombre(String nombre) {

		if (nombre == null || nombre.trim().isEmpty()) {
			throw new NombreInvalidoException(
					"El nombre no puede estar vacío");
		}

		if (nombre.contains("  ")) {
			throw new NombreInvalidoException(
					"El nombre no puede contener espacios dobles");
		}

		if (!nombre.matches(
				"^[A-Za-zÁÉÍÓÚáéíóúÑñ ]+$")) {

			throw new NombreInvalidoException(
					"El nombre solo debe contener letras y espacios");
		}

		String[] palabras =
				nombre.trim().split("\\s+");

		if (palabras.length < 1
				|| palabras.length > 2) {

			throw new NombreInvalidoException(
					"El nombre debe tener mínimo una palabra y máximo dos");
		}
	}

	public static void verificarApellido(String apellido) {

		if (apellido == null
				|| apellido.trim().isEmpty()) {

			throw new NombreInvalidoException(
					"El apellido no puede estar vacío");
		}

		if (apellido.contains("  ")) {

			throw new NombreInvalidoException(
					"El apellido no puede contener espacios dobles");
		}

		if (!apellido.matches(
				"^[A-Za-zÁÉÍÓÚáéíóúÑñ ]+$")) {

			throw new NombreInvalidoException(
					"El apellido solo debe contener letras y espacios");
		}

		String[] palabras =
				apellido.trim().split("\\s+");

		if (palabras.length < 1
				|| palabras.length > 2) {

			throw new NombreInvalidoException(
					"El apellido debe tener mínimo una palabra y máximo dos");
		}
	}

	public static boolean verificarCorreoElectronico(
			String correo) {

		if (correo == null
				|| correo.trim().isEmpty()) {

			throw new CorreoInvalidoException(
					"El correo electrónico no puede estar vacío");
		}

		Pattern pattern = Pattern.compile(
				"^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,6}$");

		Matcher matcher =
				pattern.matcher(correo);

		if (matcher.find()) {
			return true;
		}

		throw new CorreoInvalidoException(
				"El correo electrónico ingresado no es válido");
	}

	public static void verificarId(Long id) {

		if (id == null) {
			throw new IdInvalidoException(
					"El ID no puede ser nulo");
		}

		if (id <= 0) {
			throw new IdInvalidoException(
					"El ID debe ser un número positivo");
		}
	}

	public static void verificarContrasena(
			String contrasena) {

		if (contrasena == null
				|| contrasena.isEmpty()) {

			throw new ContraseniaInvalidaException(
					"La contraseña no puede estar vacía");
		}

		if (contrasena.contains(" ")) {

			throw new ContraseniaInvalidaException(
					"La contraseña no debe contener espacios");
		}

		if (contrasena.length() < 8) {

			throw new ContraseniaInvalidaException(
					"La contraseña debe tener al menos 8 caracteres");
		}

		if (!contrasena.matches(
				"^(?=.*[A-Z])(?=.*[0-9]).+$")) {

			throw new ContraseniaInvalidaException(
					"La contraseña debe contener al menos una mayúscula y un número");
		}
	}

	public static void verificarAdministrador(
			AdministradorDTO administrador) {

		if (administrador == null) {

			throw new AdministradorInvalidoException(
					"El administrador no puede ser nulo");
		}
	}

	public static void verificarDocumento(
			long documento) {

		if (documento <= 0) {

			throw new DocumentoInvalidoException(
					"El documento debe ser un número positivo");
		}

		String documentoTexto =
				String.valueOf(documento);

		if (documentoTexto.length() < 6
				|| documentoTexto.length() > 10) {

			throw new DocumentoInvalidoException(
					"El documento debe tener entre 6 y 10 dígitos");
		}
	}

	public static void verificarCodigoAdministrador(
			String codigoAdministrador) {

		if (codigoAdministrador == null
				|| codigoAdministrador.trim().isEmpty()) {

			throw new CodigoAdministradorInvalidoException(
					"El código de administrador no puede estar vacío");
		}

		if (codigoAdministrador.contains(" ")) {

			throw new CodigoAdministradorInvalidoException(
					"El código de administrador no debe contener espacios");
		}

		if (!codigoAdministrador.matches(
				"^[A-Za-z0-9]+$")) {

			throw new CodigoAdministradorInvalidoException(
					"El código de administrador solo debe contener letras y números");
		}
	}

	public static void verificarCorreoDuplicado(
			boolean duplicado) {

		if (duplicado) {

			throw new CorreoInvalidoException(
					"El correo ya se encuentra registrado");
		}
	}

	public static void verificarCodigoAdministradorDuplicado(
			boolean duplicado) {

		if (duplicado) {

			throw new CodigoAdministradorInvalidoException(
					"El código de administrador ya se encuentra registrado");
		}
	}

	public static void verificarDocumentoDuplicado(
			boolean duplicado) {

		if (duplicado) {

			throw new DocumentoInvalidoException(
					"El documento ya se encuentra registrado");
		}
	}

	/**
	 * Verifica una asignatura entidad.
	 */
	public static void verificarAsignatura(
			AsignaturaDTO asignatura) {

		if (asignatura == null) {

			throw new AsignaturaInvalidaException(
					"La asignatura no puede ser nula");
		}
	}

	public static void verificarNumCreditos(
			int numCreditos) {

		if (numCreditos <= 0) {

			throw new NumeroCreditosInvalidoException(
					"El número de créditos debe ser mayor que cero");
		}

		if (numCreditos > 10) {

			throw new NumeroCreditosInvalidoException(
					"El número de créditos no puede ser mayor a 10");
		}
	}

	public static void verificarFrecuenciaSemanal(
			int frecuenciaSemanal) {

		if (frecuenciaSemanal <= 0) {

			throw new FrecuenciaSemanalInvalidaException(
					"La frecuencia semanal debe ser mayor que cero");
		}

		if (frecuenciaSemanal > 7) {

			throw new FrecuenciaSemanalInvalidaException(
					"La frecuencia semanal no puede ser mayor a 7");
		}
	}

	public static void verificarNombreDuplicado(
			boolean duplicado) {

		if (duplicado) {

			throw new NombreInvalidoException(
					"El nombre de la asignatura ya se encuentra registrado");
		}
	}

	public static void verificarFranjaHoraria(
			FranjaHorariaDTO franjaHoraria) {

		if (franjaHoraria == null) {

			throw new FranjaHorariaInvalidaException(
					"La franja horaria no puede ser nula");
		}
	}

	public static void verificarDia(Dia dia) {

		if (dia == null) {

			throw new DiaInvalidoException(
					"El día no puede ser nulo");
		}
	}

	public static void verificarHoraInicio(
			LocalTime horaInicio) {

		if (horaInicio == null) {

			throw new HoraInicioInvalidaException(
					"La hora de inicio no puede ser nula");
		}
	}

	public static void verificarHoraFin(
			LocalTime horaFin) {

		if (horaFin == null) {

			throw new HoraFinInvalidaException(
					"La hora de fin no puede ser nula");
		}
	}

	public static void verificarHorario(
			LocalTime horaInicio,
			LocalTime horaFin) {

		if (horaInicio == null
				|| horaFin == null) {

			throw new HorarioInvalidoException(
					"Las horas de inicio y fin no pueden ser nulas");
		}

		if (!horaInicio.isBefore(horaFin)) {

			throw new HorarioInvalidoException(
					"La hora de inicio debe ser anterior a la hora de fin");
		}
	}

	public static void verificarGrupo(
			GrupoDTO grupo) {

		if (grupo == null) {

			throw new GrupoInvalidoException(
					"El grupo no puede ser nulo");
		}
	}

	public static void verificarNumeroGrupo(
			int numeroGrupo) {

		if (numeroGrupo <= 0) {

			throw new NumeroGrupoInvalidoException(
					"El número del grupo debe ser mayor que cero");
		}
	}

	public static void verificarCapacidadMinima(
			int capacidadMinima) {

		if (capacidadMinima <= 0) {

			throw new CapacidadMinimaInvalidaException(
					"La capacidad mínima debe ser mayor que cero");
		}
	}

	public static void verificarCapacidadMaxima(
			int capacidadMaxima) {

		if (capacidadMaxima <= 0) {

			throw new CapacidadMaximaInvalidaException(
					"La capacidad máxima debe ser mayor que cero");
		}
	}

	public static void verificarCapacidades(
			int capacidadMinima,
			int capacidadMaxima) {

		if (capacidadMinima > capacidadMaxima) {

			throw new CapacidadesInvalidasException(
					"La capacidad mínima no puede ser mayor que la capacidad máxima");
		}
	}

	public static void verificarProfesor(
			Profesor profesor) {

		if (profesor == null) {

			throw new ProfesorInvalidoException(
					"El profesor no puede ser nulo");
		}
	}

	public static void verificarSesiones(
			int sesiones) {

		if (sesiones <= 0) {

			throw new SesionesInvalidasException(
					"El número de sesiones debe ser mayor que cero");
		}
	}

	public static void verificarEstadoGrupo(
			EstadoGrupo estado) {

		if (estado == null) {

			throw new EstadoGrupoInvalidoException(
					"El estado del grupo no puede ser nulo");
		}
	}

	public static void verificarNumeroGrupoDuplicado(
			boolean duplicado) {

		if (duplicado) {

			throw new NumeroGrupoInvalidoException(
					"El número de grupo ya se encuentra registrado");
		}
	}
	
	public static void verificarProfesor(ProfesorDTO profesor) {
		if (profesor == null) {
			throw new ProfesorInvalidoException(
					"El profesor no puede ser nulo");
		}
	}

	public static void verificarTipoVinculacion(TipoVinculacion tipoVinculacion) {
		if (tipoVinculacion == null) {
			throw new TipoVinculacionInvalidaException(
					"El tipo de vinculación no puede ser nulo");
		}
	}

	public static void verificarEscalafon(Escalafon escalafon) {
		if (escalafon == null) {
			throw new EscalafonInvalidoException(
					"El escalafón no puede ser nulo");
		}
	}

	public static void verificarHorasMaximasSemanales(int horasMaximasSemanales) {
		if (horasMaximasSemanales <= 0) {
			throw new HorasMaximasSemanalesInvalidasException(
					"Las horas máximas semanales deben ser mayores que cero");
		}
	}

	public static void verificarEspecialidades(List<Asignatura> especialidades) {
		if (especialidades == null || especialidades.isEmpty()) {
			throw new EspecialidadesInvalidasException(
					"Las especialidades no pueden ser nulas o estar vacías");
		}
	}

	public static void verificarDisponibilidadHoraria(List<FranjaHoraria> disponibilidadHoraria) {
		if (disponibilidadHoraria == null || disponibilidadHoraria.isEmpty()) {
			throw new DisponibilidadHorariaInvalidaException(
					"La disponibilidad horaria no puede ser nula o estar vacía");
		}
	}
	
	public static void verificarSalon(SalonDTO salon) {
		if (salon == null) {
			throw new SalonInvalidoException(
					"El salón no puede ser nulo");
		}
	}

	public static void verificarBloque(Bloque bloque) {
		if (bloque == null) {
			throw new BloqueInvalidoException(
					"El bloque no puede ser nulo");
		}
	}

	public static void verificarNumeroSalon(int numeroSalon) {
		if (numeroSalon <= 0) {
			throw new NumeroSalonInvalidoException(
					"El número del salón debe ser mayor que cero");
		}
	}

	public static void verificarCapacidad(int capacidad) {
		if (capacidad <= 0) {
			throw new CapacidadInvalidaException(
					"La capacidad debe ser mayor que cero");
		}
	}

	public static void verificarSalonDuplicado(boolean duplicado) {
		if (duplicado) {
			throw new SalonDuplicadoException(
					"El salón ya se encuentra registrado para ese bloque");
		}
	}
}