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
import co.edu.unbosque.horarios.dto.GrupoDTO;
import co.edu.unbosque.horarios.dto.ProfesorDTO;
import co.edu.unbosque.horarios.service.GrupoService;
import co.edu.unbosque.horarios.util.enums.EstadoGrupo;

@RestController
@RequestMapping("/grupo")
@CrossOrigin(origins = {"*"})
public class GrupoController {
	
	@Autowired
	private GrupoService grupoSer;
	
	public GrupoController() {
		// TODO Auto-generated constructor stub
	}
	
	@GetMapping("/buscarporasignatura")
	public ResponseEntity<List<GrupoDTO>> buscarPorAsignatura(@RequestBody AsignaturaDTO asignaturaDTO) {
		List<GrupoDTO> lista = new ArrayList<>();
		try {
			lista = grupoSer.findByAsignatura(asignaturaDTO);
			return lista.isEmpty() ? new ResponseEntity<>(lista, HttpStatus.NO_CONTENT)
				: new ResponseEntity<>(lista, HttpStatus.OK);
		} catch (Exception e) {
			return new ResponseEntity<List<GrupoDTO>>(lista, HttpStatus.BAD_REQUEST);
		}
	}
	
	@GetMapping("/buscarporestado")
	public ResponseEntity<List<GrupoDTO>> buscarPorEstado(@RequestBody EstadoGrupo estadoGrupo) {
		List<GrupoDTO> lista = new ArrayList<>();
		try {
			lista = grupoSer.findByEstado(estadoGrupo);
			return lista.isEmpty() ? new ResponseEntity<>(lista, HttpStatus.NO_CONTENT)
				: new ResponseEntity<>(lista, HttpStatus.OK);
		} catch (Exception e) {
			return new ResponseEntity<List<GrupoDTO>>(lista, HttpStatus.BAD_REQUEST);
		}
	}
	
	@GetMapping("/buscarpornumero")
	public ResponseEntity<List<GrupoDTO>> buscarPorNumeroGrupo(@RequestBody int numeroGrupo) {
		List<GrupoDTO> lista = new ArrayList<>();
		try {
			lista = grupoSer.findByNumeroGrupo(numeroGrupo);
			return lista.isEmpty() ? new ResponseEntity<>(lista, HttpStatus.NO_CONTENT)
				: new ResponseEntity<>(lista, HttpStatus.OK);
		} catch (Exception e) {
			return new ResponseEntity<List<GrupoDTO>>(lista, HttpStatus.BAD_REQUEST);
		}
	}
	
	@GetMapping("/buscarporprofesor")
	public ResponseEntity<List<GrupoDTO>> buscarPorProfesor(@RequestBody ProfesorDTO profesorDTO) {
		List<GrupoDTO> lista = new ArrayList<>();
		try {
			lista = grupoSer.findByProfesor(profesorDTO);
			return lista.isEmpty() ? new ResponseEntity<>(lista, HttpStatus.NO_CONTENT)
				: new ResponseEntity<>(lista, HttpStatus.OK);
		} catch (Exception e) {
			return new ResponseEntity<List<GrupoDTO>>(lista, HttpStatus.BAD_REQUEST);
		}
	}
	
	@PostMapping("/crear")
	public ResponseEntity<String> crearGrupo(@RequestBody GrupoDTO grupoDTO) {
		try {
			grupoSer.create(grupoDTO);
			return new ResponseEntity<>("Grupo creado con éxito", HttpStatus.CREATED);
		} catch (Exception e) {
			return new ResponseEntity<>("Error al crear el grupo: " + e.getMessage(), HttpStatus.BAD_REQUEST);
		}
	}
	
	@PutMapping("/actualizar")
	public ResponseEntity<String> actualizarGrupo(@RequestBody GrupoDTO grupoDTO) {
		try {
			int status = grupoSer.updateById(grupoDTO.getId(), grupoDTO);
			return (status == 0) ? new ResponseEntity<>("Grupo actualizado exitosamente", HttpStatus.ACCEPTED)
					: new ResponseEntity<>("Grupo no encontrado o error al actualizar", HttpStatus.NOT_FOUND);
		} catch (Exception e) {
			return new ResponseEntity<>("Error al actualizar el grupo: " + e.getMessage(), HttpStatus.BAD_REQUEST);
		}
	}
	
	@DeleteMapping("/eliminarporid")
	public ResponseEntity<String> eliminaGrupoPorId(@RequestParam Long grupoId) {
		try {
			int status = grupoSer.deleteById(grupoId);
			return (status == 0) ? new ResponseEntity<>("Grupo eliminado exitosamente", HttpStatus.ACCEPTED)
				: new ResponseEntity<>("Error al eliminar, grupo no encontrado", HttpStatus.NOT_FOUND);
		} catch (Exception e) {
			return new ResponseEntity<>("Error al eliminar el grupo: " + e.getMessage(), HttpStatus.BAD_REQUEST);
		}
	}
	
	@DeleteMapping("/eliminarpornumero")
	public ResponseEntity<String> eliminarGrupoPorNumero(@RequestParam int numeroGrupo) {
		try {
			int status = grupoSer.deleteByNumeroGrupo(numeroGrupo);
			return (status == 0) ? new ResponseEntity<>("Grupo eliminado exitosamente", HttpStatus.ACCEPTED)
				: new ResponseEntity<>("Error al eliminar, grupo no encontrado", HttpStatus.NOT_FOUND);
		} catch (Exception e) {
			return new ResponseEntity<>("Error al eliminar el grupo: " + e.getMessage(), HttpStatus.BAD_REQUEST);
		}
	}
	
	@GetMapping("/mostrartodo")
	public ResponseEntity<List<GrupoDTO>> mostrarTodo() {
		List<GrupoDTO> lista = grupoSer.getAll();
		return new ResponseEntity<>(lista, HttpStatus.ACCEPTED);
	}

}
