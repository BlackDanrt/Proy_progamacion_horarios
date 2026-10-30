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

import co.edu.unbosque.horarios.dto.SalonDTO;
import co.edu.unbosque.horarios.service.SalonService;
import co.edu.unbosque.horarios.util.enums.Bloque;

/**
 * Controlador REST que gestiona las operaciones relacionadas con los salones
 * del sistema de horarios.
 * <p>
 * Proporciona endpoints para consultar, crear, actualizar y eliminar salones.
 * </p>
 *
 * @author Prisma Web
 * @version 1.0
 */
@RestController
@RequestMapping("/salon")
@CrossOrigin(origins = { "http://localhost:8080", "*" })
public class SalonController {

	/**
	 * Servicio que contiene la lógica de negocio para la gestión de salones.
	 */
	@Autowired
	private SalonService salonSer;

	/**
	 * Constructor vacío de la clase.
	 */
	public SalonController() {
		// TODO Auto-generated constructor stub
	}

	/**
	 * Busca los salones de un bloque.
	 *
	 * @param bloque bloque del salón.
	 * @return lista de salones con estado 200, 204 si no hay resultados o 400 si
	 *         hay un error.
	 */
	@GetMapping("/buscarporbloque")
	public ResponseEntity<List<SalonDTO>> buscarPorBloque(@RequestParam Bloque bloque) {
		List<SalonDTO> lista = new ArrayList<>();
		try {
			lista = salonSer.findByBloque(bloque);
			return lista.isEmpty() ? new ResponseEntity<>(lista, HttpStatus.NO_CONTENT)
					: new ResponseEntity<>(lista, HttpStatus.OK);
		} catch (Exception e) {
			return new ResponseEntity<List<SalonDTO>>(lista, HttpStatus.BAD_REQUEST);
		}
	}

	/**
	 * Busca los salones por su capacidad.
	 *
	 * @param capacidad capacidad del salón.
	 * @return lista de salones con estado 200, 204 si no hay resultados o 400 si
	 *         hay un error.
	 */
	@GetMapping("/buscarporcapacidad")
	public ResponseEntity<List<SalonDTO>> buscarPorCapacidad(@RequestParam int capacidad) {
		List<SalonDTO> lista = new ArrayList<>();
		try {
			lista = salonSer.findByCapacidad(capacidad);
			return lista.isEmpty() ? new ResponseEntity<>(lista, HttpStatus.NO_CONTENT)
					: new ResponseEntity<>(lista, HttpStatus.OK);
		} catch (Exception e) {
			return new ResponseEntity<List<SalonDTO>>(lista, HttpStatus.BAD_REQUEST);
		}
	}

	/**
	 * Busca los salones según tengan o no sillas móviles.
	 *
	 * @param tieneSillasMoviles true para salones con sillas móviles, false para
	 *                           los que no.
	 * @return lista de salones con estado 200, 204 si no hay resultados o 400 si
	 *         hay un error.
	 */
	@GetMapping("/buscarporsillasmoviles")
	public ResponseEntity<List<SalonDTO>> buscarPorTieneSillasMoviles(@RequestParam boolean tieneSillasMoviles) {
		List<SalonDTO> lista = new ArrayList<>();
		try {
			lista = salonSer.findByTieneSillasMoviles(tieneSillasMoviles);
			return lista.isEmpty() ? new ResponseEntity<>(lista, HttpStatus.NO_CONTENT)
					: new ResponseEntity<>(lista, HttpStatus.OK);
		} catch (Exception e) {
			return new ResponseEntity<List<SalonDTO>>(lista, HttpStatus.BAD_REQUEST);
		}
	}

	/**
	 * Busca los salones según tengan o no computadores.
	 *
	 * @param tieneComputadores true para salones con computadores, false para los
	 *                          que no.
	 * @return lista de salones con estado 200, 204 si no hay resultados o 400 si
	 *         hay un error.
	 */
	@GetMapping("/buscarporcomputadores")
	public ResponseEntity<List<SalonDTO>> buscarPorTieneComputadores(@RequestParam boolean tieneComputadores) {
		List<SalonDTO> lista = new ArrayList<>();
		try {
			lista = salonSer.findByTieneComputadores(tieneComputadores);
			return lista.isEmpty() ? new ResponseEntity<>(lista, HttpStatus.NO_CONTENT)
					: new ResponseEntity<>(lista, HttpStatus.OK);
		} catch (Exception e) {
			return new ResponseEntity<List<SalonDTO>>(lista, HttpStatus.BAD_REQUEST);
		}
	}

