package com.fran.saludconecta.informe.service;

import java.time.LocalDateTime;
import java.util.List;

import org.jooq.DSLContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fran.saludconecta.informe.dto.InformeDTO;
import com.fran.saludconecta.informe.mapper.InformeMapper;
import com.fran.saludconecta.informe.repository.InformeRepository;
import com.fran.saludconecta.jooq.tables.records.InformeRecord;

@Service
public class InformeServiceImpl implements IInformeService {

    @Autowired
    private DSLContext dsl;

    @Autowired
    private InformeRepository repository;

    @Override
    public List<InformeDTO> mostrarTodos() {
        return repository.obtenerTodos().stream().map(InformeMapper::toDTO).toList();
    }

    @Override
    public InformeDTO mostrarPorId(Integer id) {
        return InformeMapper.toDTO(repository.obtenerPorId(id));
    }

    @Override
    public boolean crear(InformeDTO dto) {
        InformeRecord guardarRecord = InformeMapper.fromDTO(dto, dsl);
        InformeRecord comprobarRecord = repository.obtenerPorId(guardarRecord.getId());

        if (comprobarRecord == null) {
            repository.guardar(guardarRecord);
            dto.setId(guardarRecord.getId());
            return true;
        }

        return false;
    }

    @Override
    public InformeDTO modificar(Integer id, InformeDTO dto) {
        dto.setNombreUsuario(dto.getNombreUsuario().trim());
        dto.setNombrePaciente(dto.getNombrePaciente().trim());
        dto.setContenido(dto.getContenido().trim());
        dto.setFechaCreacion(dto.getFechaCreacion());
        dto.setFechaModificacion(LocalDateTime.now());

        InformeRecord record = repository.actualizar(id, dto);
        return InformeMapper.toDTO(record);
    }

    @Override
    public boolean borrar(Integer id) {
        return repository.eliminar(id);
    }
}
