package co.edu.unbosque.horarios.service;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import co.edu.unbosque.horarios.dto.FranjaHorariaDTO;
import co.edu.unbosque.horarios.dto.ProfesorDTO;
import co.edu.unbosque.horarios.entity.FranjaHoraria;
import co.edu.unbosque.horarios.entity.Profesor;
import co.edu.unbosque.horarios.exception.EspecialidadesInvalidasException;
import co.edu.unbosque.horarios.repository.FranjaHorariaRepository;
import co.edu.unbosque.horarios.repository.ProfesorRepository;
import co.edu.unbosque.horarios.util.LanzadorDeException;
import co.edu.unbosque.horarios.util.enums.Dia;
import co.edu.unbosque.horarios.util.enums.Escalafon;
import co.edu.unbosque.horarios.util.enums.Especialidad;
import co.edu.unbosque.horarios.util.enums.TipoVinculacion;
import jakarta.transaction.Transactional;

/**
 * Servicio encargado de la gestión de profesores del sistema de horarios.
 * <p>
 * Implementa operaciones CRUD, búsquedas por diferentes atributos, manejo de la
 * disponibilidad horaria, encriptación de contraseñas y validaciones de negocio
 * mediante LanzadorDeException.
 */
@Service
public class ProfesorService implements CRUDOperation<ProfesorDTO> {

	@Autowired
	private ProfesorRepository profesorRep;

	@Autowired
	private FranjaHorariaRepository franjaRep;

	@Autowired
	private ModelMapper mapper;

	@Autowired
	private PasswordEncoder passwordEncoder;

	public ProfesorService() {
		// Constructor vacío
	}

	/**
	 * Cuenta el total de profesores registrados.
	 *
	 * @return número total de profesores.
	 */
	@Override
	public long count() {
		return profesorRep.count();
	}

	/**
	 * Verifica si existe un profesor por ID.
	 *
	 * @param id identificador del profesor.
	 * @return true si existe, false si no.
	 */
	@Override
	public boolean exist(Long id) {
		LanzadorDeException.verificarId(id);
		return profesorRep.existsById(id);
	}

	/**
	 * Crea un nuevo profesor. La contraseña se guarda encriptada.
	 *
	 * @param data información del profesor.
	 * @return 0 si se crea correctamente.
	 */
	@Override
	public int create(ProfesorDTO data) {

		LanzadorDeException.verificarProfesor(data);
		LanzadorDeException.verificarNombre(data.getNombre());
		LanzadorDeException.verificarApellido(data.getApellido());
		LanzadorDeException.verificarDocumento(data.getDocumento());
		LanzadorDeException.verificarCorreoElectronico(data.getCorreo());
		LanzadorDeException.verificarContrasena(data.getContrasenia());
		LanzadorDeException.verificarTipoVinculacion(data.getTipoVinculacion());
		LanzadorDeException.verificarEscalafon(data.getEscalafon());
		LanzadorDeException.verificarHorasMaximasSemanales(data.getHorasMaximasSemanales());
		LanzadorDeException.verificarEspecialidades(data.getEspecialidades());

		LanzadorDeException.verificarDocumentoDuplicado(profesorRep.existsByDocumento(data.getDocumento()));

		LanzadorDeException.verificarCorreoDuplicado(profesorRep.existsByCorreo(data.getCorreo()));

		Profesor profesor = mapper.map(data, Profesor.class);

		profesor.setContrasenia(passwordEncoder.encode(data.getContrasenia()));

		profesorRep.save(profesor);

		return 0;
	}

	/**
	 * Obtiene todos los profesores registrados.
	 *
	 * @return lista de profesores en formato DTO.
	 */
	@Override
	public List<ProfesorDTO> getAll() {

		List<ProfesorDTO> lista = new ArrayList<>();

		Iterable<Profesor> profesores = profesorRep.findAll();

		for (Profesor profesor : profesores) {
			lista.add(mapper.map(profesor, ProfesorDTO.class));
		}

		return lista;
	}

