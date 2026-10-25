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

import co.edu.unbosque.horarios.dto.AsignaturaDTO;
import co.edu.unbosque.horarios.entity.Asignatura;
import co.edu.unbosque.horarios.repository.AsignaturaRepository;
import co.edu.unbosque.horarios.service.AsignaturaService;
import co.edu.unbosque.horarios.util.LanzadorDeException;

@ExtendWith(MockitoExtension.class)
class AsignaturaServiceTest {

    @Mock
    private AsignaturaRepository asignaturaRep;

    @Mock
    private ModelMapper mapper;

    @InjectMocks
    private AsignaturaService asignaturaService;

    private AsignaturaDTO asignaturaDTO;
    private Asignatura asignatura;

    @BeforeEach
    void setUp() {
        asignaturaDTO = new AsignaturaDTO();
        asignaturaDTO.setNombre("Cálculo Diferencial");
        asignaturaDTO.setNumCreditos(3);
        asignaturaDTO.setFrecuenciaSemanal(3);
        asignaturaDTO.setRequiereComputadores(false);
        asignaturaDTO.setRequiereSillasMoviles(true);

        asignatura = new Asignatura();
        asignatura.setNombre("Cálculo Diferencial");
        asignatura.setNumCreditos(3);
        asignatura.setFrecuenciaSemanal(3);
        asignatura.setRequiereComputadores(false);
        asignatura.setRequiereSillasMoviles(true);
    }

    @Test
    void countAsignaturas() {
        when(asignaturaRep.count()).thenReturn(4L);

        assertEquals(4L, asignaturaService.count());
        verify(asignaturaRep).count();
    }

    @Test
    void countAsignaturasVacio() {
        when(asignaturaRep.count()).thenReturn(0L);

        assertEquals(0L, asignaturaService.count());
    }

    @Test
    void existIdExistente() {
        try (MockedStatic<LanzadorDeException> staticMock = mockStatic(LanzadorDeException.class)) {
            when(asignaturaRep.existsById(1L)).thenReturn(true);

            assertTrue(asignaturaService.exist(1L));
            verify(asignaturaRep).existsById(1L);
        }
    }

    @Test
    void existIdNoExistente() {
        try (MockedStatic<LanzadorDeException> staticMock = mockStatic(LanzadorDeException.class)) {
            when(asignaturaRep.existsById(99L)).thenReturn(false);

            assertFalse(asignaturaService.exist(99L));
        }
    }

    @Test
    void createAsignatura() {
        try (MockedStatic<LanzadorDeException> staticMock = mockStatic(LanzadorDeException.class)) {
            when(asignaturaRep.existsByNombre(asignaturaDTO.getNombre())).thenReturn(false);
            when(mapper.map(asignaturaDTO, Asignatura.class)).thenReturn(asignatura);

            int resultado = asignaturaService.create(asignaturaDTO);

            assertEquals(0, resultado);
            verify(asignaturaRep).save(asignatura);
        }
    }

    @Test
    void getAll() {
        List<Asignatura> entidades = List.of(asignatura);
        when(asignaturaRep.findAll()).thenReturn(entidades);
        when(mapper.map(asignatura, AsignaturaDTO.class)).thenReturn(asignaturaDTO);

        List<AsignaturaDTO> resultado = asignaturaService.getAll();

        assertNotNull(resultado);
        assertEquals(1, resultado.size());
        assertEquals(asignaturaDTO, resultado.get(0));
        verify(asignaturaRep).findAll();
    }

    @Test
    void getAllVacio() {
        when(asignaturaRep.findAll()).thenReturn(new ArrayList<>());

        List<AsignaturaDTO> resultado = asignaturaService.getAll();

        assertNotNull(resultado);
        assertTrue(resultado.isEmpty());
    }

    @Test
    void deleteByIdExistente() {
        try (MockedStatic<LanzadorDeException> staticMock = mockStatic(LanzadorDeException.class)) {
            when(asignaturaRep.existsById(1L)).thenReturn(true);

            int resultado = asignaturaService.deleteById(1L);

            assertEquals(0, resultado);
            verify(asignaturaRep).deleteById(1L);
        }
    }

