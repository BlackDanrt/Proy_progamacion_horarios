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

import co.edu.unbosque.horarios.dto.ProfesorDTO;
import co.edu.unbosque.horarios.service.ProfesorService;

@RestController
@RequestMapping("/profesor")
@CrossOrigin(origins = {"*"})
public class ProfesorController {

	@Autowired
	private ProfesorService profesorSer;
	
	public ProfesorController() {
		// TODO Auto-generated constructor stub
	}
	
	@GetMapping("/mostrartodo")
	public ResponseEntity<List<ProfesorDTO>> mostrarTodo() {
		List<ProfesorDTO> lista = new ArrayList<>();
		try {
			lista = profesorSer.getAll();
			return new ResponseEntity<>(lista, HttpStatus.ACCEPTED);
		} catch (Exception e) {
			return new ResponseEntity<>(lista, HttpStatus.BAD_REQUEST);
		}
	}


	@PostMapping("/crear")
	public ResponseEntity<String> crearProfesor(@RequestBody ProfesorDTO profesorDTO) {
		try {
			profesorSer.create(profesorDTO);
			return new ResponseEntity<>("Profesor creado con éxito", HttpStatus.CREATED);
		} catch (Exception e) {
			return new ResponseEntity<>("Error al crear el profesor: " + e.getMessage(), HttpStatus.BAD_REQUEST);
		}
	}

	@PutMapping("/actualizar")
	public ResponseEntity<String> actualizarProfesor(@RequestBody ProfesorDTO profesorDTO) {
		try {
			int status = profesorSer.updateById(profesorDTO.getId(), profesorDTO);
			return (status == 0) ? new ResponseEntity<>("Profesor actualizado exitosamente", HttpStatus.ACCEPTED)
					: new ResponseEntity<>("Profesor no encontrado o error al actualizar", HttpStatus.NOT_FOUND);
		} catch (Exception e) {
			return new ResponseEntity<>("Error al actualizar el profesor: " + e.getMessage(), HttpStatus.BAD_REQUEST);
		}
	}


	@DeleteMapping("/eliminar")
	public ResponseEntity<String> eliminarProfesor(@RequestParam Long id) {
		try {
			int status = profesorSer.deleteById(id);
			return (status == 0) ? new ResponseEntity<>("Profesor eliminado exitosamente", HttpStatus.ACCEPTED)
					: new ResponseEntity<>("Error al eliminar, profesor no encontrado", HttpStatus.NOT_FOUND);
		} catch (Exception e) {
			return new ResponseEntity<>("Error al eliminar el profesor: " + e.getMessage(), HttpStatus.BAD_REQUEST);
		}
	}
	
}
