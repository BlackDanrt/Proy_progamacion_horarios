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
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;

import co.edu.unbosque.horarios.dto.AsignaturaDTO;
import co.edu.unbosque.horarios.dto.FranjaHorariaDTO;
import co.edu.unbosque.horarios.dto.GrupoDTO;
import co.edu.unbosque.horarios.dto.ParametrosSistemaDTO;
import co.edu.unbosque.horarios.dto.ProfesorDTO;
import co.edu.unbosque.horarios.dto.SalonDTO;
import co.edu.unbosque.horarios.entity.Asignatura;
import co.edu.unbosque.horarios.entity.FranjaHoraria;
import co.edu.unbosque.horarios.entity.Grupo;
import co.edu.unbosque.horarios.entity.Profesor;
import co.edu.unbosque.horarios.entity.Salon;
import co.edu.unbosque.horarios.exception.AsignaturaInvalidaException;
import co.edu.unbosque.horarios.exception.CapacidadMinimaInvalidaException;
import co.edu.unbosque.horarios.exception.CapacidadSalonInsuficienteException;
import co.edu.unbosque.horarios.exception.CargaHorariaExcedidaException;
import co.edu.unbosque.horarios.exception.ConflictoHorarioProfesorException;
import co.edu.unbosque.horarios.exception.ConflictoHorarioSalonException;
import co.edu.unbosque.horarios.exception.NumeroGrupoInvalidoException;
import co.edu.unbosque.horarios.exception.ProfesorInvalidoException;
import co.edu.unbosque.horarios.exception.ProfesorNoCalificadoException;
import co.edu.unbosque.horarios.exception.ProfesorNoDisponibleException;
import co.edu.unbosque.horarios.exception.RequisitosSalonInvalidosException;
import co.edu.unbosque.horarios.exception.SalonInvalidoException;
import co.edu.unbosque.horarios.exception.SesionesFrecuenciaInvalidaException;
import co.edu.unbosque.horarios.exception.SesionesInvalidasException;
import co.edu.unbosque.horarios.repository.AsignaturaRepository;
import co.edu.unbosque.horarios.repository.FranjaHorariaRepository;
import co.edu.unbosque.horarios.repository.GrupoRepository;
import co.edu.unbosque.horarios.repository.ProfesorRepository;
import co.edu.unbosque.horarios.repository.SalonRepository;
import co.edu.unbosque.horarios.service.AuditoriaService;
import co.edu.unbosque.horarios.service.GrupoService;
import co.edu.unbosque.horarios.service.ParametrosSistemaService;
import co.edu.unbosque.horarios.util.enums.Bloque;
import co.edu.unbosque.horarios.util.enums.Dia;
import co.edu.unbosque.horarios.util.enums.Especialidad;
import co.edu.unbosque.horarios.util.enums.EstadoGrupo;
import co.edu.unbosque.horarios.util.enums.Servicio;
import co.edu.unbosque.horarios.util.enums.TipoAccion;

@ExtendWith(MockitoExtension.class)
class GrupoServiceTest {

    @Mock private GrupoRepository grupoRep;
    @Mock private ProfesorRepository profesorRep;
    @Mock private AsignaturaRepository asignaturaRep;
    @Mock private SalonRepository salonRep;
    @Mock private FranjaHorariaRepository franjaRep;
    @Mock private AuditoriaService auditoriaService;
    @Mock private ModelMapper mapper;
    @Mock private ParametrosSistemaService parametrosSistemaService;

    @InjectMocks
    private GrupoService grupoService;

    private ParametrosSistemaDTO parametros;

    private AsignaturaDTO asignaturaDTO;
    private Asignatura asignatura;

    private ProfesorDTO profesorDTO;
    private Profesor profesor;

    private SalonDTO salonDTO;
    private Salon salon;

    private FranjaHorariaDTO sesionLunesDTO;
    private FranjaHoraria sesionLunes;

    private FranjaHorariaDTO sesionMiercolesDTO;
    private FranjaHoraria sesionMiercoles;

    private GrupoDTO grupoDTO;

