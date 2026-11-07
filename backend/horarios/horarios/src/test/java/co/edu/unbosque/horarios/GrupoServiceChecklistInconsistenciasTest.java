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
import co.edu.unbosque.horarios.entity.Profesor;
import co.edu.unbosque.horarios.entity.Salon;
import co.edu.unbosque.horarios.exception.CapacidadMaximaExcedidaException;
import co.edu.unbosque.horarios.exception.CapacidadMaximaInvalidaException;
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
import co.edu.unbosque.horarios.util.enums.EstadoGrupo;

/**
 * NO es el test "normal" de GrupoService. Esta clase existe únicamente para
 * verificar, caso por caso, las 4 inconsistencias detectadas entre el
 * checklist "Validación de Grupos" y el comportamiento real de
 * {@link co.edu.unbosque.horarios.util.LanzadorDeException} usado por
 * {@link GrupoService#create}.
 * <p>
 * Con la implementación actual, se espera que estos 4 tests FALLEN — ese es
 * justamente el punto: demostrar con evidencia el desacuerdo entre lo que el
 * checklist documenta y lo que el código hace. Una vez se decida qué lado es
 * el correcto (¿se actualiza el checklist? ¿se corrige el validador?), estos
 * tests se ajustan o se eliminan.
 */
@ExtendWith(MockitoExtension.class)
class GrupoServiceChecklistInconsistenciasTest {

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
                new FranjaHoraria(Dia.MARTES, LocalTime.of(7, 0), LocalTime.of(22, 0)),
                new FranjaHoraria(Dia.MIERCOLES, LocalTime.of(7, 0), LocalTime.of(22, 0)))));

        salonDTO = new SalonDTO();
        salonDTO.setId(1L);

        salon = new Salon();
        salon.setId(1L);
        salon.setBloque(Bloque.A);
        salon.setNumeroSalon(101);
        salon.setCapacidad(44); // holgado para no interferir con las pruebas de capacidad
        salon.setTieneComputadores(false);
        salon.setTieneSillasMoviles(false);

        grupoDTO = new GrupoDTO();
        grupoDTO.setNumeroGrupo(1);
        grupoDTO.setCapacidadMinima(10);
        grupoDTO.setCapacidadMaxima(40);
        grupoDTO.setInscritos(0);
        grupoDTO.setEstado(EstadoGrupo.ACTIVO);
        grupoDTO.setPeriodo("2026-2");
        grupoDTO.setAsignatura(asignaturaDTO);
        grupoDTO.setProfesor(profesorDTO);
        grupoDTO.setSalon(salonDTO);
    }

    private void stubCargaBasica() {
        when(grupoRep.existsByNumeroGrupo(1)).thenReturn(false);
        when(parametrosSistemaService.get()).thenReturn(parametros);
        when(profesorRep.findById(1L)).thenReturn(Optional.of(profesor));
        when(salonRep.findById(1L)).thenReturn(Optional.of(salon));
        when(asignaturaRep.findById(1L)).thenReturn(Optional.of(asignatura));
    }

    /**
     * Checklist: "capacidadMaxima > 44 (40 + 10% institucional)" → se espera
     * CapacidadMaximaExcedidaException.
     * Realidad: verificarCapacidadMaxima ya lanza CapacidadMaximaInvalidaException
     * para cualquier valor > 40, así que 42 nunca llega a la segunda validación.
     */
    @Test
    void capacidadMaximaSuperaElLimiteInstitucional_42_deberiaSerCapacidadMaximaExcedida() {
        grupoDTO.setCapacidadMaxima(45); // > 40 pero <= 44
        grupoDTO.setSesiones(new ArrayList<>(List.of(
                new FranjaHorariaDTO(Dia.LUNES, LocalTime.of(7, 0), LocalTime.of(9, 0)),
                new FranjaHorariaDTO(Dia.MIERCOLES, LocalTime.of(7, 0), LocalTime.of(9, 0)))));

        stubCargaBasica();
        when(mapper.map(any(FranjaHorariaDTO.class), eq(FranjaHoraria.class)))
                .thenAnswer(inv -> {
                    FranjaHorariaDTO dto = inv.getArgument(0);
                    return new FranjaHoraria(dto.getDia(), dto.getHoraInicio(), dto.getHoraFin());
                });

        assertThrows(CapacidadMaximaExcedidaException.class, () -> grupoService.create(grupoDTO));
    }

    /**
     * Checklist: "2 o 3 sesiones en días consecutivos — criterio deseable, no
     * bloqueante" → debe pasar (sin excepción).
     * Realidad: EXIGIR_DIAS_NO_CONSECUTIVOS = true en LanzadorDeException,
     * así que 2 sesiones en LUNES+MARTES sí lanza SesionesInvalidasException.
     */
    @Test
    void dosSesionesEnDiasConsecutivos_conParametroActivado_deberiaBloquear() {
        parametros.setExigirDiasNoConsecutivos(true); // comportamiento por defecto

        grupoDTO.setSesiones(new ArrayList<>(List.of(
                new FranjaHorariaDTO(Dia.LUNES, LocalTime.of(7, 0), LocalTime.of(9, 0)),
                new FranjaHorariaDTO(Dia.MARTES, LocalTime.of(7, 0), LocalTime.of(9, 0)))));

        stubCargaBasica();
        when(mapper.map(any(FranjaHorariaDTO.class), eq(FranjaHoraria.class)))
                .thenAnswer(inv -> {
                    FranjaHorariaDTO dto = inv.getArgument(0);
                    return new FranjaHoraria(dto.getDia(), dto.getHoraInicio(), dto.getHoraFin());
                });

        assertThrows(SesionesInvalidasException.class, () -> grupoService.create(grupoDTO));
    }

    @Test
    void dosSesionesEnDiasConsecutivos_conParametroDesactivado_deberiaPasar() {
        parametros.setExigirDiasNoConsecutivos(false); // universidad decide permitirlo

        grupoDTO.setSesiones(new ArrayList<>(List.of(
                new FranjaHorariaDTO(Dia.LUNES, LocalTime.of(7, 0), LocalTime.of(9, 0)),
                new FranjaHorariaDTO(Dia.MARTES, LocalTime.of(7, 0), LocalTime.of(9, 0)))));

        stubCargaBasica();
        when(mapper.map(any(FranjaHorariaDTO.class), eq(FranjaHoraria.class)))
                .thenAnswer(inv -> {
                    FranjaHorariaDTO dto = inv.getArgument(0);
                    return new FranjaHoraria(dto.getDia(), dto.getHoraInicio(), dto.getHoraFin());
                });
        when(franjaRep.save(any(FranjaHoraria.class))).thenAnswer(inv -> inv.getArgument(0));

        assertDoesNotThrow(() -> grupoService.create(grupoDTO));
    }

    /**
     * Checklist: "sesiones = [] (lista vacía)" → se espera SesionesInvalidasException.
     * Realidad: verificarSesiones permite 0 sesiones; lo que realmente truena
     * primero es verificarSesionesFrecuencia (0 != frecuenciaSemanal de la
     * asignatura = 2), con una excepción distinta.
     */
    @Test
    void sesionesVacias_deberiaSerSesionesInvalidas() {
        grupoDTO.setSesiones(new ArrayList<>());

        stubCargaBasica();

        assertThrows(SesionesInvalidasException.class, () -> grupoService.create(grupoDTO));
    }

    /**
     * Checklist: "capacidadMaxima ≤ 0" → se espera CapacidadMaximaInvalidaException.
     * Realidad: verificarCapacidadMaxima solo valida "> maximo"; un valor de 0
     * no la dispara. Lo que realmente puede saltar antes es
     * CapacidadesInvalidasException (si capacidadMinima > 0), no la excepción
     * que el checklist documenta.
     */
    @Test
    void capacidadMaximaCero_deberiaSerCapacidadMaximaInvalida() {
        grupoDTO.setCapacidadMaxima(0);

        stubCargaBasica();

        assertThrows(CapacidadMaximaInvalidaException.class, () -> grupoService.create(grupoDTO));
    }
}