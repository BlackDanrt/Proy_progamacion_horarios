package co.edu.unbosque.horarios.util;

import java.time.Duration;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import co.edu.unbosque.horarios.dto.AdministradorDTO;
import co.edu.unbosque.horarios.dto.AsignaturaDTO;
import co.edu.unbosque.horarios.dto.FranjaHorariaDTO;
import co.edu.unbosque.horarios.dto.GrupoDTO;
import co.edu.unbosque.horarios.dto.ProfesorDTO;
import co.edu.unbosque.horarios.dto.SalonDTO;
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
import co.edu.unbosque.horarios.exception.InscritosInvalidosException;
import co.edu.unbosque.horarios.exception.NombreInvalidoException;
import co.edu.unbosque.horarios.exception.NumeroCreditosInvalidoException;
import co.edu.unbosque.horarios.exception.NumeroGrupoInvalidoException;
import co.edu.unbosque.horarios.exception.NumeroSalonInvalidoException;
import co.edu.unbosque.horarios.exception.PeriodoInvalidoException;
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
 * Clase utilitaria que centraliza la validación de datos del sistema de
 * horarios.
 *
 * @version 1.0
 */
public class LanzadorDeException {

	/**
	 * Verifica el nombre: no puede estar vacío, tener espacios dobles ni caracteres
	 * distintos de letras y espacios, y debe tener una o dos palabras.
	 *
	 * @param nombre nombre a verificar.
	 * @throws NombreInvalidoException si el nombre no cumple las reglas.
	 */
	public static void verificarNombre(String nombre) {

		if (nombre == null || nombre.trim().isEmpty()) {
			throw new NombreInvalidoException("El nombre no puede estar vacío");
		}

		if (nombre.contains("  ")) {
			throw new NombreInvalidoException("El nombre no puede contener espacios dobles");
		}

		if (!nombre.matches("^[A-Za-zÁÉÍÓÚáéíóúÑñ ]+$")) {

			throw new NombreInvalidoException("El nombre solo debe contener letras y espacios");
		}

		String[] palabras = nombre.trim().split("\\s+");

		if (palabras.length < 1 || palabras.length > 2) {

			throw new NombreInvalidoException("El nombre debe tener mínimo una palabra y máximo dos");
		}
	}

	/**
	 * Verifica que el nombre de la asignatura cumpla con los criterios
	 * establecidos. El nombre no puede estar vacío, contener espacios dobles ni
	 * caracteres especiales. Se permiten letras, números y espacios, con un máximo
	 * de cinco palabras.
	 *
	 * @param nombre nombre de la asignatura que se desea validar.
	 * @throws NombreInvalidoException si el nombre es nulo, está vacío, contiene
	 *                                 espacios dobles, caracteres no permitidos o
	 *                                 supera las cinco palabras.
	 */
	public static void verificarNombreAsignatura(String nombre) {

		if (nombre == null || nombre.trim().isEmpty()) {
			throw new NombreInvalidoException("El nombre de la asignatura no puede estar vacío");
		}

		if (nombre.contains("  ")) {
			throw new NombreInvalidoException("El nombre de la asignatura no puede contener espacios dobles");
		}

		if (!nombre.matches("^[A-Za-zÁÉÍÓÚáéíóúÑñ0-9 ]+$")) {
			throw new NombreInvalidoException(
					"El nombre de la asignatura solo debe contener letras, números y espacios");
		}

		String[] palabras = nombre.trim().split("\\s+");

		if (palabras.length > 5) {
			throw new NombreInvalidoException("El nombre de la asignatura debe tener máximo cinco palabras");
		}
	}

	/**
	 * Verifica el apellido: no puede estar vacío, tener espacios dobles ni
	 * caracteres distintos de letras y espacios, y debe tener una o dos palabras.
	 *
	 * @param apellido apellido a verificar.
	 * @throws NombreInvalidoException si el apellido no cumple las reglas.
	 */
	public static void verificarApellido(String apellido) {

		if (apellido == null || apellido.trim().isEmpty()) {

			throw new NombreInvalidoException("El apellido no puede estar vacío");
		}

		if (apellido.contains("  ")) {

			throw new NombreInvalidoException("El apellido no puede contener espacios dobles");
		}

		if (!apellido.matches("^[A-Za-zÁÉÍÓÚáéíóúÑñ ]+$")) {

			throw new NombreInvalidoException("El apellido solo debe contener letras y espacios");
		}

		String[] palabras = apellido.trim().split("\\s+");

		if (palabras.length < 1 || palabras.length > 2) {

			throw new NombreInvalidoException("El apellido debe tener mínimo una palabra y máximo dos");
		}
	}

	/**
	 * Verifica que el correo no esté vacío y pertenezca al dominio
	 * 
	 * @unbosque.edu.co.
	 *
	 *                   @param correo correo electrónico a verificar.
	 * @return true si el correo es válido.
	 * @throws CorreoInvalidoException si el correo está vacío o no es válido.
	 */
	public static boolean verificarCorreoElectronico(String correo) {

		if (correo == null || correo.trim().isEmpty()) {

			throw new CorreoInvalidoException("El correo electrónico no puede estar vacío");
		}

		Pattern pattern = Pattern.compile("^[A-Za-z0-9._%+-]+@unbosque\\.edu\\.co$");

		Matcher matcher = pattern.matcher(correo);

		if (matcher.find()) {
			return true;
		}

		throw new CorreoInvalidoException(
				"El correo electrónico ingresado no es válido (asegurese que termina en @unbosque.edu.co)");
	}

