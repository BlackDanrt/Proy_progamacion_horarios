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

@RestController
@RequestMapping("/asignatura")
@CrossOrigin(origins = {"*"})
public class AsignaturaController {
	
	@Autowired
	private AsignaturaService asignaturaSer;
	
	public AsignaturaController() {
		// TODO Auto-generated constructor stub
	}
	
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
	
	@PostMapping("/crear")
	public ResponseEntity<String> crearAsignatura(@RequestBody AsignaturaDTO asignaturaDTO) {
		try {
			asignaturaSer.create(asignaturaDTO);
			return new ResponseEntity<>("Asignatura creada con éxito", HttpStatus.CREATED);
		} catch (Exception e) {
			return new ResponseEntity<>("Error al crear la asignatura: " + e.getMessage(), HttpStatus.BAD_REQUEST);
		}
	}
	
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
	
	@GetMapping("/mostrartodo")
	public ResponseEntity<List<AsignaturaDTO>> mostrarTodo() {
		List<AsignaturaDTO> lista = asignaturaSer.getAll();
		return new ResponseEntity<>(lista, HttpStatus.ACCEPTED);
	}
	
}
