package co.edu.unbosque.horarios.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.repository.CrudRepository;

import co.edu.unbosque.horarios.entity.Asignatura;
import co.edu.unbosque.horarios.entity.Grupo;
import co.edu.unbosque.horarios.entity.Profesor;
import co.edu.unbosque.horarios.entity.Salon;
import co.edu.unbosque.horarios.util.enums.EstadoGrupo;

/**
 * Interfaz de repositorio para la entidad Grupo.
 * <p>
 * Define las operaciones de persistencia para gestionar los datos de los grupos
 * en la base de datos. Extiende de CrudRepository para heredar funcionalidades
 * básicas de CRUD y añade métodos de consulta personalizados basados en los
 * atributos específicos de la entidad.
 *
 * @version 1.0
 */
public interface GrupoRepository extends CrudRepository<Grupo, Long> {

	/**
	 * Busca los grupos asociados a una asignatura específica.
	 * 
	 * @param asignatura La asignatura asociada a los grupos.
	 * @return Una lista con los grupos encontrados.
	 */
	public List<Grupo> findByAsignatura(Asignatura asignatura);

	/**
	 * Busca los grupos asociados a un profesor específico.
	 * 
	 * @param profesor El profesor asociado a los grupos.
	 * @return Una lista con los grupos encontrados.
	 */
	public List<Grupo> findByProfesor(Profesor profesor);

	/**
	 * Busca los grupos que tengan un estado específico.
	 * 
	 * @param estado El estado de los grupos a buscar.
	 * @return Una lista con los grupos encontrados.
	 */
	public List<Grupo> findByEstado(EstadoGrupo estado);

	/**
	 * Busca los grupos por un salón específico
	 * 
	 * @param salon El salon de los grupos a buscar.
	 * @return Una lista con los grupos encontrados.
	 */
	public List<Grupo> findBySalon(Salon salon);

	/**
	 * Verifica la existencia de un grupo mediante su número, evaluando de forma
	 * global en la base de datos. (Usado por compatibilidad o validaciones
	 * generales).
	 * 
	 * @param numeroGrupo El número del grupo a comprobar.
	 * @return true si el registro existe en cualquier asignatura, false en caso
	 *         contrario.
	 */
	public boolean existsByNumeroGrupo(int numeroGrupo);

	/**
	 * Busca y retorna todos los grupos que compartan el mismo número de grupo, sin
	 * importar a qué asignatura pertenezcan.
	 * 
	 * @param numeroGrupo El número del grupo a buscar.
	 * @return Una lista con los grupos encontrados que tienen ese número.
	 */
	public List<Grupo> findByNumeroGrupo(int numeroGrupo);

	/**
	 * Elimina todos los grupos que tengan un número específico en todo el sistema,
	 * sin importar a qué asignatura pertenezcan.
	 * 
	 * @param numeroGrupo El número del grupo a eliminar.
	 */
	public void deleteByNumeroGrupo(int numeroGrupo);

	/**
	 * Busca un grupo específico garantizando que coincida su número y la asignatura
	 * a la que pertenece.
	 * 
	 * @param numeroGrupo El número del grupo a buscar.
	 * @param asignatura  La asignatura asociada.
	 * @return Un Optional con el grupo encontrado.
	 */
	public Optional<Grupo> findByNumeroGrupoAndAsignatura(int numeroGrupo, Asignatura asignatura);

	/**
	 * Verifica si ya existe un número de grupo creado específicamente dentro de una
	 * asignatura. (Garantiza que el número de grupo sea único por materia).
	 * 
	 * @param numeroGrupo El número del grupo a comprobar.
	 * @param asignatura  La asignatura asociada al grupo.
	 * @return true si el grupo ya existe en esa asignatura, false en caso
	 *         contrario.
	 */
	public boolean existsByNumeroGrupoAndAsignatura(int numeroGrupo, Asignatura asignatura);

	/**
	 * Elimina un grupo específico filtrándolo simultáneamente por su número y su
	 * asignatura.
	 * 
	 * @param numeroGrupo El número del grupo a eliminar.
	 * @param asignatura  La asignatura a la que pertenece el grupo.
	 */
	public void deleteByNumeroGrupoAndAsignatura(int numeroGrupo, Asignatura asignatura);
	
	/**
	 * Verifica si ya existe un grupo con el mismo número, asignatura y periodo
	 * académico.
	 *
	 * @param numeroGrupo El número del grupo a comprobar.
	 * @param asignatura  La asignatura asociada al grupo.
	 * @param periodo     El periodo académico del grupo, por ejemplo "2026-2".
	 * @return true si el grupo ya existe en ese periodo, false en caso contrario.
	 */
	public boolean existsByNumeroGrupoAndAsignaturaAndPeriodo(int numeroGrupo, Asignatura asignatura, String periodo);

}