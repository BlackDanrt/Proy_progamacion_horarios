package co.edu.unbosque.horarios.repository;

import java.util.Optional;

import org.springframework.data.repository.CrudRepository;

import co.edu.unbosque.horarios.entity.Administrador;

/**
 * Interfaz de repositorio para la entidad Administrador.
 * <p>
 * Define las operaciones de persistencia para gestionar los datos de los
 * administradores en la base de datos.
 *
 * @version 1.0
 */
public interface AdministradorRepository extends CrudRepository<Administrador, Long> {

	/**
	 * Busca administradores por su código de administrador.
	 *
	 * @param codigoAdministrador código del administrador.
	 * @return Optional con el administrador encontrado.
	 */
	Optional<Administrador> findByCodigoAdministrador(String codigoAdministrador);

	/**
	 * Busca administradores asociados a un correo electrónico.
	 *
	 * @param correo correo electrónico.
	 * @return Optional con el administrador encontrado.
	 */
	Optional<Administrador> findByCorreo(String correo);

	/**
	 * Busca administradores mediante su número de documento.
	 *
	 * @param documento número de documento.
	 * @return Optional con el administrador encontrado.
	 */
	Optional<Administrador> findByDocumento(long documento);

	/**
	 * Verifica si existe un administrador mediante su código.
	 *
	 * @param codigoAdministrador código del administrador.
	 * @return true si existe, false si no.
	 */
	boolean existsByCodigoAdministrador(String codigoAdministrador);

	/**
	 * Verifica si existe un administrador mediante su correo.
	 *
	 * @param correo correo electrónico.
	 * @return true si existe, false si no.
	 */
	boolean existsByCorreo(String correo);

	/**
	 * Verifica si existe un administrador mediante su documento.
	 *
	 * @param documento número de documento.
	 * @return true si existe, false si no.
	 */
	boolean existsByDocumento(long documento);

	/**
	 * Elimina un administrador mediante su código.
	 *
	 * @param codigoAdministrador código del administrador.
	 */
	void deleteByCodigoAdministrador(String codigoAdministrador);
}