	/**
	 * Elimina un profesor por ID.
	 *
	 * @param id identificador del profesor.
	 * @return 0 si se elimina, 1 si no existe.
	 */
	@Override
	public int deleteById(Long id) {

		LanzadorDeException.verificarId(id);

		if (!profesorRep.existsById(id)) {
			return 1;
		}

		profesorRep.deleteById(id);

		return 0;
	}

	/**
	 * Actualiza la información de un profesor. La contraseña se guarda encriptada.
	 * Si no se envía disponibilidad horaria, se conserva la que el profesor ya
	 * tiene registrada.
	 *
	 * @param id   identificador del profesor.
	 * @param data nuevos datos del profesor.
	 * @return 0 si se actualiza correctamente, 1 si el profesor no existe.
	 */
	@Override
	public int updateById(Long id, ProfesorDTO data) {

		LanzadorDeException.verificarId(id);
		LanzadorDeException.verificarProfesor(data);

		Optional<Profesor> encontrado = profesorRep.findById(id);

		if (encontrado.isEmpty()) {
			return 1;
		}

		LanzadorDeException.verificarNombre(data.getNombre());
		LanzadorDeException.verificarApellido(data.getApellido());
		LanzadorDeException.verificarDocumento(data.getDocumento());
		LanzadorDeException.verificarCorreoElectronico(data.getCorreo());
		LanzadorDeException.verificarContrasena(data.getContrasenia());
		LanzadorDeException.verificarTipoVinculacion(data.getTipoVinculacion());
		LanzadorDeException.verificarEscalafon(data.getEscalafon());
		LanzadorDeException.verificarHorasMaximasSemanales(data.getHorasMaximasSemanales());
		LanzadorDeException.verificarEspecialidades(data.getEspecialidades());

		List<FranjaHoraria> disponibilidad = new ArrayList<>();
		if (data.getDisponibilidadHoraria() != null) {
			for (FranjaHorariaDTO franjaDTO : data.getDisponibilidadHoraria()) {
				disponibilidad.add(mapper.map(franjaDTO, FranjaHoraria.class));
			}
		}

		Profesor profesorActual = encontrado.get();

		if (disponibilidad.isEmpty()) {
			if (profesorActual.getDisponibilidadHoraria() != null) {
				disponibilidad = new ArrayList<>(profesorActual.getDisponibilidadHoraria());
			}
		} else {
			LanzadorDeException.verificarDisponibilidadHoraria(disponibilidad);
		}

		if (profesorActual.getDocumento() != data.getDocumento()) {
			LanzadorDeException.verificarDocumentoDuplicado(profesorRep.existsByDocumento(data.getDocumento()));
		}

		if (!profesorActual.getCorreo().equals(data.getCorreo())) {
			LanzadorDeException.verificarCorreoDuplicado(profesorRep.existsByCorreo(data.getCorreo()));
		}

		Profesor profesor = mapper.map(data, Profesor.class);

		profesor.setId(id);
		profesor.setContrasenia(passwordEncoder.encode(data.getContrasenia()));
		profesor.setDisponibilidadHoraria(disponibilidad);

		profesorRep.save(profesor);

		return 0;
	}

	/**
	 * Busca un profesor por documento.
	 *
	 * @param documento número de documento.
	 * @return profesor encontrado o null si no existe.
	 */
	public ProfesorDTO findByDocumento(long documento) {

		LanzadorDeException.verificarDocumento(documento);

		Optional<Profesor> encontrado = profesorRep.findByDocumento(documento);

		if (encontrado.isEmpty()) {
			return null;
		}

		return mapper.map(encontrado.get(), ProfesorDTO.class);
	}

	/**
	 * Busca un profesor por correo electrónico.
	 *
	 * @param correo correo electrónico del profesor.
	 * @return profesor encontrado o null si no existe.
	 */
	public ProfesorDTO findByCorreo(String correo) {

		LanzadorDeException.verificarCorreoElectronico(correo);

		Optional<Profesor> encontrado = profesorRep.findByCorreo(correo);

		if (encontrado.isEmpty()) {
			return null;
		}

		return mapper.map(encontrado.get(), ProfesorDTO.class);
	}

