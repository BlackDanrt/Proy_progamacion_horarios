package co.edu.unbosque.horarios.controller;

import java.time.LocalTime;
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

import co.edu.unbosque.horarios.dto.FranjaHorariaDTO;
import co.edu.unbosque.horarios.service.FranjaHorariaService;
import co.edu.unbosque.horarios.util.enums.Dia;

/**
 * Controlador REST que gestiona las operaciones relacionadas con las franjas
 * horarias del sistema de horarios.
 * <p>
 * Proporciona endpoints para consultar, crear, actualizar y eliminar franjas
 * horarias.
 * </p>
 *
 * @author Prisma Web
 * @version 1.0
 */
@RestController
@RequestMapping("/franjahoraria")
@CrossOrigin(origins = { "http://localhost:8080", "*" })
public class FranjaHorariaController {

	/**
	 * Servicio que contiene la lógica de negocio para la gestión de franjas
	 * horarias.
	 */
	@Autowired
	private FranjaHorariaService franjaSer;

	/**
	 * Constructor vacío de la clase.
	 */
	public FranjaHorariaController() {
		// TODO Auto-generated constructor stub
	}

	/**
	 * Busca las franjas horarias de un día de la semana.
	 *
	 * @param dia día de la semana.
	 * @return lista de franjas con estado 200, 204 si no hay resultados o 400 si
	 *         hay un error.
	 */
	@GetMapping("/buscarpordia")
	public ResponseEntity<List<FranjaHorariaDTO>> buscarPorDia(@RequestParam Dia dia) {
		List<FranjaHorariaDTO> lista = new ArrayList<>();
		try {
			lista = franjaSer.findByDia(dia);
			return lista.isEmpty() ? new ResponseEntity<>(lista, HttpStatus.NO_CONTENT)
					: new ResponseEntity<>(lista, HttpStatus.OK);
		} catch (Exception e) {
			return new ResponseEntity<List<FranjaHorariaDTO>>(lista, HttpStatus.BAD_REQUEST);
		}
	}

	/**
	 * Busca las franjas horarias por su hora de inicio.
	 *
	 * @param horaInicio hora de inicio de la franja.
	 * @return lista de franjas con estado 200, 204 si no hay resultados o 400 si
	 *         hay un error.
	 */
	@GetMapping("/buscarporhorainicio")
	public ResponseEntity<List<FranjaHorariaDTO>> buscarPorHoraInicio(@RequestParam LocalTime horaInicio) {
		List<FranjaHorariaDTO> lista = new ArrayList<>();
		try {
			lista = franjaSer.findByHoraInicio(horaInicio);
			return lista.isEmpty() ? new ResponseEntity<>(lista, HttpStatus.NO_CONTENT)
					: new ResponseEntity<>(lista, HttpStatus.OK);
		} catch (Exception e) {
			return new ResponseEntity<List<FranjaHorariaDTO>>(lista, HttpStatus.BAD_REQUEST);
		}
	}

	/**
	 * Busca las franjas horarias por su hora de fin.
	 *
	 * @param horaFin hora de fin de la franja.
	 * @return lista de franjas con estado 200, 204 si no hay resultados o 400 si
	 *         hay un error.
	 */
	@GetMapping("/buscarporhorafin")
	public ResponseEntity<List<FranjaHorariaDTO>> buscarPorHoraFin(@RequestParam LocalTime horaFin) {
		List<FranjaHorariaDTO> lista = new ArrayList<>();
		try {
			lista = franjaSer.findByHoraFin(horaFin);
			return lista.isEmpty() ? new ResponseEntity<>(lista, HttpStatus.NO_CONTENT)
					: new ResponseEntity<>(lista, HttpStatus.OK);
		} catch (Exception e) {
			return new ResponseEntity<List<FranjaHorariaDTO>>(lista, HttpStatus.BAD_REQUEST);
		}
	}

