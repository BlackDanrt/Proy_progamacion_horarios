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
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import co.edu.unbosque.horarios.dto.FranjaHorariaDTO;
import co.edu.unbosque.horarios.dto.ProfesorDTO;
import co.edu.unbosque.horarios.service.FranjaHorariaService;
import co.edu.unbosque.horarios.service.ProfesorService;
import co.edu.unbosque.horarios.util.enums.Dia;
import co.edu.unbosque.horarios.util.enums.Escalafon;
import co.edu.unbosque.horarios.util.enums.Especialidad;
import co.edu.unbosque.horarios.util.enums.TipoVinculacion;

/**
 * Controlador REST que gestiona las operaciones relacionadas con los profesores
 * del sistema de horarios.
 * <p>
 * Proporciona endpoints para consultar, crear, actualizar y eliminar
 * profesores, así como para administrar su disponibilidad horaria.
 * </p>
 *
 * @author Prisma Web
 * @version 1.0
 */
@RestController
@RequestMapping("/profesor")
@CrossOrigin(origins = { "http://localhost:8080", "*" })
public class ProfesorController {

	/**
	 * Servicio que contiene la lógica de negocio para la gestión de profesores.
	 */
	@Autowired
	private ProfesorService profesorSer;

	/**
	 * Servicio que contiene la lógica de negocio para la gestión de franjas
	 * horarias.
	 */
	@Autowired
	private FranjaHorariaService franjaSer;

	/**
	 * Constructor vacío de la clase.
	 */
	public ProfesorController() {
		// TODO Auto-generated constructor stub
	}

	/**
	 * Busca un profesor por su correo electrónico.
	 *
	 * @param correo correo electrónico del profesor.
	 * @return el profesor con estado 200, 204 si no existe o 400 si hay un error.
	 */
	@GetMapping("/buscarporcorreo")
	public ResponseEntity<ProfesorDTO> buscarPorCorreo(@RequestParam String correo) {
		ProfesorDTO profesorDTO = new ProfesorDTO();
		try {
			profesorDTO = profesorSer.findByCorreo(correo);
			return profesorDTO == null ? new ResponseEntity<>(profesorDTO, HttpStatus.NO_CONTENT)
					: new ResponseEntity<>(profesorDTO, HttpStatus.OK);
		} catch (Exception e) {
			return new ResponseEntity<ProfesorDTO>(profesorDTO, HttpStatus.BAD_REQUEST);
		}
	}

	/**
	 * Busca un profesor por su número de documento.
	 *
	 * @param documento número de documento del profesor.
	 * @return el profesor con estado 200, 204 si no existe o 400 si hay un error.
	 */
	@GetMapping("/buscarpordocumento")
	public ResponseEntity<ProfesorDTO> buscarPorDocumento(@RequestParam long documento) {
		ProfesorDTO profesorDTO = new ProfesorDTO();
		try {
			profesorDTO = profesorSer.findByDocumento(documento);
			return profesorDTO == null ? new ResponseEntity<>(profesorDTO, HttpStatus.NO_CONTENT)
					: new ResponseEntity<>(profesorDTO, HttpStatus.OK);
		} catch (Exception e) {
			return new ResponseEntity<ProfesorDTO>(profesorDTO, HttpStatus.BAD_REQUEST);
		}
	}

	/**
	 * Busca los profesores disponibles en una franja horaria. Un profesor se
	 * considera disponible si alguna de sus franjas del mismo día cubre por
	 * completo la franja buscada.
	 *
	 * @param dia        día de la semana.
	 * @param horaInicio hora de inicio de la franja.
	 * @param horaFin    hora de fin de la franja.
	 * @return lista de profesores con estado 200, 204 si no hay resultados o 400 si
	 *         hay un error.
	 */
	@GetMapping("/buscarpordisponibilidad")
	public ResponseEntity<List<ProfesorDTO>> buscarPorDisponibilidad(@RequestParam Dia dia,
			@RequestParam LocalTime horaInicio, @RequestParam LocalTime horaFin) {
		List<ProfesorDTO> lista = new ArrayList<>();
		try {
			FranjaHorariaDTO franjaHorariaDTO = new FranjaHorariaDTO(dia, horaInicio, horaFin);
			lista = profesorSer.findByDisponibilidad(franjaHorariaDTO);
			return lista.isEmpty() ? new ResponseEntity<>(lista, HttpStatus.NO_CONTENT)
					: new ResponseEntity<>(lista, HttpStatus.OK);
		} catch (Exception e) {
			return new ResponseEntity<List<ProfesorDTO>>(lista, HttpStatus.BAD_REQUEST);
		}
	}

	/**
	 * Busca los profesores por su escalafón académico.
	 *
	 * @param escalafon escalafón académico.
	 * @return lista de profesores con estado 200, 204 si no hay resultados o 400 si
	 *         hay un error.
	 */
	@GetMapping("/buscarporescalafon")
	public ResponseEntity<List<ProfesorDTO>> buscarPorEscalafon(@RequestParam Escalafon escalafon) {
		List<ProfesorDTO> lista = new ArrayList<>();
		try {
			lista = profesorSer.findByEscalafon(escalafon);
			return lista.isEmpty() ? new ResponseEntity<>(lista, HttpStatus.NO_CONTENT)
					: new ResponseEntity<>(lista, HttpStatus.OK);
		} catch (Exception e) {
			return new ResponseEntity<List<ProfesorDTO>>(lista, HttpStatus.BAD_REQUEST);
		}
	}

