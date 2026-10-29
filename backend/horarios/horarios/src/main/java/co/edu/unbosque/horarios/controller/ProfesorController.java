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

import co.edu.unbosque.horarios.dto.AsignaturaDTO;
import co.edu.unbosque.horarios.dto.FranjaHorariaDTO;
import co.edu.unbosque.horarios.dto.ProfesorDTO;
import co.edu.unbosque.horarios.service.FranjaHorariaService;
import co.edu.unbosque.horarios.service.ProfesorService;
import co.edu.unbosque.horarios.util.LanzadorDeException;
import co.edu.unbosque.horarios.util.enums.Dia;
import co.edu.unbosque.horarios.util.enums.Escalafon;
import co.edu.unbosque.horarios.util.enums.Especialidad;
import co.edu.unbosque.horarios.util.enums.TipoVinculacion;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.media.Content;

@RestController
@RequestMapping("/profesor")
@CrossOrigin(origins = { "http://localhost:8080", "*" })
public class ProfesorController {

	@Autowired
	private ProfesorService profesorSer;
	
	@Autowired
	private FranjaHorariaService franjaSer;
	
	public ProfesorController() {
		// TODO Auto-generated constructor stub
	}
	
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
	
	@GetMapping("/buscarpordisponibilidad")
	public ResponseEntity<List<ProfesorDTO>> buscarPorDisponibilidad(@RequestParam FranjaHorariaDTO franjaHorariaDTO) {
		List<ProfesorDTO> lista = new ArrayList<>();
		try {
			lista = profesorSer.findByDisponibilidad(franjaHorariaDTO);
			return lista.isEmpty() ? new ResponseEntity<>(lista, HttpStatus.NO_CONTENT)
				: new ResponseEntity<>(lista, HttpStatus.OK);
		} catch (Exception e) {
			return new ResponseEntity<List<ProfesorDTO>>(lista, HttpStatus.BAD_REQUEST);
		}
	}
	
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
	
	@GetMapping("/buscarporespecialidad")
	public ResponseEntity<List<ProfesorDTO>> buscarPorEspecialidad(@RequestParam AsignaturaDTO asignaturaDTO) {
		List<ProfesorDTO> lista = new ArrayList<>();
		try {
			lista = profesorSer.findByEspecialidad(asignaturaDTO);
			return lista.isEmpty() ? new ResponseEntity<>(lista, HttpStatus.NO_CONTENT)
				: new ResponseEntity<>(lista, HttpStatus.OK);
		} catch (Exception e) {
			return new ResponseEntity<List<ProfesorDTO>>(lista, HttpStatus.BAD_REQUEST);
		}
	}
	
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
	public ResponseEntity<String> crearProfesor(
	        @RequestParam String nombre,
	        @RequestParam String apellido,
	        @RequestParam String correo,
	        @RequestParam long documento,
	        @RequestParam String contrasenia,
	        @RequestParam TipoVinculacion tipoVinculacion,
	        @RequestParam Escalafon escalafon,
	        @RequestParam List<Especialidad> especialidades,
	        @RequestParam int horasMaximasSemanales) {
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
	        return new ResponseEntity<>("Error al crear el profesor: " + e.getMessage(),
	                HttpStatus.BAD_REQUEST);
	    }
	}
	
	@PutMapping("/asignardisponibilidadhoraria")
	public ResponseEntity<String> asignarDisponibilidadHoraria(
			@RequestParam long idProfesor, 
			@RequestParam Dia dia, 
			@RequestParam LocalTime horaInicio, 
			@RequestParam LocalTime horaFin) {
		
		try {
			boolean asignado = profesorSer.asignarDisponibilidadHoraria(idProfesor, dia, horaInicio, horaFin);
			if(asignado) {
				return new ResponseEntity<>("Disponibilidad horaria agregada con éxito", HttpStatus.CREATED);
			} else {
				return new ResponseEntity<>("No existe ningún profesor con el ID ingresado", HttpStatus.NOT_FOUND);
			}
		} catch (Exception e) {
			return new ResponseEntity<>("Error al asignar la disponibilidad horaria: " + e.getMessage(), HttpStatus.BAD_REQUEST);
		}
	
	}
	
	
	@PutMapping("/actualizarinformacion")
	public ResponseEntity<String> actualizarInformacionProfesor(
			@RequestParam long id,
			@RequestParam String nombre,
			@RequestParam String apellido,
			@RequestParam long documento,
			@RequestParam String correo,
			@RequestParam String contrasenia,
			@RequestParam TipoVinculacion tipoVinculacion,
			@RequestParam Escalafon escalafon,
			@RequestParam List<Especialidad> especialidades,
			@RequestParam int horasMaximasSemanales
			) {
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
			return (status == 1) ? new ResponseEntity<>("Profesor actualizado exitosamente", HttpStatus.ACCEPTED)
					: new ResponseEntity<>("Profesor no encontrado", HttpStatus.NOT_FOUND);
		} catch (Exception e) {
			return new ResponseEntity<>("Error al actualizar el profesor: " + e.getMessage(), HttpStatus.BAD_REQUEST);
		}
	}
	
	@PutMapping("/actualizardisponibilidadhoraria")
	public ResponseEntity<String> actualizarDisponibilidadHorariaProfesor(
			@RequestParam long idProfesor,
			@RequestParam long idFranjaHoraria,
			@RequestParam Dia dia,
			@RequestParam LocalTime horaInicio,
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
			
		} catch(Exception e) {
			return new ResponseEntity<>("Error al actualizar la disponibilidad horaria: " + e.getMessage(), HttpStatus.BAD_REQUEST);
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
