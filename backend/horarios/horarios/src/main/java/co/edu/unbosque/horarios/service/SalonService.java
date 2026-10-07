package co.edu.unbosque.horarios.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import co.edu.unbosque.horarios.dto.SalonDTO;
import co.edu.unbosque.horarios.entity.Salon;
import co.edu.unbosque.horarios.repository.SalonRepository;
import co.edu.unbosque.horarios.util.LanzadorDeException;
import co.edu.unbosque.horarios.util.enums.Bloque;

@Service
public class SalonService implements CRUDOperation<SalonDTO> {

    @Autowired
    private SalonRepository salonRep;

    @Autowired
    private ModelMapper mapper;

    public SalonService() {
        // Constructor vacío
    }

    @Override
    public long count() {
        return salonRep.count();
    }

    @Override
    public boolean exist(Long id) {
        LanzadorDeException.verificarId(id);
        return salonRep.existsById(id);
    }

    @Override
    public int create(SalonDTO data) {

        LanzadorDeException.verificarSalon(data);
        LanzadorDeException.verificarBloque(data.getBloque());
        LanzadorDeException.verificarNumeroSalon(data.getNumeroSalon());
        LanzadorDeException.verificarCapacidad(data.getCapacidad());

        LanzadorDeException.verificarSalonDuplicado(
                salonRep.existsByBloqueAndNumeroSalon(
                        data.getBloque(),
                        data.getNumeroSalon()));

        Salon salon = mapper.map(data, Salon.class);

        salonRep.save(salon);

        return 0;
    }

    @Override
    public List<SalonDTO> getAll() {

        List<SalonDTO> lista = new ArrayList<>();

        Iterable<Salon> salones = salonRep.findAll();

        for (Salon salon : salones) {
            lista.add(mapper.map(salon, SalonDTO.class));
        }

        return lista;
    }

    @Override
    public int deleteById(Long id) {

        LanzadorDeException.verificarId(id);

        if (!salonRep.existsById(id)) {
            return 1;
        }

        salonRep.deleteById(id);

        return 0;
    }

    @Override
    public int updateById(Long id, SalonDTO data) {

        LanzadorDeException.verificarId(id);
        LanzadorDeException.verificarSalon(data);
        LanzadorDeException.verificarBloque(data.getBloque());
        LanzadorDeException.verificarNumeroSalon(data.getNumeroSalon());
        LanzadorDeException.verificarCapacidad(data.getCapacidad());

        Optional<Salon> encontrado = salonRep.findById(id);

        if (encontrado.isEmpty()) {
            return 1;
        }

        Salon salonActual = encontrado.get();

        if (salonActual.getBloque() != data.getBloque()
                || salonActual.getNumeroSalon() != data.getNumeroSalon()) {

            LanzadorDeException.verificarSalonDuplicado(
                    salonRep.existsByBloqueAndNumeroSalon(
                            data.getBloque(),
                            data.getNumeroSalon()));
        }

        Salon salon = mapper.map(data, Salon.class);

        salon.setId(id);

        salonRep.save(salon);

        return 0;
    }

    public SalonDTO findByBloqueAndNumeroSalon(
            Bloque bloque, int numeroSalon) {

        LanzadorDeException.verificarBloque(bloque);
        LanzadorDeException.verificarNumeroSalon(numeroSalon);

        Optional<Salon> encontrado =
                salonRep.findByBloqueAndNumeroSalon(bloque, numeroSalon);

        if (encontrado.isEmpty()) {
            return null;
        }

        return mapper.map(encontrado.get(), SalonDTO.class);
    }

    public List<SalonDTO> findByBloque(Bloque bloque) {

        LanzadorDeException.verificarBloque(bloque);

        List<SalonDTO> lista = new ArrayList<>();

        List<Salon> salones = salonRep.findByBloque(bloque);

        for (Salon salon : salones) {
            lista.add(mapper.map(salon, SalonDTO.class));
        }

        return lista;
    }

    public List<SalonDTO> findByCapacidad(int capacidad) {

        LanzadorDeException.verificarCapacidad(capacidad);

        List<SalonDTO> lista = new ArrayList<>();

        List<Salon> salones = salonRep.findByCapacidad(capacidad);

        for (Salon salon : salones) {
            lista.add(mapper.map(salon, SalonDTO.class));
        }

        return lista;
    }

    public List<SalonDTO> findByTieneComputadores(
            boolean tieneComputadores) {

        List<SalonDTO> lista = new ArrayList<>();

        List<Salon> salones =
                salonRep.findByTieneComputadores(tieneComputadores);

        for (Salon salon : salones) {
            lista.add(mapper.map(salon, SalonDTO.class));
        }

        return lista;
    }

    public List<SalonDTO> findByTieneSillasMoviles(
            boolean tieneSillasMoviles) {

        List<SalonDTO> lista = new ArrayList<>();

        List<Salon> salones =
                salonRep.findByTieneSillasMoviles(tieneSillasMoviles);

        for (Salon salon : salones) {
            lista.add(mapper.map(salon, SalonDTO.class));
        }

        return lista;
    }

    public boolean findSalonAlreadyTaken(
            Bloque bloque, int numeroSalon) {

        return salonRep.existsByBloqueAndNumeroSalon(
                bloque, numeroSalon);
    }

    public void deleteByBloqueAndNumeroSalon(
            Bloque bloque, int numeroSalon) {

        LanzadorDeException.verificarBloque(bloque);
        LanzadorDeException.verificarNumeroSalon(numeroSalon);

        if (!salonRep.existsByBloqueAndNumeroSalon(
                bloque, numeroSalon)) {
            return;
        }

        salonRep.deleteByBloqueAndNumeroSalon(
                bloque, numeroSalon);
    }

    public SalonRepository getSalonRep() {
        return salonRep;
    }

    public void setSalonRep(SalonRepository salonRep) {
        this.salonRep = salonRep;
    }

    public ModelMapper getMapper() {
        return mapper;
    }

    public void setMapper(ModelMapper mapper) {
        this.mapper = mapper;
    }
}