	/**
	 * Busca un salón por su bloque y número.
	 *
	 * @param bloque      bloque del salón.
	 * @param numeroSalon número del salón.
	 * @return el salón con estado 200, 204 si no existe o 400 si hay un error.
	 */
	@GetMapping("/buscarporbloquenumero")
	public ResponseEntity<SalonDTO> buscarPorBloqueAndNumeroSalon(@RequestParam Bloque bloque,
			@RequestParam int numeroSalon) {
		SalonDTO salonDTO = new SalonDTO();
		try {
			salonDTO = salonSer.findByBloqueAndNumeroSalon(bloque, numeroSalon);
			return salonDTO == null ? new ResponseEntity<>(salonDTO, HttpStatus.NO_CONTENT)
					: new ResponseEntity<>(salonDTO, HttpStatus.OK);
		} catch (Exception e) {
			return new ResponseEntity<SalonDTO>(salonDTO, HttpStatus.BAD_REQUEST);
		}
	}

	/**
	 * Crea un nuevo salón.
	 *
	 * @param salonDTO datos del salón a crear.
	 * @return mensaje de confirmación con estado 201 o el error con estado 400.
	 */
	@PostMapping("/crear")
	public ResponseEntity<String> crearSalon(@RequestBody SalonDTO salonDTO) {
		try {
			salonSer.create(salonDTO);
			return new ResponseEntity<>("Salón creado con éxito", HttpStatus.CREATED);
		} catch (Exception e) {
			return new ResponseEntity<>("Error al crear el salón: " + e.getMessage(), HttpStatus.BAD_REQUEST);
		}
	}

	/**
	 * Actualiza un salón existente.
	 *
	 * @param salonDTO nuevos datos del salón, incluido su identificador.
	 * @return mensaje de confirmación con estado 202, 404 si no existe o 400 si hay
	 *         un error.
	 */
	@PutMapping("/actualizar")
	public ResponseEntity<String> actualizarSalon(@RequestBody SalonDTO salonDTO) {
		try {
			int status = salonSer.updateById(salonDTO.getId(), salonDTO);
			return (status == 0) ? new ResponseEntity<>("Salón actualizado exitosamente", HttpStatus.ACCEPTED)
					: new ResponseEntity<>("Salón no encontrado o error al actualizar", HttpStatus.NOT_FOUND);
		} catch (Exception e) {
			return new ResponseEntity<>("Error al actualizar salón: " + e.getMessage(), HttpStatus.BAD_REQUEST);
		}
	}

	/**
	 * Elimina un salón por su bloque y número.
	 *
	 * @param bloque      bloque del salón.
	 * @param numeroSalon número del salón.
	 * @return mensaje de confirmación con estado 202, 404 si no existe o 400 si hay
	 *         un error.
	 */
	@DeleteMapping("/eliminar")
	public ResponseEntity<String> eliminarSalon(@RequestParam Bloque bloque, @RequestParam int numeroSalon) {
		try {
			int status = salonSer.deleteByBloqueAndNumeroSalon(bloque, numeroSalon);
			return (status == 0) ? new ResponseEntity<>("Salón eliminado exitosamente", HttpStatus.ACCEPTED)
					: new ResponseEntity<>("Error al eliminar, salón no encontrado", HttpStatus.NOT_FOUND);
		} catch (Exception e) {
			return new ResponseEntity<>("Error al eliminar el salón: " + e.getMessage(), HttpStatus.BAD_REQUEST);
		}
	}

	/**
	 * Recupera todos los salones registrados en el sistema.
	 *
	 * @return lista de salones con estado 202.
	 */
	@GetMapping("/mostrartodo")
	public ResponseEntity<List<SalonDTO>> mostrarTodo() {
		List<SalonDTO> lista = salonSer.getAll();
		return new ResponseEntity<>(lista, HttpStatus.ACCEPTED);
	}
}