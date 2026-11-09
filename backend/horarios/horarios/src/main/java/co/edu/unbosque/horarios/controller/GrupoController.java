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
@CrossOrigin(origins = { "http://localhost:8080", "https://app.netlify.com/projects/prismaweb/" })
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
	 * Crea un nuevo grupo a partir de un GrupoDTO en el cuerpo de la petición
	 * (asignatura, profesor y salón por id, y sus sesiones con día y horas).
	 *
	 * @param grupoDTO datos del grupo.
	 * @return 201 si se creó, o 400 con el mensaje de la regla de negocio que
	 *         falló.
	 */
	@PostMapping("/crear")
	public ResponseEntity<String> crearGrupo(@RequestBody GrupoDTO grupoDTO) {
		try {
			int resultado = grupoSer.create(grupoDTO);
			return resultado == 0 ? new ResponseEntity<>("Grupo creado con éxito", HttpStatus.CREATED)
					: new ResponseEntity<>("Error desconocido al crear el grupo", HttpStatus.INTERNAL_SERVER_ERROR);
		} catch (Exception e) {
			return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
		}
	}

	/**
	 * Crea un nuevo grupo recibiendo los datos como parámetros sueltos (versión
	 * anterior).
	 *
	 * @param profesorId      id del profesor.
	 * @param asignaturaId    id de la asignatura.
	 * @param numeroGrupo     número del grupo.
	 * @param capacidadMinima capacidad mínima.
	 * @param capacidadMaxima capacidad máxima.
	 * @param salonId         id del salón.
	 * @param estado          estado del grupo.
	 * @param periodo         periodo académico.
	 * @param inscritos       cantidad de estudiantes inscritos.
	 * @param franjaIds       ids de las franjas horarias (opcional).
	 * @return mensaje de confirmación.
	 */
	@PostMapping("/crearporids")
	public ResponseEntity<String> crearGrupoPorIds(@RequestParam long profesorId, @RequestParam long asignaturaId,
			@RequestParam int numeroGrupo, @RequestParam int capacidadMinima, @RequestParam int capacidadMaxima,
			@RequestParam long salonId, @RequestParam EstadoGrupo estado, @RequestParam String periodo,
			@RequestParam int inscritos, @RequestParam(required = false) List<Long> franjaIds) {

		int resultado;
		try {
			resultado = grupoSer.create(profesorId, asignaturaId, numeroGrupo, capacidadMinima, capacidadMaxima,
					salonId, estado, periodo, inscritos, franjaIds);
		} catch (RuntimeException e) {
			return new ResponseEntity<>("Los datos enviados no son válidos: " + e.getMessage(), HttpStatus.BAD_REQUEST);
		}

		switch (resultado) {
		case 0:
			return new ResponseEntity<>("Grupo creado con éxito", HttpStatus.CREATED);
		case -1:
			return new ResponseEntity<>("No existe un profesor con ese id", HttpStatus.NOT_FOUND);
		case -2:
			return new ResponseEntity<>("No existe la asignatura indicada", HttpStatus.NOT_FOUND);
		case -3:
			return new ResponseEntity<>("No existe un salón con ese id", HttpStatus.NOT_FOUND);
		case -4:
			return new ResponseEntity<>("Los datos enviados no son válidos", HttpStatus.BAD_REQUEST);
		case -5:
			return new ResponseEntity<>("Una o más franjas horarias no existen", HttpStatus.NOT_FOUND);
		default:
			return new ResponseEntity<>("Error desconocido al crear el grupo", HttpStatus.INTERNAL_SERVER_ERROR);
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