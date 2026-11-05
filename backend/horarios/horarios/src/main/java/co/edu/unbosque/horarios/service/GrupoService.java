package co.edu.unbosque.horarios.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

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

	@Autowired
	private GrupoRepository grupoRep;

	@Autowired
	private ProfesorRepository profesorRep;

	@Autowired
	private AsignaturaRepository asignaturaRep;

	@Autowired
	private SalonRepository salonRep;

	@Autowired
	private FranjaHorariaRepository franjaRep;

	@Autowired
	private AuditoriaService auditoriaService;
	
	@Autowired
	private ModelMapper mapper;
	
	@Autowired
	private ParametrosSistemaService parametrosSistemaService;

	public GrupoService() {
	}

	/**
	 * Datos de un grupo ya cargados de la base de datos y validados.
	 */
	private static class DatosGrupo {
		Asignatura asignatura;
		Profesor profesor;
		Salon salon;
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
	 * Crea un nuevo grupo. Valida cupos, salón, profesor, sesiones y horarios, y
	 * guarda las sesiones del grupo como franjas horarias nuevas.
	 *
	 * @param data información del grupo (asignatura, profesor y salón se
	 *             identifican por su id).
	 * @return 0 si se crea correctamente.
	 */
	@Override
	@Transactional
	public int create(GrupoDTO data) {

		LanzadorDeException.verificarGrupo(data);
		LanzadorDeException.verificarNumeroGrupo(data.getNumeroGrupo());
		LanzadorDeException.verificarNumeroGrupoDuplicado(grupoRep.existsByNumeroGrupo(data.getNumeroGrupo()));

		DatosGrupo datos = validar(data, 0);

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
	 * Actualiza un grupo por ID. Aplica las mismas validaciones que la creación y
	 * reemplaza las sesiones del grupo.
	 *
	 * @param id   identificador del grupo.
	 * @param data nuevos datos del grupo.
	 * @return 0 si se actualiza correctamente, 1 si no existe.
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
		 * Solo se verifica duplicado si el número del grupo realmente está cambiando.
		 */
		if (temp.getNumeroGrupo() != data.getNumeroGrupo()) {
			LanzadorDeException.verificarNumeroGrupoDuplicado(grupoRep.existsByNumeroGrupo(data.getNumeroGrupo()));
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
	 * Busca un grupo por número.
	 *
	 * @param numeroGrupo número del grupo.
	 * @return lista con el grupo encontrado.
	 */
	public List<GrupoDTO> findByNumeroGrupo(int numeroGrupo) {

		LanzadorDeException.verificarNumeroGrupo(numeroGrupo);

		Optional<Grupo> encontrado = grupoRep.findByNumeroGrupo(numeroGrupo);

		List<GrupoDTO> dtoList = new ArrayList<>();

		encontrado.ifPresent(grupo -> dtoList.add(mapper.map(grupo, GrupoDTO.class)));
		AuditoriaDTO auditoria = auditoriaService.preAccion(TipoAccion.READ, Servicio.GRUPO);
    	auditoriaService.create(auditoria);
		return dtoList;
	}

	/**
	 * Busca grupos por asignatura.
	 *
	 * @param asignaturaDTO asignatura asociada (se usa su id).
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
	 * Busca grupos por profesor.
	 *
	 * @param profesorDTO profesor asociado (se usa su id).
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
	 * Busca grupos por estado.
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
	 * Verifica si un número de grupo ya está registrado.
	 *
	 * @param numeroGrupo número del grupo.
	 * @return true si ya existe, false si no.
	 */
	public boolean findNumeroGrupoAlreadyTaken(int numeroGrupo) {

		LanzadorDeException.verificarNumeroGrupo(numeroGrupo);

		return grupoRep.existsByNumeroGrupo(numeroGrupo);
	}

	/**
	 * Elimina un grupo mediante su número.
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
	 * Carga los datos reales del grupo y aplica todas las reglas de negocio:
	 * capacidades, cupos, salón (capacidad y características), frecuencia y horario
	 * de las sesiones, días de las sesiones, especialidad del profesor,
	 * disponibilidad, cruces de horario y carga horaria del profesor.
	 *
	 * @param data     datos recibidos del grupo.
	 * @param idActual identificador del grupo que se edita (0 si es nuevo).
	 * @return asignatura, profesor, salón y sesiones validados.
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
		LanzadorDeException.verificarDiasSesiones(sesiones);
		LanzadorDeException.verificarProfesorCalificado(asignatura.getEspecialidadRequerida(),
				profesor.getEspecialidades());
		LanzadorDeException.verificarDisponibilidadProfesorParaSesiones(profesor.getDisponibilidadHoraria(), sesiones);

		List<Grupo> gruposProfesor = grupoRep.findByProfesor(profesor);

		LanzadorDeException.verificarSinConflictoHorarioProfesor(gruposProfesor, sesiones, idActual);
		LanzadorDeException.verificarSinConflictoHorarioSalon(grupoRep.findBySalon(salon), sesiones, idActual);

		int horasYaAsignadas = gruposProfesor.stream().filter(g -> g.getId() != idActual)
				.mapToInt(g -> g.getSesiones().size() * 2).sum();
		LanzadorDeException.verificarCargaHorariaProfesor(profesor.getHorasMaximasSemanales(), horasYaAsignadas,
				sesiones.size() * 2);

		return datos;
	}

	/**
	 * Guarda las sesiones del grupo como franjas horarias nuevas.
	 *
	 * @param sesiones sesiones validadas (sin guardar).
	 * @return franjas horarias ya guardadas.
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
	 * Carga el profesor registrado a partir del DTO recibido.
	 *
	 * @param dto profesor recibido (se usa su id).
	 * @return profesor registrado o null si el DTO es null.
	 */
	private Profesor cargarProfesor(ProfesorDTO dto) {
		if (dto == null) {
			return null;
		}
		return profesorRep.findById(dto.getId())
				.orElseThrow(() -> new ProfesorInvalidoException("El profesor indicado no existe"));
	}

	/**
	 * Carga el salón registrado a partir del DTO recibido.
	 *
	 * @param dto salón recibido (se usa su id).
	 * @return salón registrado o null si el DTO es null.
	 */
	private Salon cargarSalon(SalonDTO dto) {
		if (dto == null) {
			return null;
		}
		return salonRep.findById(dto.getId())
				.orElseThrow(() -> new SalonInvalidoException("El salón indicado no existe"));
	}

	/**
	 * Carga la asignatura registrada a partir del DTO recibido.
	 *
	 * @param dto asignatura recibida (se usa su id).
	 * @return asignatura registrada o null si el DTO es null.
	 */
	private Asignatura cargarAsignatura(AsignaturaDTO dto) {
		if (dto == null) {
			return null;
		}
		return asignaturaRep.findById(dto.getId())
				.orElseThrow(() -> new AsignaturaInvalidaException("La asignatura indicada no existe"));
	}

	/**
	 * Convierte una lista de franjas horarias DTO a entidades.
	 *
	 * @param dtos lista de franjas en formato DTO.
	 * @return lista de franjas en formato entidad (vacía si dtos es null).
	 */
	private List<FranjaHoraria> aEntidades(List<FranjaHorariaDTO> dtos) {
		List<FranjaHoraria> resultado = new ArrayList<>();
		if (dtos != null) {
			dtos.forEach(d -> resultado.add(mapper.map(d, FranjaHoraria.class)));
		}
		return resultado;
	}

	public GrupoRepository getGrupoRep() {
		return grupoRep;
	}

	public void setGrupoRep(GrupoRepository grupoRep) {
		this.grupoRep = grupoRep;
	}

	public ProfesorRepository getProfesorRep() {
		return profesorRep;
	}

	public void setProfesorRep(ProfesorRepository profesorRep) {
		this.profesorRep = profesorRep;
	}

	public AsignaturaRepository getAsignaturaRep() {
		return asignaturaRep;
	}

	public void setAsignaturaRep(AsignaturaRepository asignaturaRep) {
		this.asignaturaRep = asignaturaRep;
	}

	public SalonRepository getSalonRep() {
		return salonRep;
	}

	public void setSalonRep(SalonRepository salonRep) {
		this.salonRep = salonRep;
	}

	public FranjaHorariaRepository getFranjaRep() {
		return franjaRep;
	}

	public void setFranjaRep(FranjaHorariaRepository franjaRep) {
		this.franjaRep = franjaRep;
	}

	public ModelMapper getMapper() {
		return mapper;
	}

	public void setMapper(ModelMapper mapper) {
		this.mapper = mapper;
	}
}