package co.edu.unbosque.horarios;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

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

import co.edu.unbosque.horarios.dto.SalonDTO;
import co.edu.unbosque.horarios.entity.Salon;
import co.edu.unbosque.horarios.repository.SalonRepository;
import co.edu.unbosque.horarios.service.SalonService;
import co.edu.unbosque.horarios.util.LanzadorDeException;
import co.edu.unbosque.horarios.util.enums.Bloque;

@ExtendWith(MockitoExtension.class)
class SalonServiceTest {

    @Mock
    private SalonRepository salonRep;

    @Mock
    private ModelMapper mapper;

    @InjectMocks
    private SalonService salonService;

    private SalonDTO salonDTO;
    private Salon salon;

    @BeforeEach
    void setUp() {
    	salonDTO = new SalonDTO();
        salonDTO.setId(1L);
        salonDTO.setBloque(Bloque.A);
        salonDTO.setNumeroSalon(101);
        salonDTO.setCapacidad(30);
        salonDTO.setTieneComputadores(true);
        salonDTO.setTieneSillasMoviles(false);
 
        salon = new Salon();
        salon.setId(1L);
        salon.setBloque(Bloque.A);
        salon.setNumeroSalon(101);
        salon.setCapacidad(30);
        salon.setTieneComputadores(true);
        salon.setTieneSillasMoviles(false);
    }

    @Test
    void countSalones() {
        when(salonRep.count()).thenReturn(5L);

        long resultado = salonService.count();

        assertEquals(5L, resultado);
        verify(salonRep).count();
    }

    @Test
    void countSalonesVacio() {
        when(salonRep.count()).thenReturn(0L);

        assertEquals(0L, salonService.count());
    }

    @Test
    void existIdExistente() {
        try (MockedStatic<LanzadorDeException> staticMock = mockStatic(LanzadorDeException.class)) {
            when(salonRep.existsById(1L)).thenReturn(true);

            assertTrue(salonService.exist(1L));
            verify(salonRep).existsById(1L);
        }
    }

    @Test
    void existIdNoExistente() {
        try (MockedStatic<LanzadorDeException> staticMock = mockStatic(LanzadorDeException.class)) {
            when(salonRep.existsById(99L)).thenReturn(false);

            assertFalse(salonService.exist(99L));
        }
    }

    @Test
    void create() {
        try (MockedStatic<LanzadorDeException> staticMock = mockStatic(LanzadorDeException.class)) {
            when(salonRep.existsByBloqueAndNumeroSalon(Bloque.A, 101)).thenReturn(false);
            when(mapper.map(salonDTO, Salon.class)).thenReturn(salon);

            int resultado = salonService.create(salonDTO);

            assertEquals(0, resultado);
            verify(salonRep).save(salon);
        }
    }

    @Test
    void getAll() {
        List<Salon> entidades = List.of(salon);
        when(salonRep.findAll()).thenReturn(entidades);
        when(mapper.map(salon, SalonDTO.class)).thenReturn(salonDTO);

        List<SalonDTO> resultado = salonService.getAll();

        assertNotNull(resultado);
        assertEquals(1, resultado.size());
        assertEquals(salonDTO, resultado.get(0));
        verify(salonRep).findAll();
    }

    @Test
    void getAllVacio() {
        when(salonRep.findAll()).thenReturn(new ArrayList<>());

        List<SalonDTO> resultado = salonService.getAll();

        assertNotNull(resultado);
        assertTrue(resultado.isEmpty());
    }

    @Test
    void deleteByIdExistente() {
        try (MockedStatic<LanzadorDeException> staticMock = mockStatic(LanzadorDeException.class)) {
            when(salonRep.existsById(1L)).thenReturn(true);

            int resultado = salonService.deleteById(1L);

            assertEquals(0, resultado);
            verify(salonRep).deleteById(1L);
        }
    }

    @Test
    void deleteByIdNoExistente() {
        try (MockedStatic<LanzadorDeException> staticMock = mockStatic(LanzadorDeException.class)) {
            when(salonRep.existsById(99L)).thenReturn(false);

            int resultado = salonService.deleteById(99L);

            assertEquals(1, resultado);
            verify(salonRep, never()).deleteById(anyLong());
        }
    }

