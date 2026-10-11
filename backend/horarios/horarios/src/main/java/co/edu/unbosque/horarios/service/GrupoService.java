package co.edu.unbosque.horarios.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import co.edu.unbosque.horarios.dto.AsignaturaDTO;
import co.edu.unbosque.horarios.dto.GrupoDTO;
import co.edu.unbosque.horarios.dto.ProfesorDTO;
import co.edu.unbosque.horarios.entity.Asignatura;
import co.edu.unbosque.horarios.entity.Grupo;
import co.edu.unbosque.horarios.entity.Profesor;
import co.edu.unbosque.horarios.repository.GrupoRepository;
import co.edu.unbosque.horarios.util.LanzadorDeException;
import co.edu.unbosque.horarios.util.enums.EstadoGrupo;

/**
 * Servicio encargado de la gestión de grupos del sistema de horarios.
 * <p>
 * Implementa operaciones CRUD, búsquedas por diferentes atributos
 * y validaciones de negocio mediante LanzadorDeException.
 */
@Service
public class GrupoService implements CRUDOperation<GrupoDTO> {

	@Autowired
	private GrupoRepository grupoRep;

	@Autowired
	private ModelMapper mapper;

	public GrupoService() {
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
	 * Crea un nuevo grupo.
	 *
	 * @param data información del grupo.
	 * @return 0 si se crea correctamente, 1 si existe algún conflicto.
	 */
	@Override
	public int create(GrupoDTO data) {

		LanzadorDeException.verificarGrupo(data);

		LanzadorDeException.verificarNumeroGrupo(
				data.getNumeroGrupo());

		LanzadorDeException.verificarCapacidadMinima(
				data.getCapacidadMinima());

		LanzadorDeException.verificarCapacidadMaxima(
				data.getCapacidadMaxima());

		LanzadorDeException.verificarCapacidades(
				data.getCapacidadMinima(),
				data.getCapacidadMaxima());

		LanzadorDeException.verificarProfesor(
				data.getProfesor());


		LanzadorDeException.verificarEstadoGrupo(
				data.getEstado());

		LanzadorDeException.verificarNumeroGrupoDuplicado(
				grupoRep.existsByNumeroGrupo(
						data.getNumeroGrupo()));

		Grupo entity = mapper.map(data, Grupo.class);

		grupoRep.save(entity);

		return 0;
	}

	/**
	 * Obtiene todos los grupos registrados.
	 *
	 * @return lista de grupos en formato DTO.
	 */
	@Override
	public List<GrupoDTO> getAll() {

		Iterable<Grupo> entityList =
				grupoRep.findAll();

		List<GrupoDTO> dtoList =
				new ArrayList<>();

		entityList.forEach(entity -> {

			GrupoDTO dto =
					mapper.map(
							entity,
							GrupoDTO.class);

			dtoList.add(dto);
		});

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

			return 0;
		}

		return 1;
	}

	/**
	 * Actualiza un grupo por ID.
	 *
	 * @param id identificador del grupo.
	 * @param data nuevos datos del grupo.
	 * @return 0 si se actualiza correctamente, 1 si no existe.
	 */
	@Override
	public int updateById(
			Long id,
			GrupoDTO data) {

		LanzadorDeException.verificarId(id);

		LanzadorDeException.verificarGrupo(data);

		LanzadorDeException.verificarNumeroGrupo(
				data.getNumeroGrupo());

		LanzadorDeException.verificarCapacidadMinima(
				data.getCapacidadMinima());

		LanzadorDeException.verificarCapacidadMaxima(
				data.getCapacidadMaxima());

		LanzadorDeException.verificarCapacidades(
				data.getCapacidadMinima(),
				data.getCapacidadMaxima());

		LanzadorDeException.verificarProfesor(
				data.getProfesor());



		LanzadorDeException.verificarEstadoGrupo(
				data.getEstado());

		Optional<Grupo> encontrado =
				grupoRep.findById(id);

		if (encontrado.isEmpty()) {
			return 1;
		}

		Grupo temp = encontrado.get();

		/*
		 * Solo se verifica duplicado si el número
		 * del grupo realmente está cambiando.
		 */
		if (temp.getNumeroGrupo()
				!= data.getNumeroGrupo()) {

			LanzadorDeException.verificarNumeroGrupoDuplicado(
					grupoRep.existsByNumeroGrupo(
							data.getNumeroGrupo()));
		}

		temp.setAsignatura(
				data.getAsignatura());

		temp.setNumeroGrupo(
				data.getNumeroGrupo());

		temp.setCapacidadMinima(
				data.getCapacidadMinima());

		temp.setCapacidadMaxima(
				data.getCapacidadMaxima());

		temp.setProfesor(
				data.getProfesor());

		temp.setSesiones(
				data.getSesiones());

		temp.setEstado(
				data.getEstado());

		grupoRep.save(temp);

		return 0;
	}