	/**
	 * Verifica que el identificador no sea nulo y sea un número positivo.
	 *
	 * @param id identificador a verificar.
	 * @throws IdInvalidoException si el identificador es nulo o no es positivo.
	 */
	public static void verificarId(Long id) {

		if (id == null) {
			throw new IdInvalidoException("El ID no puede ser nulo");
		}

		if (id <= 0) {
			throw new IdInvalidoException("El ID debe ser un número positivo");
		}
	}

	/**
	 * Verifica la contraseña: no puede estar vacía ni tener espacios, debe tener al
	 * menos 8 caracteres y contener una mayúscula y un número.
	 *
	 * @param contrasena contraseña a verificar.
	 * @throws ContraseniaInvalidaException si la contraseña no cumple las reglas.
	 */
	public static void verificarContrasena(String contrasena) {

		if (contrasena == null || contrasena.isEmpty()) {

			throw new ContraseniaInvalidaException("La contraseña no puede estar vacía");
		}

		if (contrasena.contains(" ")) {

			throw new ContraseniaInvalidaException("La contraseña no debe contener espacios");
		}

		if (contrasena.length() < 8) {

			throw new ContraseniaInvalidaException("La contraseña debe tener al menos 8 caracteres");
		}

		if (!contrasena.matches("^(?=.*[A-Z])(?=.*[0-9]).+$")) {

			throw new ContraseniaInvalidaException("La contraseña debe contener al menos una mayúscula y un número");
		}
	}

	/**
	 * Verifica que el administrador no sea nulo.
	 *
	 * @param administrador administrador a verificar.
	 * @throws AdministradorInvalidoException si el administrador es nulo.
	 */
	public static void verificarAdministrador(AdministradorDTO administrador) {

		if (administrador == null) {

			throw new AdministradorInvalidoException("El administrador no puede ser nulo");
		}
	}

	/**
	 * Verifica que el documento sea positivo y tenga entre 6 y 10 dígitos.
	 *
	 * @param documento número de documento a verificar.
	 * @throws DocumentoInvalidoException si el documento no cumple las reglas.
	 */
	public static void verificarDocumento(long documento) {

		if (documento <= 0) {

			throw new DocumentoInvalidoException("El documento debe ser un número positivo");
		}

		String documentoTexto = String.valueOf(documento);

		if (documentoTexto.length() < 6 || documentoTexto.length() > 10) {

			throw new DocumentoInvalidoException("El documento debe tener entre 6 y 10 dígitos");
		}
	}

	/**
	 * Verifica el código de administrador: no puede estar vacío, tener espacios ni
	 * caracteres distintos de letras y números.
	 *
	 * @param codigoAdministrador código a verificar.
	 * @throws CodigoAdministradorInvalidoException si el código no cumple las
	 *                                              reglas.
	 */
	public static void verificarCodigoAdministrador(String codigoAdministrador) {

		if (codigoAdministrador == null || codigoAdministrador.trim().isEmpty()) {

			throw new CodigoAdministradorInvalidoException("El código de administrador no puede estar vacío");
		}

		if (codigoAdministrador.contains(" ")) {

			throw new CodigoAdministradorInvalidoException("El código de administrador no debe contener espacios");
		}

		if (!codigoAdministrador.matches("^[A-Za-z0-9]+$")) {

			throw new CodigoAdministradorInvalidoException(
					"El código de administrador solo debe contener letras y números");
		}
	}

	/**
	 * Verifica que el correo no esté ya registrado.
	 *
	 * @param duplicado true si el correo ya está registrado.
	 * @throws CorreoInvalidoException si el correo ya está registrado.
	 */
	public static void verificarCorreoDuplicado(boolean duplicado) {

		if (duplicado) {

			throw new CorreoInvalidoException("El correo ya se encuentra registrado");
		}
	}

	/**
	 * Verifica que el código de administrador no esté ya registrado.
	 *
	 * @param duplicado true si el código ya está registrado.
	 * @throws CodigoAdministradorInvalidoException si el código ya está registrado.
	 */
	public static void verificarCodigoAdministradorDuplicado(boolean duplicado) {

		if (duplicado) {

			throw new CodigoAdministradorInvalidoException("El código de administrador ya se encuentra registrado");
		}
	}

	/**
	 * Verifica que el documento no esté ya registrado.
	 *
	 * @param duplicado true si el documento ya está registrado.
	 * @throws DocumentoInvalidoException si el documento ya está registrado.
	 */
	public static void verificarDocumentoDuplicado(boolean duplicado) {

		if (duplicado) {

			throw new DocumentoInvalidoException("El documento ya se encuentra registrado");
		}
	}

	/**
	 * Verifica que la asignatura no sea nula.
	 *
	 * @param asignatura asignatura a verificar.
	 * @throws AsignaturaInvalidaException si la asignatura es nula.
	 */
	public static void verificarAsignatura(AsignaturaDTO asignatura) {

		if (asignatura == null) {

			throw new AsignaturaInvalidaException("La asignatura no puede ser nula");
		}
	}

	/**
	 * Verifica que el número de créditos esté entre 1 y 10.
	 *
	 * @param numCreditos número de créditos a verificar.
	 * @throws NumeroCreditosInvalidoException si el número de créditos está fuera
	 *                                         del rango.
	 */
	public static void verificarNumCreditos(int numCreditos) {

		if (numCreditos <= 0) {

			throw new NumeroCreditosInvalidoException("El número de créditos debe ser mayor que cero");
		}

		if (numCreditos > 10) {

			throw new NumeroCreditosInvalidoException("El número de créditos no puede ser mayor a 10");
		}
	}