    @BeforeEach
    void setUp() {

        parametros = new ParametrosSistemaDTO();
        parametros.setHoraInicioEntreSemana(LocalTime.of(7, 0));
        parametros.setHoraFinEntreSemana(LocalTime.of(22, 0));
        parametros.setHoraInicioSabado(LocalTime.of(7, 0));
        parametros.setHoraFinSabado(LocalTime.of(13, 0));
        parametros.setHoraInicioAlmuerzo(LocalTime.of(12, 0));
        parametros.setHoraFinAlmuerzo(LocalTime.of(13, 0));
        parametros.setMaxSesionesPorSemana(4);
        parametros.setDuracionSesionMinutos(120);
        parametros.setCapacidadMaximaGrupo(40);
        parametros.setToleranciaCapacidad(10);
        parametros.setMinEstudiantesParaAbrir(10);

        asignaturaDTO = new AsignaturaDTO();
        asignaturaDTO.setId(1L);

        asignatura = new Asignatura("Bases de Datos I", 3, 2, false, false);
        asignatura.setId(1L);
        asignatura.setEspecialidadRequerida(null);

        profesorDTO = new ProfesorDTO();
        profesorDTO.setId(1L);

        profesor = new Profesor();
        profesor.setId(1L);
        profesor.setHorasMaximasSemanales(40);
        profesor.setEspecialidades(new ArrayList<>());

        profesor.setDisponibilidadHoraria(new ArrayList<>(List.of(
                new FranjaHoraria(Dia.LUNES, LocalTime.of(7, 0), LocalTime.of(22, 0)),
                new FranjaHoraria(Dia.MIERCOLES, LocalTime.of(7, 0), LocalTime.of(22, 0)))));

        salonDTO = new SalonDTO();
        salonDTO.setId(1L);

        salon = new Salon();
        salon.setId(1L);
        salon.setBloque(Bloque.A);
        salon.setNumeroSalon(101);
        salon.setCapacidad(40);
        salon.setTieneComputadores(false);
        salon.setTieneSillasMoviles(false);

        sesionLunesDTO = new FranjaHorariaDTO(Dia.LUNES, LocalTime.of(7, 0), LocalTime.of(9, 0));
        sesionLunes = new FranjaHoraria(Dia.LUNES, LocalTime.of(7, 0), LocalTime.of(9, 0));

        sesionMiercolesDTO = new FranjaHorariaDTO(Dia.MIERCOLES, LocalTime.of(7, 0), LocalTime.of(9, 0));
        sesionMiercoles = new FranjaHoraria(Dia.MIERCOLES, LocalTime.of(7, 0), LocalTime.of(9, 0));

        grupoDTO = new GrupoDTO();
        grupoDTO.setNumeroGrupo(1);
        grupoDTO.setCapacidadMinima(10);
        grupoDTO.setCapacidadMaxima(40);
        grupoDTO.setInscritos(35);
        grupoDTO.setEstado(EstadoGrupo.ACTIVO);
        grupoDTO.setPeriodo("2026-2");
        grupoDTO.setAsignatura(asignaturaDTO);
        grupoDTO.setProfesor(profesorDTO);
        grupoDTO.setSalon(salonDTO);
        grupoDTO.setSesiones(new ArrayList<>(List.of(sesionLunesDTO, sesionMiercolesDTO)));
    }

    @Test
    void contarGrupos() {
        when(grupoRep.count()).thenReturn(3L);
        assertEquals(3L, grupoService.count());
    }

    @Test
    void contarGruposVacios() {
        when(grupoRep.count()).thenReturn(0L);
        assertEquals(0L, grupoService.count());
    }

    @Test
    void existeIdExistente() {
        when(grupoRep.existsById(1L)).thenReturn(true);
        assertTrue(grupoService.exist(1L));
    }

    @Test
    void existeIdNoExistente() {
        when(grupoRep.existsById(99L)).thenReturn(false);
        assertFalse(grupoService.exist(99L));
    }

 

