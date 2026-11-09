package co.edu.unbosque.horarios.service;

import java.time.Duration;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import co.edu.unbosque.horarios.dto.AsignaturaDTO;
import co.edu.unbosque.horarios.dto.AuditoriaDTO;
import co.edu.unbosque.horarios.dto.FranjaHorariaDTO;
import co.edu.unbosque.horarios.dto.GrupoDTO;
import co.edu.unbosque.horarios.dto.ParametrosSistemaDTO;
import co.edu.unbosque.horarios.dto.ProfesorDTO;
import co.edu.unbosque.horarios.dto.SalonDTO;
import co.edu.unbosque.horarios.entity.Asignatura;
import co.edu.unbosque.horarios.entity.FranjaHoraria;
import co.edu.unbosque.horarios.entity.Grupo;
import co.edu.unbosque.horarios.entity.Profesor;
import co.edu.unbosque.horarios.entity.Salon;
import co.edu.unbosque.horarios.exception.AsignaturaInvalidaException;
import co.edu.unbosque.horarios.exception.ProfesorInvalidoException;
import co.edu.unbosque.horarios.exception.SalonInvalidoException;
import co.edu.unbosque.horarios.repository.AsignaturaRepository;
import co.edu.unbosque.horarios.repository.FranjaHorariaRepository;
import co.edu.unbosque.horarios.repository.GrupoRepository;
import co.edu.unbosque.horarios.repository.ProfesorRepository;
import co.edu.unbosque.horarios.repository.SalonRepository;
import co.edu.unbosque.horarios.util.LanzadorDeException;
import co.edu.unbosque.horarios.util.enums.EstadoGrupo;
import co.edu.unbosque.horarios.util.enums.Servicio;
import co.edu.unbosque.horarios.util.enums.TipoAccion;

/**
 * Servicio encargado de la gestión de grupos del sistema de horarios.
 * <p>
 * Implementa operaciones CRUD, búsquedas por diferentes atributos y
 * validaciones de negocio mediante LanzadorDeException. La asignatura, el
 * profesor y el salón se leen de la base de datos a partir de su identificador,
 * de modo que las validaciones usan los datos registrados y no los que envíe el
 * cliente.
 */
@Service
public class GrupoService implements CRUDOperation<GrupoDTO> {

	/**
	 * Repositorio utilizado para acceder a los datos de los grupos.
	 */
	@Autowired
	private GrupoRepository grupoRep;

	/**
	 * Repositorio utilizado para acceder a los datos de los profesores.
	 */
	@Autowired
	private ProfesorRepository profesorRep;

	/**
	 * Repositorio utilizado para acceder a los datos de las asignaturas.
	 */
	@Autowired
	private AsignaturaRepository asignaturaRep;

	/**
	 * Repositorio utilizado para acceder a los datos de los salones.
	 */
	@Autowired
	private SalonRepository salonRep;

	/**
	 * Repositorio utilizado para acceder a los datos de las franjas horarias.
	 */
	@Autowired
	private FranjaHorariaRepository franjaRep;

	/**
	 * Servicio utilizado para registrar la auditoría de las acciones.
	 */
	@Autowired
	private AuditoriaService auditoriaService;

	/**
	 * Mapeador utilizado para convertir entre entidades y DTO.
	 */
	@Autowired
	private ModelMapper mapper;
	
	/**
	 * Servicio utilizado para consultar los parámetros del sistema.
	 */
	@Autowired
	private ParametrosSistemaService parametrosSistemaService;

	/**
	 * Constructor vacío de la clase.
	 */
	public GrupoService() {
	}

	/**
	 * Datos de un grupo ya cargados de la base de datos y validados.
	 */
	private static class DatosGrupo {
		/**
		 * Asignatura cargada de la base de datos.
		 */
		Asignatura asignatura;
		/**
		 * Profesor cargado de la base de datos.
		 */
		Profesor profesor;
		/**
		 * Salón cargado de la base de datos.
		 */
		Salon salon;
		/**
		 * Sesiones del grupo convertidas a entidades.
		 */
		List<FranjaHoraria> sesiones;
	}