	/**
	 * Verifica que la frecuencia semanal esté entre 1 y 4.
	 *
	 * @param frecuenciaSemanal frecuencia semanal a verificar.
	 * @throws FrecuenciaSemanalInvalidaException si la frecuencia está fuera del
	 *                                            rango.
	 */
	public static void verificarFrecuenciaSemanal(int frecuenciaSemanal) {

		if (frecuenciaSemanal <= 0) {

			throw new FrecuenciaSemanalInvalidaException("La frecuencia semanal debe ser mayor que cero");
		}

		if (frecuenciaSemanal > 4) {

			throw new FrecuenciaSemanalInvalidaException("La frecuencia semanal no puede ser mayor a 4");
		}
	}

	/**
	 * Verifica que el nombre de la asignatura no esté ya registrado.
	 *
	 * @param duplicado true si el nombre ya está registrado.
	 * @throws NombreInvalidoException si el nombre ya está registrado.
	 */
	public static void verificarNombreDuplicado(boolean duplicado) {

		if (duplicado) {

			throw new NombreInvalidoException("El nombre de la asignatura ya se encuentra registrado");
		}
	}

	/**
	 * Verifica que la franja horaria no sea nula.
	 *
	 * @param franjaHoraria franja horaria a verificar.
	 * @throws FranjaHorariaInvalidaException si la franja horaria es nula.
	 */
	public static void verificarFranjaHoraria(FranjaHorariaDTO franjaHoraria) {

		if (franjaHoraria == null) {

			throw new FranjaHorariaInvalidaException("La franja horaria no puede ser nula");
		}
	}

	/**
	 * Verifica que el día no sea nulo.
	 *
	 * @param dia día a verificar.
	 * @throws DiaInvalidoException si el día es nulo.
	 */
	public static void verificarDia(Dia dia) {

		if (dia == null) {

			throw new DiaInvalidoException("El día no puede ser nulo");
		}
	}

	/**
	 * Verifica que la hora de inicio no sea nula y esté entre las 7:00 a. m. y las
	 * 9:00 p. m.
	 *
	 * @param horaInicio hora de inicio a verificar.
	 * @throws HoraInicioInvalidaException si la hora es nula o está fuera del
	 *                                     rango.
	 */
	public static void verificarHoraInicio(LocalTime horaInicio) {

		if (horaInicio == null) {
			throw new HoraInicioInvalidaException("La hora de inicio no puede ser nula");
		}

		if (horaInicio.isBefore(LocalTime.of(7, 0))) {
			throw new HoraInicioInvalidaException("La hora de inicio no puede ser antes de las 7:00 a. m.");
		}

		if (horaInicio.isAfter(LocalTime.of(21, 0))) {
			throw new HoraInicioInvalidaException("La hora de inicio no puede ser después de las 9:00 p. m.");
		}
	}

	/**
	 * Verifica que la hora de fin no sea nula y esté entre las 8:00 a. m. y las
	 * 10:00 p. m.
	 *
	 * @param horaFin hora de fin a verificar.
	 * @throws HoraFinInvalidaException si la hora es nula o está fuera del rango.
	 */
	public static void verificarHoraFin(LocalTime horaFin) {

		if (horaFin == null) {
			throw new HoraFinInvalidaException("La hora de fin no puede ser nula");
		}

		if (horaFin.isBefore(LocalTime.of(8, 0))) {
			throw new HoraFinInvalidaException("La hora de fin no puede ser antes de las 8:00 a. m.");
		}

		if (horaFin.isAfter(LocalTime.of(22, 0))) {
			throw new HoraFinInvalidaException("La hora de fin no puede ser después de las 10:00 p. m.");
		}
	}

	/**
	 * Verifica que las horas de inicio y fin no sean nulas, sean válidas, no queden
	 * dentro del horario de almuerzo y que la de inicio sea anterior a la de fin.
	 *
	 * @param horaInicio hora de inicio.
	 * @param horaFin    hora de fin.
	 * @throws HorarioInvalidoException si las horas son nulas, están dentro del
	 *                                  horario de almuerzo o la de inicio no es
	 *                                  anterior a la de fin.
	 */
	public static void verificarHorario(LocalTime horaInicio, LocalTime horaFin) {

		if (horaInicio == null || horaFin == null) {
			throw new HorarioInvalidoException("Las horas de inicio y fin no pueden ser nulas");
		}

		verificarHoraInicio(horaInicio);
		verificarHoraFin(horaFin);

		if (horaInicio.isAfter(LocalTime.of(12, 0)) && horaInicio.isBefore(LocalTime.of(13, 0))
				|| horaFin.isAfter(LocalTime.of(12, 0)) && horaFin.isBefore(LocalTime.of(13, 0))) {

			throw new HorarioInvalidoException(
					"Las horas de inicio y fin no pueden estar entre las 12:00 p. m. y la 1:00 p. m.");
		}

		if (!horaInicio.isBefore(horaFin)) {
			throw new HorarioInvalidoException("La hora de inicio debe ser anterior a la hora de fin");
		}
	}

	/**
	 * Verifica que el grupo no sea nulo.
	 *
	 * @param grupo grupo a verificar.
	 * @throws GrupoInvalidoException si el grupo es nulo.
	 */
	public static void verificarGrupo(GrupoDTO grupo) {

		if (grupo == null) {

			throw new GrupoInvalidoException("El grupo no puede ser nulo");
		}
	}