    @Test
    void createExitoso() {
        when(parametrosSistemaService.get()).thenReturn(parametros);
        when(profesorRep.findById(1L)).thenReturn(Optional.of(profesor));
        when(salonRep.findById(1L)).thenReturn(Optional.of(salon));
        when(asignaturaRep.findById(1L)).thenReturn(Optional.of(asignatura));
        when(mapper.map(sesionLunesDTO, FranjaHoraria.class)).thenReturn(sesionLunes);
        when(mapper.map(sesionMiercolesDTO, FranjaHoraria.class)).thenReturn(sesionMiercoles);
        when(franjaRep.save(any(FranjaHoraria.class))).thenAnswer(inv -> inv.getArgument(0));

        int resultado = grupoService.create(grupoDTO);

        assertEquals(0, resultado);
        verify(grupoRep).save(any(Grupo.class));
        verify(auditoriaService).preAccion(TipoAccion.CREATE, Servicio.GRUPO);
        verify(auditoriaService).create(any());
    }



    @Test
    void createProfesorNoExiste() {
        when(parametrosSistemaService.get()).thenReturn(parametros);
        when(profesorRep.findById(1L)).thenReturn(Optional.empty());

        assertThrows(ProfesorInvalidoException.class, () -> grupoService.create(grupoDTO));
    }

    @Test
    void createSalonNoExiste() {
        when(parametrosSistemaService.get()).thenReturn(parametros);
        when(profesorRep.findById(1L)).thenReturn(Optional.of(profesor));
        when(salonRep.findById(1L)).thenReturn(Optional.empty());

        assertThrows(SalonInvalidoException.class, () -> grupoService.create(grupoDTO));
    }

    @Test
    void createAsignaturaNoExiste() {
        when(parametrosSistemaService.get()).thenReturn(parametros);
        when(profesorRep.findById(1L)).thenReturn(Optional.of(profesor));
        when(salonRep.findById(1L)).thenReturn(Optional.of(salon));
        when(asignaturaRep.findById(1L)).thenReturn(Optional.empty());

        assertThrows(AsignaturaInvalidaException.class, () -> grupoService.create(grupoDTO));
    }

    @Test
    void createNumeroGrupoDuplicado() {
        when(parametrosSistemaService.get()).thenReturn(parametros);
        when(profesorRep.findById(1L)).thenReturn(Optional.of(profesor));
        when(salonRep.findById(1L)).thenReturn(Optional.of(salon));
        when(asignaturaRep.findById(1L)).thenReturn(Optional.of(asignatura));
        when(mapper.map(sesionLunesDTO, FranjaHoraria.class)).thenReturn(sesionLunes);
        when(mapper.map(sesionMiercolesDTO, FranjaHoraria.class)).thenReturn(sesionMiercoles);
        when(grupoRep.existsByNumeroGrupoAndAsignaturaAndPeriodo(1, asignatura, "2026-2")).thenReturn(true);

        assertThrows(NumeroGrupoInvalidoException.class, () -> grupoService.create(grupoDTO));
    }

   

    @Test
    void createCapacidadMinimaMenorAlParametro() {
        grupoDTO.setCapacidadMinima(5); 

        when(parametrosSistemaService.get()).thenReturn(parametros);
        when(profesorRep.findById(1L)).thenReturn(Optional.of(profesor));
        when(salonRep.findById(1L)).thenReturn(Optional.of(salon));
        when(asignaturaRep.findById(1L)).thenReturn(Optional.of(asignatura));
        when(mapper.map(sesionLunesDTO, FranjaHoraria.class)).thenReturn(sesionLunes);
        when(mapper.map(sesionMiercolesDTO, FranjaHoraria.class)).thenReturn(sesionMiercoles);

        assertThrows(CapacidadMinimaInvalidaException.class, () -> grupoService.create(grupoDTO));
    }

    @Test
    void createSesionesNoCoincidenConFrecuenciaAsignatura() {
   
        grupoDTO.setSesiones(new ArrayList<>(List.of(sesionLunesDTO)));

        when(parametrosSistemaService.get()).thenReturn(parametros);
        when(profesorRep.findById(1L)).thenReturn(Optional.of(profesor));
        when(salonRep.findById(1L)).thenReturn(Optional.of(salon));
        when(asignaturaRep.findById(1L)).thenReturn(Optional.of(asignatura));
        when(mapper.map(sesionLunesDTO, FranjaHoraria.class)).thenReturn(sesionLunes);

        assertThrows(SesionesFrecuenciaInvalidaException.class, () -> grupoService.create(grupoDTO));
    }

