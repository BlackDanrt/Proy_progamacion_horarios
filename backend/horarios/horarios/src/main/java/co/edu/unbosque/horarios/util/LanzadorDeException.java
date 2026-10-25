package co.edu.unbosque.horarios.util;

import java.time.Duration;
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
import co.edu.unbosque.horarios.entity.Grupo;
import co.edu.unbosque.horarios.entity.Profesor;

import co.edu.unbosque.horarios.exception.AdministradorInvalidoException;
import co.edu.unbosque.horarios.exception.AsignaturaInvalidaException;
import co.edu.unbosque.horarios.exception.BloqueAlmuerzoInvalidoException;
import co.edu.unbosque.horarios.exception.BloqueInvalidoException;
import co.edu.unbosque.horarios.exception.CapacidadInvalidaException;
import co.edu.unbosque.horarios.exception.CapacidadMaximaExcedidaException;
import co.edu.unbosque.horarios.exception.CapacidadMaximaInvalidaException;
import co.edu.unbosque.horarios.exception.CapacidadMinimaInvalidaException;
import co.edu.unbosque.horarios.exception.CapacidadSalonInsuficienteException;
import co.edu.unbosque.horarios.exception.CapacidadesInvalidasException;
import co.edu.unbosque.horarios.exception.CargaHorariaExcedidaException;
import co.edu.unbosque.horarios.exception.CodigoAdministradorInvalidoException;
import co.edu.unbosque.horarios.exception.ConflictoHorarioProfesorException;
import co.edu.unbosque.horarios.exception.ConflictoHorarioSalonException;
import co.edu.unbosque.horarios.exception.ContraseniaInvalidaException;
import co.edu.unbosque.horarios.exception.CorreoInvalidoException;
import co.edu.unbosque.horarios.exception.DiaInvalidoException;
import co.edu.unbosque.horarios.exception.DisponibilidadHorariaInvalidaException;
import co.edu.unbosque.horarios.exception.DocumentoInvalidoException;
import co.edu.unbosque.horarios.exception.DuracionSesionInvalidaException;
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
import co.edu.unbosque.horarios.exception.ProfesorNoCalificadoException;
import co.edu.unbosque.horarios.exception.ProfesorNoDisponibleException;
import co.edu.unbosque.horarios.exception.RangoHorarioInvalidoException;
import co.edu.unbosque.horarios.exception.RequisitosSalonInvalidosException;
import co.edu.unbosque.horarios.exception.SalonDuplicadoException;
import co.edu.unbosque.horarios.exception.SalonInvalidoException;
import co.edu.unbosque.horarios.exception.SesionesFrecuenciaInvalidaException;
import co.edu.unbosque.horarios.exception.SesionesInvalidasException;
import co.edu.unbosque.horarios.exception.TipoVinculacionInvalidaException;
import co.edu.unbosque.horarios.util.enums.Bloque;
import co.edu.unbosque.horarios.util.enums.Dia;
import co.edu.unbosque.horarios.util.enums.Escalafon;
import co.edu.unbosque.horarios.util.enums.Especialidad;
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
				"^[A-Za-z0-9._%+-]+@unbosque\\.edu\\.co$");

		Matcher matcher =
				pattern.matcher(correo);

		if (matcher.find()) {
			return true;
		}

		throw new CorreoInvalidoException(
				"El correo electrónico ingresado no es válido (asegurese que termina en @unbosque.edu.co)");
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

		if (frecuenciaSemanal > 6) {

			throw new FrecuenciaSemanalInvalidaException(
					"La frecuencia semanal no puede ser mayor a 6");
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

	public static void verificarHoraInicio(LocalTime horaInicio) {

	    if (horaInicio == null) {
	        throw new HoraInicioInvalidaException(
	                "La hora de inicio no puede ser nula");
	    }

	    if (horaInicio.isBefore(LocalTime.of(7, 0))) {
	        throw new HoraInicioInvalidaException(
	                "La hora de inicio no puede ser antes de las 7:00 a. m.");
	    }

	    if (horaInicio.isAfter(LocalTime.of(20, 0))) {
	        throw new HoraInicioInvalidaException(
	                "La hora de inicio no puede ser después de las 8:00 p. m.");
	    }
	}

	public static void verificarHoraFin(LocalTime horaFin) {

	    if (horaFin == null) {
	        throw new HoraFinInvalidaException(
	                "La hora de fin no puede ser nula");
	    }

	    if (horaFin.isBefore(LocalTime.of(9, 0))) {
	        throw new HoraFinInvalidaException(
	                "La hora de fin no puede ser antes de las 9:00 a. m.");
	    }

	    if (horaFin.isAfter(LocalTime.of(22, 0))) {
	        throw new HoraFinInvalidaException(
	                "La hora de fin no puede ser después de las 10:00 p. m.");
	    }
	}
	
	public static void verificarHorario(
	        LocalTime horaInicio,
	        LocalTime horaFin) {

	    if (horaInicio == null || horaFin == null) {
	        throw new HorarioInvalidoException(
	                "Las horas de inicio y fin no pueden ser nulas");
	    }

	    verificarHoraInicio(horaInicio);
	    verificarHoraFin(horaFin);

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

	    if (capacidadMinima < 10) {
	        throw new CapacidadMinimaInvalidaException(
	                "La capacidad mínima no puede ser menor que 10");
	    }
	}

	public static void verificarCapacidadMaxima(
	        int capacidadMaxima) {

	    if (capacidadMaxima > 40) {
	        throw new CapacidadMaximaInvalidaException(
	                "La capacidad máxima no puede ser mayor que 40");
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

	public static void verificarSesiones(int sesiones) {

	    if (sesiones < 0) {
	        throw new SesionesInvalidasException(
	                "El número de sesiones no puede ser menor que 0");
	    }

	    if (sesiones > 3) {
	        throw new SesionesInvalidasException(
	                "El número de sesiones no puede ser mayor que 3");
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

	public static void verificarHorasMaximasSemanales(
	        int horasMaximasSemanales) {

	    if (horasMaximasSemanales <= 0) {
	        throw new HorasMaximasSemanalesInvalidasException(
	                "Las horas máximas semanales deben ser mayores que cero");
	    }

	    if (horasMaximasSemanales > 40) {
	        throw new HorasMaximasSemanalesInvalidasException(
	                "Las horas máximas semanales no pueden ser mayores a 40");
	    }
	}

	public static void verificarEspecialidades(List<Especialidad> especialidades) {
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

	    if (capacidad > 40) {
	        throw new CapacidadInvalidaException(
	                "La capacidad no puede ser mayor a 40");
	    }
	}

	public static void verificarSalonDuplicado(boolean duplicado) {
		if (duplicado) {
			throw new SalonDuplicadoException(
					"El salón ya se encuentra registrado para ese bloque");
		}
	}
	
	
	public static void verificarRangoHorarioInstitucional(Dia dia, LocalTime horaInicio, LocalTime horaFin) {

	    LocalTime inicioJornada = LocalTime.of(7, 0);
	    LocalTime finJornadaSemana = LocalTime.of(22, 0);
	    LocalTime finJornadaSabado = LocalTime.of(13, 0);

	    LocalTime finPermitido = (dia == Dia.SABADO) ? finJornadaSabado : finJornadaSemana;

	    if (horaInicio.isBefore(inicioJornada) || horaFin.isAfter(finPermitido)) {
	        throw new RangoHorarioInvalidoException(
	            dia == Dia.SABADO
	                ? "El sábado solo se pueden asignar clases entre las 7:00 AM y la 1:00 PM"
	                : "De lunes a viernes solo se pueden asignar clases entre las 7:00 AM y las 10:00 PM");
	    }
	}

	public static void verificarBloqueAlmuerzo(LocalTime horaInicio, LocalTime horaFin) {

	    LocalTime inicioAlmuerzo = LocalTime.of(12, 0);
	    LocalTime finAlmuerzo = LocalTime.of(13, 0);

	    boolean seSolapa = horaInicio.isBefore(finAlmuerzo) && horaFin.isAfter(inicioAlmuerzo);

	    if (seSolapa) {
	        throw new BloqueAlmuerzoInvalidoException(
	            "No se pueden asignar clases entre las 12:00 M y la 1:00 PM");
	    }
	}

	public static void verificarDuracionSesion(LocalTime horaInicio, LocalTime horaFin) {

	    long minutos = Duration.between(horaInicio, horaFin).toMinutes();

	    if (minutos != 120) {
	        throw new DuracionSesionInvalidaException(
	            "Cada sesión de clase debe durar exactamente 2 horas");
	    }
	}

	public static void verificarDisponibilidadProfesorParaSesiones(
	        List<FranjaHoraria> disponibilidadProfesor,
	        List<FranjaHoraria> sesionesGrupo) {

	    for (FranjaHoraria sesion : sesionesGrupo) {

	        boolean cubierta = disponibilidadProfesor.stream().anyMatch(franja ->
	                franja.getDia() == sesion.getDia()
	                        && !sesion.getHoraInicio().isBefore(franja.getHoraInicio())
	                        && !sesion.getHoraFin().isAfter(franja.getHoraFin()));

	        if (!cubierta) {
	            throw new ProfesorNoDisponibleException(
	                "El profesor no tiene disponibilidad horaria para la sesión del día "
	                    + sesion.getDia() + " de " + sesion.getHoraInicio() + " a " + sesion.getHoraFin());
	        }
	    }
	}

	public static void verificarSinConflictoHorarioProfesor(
	        List<Grupo> gruposDelProfesor,
	        List<FranjaHoraria> nuevasSesiones,
	        long idGrupoActual) {

	    for (Grupo grupo : gruposDelProfesor) {

	        if (grupo.getId() == idGrupoActual) continue;

	        for (FranjaHoraria existente : grupo.getSesiones()) {
	            for (FranjaHoraria nueva : nuevasSesiones) {

	                boolean mismoDia = existente.getDia() == nueva.getDia();
	                boolean seSolapan = nueva.getHoraInicio().isBefore(existente.getHoraFin())
	                        && nueva.getHoraFin().isAfter(existente.getHoraInicio());

	                if (mismoDia && seSolapan) {
	                    throw new ConflictoHorarioProfesorException(
	                        "El profesor ya tiene una clase asignada el día " + existente.getDia()
	                            + " entre " + existente.getHoraInicio() + " y " + existente.getHoraFin());
	                }
	            }
	        }
	    }
	}

	public static void verificarSinConflictoHorarioSalon(
	        List<Grupo> gruposDelSalon,
	        List<FranjaHoraria> nuevasSesiones,
	        long idGrupoActual) {

	    for (Grupo grupo : gruposDelSalon) {

	        if (grupo.getId() == idGrupoActual) continue;

	        for (FranjaHoraria existente : grupo.getSesiones()) {
	            for (FranjaHoraria nueva : nuevasSesiones) {

	                boolean mismoDia = existente.getDia() == nueva.getDia();
	                boolean seSolapan = nueva.getHoraInicio().isBefore(existente.getHoraFin())
	                        && nueva.getHoraFin().isAfter(existente.getHoraInicio());

	                if (mismoDia && seSolapan) {
	                    throw new ConflictoHorarioSalonException(
	                        "El salón ya está ocupado el día " + existente.getDia()
	                            + " entre " + existente.getHoraInicio() + " y " + existente.getHoraFin());
	                }
	            }
	        }
	    }
	}

	public static void verificarCapacidadSalon(int capacidadSalon, int capacidadMaximaGrupo) {

	    if (capacidadSalon < capacidadMaximaGrupo) {
	        throw new CapacidadSalonInsuficienteException(
	            "La capacidad del salón (" + capacidadSalon
	                + ") es menor a la capacidad máxima del grupo (" + capacidadMaximaGrupo + ")");
	    }
	}

	public static void verificarRequisitosSalon(
	        boolean requiereComputadores, boolean tieneComputadores,
	        boolean requiereSillasMoviles, boolean tieneSillasMoviles) {

	    if (requiereComputadores && !tieneComputadores) {
	        throw new RequisitosSalonInvalidosException(
	            "La asignatura requiere un salón con computadores");
	    }

	    if (requiereSillasMoviles && !tieneSillasMoviles) {
	        throw new RequisitosSalonInvalidosException(
	            "La asignatura requiere un salón con sillas móviles");
	    }
	}

	public static void verificarCargaHorariaProfesor(
	        int horasMaximasSemanales, int horasYaAsignadas, int horasNuevoGrupo) {

	    if (horasYaAsignadas + horasNuevoGrupo > horasMaximasSemanales) {
	        throw new CargaHorariaExcedidaException(
	            "El profesor superaría sus horas máximas semanales ("
	                + horasMaximasSemanales + "h) al asignarle este grupo");
	    }
	}

	public static void verificarSesionesFrecuencia(int numeroSesiones, int frecuenciaSemanal) {

	    if (numeroSesiones != frecuenciaSemanal) {
	        throw new SesionesFrecuenciaInvalidaException(
	            "El número de sesiones del grupo (" + numeroSesiones
	                + ") debe coincidir con la frecuencia semanal de la asignatura (" + frecuenciaSemanal + ")");
	    }
	}

	public static void verificarCapacidadMaximaInstitucional(int capacidadMaxima) {

	    final int CAPACIDAD_MAXIMA_INSTITUCIONAL = 40;
	    final double TOLERANCIA = 0.10;
	    int limite = (int) Math.floor(CAPACIDAD_MAXIMA_INSTITUCIONAL * (1 + TOLERANCIA)); // 44

	    if (capacidadMaxima > limite) {
	        throw new CapacidadMaximaExcedidaException(
	            "La capacidad máxima no puede superar " + limite
	                + " estudiantes (40 + 10% de tolerancia)");
	    }
	}
}