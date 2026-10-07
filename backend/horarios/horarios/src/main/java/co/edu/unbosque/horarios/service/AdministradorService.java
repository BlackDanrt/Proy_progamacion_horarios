package co.edu.unbosque.horarios.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import co.edu.unbosque.horarios.dto.AdministradorDTO;
import co.edu.unbosque.horarios.entity.Administrador;
import co.edu.unbosque.horarios.repository.AdministradorRepository;
import co.edu.unbosque.horarios.util.LanzadorDeException;

/**
 * Servicio encargado de la gestión de administradores del sistema.
 * <p>
 * Implementa operaciones CRUD, autenticación, búsquedas y validaciones
 * relacionadas con los administradores.
 */
@Service
public class AdministradorService implements CRUDOperation<AdministradorDTO> {

	@Autowired
	private AdministradorRepository administradorRep;

	@Autowired
	private ModelMapper mapper;

	@Autowired
	private PasswordEncoder passwordEncoder;

	public AdministradorService() {
	}

	/**
	 * Cuenta el total de administradores registrados.
	 *
	 * @return número total de administradores.
	 */
	@Override
	public long count() {
		return administradorRep.count();
	}

	/**
	 * Verifica si existe un administrador por ID.
	 *
	 * @param id identificador del administrador.
	 * @return true si existe, false si no.
	 */
	@Override
	public boolean exist(Long id) {
		LanzadorDeException.verificarId(id);
		return administradorRep.existsById(id);
	}

	/**
	 * Crea un nuevo administrador.
	 *
	 * @param data información del administrador.
	 * @return 0 si se crea correctamente, 1 si existe algún duplicado.
	 */
	@Override
	public int create(AdministradorDTO data) {

		LanzadorDeException.verificarAdministrador(data);
		LanzadorDeException.verificarNombre(data.getNombre());
		LanzadorDeException.verificarApellido(data.getApellido());
		LanzadorDeException.verificarDocumento(data.getDocumento());
		LanzadorDeException.verificarCorreoElectronico(data.getCorreo());
		LanzadorDeException.verificarContrasena(data.getContrasenia());
		LanzadorDeException.verificarCodigoAdministrador(
				data.getCodigoAdministrador());

		LanzadorDeException.verificarCorreoDuplicado(
				administradorRep.existsByCorreo(data.getCorreo()));

		LanzadorDeException.verificarCodigoAdministradorDuplicado(
				administradorRep.existsByCodigoAdministrador(
						data.getCodigoAdministrador()));

		LanzadorDeException.verificarDocumentoDuplicado(
				administradorRep.existsByDocumento(data.getDocumento()));

		Administrador entity = mapper.map(data, Administrador.class);

		entity.setContrasenia(
				passwordEncoder.encode(data.getContrasenia()));

		administradorRep.save(entity);

		return 0;
	}

	/**
	 * Obtiene todos los administradores registrados.
	 *
	 * @return lista de administradores en formato DTO.
	 */
	@Override
	public List<AdministradorDTO> getAll() {

		Iterable<Administrador> entityList = administradorRep.findAll();

		List<AdministradorDTO> dtoList = new ArrayList<>();

		entityList.forEach(entity -> {
			AdministradorDTO dto =
					mapper.map(entity, AdministradorDTO.class);

			dtoList.add(dto);
		});

		return dtoList;
	}

	/**
	 * Elimina un administrador por ID.
	 *
	 * @param id identificador del administrador.
	 * @return 0 si se elimina, 1 si no existe.
	 */
	@Override
	public int deleteById(Long id) {

		LanzadorDeException.verificarId(id);

		if (administradorRep.existsById(id)) {
			administradorRep.deleteById(id);
			return 0;
		}

		return 1;
	}