	/**
	 * Busca profesores por tipo de vinculación.
	 *
	 * @param tipoVinculacion tipo de vinculación laboral.
	 * @return lista de profesores encontrados.
	 */
	public List<ProfesorDTO> findByTipoVinculacion(TipoVinculacion tipoVinculacion) {

		LanzadorDeException.verificarTipoVinculacion(tipoVinculacion);

		List<ProfesorDTO> lista = new ArrayList<>();

		List<Profesor> profesores = profesorRep.findByTipoVinculacion(tipoVinculacion);

		for (Profesor profesor : profesores) {
			lista.add(mapper.map(profesor, ProfesorDTO.class));
		}

		return lista;
	}

	/**
	 * Busca profesores por escalafón.
	 *
	 * @param escalafon escalafón académico.
	 * @return lista de profesores encontrados.
	 */
	public List<ProfesorDTO> findByEscalafon(Escalafon escalafon) {

		LanzadorDeException.verificarEscalafon(escalafon);

		List<ProfesorDTO> lista = new ArrayList<>();

		List<Profesor> profesores = profesorRep.findByEscalafon(escalafon);

		for (Profesor profesor : profesores) {
			lista.add(mapper.map(profesor, ProfesorDTO.class));
		}

		return lista;
	}

	/**
	 * Busca los profesores que tienen una especialidad determinada.
	 *
	 * @param especialidad especialidad a buscar.
	 * @return lista de profesores con esa especialidad.
	 */
	public List<ProfesorDTO> findByEspecialidad(Especialidad especialidad) {

		if (especialidad == null) {
			throw new EspecialidadesInvalidasException("La especialidad no puede ser nula");
		}

		List<ProfesorDTO> lista = new ArrayList<>();

		for (Profesor profesor : profesorRep.findAll()) {
			if (profesor.getEspecialidades() != null && profesor.getEspecialidades().contains(especialidad)) {
				lista.add(mapper.map(profesor, ProfesorDTO.class));
			}
		}

		return lista;
	}

	/**
	 * Busca los profesores disponibles en una franja horaria. Un profesor se
	 * considera disponible si alguna de sus franjas registradas, del mismo día,
	 * cubre por completo la franja buscada.
	 *
	 * @param franjaHorariaDTO franja con día, hora de inicio y hora de fin.
	 * @return lista de profesores disponibles en esa franja.
	 */
	public List<ProfesorDTO> findByDisponibilidad(FranjaHorariaDTO franjaHorariaDTO) {

		LanzadorDeException.verificarFranjaHoraria(franjaHorariaDTO);
		LanzadorDeException.verificarDia(franjaHorariaDTO.getDia());
		LanzadorDeException.verificarHorario(franjaHorariaDTO.getHoraInicio(), franjaHorariaDTO.getHoraFin());

		List<ProfesorDTO> lista = new ArrayList<>();

		for (Profesor profesor : profesorRep.findAll()) {

			if (profesor.getDisponibilidadHoraria() == null) {
				continue;
			}

			boolean disponible = profesor.getDisponibilidadHoraria().stream()
					.anyMatch(f -> f.getDia() == franjaHorariaDTO.getDia()
							&& !franjaHorariaDTO.getHoraInicio().isBefore(f.getHoraInicio())
							&& !franjaHorariaDTO.getHoraFin().isAfter(f.getHoraFin()));

			if (disponible) {
				lista.add(mapper.map(profesor, ProfesorDTO.class));
			}
		}

		return lista;
	}

	/**
	 * Verifica si un documento ya está registrado.
	 *
	 * @param documento documento a verificar.
	 * @return true si ya existe, false si no.
	 */
	public boolean findDocumentoAlreadyTaken(long documento) {
		return profesorRep.existsByDocumento(documento);
	}

	/**
	 * Verifica si un correo ya está registrado.
	 *
	 * @param correo correo a verificar.
	 * @return true si ya existe, false si no.
	 */
	public boolean findCorreoAlreadyTaken(String correo) {
		return profesorRep.existsByCorreo(correo);
	}

