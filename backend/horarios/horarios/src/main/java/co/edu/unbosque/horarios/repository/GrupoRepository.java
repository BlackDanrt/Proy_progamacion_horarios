package co.edu.unbosque.horarios.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.repository.CrudRepository;

import co.edu.unbosque.horarios.entity.Asignatura;
import co.edu.unbosque.horarios.entity.Grupo;
import co.edu.unbosque.horarios.entity.Profesor;
import co.edu.unbosque.horarios.util.enums.EstadoGrupo;

/**
 * Interfaz de repositorio para la entidad Grupo.
 * <p>
 * Define las operaciones de persistencia para gestionar los datos de los
 * grupos en la base de datos. Extiende de CrudRepository para heredar
 * funcionalidades básicas de CRUD y añade métodos de consulta personalizados
 * basados en los atributos específicos de la entidad.
 *
 * @version 1.0
 */
public interface GrupoRepository extends CrudRepository<Grupo, Long> {

	/**
	 * Busca un grupo por su número.
	 * * @param numeroGrupo El número del grupo a buscar.
	 * @return Un Optional con el grupo encontrado.
	 */
	public Optional<Grupo> findByNumeroGrupo(int numeroGrupo);

	/**
	 * Busca los grupos asociados a una asignatura específica.
	 * * @param asignatura La asignatura asociada a los grupos.
	 * @return Una lista con los grupos encontrados.
	 */
	public List<Grupo> findByAsignatura(Asignatura asignatura);

	/**
	 * Busca los grupos asociados a un profesor específico.
	 * * @param profesor El profesor asociado a los grupos.
	 * @return Una lista con los grupos encontrados.
	 */
	public List<Grupo> findByProfesor(Profesor profesor);

	/**
	 * Busca los grupos que tengan un estado específico.
	 * * @param estado El estado de los grupos a buscar.
	 * @return Una lista con los grupos encontrados.
	 */
	public List<Grupo> findByEstado(EstadoGrupo estado);

	/**
	 * Verifica la existencia de un grupo mediante su número.
	 * * @param numeroGrupo El número del grupo a comprobar.
	 * @return true si el registro existe, false en caso contrario.
	 */
	public boolean existsByNumeroGrupo(int numeroGrupo);

	/**
	 * Elimina un grupo mediante su número.
	 * * @param numeroGrupo El número del grupo a eliminar.
	 */
	public void deleteByNumeroGrupo(int numeroGrupo);

}