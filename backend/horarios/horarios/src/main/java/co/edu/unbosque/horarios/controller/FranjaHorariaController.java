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

@RestController
@RequestMapping("/franjahoraria")
@CrossOrigin(origins = {"*"})
public class FranjaHorariaController {

	@Autowired
	private FranjaHorariaService franjaSer;
	
	public FranjaHorariaController() {
		// TODO Auto-generated constructor stub
	}
	
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
	
	@GetMapping("/buscarporhorafin")
	public ResponseEntity<List<FranjaHorariaDTO>> buscarPorHoraFin(@RequestParam LocalTime horaFin) {
		List<FranjaHorariaDTO> lista = new ArrayList<>();
		try {
			lista = franjaSer.findByHoraInicio(horaFin);
			return lista.isEmpty() ? new ResponseEntity<>(lista, HttpStatus.NO_CONTENT)
				: new ResponseEntity<>(lista, HttpStatus.OK);
		} catch (Exception e) {
			return new ResponseEntity<List<FranjaHorariaDTO>>(lista, HttpStatus.BAD_REQUEST);
		}
	}
	
	@PostMapping("/crear")
	public ResponseEntity<String> crearFranjaHoraria(@RequestBody FranjaHorariaDTO franjaHorariaDTO) {
		try {
			franjaSer.create(franjaHorariaDTO);
			return new ResponseEntity<>("Franja horaria creada con éxito", HttpStatus.CREATED);
		} catch (Exception e) {
			return new ResponseEntity<>("Error al crear la franja horaria: " + e.getMessage(), HttpStatus.BAD_REQUEST);
		}
	}
	
	@PutMapping("/actualizar")
	public ResponseEntity<String> actualizarFranjaHoraria(@RequestBody FranjaHorariaDTO franjaHorariaDTO) {
		try {
			int status = franjaSer.updateById(franjaHorariaDTO.getId(), franjaHorariaDTO);
			return (status == 0) ? new ResponseEntity<>("Franja horaria actualizada exitosamente", HttpStatus.ACCEPTED)
					: new ResponseEntity<>("Franja horaria no encontrada o error al actualizar", HttpStatus.NOT_FOUND);
		} catch (Exception e) {
			return new ResponseEntity<>("Error al actualizar la franja horaria: " + e.getMessage(), HttpStatus.BAD_REQUEST);
		}
	}
	
	@DeleteMapping("/eliminarporid")
	public ResponseEntity<String> eliminarFranjaHorariaPorId(@RequestParam Long franjaHorariaId) {
		try {
			int status = franjaSer.deleteById(franjaHorariaId);
			return (status == 0) ? new ResponseEntity<>("Franja horaria eliminada exitosamente", HttpStatus.ACCEPTED)
				: new ResponseEntity<>("Error al eliminar, franja horaria no encontrada", HttpStatus.NOT_FOUND);
		} catch (Exception e) {
			return new ResponseEntity<>("Error al eliminar la franja horaria: " + e.getMessage(), HttpStatus.BAD_REQUEST);
		}
	}
	
	@DeleteMapping("/eliminarpordia")
	public ResponseEntity<String> eliminarFranjaHorariaPorDia(@RequestParam Dia dia) {
		try {
			int status = franjaSer.deleteByDia(dia);
			return (status == 0) ? new ResponseEntity<>("Franja horaria eliminada exitosamente", HttpStatus.ACCEPTED)
				: new ResponseEntity<>("Error al eliminar, franja horaria no encontrada", HttpStatus.NOT_FOUND);
		} catch (Exception e) {
			return new ResponseEntity<>("Error al eliminar la franja horaria: " + e.getMessage(), HttpStatus.BAD_REQUEST);
		}
	}
	
	@GetMapping("/mostrartodo")
	public ResponseEntity<List<FranjaHorariaDTO>> mostrarTodo() {
		List<FranjaHorariaDTO> lista = franjaSer.getAll();
		return new ResponseEntity<>(lista, HttpStatus.ACCEPTED);
	}
	
}
