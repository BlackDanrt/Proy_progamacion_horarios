package co.edu.unbosque.horarios.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import co.edu.unbosque.horarios.dto.AuditoriaDTO;
import co.edu.unbosque.horarios.service.AuditoriaService;

import org.springframework.web.bind.annotation.GetMapping;

/**
 * Controlador REST para gestionar las operaciones relacionadas con la auditoría
 * del sistema. Proporciona endpoints para consultar el historial de acciones y
 * eventos registrados.
 * @author Prisma Web
 * @version 1.0
 */
@RestController
@RequestMapping("/auditoria")
@CrossOrigin(origins = { "http://localhost:8080", "https://app.netlify.com/projects/prismaweb/" })
public class AuditoriaController {

	/** Servicio para la lógica de la auditoria. */
	@Autowired
	private AuditoriaService auditoriaService;

	/**
	 * Constructor por defecto del controlador.
	 */
	public AuditoriaController() {
		// TODO Auto-generated constructor stub
	}

	/**
	 * Obtiene la lista completa de registros de auditoría almacenados en el
	 * sistema. * @return Un {@link ResponseEntity} que contiene la lista de
	 * {@link AuditoriaDTO} con estado 200 OK, o un estado 204 NO CONTENT si la
	 * lista se encuentra vacía.
	 */
	@GetMapping("/mostrar")
	public ResponseEntity<List<AuditoriaDTO>> mostrar() {
		List<AuditoriaDTO> listaAuditoria = auditoriaService.getAll();
		if (!listaAuditoria.isEmpty())
			return new ResponseEntity<List<AuditoriaDTO>>(listaAuditoria, HttpStatus.OK);
		else
			return new ResponseEntity<List<AuditoriaDTO>>(HttpStatus.NO_CONTENT);
	}

}
