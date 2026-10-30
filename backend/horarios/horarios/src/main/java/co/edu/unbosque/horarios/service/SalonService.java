package co.edu.unbosque.horarios.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import co.edu.unbosque.horarios.dto.SalonDTO;
import co.edu.unbosque.horarios.entity.Salon;
import co.edu.unbosque.horarios.repository.SalonRepository;
import co.edu.unbosque.horarios.util.LanzadorDeException;
import co.edu.unbosque.horarios.util.enums.Bloque;

/**
 * Servicio encargado de la gestión de salones del sistema de horarios.
 * <p>
 * Implementa operaciones CRUD, búsquedas por diferentes atributos y
 * validaciones de negocio mediante LanzadorDeException.
 */
@Service
public class SalonService implements CRUDOperation<SalonDTO> {

	/**
	 * Repositorio utilizado para el acceso a los datos de los salones.
	 */
	@Autowired
	private SalonRepository salonRep;

	/**
	 * Conversor utilizado para transformar entre entidades y DTOs.
	 */
	@Autowired
	private ModelMapper mapper;

	/**
	 * Constructor vacío de la clase.
	 */
	public SalonService() {
		// Constructor vacío
	}

	/**
	 * Cuenta el total de salones registrados.
	 *
	 * @return número total de salones.
	 */
	@Override
	public long count() {
		return salonRep.count();
	}

	/**
	 * Verifica si existe un salón por ID.
	 *
	 * @param id identificador del salón.
	 * @return true si existe, false si no.
	 */
	@Override
	public boolean exist(Long id) {
		LanzadorDeException.verificarId(id);
		return salonRep.existsById(id);
	}

	/**
	 * Crea un nuevo salón. Valida sus datos y que no exista otro salón con el mismo
	 * bloque y número.
	 *
	 * @param data información del salón.
	 * @return 0 si se crea correctamente.
	 */
	@Override
	public int create(SalonDTO data) {

		LanzadorDeException.verificarSalon(data);
		LanzadorDeException.verificarBloque(data.getBloque());
		LanzadorDeException.verificarNumeroSalon(data.getNumeroSalon());
		LanzadorDeException.verificarCapacidad(data.getCapacidad());

		LanzadorDeException.verificarSalonDuplicado(
				salonRep.existsByBloqueAndNumeroSalon(data.getBloque(), data.getNumeroSalon()));

		Salon salon = mapper.map(data, Salon.class);

		salonRep.save(salon);

		return 0;
	}

	/**
	 * Obtiene todos los salones registrados.
	 *
	 * @return lista de salones en formato DTO.
	 */
	@Override
	public List<SalonDTO> getAll() {

		List<SalonDTO> lista = new ArrayList<>();

		Iterable<Salon> salones = salonRep.findAll();

		for (Salon salon : salones) {
			lista.add(mapper.map(salon, SalonDTO.class));
		}

		return lista;
	}

	/**
	 * Elimina un salón por ID.
	 *
	 * @param id identificador del salón.
	 * @return 0 si se elimina, 1 si no existe.
	 */
	@Override
	public int deleteById(Long id) {

		LanzadorDeException.verificarId(id);

		if (!salonRep.existsById(id)) {
			return 1;
		}

		salonRep.deleteById(id);

		return 0;
	}

	/**
	 * Actualiza un salón por ID. Solo verifica duplicados si el bloque o el número
	 * cambian.
	 *
	 * @param id   identificador del salón.
	 * @param data nuevos datos del salón.
	 * @return 0 si se actualiza correctamente, 1 si no existe.
	 */
	@Override
	public int updateById(Long id, SalonDTO data) {

		LanzadorDeException.verificarId(id);
		LanzadorDeException.verificarSalon(data);
		LanzadorDeException.verificarBloque(data.getBloque());
		LanzadorDeException.verificarNumeroSalon(data.getNumeroSalon());
		LanzadorDeException.verificarCapacidad(data.getCapacidad());

		Optional<Salon> encontrado = salonRep.findById(id);

		if (encontrado.isEmpty()) {
			return 1;
		}

		Salon salonActual = encontrado.get();

		if (salonActual.getBloque() != data.getBloque() || salonActual.getNumeroSalon() != data.getNumeroSalon()) {

			LanzadorDeException.verificarSalonDuplicado(
					salonRep.existsByBloqueAndNumeroSalon(data.getBloque(), data.getNumeroSalon()));
		}

		Salon salon = mapper.map(data, Salon.class);

		salon.setId(id);

		salonRep.save(salon);

		return 0;
	}

