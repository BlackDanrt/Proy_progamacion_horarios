package co.edu.unbosque.horarios.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import co.edu.unbosque.horarios.dto.AsignaturaDTO;
import co.edu.unbosque.horarios.entity.Asignatura;
import co.edu.unbosque.horarios.repository.AsignaturaRepository;
import co.edu.unbosque.horarios.util.LanzadorDeException;

/**
 * Servicio encargado de la gestión de asignaturas del sistema de horarios.
 * <p>
 * Implementa operaciones CRUD, búsquedas por diferentes atributos
 * y validaciones de negocio mediante LanzadorDeException.
 */
@Service
public class AsignaturaService implements CRUDOperation<AsignaturaDTO> {

	@Autowired
	private AsignaturaRepository asignaturaRep;

	@Autowired
	private ModelMapper mapper;

	public AsignaturaService() {
	}

	/**
	 * Cuenta el total de asignaturas registradas.
	 *
	 * @return número total de asignaturas.
	 */
	@Override
	public long count() {
		return asignaturaRep.count();
	}

	/**
	 * Verifica si una asignatura existe por ID.
	 *
	 * @param id identificador de la asignatura.
	 * @return true si existe, false si no.
	 */
	@Override
	public boolean exist(Long id) {
		LanzadorDeException.verificarId(id);
		return asignaturaRep.existsById(id);
	}

	/**
	 * Crea una nueva asignatura.
	 *
	 * @param data información de la asignatura.
	 * @return 0 si se crea correctamente, 1 si existe algún duplicado.
	 */
	@Override
	public int create(AsignaturaDTO data) {

		LanzadorDeException.verificarAsignatura(data);

		LanzadorDeException.verificarNombre(data.getNombre());
		LanzadorDeException.verificarNumCreditos(
				data.getNumCreditos());
		LanzadorDeException.verificarFrecuenciaSemanal(
				data.getFrecuenciaSemanal());

		LanzadorDeException.verificarNombreDuplicado(
				asignaturaRep.existsByNombre(data.getNombre()));

		Asignatura entity = mapper.map(data, Asignatura.class);

		asignaturaRep.save(entity);

		return 0;
	}

	/**
	 * Obtiene todas las asignaturas registradas.
	 *
	 * @return lista de asignaturas en formato DTO.
	 */
	@Override
	public List<AsignaturaDTO> getAll() {

		Iterable<Asignatura> entityList = asignaturaRep.findAll();

		List<AsignaturaDTO> dtoList = new ArrayList<>();

		entityList.forEach(entity -> {
			AsignaturaDTO dto =
					mapper.map(entity, AsignaturaDTO.class);

			dtoList.add(dto);
		});

		return dtoList;
	}

	/**
	 * Elimina una asignatura por ID.
	 *
	 * @param id identificador de la asignatura.
	 * @return 0 si se elimina, 1 si no existe.
	 */
	@Override
	public int deleteById(Long id) {

		LanzadorDeException.verificarId(id);

		if (asignaturaRep.existsById(id)) {
			asignaturaRep.deleteById(id);
			return 0;
		}

		return 1;
	}

	/**
	 * Actualiza una asignatura por ID.
	 *
	 * @param id identificador de la asignatura.
	 * @param data nuevos datos de la asignatura.
	 * @return 0 si se actualiza correctamente, 1 si no existe.
	 */
	@Override
	public int updateById(Long id, AsignaturaDTO data) {

		LanzadorDeException.verificarId(id);
		LanzadorDeException.verificarAsignatura(data);

		LanzadorDeException.verificarNombre(data.getNombre());
		LanzadorDeException.verificarNumCreditos(
				data.getNumCreditos());
		LanzadorDeException.verificarFrecuenciaSemanal(
				data.getFrecuenciaSemanal());

		Optional<Asignatura> encontrado =
				asignaturaRep.findById(id);

		if (encontrado.isEmpty()) {
			return 1;
		}

		Asignatura temp = encontrado.get();

		/*
		 * Solo se verifica duplicado si el nombre
		 * realmente está cambiando.
		 */
		if (!temp.getNombre().equals(data.getNombre())) {

			LanzadorDeException.verificarNombreDuplicado(
					asignaturaRep.existsByNombre(
							data.getNombre()));
		}

		temp.setNombre(data.getNombre());
		temp.setNumCreditos(data.getNumCreditos());
		temp.setFrecuenciaSemanal(
				data.getFrecuenciaSemanal());

		asignaturaRep.save(temp);

		return 0;
	}