    @Test
    void deleteByIdNoExistente() {
        try (MockedStatic<LanzadorDeException> staticMock = mockStatic(LanzadorDeException.class)) {
            when(asignaturaRep.existsById(99L)).thenReturn(false);

            int resultado = asignaturaService.deleteById(99L);

            assertEquals(1, resultado);
            verify(asignaturaRep, never()).deleteById(anyLong());
        }
    }


    @Test
    void updateByIdExistente() {
        try (MockedStatic<LanzadorDeException> staticMock = mockStatic(LanzadorDeException.class)) {
        	
            when(asignaturaRep.findById(1L)).thenReturn(Optional.of(asignatura));

            int resultado = asignaturaService.updateById(1L, asignaturaDTO);

            assertEquals(0, resultado);
            verify(asignaturaRep).save(asignatura);

            verify(asignaturaRep, never()).existsByNombre(anyString());
        }
    }

    @Test
    void updateByIdNoExistente() {
        try (MockedStatic<LanzadorDeException> staticMock = mockStatic(LanzadorDeException.class)) {
            when(asignaturaRep.findById(99L)).thenReturn(Optional.empty());

            int resultado = asignaturaService.updateById(99L, asignaturaDTO);

            assertEquals(1, resultado);
            verify(asignaturaRep, never()).save(any());
        }
    }

    @Test
    void updateByIdDiferente() {
        try (MockedStatic<LanzadorDeException> staticMock = mockStatic(LanzadorDeException.class)) {
            Asignatura existente = new Asignatura();
            existente.setNombre("Álgebra Lineal"); 
            existente.setNumCreditos(2);
            existente.setFrecuenciaSemanal(2);

            when(asignaturaRep.findById(1L)).thenReturn(Optional.of(existente));
            when(asignaturaRep.existsByNombre(asignaturaDTO.getNombre())).thenReturn(false);

            int resultado = asignaturaService.updateById(1L, asignaturaDTO);

            assertEquals(0, resultado);
            verify(asignaturaRep).existsByNombre(asignaturaDTO.getNombre());
            verify(asignaturaRep).save(existente);
        }
    }

    @Test
    void updateByIdDiferenteAlExistente() {
        try (MockedStatic<LanzadorDeException> staticMock = mockStatic(LanzadorDeException.class)) {
            Asignatura existente = new Asignatura();
            existente.setNombre("Nombre Viejo");
            existente.setNumCreditos(1);
            existente.setFrecuenciaSemanal(1);

            when(asignaturaRep.findById(1L)).thenReturn(Optional.of(existente));
            when(asignaturaRep.existsByNombre(asignaturaDTO.getNombre())).thenReturn(false);

            asignaturaService.updateById(1L, asignaturaDTO);

            assertEquals("Cálculo Diferencial", existente.getNombre());
            assertEquals(3, existente.getNumCreditos());
            assertEquals(3, existente.getFrecuenciaSemanal());
        }
    }


    @Test
    void findByNombreExistente() {
        try (MockedStatic<LanzadorDeException> staticMock = mockStatic(LanzadorDeException.class)) {
            when(asignaturaRep.findByNombre("Cálculo Diferencial"))
                    .thenReturn(Optional.of(asignatura));
            when(mapper.map(asignatura, AsignaturaDTO.class)).thenReturn(asignaturaDTO);

            List<AsignaturaDTO> resultado =
                    asignaturaService.findByNombre("Cálculo Diferencial");

            assertNotNull(resultado);
            assertEquals(1, resultado.size());
            assertEquals(asignaturaDTO, resultado.get(0));
        }
    }

    @Test
    void findByNombreNoExistente() {
        try (MockedStatic<LanzadorDeException> staticMock = mockStatic(LanzadorDeException.class)) {
            when(asignaturaRep.findByNombre("Inexistente")).thenReturn(Optional.empty());

            List<AsignaturaDTO> resultado = asignaturaService.findByNombre("Inexistente");

            assertNotNull(resultado);
            assertTrue(resultado.isEmpty());
        }
    }

