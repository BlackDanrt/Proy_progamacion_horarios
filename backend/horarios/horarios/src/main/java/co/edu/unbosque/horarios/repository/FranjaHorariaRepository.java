package co.edu.unbosque.horarios.repository;

import java.time.LocalTime;
import java.util.List;

import org.springframework.data.repository.CrudRepository;

import co.edu.unbosque.horarios.entity.FranjaHoraria;
import co.edu.unbosque.horarios.util.enums.Dia;

/**
 * Interfaz de repositorio para la entidad FranjaHoraria.
 * <p>
 * Define las operaciones de persistencia para gestionar los datos de las
 * franjas horarias en la base de datos. Extiende de CrudRepository para heredar
 * funcionalidades básicas de CRUD y añade métodos de consulta personalizados
 * basados en los atributos específicos de la entidad.
 *
 * @version 1.0
 */
public interface FranjaHorariaRepository extends CrudRepository<FranjaHoraria, Long> {

	/**
	 * Busca las franjas horarias correspondientes a un día específico.
	 * * @param dia El día de la semana a buscar.
	 * @return Una lista con las franjas horarias encontradas.
	 */
	public List<FranjaHoraria> findByDia(Dia dia);

	/**
	 * Busca las franjas horarias que comienzan a una hora específica.
	 * * @param horaInicio La hora de inicio a buscar.
	 * @return Una lista con las franjas horarias encontradas.
	 */
	public List<FranjaHoraria> findByHoraInicio(LocalTime horaInicio);

	/**
	 * Busca las franjas horarias que terminan a una hora específica.
	 * * @param horaFin La hora de fin a buscar.
	 * @return Una lista con las franjas horarias encontradas.
	 */
	public List<FranjaHoraria> findByHoraFin(LocalTime horaFin);

	/**
	 * Verifica la existencia de una franja horaria correspondiente a un día.
	 * * @param dia El día de la semana a comprobar.
	 * @return true si existe al menos una franja para ese día, false en caso contrario.
	 */
	public boolean existsByDia(Dia dia);

	/**
	 * Elimina todas las franjas horarias correspondientes a un día específico.
	 * * @param dia El día de la semana cuyas franjas se eliminarán.
	 */
	public void deleteByDia(Dia dia);

}