	/**
	 * Cuenta el total de grupos registrados.
	 *
	 * @return número total de grupos.
	 */
	@Override
	public long count() {
		return grupoRep.count();
	}

	/**
	 * Verifica si existe un grupo por ID.
	 *
	 * @param id identificador del grupo.
	 * @return true si existe, false si no.
	 */
	@Override
	public boolean exist(Long id) {
		LanzadorDeException.verificarId(id);
		return grupoRep.existsById(id);
	}

	/**
	 * Crea un nuevo grupo. Carga y valida la asignatura, el profesor, el salón y
	 * las sesiones, y verifica que no exista otro grupo con el mismo número,
	 * asignatura y periodo.
	 *
	 * @param data información del grupo (asignatura, profesor y salón por id, y sus
	 *             sesiones con día y horas).
	 * @return 0 si se crea correctamente.
	 */
	@Override
	@Transactional
	public int create(GrupoDTO data) {
		LanzadorDeException.verificarGrupo(data);
		LanzadorDeException.verificarNumeroGrupo(data.getNumeroGrupo());

		// 1. Cargamos y validamos la asignatura y dependencias primero
		DatosGrupo datos = validar(data, 0);

		// 2. Validamos duplicidad pero ahora DEPENDIENDO DE LA ASIGNATURA
		LanzadorDeException.verificarNumeroGrupoDuplicado(grupoRep.existsByNumeroGrupoAndAsignaturaAndPeriodo(
				data.getNumeroGrupo(), datos.asignatura, data.getPeriodo()));

		Grupo entity = new Grupo(datos.asignatura, data.getNumeroGrupo(), data.getCapacidadMinima(),
				data.getCapacidadMaxima(), datos.profesor, datos.salon, guardarSesiones(datos.sesiones),
				data.getEstado());
		entity.setPeriodo(data.getPeriodo());
		entity.setInscritos(data.getInscritos());
		
		grupoRep.save(entity);
		AuditoriaDTO auditoria = auditoriaService.preAccion(TipoAccion.CREATE, Servicio.GRUPO);
    	auditoriaService.create(auditoria);
		return 0;
	}

