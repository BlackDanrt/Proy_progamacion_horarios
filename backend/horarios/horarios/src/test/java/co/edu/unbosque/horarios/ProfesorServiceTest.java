package co.edu.unbosque.horarios;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import org.springframework.security.crypto.password.PasswordEncoder;

import co.edu.unbosque.horarios.dto.FranjaHorariaDTO;
import co.edu.unbosque.horarios.dto.ProfesorDTO;
import co.edu.unbosque.horarios.entity.FranjaHoraria;
import co.edu.unbosque.horarios.entity.Profesor;
import co.edu.unbosque.horarios.exception.EspecialidadesInvalidasException;
import co.edu.unbosque.horarios.repository.ProfesorRepository;
import co.edu.unbosque.horarios.service.ProfesorService;
import co.edu.unbosque.horarios.util.LanzadorDeException;
import co.edu.unbosque.horarios.util.enums.Dia;
import co.edu.unbosque.horarios.util.enums.Escalafon;
import co.edu.unbosque.horarios.util.enums.Especialidad;
import co.edu.unbosque.horarios.util.enums.TipoVinculacion;

@ExtendWith(MockitoExtension.class)
class ProfesorServiceTest {

	@Mock
	private ProfesorRepository profesorRep;

	@Mock
	private ModelMapper mapper;

	@Mock
	private PasswordEncoder passwordEncoder;

	@InjectMocks
	private ProfesorService profesorService;

	private ProfesorDTO profesorDTO;
	private Profesor profesor;

	@BeforeEach
	void setUp() {
		profesorDTO = new ProfesorDTO();
		profesorDTO.setNombre("Juan");
		profesorDTO.setApellido("Pérez");
		profesorDTO.setDocumento(12345678L);
		profesorDTO.setCorreo("juan@unbosque.edu.co");
		profesorDTO.setContrasenia("Abc123*");
		profesorDTO.setTipoVinculacion(TipoVinculacion.TIEMPO_COMPLETO);
		profesorDTO.setEscalafon(Escalafon.PROFESOR_ASOCIADO);
		profesorDTO.setHorasMaximasSemanales(20);
		profesorDTO.setEspecialidades(new ArrayList<>());
		profesorDTO.setDisponibilidadHoraria(new ArrayList<>());

		profesor = new Profesor();
		profesor.setNombre("Juan");
		profesor.setApellido("Pérez");
		profesor.setDocumento(12345678L);
		profesor.setCorreo("juan@unbosque.edu.co");
		profesor.setContrasenia("Abc123*");
		profesor.setTipoVinculacion(TipoVinculacion.TIEMPO_COMPLETO);
		profesor.setEscalafon(Escalafon.PROFESOR_ASOCIADO);
		profesor.setHorasMaximasSemanales(20);
	}

	@Test
	void countProfesores() {
		when(profesorRep.count()).thenReturn(5L);

		long resultado = profesorService.count();

		assertEquals(5L, resultado);
		verify(profesorRep).count();
	}

	@Test
	void countProfesoresVacio() {
		when(profesorRep.count()).thenReturn(0L);

		assertEquals(0L, profesorService.count());
	}

	@Test
	void existIdExistente() {
		try (MockedStatic<LanzadorDeException> staticMock = mockStatic(LanzadorDeException.class)) {
			when(profesorRep.existsById(1L)).thenReturn(true);

			assertTrue(profesorService.exist(1L));
			verify(profesorRep).existsById(1L);
		}
	}

	@Test
	void existIdNoExistente() {
		try (MockedStatic<LanzadorDeException> staticMock = mockStatic(LanzadorDeException.class)) {
			when(profesorRep.existsById(99L)).thenReturn(false);

			assertFalse(profesorService.exist(99L));
		}
	}

	@Test
	void create() {
		try (MockedStatic<LanzadorDeException> staticMock = mockStatic(LanzadorDeException.class)) {
			when(profesorRep.existsByDocumento(profesorDTO.getDocumento())).thenReturn(false);
			when(profesorRep.existsByCorreo(profesorDTO.getCorreo())).thenReturn(false);
			when(mapper.map(profesorDTO, Profesor.class)).thenReturn(profesor);

			int resultado = profesorService.create(profesorDTO);

			assertEquals(0, resultado);
			verify(profesorRep).save(profesor);
			verify(passwordEncoder).encode("Abc123*");
		}
	}

	@Test
	void getAll() {
		List<Profesor> entidades = List.of(profesor);
		when(profesorRep.findAll()).thenReturn(entidades);
		when(mapper.map(profesor, ProfesorDTO.class)).thenReturn(profesorDTO);

		List<ProfesorDTO> resultado = profesorService.getAll();

		assertNotNull(resultado);
		assertEquals(1, resultado.size());
		assertEquals(profesorDTO, resultado.get(0));
		verify(profesorRep).findAll();
	}