	/**
	 * Elimina un profesor mediante su documento. Si no existe, no hace nada.
	 *
	 * @param documento número de documento del profesor.
	 */
	public void deleteByDocumento(long documento) {

		LanzadorDeException.verificarDocumento(documento);

		if (!profesorRep.existsByDocumento(documento)) {
			return;
		}

		profesorRep.deleteByDocumento(documento);
	}

	/**
	 * Agrega una nueva franja de disponibilidad horaria a un profesor.
	 *
	 * @param id         identificador del profesor.
	 * @param dia        día de la semana de la franja.
	 * @param horaInicio hora de inicio de la franja.
	 * @param horaFin    hora de fin de la franja.
	 * @return true si se agrega, false si el profesor no existe.
	 */
	public boolean asignarDisponibilidadHoraria(long id, Dia dia, LocalTime horaInicio, LocalTime horaFin) {

		LanzadorDeException.verificarId(id);
		LanzadorDeException.verificarDia(dia);
		LanzadorDeException.verificarHoraInicio(horaInicio);
		LanzadorDeException.verificarHoraFin(horaFin);

		if (!horaFin.isAfter(horaInicio)) {
			throw new IllegalArgumentException("La hora de fin debe ser posterior a la hora de inicio");
		}

		Optional<Profesor> encontrado = profesorRep.findById(id);
		if (encontrado.isEmpty()) {
			return false;
		}

		Profesor profesor = encontrado.get();

		FranjaHoraria franja = franjaRep.save(new FranjaHoraria(dia, horaInicio, horaFin));

		if (profesor.getDisponibilidadHoraria() == null) {
			profesor.setDisponibilidadHoraria(new ArrayList<>());
		}
		profesor.getDisponibilidadHoraria().add(franja);

		profesorRep.save(profesor);
		return true;
	}

	/**
	 * Actualiza una franja de disponibilidad horaria de un profesor.
	 *
	 * @param idProfesor identificador del profesor.
	 * @param franjaDTO  franja con el id de la franja a modificar y sus nuevos
	 *                   datos.
	 * @return true si se actualiza, false si el profesor o la franja no existen.
	 */
	@Transactional
	public boolean updateDisponibilidadHoraria(long idProfesor, FranjaHorariaDTO franjaDTO) {

		LanzadorDeException.verificarFranjaHoraria(franjaDTO);

		Optional<Profesor> encontrado = profesorRep.findById(idProfesor);

		if (encontrado.isEmpty()) {
			return false;
		}

		Profesor profesor = encontrado.get();

		FranjaHoraria franja = profesor.getDisponibilidadHoraria().stream().filter(f -> f.getId() == franjaDTO.getId())
				.findFirst().orElse(null);

		if (franja == null) {
			return false;
		}

		LanzadorDeException.verificarDia(franjaDTO.getDia());
		LanzadorDeException.verificarHoraInicio(franjaDTO.getHoraInicio());
		LanzadorDeException.verificarHoraFin(franjaDTO.getHoraFin());
		LanzadorDeException.verificarHorario(franjaDTO.getHoraInicio(), franjaDTO.getHoraFin());

		franja.setDia(franjaDTO.getDia());
		franja.setHoraInicio(franjaDTO.getHoraInicio());
		franja.setHoraFin(franjaDTO.getHoraFin());

		return true;
	}

	public ProfesorRepository getProfesorRep() {
		return profesorRep;
	}

	public void setProfesorRep(ProfesorRepository profesorRep) {
		this.profesorRep = profesorRep;
	}

	public ModelMapper getMapper() {
		return mapper;
	}

	public void setMapper(ModelMapper mapper) {
		this.mapper = mapper;
	}

	public PasswordEncoder getPasswordEncoder() {
		return passwordEncoder;
	}

	public void setPasswordEncoder(PasswordEncoder passwordEncoder) {
		this.passwordEncoder = passwordEncoder;
	}
}