	/**
	 * Crea un nuevo grupo a partir de los identificadores del profesor, la
	 * asignatura, el salón y las franjas horarias. Construye un GrupoDTO y lo
	 * delega en el método de creación principal.
	 *
	 * @param profesorId      identificador del profesor.
	 * @param asignaturaId    identificador de la asignatura.
	 * @param numeroGrupo     número del grupo.
	 * @param capacidadMinima capacidad mínima del grupo.
	 * @param capacidadMaxima capacidad máxima del grupo.
	 * @param salonId         identificador del salón.
	 * @param estado          estado del grupo.
	 * @param periodo         periodo académico del grupo.
	 * @param inscritos       cantidad de estudiantes inscritos.
	 * @param franjaIds       identificadores de las franjas horarias (opcional).
	 * @return 0 si se crea correctamente, -1 si no existe el profesor, -2 si no
	 *         existe la asignatura, -3 si no existe el salón o -5 si alguna franja
	 *         horaria no existe.
	 */
	@Transactional
	public int create(long profesorId, long asignaturaId, int numeroGrupo, int capacidadMinima, int capacidadMaxima,
			long salonId, EstadoGrupo estado, String periodo, int inscritos, List<Long> franjaIds) {

		LanzadorDeException.verificarId(profesorId);
		LanzadorDeException.verificarId(asignaturaId);
		LanzadorDeException.verificarId(salonId);
		LanzadorDeException.verificarNumeroGrupo(numeroGrupo);
		LanzadorDeException.verificarCapacidadMinima(capacidadMinima, parametrosSistemaService.get());
		LanzadorDeException.verificarCapacidadMaxima(capacidadMaxima, parametrosSistemaService.get());
		LanzadorDeException.verificarEstadoGrupo(estado);
		LanzadorDeException.verificarPeriodo(periodo);
		LanzadorDeException.verificarInscritos(inscritos);

		if (franjaIds != null) {
			for (Long idFranja : franjaIds) {
				LanzadorDeException.verificarId(idFranja);
			}
		}

		Optional<Profesor> profesor = profesorRep.findById(profesorId);
		if (profesor.isEmpty()) {
			return -1;
		}

		Optional<Asignatura> asignatura = asignaturaRep.findById(asignaturaId);
		if (asignatura.isEmpty()) {
			return -2;
		}

		Optional<Salon> salon = salonRep.findById(salonId);
		if (salon.isEmpty()) {
			return -3;
		}

		List<FranjaHorariaDTO> sesiones = new ArrayList<>();
		if (franjaIds != null && !franjaIds.isEmpty()) {
			List<FranjaHoraria> franjas = new ArrayList<>();
			franjaRep.findAllById(franjaIds).forEach(franjas::add);

			// Si alguna franja no existe, se rechaza en lugar de ignorarla en silencio
			if (franjas.size() != new HashSet<>(franjaIds).size()) {
				return -5;
			}
			franjas.forEach(f -> sesiones.add(mapper.map(f, FranjaHorariaDTO.class)));
		}

		GrupoDTO dto = new GrupoDTO();
		dto.setProfesor(mapper.map(profesor.get(), ProfesorDTO.class));
		dto.setAsignatura(mapper.map(asignatura.get(), AsignaturaDTO.class));
		dto.setSalon(mapper.map(salon.get(), SalonDTO.class));
		dto.setSesiones(sesiones);
		dto.setNumeroGrupo(numeroGrupo);
		dto.setCapacidadMinima(capacidadMinima);
		dto.setCapacidadMaxima(capacidadMaxima);
		dto.setEstado(estado);
		dto.setPeriodo(periodo);
		dto.setInscritos(inscritos);

		return create(dto);
	}

	/**
	 * Obtiene todos los grupos registrados.
	 *
	 * @return lista de grupos en formato DTO.
	 */
	@Override
	public List<GrupoDTO> getAll() {
		Iterable<Grupo> entityList = grupoRep.findAll();
		List<GrupoDTO> dtoList = new ArrayList<>();
		entityList.forEach(entity -> dtoList.add(mapper.map(entity, GrupoDTO.class)));
		AuditoriaDTO auditoria = auditoriaService.preAccion(TipoAccion.READ, Servicio.GRUPO);
    	auditoriaService.create(auditoria);
		return dtoList;
	}

	/**
	 * Elimina un grupo por ID.
	 *
	 * @param id identificador del grupo.
	 * @return 0 si se elimina, 1 si no existe.
	 */
	@Override
	public int deleteById(Long id) {
		LanzadorDeException.verificarId(id);
		if (grupoRep.existsById(id)) {
			grupoRep.deleteById(id);
			AuditoriaDTO auditoria = auditoriaService.preAccion(TipoAccion.DELETE, Servicio.GRUPO);
	    	auditoriaService.create(auditoria);
			return 0;
		}
		return 1;
	}