    @Test
    void updateByIdSinCambioDeBloqueONumero() {
        try (MockedStatic<LanzadorDeException> staticMock = mockStatic(LanzadorDeException.class)) {
            when(salonRep.findById(1L)).thenReturn(Optional.of(salon));
            when(mapper.map(salonDTO, Salon.class)).thenReturn(salon);

            int resultado = salonService.updateById(1L, salonDTO);

            assertEquals(0, resultado);
            verify(salonRep, never()).existsByBloqueAndNumeroSalon(any(), anyInt());
            verify(salonRep).save(salon);
        }
    }

    @Test
    void updateByIdCambioDeNumero() {
        try (MockedStatic<LanzadorDeException> staticMock = mockStatic(LanzadorDeException.class)) {
            Salon existente = new Salon(Bloque.A, 102, 30, true, false);
            existente.setId(1L);

            when(salonRep.findById(1L)).thenReturn(Optional.of(existente));
            when(salonRep.existsByBloqueAndNumeroSalon(Bloque.A, 101)).thenReturn(false);
            when(mapper.map(salonDTO, Salon.class)).thenReturn(salon);

            int resultado = salonService.updateById(1L, salonDTO);

            assertEquals(0, resultado);
            verify(salonRep).existsByBloqueAndNumeroSalon(Bloque.A, 101);
            verify(salonRep).save(salon);
        }
    }

    @Test
    void updateByIdNoExistente() {
        try (MockedStatic<LanzadorDeException> staticMock = mockStatic(LanzadorDeException.class)) {
            when(salonRep.findById(99L)).thenReturn(Optional.empty());

            int resultado = salonService.updateById(99L, salonDTO);

            assertEquals(1, resultado);
            verify(salonRep, never()).save(any());
        }
    }

    @Test
    void findByBloqueAndNumeroSalonExistente() {
        try (MockedStatic<LanzadorDeException> staticMock = mockStatic(LanzadorDeException.class)) {
            when(salonRep.findByBloqueAndNumeroSalon(Bloque.A, 101)).thenReturn(Optional.of(salon));
            when(mapper.map(salon, SalonDTO.class)).thenReturn(salonDTO);

            SalonDTO resultado = salonService.findByBloqueAndNumeroSalon(Bloque.A, 101);

            assertNotNull(resultado);
            assertEquals(salonDTO, resultado);
        }
    }

    @Test
    void findByBloqueAndNumeroSalonNoExistente() {
        try (MockedStatic<LanzadorDeException> staticMock = mockStatic(LanzadorDeException.class)) {
            when(salonRep.findByBloqueAndNumeroSalon(Bloque.A, 101)).thenReturn(Optional.empty());

            SalonDTO resultado = salonService.findByBloqueAndNumeroSalon(Bloque.A, 101);

            assertNull(resultado);
        }
    }

    @Test
    void findByBloque() {
        try (MockedStatic<LanzadorDeException> staticMock = mockStatic(LanzadorDeException.class)) {
            when(salonRep.findByBloque(Bloque.A)).thenReturn(List.of(salon));
            when(mapper.map(salon, SalonDTO.class)).thenReturn(salonDTO);

            List<SalonDTO> resultado = salonService.findByBloque(Bloque.A);

            assertNotNull(resultado);
            assertEquals(1, resultado.size());
            assertEquals(salonDTO, resultado.get(0));
        }
    }

    @Test
    void findByBloqueVacio() {
        try (MockedStatic<LanzadorDeException> staticMock = mockStatic(LanzadorDeException.class)) {
            when(salonRep.findByBloque(Bloque.A)).thenReturn(new ArrayList<>());

            List<SalonDTO> resultado = salonService.findByBloque(Bloque.A);

            assertNotNull(resultado);
            assertTrue(resultado.isEmpty());
        }
    }

    @Test
    void findByCapacidad() {
        try (MockedStatic<LanzadorDeException> staticMock = mockStatic(LanzadorDeException.class)) {
            when(salonRep.findByCapacidad(30)).thenReturn(List.of(salon));
            when(mapper.map(salon, SalonDTO.class)).thenReturn(salonDTO);

            List<SalonDTO> resultado = salonService.findByCapacidad(30);

            assertNotNull(resultado);
            assertEquals(1, resultado.size());
        }
    }