	/**
	 * Verifica que el número del grupo sea mayor que cero y menor que 800.
	 * 
	 * @param numeroGrupo número del grupo a verificar.
	 * @throws NumeroGrupoInvalidoException si el número no es mayor que cero o es
	 *                                      igual o mayor a 800.
	 */
	public static void verificarNumeroGrupo(int numeroGrupo) {
		if (numeroGrupo <= 0 || numeroGrupo >= 800) {
			throw new NumeroGrupoInvalidoException("El número del grupo debe ser mayor que cero y menor que 800");
		}
	}

	/**
	 * Verifica que la capacidad mínima del grupo no sea menor que 10.
	 *
	 * @param capacidadMinima capacidad mínima a verificar.
	 * @throws CapacidadMinimaInvalidaException si la capacidad mínima es menor que
	 *                                          10.
	 */
	public static void verificarCapacidadMinima(int capacidadMinima) {

		if (capacidadMinima < 10) {
			throw new CapacidadMinimaInvalidaException("La capacidad mínima no puede ser menor que 10");
		}
	}

	/**
	 * Verifica que la capacidad máxima del grupo no sea mayor que 44.
	 *
	 * @param capacidadMaxima capacidad máxima a verificar.
	 * @throws CapacidadMaximaInvalidaException si la capacidad máxima es mayor que
	 *                                          44.
	 */
	public static void verificarCapacidadMaxima(int capacidadMaxima) {

		if (capacidadMaxima > 44) {
			throw new CapacidadMaximaInvalidaException("La capacidad máxima no puede ser mayor que 44");
		}
	}

	/**
	 * Verifica que la capacidad mínima no sea mayor que la máxima.
	 *
	 * @param capacidadMinima capacidad mínima del grupo.
	 * @param capacidadMaxima capacidad máxima del grupo.
	 * @throws CapacidadesInvalidasException si la mínima es mayor que la máxima.
	 */
	public static void verificarCapacidades(int capacidadMinima, int capacidadMaxima) {

		if (capacidadMinima > capacidadMaxima) {

			throw new CapacidadesInvalidasException("La capacidad mínima no puede ser mayor que la capacidad máxima");
		}
	}

	/**
	 * Verifica que el profesor (entidad) no sea nulo.
	 *
	 * @param profesor profesor a verificar.
	 * @throws ProfesorInvalidoException si el profesor es nulo.
	 */
	public static void verificarProfesor(Profesor profesor) {

		if (profesor == null) {

			throw new ProfesorInvalidoException("El profesor no puede ser nulo");
		}
	}

	/**
	 * Verifica que el número de sesiones esté entre 0 y 4.
	 *
	 * @param sesiones número de sesiones a verificar.
	 * @throws SesionesInvalidasException si el número de sesiones está fuera del
	 *                                    rango.
	 */
	public static void verificarSesiones(int sesiones) {

		if (sesiones < 0) {
			throw new SesionesInvalidasException("El número de sesiones no puede ser menor que 0");
		}

		if (sesiones > 4) {
			throw new SesionesInvalidasException("El número de sesiones no puede ser mayor que 4");
		}
	}

	/**
	 * Verifica que el estado del grupo no sea nulo.
	 *
	 * @param estado estado a verificar.
	 * @throws EstadoGrupoInvalidoException si el estado es nulo.
	 */
	public static void verificarEstadoGrupo(EstadoGrupo estado) {

		if (estado == null) {

			throw new EstadoGrupoInvalidoException("El estado del grupo no puede ser nulo");
		}
	}

	/**
	 * Verifica que el número de grupo no esté ya registrado.
	 *
	 * @param duplicado true si el número de grupo ya está registrado.
	 * @throws NumeroGrupoInvalidoException si el número de grupo ya está
	 *                                      registrado.
	 */
	public static void verificarNumeroGrupoDuplicado(boolean duplicado) {

		if (duplicado) {

			throw new NumeroGrupoInvalidoException("El número de grupo ya se encuentra registrado");
		}
	}

	/**
	 * Verifica que el profesor (DTO) no sea nulo.
	 *
	 * @param profesor profesor a verificar.
	 * @throws ProfesorInvalidoException si el profesor es nulo.
	 */
	public static void verificarProfesor(ProfesorDTO profesor) {
		if (profesor == null) {
			throw new ProfesorInvalidoException("El profesor no puede ser nulo");
		}
	}

	/**
	 * Verifica que el tipo de vinculación no sea nulo.
	 *
	 * @param tipoVinculacion tipo de vinculación a verificar.
	 * @throws TipoVinculacionInvalidaException si el tipo de vinculación es nulo.
	 */
	public static void verificarTipoVinculacion(TipoVinculacion tipoVinculacion) {
		if (tipoVinculacion == null) {
			throw new TipoVinculacionInvalidaException("El tipo de vinculación no puede ser nulo");
		}
	}

	/**
	 * Verifica que el escalafón no sea nulo.
	 *
	 * @param escalafon escalafón a verificar.
	 * @throws EscalafonInvalidoException si el escalafón es nulo.
	 */
	public static void verificarEscalafon(Escalafon escalafon) {
		if (escalafon == null) {
			throw new EscalafonInvalidoException("El escalafón no puede ser nulo");
		}
	}

