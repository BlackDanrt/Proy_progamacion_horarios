package co.edu.unbosque.horarios.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import co.edu.unbosque.horarios.dto.AsignaturaDTO;
import co.edu.unbosque.horarios.dto.FranjaHorariaDTO;
import co.edu.unbosque.horarios.dto.GrupoDTO;
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
	private ModelMapper mapper;

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

	@Override
	public long count() {
		return grupoRep.count();
	}

	@Override
	public boolean exist(Long id) {
		LanzadorDeException.verificarId(id);
		return grupoRep.existsById(id);
	}

	@Override
	@Transactional
	public int create(GrupoDTO data) {
		LanzadorDeException.verificarGrupo(data);
		LanzadorDeException.verificarNumeroGrupo(data.getNumeroGrupo());

		// 1. Cargamos y validamos la asignatura y dependencias primero
		DatosGrupo datos = validar(data, 0);

		// 2. Validamos duplicidad pero ahora DEPENDIENDO DE LA ASIGNATURA
		LanzadorDeException.verificarNumeroGrupoDuplicado(
				grupoRep.existsByNumeroGrupoAndAsignatura(data.getNumeroGrupo(), datos.asignatura));

		Grupo entity = new Grupo(datos.asignatura, data.getNumeroGrupo(), data.getCapacidadMinima(),
				data.getCapacidadMaxima(), datos.profesor, datos.salon, guardarSesiones(datos.sesiones),
				data.getEstado());
		entity.setPeriodo(data.getPeriodo());
		entity.setInscritos(data.getInscritos());

		grupoRep.save(entity);

		return 0;
	}

	@Override
	public List<GrupoDTO> getAll() {
		Iterable<Grupo> entityList = grupoRep.findAll();
		List<GrupoDTO> dtoList = new ArrayList<>();
		entityList.forEach(entity -> dtoList.add(mapper.map(entity, GrupoDTO.class)));
		return dtoList;
	}

	@Override
	public int deleteById(Long id) {
		LanzadorDeException.verificarId(id);
		if (grupoRep.existsById(id)) {
			grupoRep.deleteById(id);
			return 0;
		}
		return 1;
	}

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
		if (temp.getNumeroGrupo() != data.getNumeroGrupo()
				|| temp.getAsignatura().getId() != datos.asignatura.getId()) {
			LanzadorDeException.verificarNumeroGrupoDuplicado(
					grupoRep.existsByNumeroGrupoAndAsignatura(data.getNumeroGrupo(), datos.asignatura));
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

		return 0;
	}

	public List<GrupoDTO> findByNumeroGrupo(int numeroGrupo) {
		LanzadorDeException.verificarNumeroGrupo(numeroGrupo);

		// Ahora devuelve una lista, porque puede haber un "Grupo 1" en varias materias
		List<Grupo> encontrados = grupoRep.findByNumeroGrupo(numeroGrupo);
		List<GrupoDTO> dtoList = new ArrayList<>();

		encontrados.forEach(grupo -> dtoList.add(mapper.map(grupo, GrupoDTO.class)));

		return dtoList;
	}

	public List<GrupoDTO> findByAsignatura(AsignaturaDTO asignaturaDTO) {
		Asignatura asignatura = mapper.map(asignaturaDTO, Asignatura.class);
		List<Grupo> encontrados = grupoRep.findByAsignatura(asignatura);
		List<GrupoDTO> dtoList = new ArrayList<>();
		encontrados.forEach(grupo -> dtoList.add(mapper.map(grupo, GrupoDTO.class)));
		return dtoList;
	}

	public List<GrupoDTO> findByProfesor(ProfesorDTO profesorDTO) {
		Profesor profesor = mapper.map(profesorDTO, Profesor.class);
		LanzadorDeException.verificarProfesor(profesor);

		List<Grupo> encontrados = grupoRep.findByProfesor(profesor);
		List<GrupoDTO> dtoList = new ArrayList<>();
		encontrados.forEach(grupo -> dtoList.add(mapper.map(grupo, GrupoDTO.class)));
		return dtoList;
	}

	public List<GrupoDTO> findByEstado(EstadoGrupo estado) {
		LanzadorDeException.verificarEstadoGrupo(estado);

		List<Grupo> encontrados = grupoRep.findByEstado(estado);
		List<GrupoDTO> dtoList = new ArrayList<>();
		encontrados.forEach(grupo -> dtoList.add(mapper.map(grupo, GrupoDTO.class)));
		return dtoList;
	}

	public boolean findNumeroGrupoAlreadyTaken(int numeroGrupo) {
		LanzadorDeException.verificarNumeroGrupo(numeroGrupo);
		return grupoRep.existsByNumeroGrupo(numeroGrupo);
	}

	public int deleteByNumeroGrupo(int numeroGrupo) {
		LanzadorDeException.verificarNumeroGrupo(numeroGrupo);
		if (grupoRep.existsByNumeroGrupo(numeroGrupo)) {
			grupoRep.deleteByNumeroGrupo(numeroGrupo);
			return 0;
		}
		return 1;
	}

	private DatosGrupo validar(GrupoDTO data, long idActual) {
		DatosGrupo datos = new DatosGrupo();
		datos.profesor = cargarProfesor(data.getProfesor());
		datos.salon = cargarSalon(data.getSalon());
		datos.asignatura = cargarAsignatura(data.getAsignatura());
		datos.sesiones = aEntidades(data.getSesiones());

		Profesor profesor = datos.profesor;
		Salon salon = datos.salon;
		Asignatura asignatura = datos.asignatura;
		List<FranjaHoraria> sesiones = datos.sesiones;

		LanzadorDeException.verificarCapacidadMinima(data.getCapacidadMinima());
		LanzadorDeException.verificarCapacidadMaxima(data.getCapacidadMaxima());
		LanzadorDeException.verificarCapacidades(data.getCapacidadMinima(), data.getCapacidadMaxima());
		LanzadorDeException.verificarProfesor(profesor);
		LanzadorDeException.verificarEstadoGrupo(data.getEstado());
		LanzadorDeException.verificarAsignatura(data.getAsignatura());
		LanzadorDeException.verificarSalon(data.getSalon());
		LanzadorDeException.verificarSesiones(sesiones.size());
		LanzadorDeException.verificarCapacidadMaximaInstitucional(data.getCapacidadMaxima());
		LanzadorDeException.verificarCuposInscritos(data.getInscritos(), data.getCapacidadMaxima());
		LanzadorDeException.verificarCapacidadSalon(salon.getCapacidad(), data.getCapacidadMaxima());
		LanzadorDeException.verificarRequisitosSalon(asignatura.isRequiereComputadores(), salon.isTieneComputadores(),
				asignatura.isRequiereSillasMoviles(), salon.isTieneSillasMoviles());
		LanzadorDeException.verificarSesionesFrecuencia(sesiones.size(), asignatura.getFrecuenciaSemanal());
		LanzadorDeException.verificarSesionesGrupo(sesiones);
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

	private List<FranjaHoraria> guardarSesiones(List<FranjaHoraria> sesiones) {
		List<FranjaHoraria> guardadas = new ArrayList<>();
		for (FranjaHoraria sesion : sesiones) {
			guardadas.add(
					franjaRep.save(new FranjaHoraria(sesion.getDia(), sesion.getHoraInicio(), sesion.getHoraFin())));
		}
		return guardadas;
	}

	private Profesor cargarProfesor(ProfesorDTO dto) {
		if (dto == null)
			return null;
		return profesorRep.findById(dto.getId())
				.orElseThrow(() -> new ProfesorInvalidoException("El profesor indicado no existe"));
	}

	private Salon cargarSalon(SalonDTO dto) {
		if (dto == null)
			return null;
		return salonRep.findById(dto.getId())
				.orElseThrow(() -> new SalonInvalidoException("El salón indicado no existe"));
	}

	private Asignatura cargarAsignatura(AsignaturaDTO dto) {
		if (dto == null)
			return null;
		return asignaturaRep.findById(dto.getId())
				.orElseThrow(() -> new AsignaturaInvalidaException("La asignatura indicada no existe"));
	}

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