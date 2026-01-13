package com.fran.saludconecta.cita.service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.jooq.DSLContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fran.saludconecta.cita.dto.CitaDTO;
import com.fran.saludconecta.cita.mapper.CitaMapper;
import com.fran.saludconecta.cita.repository.CitaRepository;
import com.fran.saludconecta.jooq.tables.records.CitaRecord;

@Service
public class CitaServiceImpl implements ICitaService {

    @Autowired
    private DSLContext dsl;

    @Autowired
    private CitaRepository repository;

    @Override
    public List<CitaDTO> mostrarTodos() {
        return repository.obtenerTodos().stream().map(CitaMapper::toDTO).toList();
    }

    @Override
    public CitaDTO mostrarPorId(Integer id) {
        return CitaMapper.toDTO(repository.obtenerPorId(id));
    }

    @Override
    public CitaDTO mostrarDetallesPorId(Integer id) {
        return mostrarPorId(id);
    }

    @Override
    public boolean crear(CitaDTO dto) {
        CitaRecord guardarRecord = CitaMapper.fromDTO(dto, dsl);
        CitaRecord comprobarRecord = repository.obtenerPorId(guardarRecord.getId());

        if (comprobarRecord == null) {
            repository.guardar(guardarRecord);
            dto.setId(guardarRecord.getId());
            return true;
        }
        return false;
    }

    @Override
    public boolean comprobarCrear(CitaDTO dto) {
        if (dto == null)
            return false;
        if (dto.getFechaCita() == null || dto.getPacienteId() == null || dto.getUsuarioId() == null)
            return false;

        if (dto.getId() == null)
            return true;

        CitaRecord record = repository.obtenerPorId(dto.getId());
        return record != null;
    }

    @Override
    public boolean borrar(Integer id) {
        return repository.eliminar(id);
    }

    @Override
    public List<CitaDTO> porUsuario(Integer usuarioId) {
        List<CitaDTO> todasLasCitas = mostrarTodos();
        List<CitaDTO> citasUsuario = new ArrayList<>();

        for (CitaDTO c : todasLasCitas) {
            if (c.getUsuarioId() != null && c.getUsuarioId().equals(usuarioId)) {
                citasUsuario.add(c);
            }
        }

        return citasUsuario;
    }

    @Override
    public List<CitaDTO> proximasPorUsuario(Integer usuarioId, int limit) {
        List<CitaDTO> todasLasCitas = mostrarTodos();
        List<CitaDTO> citasProximas = new ArrayList<>();
        LocalDateTime ahora = LocalDateTime.now().minusMinutes(1);

        for (CitaDTO c : todasLasCitas) {
            if (c.getUsuarioId() != null && c.getUsuarioId().equals(usuarioId)) {
                if (c.getFechaCita() != null && c.getFechaCita().isAfter(ahora)) {
                    citasProximas.add(c);
                }
            }
        }

        citasProximas.sort((a, b) -> a.getFechaCita().compareTo(b.getFechaCita()));

        if (citasProximas.size() > limit) {
            return citasProximas.subList(0, limit);
        }

        return citasProximas;
    }

    @Override
    public List<CitaDTO> citasHoyPorUsuario(Integer usuarioId) {
        LocalDate today = LocalDate.now();
        List<CitaDTO> todasLasCitas = mostrarTodos();
        List<CitaDTO> citasHoy = new ArrayList<>();

        for (CitaDTO c : todasLasCitas) {
            if (c.getUsuarioId() != null && c.getUsuarioId().equals(usuarioId)) {
                if (c.getFechaCita() != null && c.getFechaCita().toLocalDate().equals(today)) {
                    citasHoy.add(c);
                }
            }
        }

        citasHoy.sort((a, b) -> a.getFechaCita().compareTo(b.getFechaCita()));
        return citasHoy;
    }
}