	/**
	 * Verifica que las horas máximas semanales estén entre 1 y 40.
	 *
	 * @param horasMaximasSemanales horas máximas a verificar.
	 * @throws HorasMaximasSemanalesInvalidasException si las horas están fuera del
	 *                                                 rango.
	 */
	public static void verificarHorasMaximasSemanales(int horasMaximasSemanales) {

		if (horasMaximasSemanales <= 0) {
			throw new HorasMaximasSemanalesInvalidasException("Las horas máximas semanales deben ser mayores que cero");
		}

		if (horasMaximasSemanales > 40) {
			throw new HorasMaximasSemanalesInvalidasException("Las horas máximas semanales no pueden ser mayores a 40");
		}
	}

	/**
	 * Verifica que la lista de especialidades no sea nula ni esté vacía.
	 *
	 * @param especialidades especialidades a verificar.
	 * @throws EspecialidadesInvalidasException si la lista es nula o está vacía.
	 */
	public static void verificarEspecialidades(List<Especialidad> especialidades) {
		if (especialidades == null || especialidades.isEmpty()) {
			throw new EspecialidadesInvalidasException("Las especialidades no pueden ser nulas o estar vacías");
		}
	}

	/**
	 * Verifica que la disponibilidad horaria no sea nula ni esté vacía.
	 *
	 * @param disponibilidadHoraria franjas de disponibilidad a verificar.
	 * @throws DisponibilidadHorariaInvalidaException si la lista es nula o está
	 *                                                vacía.
	 */
	public static void verificarDisponibilidadHoraria(List<FranjaHoraria> disponibilidadHoraria) {
		if (disponibilidadHoraria == null || disponibilidadHoraria.isEmpty()) {
			throw new DisponibilidadHorariaInvalidaException(
					"La disponibilidad horaria no puede ser nula o estar vacía");
		}
	}

	/**
	 * Verifica que el salón no sea nulo.
	 *
	 * @param salon salón a verificar.
	 * @throws SalonInvalidoException si el salón es nulo.
	 */
	public static void verificarSalon(SalonDTO salon) {
		if (salon == null) {
			throw new SalonInvalidoException("El salón no puede ser nulo");
		}
	}

	/**
	 * Verifica que el bloque no sea nulo.
	 *
	 * @param bloque bloque a verificar.
	 * @throws BloqueInvalidoException si el bloque es nulo.
	 */
	public static void verificarBloque(Bloque bloque) {
		if (bloque == null) {
			throw new BloqueInvalidoException("El bloque no puede ser nulo");
		}
	}

	/**
	 * Verifica que el número del salón sea mayor que cero.
	 *
	 * @param numeroSalon número del salón a verificar.
	 * @throws NumeroSalonInvalidoException si el número no es mayor que cero.
	 */
	public static void verificarNumeroSalon(int numeroSalon) {
		if (numeroSalon <= 0) {
			throw new NumeroSalonInvalidoException("El número del salón debe ser mayor que cero");
		}
	}

	/**
	 * Verifica que la capacidad del salón esté entre 1 y 44.
	 *
	 * @param capacidad capacidad a verificar.
	 * @throws CapacidadInvalidaException si la capacidad está fuera del rango.
	 */
	public static void verificarCapacidad(int capacidad) {

		if (capacidad <= 0) {
			throw new CapacidadInvalidaException("La capacidad debe ser mayor que cero");
		}

		if (capacidad > 44) {
			throw new CapacidadInvalidaException("La capacidad no puede ser mayor a 44");
		}
	}

	/**
	 * Verifica que no exista ya un salón con el mismo bloque y número.
	 *
	 * @param duplicado true si el salón ya está registrado.
	 * @throws SalonDuplicadoException si el salón ya está registrado.
	 */
	public static void verificarSalonDuplicado(boolean duplicado) {
		if (duplicado) {
			throw new SalonDuplicadoException("El salón ya se encuentra registrado para ese bloque");
		}
	}

	/**
	 * Verifica que la franja esté dentro de la jornada institucional: de lunes a
	 * viernes entre las 7:00 a. m. y las 10:00 p. m., y el sábado entre las 7:00 a.
	 * m. y la 1:00 p. m.
	 *
	 * @param dia        día de la franja.
	 * @param horaInicio hora de inicio.
	 * @param horaFin    hora de fin.
	 * @throws RangoHorarioInvalidoException si la franja queda fuera de la jornada.
	 */
	public static void verificarRangoHorarioInstitucional(Dia dia, LocalTime horaInicio, LocalTime horaFin) {

		LocalTime inicioJornada = LocalTime.of(7, 0);
		LocalTime finJornadaSemana = LocalTime.of(22, 0);
		LocalTime finJornadaSabado = LocalTime.of(13, 0);

		LocalTime finPermitido = (dia == Dia.SABADO) ? finJornadaSabado : finJornadaSemana;

		if (horaInicio.isBefore(inicioJornada) || horaFin.isAfter(finPermitido)) {
			throw new RangoHorarioInvalidoException(
					dia == Dia.SABADO ? "El sábado solo se pueden asignar clases entre las 7:00 AM y la 1:00 PM"
							: "De lunes a viernes solo se pueden asignar clases entre las 7:00 AM y las 10:00 PM");
		}
	}

