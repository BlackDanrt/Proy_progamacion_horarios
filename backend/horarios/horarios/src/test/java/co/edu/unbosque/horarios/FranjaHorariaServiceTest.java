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

import co.edu.unbosque.horarios.dto.FranjaHorariaDTO;
import co.edu.unbosque.horarios.entity.FranjaHoraria;
import co.edu.unbosque.horarios.repository.FranjaHorariaRepository;
import co.edu.unbosque.horarios.service.FranjaHorariaService;
import co.edu.unbosque.horarios.util.LanzadorDeException;
import co.edu.unbosque.horarios.util.enums.Dia;

@ExtendWith(MockitoExtension.class)
class FranjaHorariaServiceTest {

    @Mock
    private FranjaHorariaRepository franjaHorariaRep;

    @Mock
    private ModelMapper mapper;

    @InjectMocks
    private FranjaHorariaService franjaHorariaService;

    private FranjaHorariaDTO franjaDTO;
    private FranjaHoraria franja;

    @BeforeEach
    void setUp() {
    	franjaDTO = new FranjaHorariaDTO();
        franjaDTO.setId(1L);
        franjaDTO.setDia(Dia.LUNES);
        franjaDTO.setHoraInicio(LocalTime.of(7, 0));
        franjaDTO.setHoraFin(LocalTime.of(9, 0));
 
        franja = new FranjaHoraria();
        franja.setId(1L);
        franja.setDia(Dia.LUNES);
        franja.setHoraInicio(LocalTime.of(7, 0));
        franja.setHoraFin(LocalTime.of(9, 0));
    }

    @Test
    void countFranjas() {
        when(franjaHorariaRep.count()).thenReturn(7L);

        long resultado = franjaHorariaService.count();

        assertEquals(7L, resultado);
        verify(franjaHorariaRep).count();
    }

    @Test
    void countFranjasVacio() {
        when(franjaHorariaRep.count()).thenReturn(0L);

        assertEquals(0L, franjaHorariaService.count());
    }

    @Test
    void existIdExistente() {
        try (MockedStatic<LanzadorDeException> staticMock = mockStatic(LanzadorDeException.class)) {
            when(franjaHorariaRep.existsById(1L)).thenReturn(true);

            assertTrue(franjaHorariaService.exist(1L));
            verify(franjaHorariaRep).existsById(1L);
        }
    }

    @Test
    void existIdNoExistente() {
        try (MockedStatic<LanzadorDeException> staticMock = mockStatic(LanzadorDeException.class)) {
            when(franjaHorariaRep.existsById(99L)).thenReturn(false);

            assertFalse(franjaHorariaService.exist(99L));
        }
    }

    @Test
    void create() {
        try (MockedStatic<LanzadorDeException> staticMock = mockStatic(LanzadorDeException.class)) {
            when(mapper.map(franjaDTO, FranjaHoraria.class)).thenReturn(franja);

            int resultado = franjaHorariaService.create(franjaDTO);

            assertEquals(0, resultado);
            verify(franjaHorariaRep).save(franja);
        }
    }

    @Test
    void getAll() {
        List<FranjaHoraria> entidades = List.of(franja);
        when(franjaHorariaRep.findAll()).thenReturn(entidades);
        when(mapper.map(franja, FranjaHorariaDTO.class)).thenReturn(franjaDTO);

        List<FranjaHorariaDTO> resultado = franjaHorariaService.getAll();

        assertNotNull(resultado);
        assertEquals(1, resultado.size());
        assertEquals(franjaDTO, resultado.get(0));
        verify(franjaHorariaRep).findAll();
    }

    @Test
    void getAllVacio() {
        when(franjaHorariaRep.findAll()).thenReturn(new ArrayList<>());

        List<FranjaHorariaDTO> resultado = franjaHorariaService.getAll();

        assertNotNull(resultado);
        assertTrue(resultado.isEmpty());
    }