	/**
	 * Busca los profesores que tienen una especialidad determinada.
	 *
	 * @param especialidad especialidad a buscar.
	 * @return lista de profesores con estado 200, 204 si no hay resultados o 400 si
	 *         hay un error.
	 */
	@GetMapping("/buscarporespecialidad")
	public ResponseEntity<List<ProfesorDTO>> buscarPorEspecialidad(@RequestParam Especialidad especialidad) {
		List<ProfesorDTO> lista = new ArrayList<>();
		try {
			lista = profesorSer.findByEspecialidad(especialidad);
			return lista.isEmpty() ? new ResponseEntity<>(lista, HttpStatus.NO_CONTENT)
					: new ResponseEntity<>(lista, HttpStatus.OK);
		} catch (Exception e) {
			return new ResponseEntity<List<ProfesorDTO>>(lista, HttpStatus.BAD_REQUEST);
		}
	}

	/**
	 * Busca los profesores por su tipo de vinculación laboral.
	 *
	 * @param tipoVinculacion tipo de vinculación laboral.
	 * @return lista de profesores con estado 200, 204 si no hay resultados o 400 si
	 *         hay un error.
	 */
	@GetMapping("/buscarporvinculacion")
	public ResponseEntity<List<ProfesorDTO>> buscarPorTipoVBinculacion(@RequestParam TipoVinculacion tipoVinculacion) {
		List<ProfesorDTO> lista = new ArrayList<>();
		try {
			lista = profesorSer.findByTipoVinculacion(tipoVinculacion);
			return lista.isEmpty() ? new ResponseEntity<>(lista, HttpStatus.NO_CONTENT)
					: new ResponseEntity<>(lista, HttpStatus.OK);
		} catch (Exception e) {
			return new ResponseEntity<List<ProfesorDTO>>(lista, HttpStatus.BAD_REQUEST);
		}
	}

	/**
	 * Recupera todos los profesores registrados en el sistema.
	 *
	 * @return lista de profesores con estado 202 o 400 si hay un error.
	 */
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

	/**
	 * Crea un nuevo profesor. La disponibilidad horaria se asigna después con el
	 * endpoint correspondiente.
	 *
	 * @param nombre                nombre del profesor.
	 * @param apellido              apellido del profesor.
	 * @param correo                correo electrónico del profesor.
	 * @param documento             número de documento del profesor.
	 * @param contrasenia           contraseña del profesor.
	 * @param tipoVinculacion       tipo de vinculación laboral.
	 * @param escalafon             escalafón académico.
	 * @param especialidades        lista de especialidades del profesor.
	 * @param horasMaximasSemanales máximo de horas de trabajo por semana.
	 * @return mensaje de confirmación con estado 201 o el error con estado 400.
	 */
	@PostMapping("/crear")
	public ResponseEntity<String> crearProfesor(@RequestParam String nombre, @RequestParam String apellido,
			@RequestParam String correo, @RequestParam long documento, @RequestParam String contrasenia,
			@RequestParam TipoVinculacion tipoVinculacion, @RequestParam Escalafon escalafon,
			@RequestParam List<Especialidad> especialidades, @RequestParam int horasMaximasSemanales) {
		try {
			ProfesorDTO profesorDTO = new ProfesorDTO();
			profesorDTO.setNombre(nombre);
			profesorDTO.setApellido(apellido);
			profesorDTO.setCorreo(correo);
			profesorDTO.setDocumento(documento);
			profesorDTO.setContrasenia(contrasenia);
			profesorDTO.setTipoVinculacion(tipoVinculacion);
			profesorDTO.setEscalafon(escalafon);
			profesorDTO.setEspecialidades(especialidades);
			profesorDTO.setHorasMaximasSemanales(horasMaximasSemanales);
			profesorDTO.setDisponibilidadHoraria(null);

			profesorSer.create(profesorDTO);
			return new ResponseEntity<>("Profesor creado con éxito", HttpStatus.CREATED);
		} catch (Exception e) {
			return new ResponseEntity<>("Error al crear el profesor: " + e.getMessage(), HttpStatus.BAD_REQUEST);
		}
	}