    @Test
    void findByNumCreditosExistente() {
        try (MockedStatic<LanzadorDeException> staticMock = mockStatic(LanzadorDeException.class)) {
            when(asignaturaRep.findByNumCreditos(3)).thenReturn(Optional.of(asignatura));
            when(mapper.map(asignatura, AsignaturaDTO.class)).thenReturn(asignaturaDTO);

            List<AsignaturaDTO> resultado = asignaturaService.findByNumCreditos(3);

            assertNotNull(resultado);
            assertEquals(1, resultado.size());
            assertEquals(asignaturaDTO, resultado.get(0));
        }
    }

    @Test
    void findByNumCreditosNoExistente() {
        try (MockedStatic<LanzadorDeException> staticMock = mockStatic(LanzadorDeException.class)) {
            when(asignaturaRep.findByNumCreditos(10)).thenReturn(Optional.empty());

            List<AsignaturaDTO> resultado = asignaturaService.findByNumCreditos(10);

            assertNotNull(resultado);
            assertTrue(resultado.isEmpty());
        }
    }

    @Test
    void findByFrecuenciaSemanalExistente() {
        try (MockedStatic<LanzadorDeException> staticMock = mockStatic(LanzadorDeException.class)) {
            when(asignaturaRep.findByFrecuenciaSemanal(3)).thenReturn(Optional.of(asignatura));
            when(mapper.map(asignatura, AsignaturaDTO.class)).thenReturn(asignaturaDTO);

            List<AsignaturaDTO> resultado = asignaturaService.findByFrecuenciaSemanal(3);

            assertNotNull(resultado);
            assertEquals(1, resultado.size());
            assertEquals(asignaturaDTO, resultado.get(0));
        }
    }

    @Test
    void findByFrecuenciaSemanalNoExistente() {
        try (MockedStatic<LanzadorDeException> staticMock = mockStatic(LanzadorDeException.class)) {
            when(asignaturaRep.findByFrecuenciaSemanal(7)).thenReturn(Optional.empty());

            List<AsignaturaDTO> resultado = asignaturaService.findByFrecuenciaSemanal(7);

            assertNotNull(resultado);
            assertTrue(resultado.isEmpty());
        }
    }

    @Test
    void findNombreAlreadyTakenNombreExistente() {
        try (MockedStatic<LanzadorDeException> staticMock = mockStatic(LanzadorDeException.class)) {
            when(asignaturaRep.existsByNombre("Cálculo Diferencial")).thenReturn(true);

            assertTrue(asignaturaService.findNombreAlreadyTaken("Cálculo Diferencial"));
            verify(asignaturaRep).existsByNombre("Cálculo Diferencial");
        }
    }

    @Test
    void findNombreAlreadyTakenNombreNoExistente() {
        try (MockedStatic<LanzadorDeException> staticMock = mockStatic(LanzadorDeException.class)) {
            when(asignaturaRep.existsByNombre("Nueva Materia")).thenReturn(false);

            assertFalse(asignaturaService.findNombreAlreadyTaken("Nueva Materia"));
        }
    }

    @Test
    void deleteByNombreExistente() {
        try (MockedStatic<LanzadorDeException> staticMock = mockStatic(LanzadorDeException.class)) {
            when(asignaturaRep.existsByNombre("Cálculo Diferencial")).thenReturn(true);

            int resultado = asignaturaService.deleteByNombre("Cálculo Diferencial");

            assertEquals(0, resultado);
            verify(asignaturaRep).deleteByNombre("Cálculo Diferencial");
        }
    }

    @Test
    void deleteByNombreNoExistente() {
        try (MockedStatic<LanzadorDeException> staticMock = mockStatic(LanzadorDeException.class)) {
            when(asignaturaRep.existsByNombre("Inexistente")).thenReturn(false);

            int resultado = asignaturaService.deleteByNombre("Inexistente");

            assertEquals(1, resultado);
            verify(asignaturaRep, never()).deleteByNombre(anyString());
        }
    }

    @Test
    void setGetAsignaturaRepository() {
        AsignaturaRepository nuevoRepo = mock(AsignaturaRepository.class);

        asignaturaService.setAsignaturaRep(nuevoRepo);

        assertEquals(nuevoRepo, asignaturaService.getAsignaturaRep());
    }

    @Test
    void setGetMapper() {
        ModelMapper nuevoMapper = mock(ModelMapper.class);

        asignaturaService.setMapper(nuevoMapper);

        assertEquals(nuevoMapper, asignaturaService.getMapper());
    }
}