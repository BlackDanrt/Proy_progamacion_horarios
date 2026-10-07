package co.edu.unbosque.horarios.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.repository.CrudRepository;

import co.edu.unbosque.horarios.entity.Salon;
import co.edu.unbosque.horarios.util.enums.Bloque;

/**
 * Interfaz de repositorio para la entidad Salon.
 * <p>
 * Define las operaciones de persistencia para gestionar los datos de los
 * salones en la base de datos. Extiende de CrudRepository para heredar
 * funcionalidades básicas de CRUD y añade métodos de consulta personalizados
 * basados en los atributos específicos de la entidad.
 *
 * @version 1.0
 */
public interface SalonRepository extends CrudRepository<Salon, Long> {

	/**
	 * Busca un salón mediante su bloque y número de salón.
	 * * @param bloque El bloque donde se encuentra el salón.
	 * @param numeroSalon El número del salón a buscar.
	 * @return Un Optional con el salón encontrado.
	 */
	public Optional<Salon> findByBloqueAndNumeroSalon(Bloque bloque, int numeroSalon);

	/**
	 * Busca los salones pertenecientes a un bloque específico.
	 * * @param bloque El bloque a buscar.
	 * @return Una lista con los salones encontrados.
	 */
	public List<Salon> findByBloque(Bloque bloque);

	/**
	 * Busca los salones que tengan una capacidad específica.
	 * * @param capacidad La capacidad a buscar.
	 * @return Una lista con los salones encontrados.
	 */
	public List<Salon> findByCapacidad(int capacidad);

	/**
	 * Busca los salones que tienen computadores.
	 * * @param tieneComputadores Indica si el salón tiene computadores.
	 * @return Una lista con los salones encontrados.
	 */
	public List<Salon> findByTieneComputadores(boolean tieneComputadores);

	/**
	 * Busca los salones que tienen sillas móviles.
	 * * @param tieneSillasMoviles Indica si el salón tiene sillas móviles.
	 * @return Una lista con los salones encontrados.
	 */
	public List<Salon> findByTieneSillasMoviles(boolean tieneSillasMoviles);

	/**
	 * Verifica la existencia de un salón mediante su bloque y número.
	 * * @param bloque El bloque donde se encuentra el salón.
	 * @param numeroSalon El número del salón a comprobar.
	 * @return true si el registro existe, false en caso contrario.
	 */
	public boolean existsByBloqueAndNumeroSalon(Bloque bloque, int numeroSalon);

	/**
	 * Elimina un salón mediante su bloque y número.
	 * * @param bloque El bloque donde se encuentra el salón.
	 * @param numeroSalon El número del salón a eliminar.
	 */
	public void deleteByBloqueAndNumeroSalon(Bloque bloque, int numeroSalon);

}