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
import org.springframework.security.crypto.password.PasswordEncoder;

import co.edu.unbosque.horarios.dto.AdministradorDTO;
import co.edu.unbosque.horarios.entity.Administrador;
import co.edu.unbosque.horarios.repository.AdministradorRepository;
import co.edu.unbosque.horarios.service.AdministradorService;
import co.edu.unbosque.horarios.util.LanzadorDeException;

@ExtendWith(MockitoExtension.class)
class AdministradorServiceTest {

    @Mock
    private AdministradorRepository administradorRep;

    @Mock
    private ModelMapper mapper;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private AdministradorService administradorService;

    private AdministradorDTO administradorDTO;
    private Administrador administrador;
    
    @BeforeEach
    void setUp() {
        administradorDTO = new AdministradorDTO();
        administradorDTO.setNombre("Carlos");
        administradorDTO.setApellido("García");
        administradorDTO.setDocumento(87654321L);
        administradorDTO.setCorreo("carlos@unbosque.edu.co");
        administradorDTO.setContrasenia("Admin123*");
        administradorDTO.setCodigoAdministrador("ADM-001");
        administradorDTO.setEstado(true);

        administrador = new Administrador();
        administrador.setNombre("Carlos");
        administrador.setApellido("García");
        administrador.setDocumento(87654321L);
        administrador.setCorreo("carlos@unbosque.edu.co");
        administrador.setContrasenia("hash");
        administrador.setCodigoAdministrador("ADM-001");
        administrador.setEstado(true);
    }

    @Test
    void contarAdministradores() {
        when(administradorRep.count()).thenReturn(2L);

        assertEquals(2L, administradorService.count());
        verify(administradorRep).count();
    }

    @Test
    void contarAdministradoresVacios() {
        when(administradorRep.count()).thenReturn(0L);

        assertEquals(0L, administradorService.count());
    }

    @Test
    void existeIdExistente() {
        try (MockedStatic<LanzadorDeException> staticMock = mockStatic(LanzadorDeException.class)) {
            when(administradorRep.existsById(1L)).thenReturn(true);

            assertTrue(administradorService.exist(1L));
            verify(administradorRep).existsById(1L);
        }
    }

    @Test
    void existeIdNoExistente() {
        try (MockedStatic<LanzadorDeException> staticMock = mockStatic(LanzadorDeException.class)) {
            when(administradorRep.existsById(99L)).thenReturn(false);

            assertFalse(administradorService.exist(99L));
        }
    }

    
    @Test
    void createAdminitrador() {
        try (MockedStatic<LanzadorDeException> staticMock = mockStatic(LanzadorDeException.class)) {
            when(administradorRep.existsByCorreo(administradorDTO.getCorreo())).thenReturn(false);
            when(administradorRep.existsByCodigoAdministrador(
                    administradorDTO.getCodigoAdministrador())).thenReturn(false);
            when(administradorRep.existsByDocumento(administradorDTO.getDocumento())).thenReturn(false);
            when(mapper.map(administradorDTO, Administrador.class)).thenReturn(administrador);
            when(passwordEncoder.encode("Admin123*")).thenReturn("hash");

            int resultado = administradorService.create(administradorDTO);

            assertEquals(0, resultado);
            verify(passwordEncoder).encode("Admin123*");
            verify(administradorRep).save(administrador);
            assertEquals("hash", administrador.getContrasenia());
        }
    }

    @Test
    void getAll() {
        List<Administrador> entidades = List.of(administrador);
        when(administradorRep.findAll()).thenReturn(entidades);
        when(mapper.map(administrador, AdministradorDTO.class)).thenReturn(administradorDTO);

        List<AdministradorDTO> resultado = administradorService.getAll();

        assertNotNull(resultado);
        assertEquals(1, resultado.size());
        assertEquals(administradorDTO, resultado.get(0));
        verify(administradorRep).findAll();
    }

    @Test
    void getAllVacio() {
        when(administradorRep.findAll()).thenReturn(new ArrayList<>());

        List<AdministradorDTO> resultado = administradorService.getAll();

        assertNotNull(resultado);
        assertTrue(resultado.isEmpty());
    }