    @Test
    void deleteByIdExistente() {
        try (MockedStatic<LanzadorDeException> staticMock = mockStatic(LanzadorDeException.class)) {
            when(franjaHorariaRep.existsById(1L)).thenReturn(true);

            int resultado = franjaHorariaService.deleteById(1L);

            assertEquals(0, resultado);
            verify(franjaHorariaRep).deleteById(1L);
        }
    }

    @Test
    void deleteByIdNoExistente() {
        try (MockedStatic<LanzadorDeException> staticMock = mockStatic(LanzadorDeException.class)) {
            when(franjaHorariaRep.existsById(99L)).thenReturn(false);

            int resultado = franjaHorariaService.deleteById(99L);

            assertEquals(1, resultado);
            verify(franjaHorariaRep, never()).deleteById(anyLong());
        }
    }

    @Test
    void updateByIdExistente() {
        try (MockedStatic<LanzadorDeException> staticMock = mockStatic(LanzadorDeException.class)) {
            FranjaHoraria existente = new FranjaHoraria(Dia.MARTES, LocalTime.of(10, 0), LocalTime.of(11, 0));
            existente.setId(1L);

            when(franjaHorariaRep.findById(1L)).thenReturn(Optional.of(existente));

            int resultado = franjaHorariaService.updateById(1L, franjaDTO);

            assertEquals(0, resultado);
            assertEquals(Dia.LUNES, existente.getDia());
            assertEquals(LocalTime.of(7, 0), existente.getHoraInicio());
            assertEquals(LocalTime.of(9, 0), existente.getHoraFin());
            verify(franjaHorariaRep).save(existente);
        }
    }

    @Test
    void updateByIdNoExistente() {
        try (MockedStatic<LanzadorDeException> staticMock = mockStatic(LanzadorDeException.class)) {
            when(franjaHorariaRep.findById(99L)).thenReturn(Optional.empty());

            int resultado = franjaHorariaService.updateById(99L, franjaDTO);

            assertEquals(1, resultado);
            verify(franjaHorariaRep, never()).save(any());
        }
    }

    @Test
    void findByDia() {
        try (MockedStatic<LanzadorDeException> staticMock = mockStatic(LanzadorDeException.class)) {
            when(franjaHorariaRep.findByDia(Dia.LUNES)).thenReturn(List.of(franja));
            when(mapper.map(franja, FranjaHorariaDTO.class)).thenReturn(franjaDTO);

            List<FranjaHorariaDTO> resultado = franjaHorariaService.findByDia(Dia.LUNES);

            assertNotNull(resultado);
            assertEquals(1, resultado.size());
            assertEquals(franjaDTO, resultado.get(0));
        }
    }

    @Test
    void findByDiaVacio() {
        try (MockedStatic<LanzadorDeException> staticMock = mockStatic(LanzadorDeException.class)) {
            when(franjaHorariaRep.findByDia(Dia.LUNES)).thenReturn(new ArrayList<>());

            List<FranjaHorariaDTO> resultado = franjaHorariaService.findByDia(Dia.LUNES);

            assertNotNull(resultado);
            assertTrue(resultado.isEmpty());
        }
    }

    @Test
    void findByHoraInicio() {
        try (MockedStatic<LanzadorDeException> staticMock = mockStatic(LanzadorDeException.class)) {
            LocalTime hora = LocalTime.of(7, 0);
            when(franjaHorariaRep.findByHoraInicio(hora)).thenReturn(List.of(franja));
            when(mapper.map(franja, FranjaHorariaDTO.class)).thenReturn(franjaDTO);

            List<FranjaHorariaDTO> resultado = franjaHorariaService.findByHoraInicio(hora);

            assertNotNull(resultado);
            assertEquals(1, resultado.size());
        }
    }

