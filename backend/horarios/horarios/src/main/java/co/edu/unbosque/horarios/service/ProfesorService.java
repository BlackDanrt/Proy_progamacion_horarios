package co.edu.unbosque.horarios.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import co.edu.unbosque.horarios.dto.AsignaturaDTO;
import co.edu.unbosque.horarios.dto.FranjaHorariaDTO;
import co.edu.unbosque.horarios.dto.ProfesorDTO;
import co.edu.unbosque.horarios.entity.Asignatura;
import co.edu.unbosque.horarios.entity.FranjaHoraria;
import co.edu.unbosque.horarios.entity.Profesor;
import co.edu.unbosque.horarios.repository.ProfesorRepository;
import co.edu.unbosque.horarios.util.LanzadorDeException;
import co.edu.unbosque.horarios.util.enums.Escalafon;
import co.edu.unbosque.horarios.util.enums.TipoVinculacion;

@Service
public class ProfesorService implements CRUDOperation<ProfesorDTO> {

    @Autowired
    private ProfesorRepository profesorRep;

    @Autowired
    private ModelMapper mapper;

    public ProfesorService() {
        // Constructor vacío
    }

    @Override
    public long count() {
        return profesorRep.count();
    }

    @Override
    public boolean exist(Long id) {
        LanzadorDeException.verificarId(id);
        return profesorRep.existsById(id);
    }

    @Override
    public int create(ProfesorDTO data) {

        LanzadorDeException.verificarProfesor(data);
        LanzadorDeException.verificarNombre(data.getNombre());
        LanzadorDeException.verificarApellido(data.getApellido());
        LanzadorDeException.verificarDocumento(data.getDocumento());
        LanzadorDeException.verificarCorreoElectronico(data.getCorreo());
        LanzadorDeException.verificarContrasena(data.getContrasenia());
        LanzadorDeException.verificarTipoVinculacion(data.getTipoVinculacion());
        LanzadorDeException.verificarEscalafon(data.getEscalafon());
        LanzadorDeException.verificarHorasMaximasSemanales(
                data.getHorasMaximasSemanales());
        LanzadorDeException.verificarEspecialidades(
                data.getEspecialidades());
        LanzadorDeException.verificarDisponibilidadHoraria(
                data.getDisponibilidadHoraria());

        LanzadorDeException.verificarDocumentoDuplicado(
                profesorRep.existsByDocumento(data.getDocumento()));

        LanzadorDeException.verificarCorreoDuplicado(
                profesorRep.existsByCorreo(data.getCorreo()));

        Profesor profesor = mapper.map(data, Profesor.class);

        profesorRep.save(profesor);

        return 0;
    }

    @Override
    public List<ProfesorDTO> getAll() {

        List<ProfesorDTO> lista = new ArrayList<>();

        Iterable<Profesor> profesores = profesorRep.findAll();

        for (Profesor profesor : profesores) {
            lista.add(mapper.map(profesor, ProfesorDTO.class));
        }

        return lista;
    }

    @Override
    public int deleteById(Long id) {

        LanzadorDeException.verificarId(id);

        if (!profesorRep.existsById(id)) {
            return 1;
        }

        profesorRep.deleteById(id);

        return 0;
    }

    @Override
    public int updateById(Long id, ProfesorDTO data) {

        LanzadorDeException.verificarId(id);
        LanzadorDeException.verificarProfesor(data);

        Optional<Profesor> encontrado = profesorRep.findById(id);

        if (encontrado.isEmpty()) {
            return 1;
        }

        LanzadorDeException.verificarNombre(data.getNombre());
        LanzadorDeException.verificarApellido(data.getApellido());
        LanzadorDeException.verificarDocumento(data.getDocumento());
        LanzadorDeException.verificarCorreoElectronico(data.getCorreo());
        LanzadorDeException.verificarContrasena(data.getContrasenia());
        LanzadorDeException.verificarTipoVinculacion(
                data.getTipoVinculacion());
        LanzadorDeException.verificarEscalafon(data.getEscalafon());
        LanzadorDeException.verificarHorasMaximasSemanales(
                data.getHorasMaximasSemanales());
        LanzadorDeException.verificarEspecialidades(
                data.getEspecialidades());
        LanzadorDeException.verificarDisponibilidadHoraria(
                data.getDisponibilidadHoraria());

        Profesor profesorActual = encontrado.get();

        if (profesorActual.getDocumento() != data.getDocumento()) {
            LanzadorDeException.verificarDocumentoDuplicado(
                    profesorRep.existsByDocumento(data.getDocumento()));
        }

        if (!profesorActual.getCorreo().equals(data.getCorreo())) {
            LanzadorDeException.verificarCorreoDuplicado(
                    profesorRep.existsByCorreo(data.getCorreo()));
        }

        Profesor profesor = mapper.map(data, Profesor.class);

        profesor.setId(id);

        profesorRep.save(profesor);

        return 0;
    }