	@Test
	void getAllVacio() {
		when(profesorRep.findAll()).thenReturn(new ArrayList<>());

		List<ProfesorDTO> resultado = profesorService.getAll();

		assertNotNull(resultado);
		assertTrue(resultado.isEmpty());
	}

	@Test
	void deleteByIdExistente() {
		try (MockedStatic<LanzadorDeException> staticMock = mockStatic(LanzadorDeException.class)) {
			when(profesorRep.existsById(1L)).thenReturn(true);

			int resultado = profesorService.deleteById(1L);

			assertEquals(0, resultado);
			verify(profesorRep).deleteById(1L);
		}
	}

	@Test
	void deleteByIdNoExistente() {
		try (MockedStatic<LanzadorDeException> staticMock = mockStatic(LanzadorDeException.class)) {
			when(profesorRep.existsById(99L)).thenReturn(false);

			int resultado = profesorService.deleteById(99L);

			assertEquals(1, resultado);
			verify(profesorRep, never()).deleteById(anyLong());
		}
	}

	@Test
	void updateByIdExistente() {
		try (MockedStatic<LanzadorDeException> staticMock = mockStatic(LanzadorDeException.class)) {

			when(profesorRep.findById(1L)).thenReturn(Optional.of(profesor));
			when(mapper.map(profesorDTO, Profesor.class)).thenReturn(profesor);

			int resultado = profesorService.updateById(1L, profesorDTO);

			assertEquals(0, resultado);
			verify(profesorRep).save(any(Profesor.class));

			verify(profesorRep, never()).existsByDocumento(anyLong());
			verify(profesorRep, never()).existsByCorreo(anyString());
		}
	}

	@Test
	void updateByIdNoExistente() {
		try (MockedStatic<LanzadorDeException> staticMock = mockStatic(LanzadorDeException.class)) {
			when(profesorRep.findById(99L)).thenReturn(Optional.empty());

			int resultado = profesorService.updateById(99L, profesorDTO);

			assertEquals(1, resultado);
			verify(profesorRep, never()).save(any());
		}
	}

	@Test
	void updateByIdDocumentoDiferente() {
		try (MockedStatic<LanzadorDeException> staticMock = mockStatic(LanzadorDeException.class)) {
			Profesor existente = new Profesor();
			existente.setDocumento(99999999L);
			existente.setCorreo("juan@unbosque.edu.co");

			when(profesorRep.findById(1L)).thenReturn(Optional.of(existente));
			when(profesorRep.existsByDocumento(profesorDTO.getDocumento())).thenReturn(false);
			when(mapper.map(profesorDTO, Profesor.class)).thenReturn(profesor);

			int resultado = profesorService.updateById(1L, profesorDTO);

			assertEquals(0, resultado);
			verify(profesorRep).existsByDocumento(profesorDTO.getDocumento());
		}
	}

	@Test
	void updateByIdCorreoDiferente() {
		try (MockedStatic<LanzadorDeException> staticMock = mockStatic(LanzadorDeException.class)) {
			Profesor existente = new Profesor();
			existente.setDocumento(12345678L);
			existente.setCorreo("otro@unbosque.edu.co");

			when(profesorRep.findById(1L)).thenReturn(Optional.of(existente));
			when(profesorRep.existsByCorreo(profesorDTO.getCorreo())).thenReturn(false);
			when(mapper.map(profesorDTO, Profesor.class)).thenReturn(profesor);

			int resultado = profesorService.updateById(1L, profesorDTO);

			assertEquals(0, resultado);
			verify(profesorRep).existsByCorreo(profesorDTO.getCorreo());
		}
	}

	@Test
	void findByDocumentoExistente() {
		try (MockedStatic<LanzadorDeException> staticMock = mockStatic(LanzadorDeException.class)) {
			when(profesorRep.findByDocumento(12345678L)).thenReturn(Optional.of(profesor));
			when(mapper.map(profesor, ProfesorDTO.class)).thenReturn(profesorDTO);

			ProfesorDTO resultado = profesorService.findByDocumento(12345678L);

			assertNotNull(resultado);
			assertEquals(profesorDTO, resultado);
		}
	}

	@Test
	void findByDocumentoNoExistente() {
		try (MockedStatic<LanzadorDeException> staticMock = mockStatic(LanzadorDeException.class)) {
			when(profesorRep.findByDocumento(99999999L)).thenReturn(Optional.empty());

			ProfesorDTO resultado = profesorService.findByDocumento(99999999L);

			assertNull(resultado);
		}
	}