    @Test
    void deleteByIdExistente() {
        try (MockedStatic<LanzadorDeException> staticMock = mockStatic(LanzadorDeException.class)) {
            when(administradorRep.existsById(1L)).thenReturn(true);

            int resultado = administradorService.deleteById(1L);

            assertEquals(0, resultado);
            verify(administradorRep).deleteById(1L);
        }
    }

    @Test
    void deleteByIdNoExistente() {
        try (MockedStatic<LanzadorDeException> staticMock = mockStatic(LanzadorDeException.class)) {
            when(administradorRep.existsById(99L)).thenReturn(false);

            int resultado = administradorService.deleteById(99L);

            assertEquals(1, resultado);
            verify(administradorRep, never()).deleteById(anyLong());
        }
    }

    @Test
    void updateByIdNoExistente() {
        try (MockedStatic<LanzadorDeException> staticMock = mockStatic(LanzadorDeException.class)) {
            when(administradorRep.findById(99L)).thenReturn(Optional.empty());

            int resultado = administradorService.updateById(99L, administradorDTO);

            assertEquals(1, resultado);
            verify(administradorRep, never()).save(any());
        }
    }

    @Test
    void updateByIdExistente() {
        try (MockedStatic<LanzadorDeException> staticMock = mockStatic(LanzadorDeException.class)) {
            administradorDTO.setContrasenia(null);
            when(administradorRep.findById(1L)).thenReturn(Optional.of(administrador));

            int resultado = administradorService.updateById(1L, administradorDTO);

            assertEquals(0, resultado);
            verify(administradorRep).save(administrador);
            verify(administradorRep, never()).existsByCodigoAdministrador(anyString());
            verify(administradorRep, never()).existsByCorreo(anyString());
            verify(administradorRep, never()).existsByDocumento(anyLong());
            verify(passwordEncoder, never()).encode(anyString());
        }
    }

    @Test
    void updateByIdNuevaContrasenia() {
        try (MockedStatic<LanzadorDeException> staticMock = mockStatic(LanzadorDeException.class)) {
            administradorDTO.setContrasenia("Contrasenia2"); 
            when(administradorRep.findById(1L)).thenReturn(Optional.of(administrador));
            when(passwordEncoder.encode("Contrasenia2")).thenReturn("hash");

            int resultado = administradorService.updateById(1L, administradorDTO);

            assertEquals(0, resultado);
            verify(passwordEncoder).encode("Contrasenia2");
            assertEquals("hash", administrador.getContrasenia());
        }
    }

    @Test
    void updateByIdContraseniaVacia() {
        try (MockedStatic<LanzadorDeException> staticMock = mockStatic(LanzadorDeException.class)) {
            administradorDTO.setContrasenia("   "); 
            when(administradorRep.findById(1L)).thenReturn(Optional.of(administrador));

            int resultado = administradorService.updateById(1L, administradorDTO);

            assertEquals(0, resultado);
            verify(passwordEncoder, never()).encode(anyString());
        }
    }

    @Test
    void updateByIdDiferenteAlExistente() {
        try (MockedStatic<LanzadorDeException> staticMock = mockStatic(LanzadorDeException.class)) {
            Administrador existente = new Administrador();
            existente.setCodigoAdministrador("ADM-999");
            existente.setCorreo("carlos@unbosque.edu.co");
            existente.setDocumento(87654321L); 

            administradorDTO.setContrasenia(null);
            when(administradorRep.findById(1L)).thenReturn(Optional.of(existente));
            when(administradorRep.existsByCodigoAdministrador("ADM-001")).thenReturn(false);

            int resultado = administradorService.updateById(1L, administradorDTO);

            assertEquals(0, resultado);
            verify(administradorRep).existsByCodigoAdministrador("ADM-001");
        }
    }