	/**
	 * Busca una asignatura por nombre.
	 *
	 * @param nombre nombre de la asignatura.
	 * @return lista con la asignatura encontrada.
	 */
	public List<AsignaturaDTO> findByNombre(String nombre) {

		LanzadorDeException.verificarNombre(nombre);

		Optional<Asignatura> encontrado =
				asignaturaRep.findByNombre(nombre);

		List<AsignaturaDTO> dtoList = new ArrayList<>();

		encontrado.ifPresent(asignatura ->
				dtoList.add(
						mapper.map(
								asignatura,
								AsignaturaDTO.class)));

		return dtoList;
	}

	/**
	 * Busca una asignatura por número de créditos.
	 *
	 * @param numCreditos número de créditos.
	 * @return lista con las asignaturas encontradas.
	 */
	public List<AsignaturaDTO> findByNumCreditos(
			int numCreditos) {

		LanzadorDeException.verificarNumCreditos(
				numCreditos);

		Optional<Asignatura> encontrado =
				asignaturaRep.findByNumCreditos(numCreditos);

		List<AsignaturaDTO> dtoList = new ArrayList<>();

		encontrado.ifPresent(asignatura ->
				dtoList.add(
						mapper.map(
								asignatura,
								AsignaturaDTO.class)));

		return dtoList;
	}

	/**
	 * Busca una asignatura por frecuencia semanal.
	 *
	 * @param frecuenciaSemanal frecuencia semanal.
	 * @return lista con las asignaturas encontradas.
	 */
	public List<AsignaturaDTO> findByFrecuenciaSemanal(
			int frecuenciaSemanal) {

		LanzadorDeException.verificarFrecuenciaSemanal(
				frecuenciaSemanal);

		Optional<Asignatura> encontrado =
				asignaturaRep.findByFrecuenciaSemanal(
						frecuenciaSemanal);

		List<AsignaturaDTO> dtoList = new ArrayList<>();

		encontrado.ifPresent(asignatura ->
				dtoList.add(
						mapper.map(
								asignatura,
								AsignaturaDTO.class)));

		return dtoList;
	}

	/**
	 * Verifica si un nombre de asignatura ya está registrado.
	 *
	 * @param nombre nombre de la asignatura.
	 * @return true si ya existe, false si no.
	 */
	public boolean findNombreAlreadyTaken(String nombre) {

		LanzadorDeException.verificarNombre(nombre);

		return asignaturaRep.existsByNombre(nombre);
	}

	/**
	 * Elimina una asignatura mediante su nombre.
	 *
	 * @param nombre nombre de la asignatura.
	 * @return 0 si se elimina, 1 si no existe.
	 */
	public int deleteByNombre(String nombre) {

		LanzadorDeException.verificarNombre(nombre);

		if (asignaturaRep.existsByNombre(nombre)) {

			asignaturaRep.deleteByNombre(nombre);

			return 0;
		}

		return 1;
	}

	public AsignaturaRepository getAsignaturaRep() {
		return asignaturaRep;
	}

	public void setAsignaturaRep(
			AsignaturaRepository asignaturaRep) {

		this.asignaturaRep = asignaturaRep;
	}

	public ModelMapper getMapper() {
		return mapper;
	}

	public void setMapper(ModelMapper mapper) {
		this.mapper = mapper;
	}
}