    @Test
    void createSalonConCapacidadInsuficiente() {
        salon.setCapacidad(30);

        when(parametrosSistemaService.get()).thenReturn(parametros);
        when(profesorRep.findById(1L)).thenReturn(Optional.of(profesor));
        when(salonRep.findById(1L)).thenReturn(Optional.of(salon));
        when(asignaturaRep.findById(1L)).thenReturn(Optional.of(asignatura));
        when(mapper.map(sesionLunesDTO, FranjaHoraria.class)).thenReturn(sesionLunes);
        when(mapper.map(sesionMiercolesDTO, FranjaHoraria.class)).thenReturn(sesionMiercoles);

        assertThrows(CapacidadSalonInsuficienteException.class, () -> grupoService.create(grupoDTO));
    }

    @Test
    void createSalonSinRequisitosDeLaAsignatura() {
        asignatura.setRequiereComputadores(true); 

        when(parametrosSistemaService.get()).thenReturn(parametros);
        when(profesorRep.findById(1L)).thenReturn(Optional.of(profesor));
        when(salonRep.findById(1L)).thenReturn(Optional.of(salon));
        when(asignaturaRep.findById(1L)).thenReturn(Optional.of(asignatura));
        when(mapper.map(sesionLunesDTO, FranjaHoraria.class)).thenReturn(sesionLunes);
        when(mapper.map(sesionMiercolesDTO, FranjaHoraria.class)).thenReturn(sesionMiercoles);

        assertThrows(RequisitosSalonInvalidosException.class, () -> grupoService.create(grupoDTO));
    }

    @Test
    void createProfesorNoCalificadoParaLaAsignatura() {
        asignatura.setEspecialidadRequerida(Especialidad.BASES_DE_DATOS);

        when(parametrosSistemaService.get()).thenReturn(parametros);
        when(profesorRep.findById(1L)).thenReturn(Optional.of(profesor));
        when(salonRep.findById(1L)).thenReturn(Optional.of(salon));
        when(asignaturaRep.findById(1L)).thenReturn(Optional.of(asignatura));
        when(mapper.map(sesionLunesDTO, FranjaHoraria.class)).thenReturn(sesionLunes);
        when(mapper.map(sesionMiercolesDTO, FranjaHoraria.class)).thenReturn(sesionMiercoles);

        assertThrows(ProfesorNoCalificadoException.class, () -> grupoService.create(grupoDTO));
    }

    @Test
    void createProfesorSinDisponibilidadParaLasSesiones() {
        profesor.setDisponibilidadHoraria(new ArrayList<>()); 

        when(parametrosSistemaService.get()).thenReturn(parametros);
        when(profesorRep.findById(1L)).thenReturn(Optional.of(profesor));
        when(salonRep.findById(1L)).thenReturn(Optional.of(salon));
        when(asignaturaRep.findById(1L)).thenReturn(Optional.of(asignatura));
        when(mapper.map(sesionLunesDTO, FranjaHoraria.class)).thenReturn(sesionLunes);
        when(mapper.map(sesionMiercolesDTO, FranjaHoraria.class)).thenReturn(sesionMiercoles);

        assertThrows(ProfesorNoDisponibleException.class, () -> grupoService.create(grupoDTO));
    }

    @Test
    void createConCruceDeHorarioDelProfesor() {
        Grupo grupoExistenteProfesor = new Grupo(asignatura, 99, 10, 40, profesor, salon,
                new ArrayList<>(List.of(new FranjaHoraria(Dia.LUNES, LocalTime.of(8, 0), LocalTime.of(10, 0)))),
                EstadoGrupo.ACTIVO);
        grupoExistenteProfesor.setId(2L);
        grupoExistenteProfesor.setPeriodo("2026-2");

        when(parametrosSistemaService.get()).thenReturn(parametros);
        when(profesorRep.findById(1L)).thenReturn(Optional.of(profesor));
        when(salonRep.findById(1L)).thenReturn(Optional.of(salon));
        when(asignaturaRep.findById(1L)).thenReturn(Optional.of(asignatura));
        when(mapper.map(sesionLunesDTO, FranjaHoraria.class)).thenReturn(sesionLunes);
        when(mapper.map(sesionMiercolesDTO, FranjaHoraria.class)).thenReturn(sesionMiercoles);
        when(grupoRep.findByProfesor(profesor)).thenReturn(List.of(grupoExistenteProfesor));

        assertThrows(ConflictoHorarioProfesorException.class, () -> grupoService.create(grupoDTO));
    }