	/**
	 * Actualiza la información de un administrador.
	 *
	 * @param id identificador del administrador.
	 * @param data nuevos datos.
	 * @return 0 si se actualiza correctamente, 1 si no existe.
	 */
	@Override
	public int updateById(Long id, AdministradorDTO data) {

		LanzadorDeException.verificarId(id);
		LanzadorDeException.verificarAdministrador(data);

		LanzadorDeException.verificarNombre(data.getNombre());
		LanzadorDeException.verificarApellido(data.getApellido());
		LanzadorDeException.verificarDocumento(data.getDocumento());
		LanzadorDeException.verificarCorreoElectronico(data.getCorreo());
		LanzadorDeException.verificarCodigoAdministrador(
				data.getCodigoAdministrador());

		if (data.getContrasenia() != null
				&& !data.getContrasenia().isBlank()) {

			LanzadorDeException.verificarContrasena(
					data.getContrasenia());
		}

		Optional<Administrador> encontrado =
				administradorRep.findById(id);

		if (encontrado.isEmpty()) {
			return 1;
		}

		Administrador temp = encontrado.get();

		// Verificar código duplicado
		if (!temp.getCodigoAdministrador()
				.equals(data.getCodigoAdministrador())) {

			LanzadorDeException.verificarCodigoAdministradorDuplicado(
					administradorRep.existsByCodigoAdministrador(
							data.getCodigoAdministrador()));
		}

		// Verificar correo duplicado
		if (!temp.getCorreo().equals(data.getCorreo())) {

			LanzadorDeException.verificarCorreoDuplicado(
					administradorRep.existsByCorreo(
							data.getCorreo()));
		}

		// Verificar documento duplicado
		if (temp.getDocumento() != data.getDocumento()) {

			LanzadorDeException.verificarDocumentoDuplicado(
					administradorRep.existsByDocumento(
							data.getDocumento()));
		}

		temp.setNombre(data.getNombre());
		temp.setApellido(data.getApellido());
		temp.setDocumento(data.getDocumento());
		temp.setCorreo(data.getCorreo());
		temp.setCodigoAdministrador(
				data.getCodigoAdministrador());
		temp.setEstado(data.isEstado());

		if (data.getContrasenia() != null
				&& !data.getContrasenia().isBlank()) {

			temp.setContrasenia(
					passwordEncoder.encode(
							data.getContrasenia()));
		}

		administradorRep.save(temp);

		return 0;
	}

	/**
	 * Busca un administrador por código.
	 *
	 * @param codigoAdministrador código del administrador.
	 * @return lista con el administrador encontrado.
	 */
	public List<AdministradorDTO> findByCodigoAdministrador(
			String codigoAdministrador) {

		LanzadorDeException.verificarCodigoAdministrador(
				codigoAdministrador);

		Optional<Administrador> encontrado =
				administradorRep.findByCodigoAdministrador(
						codigoAdministrador);

		List<AdministradorDTO> dtoList = new ArrayList<>();

		encontrado.ifPresent(administrador ->
				dtoList.add(
						mapper.map(
								administrador,
								AdministradorDTO.class)));

		return dtoList;
	}

	/**
	 * Busca un administrador por correo.
	 *
	 * @param correo correo electrónico.
	 * @return lista con el administrador encontrado.
	 */
	public List<AdministradorDTO> findByCorreo(String correo) {

		LanzadorDeException.verificarCorreoElectronico(correo);

		Optional<Administrador> encontrado =
				administradorRep.findByCorreo(correo);

		List<AdministradorDTO> dtoList = new ArrayList<>();

		encontrado.ifPresent(administrador ->
				dtoList.add(
						mapper.map(
								administrador,
								AdministradorDTO.class)));

		return dtoList;
	}

	/**
	 * Busca un administrador por documento.
	 *
	 * @param documento número de documento.
	 * @return lista con el administrador encontrado.
	 */
	public List<AdministradorDTO> findByDocumento(long documento) {

		LanzadorDeException.verificarDocumento(documento);

		Optional<Administrador> encontrado =
				administradorRep.findByDocumento(documento);

		List<AdministradorDTO> dtoList = new ArrayList<>();

		encontrado.ifPresent(administrador ->
				dtoList.add(
						mapper.map(
								administrador,
								AdministradorDTO.class)));

		return dtoList;
	}