	/**
	 * Busca un salón por su bloque y número.
	 *
	 * @param bloque      bloque del salón.
	 * @param numeroSalon número del salón.
	 * @return salón encontrado o null si no existe.
	 */
	public SalonDTO findByBloqueAndNumeroSalon(Bloque bloque, int numeroSalon) {

		LanzadorDeException.verificarBloque(bloque);
		LanzadorDeException.verificarNumeroSalon(numeroSalon);

		Optional<Salon> encontrado = salonRep.findByBloqueAndNumeroSalon(bloque, numeroSalon);

		if (encontrado.isEmpty()) {
			return null;
		}

		return mapper.map(encontrado.get(), SalonDTO.class);
	}

	/**
	 * Busca los salones de un bloque.
	 *
	 * @param bloque bloque del salón.
	 * @return lista de salones encontrados.
	 */
	public List<SalonDTO> findByBloque(Bloque bloque) {

		LanzadorDeException.verificarBloque(bloque);

		List<SalonDTO> lista = new ArrayList<>();

		List<Salon> salones = salonRep.findByBloque(bloque);

		for (Salon salon : salones) {
			lista.add(mapper.map(salon, SalonDTO.class));
		}

		return lista;
	}

	/**
	 * Busca los salones por su capacidad.
	 *
	 * @param capacidad capacidad del salón.
	 * @return lista de salones encontrados.
	 */
	public List<SalonDTO> findByCapacidad(int capacidad) {

		LanzadorDeException.verificarCapacidad(capacidad);

		List<SalonDTO> lista = new ArrayList<>();

		List<Salon> salones = salonRep.findByCapacidad(capacidad);

		for (Salon salon : salones) {
			lista.add(mapper.map(salon, SalonDTO.class));
		}

		return lista;
	}

	/**
	 * Busca los salones según tengan o no computadores.
	 *
	 * @param tieneComputadores true para buscar salones con computadores, false
	 *                          para los que no.
	 * @return lista de salones encontrados.
	 */
	public List<SalonDTO> findByTieneComputadores(boolean tieneComputadores) {

		List<SalonDTO> lista = new ArrayList<>();

		List<Salon> salones = salonRep.findByTieneComputadores(tieneComputadores);

		for (Salon salon : salones) {
			lista.add(mapper.map(salon, SalonDTO.class));
		}

		return lista;
	}

	/**
	 * Busca los salones según tengan o no sillas móviles.
	 *
	 * @param tieneSillasMoviles true para buscar salones con sillas móviles, false
	 *                           para los que no.
	 * @return lista de salones encontrados.
	 */
	public List<SalonDTO> findByTieneSillasMoviles(boolean tieneSillasMoviles) {

		List<SalonDTO> lista = new ArrayList<>();

		List<Salon> salones = salonRep.findByTieneSillasMoviles(tieneSillasMoviles);

		for (Salon salon : salones) {
			lista.add(mapper.map(salon, SalonDTO.class));
		}

		return lista;
	}

	/**
	 * Verifica si ya existe un salón con el bloque y número indicados.
	 *
	 * @param bloque      bloque del salón.
	 * @param numeroSalon número del salón.
	 * @return true si ya existe, false si no.
	 */
	public boolean findSalonAlreadyTaken(Bloque bloque, int numeroSalon) {

		return salonRep.existsByBloqueAndNumeroSalon(bloque, numeroSalon);
	}

	/**
	 * Elimina un salón mediante su bloque y número.
	 *
	 * @param bloque      bloque del salón.
	 * @param numeroSalon número del salón.
	 * @return 0 si se elimina, 1 si no existe.
	 */
	public int deleteByBloqueAndNumeroSalon(Bloque bloque, int numeroSalon) {

		LanzadorDeException.verificarBloque(bloque);
		LanzadorDeException.verificarNumeroSalon(numeroSalon);

		if (!salonRep.existsByBloqueAndNumeroSalon(bloque, numeroSalon)) {
			return 1;
		}

		salonRep.deleteByBloqueAndNumeroSalon(bloque, numeroSalon);
		return 0;
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
	 * Obtiene el conversor entre entidades y DTOs.
	 *
	 * @return conversor ModelMapper.
	 */
	public ModelMapper getMapper() {
		return mapper;
	}

	/**
	 * Establece el conversor entre entidades y DTOs.
	 *
	 * @param mapper conversor ModelMapper.
	 */
	public void setMapper(ModelMapper mapper) {
		this.mapper = mapper;
	}
}