    @Test
    void createConCruceDeHorarioDelSalon() {
        Grupo grupoExistenteSalon = new Grupo(asignatura, 98, 10, 40, profesor, salon,
                new ArrayList<>(List.of(new FranjaHoraria(Dia.MIERCOLES, LocalTime.of(8, 0), LocalTime.of(10, 0)))),
                EstadoGrupo.ACTIVO);
        grupoExistenteSalon.setId(3L);
        grupoExistenteSalon.setPeriodo("2026-2");

        when(parametrosSistemaService.get()).thenReturn(parametros);
        when(profesorRep.findById(1L)).thenReturn(Optional.of(profesor));
        when(salonRep.findById(1L)).thenReturn(Optional.of(salon));
        when(asignaturaRep.findById(1L)).thenReturn(Optional.of(asignatura));
        when(mapper.map(sesionLunesDTO, FranjaHoraria.class)).thenReturn(sesionLunes);
        when(mapper.map(sesionMiercolesDTO, FranjaHoraria.class)).thenReturn(sesionMiercoles);
        when(grupoRep.findBySalon(salon)).thenReturn(List.of(grupoExistenteSalon));

        assertThrows(ConflictoHorarioSalonException.class, () -> grupoService.create(grupoDTO));
    }

    @Test
    void createExcedeCargaHorariaDelProfesor() {
        profesor.setHorasMaximasSemanales(4); 

        Grupo grupoExistenteSinCruce = new Grupo(asignatura, 97, 10, 40, profesor, salon,
                new ArrayList<>(List.of(new FranjaHoraria(Dia.VIERNES, LocalTime.of(7, 0), LocalTime.of(9, 0)))),
                EstadoGrupo.ACTIVO);
        grupoExistenteSinCruce.setId(4L);
        grupoExistenteSinCruce.setPeriodo("2026-2");

        when(parametrosSistemaService.get()).thenReturn(parametros);
        when(profesorRep.findById(1L)).thenReturn(Optional.of(profesor));
        when(salonRep.findById(1L)).thenReturn(Optional.of(salon));
        when(asignaturaRep.findById(1L)).thenReturn(Optional.of(asignatura));
        when(mapper.map(sesionLunesDTO, FranjaHoraria.class)).thenReturn(sesionLunes);
        when(mapper.map(sesionMiercolesDTO, FranjaHoraria.class)).thenReturn(sesionMiercoles);
        when(grupoRep.findByProfesor(profesor)).thenReturn(List.of(grupoExistenteSinCruce));

        assertThrows(CargaHorariaExcedidaException.class, () -> grupoService.create(grupoDTO));
    }


    @Test
    void getAll() {
        Grupo entidad = new Grupo(asignatura, 1, 10, 40, profesor, salon,
                new ArrayList<>(List.of(sesionLunes, sesionMiercoles)), EstadoGrupo.ACTIVO);

        when(grupoRep.findAll()).thenReturn(List.of(entidad));
        when(mapper.map(entidad, GrupoDTO.class)).thenReturn(grupoDTO);

        List<GrupoDTO> resultado = grupoService.getAll();

        assertEquals(1, resultado.size());
        assertEquals(grupoDTO, resultado.get(0));
    }

    @Test
    void getAllVacio() {
        when(grupoRep.findAll()).thenReturn(new ArrayList<>());

        List<GrupoDTO> resultado = grupoService.getAll();

        assertNotNull(resultado);
        assertTrue(resultado.isEmpty());
    }


