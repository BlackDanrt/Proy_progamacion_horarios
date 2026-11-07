package co.edu.unbosque.horarios;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import java.util.Collections;

import org.hibernate.MappingException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import org.springframework.dao.DataIntegrityViolationException;

import co.edu.unbosque.horarios.dto.ParametrosSistemaDTO;
import co.edu.unbosque.horarios.entity.ParametrosSistema;
import co.edu.unbosque.horarios.repository.ParametrosSistemaRepository;
import co.edu.unbosque.horarios.service.AuditoriaService;
import co.edu.unbosque.horarios.service.ParametrosSistemaService;
import co.edu.unbosque.horarios.util.enums.Servicio;
import co.edu.unbosque.horarios.util.enums.TipoAccion;
import jakarta.validation.ConstraintViolationException;

@ExtendWith(MockitoExtension.class)
class ParametrosSistemaServiceTest {

    @Mock
    private ParametrosSistemaRepository parametrosSistemaRepository;

    @Mock
    private AuditoriaService auditoriaService;

    @Mock
    private ModelMapper mapper;

    @InjectMocks
    private ParametrosSistemaService parametrosSistemaService;

    private ParametrosSistema parametrosSistema;
    private ParametrosSistemaDTO parametrosSistemaDTO;

    @BeforeEach
    void setUp() {
        parametrosSistema = new ParametrosSistema();
        parametrosSistema.setId(1L);

        parametrosSistemaDTO = new ParametrosSistemaDTO();
        parametrosSistemaDTO.setId(1L);
    }

    @Test
    void get() {
        when(parametrosSistemaRepository.getById(1L)).thenReturn(parametrosSistema);
        when(mapper.map(parametrosSistema, ParametrosSistemaDTO.class)).thenReturn(parametrosSistemaDTO);

        ParametrosSistemaDTO resultado = parametrosSistemaService.get();

        assertNotNull(resultado);
        assertEquals(1L, resultado.getId());
        verify(parametrosSistemaRepository).getById(1L);
        verify(auditoriaService).preAccion(TipoAccion.READ, Servicio.PARAMETROS);
        verify(auditoriaService).create(any());
    }

    @Test
    void actualizarExitoso() {
        when(mapper.map(parametrosSistemaDTO, ParametrosSistema.class)).thenReturn(parametrosSistema);

        int resultado = parametrosSistemaService.actualizar(parametrosSistemaDTO);

        assertEquals(0, resultado);
        assertEquals(1L, parametrosSistema.getId());
        verify(parametrosSistemaRepository).save(parametrosSistema);
        verify(auditoriaService).preAccion(TipoAccion.UPDATE, Servicio.PARAMETROS);
        verify(auditoriaService).create(any());
    }

    @Test
    void actualizarConstraintViolationException() {
        when(mapper.map(parametrosSistemaDTO, ParametrosSistema.class))
                .thenThrow(new ConstraintViolationException(Collections.emptySet()));

        int resultado = parametrosSistemaService.actualizar(parametrosSistemaDTO);

        assertEquals(1, resultado);
        verify(parametrosSistemaRepository, never()).save(any());
        verify(auditoriaService, never()).create(any());
    }

    @Test
    void actualizarIllegalArgumentException() {
        when(mapper.map(parametrosSistemaDTO, ParametrosSistema.class))
                .thenThrow(new IllegalArgumentException("dato inválido"));

        int resultado = parametrosSistemaService.actualizar(parametrosSistemaDTO);

        assertEquals(2, resultado);
    }

    @Test
    void actualizarMappingException() {
        when(mapper.map(parametrosSistemaDTO, ParametrosSistema.class))
                .thenThrow(new MappingException("error de mapeo"));

        int resultado = parametrosSistemaService.actualizar(parametrosSistemaDTO);

        assertEquals(2, resultado);
    }

    @Test
    void actualizarDataIntegrityViolationException() {
        when(mapper.map(parametrosSistemaDTO, ParametrosSistema.class)).thenReturn(parametrosSistema);
        when(parametrosSistemaRepository.save(parametrosSistema))
                .thenThrow(new DataIntegrityViolationException("violación de integridad"));

        int resultado = parametrosSistemaService.actualizar(parametrosSistemaDTO);

        assertEquals(3, resultado);
    }

    @Test
    void actualizarExcepcionGenerica() {
        when(mapper.map(parametrosSistemaDTO, ParametrosSistema.class))
                .thenThrow(new RuntimeException("error inesperado"));

        int resultado = parametrosSistemaService.actualizar(parametrosSistemaDTO);

        assertEquals(4, resultado);
        verify(auditoriaService, never()).create(any());
    }
}