    @Test
    void updateByIdCorreoDiferente() {
        try (MockedStatic<LanzadorDeException> staticMock = mockStatic(LanzadorDeException.class)) {
            Administrador existente = new Administrador();
            existente.setCodigoAdministrador("ADM-001");        
            existente.setCorreo("random@unbosque.edu.co");         
            existente.setDocumento(87654321L);             

            administradorDTO.setContrasenia(null);
            when(administradorRep.findById(1L)).thenReturn(Optional.of(existente));
            when(administradorRep.existsByCorreo("carlos@unbosque.edu.co")).thenReturn(false);

            int resultado = administradorService.updateById(1L, administradorDTO);

            assertEquals(0, resultado);
            verify(administradorRep).existsByCorreo("carlos@unbosque.edu.co");
        }
    }

    @Test
    void updateByIdDocumentoDiferente() {
        try (MockedStatic<LanzadorDeException> staticMock = mockStatic(LanzadorDeException.class)) {
            Administrador existente = new Administrador();
            existente.setCodigoAdministrador("ADM-001"); 
            existente.setCorreo("carlos@unbosque.edu.co"); 
            existente.setDocumento(11111111L);              

            administradorDTO.setContrasenia(null);
            when(administradorRep.findById(1L)).thenReturn(Optional.of(existente));
            when(administradorRep.existsByDocumento(87654321L)).thenReturn(false);

            int resultado = administradorService.updateById(1L, administradorDTO);

            assertEquals(0, resultado);
            verify(administradorRep).existsByDocumento(87654321L);
        }
    }

    @Test
    void findByCodigoAdministradorExistente() {
        try (MockedStatic<LanzadorDeException> staticMock = mockStatic(LanzadorDeException.class)) {
            when(administradorRep.findByCodigoAdministrador("ADM-001"))
                    .thenReturn(Optional.of(administrador));
            when(mapper.map(administrador, AdministradorDTO.class)).thenReturn(administradorDTO);

            List<AdministradorDTO> resultado =
                    administradorService.findByCodigoAdministrador("ADM-001");

            assertNotNull(resultado);
            assertEquals(1, resultado.size());
            assertEquals(administradorDTO, resultado.get(0));
        }
    }

    @Test
    void findByCodigoAdministradorNoExistente() {
        try (MockedStatic<LanzadorDeException> staticMock = mockStatic(LanzadorDeException.class)) {
            when(administradorRep.findByCodigoAdministrador("ADM-000"))
                    .thenReturn(Optional.empty());

            List<AdministradorDTO> resultado =
                    administradorService.findByCodigoAdministrador("ADM-000");

            assertNotNull(resultado);
            assertTrue(resultado.isEmpty());
        }
    }

    @Test
    void findByCorreoCorreoExistente() {
        try (MockedStatic<LanzadorDeException> staticMock = mockStatic(LanzadorDeException.class)) {
            when(administradorRep.findByCorreo("carlos@unbosque.edu.co"))
                    .thenReturn(Optional.of(administrador));
            when(mapper.map(administrador, AdministradorDTO.class)).thenReturn(administradorDTO);

            List<AdministradorDTO> resultado =
                    administradorService.findByCorreo("carlos@unbosque.edu.co");

            assertNotNull(resultado);
            assertEquals(1, resultado.size());
        }
    }

    @Test
    void findByCorreoCorreoNoExistente() {
        try (MockedStatic<LanzadorDeException> staticMock = mockStatic(LanzadorDeException.class)) {
            when(administradorRep.findByCorreo("noexiste@unbosque.edu.co"))
                    .thenReturn(Optional.empty());

            List<AdministradorDTO> resultado =
                    administradorService.findByCorreo("noexiste@unbosque.edu.co");

            assertNotNull(resultado);
            assertTrue(resultado.isEmpty());
        }
    }

    @Test
    void findByDocumentoExistente() {
        try (MockedStatic<LanzadorDeException> staticMock = mockStatic(LanzadorDeException.class)) {
            when(administradorRep.findByDocumento(87654321L)).thenReturn(Optional.of(administrador));
            when(mapper.map(administrador, AdministradorDTO.class)).thenReturn(administradorDTO);

            List<AdministradorDTO> resultado = administradorService.findByDocumento(87654321L);

            assertNotNull(resultado);
            assertEquals(1, resultado.size());
        }
    }

