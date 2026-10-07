package co.edu.unbosque.horarios.repository;

import java.util.Optional;

import org.springframework.data.repository.CrudRepository;

import co.edu.unbosque.horarios.entity.Asignatura;

/**
 * Interfaz de repositorio para la entidad Asignatura.
 * <p>
 * Define las operaciones de persistencia para gestionar los datos de las
 * asignaturas en la base de datos. Extiende de CrudRepository para heredar
 * funcionalidades básicas de CRUD y añade métodos de consulta personalizados
 * basados en los atributos específicos de la entidad.
 *
 * @version 1.0
 */
public interface AsignaturaRepository extends CrudRepository<Asignatura, Long> {

	/**
	 * Busca asignaturas por su nombre.
	 * * @param nombre El nombre de la asignatura a buscar.
	 * @return Un Optional con la asignatura encontrada.
	 */
	public Optional<Asignatura> findByNombre(String nombre);

	/**
	 * Busca asignaturas por el número de créditos.
	 * * @param numCreditos El número de créditos a buscar.
	 * @return Un Optional con la asignatura encontrada.
	 */
	public Optional<Asignatura> findByNumCreditos(int numCreditos);

	/**
	 * Busca asignaturas por su frecuencia semanal.
	 * * @param frecuenciaSemanal La frecuencia semanal a buscar.
	 * @return Un Optional con la asignatura encontrada.
	 */
	public Optional<Asignatura> findByFrecuenciaSemanal(int frecuenciaSemanal);

	/**
	 * Verifica la existencia de una asignatura mediante su nombre.
	 * * @param nombre El nombre de la asignatura a comprobar.
	 * @return true si el registro existe, false en caso contrario.
	 */
	public boolean existsByNombre(String nombre);

	/**
	 * Elimina una asignatura mediante su nombre.
	 * * @param nombre El nombre de la asignatura a eliminar.
	 */
	public void deleteByNombre(String nombre);

}