    public ProfesorDTO findByDocumento(long documento) {

        LanzadorDeException.verificarDocumento(documento);

        Optional<Profesor> encontrado =
                profesorRep.findByDocumento(documento);

        if (encontrado.isEmpty()) {
            return null;
        }

        return mapper.map(encontrado.get(), ProfesorDTO.class);
    }

    public ProfesorDTO findByCorreo(String correo) {

        LanzadorDeException.verificarCorreoElectronico(correo);

        Optional<Profesor> encontrado =
                profesorRep.findByCorreo(correo);

        if (encontrado.isEmpty()) {
            return null;
        }

        return mapper.map(encontrado.get(), ProfesorDTO.class);
    }

    public List<ProfesorDTO> findByTipoVinculacion(
            TipoVinculacion tipoVinculacion) {

        LanzadorDeException.verificarTipoVinculacion(tipoVinculacion);

        List<ProfesorDTO> lista = new ArrayList<>();

        List<Profesor> profesores =
                profesorRep.findByTipoVinculacion(tipoVinculacion);

        for (Profesor profesor : profesores) {
            lista.add(mapper.map(profesor, ProfesorDTO.class));
        }

        return lista;
    }

    public List<ProfesorDTO> findByEscalafon(Escalafon escalafon) {

        LanzadorDeException.verificarEscalafon(escalafon);

        List<ProfesorDTO> lista = new ArrayList<>();

        List<Profesor> profesores =
                profesorRep.findByEscalafon(escalafon);

        for (Profesor profesor : profesores) {
            lista.add(mapper.map(profesor, ProfesorDTO.class));
        }

        return lista;
    }

    public List<ProfesorDTO> findByEspecialidad(Asignatura asignatura) {

        LanzadorDeException.verificarAsignatura(
                mapper.map(asignatura, AsignaturaDTO.class));

        List<ProfesorDTO> lista = new ArrayList<>();

        List<Profesor> profesores =
                profesorRep.findByEspecialidadesContaining(asignatura);

        for (Profesor profesor : profesores) {
            lista.add(mapper.map(profesor, ProfesorDTO.class));
        }

        return lista;
    }

    public List<ProfesorDTO> findByDisponibilidad(
            FranjaHoraria franjaHoraria) {

        LanzadorDeException.verificarFranjaHoraria(
                mapper.map(franjaHoraria, FranjaHorariaDTO.class));

        List<ProfesorDTO> lista = new ArrayList<>();

        List<Profesor> profesores =
                profesorRep.findByDisponibilidadHorariaContaining(
                        franjaHoraria);

        for (Profesor profesor : profesores) {
            lista.add(mapper.map(profesor, ProfesorDTO.class));
        }

        return lista;
    }

    public boolean findDocumentoAlreadyTaken(long documento) {
        return profesorRep.existsByDocumento(documento);
    }

    public boolean findCorreoAlreadyTaken(String correo) {
        return profesorRep.existsByCorreo(correo);
    }

    public void deleteByDocumento(long documento) {

        LanzadorDeException.verificarDocumento(documento);

        if (!profesorRep.existsByDocumento(documento)) {
            return;
        }

        profesorRep.deleteByDocumento(documento);
    }

    public ProfesorRepository getProfesorRep() {
        return profesorRep;
    }

    public void setProfesorRep(ProfesorRepository profesorRep) {
        this.profesorRep = profesorRep;
    }

    public ModelMapper getMapper() {
        return mapper;
    }

    public void setMapper(ModelMapper mapper) {
        this.mapper = mapper;
    }
}