	/**
	 * Verifica que la franja no se cruce con el bloque de almuerzo (12:00 m. a 1:00
	 * p. m.).
	 *
	 * @param horaInicio hora de inicio.
	 * @param horaFin    hora de fin.
	 * @throws BloqueAlmuerzoInvalidoException si la franja se cruza con el
	 *                                         almuerzo.
	 */
	public static void verificarBloqueAlmuerzo(LocalTime horaInicio, LocalTime horaFin) {

		LocalTime inicioAlmuerzo = LocalTime.of(12, 0);
		LocalTime finAlmuerzo = LocalTime.of(13, 0);

		boolean seSolapa = horaInicio.isBefore(finAlmuerzo) && horaFin.isAfter(inicioAlmuerzo);

		if (seSolapa) {
			throw new BloqueAlmuerzoInvalidoException("No se pueden asignar clases entre las 12:00 M y la 1:00 PM");
		}
	}

	/**
	 * Verifica que la sesión dure exactamente 2 horas.
	 *
	 * @param horaInicio hora de inicio.
	 * @param horaFin    hora de fin.
	 * @throws DuracionSesionInvalidaException si la duración no es de 2 horas.
	 */
	public static void verificarDuracionSesion(LocalTime horaInicio, LocalTime horaFin) {

		long minutos = Duration.between(horaInicio, horaFin).toMinutes();

		if (minutos != 120) {
			throw new DuracionSesionInvalidaException("Cada sesión de clase debe durar exactamente 2 horas");
		}
	}

	/**
	 * Verifica que cada sesión del grupo quede cubierta por alguna franja de
	 * disponibilidad del profesor.
	 *
	 * @param disponibilidadProfesor franjas de disponibilidad del profesor.
	 * @param sesionesGrupo          sesiones del grupo.
	 * @throws ProfesorNoDisponibleException si alguna sesión no está cubierta por
	 *                                       la disponibilidad.
	 */
	public static void verificarDisponibilidadProfesorParaSesiones(List<FranjaHoraria> disponibilidadProfesor,
			List<FranjaHoraria> sesionesGrupo) {

		for (FranjaHoraria sesion : sesionesGrupo) {

			boolean cubierta = disponibilidadProfesor.stream()
					.anyMatch(franja -> franja.getDia() == sesion.getDia()
							&& !sesion.getHoraInicio().isBefore(franja.getHoraInicio())
							&& !sesion.getHoraFin().isAfter(franja.getHoraFin()));

			if (!cubierta) {
				throw new ProfesorNoDisponibleException(
						"El profesor no tiene disponibilidad horaria para la sesión del día " + sesion.getDia() + " de "
								+ sesion.getHoraInicio() + " a " + sesion.getHoraFin());
			}
		}
	}

	/**
	 * Verifica que las nuevas sesiones no se crucen con las de otros grupos del
	 * profesor.
	 *
	 * @param gruposDelProfesor grupos que ya tiene el profesor.
	 * @param nuevasSesiones    sesiones que se quieren asignar.
	 * @param idGrupoActual     identificador del grupo que se edita (se ignora en
	 *                          la comparación).
	 * @throws ConflictoHorarioProfesorException si hay cruce de horarios.
	 */
	public static void verificarSinConflictoHorarioProfesor(List<Grupo> gruposDelProfesor,
			List<FranjaHoraria> nuevasSesiones, long idGrupoActual) {

		for (Grupo grupo : gruposDelProfesor) {

			if (grupo.getId() == idGrupoActual)
				continue;

			for (FranjaHoraria existente : grupo.getSesiones()) {
				for (FranjaHoraria nueva : nuevasSesiones) {

					boolean mismoDia = existente.getDia() == nueva.getDia();
					boolean seSolapan = nueva.getHoraInicio().isBefore(existente.getHoraFin())
							&& nueva.getHoraFin().isAfter(existente.getHoraInicio());

					if (mismoDia && seSolapan) {
						throw new ConflictoHorarioProfesorException(
								"El profesor ya tiene una clase asignada el día " + existente.getDia() + " entre "
										+ existente.getHoraInicio() + " y " + existente.getHoraFin());
					}
				}
			}
		}
	}

	/**
	 * Verifica que las nuevas sesiones no se crucen con las de otros grupos que
	 * usan el mismo salón.
	 *
	 * @param gruposDelSalon grupos que ya usan el salón.
	 * @param nuevasSesiones sesiones que se quieren asignar.
	 * @param idGrupoActual  identificador del grupo que se edita (se ignora en la
	 *                       comparación).
	 * @throws ConflictoHorarioSalonException si hay cruce de horarios.
	 */
	public static void verificarSinConflictoHorarioSalon(List<Grupo> gruposDelSalon, List<FranjaHoraria> nuevasSesiones,
			long idGrupoActual) {

		for (Grupo grupo : gruposDelSalon) {

			if (grupo.getId() == idGrupoActual)
				continue;

			for (FranjaHoraria existente : grupo.getSesiones()) {
				for (FranjaHoraria nueva : nuevasSesiones) {

					boolean mismoDia = existente.getDia() == nueva.getDia();
					boolean seSolapan = nueva.getHoraInicio().isBefore(existente.getHoraFin())
							&& nueva.getHoraFin().isAfter(existente.getHoraInicio());

					if (mismoDia && seSolapan) {
						throw new ConflictoHorarioSalonException("El salón ya está ocupado el día " + existente.getDia()
								+ " entre " + existente.getHoraInicio() + " y " + existente.getHoraFin());
					}
				}
			}
		}
	}