    @Test
    void findByDocumentoNoExistente() {
        try (MockedStatic<LanzadorDeException> staticMock = mockStatic(LanzadorDeException.class)) {
            when(administradorRep.findByDocumento(99999999L)).thenReturn(Optional.empty());

            List<AdministradorDTO> resultado = administradorService.findByDocumento(99999999L);

            assertNotNull(resultado);
            assertTrue(resultado.isEmpty());
        }
    }

    @Test
    void findCodigoAdministradorAlreadyTakenExistente() {
        try (MockedStatic<LanzadorDeException> staticMock = mockStatic(LanzadorDeException.class)) {
            when(administradorRep.existsByCodigoAdministrador("ADM-001")).thenReturn(true);

            assertTrue(administradorService.findCodigoAdministradorAlreadyTaken("ADM-001"));
            verify(administradorRep).existsByCodigoAdministrador("ADM-001");
        }
    }

    @Test
    void findCodigoAdministradorAlreadyTakenNoExistente() {
        try (MockedStatic<LanzadorDeException> staticMock = mockStatic(LanzadorDeException.class)) {
            when(administradorRep.existsByCodigoAdministrador("ADM-999")).thenReturn(false);

            assertFalse(administradorService.findCodigoAdministradorAlreadyTaken("ADM-999"));
        }
    }

    @Test
    void findCorreoAlreadyTakenCorreoExistente() {
        try (MockedStatic<LanzadorDeException> staticMock = mockStatic(LanzadorDeException.class)) {
            when(administradorRep.existsByCorreo("carlos@unbosque.edu.co")).thenReturn(true);

            assertTrue(administradorService.findCorreoAlreadyTaken("carlos@unbosque.edu.co"));
        }
    }

    @Test
    void findCorreoAlreadyTakenCorreoNoExistente() {
        try (MockedStatic<LanzadorDeException> staticMock = mockStatic(LanzadorDeException.class)) {
            when(administradorRep.existsByCorreo("libre@unbosque.edu.co")).thenReturn(false);

            assertFalse(administradorService.findCorreoAlreadyTaken("libre@unbosque.edu.co"));
        }
    }

    @Test
    void findDocumentoAlreadyTakenDocumenteExistente() {
        try (MockedStatic<LanzadorDeException> staticMock = mockStatic(LanzadorDeException.class)) {
            when(administradorRep.existsByDocumento(87654321L)).thenReturn(true);

            assertTrue(administradorService.findDocumentoAlreadyTaken(87654321L));
        }
    }

    @Test
    void findDocumentoAlreadyTakenDocumenteNoExistente() {
        try (MockedStatic<LanzadorDeException> staticMock = mockStatic(LanzadorDeException.class)) {
            when(administradorRep.existsByDocumento(87654321L)).thenReturn(false);

            assertFalse(administradorService.findDocumentoAlreadyTaken(87654321L));
        }
    }

    @Test
    void loginCorreoNoExistente() {
        try (MockedStatic<LanzadorDeException> staticMock = mockStatic(LanzadorDeException.class)) {
            when(administradorRep.findByCorreo("noexiste@unbosque.edu.co"))
                    .thenReturn(Optional.empty());

            int resultado = administradorService.login(
                    "noexiste@unbosque.edu.co", "Admin123*");

            assertEquals(2, resultado);
            verify(passwordEncoder, never()).matches(anyString(), anyString());
        }
    }

    @Test
    void loginaAdministradorDesactivado() {
        try (MockedStatic<LanzadorDeException> staticMock = mockStatic(LanzadorDeException.class)) {
            administrador.setEstado(false);
            when(administradorRep.findByCorreo("carlos@unbosque.edu.co"))
                    .thenReturn(Optional.of(administrador));

            int resultado = administradorService.login(
                    "carlos@unbosque.edu.co", "Admin123*");

            assertEquals(3, resultado);
            verify(passwordEncoder, never()).matches(anyString(), anyString());
        }
    }

