package co.edu.unbosque.horarios.service;

import org.hibernate.MappingException;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import co.edu.unbosque.horarios.dto.ParametrosSistemaDTO;
import co.edu.unbosque.horarios.entity.ParametrosSistema;
import co.edu.unbosque.horarios.repository.ParametrosSistemaRepository;
import jakarta.validation.ConstraintViolationException;

@Validated
@Service
public class ParametrosSistemaService {

	@Autowired
	private ParametrosSistemaRepository parametrosSistemaRepository;

	@Autowired
	private ModelMapper mapper;

	public ParametrosSistemaService() {
		// TODO Auto-generated constructor stub
	}

	public ParametrosSistemaDTO get() {
		ParametrosSistema parametros = parametrosSistemaRepository.getById(1L);
		ParametrosSistemaDTO parametrosDTO = mapper.map(parametros, ParametrosSistemaDTO.class);
		parametrosDTO.setId(1L);
		return parametrosDTO;
	}

	public int actualizar(ParametrosSistemaDTO nuevosValores) {
		 try {
		        ParametrosSistema parametros = mapper.map(nuevosValores, ParametrosSistema.class);
		        parametros.setId(1L);
		        parametrosSistemaRepository.save(parametros);
		        return 0;
		    } catch (ConstraintViolationException e) {
		        return 1;
		    } catch (IllegalArgumentException | MappingException e) {
		        return 2;
		    } catch (DataIntegrityViolationException e) {
		        return 3;
		    } catch (Exception e) {
		        return 4;
		    }
	}
}