    @Test
    void deleteByIdExistente() {
        when(grupoRep.existsById(1L)).thenReturn(true);

        int resultado = grupoService.deleteById(1L);

        assertEquals(0, resultado);
        verify(grupoRep).deleteById(1L);
        verify(auditoriaService).preAccion(TipoAccion.DELETE, Servicio.GRUPO);
    }

    @Test
    void deleteByIdNoExistente() {
        when(grupoRep.existsById(99L)).thenReturn(false);

        int resultado = grupoService.deleteById(99L);

        assertEquals(1, resultado);
        verify(grupoRep, never()).deleteById(anyLong());
    }

    // ---------- updateById ----------

    @Test
    void updateByIdNoExistente() {
        when(grupoRep.findById(99L)).thenReturn(Optional.empty());

        int resultado = grupoService.updateById(99L, grupoDTO);

        assertEquals(1, resultado);
        verify(grupoRep, never()).save(any());
    }

    @Test
    void updateByIdExistenteMismoNumeroGrupo() {
        Grupo existente = new Grupo(asignatura, 1, 10, 40, profesor, salon,
                new ArrayList<>(List.of(sesionLunes, sesionMiercoles)), EstadoGrupo.ACTIVO);
        existente.setId(1L);

        when(grupoRep.findById(1L)).thenReturn(Optional.of(existente));
        when(parametrosSistemaService.get()).thenReturn(parametros);
        when(profesorRep.findById(1L)).thenReturn(Optional.of(profesor));
        when(salonRep.findById(1L)).thenReturn(Optional.of(salon));
        when(asignaturaRep.findById(1L)).thenReturn(Optional.of(asignatura));
        when(mapper.map(sesionLunesDTO, FranjaHoraria.class)).thenReturn(sesionLunes);
        when(mapper.map(sesionMiercolesDTO, FranjaHoraria.class)).thenReturn(sesionMiercoles);
        when(franjaRep.save(any(FranjaHoraria.class))).thenAnswer(inv -> inv.getArgument(0));

        int resultado = grupoService.updateById(1L, grupoDTO);

        assertEquals(0, resultado);
        verify(grupoRep, never()).existsByNumeroGrupo(anyInt());
        verify(grupoRep).save(existente);
        verify(auditoriaService).preAccion(TipoAccion.UPDATE, Servicio.GRUPO);
    }

    @Test
    void updateByIdNumeroGrupoDiferenteYDisponible() {
        Grupo existente = new Grupo(asignatura, 2, 10, 40, profesor, salon,
                new ArrayList<>(List.of(sesionLunes, sesionMiercoles)), EstadoGrupo.ACTIVO);
        existente.setId(1L);

        when(grupoRep.findById(1L)).thenReturn(Optional.of(existente));
        when(grupoRep.existsByNumeroGrupoAndAsignaturaAndPeriodo(1, asignatura, "2026-2")).thenReturn(false);
        when(parametrosSistemaService.get()).thenReturn(parametros);
        when(profesorRep.findById(1L)).thenReturn(Optional.of(profesor));
        when(salonRep.findById(1L)).thenReturn(Optional.of(salon));
        when(asignaturaRep.findById(1L)).thenReturn(Optional.of(asignatura));
        when(mapper.map(sesionLunesDTO, FranjaHoraria.class)).thenReturn(sesionLunes);
        when(mapper.map(sesionMiercolesDTO, FranjaHoraria.class)).thenReturn(sesionMiercoles);
        when(franjaRep.save(any(FranjaHoraria.class))).thenAnswer(inv -> inv.getArgument(0));

        int resultado = grupoService.updateById(1L, grupoDTO);

        assertEquals(0, resultado);
        verify(grupoRep).existsByNumeroGrupoAndAsignaturaAndPeriodo(1, asignatura, "2026-2");
    }

    @Test
    void findByNumeroGrupoExistente() {
        Grupo entidad = new Grupo(asignatura, 1, 10, 40, profesor, salon,
                new ArrayList<>(List.of(sesionLunes, sesionMiercoles)), EstadoGrupo.ACTIVO);

        when(grupoRep.findByNumeroGrupo(1)).thenReturn(List.of(entidad));
        when(mapper.map(entidad, GrupoDTO.class)).thenReturn(grupoDTO);

        List<GrupoDTO> resultado = grupoService.findByNumeroGrupo(1);

        assertEquals(1, resultado.size());
    }