	/**
	 * Actualiza un grupo por ID. Solo verifica duplicados si cambian el número, la
	 * asignatura o el periodo del grupo.
	 *
	 * @param id   identificador del grupo.
	 * @param data nuevos datos del grupo.
	 * @return 0 si se actualiza correctamente, 1 si el grupo no existe.
	 */
	@Override
	@Transactional
	public int updateById(Long id, GrupoDTO data) {
		LanzadorDeException.verificarId(id);
		LanzadorDeException.verificarGrupo(data);

		Optional<Grupo> encontrado = grupoRep.findById(id);

		if (encontrado.isEmpty()) {
			return 1;
		}

		LanzadorDeException.verificarNumeroGrupo(data.getNumeroGrupo());

		DatosGrupo datos = validar(data, id);
		Grupo temp = encontrado.get();

		/*
		 * Se verifica duplicidad solo si el número del grupo cambia O si cambiaron la
		 * asignatura a la que pertenece.
		 */
		if (temp.getNumeroGrupo() != data.getNumeroGrupo() || temp.getAsignatura().getId() != datos.asignatura.getId()
				|| !Objects.equals(temp.getPeriodo(), data.getPeriodo())) {
			LanzadorDeException.verificarNumeroGrupoDuplicado(grupoRep.existsByNumeroGrupoAndAsignaturaAndPeriodo(
					data.getNumeroGrupo(), datos.asignatura, data.getPeriodo()));
		}

		temp.setAsignatura(datos.asignatura);
		temp.setNumeroGrupo(data.getNumeroGrupo());
		temp.setCapacidadMinima(data.getCapacidadMinima());
		temp.setCapacidadMaxima(data.getCapacidadMaxima());
		temp.setProfesor(datos.profesor);
		temp.setSalon(datos.salon);
		temp.setSesiones(guardarSesiones(datos.sesiones));
		temp.setEstado(data.getEstado());
		temp.setPeriodo(data.getPeriodo());
		temp.setInscritos(data.getInscritos());

		grupoRep.save(temp);
		AuditoriaDTO auditoria = auditoriaService.preAccion(TipoAccion.UPDATE, Servicio.GRUPO);
    	auditoriaService.create(auditoria);
		return 0;
	}

	/**
	 * Busca los grupos que tienen un número determinado, sin importar la
	 * asignatura.
	 *
	 * @param numeroGrupo número del grupo.
	 * @return lista de grupos encontrados.
	 */
	public List<GrupoDTO> findByNumeroGrupo(int numeroGrupo) {
		LanzadorDeException.verificarNumeroGrupo(numeroGrupo);

		List<Grupo> encontrado = grupoRep.findByNumeroGrupo(numeroGrupo);

		List<GrupoDTO> dtoList = new ArrayList<>();

		encontrado.forEach(grupo -> dtoList.add(mapper.map(grupo, GrupoDTO.class)));
		AuditoriaDTO auditoria = auditoriaService.preAccion(TipoAccion.READ, Servicio.GRUPO);
    	auditoriaService.create(auditoria);
		return dtoList;
	}

	/**
	 * Busca los grupos de una asignatura.
	 *
	 * @param asignaturaDTO asignatura de la cual se buscan los grupos (se usa su id).
	 * @return lista de grupos encontrados.
	 */
	public List<GrupoDTO> findByAsignatura(AsignaturaDTO asignaturaDTO) {

		Asignatura asignatura = mapper.map(asignaturaDTO, Asignatura.class);
		List<Grupo> encontrados = grupoRep.findByAsignatura(asignatura);
		List<GrupoDTO> dtoList = new ArrayList<>();

		encontrados.forEach(grupo -> dtoList.add(mapper.map(grupo, GrupoDTO.class)));
		AuditoriaDTO auditoria = auditoriaService.preAccion(TipoAccion.READ, Servicio.GRUPO);
    	auditoriaService.create(auditoria);
		return dtoList;
	}

	/**
	 * Busca los grupos asignados a un profesor.
	 *
	 * @param profesorDTO profesor del cual se buscan los grupos.
	 * @return lista de grupos encontrados.
	 */
	public List<GrupoDTO> findByProfesor(ProfesorDTO profesorDTO) {
		Profesor profesor = mapper.map(profesorDTO, Profesor.class);
		LanzadorDeException.verificarProfesor(profesor);

		List<Grupo> encontrados = grupoRep.findByProfesor(profesor);
		List<GrupoDTO> dtoList = new ArrayList<>();
		encontrados.forEach(grupo -> dtoList.add(mapper.map(grupo, GrupoDTO.class)));
		AuditoriaDTO auditoria = auditoriaService.preAccion(TipoAccion.READ, Servicio.GRUPO);
    	auditoriaService.create(auditoria);
		return dtoList;
	}