	/**
	 * Busca un grupo por número.
	 *
	 * @param numeroGrupo número del grupo.
	 * @return lista con el grupo encontrado.
	 */
	public List<GrupoDTO> findByNumeroGrupo(
			int numeroGrupo) {

		LanzadorDeException.verificarNumeroGrupo(
				numeroGrupo);

		Optional<Grupo> encontrado =
				grupoRep.findByNumeroGrupo(
						numeroGrupo);

		List<GrupoDTO> dtoList =
				new ArrayList<>();

		encontrado.ifPresent(grupo ->
				dtoList.add(
						mapper.map(
								grupo,
								GrupoDTO.class)));

		return dtoList;
	}

	/**
	 * Busca grupos por asignatura.
	 *
	 * @param asignatura asignatura asociada.
	 * @return lista de grupos encontrados.
	 */
	public List<GrupoDTO> findByAsignatura(
			AsignaturaDTO asignaturaDTO) {

		Asignatura asignatura = mapper.map(asignaturaDTO, Asignatura.class);
		List<Grupo> encontrados =
				grupoRep.findByAsignatura(
						asignatura);

		List<GrupoDTO> dtoList =
				new ArrayList<>();

		encontrados.forEach(grupo ->
				dtoList.add(
						mapper.map(
								grupo,
								GrupoDTO.class)));

		return dtoList;
	}

	/**
	 * Busca grupos por profesor.
	 *
	 * @param profesor profesor asociado.
	 * @return lista de grupos encontrados.
	 */
	public List<GrupoDTO> findByProfesor(
			ProfesorDTO profesorDTO) {
		
		Profesor profesor = mapper.map(profesorDTO, Profesor.class);
		LanzadorDeException.verificarProfesor(
				profesor);

		List<Grupo> encontrados =
				grupoRep.findByProfesor(
						profesor);

		List<GrupoDTO> dtoList =
				new ArrayList<>();

		encontrados.forEach(grupo ->
				dtoList.add(
						mapper.map(
								grupo,
								GrupoDTO.class)));

		return dtoList;
	}

	/**
	 * Busca grupos por estado.
	 *
	 * @param estado estado del grupo.
	 * @return lista de grupos encontrados.
	 */
	public List<GrupoDTO> findByEstado(
			EstadoGrupo estado) {

		LanzadorDeException.verificarEstadoGrupo(
				estado);

		List<Grupo> encontrados =
				grupoRep.findByEstado(
						estado);

		List<GrupoDTO> dtoList =
				new ArrayList<>();

		encontrados.forEach(grupo ->
				dtoList.add(
						mapper.map(
								grupo,
								GrupoDTO.class)));

		return dtoList;
	}

	/**
	 * Verifica si un número de grupo ya está registrado.
	 *
	 * @param numeroGrupo número del grupo.
	 * @return true si ya existe, false si no.
	 */
	public boolean findNumeroGrupoAlreadyTaken(
			int numeroGrupo) {

		LanzadorDeException.verificarNumeroGrupo(
				numeroGrupo);

		return grupoRep.existsByNumeroGrupo(
				numeroGrupo);
	}

	/**
	 * Elimina un grupo mediante su número.
	 *
	 * @param numeroGrupo número del grupo.
	 * @return 0 si se elimina, 1 si no existe.
	 */
	public int deleteByNumeroGrupo(
			int numeroGrupo) {

		LanzadorDeException.verificarNumeroGrupo(
				numeroGrupo);

		if (grupoRep.existsByNumeroGrupo(
				numeroGrupo)) {

			grupoRep.deleteByNumeroGrupo(
					numeroGrupo);

			return 0;
		}

		return 1;
	}

	public GrupoRepository getGrupoRep() {
		return grupoRep;
	}

	public void setGrupoRep(
			GrupoRepository grupoRep) {

		this.grupoRep = grupoRep;
	}

	public ModelMapper getMapper() {
		return mapper;
	}

	public void setMapper(
			ModelMapper mapper) {

		this.mapper = mapper;
	}
}