    @Test
    void findByCapacidadVacio() {
        try (MockedStatic<LanzadorDeException> staticMock = mockStatic(LanzadorDeException.class)) {
            when(salonRep.findByCapacidad(30)).thenReturn(new ArrayList<>());

            List<SalonDTO> resultado = salonService.findByCapacidad(30);

            assertNotNull(resultado);
            assertTrue(resultado.isEmpty());
        }
    }

    @Test
    void findByTieneComputadores() {
        when(salonRep.findByTieneComputadores(true)).thenReturn(List.of(salon));
        when(mapper.map(salon, SalonDTO.class)).thenReturn(salonDTO);

        List<SalonDTO> resultado = salonService.findByTieneComputadores(true);

        assertNotNull(resultado);
        assertEquals(1, resultado.size());
        verify(salonRep).findByTieneComputadores(true);
    }

    @Test
    void findByTieneComputadoresVacio() {
        when(salonRep.findByTieneComputadores(true)).thenReturn(new ArrayList<>());

        List<SalonDTO> resultado = salonService.findByTieneComputadores(true);

        assertNotNull(resultado);
        assertTrue(resultado.isEmpty());
    }

    @Test
    void findByTieneSillasMoviles() {
        when(salonRep.findByTieneSillasMoviles(false)).thenReturn(List.of(salon));
        when(mapper.map(salon, SalonDTO.class)).thenReturn(salonDTO);

        List<SalonDTO> resultado = salonService.findByTieneSillasMoviles(false);

        assertNotNull(resultado);
        assertEquals(1, resultado.size());
        verify(salonRep).findByTieneSillasMoviles(false);
    }

    @Test
    void findByTieneSillasMovilesVacio() {
        when(salonRep.findByTieneSillasMoviles(false)).thenReturn(new ArrayList<>());

        List<SalonDTO> resultado = salonService.findByTieneSillasMoviles(false);

        assertNotNull(resultado);
        assertTrue(resultado.isEmpty());
    }

    @Test
    void findSalonAlreadyTakenSalonExistente() {
        when(salonRep.existsByBloqueAndNumeroSalon(Bloque.A, 101)).thenReturn(true);

        assertTrue(salonService.findSalonAlreadyTaken(Bloque.A, 101));
        verify(salonRep).existsByBloqueAndNumeroSalon(Bloque.A, 101);
    }

    @Test
    void findSalonAlreadyTakenSalonNoExistente() {
        when(salonRep.existsByBloqueAndNumeroSalon(Bloque.A, 101)).thenReturn(false);

        assertFalse(salonService.findSalonAlreadyTaken(Bloque.A, 101));
    }

    @Test
    void deleteByBloqueAndNumeroSalonExistente() {
        try (MockedStatic<LanzadorDeException> staticMock = mockStatic(LanzadorDeException.class)) {
            when(salonRep.existsByBloqueAndNumeroSalon(Bloque.A, 101)).thenReturn(true);

            int resultado = salonService.deleteByBloqueAndNumeroSalon(Bloque.A, 101);

            assertEquals(0, resultado);
            verify(salonRep).deleteByBloqueAndNumeroSalon(Bloque.A, 101);
        }
    }

    @Test
    void deleteByBloqueAndNumeroSalonNoExistente() {
        try (MockedStatic<LanzadorDeException> staticMock = mockStatic(LanzadorDeException.class)) {
            when(salonRep.existsByBloqueAndNumeroSalon(Bloque.A, 101)).thenReturn(false);

            int resultado = salonService.deleteByBloqueAndNumeroSalon(Bloque.A, 101);

            assertEquals(1, resultado);
            verify(salonRep, never()).deleteByBloqueAndNumeroSalon(any(), anyInt());
        }
    }

    @Test
    void setGetSalonRepository() {
        SalonRepository nuevoRepo = mock(SalonRepository.class);

        salonService.setSalonRep(nuevoRepo);

        assertEquals(nuevoRepo, salonService.getSalonRep());
    }

    @Test
    void setGetMapper() {
        ModelMapper nuevoMapper = mock(ModelMapper.class);

        salonService.setMapper(nuevoMapper);

        assertEquals(nuevoMapper, salonService.getMapper());
    }
}