	/**
	 * Busca los grupos por su estado.
	 *
	 * @param estado estado del grupo.
	 * @return lista de grupos encontrados.
	 */
	public List<GrupoDTO> findByEstado(EstadoGrupo estado) {
		LanzadorDeException.verificarEstadoGrupo(estado);

		List<Grupo> encontrados = grupoRep.findByEstado(estado);
		List<GrupoDTO> dtoList = new ArrayList<>();
		encontrados.forEach(grupo -> dtoList.add(mapper.map(grupo, GrupoDTO.class)));
		AuditoriaDTO auditoria = auditoriaService.preAccion(TipoAccion.READ, Servicio.GRUPO);
    	auditoriaService.create(auditoria);
		return dtoList;
	}

	/**
	 * Verifica si ya existe algún grupo con el número indicado.
	 *
	 * @param numeroGrupo número del grupo.
	 * @return true si ya existe, false si no.
	 */
	public boolean findNumeroGrupoAlreadyTaken(int numeroGrupo) {
		LanzadorDeException.verificarNumeroGrupo(numeroGrupo);
		return grupoRep.existsByNumeroGrupo(numeroGrupo);
	}

	/**
	 * Elimina los grupos que tienen un número determinado.
	 *
	 * @param numeroGrupo número del grupo.
	 * @return 0 si se elimina, 1 si no existe.
	 */
	public int deleteByNumeroGrupo(int numeroGrupo) {
		LanzadorDeException.verificarNumeroGrupo(numeroGrupo);
		if (grupoRep.existsByNumeroGrupo(numeroGrupo)) {
			grupoRep.deleteByNumeroGrupo(numeroGrupo);
			AuditoriaDTO auditoria = auditoriaService.preAccion(TipoAccion.DELETE, Servicio.GRUPO);
	    	auditoriaService.create(auditoria);
			return 0;
		}
		return 1;
	}

	/**
	 * Carga de la base de datos el profesor, el salón, la asignatura y las sesiones
	 * del grupo, y aplica todas las validaciones de negocio: capacidades,
	 * requisitos del salón, frecuencia semanal, especialidad y disponibilidad del
	 * profesor, conflictos de horario del profesor y del salón dentro del mismo
	 * periodo, y carga horaria máxima del profesor.
	 *
	 * @param data     datos del grupo a validar.
	 * @param idActual identificador del grupo que se está actualizando, o 0 si es
	 *                 un grupo nuevo (se excluye de los conflictos de horario).
	 * @return datos del grupo ya cargados y validados.
	 */
	private DatosGrupo validar(GrupoDTO data, long idActual) {

		ParametrosSistemaDTO parametros = parametrosSistemaService.get();
		
		DatosGrupo datos = new DatosGrupo();
		datos.profesor = cargarProfesor(data.getProfesor());
		datos.salon = cargarSalon(data.getSalon());
		datos.asignatura = cargarAsignatura(data.getAsignatura());
		datos.sesiones = aEntidades(data.getSesiones());

		Profesor profesor = datos.profesor;
		Salon salon = datos.salon;
		Asignatura asignatura = datos.asignatura;
		List<FranjaHoraria> sesiones = datos.sesiones;

		LanzadorDeException.verificarPeriodo(data.getPeriodo());
		LanzadorDeException.verificarCapacidadMinima(data.getCapacidadMinima(), parametros);
		LanzadorDeException.verificarCapacidadMaxima(data.getCapacidadMaxima(), parametros);
		LanzadorDeException.verificarCapacidades(data.getCapacidadMinima(), data.getCapacidadMaxima());
		LanzadorDeException.verificarProfesor(profesor);
		LanzadorDeException.verificarEstadoGrupo(data.getEstado());
		LanzadorDeException.verificarAsignatura(data.getAsignatura());
		LanzadorDeException.verificarSalon(data.getSalon());
		LanzadorDeException.verificarSesiones(sesiones.size(), parametros);
		LanzadorDeException.verificarCapacidadMaximaInstitucional(data.getCapacidadMaxima(), parametros);
		LanzadorDeException.verificarCuposInscritos(data.getInscritos(), data.getCapacidadMaxima());
		LanzadorDeException.verificarCapacidadSalon(salon.getCapacidad(), data.getCapacidadMaxima());
		LanzadorDeException.verificarRequisitosSalon(asignatura.isRequiereComputadores(), salon.isTieneComputadores(),
				asignatura.isRequiereSillasMoviles(), salon.isTieneSillasMoviles());
		LanzadorDeException.verificarSesionesFrecuencia(sesiones.size(), asignatura.getFrecuenciaSemanal());
		LanzadorDeException.verificarSesionesGrupo(sesiones, parametros);
		LanzadorDeException.verificarDiasSesiones(sesiones, parametros);
		LanzadorDeException.verificarProfesorCalificado(asignatura.getEspecialidadRequerida(),
				profesor.getEspecialidades());
		LanzadorDeException.verificarDisponibilidadProfesorParaSesiones(profesor.getDisponibilidadHoraria(), sesiones);

		// Solo cuentan los grupos del MISMO periodo: un grupo de 2026-1 no choca con
		// uno de 2027-1
		List<Grupo> gruposProfesor = grupoRep.findByProfesor(profesor).stream()
				.filter(g -> Objects.equals(g.getPeriodo(), data.getPeriodo())).collect(Collectors.toList());
		List<Grupo> gruposSalon = grupoRep.findBySalon(salon).stream()
				.filter(g -> Objects.equals(g.getPeriodo(), data.getPeriodo())).collect(Collectors.toList());

		LanzadorDeException.verificarSinConflictoHorarioProfesor(gruposProfesor, sesiones, idActual);
		LanzadorDeException.verificarSinConflictoHorarioSalon(gruposSalon, sesiones, idActual);

		// Horas reales según la duración de cada sesión (2 o 4 h), no un valor fijo
		int horasYaAsignadas = gruposProfesor.stream().filter(g -> g.getId() != idActual)
				.mapToInt(g -> horasDe(g.getSesiones())).sum();
		LanzadorDeException.verificarCargaHorariaProfesor(profesor.getHorasMaximasSemanales(), horasYaAsignadas,
				horasDe(sesiones));

		return datos;
	}

