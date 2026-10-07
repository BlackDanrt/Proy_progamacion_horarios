package co.edu.unbosque.horarios.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.repository.CrudRepository;

import co.edu.unbosque.horarios.entity.Asignatura;
import co.edu.unbosque.horarios.entity.FranjaHoraria;
import co.edu.unbosque.horarios.entity.Profesor;
import co.edu.unbosque.horarios.util.enums.Escalafon;
import co.edu.unbosque.horarios.util.enums.TipoVinculacion;

/**
 * Interfaz de repositorio para la entidad Profesor.
 * <p>
 * Define las operaciones de persistencia para gestionar los datos de los
 * profesores en la base de datos. Extiende de CrudRepository para heredar
 * funcionalidades básicas de CRUD y añade métodos de consulta personalizados
 * basados en los atributos específicos de la entidad.
 *
 * @version 1.0
 */
public interface ProfesorRepository extends CrudRepository<Profesor, Long> {

	/**
	 * Busca profesores por su número de documento.
	 * * @param documento El número de documento a buscar.
	 * @return Un Optional con el profesor encontrado.
	 */
	public Optional<Profesor> findByDocumento(long documento);

	/**
	 * Busca profesores por su correo electrónico.
	 * * @param correo El correo electrónico a buscar.
	 * @return Un Optional con el profesor encontrado.
	 */
	public Optional<Profesor> findByCorreo(String correo);

	/**
	 * Busca profesores según su tipo de vinculación.
	 * * @param tipoVinculacion El tipo de vinculación a buscar.
	 * @return Una lista con los profesores encontrados.
	 */
	public List<Profesor> findByTipoVinculacion(TipoVinculacion tipoVinculacion);

	/**
	 * Busca profesores según su escalafón.
	 * * @param escalafon El escalafón a buscar.
	 * @return Una lista con los profesores encontrados.
	 */
	public List<Profesor> findByEscalafon(Escalafon escalafon);

	/**
	 * Busca profesores que tengan una asignatura específica entre sus especialidades.
	 * * @param asignatura La asignatura que pertenece a la especialidad del profesor.
	 * @return Una lista con los profesores encontrados.
	 */
	public List<Profesor> findByEspecialidadesContaining(Asignatura asignatura);

	/**
	 * Busca profesores que tengan disponibilidad en una franja horaria específica.
	 * * @param franjaHoraria La franja horaria disponible.
	 * @return Una lista con los profesores encontrados.
	 */
	public List<Profesor> findByDisponibilidadHorariaContaining(FranjaHoraria franjaHoraria);

	/**
	 * Verifica la existencia de un profesor mediante su número de documento.
	 * * @param documento El número de documento a comprobar.
	 * @return true si el registro existe, false en caso contrario.
	 */
	public boolean existsByDocumento(long documento);

	/**
	 * Verifica la existencia de un profesor mediante su correo electrónico.
	 * * @param correo El correo electrónico a comprobar.
	 * @return true si el registro existe, false en caso contrario.
	 */
	public boolean existsByCorreo(String correo);

	/**
	 * Elimina un profesor mediante su número de documento.
	 * * @param documento El número de documento del profesor a eliminar.
	 */
	public void deleteByDocumento(long documento);

}