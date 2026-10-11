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

@RestController
@RequestMapping("/administrador")
@CrossOrigin(origins = {"*"})
public class AdministradorController {

	@Autowired
	private AdministradorService administradorSer;
	
	public AdministradorController() {
		// TODO Auto-generated constructor stub
	}
	
	@GetMapping("/buscarporcodigo")
	public ResponseEntity<List<AdministradorDTO>> buscarPorCodigoAdministrador(@RequestParam String codigoAdministrador) {
		List<AdministradorDTO> lista = new ArrayList<>();
		try {
			lista = administradorSer.findByCodigoAdministrador(codigoAdministrador);
			return lista.isEmpty() ? new ResponseEntity<>(lista, HttpStatus.NO_CONTENT)
				: new ResponseEntity<>(lista, HttpStatus.OK);
		} catch (Exception e) {
			return new ResponseEntity<List<AdministradorDTO>>(lista, HttpStatus.BAD_REQUEST);
		}
	}
	
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


	@PostMapping("/crear")
	public ResponseEntity<String> crearAdministrador(@RequestBody AdministradorDTO administradorDTO) {
		try {
			administradorSer.create(administradorDTO);
			return new ResponseEntity<>("Administrador creado con éxito", HttpStatus.CREATED);
		} catch (Exception e) {
			return new ResponseEntity<>("Error al crear el administrador: " + e.getMessage(), HttpStatus.BAD_REQUEST);
		}
	}

	@PutMapping("/actualizar")
	public ResponseEntity<String> actualizarAdministrador(@RequestBody AdministradorDTO administradorDTO) {
		try {
			int status = administradorSer.updateById(administradorDTO.getId(), administradorDTO);
			return (status == 0) ? new ResponseEntity<>("Administrador actualizado exitosamente", HttpStatus.ACCEPTED)
					: new ResponseEntity<>("Administrador no encontrado o error al actualizar", HttpStatus.NOT_FOUND);
		} catch (Exception e) {
			return new ResponseEntity<>("Error al actualizar el administrador: " + e.getMessage(), HttpStatus.BAD_REQUEST);
		}
	}


	@DeleteMapping("/eliminar")
	public ResponseEntity<String> eliminarAdministrador(@RequestParam Long id) {
		try {
			int status = administradorSer.deleteById(id);
			return (status == 0) ? new ResponseEntity<>("Administrador eliminado exitosamente", HttpStatus.ACCEPTED)
					: new ResponseEntity<>("Error al eliminar, administrador no encontrado", HttpStatus.NOT_FOUND);
		} catch (Exception e) {
			return new ResponseEntity<>("Error al eliminar el administrador: " + e.getMessage(), HttpStatus.BAD_REQUEST);
		}
	}
}
