package co.edu.unbosque.horarios.service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import co.edu.unbosque.horarios.dto.AuditoriaDTO;
import co.edu.unbosque.horarios.entity.Administrador;
import co.edu.unbosque.horarios.entity.Auditoria;
import co.edu.unbosque.horarios.entity.Persona;
import co.edu.unbosque.horarios.repository.AuditoriaRepository;
import co.edu.unbosque.horarios.util.enums.Servicio;
import co.edu.unbosque.horarios.util.enums.TipoAccion;


/**
 * Servicio encargado de gestionar el registro y la persistencia de las pistas de auditoría
 * de las operaciones realizadas en el sistema.
 * @author Prisma Web
 * @version 1.0
 */
@Service
public class AuditoriaService implements CRUDOperation<AuditoriaDTO> {
	
	/** Repositorio de acceso a datos para las operaciones CRUD sobre la entidad Auditoria. */
	@Autowired
	private AuditoriaRepository auditoriaRepository;
	
	/** Componente encargado del mapeo relacional entre estructuras de datos DTO y Entidades. */
	@Autowired
	private ModelMapper modelMapper;
	
	/**
	 * Constructor por defecto del servicio.
	 */
	public AuditoriaService() {
		// TODO Auto-generated constructor stub
	}
	
	/**
	 * Prepara un objeto de transferencia de datos (DTO) de auditoría, extrayendo la información
	 * del usuario autenticado a través del token JWT si se encuentra disponible.
	 * * @param tipoAccion   El tipo de acción que se va a auditar (ej. READ, CREATE, etc.).
	 * @param servicioUsado El módulo o servicio del sistema donde se realiza la acción.
	 * @return Un objeto {@link AuditoriaDTO} con los detalles del usuario, acción y marca de tiempo actual.
	 */
	public AuditoriaDTO preAccion(TipoAccion tipoAccion, Servicio servicioUsado) {
	    Authentication auth = SecurityContextHolder.getContext().getAuthentication();

	    Long idUsuario = null;
	    String nombreUsuario = null;
	    boolean esAdmin = false;

	    if (auth != null && auth.isAuthenticated() && auth.getPrincipal() instanceof Persona persona) {
	        idUsuario = persona.getId();
	        nombreUsuario = persona.getNombre();
	        esAdmin = persona instanceof Administrador;
	    }

	    return new AuditoriaDTO(idUsuario, nombreUsuario, tipoAccion, esAdmin, servicioUsado, LocalDateTime.now());
	}


	/**
	 * Registra una nueva entrada de auditoría en la persistencia del sistema.
	 * * @param data Contenedor de datos de la auditoría a crear.
	 * @return Código de respuesta: 0 si se completó correctamente.
	 */
	@Override
	public int create(AuditoriaDTO data) {
		Auditoria entity = modelMapper.map(data, Auditoria.class);
		auditoriaRepository.save(entity);
		return 0;
	}
	
	/**
	 * Recupera todas las trazas de auditoría registradas en el sistema.
	 * * @return Lista completa de objetos de tipo {@link AuditoriaDTO}.
	 */
	@Override
	public List<AuditoriaDTO> getAll() {
		List<Auditoria> entityList = auditoriaRepository.findAll();
		List<AuditoriaDTO> dtoList = new ArrayList<>();
		entityList.forEach(
				(entity)->{
					AuditoriaDTO dto  = modelMapper.map(entity, AuditoriaDTO.class);
					dtoList.add(dto);
				});
		return dtoList;
	}

	/**
	 * Remueve un registro de auditoría de la persistencia a través de su ID único.
	 * * @param id Identificador numérico único de la auditoría.
	 * @return Código de respuesta: 0 si fue eliminado, 1 si el registro no existía.
	 */
	@Override
	public int deleteById(Long id) {
		Optional<Auditoria> encontrado = auditoriaRepository.findById(id);
		if(!encontrado.isPresent()) return 1;
		auditoriaRepository.deleteById(id);
		return 0;
	}

	/**
	 * Actualiza de forma total un registro de auditoría existente.
	 * * @param id      Identificador único del registro a modificar.
	 * @param newData Nuevos valores provistos en un contenedor DTO.
	 * @return Código de respuesta: 0 si la actualización fue correcta, 1 si no se encontró.
	 */
	@Override
	public int updateById(Long id, AuditoriaDTO newData) {
		Optional<Auditoria> encontrado = auditoriaRepository.findById(id);
		if(!encontrado.isPresent()) return 1;
		Auditoria entity = modelMapper.map(newData, Auditoria.class);
		auditoriaRepository.save(entity);
		return 0;
	}

	/**
	 * Retorna la cantidad agregada de registros de auditoría almacenados.
	 * * @return Total numérico en formato {@code long}.
	 */
	@Override
	public long count() {
		return auditoriaRepository.count();
	}

	/**
	 * Valida de forma rápida la existencia de una traza por su identificador primario.
	 * * @param id Identificador único de consulta.
	 * @return true si el registro existe, false de lo contrario.
	 */
	@Override
	public boolean exist(Long id) {
		return auditoriaRepository.existsById(id);
	}
	
	
}