    @Test
    void findByHoraInicioVacio() {
        try (MockedStatic<LanzadorDeException> staticMock = mockStatic(LanzadorDeException.class)) {
            LocalTime hora = LocalTime.of(7, 0);
            when(franjaHorariaRep.findByHoraInicio(hora)).thenReturn(new ArrayList<>());

            List<FranjaHorariaDTO> resultado = franjaHorariaService.findByHoraInicio(hora);

            assertNotNull(resultado);
            assertTrue(resultado.isEmpty());
        }
    }

    @Test
    void findByHoraFin() {
        try (MockedStatic<LanzadorDeException> staticMock = mockStatic(LanzadorDeException.class)) {
            LocalTime hora = LocalTime.of(9, 0);
            when(franjaHorariaRep.findByHoraFin(hora)).thenReturn(List.of(franja));
            when(mapper.map(franja, FranjaHorariaDTO.class)).thenReturn(franjaDTO);

            List<FranjaHorariaDTO> resultado = franjaHorariaService.findByHoraFin(hora);

            assertNotNull(resultado);
            assertEquals(1, resultado.size());
        }
    }

    @Test
    void findByHoraFinVacio() {
        try (MockedStatic<LanzadorDeException> staticMock = mockStatic(LanzadorDeException.class)) {
            LocalTime hora = LocalTime.of(9, 0);
            when(franjaHorariaRep.findByHoraFin(hora)).thenReturn(new ArrayList<>());

            List<FranjaHorariaDTO> resultado = franjaHorariaService.findByHoraFin(hora);

            assertNotNull(resultado);
            assertTrue(resultado.isEmpty());
        }
    }

    @Test
    void findDiaAlreadyTakenDiaExistente() {
        try (MockedStatic<LanzadorDeException> staticMock = mockStatic(LanzadorDeException.class)) {
            when(franjaHorariaRep.existsByDia(Dia.LUNES)).thenReturn(true);

            assertTrue(franjaHorariaService.findDiaAlreadyTaken(Dia.LUNES));
            verify(franjaHorariaRep).existsByDia(Dia.LUNES);
        }
    }

    @Test
    void findDiaAlreadyTakenDiaNoExistente() {
        try (MockedStatic<LanzadorDeException> staticMock = mockStatic(LanzadorDeException.class)) {
            when(franjaHorariaRep.existsByDia(Dia.LUNES)).thenReturn(false);

            assertFalse(franjaHorariaService.findDiaAlreadyTaken(Dia.LUNES));
        }
    }

    @Test
    void deleteByDiaExistente() {
        try (MockedStatic<LanzadorDeException> staticMock = mockStatic(LanzadorDeException.class)) {
            when(franjaHorariaRep.existsByDia(Dia.LUNES)).thenReturn(true);

            int resultado = franjaHorariaService.deleteByDia(Dia.LUNES);

            assertEquals(0, resultado);
            verify(franjaHorariaRep).deleteByDia(Dia.LUNES);
        }
    }

    @Test
    void deleteByDiaNoExistente() {
        try (MockedStatic<LanzadorDeException> staticMock = mockStatic(LanzadorDeException.class)) {
            when(franjaHorariaRep.existsByDia(Dia.LUNES)).thenReturn(false);

            int resultado = franjaHorariaService.deleteByDia(Dia.LUNES);

            assertEquals(1, resultado);
            verify(franjaHorariaRep, never()).deleteByDia(any());
        }
    }

    @Test
    void setGetFranjaHorariaRepository() {
        FranjaHorariaRepository nuevoRepo = mock(FranjaHorariaRepository.class);

        franjaHorariaService.setFranjaHorariaRep(nuevoRepo);

        assertEquals(nuevoRepo, franjaHorariaService.getFranjaHorariaRep());
    }

    @Test
    void setGetMapper() {
        ModelMapper nuevoMapper = mock(ModelMapper.class);

        franjaHorariaService.setMapper(nuevoMapper);

        assertEquals(nuevoMapper, franjaHorariaService.getMapper());
    }
}