    @Test
    void loginContraseniaCorrecta() {
        try (MockedStatic<LanzadorDeException> staticMock = mockStatic(LanzadorDeException.class)) {
            administrador.setEstado(true);
            when(administradorRep.findByCorreo("carlos@unbosque.edu.co"))
                    .thenReturn(Optional.of(administrador));
            when(passwordEncoder.matches("Admin123*", administrador.getContrasenia()))
                    .thenReturn(true);

            int resultado = administradorService.login(
                    "carlos@unbosque.edu.co", "Admin123*");

            assertEquals(0, resultado);
            verify(passwordEncoder).matches("Admin123*", administrador.getContrasenia());
        }
    }

    @Test
    void loginContraseniaIncorrecta() {
        try (MockedStatic<LanzadorDeException> staticMock = mockStatic(LanzadorDeException.class)) {
            administrador.setEstado(true);
            when(administradorRep.findByCorreo("carlos@unbosque.edu.co"))
                    .thenReturn(Optional.of(administrador));
            when(passwordEncoder.matches("ClaveErrada", administrador.getContrasenia()))
                    .thenReturn(false);

            int resultado = administradorService.login(
                    "carlos@unbosque.edu.co", "ClaveErrada");

            assertEquals(1, resultado);
        }
    }

    @Test
    void actualizarEstadoIdExistente() {
        try (MockedStatic<LanzadorDeException> staticMock = mockStatic(LanzadorDeException.class)) {
            administrador.setEstado(true);
            when(administradorRep.findById(1L)).thenReturn(Optional.of(administrador));

            int resultado = administradorService.actualizarEstado(1L, false);

            assertEquals(0, resultado);
            assertFalse(administrador.isEstado());
            verify(administradorRep).save(administrador);
        }
    }

    @Test
    void actualizarEstadoIdNoExistente() {
        try (MockedStatic<LanzadorDeException> staticMock = mockStatic(LanzadorDeException.class)) {
            when(administradorRep.findById(99L)).thenReturn(Optional.empty());

            int resultado = administradorService.actualizarEstado(99L, false);

            assertEquals(1, resultado);
            verify(administradorRep, never()).save(any());
        }
    }

    @Test
    void deleteByCodigoAdministrador() {
        try (MockedStatic<LanzadorDeException> staticMock = mockStatic(LanzadorDeException.class)) {
            when(administradorRep.existsByCodigoAdministrador("ADM-001")).thenReturn(true);

            int resultado = administradorService.deleteByCodigoAdministrador("ADM-001");

            assertEquals(0, resultado);
            verify(administradorRep).deleteByCodigoAdministrador("ADM-001");
        }
    }

    @Test
    void deleteByCodigoAdministradorNoExistente() {
        try (MockedStatic<LanzadorDeException> staticMock = mockStatic(LanzadorDeException.class)) {
            when(administradorRep.existsByCodigoAdministrador("ADM-999")).thenReturn(false);

            int resultado = administradorService.deleteByCodigoAdministrador("ADM-999");

            assertEquals(1, resultado);
            verify(administradorRep, never()).deleteByCodigoAdministrador(anyString());
        }
    }

    @Test
    void setGetAdministradorRepository() {
        AdministradorRepository nuevoRepo = mock(AdministradorRepository.class);

        administradorService.setAdministradorRep(nuevoRepo);

        assertEquals(nuevoRepo, administradorService.getAdministradorRep());
    }

    @Test
    void setGetMapper() {
        ModelMapper nuevoMapper = mock(ModelMapper.class);

        administradorService.setMapper(nuevoMapper);

        assertEquals(nuevoMapper, administradorService.getMapper());
    }

    @Test
    void setGetPasswordEncoder() {
        PasswordEncoder nuevoEncoder = mock(PasswordEncoder.class);

        administradorService.setPasswordEncoder(nuevoEncoder);

        assertEquals(nuevoEncoder, administradorService.getPasswordEncoder());
    }
}