	/**
	 * Agrega una nueva franja de disponibilidad horaria a un profesor.
	 *
	 * @param idProfesor identificador del profesor.
	 * @param dia        día de la semana de la franja.
	 * @param horaInicio hora de inicio de la franja.
	 * @param horaFin    hora de fin de la franja.
	 * @return mensaje de confirmación con estado 201, 404 si el profesor no existe
	 *         o 400 si hay un error.
	 */
	@PutMapping("/asignardisponibilidadhoraria")
	public ResponseEntity<String> asignarDisponibilidadHoraria(@RequestParam long idProfesor, @RequestParam Dia dia,
			@RequestParam LocalTime horaInicio, @RequestParam LocalTime horaFin) {

		try {
			boolean asignado = profesorSer.asignarDisponibilidadHoraria(idProfesor, dia, horaInicio, horaFin);
			if (asignado) {
				return new ResponseEntity<>("Disponibilidad horaria agregada con éxito", HttpStatus.CREATED);
			} else {
				return new ResponseEntity<>("No existe ningún profesor con el ID ingresado", HttpStatus.NOT_FOUND);
			}
		} catch (Exception e) {
			return new ResponseEntity<>("Error al asignar la disponibilidad horaria: " + e.getMessage(),
					HttpStatus.BAD_REQUEST);
		}

	}

	/**
	 * Actualiza la información de un profesor. Conserva la disponibilidad horaria
	 * que el profesor ya tiene registrada.
	 *
	 * @param id                    identificador del profesor.
	 * @param nombre                nuevo nombre.
	 * @param apellido              nuevo apellido.
	 * @param documento             nuevo número de documento.
	 * @param correo                nuevo correo electrónico.
	 * @param contrasenia           nueva contraseña.
	 * @param tipoVinculacion       nuevo tipo de vinculación.
	 * @param escalafon             nuevo escalafón.
	 * @param especialidades        nueva lista de especialidades.
	 * @param horasMaximasSemanales nuevo máximo de horas semanales.
	 * @return mensaje de confirmación con estado 202, 404 si el profesor no existe
	 *         o 400 si hay un error.
	 */
	@PutMapping("/actualizarinformacion")
	public ResponseEntity<String> actualizarInformacionProfesor(@RequestParam long id, @RequestParam String nombre,
			@RequestParam String apellido, @RequestParam long documento, @RequestParam String correo,
			@RequestParam String contrasenia, @RequestParam TipoVinculacion tipoVinculacion,
			@RequestParam Escalafon escalafon, @RequestParam List<Especialidad> especialidades,
			@RequestParam int horasMaximasSemanales) {
		try {
			ProfesorDTO profesor = new ProfesorDTO();
			profesor.setId(id);
			profesor.setNombre(nombre);
			profesor.setApellido(apellido);
			profesor.setDocumento(documento);
			profesor.setCorreo(correo);
			profesor.setContrasenia(contrasenia);
			profesor.setTipoVinculacion(tipoVinculacion);
			profesor.setEscalafon(escalafon);
			profesor.setEspecialidades(especialidades);
			profesor.setHorasMaximasSemanales(horasMaximasSemanales);

			int status = profesorSer.updateById(id, profesor);
			return (status == 0) ? new ResponseEntity<>("Profesor actualizado exitosamente", HttpStatus.ACCEPTED)
					: new ResponseEntity<>("Profesor no encontrado", HttpStatus.NOT_FOUND);
		} catch (Exception e) {
			return new ResponseEntity<>("Error al actualizar el profesor: " + e.getMessage(), HttpStatus.BAD_REQUEST);
		}
	}

	/**
	 * Actualiza una franja de disponibilidad horaria de un profesor.
	 *
	 * @param idProfesor      identificador del profesor.
	 * @param idFranjaHoraria identificador de la franja a modificar.
	 * @param dia             nuevo día de la semana.
	 * @param horaInicio      nueva hora de inicio.
	 * @param horaFin         nueva hora de fin.
	 * @return mensaje de confirmación con estado 202, 404 si el profesor o la
	 *         franja no existen o 400 si hay un error.
	 */
	@PutMapping("/actualizardisponibilidadhoraria")
	public ResponseEntity<String> actualizarDisponibilidadHorariaProfesor(@RequestParam long idProfesor,
			@RequestParam long idFranjaHoraria, @RequestParam Dia dia, @RequestParam LocalTime horaInicio,
			@RequestParam LocalTime horaFin

	) {
		try {
			FranjaHorariaDTO franjaHoraria = new FranjaHorariaDTO();
			franjaHoraria.setId(idFranjaHoraria);
			franjaHoraria.setDia(dia);
			franjaHoraria.setHoraInicio(horaInicio);
			franjaHoraria.setHoraFin(horaFin);

			franjaSer.updateById(idFranjaHoraria, franjaHoraria);
			boolean actualizado = profesorSer.updateDisponibilidadHoraria(idProfesor, franjaHoraria);

			if (actualizado) {
				return new ResponseEntity<>("Disponibilildad horaria actualizada exitosamente", HttpStatus.ACCEPTED);
			} else {
				return new ResponseEntity<>("Profesor o disponibilidad horaria no encontrados ", HttpStatus.NOT_FOUND);
			}

		} catch (Exception e) {
			return new ResponseEntity<>("Error al actualizar la disponibilidad horaria: " + e.getMessage(),
					HttpStatus.BAD_REQUEST);
		}

	}

	/**
	 * Elimina un profesor por su identificador.
	 *
	 * @param id identificador del profesor.
	 * @return mensaje de confirmación con estado 202, 404 si el profesor no existe
	 *         o 400 si hay un error.
	 */
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