    @Test
    void findByNumeroGrupoNoExistente() {
        when(grupoRep.findByNumeroGrupo(5)).thenReturn(List.of());

        List<GrupoDTO> resultado = grupoService.findByNumeroGrupo(5);

        assertTrue(resultado.isEmpty());
    }

    @Test
    void findByAsignatura() {
        Grupo entidad = new Grupo(asignatura, 1, 10, 40, profesor, salon,
                new ArrayList<>(List.of(sesionLunes, sesionMiercoles)), EstadoGrupo.ACTIVO);

        when(mapper.map(asignaturaDTO, Asignatura.class)).thenReturn(asignatura);
        when(grupoRep.findByAsignatura(asignatura)).thenReturn(List.of(entidad));
        when(mapper.map(entidad, GrupoDTO.class)).thenReturn(grupoDTO);

        List<GrupoDTO> resultado = grupoService.findByAsignatura(asignaturaDTO);

        assertEquals(1, resultado.size());
    }

    @Test
    void findByProfesor() {
        Grupo entidad = new Grupo(asignatura, 1, 10, 40, profesor, salon,
                new ArrayList<>(List.of(sesionLunes, sesionMiercoles)), EstadoGrupo.ACTIVO);

        when(mapper.map(profesorDTO, Profesor.class)).thenReturn(profesor);
        when(grupoRep.findByProfesor(profesor)).thenReturn(List.of(entidad));
        when(mapper.map(entidad, GrupoDTO.class)).thenReturn(grupoDTO);

        List<GrupoDTO> resultado = grupoService.findByProfesor(profesorDTO);

        assertEquals(1, resultado.size());
    }

    @Test
    void findByEstado() {
        Grupo entidad = new Grupo(asignatura, 1, 10, 40, profesor, salon,
                new ArrayList<>(List.of(sesionLunes, sesionMiercoles)), EstadoGrupo.ACTIVO);

        when(grupoRep.findByEstado(EstadoGrupo.ACTIVO)).thenReturn(List.of(entidad));
        when(mapper.map(entidad, GrupoDTO.class)).thenReturn(grupoDTO);

        List<GrupoDTO> resultado = grupoService.findByEstado(EstadoGrupo.ACTIVO);

        assertEquals(1, resultado.size());
    }

    @Test
    void findNumeroGrupoAlreadyTakenExistente() {
        when(grupoRep.existsByNumeroGrupo(1)).thenReturn(true);
        assertTrue(grupoService.findNumeroGrupoAlreadyTaken(1));
    }

    @Test
    void findNumeroGrupoAlreadyTakenNoExistente() {
        when(grupoRep.existsByNumeroGrupo(5)).thenReturn(false);
        assertFalse(grupoService.findNumeroGrupoAlreadyTaken(5));
    }

    @Test
    void deleteByNumeroGrupoExistente() {
        when(grupoRep.existsByNumeroGrupo(1)).thenReturn(true);

        int resultado = grupoService.deleteByNumeroGrupo(1);

        assertEquals(0, resultado);
        verify(grupoRep).deleteByNumeroGrupo(1);
    }

    @Test
    void deleteByNumeroGrupoNoExistente() {
        when(grupoRep.existsByNumeroGrupo(5)).thenReturn(false);

        int resultado = grupoService.deleteByNumeroGrupo(5);

        assertEquals(1, resultado);
        verify(grupoRep, never()).deleteByNumeroGrupo(anyInt());
    }

    @Test
    void setGetGrupoRep() {
        GrupoRepository nuevo = mock(GrupoRepository.class);
        grupoService.setGrupoRep(nuevo);
        assertEquals(nuevo, grupoService.getGrupoRep());
    }

    @Test
    void setGetProfesorRep() {
        ProfesorRepository nuevo = mock(ProfesorRepository.class);
        grupoService.setProfesorRep(nuevo);
        assertEquals(nuevo, grupoService.getProfesorRep());
    }