	/**
	 * Suma las horas reales de una lista de sesiones.
	 *
	 * @param sesiones sesiones cuya duración se suma.
	 * @return total de horas de las sesiones.
	 */
	private int horasDe(List<FranjaHoraria> sesiones) {
		long minutos = 0;
		for (FranjaHoraria s : sesiones) {
			minutos += Duration.between(s.getHoraInicio(), s.getHoraFin()).toMinutes();
		}
		return (int) (minutos / 60);
	}

	/**
	 * Guarda en la base de datos una copia de cada sesión recibida.
	 *
	 * @param sesiones sesiones a guardar.
	 * @return lista de franjas horarias guardadas.
	 */
	private List<FranjaHoraria> guardarSesiones(List<FranjaHoraria> sesiones) {
		List<FranjaHoraria> guardadas = new ArrayList<>();
		for (FranjaHoraria sesion : sesiones) {
			guardadas.add(
					franjaRep.save(new FranjaHoraria(sesion.getDia(), sesion.getHoraInicio(), sesion.getHoraFin())));
		}
		return guardadas;
	}

	/**
	 * Carga de la base de datos el profesor indicado.
	 *
	 * @param dto profesor con su identificador.
	 * @return profesor encontrado, o null si no se indicó ninguno.
	 * @throws ProfesorInvalidoException si el profesor no existe.
	 */
	private Profesor cargarProfesor(ProfesorDTO dto) {
		if (dto == null)
			return null;
		return profesorRep.findById(dto.getId())
				.orElseThrow(() -> new ProfesorInvalidoException("El profesor indicado no existe"));
	}

	/**
	 * Carga de la base de datos el salón indicado.
	 *
	 * @param dto salón con su identificador.
	 * @return salón encontrado, o null si no se indicó ninguno.
	 * @throws SalonInvalidoException si el salón no existe.
	 */
	private Salon cargarSalon(SalonDTO dto) {
		if (dto == null)
			return null;
		return salonRep.findById(dto.getId())
				.orElseThrow(() -> new SalonInvalidoException("El salón indicado no existe"));
	}

