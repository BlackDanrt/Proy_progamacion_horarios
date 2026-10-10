package co.edu.unbosque.horarios.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
	
	
}
