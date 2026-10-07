package co.edu.unbosque.horarios.service;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import co.edu.unbosque.horarios.dto.FranjaHorariaDTO;
import co.edu.unbosque.horarios.entity.FranjaHoraria;
import co.edu.unbosque.horarios.repository.FranjaHorariaRepository;
import co.edu.unbosque.horarios.util.LanzadorDeException;
import co.edu.unbosque.horarios.util.enums.Dia;

/**
 * Servicio encargado de la gestión de franjas horarias del sistema.
 * <p>
 * Implementa operaciones CRUD, búsquedas por día y horarios
 * y validaciones de negocio mediante LanzadorDeException.
 */
@Service
public class FranjaHorariaService implements CRUDOperation<FranjaHorariaDTO> {

	@Autowired
	private FranjaHorariaRepository franjaHorariaRep;

	@Autowired
	private ModelMapper mapper;

	public FranjaHorariaService() {
	}

	/**
	 * Cuenta el total de franjas horarias registradas.
	 *
	 * @return número total de franjas horarias.
	 */
	@Override
	public long count() {
		return franjaHorariaRep.count();
	}

	/**
	 * Verifica si existe una franja horaria por ID.
	 *
	 * @param id identificador de la franja.
	 * @return true si existe, false si no.
	 */
	@Override
	public boolean exist(Long id) {
		LanzadorDeException.verificarId(id);
		return franjaHorariaRep.existsById(id);
	}

	/**
	 * Crea una nueva franja horaria.
	 *
	 * @param data información de la franja.
	 * @return 0 si se crea correctamente, 1 si existe algún conflicto.
	 */
	@Override
	public int create(FranjaHorariaDTO data) {

		LanzadorDeException.verificarFranjaHoraria(data);

		LanzadorDeException.verificarDia(data.getDia());

		LanzadorDeException.verificarHoraInicio(
				data.getHoraInicio());

		LanzadorDeException.verificarHoraFin(
				data.getHoraFin());

		LanzadorDeException.verificarHorario(
				data.getHoraInicio(),
				data.getHoraFin());

		FranjaHoraria entity =
				mapper.map(data, FranjaHoraria.class);

		franjaHorariaRep.save(entity);

		return 0;
	}

	/**
	 * Obtiene todas las franjas horarias registradas.
	 *
	 * @return lista de franjas horarias en formato DTO.
	 */
	@Override
	public List<FranjaHorariaDTO> getAll() {

		Iterable<FranjaHoraria> entityList =
				franjaHorariaRep.findAll();

		List<FranjaHorariaDTO> dtoList =
				new ArrayList<>();

		entityList.forEach(entity -> {

			FranjaHorariaDTO dto =
					mapper.map(
							entity,
							FranjaHorariaDTO.class);

			dtoList.add(dto);
		});

		return dtoList;
	}

	/**
	 * Elimina una franja horaria por ID.
	 *
	 * @param id identificador de la franja.
	 * @return 0 si se elimina, 1 si no existe.
	 */
	@Override
	public int deleteById(Long id) {

		LanzadorDeException.verificarId(id);

		if (franjaHorariaRep.existsById(id)) {

			franjaHorariaRep.deleteById(id);

			return 0;
		}

		return 1;
	}

	/**
	 * Actualiza una franja horaria.
	 *
	 * @param id identificador de la franja.
	 * @param data nuevos datos.
	 * @return 0 si se actualiza correctamente, 1 si no existe.
	 */
	@Override
	public int updateById(
			Long id,
			FranjaHorariaDTO data) {

		LanzadorDeException.verificarId(id);

		LanzadorDeException.verificarFranjaHoraria(data);

		LanzadorDeException.verificarDia(data.getDia());

		LanzadorDeException.verificarHoraInicio(
				data.getHoraInicio());

		LanzadorDeException.verificarHoraFin(
				data.getHoraFin());

		LanzadorDeException.verificarHorario(
				data.getHoraInicio(),
				data.getHoraFin());

		Optional<FranjaHoraria> encontrado =
				franjaHorariaRep.findById(id);

		if (encontrado.isEmpty()) {
			return 1;
		}

		FranjaHoraria temp = encontrado.get();

		temp.setDia(data.getDia());

		temp.setHoraInicio(
				data.getHoraInicio());

		temp.setHoraFin(
				data.getHoraFin());

		franjaHorariaRep.save(temp);

		return 0;
	}

	/**
	 * Busca franjas horarias por día.
	 *
	 * @param dia día de la semana.
	 * @return lista de franjas encontradas.
	 */
	public List<FranjaHorariaDTO> findByDia(Dia dia) {

		LanzadorDeException.verificarDia(dia);

		List<FranjaHoraria> encontrado =
				franjaHorariaRep.findByDia(dia);

		List<FranjaHorariaDTO> dtoList =
				new ArrayList<>();

		encontrado.forEach(franja ->
				dtoList.add(
						mapper.map(
								franja,
								FranjaHorariaDTO.class)));

		return dtoList;
	}

	/**
	 * Busca franjas horarias por hora de inicio.
	 *
	 * @param horaInicio hora de inicio.
	 * @return lista de franjas encontradas.
	 */
	public List<FranjaHorariaDTO> findByHoraInicio(
			LocalTime horaInicio) {

		LanzadorDeException.verificarHoraInicio(
				horaInicio);

		List<FranjaHoraria> encontrado =
				franjaHorariaRep.findByHoraInicio(
						horaInicio);

		List<FranjaHorariaDTO> dtoList =
				new ArrayList<>();

		encontrado.forEach(franja ->
				dtoList.add(
						mapper.map(
								franja,
								FranjaHorariaDTO.class)));

		return dtoList;
	}

	/**
	 * Busca franjas horarias por hora de fin.
	 *
	 * @param horaFin hora de fin.
	 * @return lista de franjas encontradas.
	 */
	public List<FranjaHorariaDTO> findByHoraFin(
			LocalTime horaFin) {

		LanzadorDeException.verificarHoraFin(
				horaFin);

		List<FranjaHoraria> encontrado =
				franjaHorariaRep.findByHoraFin(
						horaFin);

		List<FranjaHorariaDTO> dtoList =
				new ArrayList<>();

		encontrado.forEach(franja ->
				dtoList.add(
						mapper.map(
								franja,
								FranjaHorariaDTO.class)));

		return dtoList;
	}

	/**
	 * Verifica si existen franjas para un día determinado.
	 *
	 * @param dia día de la semana.
	 * @return true si existe al menos una franja.
	 */
	public boolean findDiaAlreadyTaken(Dia dia) {

		LanzadorDeException.verificarDia(dia);

		return franjaHorariaRep.existsByDia(dia);
	}

	/**
	 * Elimina todas las franjas horarias de un día.
	 *
	 * @param dia día cuyas franjas serán eliminadas.
	 * @return 0 si se eliminan, 1 si no existen.
	 */
	public int deleteByDia(Dia dia) {

		LanzadorDeException.verificarDia(dia);

		if (franjaHorariaRep.existsByDia(dia)) {

			franjaHorariaRep.deleteByDia(dia);

			return 0;
		}

		return 1;
	}

	public FranjaHorariaRepository getFranjaHorariaRep() {
		return franjaHorariaRep;
	}

	public void setFranjaHorariaRep(
			FranjaHorariaRepository franjaHorariaRep) {
		this.franjaHorariaRep = franjaHorariaRep;
	}

	public ModelMapper getMapper() {
		return mapper;
	}

	public void setMapper(ModelMapper mapper) {
		this.mapper = mapper;
	}
}