	/**
	 * Verifica si un código de administrador ya está registrado.
	 *
	 * @param codigoAdministrador código a verificar.
	 * @return true si ya existe.
	 */
	public boolean findCodigoAdministradorAlreadyTaken(
			String codigoAdministrador) {

		LanzadorDeException.verificarCodigoAdministrador(
				codigoAdministrador);

		return administradorRep.existsByCodigoAdministrador(
				codigoAdministrador);
	}

	/**
	 * Verifica si un correo ya está registrado.
	 *
	 * @param correo correo a verificar.
	 * @return true si ya existe.
	 */
	public boolean findCorreoAlreadyTaken(String correo) {

		LanzadorDeException.verificarCorreoElectronico(correo);

		return administradorRep.existsByCorreo(correo);
	}

	/**
	 * Verifica si un documento ya está registrado.
	 *
	 * @param documento documento a verificar.
	 * @return true si ya existe.
	 */
	public boolean findDocumentoAlreadyTaken(long documento) {

		LanzadorDeException.verificarDocumento(documento);

		return administradorRep.existsByDocumento(documento);
	}

	/**
	 * Autentica un administrador mediante correo y contraseña.
	 *
	 * @param correo correo del administrador.
	 * @param contrasenia contraseña.
	 * @return 0 login correcto, 1 contraseña incorrecta,
	 *         2 administrador inexistente, 3 administrador inactivo.
	 */
	public int login(String correo, String contrasenia) {

		LanzadorDeException.verificarCorreoElectronico(correo);
		LanzadorDeException.verificarContrasena(contrasenia);

		Optional<Administrador> encontrado =
				administradorRep.findByCorreo(correo);

		if (encontrado.isEmpty()) {
			return 2;
		}

		Administrador administrador = encontrado.get();

		if (!administrador.isEstado()) {
			return 3;
		}

		if (passwordEncoder.matches(
				contrasenia,
				administrador.getContrasenia())) {

			return 0;
		}

		return 1;
	}

	/**
	 * Actualiza el estado de un administrador.
	 *
	 * @param id identificador del administrador.
	 * @param estado nuevo estado.
	 * @return 0 si se actualiza, 1 si no existe.
	 */
	public int actualizarEstado(Long id, boolean estado) {

		LanzadorDeException.verificarId(id);

		Optional<Administrador> encontrado =
				administradorRep.findById(id);

		if (encontrado.isEmpty()) {
			return 1;
		}

		Administrador administrador = encontrado.get();

		administrador.setEstado(estado);

		administradorRep.save(administrador);

		return 0;
	}

	/**
	 * Elimina un administrador mediante su código.
	 *
	 * @param codigoAdministrador código del administrador.
	 * @return 0 si se elimina, 1 si no existe.
	 */
	public int deleteByCodigoAdministrador(
			String codigoAdministrador) {

		LanzadorDeException.verificarCodigoAdministrador(
				codigoAdministrador);

		if (administradorRep.existsByCodigoAdministrador(
				codigoAdministrador)) {

			administradorRep.deleteByCodigoAdministrador(
					codigoAdministrador);

			return 0;
		}

		return 1;
	}

	public AdministradorRepository getAdministradorRep() {
		return administradorRep;
	}

	public void setAdministradorRep(
			AdministradorRepository administradorRep) {

		this.administradorRep = administradorRep;
	}

	public ModelMapper getMapper() {
		return mapper;
	}

	public void setMapper(ModelMapper mapper) {
		this.mapper = mapper;
	}

	public PasswordEncoder getPasswordEncoder() {
		return passwordEncoder;
	}

	public void setPasswordEncoder(
			PasswordEncoder passwordEncoder) {

		this.passwordEncoder = passwordEncoder;
	}
}