    @Test
    void setGetAsignaturaRep() {
        AsignaturaRepository nuevo = mock(AsignaturaRepository.class);
        grupoService.setAsignaturaRep(nuevo);
        assertEquals(nuevo, grupoService.getAsignaturaRep());
    }

    @Test
    void setGetSalonRep() {
        SalonRepository nuevo = mock(SalonRepository.class);
        grupoService.setSalonRep(nuevo);
        assertEquals(nuevo, grupoService.getSalonRep());
    }

    @Test
    void setGetFranjaRep() {
        FranjaHorariaRepository nuevo = mock(FranjaHorariaRepository.class);
        grupoService.setFranjaRep(nuevo);
        assertEquals(nuevo, grupoService.getFranjaRep());
    }

    @Test
    void setGetMapper() {
        ModelMapper nuevo = mock(ModelMapper.class);
        grupoService.setMapper(nuevo);
        assertEquals(nuevo, grupoService.getMapper());
    }
    
    @Test
    void createDosSesionesEnDiasConsecutivos_parametroActivado_deberiaBloquear() {
        FranjaHorariaDTO sesionMartesDTO = new FranjaHorariaDTO(Dia.MARTES, LocalTime.of(7, 0), LocalTime.of(9, 0));
        FranjaHoraria sesionMartes = new FranjaHoraria(Dia.MARTES, LocalTime.of(7, 0), LocalTime.of(9, 0));

        grupoDTO.setSesiones(new ArrayList<>(List.of(sesionLunesDTO, sesionMartesDTO)));
        parametros.setExigirDiasNoConsecutivos(true);

        when(parametrosSistemaService.get()).thenReturn(parametros);
        when(profesorRep.findById(1L)).thenReturn(Optional.of(profesor));
        when(salonRep.findById(1L)).thenReturn(Optional.of(salon));
        when(asignaturaRep.findById(1L)).thenReturn(Optional.of(asignatura));
        when(mapper.map(sesionLunesDTO, FranjaHoraria.class)).thenReturn(sesionLunes);
        when(mapper.map(sesionMartesDTO, FranjaHoraria.class)).thenReturn(sesionMartes);

        assertThrows(SesionesInvalidasException.class, () -> grupoService.create(grupoDTO));
    }

    @Test
    void createDosSesionesEnDiasConsecutivos_parametroDesactivado_deberiaPasar() {
        FranjaHorariaDTO sesionMartesDTO = new FranjaHorariaDTO(Dia.MARTES, LocalTime.of(7, 0), LocalTime.of(9, 0));
        FranjaHoraria sesionMartes = new FranjaHoraria(Dia.MARTES, LocalTime.of(7, 0), LocalTime.of(9, 0));

        grupoDTO.setSesiones(new ArrayList<>(List.of(sesionLunesDTO, sesionMartesDTO)));
        parametros.setExigirDiasNoConsecutivos(false); 
        
        profesor.setDisponibilidadHoraria(new ArrayList<>(List.of(
                new FranjaHoraria(Dia.LUNES, LocalTime.of(7, 0), LocalTime.of(22, 0)),
                new FranjaHoraria(Dia.MARTES, LocalTime.of(7, 0), LocalTime.of(22, 0)),
                new FranjaHoraria(Dia.MIERCOLES, LocalTime.of(7, 0), LocalTime.of(22, 0)))));

        when(parametrosSistemaService.get()).thenReturn(parametros);
        when(profesorRep.findById(1L)).thenReturn(Optional.of(profesor));
        when(salonRep.findById(1L)).thenReturn(Optional.of(salon));
        when(asignaturaRep.findById(1L)).thenReturn(Optional.of(asignatura));
        when(mapper.map(sesionLunesDTO, FranjaHoraria.class)).thenReturn(sesionLunes);
        when(mapper.map(sesionMartesDTO, FranjaHoraria.class)).thenReturn(sesionMartes);
        when(franjaRep.save(any(FranjaHoraria.class))).thenAnswer(inv -> inv.getArgument(0));

        assertDoesNotThrow(() -> grupoService.create(grupoDTO));
    }
}