	/**
	 * Verifica que la capacidad del salón sea suficiente para la capacidad máxima
	 * del grupo.
	 *
	 * @param capacidadSalon       capacidad del salón.
	 * @param capacidadMaximaGrupo capacidad máxima del grupo.
	 * @throws CapacidadSalonInsuficienteException si el salón es más pequeño que el
	 *                                             grupo.
	 */
	public static void verificarCapacidadSalon(int capacidadSalon, int capacidadMaximaGrupo) {

		if (capacidadSalon < capacidadMaximaGrupo) {
			throw new CapacidadSalonInsuficienteException("La capacidad del salón (" + capacidadSalon
					+ ") es menor a la capacidad máxima del grupo (" + capacidadMaximaGrupo + ")");
		}
	}

	/**
	 * Verifica que el salón cumpla los requisitos de la asignatura (computadores y
	 * sillas móviles).
	 *
	 * @param requiereComputadores  true si la asignatura requiere computadores.
	 * @param tieneComputadores     true si el salón tiene computadores.
	 * @param requiereSillasMoviles true si la asignatura requiere sillas móviles.
	 * @param tieneSillasMoviles    true si el salón tiene sillas móviles.
	 * @throws RequisitosSalonInvalidosException si el salón no cumple algún
	 *                                           requisito.
	 */
	public static void verificarRequisitosSalon(boolean requiereComputadores, boolean tieneComputadores,
			boolean requiereSillasMoviles, boolean tieneSillasMoviles) {

		if (requiereComputadores && !tieneComputadores) {
			throw new RequisitosSalonInvalidosException("La asignatura requiere un salón con computadores");
		}

		if (requiereSillasMoviles && !tieneSillasMoviles) {
			throw new RequisitosSalonInvalidosException("La asignatura requiere un salón con sillas móviles");
		}
	}

	/**
	 * Verifica que asignar el nuevo grupo no haga que el profesor supere sus horas
	 * máximas semanales.
	 *
	 * @param horasMaximasSemanales horas máximas semanales del profesor.
	 * @param horasYaAsignadas      horas que ya tiene asignadas.
	 * @param horasNuevoGrupo       horas del grupo que se quiere asignar.
	 * @throws CargaHorariaExcedidaException si se superan las horas máximas.
	 */
	public static void verificarCargaHorariaProfesor(int horasMaximasSemanales, int horasYaAsignadas,
			int horasNuevoGrupo) {

		if (horasYaAsignadas + horasNuevoGrupo > horasMaximasSemanales) {
			throw new CargaHorariaExcedidaException("El profesor superaría sus horas máximas semanales ("
					+ horasMaximasSemanales + "h) al asignarle este grupo");
		}
	}

	/**
	 * Verifica que el número de sesiones del grupo coincida con la frecuencia
	 * semanal de la asignatura.
	 *
	 * @param numeroSesiones    número de sesiones del grupo.
	 * @param frecuenciaSemanal frecuencia semanal de la asignatura.
	 * @throws SesionesFrecuenciaInvalidaException si no coinciden.
	 */
	public static void verificarSesionesFrecuencia(int numeroSesiones, int frecuenciaSemanal) {

		if (numeroSesiones != frecuenciaSemanal) {
			throw new SesionesFrecuenciaInvalidaException("El número de sesiones del grupo (" + numeroSesiones
					+ ") debe coincidir con la frecuencia semanal de la asignatura (" + frecuenciaSemanal + ")");
		}
	}

	/**
	 * Verifica que la capacidad máxima no supere el límite institucional: 40
	 * estudiantes más 10 % de tolerancia (44).
	 *
	 * @param capacidadMaxima capacidad máxima del grupo.
	 * @throws CapacidadMaximaExcedidaException si se supera el límite.
	 */
	public static void verificarCapacidadMaximaInstitucional(int capacidadMaxima) {

		final int CAPACIDAD_MAXIMA_INSTITUCIONAL = 40;
		final double TOLERANCIA = 0.10;
		int limite = (int) Math.floor(CAPACIDAD_MAXIMA_INSTITUCIONAL * (1 + TOLERANCIA)); // 44

		if (capacidadMaxima > limite) {
			throw new CapacidadMaximaExcedidaException(
					"La capacidad máxima no puede superar " + limite + " estudiantes (40 + 10% de tolerancia)");
		}
	}

	/**
	 * Indica si se exige que las sesiones de un grupo con dos o tres sesiones no
	 * sean en días consecutivos.
	 */
	private static final boolean EXIGIR_DIAS_NO_CONSECUTIVOS = true;

	/**
	 * Verifica que las horas máximas semanales no superen el tope de la dedicación
	 * (Estatuto Docente, artículo 8): tiempo completo 40, tres cuartos 30, medio
	 * tiempo 20 y un cuarto de tiempo 10.
	 *
	 * @param tipoVinculacion       tipo de vinculación del profesor.
	 * @param horasMaximasSemanales horas máximas semanales a verificar.
	 * @throws HorasMaximasSemanalesInvalidasException si las horas superan el tope.
	 */
	public static void verificarHorasSegunVinculacion(TipoVinculacion tipoVinculacion, int horasMaximasSemanales) {
		if (tipoVinculacion != null && horasMaximasSemanales > tipoVinculacion.getHorasMaximas()) {
			throw new HorasMaximasSemanalesInvalidasException("Con vinculación " + tipoVinculacion
					+ " las horas máximas semanales no pueden superar " + tipoVinculacion.getHorasMaximas());
		}
	}

