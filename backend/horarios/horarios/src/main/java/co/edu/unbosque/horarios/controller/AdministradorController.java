package co.edu.unbosque.horarios.controller;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import co.edu.unbosque.horarios.dto.AdministradorDTO;
import co.edu.unbosque.horarios.service.AdministradorService;

/**
 * Controlador REST que gestiona las operaciones relacionadas con los
 * administradores del sistema de horarios.
 * <p>
 * Proporciona endpoints para consultar, crear, actualizar y eliminar
 * administradores.
 * </p>
 *
 * @author Prisma Web
 * @version 1.0
 */
@RestController
@RequestMapping("/administrador")
@CrossOrigin(origins = { "http://localhost:8080", "*" })
public class AdministradorController {

	/**
	 * Servicio que contiene la lógica de negocio para la gestión de
	 * administradores.
	 */
	@Autowired
	private AdministradorService administradorSer;

	/**
	 * Constructor vacío de la clase.
	 */
	public AdministradorController() {
		// TODO Auto-generated constructor stub
	}

	/**
	 * Busca un administrador por su código.
	 *
	 * @param codigoAdministrador código del administrador.
	 * @return lista con el administrador y estado 200, 204 si no existe o 400 si
	 *         hay un error.
	 */
	@GetMapping("/buscarporcodigo")
	public ResponseEntity<List<AdministradorDTO>> buscarPorCodigoAdministrador(
			@RequestParam String codigoAdministrador) {
		List<AdministradorDTO> lista = new ArrayList<>();
		try {
			lista = administradorSer.findByCodigoAdministrador(codigoAdministrador);
			return lista.isEmpty() ? new ResponseEntity<>(lista, HttpStatus.NO_CONTENT)
					: new ResponseEntity<>(lista, HttpStatus.OK);
		} catch (Exception e) {
			return new ResponseEntity<List<AdministradorDTO>>(lista, HttpStatus.BAD_REQUEST);
		}
	}

	/**
	 * Busca un administrador por su correo electrónico.
	 *
	 * @param correo correo electrónico del administrador.
	 * @return lista con el administrador y estado 200, 204 si no existe o 400 si
	 *         hay un error.
	 */
	@GetMapping("/buscarporcorreo")
	public ResponseEntity<List<AdministradorDTO>> buscarPorCorreo(@RequestParam String correo) {
		List<AdministradorDTO> lista = new ArrayList<>();
		try {
			lista = administradorSer.findByCorreo(correo);
			return lista.isEmpty() ? new ResponseEntity<>(lista, HttpStatus.NO_CONTENT)
					: new ResponseEntity<>(lista, HttpStatus.OK);
		} catch (Exception e) {
			return new ResponseEntity<List<AdministradorDTO>>(lista, HttpStatus.BAD_REQUEST);
		}
	}

	/**
	 * Busca un administrador por su número de documento.
	 *
	 * @param documento número de documento del administrador.
	 * @return lista con el administrador y estado 200, 204 si no existe o 400 si
	 *         hay un error.
	 */
	@GetMapping("/buscarpordocumento")
	public ResponseEntity<List<AdministradorDTO>> buscarPorDocumento(@RequestParam long documento) {
		List<AdministradorDTO> lista = new ArrayList<>();
		try {
			lista = administradorSer.findByDocumento(documento);
			return lista.isEmpty() ? new ResponseEntity<>(lista, HttpStatus.NO_CONTENT)
					: new ResponseEntity<>(lista, HttpStatus.OK);
		} catch (Exception e) {
			return new ResponseEntity<List<AdministradorDTO>>(lista, HttpStatus.BAD_REQUEST);
		}
	}

	/**
	 * Recupera todos los administradores registrados en el sistema.
	 *
	 * @return lista de administradores con estado 202 o 400 si hay un error.
	 */
	@GetMapping("/mostrartodo")
	public ResponseEntity<List<AdministradorDTO>> mostrarTodo() {
		List<AdministradorDTO> lista = new ArrayList<>();
		try {
			lista = administradorSer.getAll();
			return new ResponseEntity<>(lista, HttpStatus.ACCEPTED);
		} catch (Exception e) {
			return new ResponseEntity<>(lista, HttpStatus.BAD_REQUEST);
		}
	}

	/**
	 * Crea un nuevo administrador.
	 *
	 * @param nombre              nombre del administrador.
	 * @param apellido            apellido del administrador.
	 * @param documento           número de documento del administrador.
	 * @param correo              correo electrónico del administrador.
	 * @param contrasenia         contraseña del administrador.
	 * @param codigoAdministrador código del administrador.
	 * @param estado              true si el administrador está activo, false si no.
	 * @return mensaje de confirmación con estado 201 o el error con estado 400.
	 */
	@PostMapping("/crear")
	public ResponseEntity<String> crearAdministrador(@RequestParam String nombre, @RequestParam String apellido,
			@RequestParam long documento, @RequestParam String correo, @RequestParam String contrasenia,
			@RequestParam String codigoAdministrador, @RequestParam boolean estado) {
		try {
			AdministradorDTO administrador = new AdministradorDTO();
			administrador.setNombre(nombre);
			administrador.setApellido(apellido);
			administrador.setDocumento(documento);
			administrador.setCorreo(correo);
			administrador.setContrasenia(contrasenia);
			administrador.setContrasenia(contrasenia);
			administrador.setCodigoAdministrador(codigoAdministrador);
			administrador.setEstado(estado);

			administradorSer.create(administrador);
			return new ResponseEntity<>("Administrador creado con éxito", HttpStatus.CREATED);
		} catch (Exception e) {
			return new ResponseEntity<>("Error al crear el administrador: " + e.getMessage(), HttpStatus.BAD_REQUEST);
		}
	}

	/**
	 * Actualiza un administrador existente.
	 *
	 * @param administradorDTO nuevos datos del administrador, incluido su
	 *                         identificador.
	 * @return mensaje de confirmación con estado 202, 404 si no existe o 400 si hay
	 *         un error.
	 */
	@PutMapping("/actualizar")
	public ResponseEntity<String> actualizarAdministrador(@RequestBody AdministradorDTO administradorDTO) {
		try {
			int status = administradorSer.updateById(administradorDTO.getId(), administradorDTO);
			return (status == 0) ? new ResponseEntity<>("Administrador actualizado exitosamente", HttpStatus.ACCEPTED)
					: new ResponseEntity<>("Administrador no encontrado o error al actualizar", HttpStatus.NOT_FOUND);
		} catch (Exception e) {
			return new ResponseEntity<>("Error al actualizar el administrador: " + e.getMessage(),
					HttpStatus.BAD_REQUEST);
		}
	}

	/**
	 * Elimina un administrador por su identificador.
	 *
	 * @param id identificador del administrador.
	 * @return mensaje de confirmación con estado 202, 404 si no existe o 400 si hay
	 *         un error.
	 */
	@DeleteMapping("/eliminar")
	public ResponseEntity<String> eliminarAdministrador(@RequestParam Long id) {
		try {
			int status = administradorSer.deleteById(id);
			return (status == 0) ? new ResponseEntity<>("Administrador eliminado exitosamente", HttpStatus.ACCEPTED)
					: new ResponseEntity<>("Error al eliminar, administrador no encontrado", HttpStatus.NOT_FOUND);
		} catch (Exception e) {
			return new ResponseEntity<>("Error al eliminar el administrador: " + e.getMessage(),
					HttpStatus.BAD_REQUEST);
		}
	}
}