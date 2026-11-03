package co.edu.unbosque.horarios.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import co.edu.unbosque.horarios.dto.ParametrosSistemaDTO;
import co.edu.unbosque.horarios.service.ParametrosSistemaService;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;

@RestController
@RequestMapping("/parametrossistema")
@CrossOrigin(origins = { "http://localhost:8080", "*" })
public class ParametrosSistemaController {

	@Autowired
	private ParametrosSistemaService paramSer;

	public ParametrosSistemaController() {
		// TODO Auto-generated constructor stub
	}

	@GetMapping("/traerparametros")
	public ResponseEntity<ParametrosSistemaDTO> get() {
		return new ResponseEntity<ParametrosSistemaDTO>(paramSer.get(), HttpStatus.OK);
	}

	@PutMapping("/actualizar")
	public ResponseEntity<String> actualizar(@RequestBody ParametrosSistemaDTO parametrosDTO) {
		int status = paramSer.actualizar(parametrosDTO);

		if (status == 0)
			return new ResponseEntity<>("Parámetros actualizados exitosamente", HttpStatus.OK);
		else if (status == 1)
			return new ResponseEntity<>("Error de validación en los parámetros enviados", HttpStatus.BAD_REQUEST);
		else if (status == 2)
			return new ResponseEntity<>("Error al procesar los datos recibidos", HttpStatus.BAD_REQUEST);
		else if (status == 3)
			return new ResponseEntity<>("Los parámetros violan una restricción de la base de datos",
					HttpStatus.CONFLICT);
		else
			return new ResponseEntity<>("Error inesperado al actualizar los parámetros",
					HttpStatus.INTERNAL_SERVER_ERROR);
	}
}
