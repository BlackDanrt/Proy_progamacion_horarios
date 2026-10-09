package co.edu.unbosque.horarios.controller;

import co.edu.unbosque.horarios.repository.FranjaHorariaRepository;
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

@RestController
@RequestMapping("/salon")
@CrossOrigin(origins = {"*"})
public class SalonController {

	@Autowired
	private SalonService salonSer;
	
	public SalonController() {
		// TODO Auto-generated constructor stub
	}
	
	@GetMapping("/buscarporbloque")
	public ResponseEntity<List<SalonDTO>> buscarPorBloque(@RequestParam Bloque bloque) {
		List<SalonDTO> lista = new ArrayList<>();
		try {
			lista = salonSer.findByBloque(bloque);
			return lista.isEmpty() ? new ResponseEntity<>(lista, HttpStatus.NO_CONTENT)
				: new ResponseEntity<>(lista, HttpStatus.ACCEPTED);
		} catch (Exception e) {
			return new ResponseEntity<List<SalonDTO>>(lista, HttpStatus.BAD_REQUEST);
		}
	}
	
	@GetMapping("/buscarporcapacidad")
	public ResponseEntity<List<SalonDTO>> buscarPorCapacidad(@RequestParam int capacidad) {
		List<SalonDTO> lista = new ArrayList<>();
		try {
			lista = salonSer.findByCapacidad(capacidad);
			return lista.isEmpty() ? new ResponseEntity<>(lista, HttpStatus.NO_CONTENT)
				: new ResponseEntity<>(lista, HttpStatus.ACCEPTED);
		} catch (Exception e) {
			return new ResponseEntity<List<SalonDTO>>(lista, HttpStatus.BAD_REQUEST);
		}
	}
	
	@GetMapping("/buscarporsillasmoviles")
	public ResponseEntity<List<SalonDTO>> buscarPorTieneSillasMoviles(@RequestParam boolean tieneSillasMoviles) {
		List<SalonDTO> lista = new ArrayList<>();
		try {
			lista = salonSer.findByTieneSillasMoviles(tieneSillasMoviles);
			return lista.isEmpty() ? new ResponseEntity<>(lista, HttpStatus.NO_CONTENT)
				: new ResponseEntity<>(lista, HttpStatus.ACCEPTED);
		} catch (Exception e) {
			return new ResponseEntity<List<SalonDTO>>(lista, HttpStatus.BAD_REQUEST);
		}
	}
	
	@GetMapping("/buscarporcomputadores")
	public ResponseEntity<List<SalonDTO>> buscarPorTieneComputadores(@RequestParam boolean tieneComputadores) {
		List<SalonDTO> lista = new ArrayList<>();
		try {
			lista = salonSer.findByTieneComputadores(tieneComputadores);
			return lista.isEmpty() ? new ResponseEntity<>(lista, HttpStatus.NO_CONTENT)
				: new ResponseEntity<>(lista, HttpStatus.ACCEPTED);
		} catch (Exception e) {
			return new ResponseEntity<List<SalonDTO>>(lista, HttpStatus.BAD_REQUEST);
		}
	}
	
	/* 
	 * @GetMapping("/buscarporbloque")
	public ResponseEntity<SalonDTO> buscarPorBloqueAndNumeroSalon(@RequestParam Bloque bloque, int numeroSalon) {
		List<SalonDTO> lista = new ArrayList<>();
		try {
			lista = salonSer.findByBloqueAndNumeroSalon(bloque, numeroSalon);
			return lista.isEmpty() ? new ResponseEntity<>(lista, HttpStatus.NO_CONTENT)
				: new ResponseEntity<>(lista, HttpStatus.ACCEPTED);
		} catch (Exception e) {
			return new ResponseEntity<List<SalonDTO>>(lista, HttpStatus.BAD_REQUEST);
		}
	}
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
	
	@DeleteMapping("/eliminar")
	public ResponseEntity<String> eliminarSalon(@RequestParam Bloque bloque, int numeroSalon) {
		try {
			int status = salonSer.deleteByBloqueAndNumeroSalon(bloque, numeroSalon);
			return (status == 0) ? new ResponseEntity<>("Salón eliminado exitosamente", HttpStatus.ACCEPTED)
				: new ResponseEntity<>("Error al eliminar, salón no encontrado", HttpStatus.NOT_FOUND);
		} catch (Exception e) {
			return new ResponseEntity<>("Error al eliminar el salón: " + e.getMessage(), HttpStatus.BAD_REQUEST);
		}
	}
	
	@GetMapping("/mostrartodo")
	public ResponseEntity<List<SalonDTO>> mostrarTodo() {
		List<SalonDTO> lista = salonSer.getAll();
		return new ResponseEntity<>(lista, HttpStatus.ACCEPTED);
	}
}