	@Test
	void findByCorreoExistente() {
		try (MockedStatic<LanzadorDeException> staticMock = mockStatic(LanzadorDeException.class)) {
			when(profesorRep.findByCorreo("juan@unbosque.edu.co")).thenReturn(Optional.of(profesor));
			when(mapper.map(profesor, ProfesorDTO.class)).thenReturn(profesorDTO);

			ProfesorDTO resultado = profesorService.findByCorreo("juan@unbosque.edu.co");

			assertNotNull(resultado);
			assertEquals(profesorDTO, resultado);
		}
	}

	@Test
	void findByCorreoNoExistente() {
		try (MockedStatic<LanzadorDeException> staticMock = mockStatic(LanzadorDeException.class)) {
			when(profesorRep.findByCorreo("noexiste@unbosque.edu.co")).thenReturn(Optional.empty());

			ProfesorDTO resultado = profesorService.findByCorreo("noexiste@unbosque.edu.co");

			assertNull(resultado);
		}
	}

	@Test
	void findByTipoVinculacion() {
		try (MockedStatic<LanzadorDeException> staticMock = mockStatic(LanzadorDeException.class)) {
			when(profesorRep.findByTipoVinculacion(TipoVinculacion.TIEMPO_COMPLETO)).thenReturn(List.of(profesor));
			when(mapper.map(profesor, ProfesorDTO.class)).thenReturn(profesorDTO);

			List<ProfesorDTO> resultado = profesorService.findByTipoVinculacion(TipoVinculacion.TIEMPO_COMPLETO);

			assertNotNull(resultado);
			assertEquals(1, resultado.size());
			assertEquals(profesorDTO, resultado.get(0));
		}
	}

	@Test
	void findByTipoVinculacionVacio() {
		try (MockedStatic<LanzadorDeException> staticMock = mockStatic(LanzadorDeException.class)) {
			when(profesorRep.findByTipoVinculacion(TipoVinculacion.TIEMPO_COMPLETO)).thenReturn(new ArrayList<>());

			List<ProfesorDTO> resultado = profesorService.findByTipoVinculacion(TipoVinculacion.TIEMPO_COMPLETO);

			assertNotNull(resultado);
			assertTrue(resultado.isEmpty());
		}
	}

	@Test
	void findByEscalafon() {
		try (MockedStatic<LanzadorDeException> staticMock = mockStatic(LanzadorDeException.class)) {
			when(profesorRep.findByEscalafon(Escalafon.PROFESOR_ASOCIADO)).thenReturn(List.of(profesor));
			when(mapper.map(profesor, ProfesorDTO.class)).thenReturn(profesorDTO);

			List<ProfesorDTO> resultado = profesorService.findByEscalafon(Escalafon.PROFESOR_ASOCIADO);

			assertNotNull(resultado);
			assertEquals(1, resultado.size());
			assertEquals(profesorDTO, resultado.get(0));
		}
	}

	@Test
	void findByEscalafonVacio() {
		try (MockedStatic<LanzadorDeException> staticMock = mockStatic(LanzadorDeException.class)) {
			when(profesorRep.findByEscalafon(Escalafon.PROFESOR_ASOCIADO)).thenReturn(new ArrayList<>());

			List<ProfesorDTO> resultado = profesorService.findByEscalafon(Escalafon.PROFESOR_ASOCIADO);

			assertNotNull(resultado);
			assertTrue(resultado.isEmpty());
		}
	}

	@Test
	void findByEspecialidad() {
		try (MockedStatic<LanzadorDeException> staticMock = mockStatic(LanzadorDeException.class)) {
			profesor.setEspecialidades(new ArrayList<>(List.of(Especialidad.BASES_DE_DATOS)));

			when(profesorRep.findAll()).thenReturn(List.of(profesor));
			when(mapper.map(profesor, ProfesorDTO.class)).thenReturn(profesorDTO);

			List<ProfesorDTO> resultado = profesorService.findByEspecialidad(Especialidad.BASES_DE_DATOS);

			assertNotNull(resultado);
			assertEquals(1, resultado.size());
			assertEquals(profesorDTO, resultado.get(0));
		}
	}

	@Test
	void findByEspecialidadVacio() {
		try (MockedStatic<LanzadorDeException> staticMock = mockStatic(LanzadorDeException.class)) {
			profesor.setEspecialidades(new ArrayList<>(List.of(Especialidad.BASES_DE_DATOS)));

			when(profesorRep.findAll()).thenReturn(List.of(profesor));

			List<ProfesorDTO> resultado = profesorService.findByEspecialidad(Especialidad.CIBERSEGURIDAD);

			assertNotNull(resultado);
			assertTrue(resultado.isEmpty());
		}
	}