	/**
	 * Crea una nueva franja horaria.
	 *
	 * @param franjaHorariaDTO datos de la franja a crear.
	 * @return mensaje de confirmación con estado 201 o el error con estado 400.
	 */
	@PostMapping("/crear")
	public ResponseEntity<String> crearFranjaHoraria(@RequestBody FranjaHorariaDTO franjaHorariaDTO) {
		try {
			franjaSer.create(franjaHorariaDTO);
			return new ResponseEntity<>("Franja horaria creada con éxito", HttpStatus.CREATED);
		} catch (Exception e) {
			return new ResponseEntity<>("Error al crear la franja horaria: " + e.getMessage(), HttpStatus.BAD_REQUEST);
		}
	}

	/**
	 * Actualiza una franja horaria existente.
	 *
	 * @param franjaHorariaDTO nuevos datos de la franja, incluido su identificador.
	 * @return mensaje de confirmación con estado 202, 404 si la franja no existe o
	 *         400 si hay un error.
	 */
	@PutMapping("/actualizar")
	public ResponseEntity<String> actualizarFranjaHoraria(@RequestBody FranjaHorariaDTO franjaHorariaDTO) {
		try {
			int status = franjaSer.updateById(franjaHorariaDTO.getId(), franjaHorariaDTO);
			return (status == 0) ? new ResponseEntity<>("Franja horaria actualizada exitosamente", HttpStatus.ACCEPTED)
					: new ResponseEntity<>("Franja horaria no encontrada o error al actualizar", HttpStatus.NOT_FOUND);
		} catch (Exception e) {
			return new ResponseEntity<>("Error al actualizar la franja horaria: " + e.getMessage(),
					HttpStatus.BAD_REQUEST);
		}
	}

	/**
	 * Elimina una franja horaria por su identificador.
	 *
	 * @param franjaHorariaId identificador de la franja.
	 * @return mensaje de confirmación con estado 202, 404 si la franja no existe o
	 *         400 si hay un error.
	 */
	@DeleteMapping("/eliminarporid")
	public ResponseEntity<String> eliminarFranjaHorariaPorId(@RequestParam Long franjaHorariaId) {
		try {
			int status = franjaSer.deleteById(franjaHorariaId);
			return (status == 0) ? new ResponseEntity<>("Franja horaria eliminada exitosamente", HttpStatus.ACCEPTED)
					: new ResponseEntity<>("Error al eliminar, franja horaria no encontrada", HttpStatus.NOT_FOUND);
		} catch (Exception e) {
			return new ResponseEntity<>("Error al eliminar la franja horaria: " + e.getMessage(),
					HttpStatus.BAD_REQUEST);
		}
	}

	/**
	 * Elimina las franjas horarias de un día de la semana.
	 *
	 * @param dia día de la semana.
	 * @return mensaje de confirmación con estado 202, 404 si no hay franjas ese día
	 *         o 400 si hay un error.
	 */
	@DeleteMapping("/eliminarpordia")
	public ResponseEntity<String> eliminarFranjaHorariaPorDia(@RequestParam Dia dia) {
		try {
			int status = franjaSer.deleteByDia(dia);
			return (status == 0) ? new ResponseEntity<>("Franja horaria eliminada exitosamente", HttpStatus.ACCEPTED)
					: new ResponseEntity<>("Error al eliminar, franja horaria no encontrada", HttpStatus.NOT_FOUND);
		} catch (Exception e) {
			return new ResponseEntity<>("Error al eliminar la franja horaria: " + e.getMessage(),
					HttpStatus.BAD_REQUEST);
		}
	}

	/**
	 * Recupera todas las franjas horarias registradas en el sistema.
	 *
	 * @return lista de franjas con estado 202.
	 */
	@GetMapping("/mostrartodo")
	public ResponseEntity<List<FranjaHorariaDTO>> mostrarTodo() {
		List<FranjaHorariaDTO> lista = franjaSer.getAll();
		return new ResponseEntity<>(lista, HttpStatus.ACCEPTED);
	}

}