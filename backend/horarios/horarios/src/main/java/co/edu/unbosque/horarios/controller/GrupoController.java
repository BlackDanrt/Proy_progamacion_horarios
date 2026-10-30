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

/**
 * Controlador REST que gestiona las operaciones relacionadas con los grupos del
 * sistema de horarios.
 * <p>
 * Proporciona endpoints para consultar, crear, actualizar y eliminar grupos.
 * </p>
 *
 * @author Prisma Web
 * @version 1.0
 */
@RestController
@RequestMapping("/grupo")
@CrossOrigin(origins = { "http://localhost:8080", "*" })
public class GrupoController {

	/**
	 * Servicio que contiene la lógica de negocio para la gestión de grupos.
	 */
	@Autowired
	private GrupoService grupoSer;

	/**
	 * Constructor vacío de la clase.
	 */
	public GrupoController() {
		// TODO Auto-generated constructor stub
	}

	/**
	 * Busca los grupos de una asignatura.
	 *
	 * @param idAsignatura identificador de la asignatura.
	 * @return lista de grupos con estado 200, 204 si no hay resultados o 400 si hay
	 *         un error.
	 */
	@GetMapping("/buscarporasignatura")
	public ResponseEntity<List<GrupoDTO>> buscarPorAsignatura(@RequestParam long idAsignatura) {
		List<GrupoDTO> lista = new ArrayList<>();
		try {
			AsignaturaDTO asignaturaDTO = new AsignaturaDTO();
			asignaturaDTO.setId(idAsignatura);
			lista = grupoSer.findByAsignatura(asignaturaDTO);
			return lista.isEmpty() ? new ResponseEntity<>(lista, HttpStatus.NO_CONTENT)
					: new ResponseEntity<>(lista, HttpStatus.OK);
		} catch (Exception e) {
			return new ResponseEntity<List<GrupoDTO>>(lista, HttpStatus.BAD_REQUEST);
		}
	}

	/**
	 * Busca los grupos por su estado.
	 *
	 * @param estadoGrupo estado del grupo.
	 * @return lista de grupos con estado 200, 204 si no hay resultados o 400 si hay
	 *         un error.
	 */
	@GetMapping("/buscarporestado")
	public ResponseEntity<List<GrupoDTO>> buscarPorEstado(@RequestParam EstadoGrupo estadoGrupo) {
		List<GrupoDTO> lista = new ArrayList<>();
		try {
			lista = grupoSer.findByEstado(estadoGrupo);
			return lista.isEmpty() ? new ResponseEntity<>(lista, HttpStatus.NO_CONTENT)
					: new ResponseEntity<>(lista, HttpStatus.OK);
		} catch (Exception e) {
			return new ResponseEntity<List<GrupoDTO>>(lista, HttpStatus.BAD_REQUEST);
		}
	}

	/**
	 * Busca un grupo por su número.
	 *
	 * @param numeroGrupo número del grupo.
	 * @return lista con el grupo encontrado y estado 200, 204 si no existe o 400 si
	 *         hay un error.
	 */
	@GetMapping("/buscarpornumero")
	public ResponseEntity<List<GrupoDTO>> buscarPorNumeroGrupo(@RequestParam int numeroGrupo) {
		List<GrupoDTO> lista = new ArrayList<>();
		try {
			lista = grupoSer.findByNumeroGrupo(numeroGrupo);
			return lista.isEmpty() ? new ResponseEntity<>(lista, HttpStatus.NO_CONTENT)
					: new ResponseEntity<>(lista, HttpStatus.OK);
		} catch (Exception e) {
			return new ResponseEntity<List<GrupoDTO>>(lista, HttpStatus.BAD_REQUEST);
		}
	}

	/**
	 * Busca los grupos asignados a un profesor.
	 *
	 * @param idProfesor identificador del profesor.
	 * @return lista de grupos con estado 200, 204 si no hay resultados o 400 si hay
	 *         un error.
	 */
	@GetMapping("/buscarporprofesor")
	public ResponseEntity<List<GrupoDTO>> buscarPorProfesor(@RequestParam long idProfesor) {
		List<GrupoDTO> lista = new ArrayList<>();
		try {
			ProfesorDTO profesorDTO = new ProfesorDTO();
			profesorDTO.setId(idProfesor);
			lista = grupoSer.findByProfesor(profesorDTO);
			return lista.isEmpty() ? new ResponseEntity<>(lista, HttpStatus.NO_CONTENT)
					: new ResponseEntity<>(lista, HttpStatus.OK);
		} catch (Exception e) {
			return new ResponseEntity<List<GrupoDTO>>(lista, HttpStatus.BAD_REQUEST);
		}
	}

	/**
	 * Crea un nuevo grupo.
	 *
	 * @param grupoDTO datos del grupo a crear.
	 * @return mensaje de confirmación con estado 201 o el error con estado 400.
	 */
	@PostMapping("/crear")
	public ResponseEntity<String> crearGrupo(@RequestBody GrupoDTO grupoDTO) {
		try {
			grupoSer.create(grupoDTO);
			return new ResponseEntity<>("Grupo creado con éxito", HttpStatus.CREATED);
		} catch (Exception e) {
			return new ResponseEntity<>("Error al crear el grupo: " + e.getMessage(), HttpStatus.BAD_REQUEST);
		}
	}

	/**
	 * Actualiza un grupo existente.
	 *
	 * @param grupoDTO nuevos datos del grupo, incluido su identificador.
	 * @return mensaje de confirmación con estado 202, 404 si el grupo no existe o
	 *         400 si hay un error.
	 */
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

	/**
	 * Elimina un grupo por su identificador.
	 *
	 * @param grupoId identificador del grupo.
	 * @return mensaje de confirmación con estado 202, 404 si el grupo no existe o
	 *         400 si hay un error.
	 */
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

	/**
	 * Elimina un grupo por su número.
	 *
	 * @param numeroGrupo número del grupo.
	 * @return mensaje de confirmación con estado 202, 404 si el grupo no existe o
	 *         400 si hay un error.
	 */
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

	/**
	 * Recupera todos los grupos registrados en el sistema.
	 *
	 * @return lista de grupos con estado 202.
	 */
	@GetMapping("/mostrartodo")
	public ResponseEntity<List<GrupoDTO>> mostrarTodo() {
		List<GrupoDTO> lista = grupoSer.getAll();
		return new ResponseEntity<>(lista, HttpStatus.ACCEPTED);
	}

}