	/**
	 * Verifica que cada sesión del grupo cumpla las reglas de horario: rango
	 * institucional, sin cruce con el almuerzo y duración de 2 horas.
	 *
	 * @param sesiones sesiones del grupo.
	 * @throws RangoHorarioInvalidoException si alguna sesión incumple las reglas.
	 */
	public static void verificarSesionesGrupo(List<FranjaHoraria> sesiones) {
		for (FranjaHoraria sesion : sesiones) {
			verificarDia(sesion.getDia());
			verificarHorario(sesion.getHoraInicio(), sesion.getHoraFin());
			verificarRangoHorarioInstitucional(sesion.getDia(), sesion.getHoraInicio(), sesion.getHoraFin());
			verificarBloqueAlmuerzo(sesion.getHoraInicio(), sesion.getHoraFin());
			verificarDuracionSesion(sesion.getHoraInicio(), sesion.getHoraFin());
		}
	}

	/**
	 * Verifica que las sesiones de un grupo sean en días distintos y, cuando el
	 * grupo tiene dos o tres sesiones, que no sean en días consecutivos.
	 *
	 * @param sesiones sesiones del grupo.
	 * @throws SesionesInvalidasException si dos sesiones coinciden en el día o son
	 *                                    en días consecutivos.
	 */
	public static void verificarDiasSesiones(List<FranjaHoraria> sesiones) {
		List<Integer> dias = new ArrayList<>();
		for (FranjaHoraria sesion : sesiones) {
			dias.add(sesion.getDia().ordinal());
		}
		dias.sort(null);
		for (int i = 1; i < dias.size(); i++) {
			if (dias.get(i).equals(dias.get(i - 1))) {
				throw new SesionesInvalidasException("Las sesiones de un mismo grupo deben ser en días distintos");
			}
			if (EXIGIR_DIAS_NO_CONSECUTIVOS && sesiones.size() <= 3 && dias.get(i) - dias.get(i - 1) == 1) {
				throw new SesionesInvalidasException(
						"Las sesiones de un grupo con dos o tres sesiones no pueden ser en días consecutivos");
			}
		}
	}

	/**
	 * Verifica que los inscritos no sean negativos ni superen los cupos del grupo.
	 *
	 * @param inscritos       estudiantes inscritos.
	 * @param capacidadMaxima cupos del grupo.
	 * @throws CapacidadMaximaExcedidaException si los inscritos están fuera del
	 *                                          rango.
	 */
	public static void verificarCuposInscritos(int inscritos, int capacidadMaxima) {
		if (inscritos < 0 || inscritos > capacidadMaxima) {
			throw new CapacidadMaximaExcedidaException("Los inscritos (" + inscritos
					+ ") deben estar entre 0 y los cupos del grupo (" + capacidadMaxima + ")");
		}
	}

	/**
	 * Verifica que el profesor tenga la especialidad que exige la asignatura. Si la
	 * asignatura no exige ninguna, no se valida.
	 *
	 * @param especialidadRequerida  especialidad que exige la asignatura (puede ser
	 *                               null).
	 * @param especialidadesProfesor especialidades del profesor.
	 * @throws ProfesorNoCalificadoException si el profesor no tiene la
	 *                                       especialidad.
	 */
	public static void verificarProfesorCalificado(Especialidad especialidadRequerida,
			List<Especialidad> especialidadesProfesor) {
		if (especialidadRequerida != null
				&& (especialidadesProfesor == null || !especialidadesProfesor.contains(especialidadRequerida))) {
			throw new ProfesorNoCalificadoException(
					"El profesor no tiene la especialidad requerida por la asignatura: " + especialidadRequerida);
		}
	}

	/**
	 * Verifica que ninguna franja de la lista se cruce con otra del mismo día.
	 *
	 * @param franjas franjas a verificar.
	 * @throws HorarioInvalidoException si dos franjas del mismo día se cruzan.
	 */
	public static void verificarSinSolapamientoFranjas(List<FranjaHoraria> franjas) {
		for (int i = 0; i < franjas.size(); i++) {
			for (int j = i + 1; j < franjas.size(); j++) {
				FranjaHoraria a = franjas.get(i);
				FranjaHoraria b = franjas.get(j);
				if (a.getDia() == b.getDia() && a.getHoraInicio().isBefore(b.getHoraFin())
						&& b.getHoraInicio().isBefore(a.getHoraFin())) {
					throw new HorarioInvalidoException("Las franjas del día " + a.getDia() + " se cruzan entre sí");
				}
			}
		}
	}

	/**
	 * Verifica que el periodo academico tenga el formato AAAA-N.
	 *
	 * El año debe contener exactamente cuatro digitos y el periodo debe ser 1 o 2.
	 *
	 * @param periodo periodo academico que se desea validar.
	 * @throws PeriodoInvalidoException si el periodo no cumple con el formato
	 *                                  establecido.
	 */
	public static void verificarPeriodo(String periodo) {

		if (periodo == null || !periodo.matches("(Intersemestral )?\\d{4}-[12]")) {
			throw new PeriodoInvalidoException(
					"El periodo debe tener el formato AAAA-N o Intersemestral AAAA-N. Ejemplo: 2026-1 o Intersemestral 2026-2.");
		}
	}

	/**
	 * Verifica que la cantidad de inscritos se encuentre entre 0 y 44, ambos
	 * valores incluidos.
	 *
	 * @param inscritos cantidad de inscritos que se desea validar.
	 * @throws InscritosInvalidosException si el valor es menor a 0 o mayor a 44.
	 */
	public static void verificarInscritos(int inscritos) {

		if (inscritos < 0 || inscritos > 44) {
			throw new InscritosInvalidosException("La cantidad de inscritos debe estar entre 0 y 44.");
		}
	}

}