	/**
	 * Carga de la base de datos la asignatura indicada.
	 *
	 * @param dto asignatura con su identificador.
	 * @return asignatura encontrada, o null si no se indicó ninguna.
	 * @throws AsignaturaInvalidaException si la asignatura no existe.
	 */
	private Asignatura cargarAsignatura(AsignaturaDTO dto) {
		if (dto == null)
			return null;
		return asignaturaRep.findById(dto.getId())
				.orElseThrow(() -> new AsignaturaInvalidaException("La asignatura indicada no existe"));
	}

	/**
	 * Convierte una lista de franjas horarias DTO en entidades.
	 *
	 * @param dtos franjas horarias en formato DTO.
	 * @return lista de franjas horarias como entidades.
	 */
	private List<FranjaHoraria> aEntidades(List<FranjaHorariaDTO> dtos) {
		List<FranjaHoraria> resultado = new ArrayList<>();
		if (dtos != null) {
			dtos.forEach(d -> resultado.add(mapper.map(d, FranjaHoraria.class)));
		}
		return resultado;
	}

	/**
	 * Obtiene el repositorio de grupos.
	 *
	 * @return repositorio de grupos.
	 */
	public GrupoRepository getGrupoRep() {
		return grupoRep;
	}

	/**
	 * Establece el repositorio de grupos.
	 *
	 * @param grupoRep repositorio de grupos.
	 */
	public void setGrupoRep(GrupoRepository grupoRep) {
		this.grupoRep = grupoRep;
	}

	/**
	 * Obtiene el repositorio de profesores.
	 *
	 * @return repositorio de profesores.
	 */
	public ProfesorRepository getProfesorRep() {
		return profesorRep;
	}

	/**
	 * Establece el repositorio de profesores.
	 *
	 * @param profesorRep repositorio de profesores.
	 */
	public void setProfesorRep(ProfesorRepository profesorRep) {
		this.profesorRep = profesorRep;
	}

	/**
	 * Obtiene el repositorio de asignaturas.
	 *
	 * @return repositorio de asignaturas.
	 */
	public AsignaturaRepository getAsignaturaRep() {
		return asignaturaRep;
	}

	/**
	 * Establece el repositorio de asignaturas.
	 *
	 * @param asignaturaRep repositorio de asignaturas.
	 */
	public void setAsignaturaRep(AsignaturaRepository asignaturaRep) {
		this.asignaturaRep = asignaturaRep;
	}

	/**
	 * Obtiene el repositorio de salones.
	 *
	 * @return repositorio de salones.
	 */
	public SalonRepository getSalonRep() {
		return salonRep;
	}

	/**
	 * Establece el repositorio de salones.
	 *
	 * @param salonRep repositorio de salones.
	 */
	public void setSalonRep(SalonRepository salonRep) {
		this.salonRep = salonRep;
	}

	/**
	 * Obtiene el repositorio de franjas horarias.
	 *
	 * @return repositorio de franjas horarias.
	 */
	public FranjaHorariaRepository getFranjaRep() {
		return franjaRep;
	}

	/**
	 * Establece el repositorio de franjas horarias.
	 *
	 * @param franjaRep repositorio de franjas horarias.
	 */
	public void setFranjaRep(FranjaHorariaRepository franjaRep) {
		this.franjaRep = franjaRep;
	}

	/**
	 * Obtiene el mapeador utilizado para convertir entre entidades y DTO.
	 *
	 * @return mapeador de objetos.
	 */
	public ModelMapper getMapper() {
		return mapper;
	}

	/**
	 * Establece el mapeador utilizado para convertir entre entidades y DTO.
	 *
	 * @param mapper mapeador de objetos.
	 */
	public void setMapper(ModelMapper mapper) {
		this.mapper = mapper;
	}
}