	@Test
	void findByEspecialidadNula() {
		assertThrows(EspecialidadesInvalidasException.class, () -> profesorService.findByEspecialidad(null));
	}

	@Test
	void findByDisponibilidad() {
		try (MockedStatic<LanzadorDeException> staticMock = mockStatic(LanzadorDeException.class)) {
			profesor.setDisponibilidadHoraria(
					new ArrayList<>(List.of(new FranjaHoraria(Dia.LUNES, LocalTime.of(8, 0), LocalTime.of(12, 0)))));
			FranjaHorariaDTO busqueda = new FranjaHorariaDTO(Dia.LUNES, LocalTime.of(8, 0), LocalTime.of(10, 0));

			when(profesorRep.findAll()).thenReturn(List.of(profesor));
			when(mapper.map(profesor, ProfesorDTO.class)).thenReturn(profesorDTO);

			List<ProfesorDTO> resultado = profesorService.findByDisponibilidad(busqueda);

			assertNotNull(resultado);
			assertEquals(1, resultado.size());
			assertEquals(profesorDTO, resultado.get(0));
		}
	}

	@Test
	void findByDisponibilidadVacio() {
		try (MockedStatic<LanzadorDeException> staticMock = mockStatic(LanzadorDeException.class)) {
			profesor.setDisponibilidadHoraria(
					new ArrayList<>(List.of(new FranjaHoraria(Dia.LUNES, LocalTime.of(8, 0), LocalTime.of(12, 0)))));
			FranjaHorariaDTO busqueda = new FranjaHorariaDTO(Dia.MARTES, LocalTime.of(8, 0), LocalTime.of(10, 0));

			when(profesorRep.findAll()).thenReturn(List.of(profesor));

			List<ProfesorDTO> resultado = profesorService.findByDisponibilidad(busqueda);

			assertNotNull(resultado);
			assertTrue(resultado.isEmpty());
		}
	}

	@Test
	void findDocumentoAlreadyTakenDocumentoExistente() {
		when(profesorRep.existsByDocumento(12345678L)).thenReturn(true);

		assertTrue(profesorService.findDocumentoAlreadyTaken(12345678L));
		verify(profesorRep).existsByDocumento(12345678L);
	}

	@Test
	void findDocumentoAlreadyTakenDocumentoNoExistente() {
		when(profesorRep.existsByDocumento(12345678L)).thenReturn(false);

		assertFalse(profesorService.findDocumentoAlreadyTaken(12345678L));
	}

	@Test
	void findCorreoAlreadyTakenCorreoExistente() {
		when(profesorRep.existsByCorreo("juan@unbosque.edu.co")).thenReturn(true);

		assertTrue(profesorService.findCorreoAlreadyTaken("juan@unbosque.edu.co"));
		verify(profesorRep).existsByCorreo("juan@unbosque.edu.co");
	}

	@Test
	void findCorreoAlreadyTakenCorreoNoExistente() {
		when(profesorRep.existsByCorreo("juan@unbosque.edu.co")).thenReturn(false);

		assertFalse(profesorService.findCorreoAlreadyTaken("juan@unbosque.edu.co"));
	}

	@Test
	void deleteByDocumentoExistente() {
		try (MockedStatic<LanzadorDeException> staticMock = mockStatic(LanzadorDeException.class)) {
			when(profesorRep.existsByDocumento(12345678L)).thenReturn(true);

			profesorService.deleteByDocumento(12345678L);

			verify(profesorRep).deleteByDocumento(12345678L);
		}
	}

	@Test
	void deleteByDocumentoNoExistente() {
		try (MockedStatic<LanzadorDeException> staticMock = mockStatic(LanzadorDeException.class)) {
			when(profesorRep.existsByDocumento(99999999L)).thenReturn(false);

			profesorService.deleteByDocumento(99999999L);

			verify(profesorRep, never()).deleteByDocumento(anyLong());
		}
	}

	@Test
	void setGetProfesorRepository() {
		ProfesorRepository nuevoRepo = mock(ProfesorRepository.class);

		profesorService.setProfesorRep(nuevoRepo);

		assertEquals(nuevoRepo, profesorService.getProfesorRep());
	}

	@Test
	void setGetMapper() {
		ModelMapper nuevoMapper = mock(ModelMapper.class);

		profesorService.setMapper(nuevoMapper);

		assertEquals(nuevoMapper, profesorService.getMapper());
	}
}