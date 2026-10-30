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

import co.edu.unbosque.horarios.dto.AsignaturaDTO;
import co.edu.unbosque.horarios.service.AsignaturaService;

/**
 * Controlador REST que gestiona las operaciones relacionadas con las
 * asignaturas del sistema de horarios.
 * <p>
 * Proporciona endpoints para consultar, crear, actualizar y eliminar
 * asignaturas.
 * </p>
 *
 * @author Prisma Web
 * @version 1.0
 */
@RestController
@RequestMapping("/asignatura")
@CrossOrigin(origins = { "http://localhost:8080", "*" })
public class AsignaturaController {

	/**
	 * Servicio que contiene la lógica de negocio para la gestión de asignaturas.
	 */
	@Autowired
	private AsignaturaService asignaturaSer;

	/**
	 * Constructor vacío de la clase.
	 */
	public AsignaturaController() {
		// TODO Auto-generated constructor stub
	}

	/**
	 * Busca una asignatura por su nombre.
	 *
	 * @param nombre nombre de la asignatura.
	 * @return lista con la asignatura y estado 202, 204 si no existe o 400 si hay
	 *         un error.
	 */
	@GetMapping("/buscarpornombre")
	public ResponseEntity<List<AsignaturaDTO>> buscarPorNombre(@RequestParam String nombre) {
		List<AsignaturaDTO> lista = new ArrayList<>();
		try {
			lista = asignaturaSer.findByNombre(nombre);
			return lista.isEmpty() ? new ResponseEntity<>(lista, HttpStatus.NO_CONTENT)
					: new ResponseEntity<>(lista, HttpStatus.ACCEPTED);
		} catch (Exception e) {
			return new ResponseEntity<>(lista, HttpStatus.BAD_REQUEST);
		}
	}

	/**
	 * Busca asignaturas por su frecuencia semanal.
	 *
	 * @param frecuenciaSemanal número de sesiones semanales.
	 * @return lista de asignaturas con estado 202, 204 si no hay resultados o 400
	 *         si hay un error.
	 */
	@GetMapping("/buscarporfrecuencia")
	public ResponseEntity<List<AsignaturaDTO>> buscarPorFrecuenciaSemanal(@RequestParam int frecuenciaSemanal) {
		List<AsignaturaDTO> lista = new ArrayList<>();
		try {
			lista = asignaturaSer.findByFrecuenciaSemanal(frecuenciaSemanal);
			return lista.isEmpty() ? new ResponseEntity<>(lista, HttpStatus.NO_CONTENT)
					: new ResponseEntity<>(lista, HttpStatus.ACCEPTED);
		} catch (Exception e) {
			return new ResponseEntity<>(lista, HttpStatus.BAD_REQUEST);
		}
	}

	/**
	 * Busca asignaturas por su número de créditos.
	 *
	 * @param numCreditos número de créditos.
	 * @return lista de asignaturas con estado 202, 204 si no hay resultados o 400
	 *         si hay un error.
	 */
	@GetMapping("/buscarporcreditos")
	public ResponseEntity<List<AsignaturaDTO>> buscarPorNumCreditos(@RequestParam int numCreditos) {
		List<AsignaturaDTO> lista = new ArrayList<>();
		try {
			lista = asignaturaSer.findByNumCreditos(numCreditos);
			return lista.isEmpty() ? new ResponseEntity<>(lista, HttpStatus.NO_CONTENT)
					: new ResponseEntity<>(lista, HttpStatus.ACCEPTED);
		} catch (Exception e) {
			return new ResponseEntity<>(lista, HttpStatus.BAD_REQUEST);
		}
	}

	/**
	 * Crea una nueva asignatura.
	 *
	 * @param asignaturaDTO datos de la asignatura a crear.
	 * @return mensaje de confirmación con estado 201 o el error con estado 400.
	 */
	@PostMapping("/crear")
	public ResponseEntity<String> crearAsignatura(@RequestBody AsignaturaDTO asignaturaDTO) {
		try {
			asignaturaSer.create(asignaturaDTO);
			return new ResponseEntity<>("Asignatura creada con éxito", HttpStatus.CREATED);
		} catch (Exception e) {
			return new ResponseEntity<>("Error al crear la asignatura: " + e.getMessage(), HttpStatus.BAD_REQUEST);
		}
	}

	/**
	 * Actualiza una asignatura existente.
	 *
	 * @param asignaturaDTO nuevos datos de la asignatura, incluido su
	 *                      identificador.
	 * @return mensaje de confirmación con estado 202, 404 si no existe o 400 si hay
	 *         un error.
	 */
	@PutMapping("/actualizar")
	public ResponseEntity<String> actualizarAsignatura(@RequestBody AsignaturaDTO asignaturaDTO) {
		try {
			int status = asignaturaSer.updateById(asignaturaDTO.getId(), asignaturaDTO);
			return (status == 0) ? new ResponseEntity<>("Asignatura actualizada exitosamente", HttpStatus.ACCEPTED)
					: new ResponseEntity<>("Asignatura no encontrada o error al actualizar", HttpStatus.NOT_FOUND);
		} catch (Exception e) {
			return new ResponseEntity<>("Error al actualizar asignatura: " + e.getMessage(), HttpStatus.BAD_REQUEST);
		}
	}

	/**
	 * Elimina una asignatura por su nombre.
	 *
	 * @param nombre nombre de la asignatura.
	 * @return mensaje de confirmación con estado 202, 404 si no existe o 400 si hay
	 *         un error.
	 */
	@DeleteMapping("/eliminar")
	public ResponseEntity<String> eliminarAsignatura(@RequestParam String nombre) {
		try {
			int status = asignaturaSer.deleteByNombre(nombre);
			return (status == 0) ? new ResponseEntity<>("Asignatura eliminado exitosamente", HttpStatus.ACCEPTED)
					: new ResponseEntity<>("Error al eliminar, asignatura no encontrada", HttpStatus.NOT_FOUND);
		} catch (Exception e) {
			return new ResponseEntity<>("Error al eliminar la asignatura: " + e.getMessage(), HttpStatus.BAD_REQUEST);
		}
	}

	/**
	 * Recupera todas las asignaturas registradas en el sistema.
	 *
	 * @return lista de asignaturas con estado 202.
	 */
	@GetMapping("/mostrartodo")
	public ResponseEntity<List<AsignaturaDTO>> mostrarTodo() {
		List<AsignaturaDTO> lista = asignaturaSer.getAll();
		return new ResponseEntity<>(lista, HttpStatus.ACCEPTED);
	}

}