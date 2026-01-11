package com.fran.saludconecta.negocio.service;

import java.util.List;

import org.jooq.DSLContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fran.saludconecta.jooq.tables.records.NegocioRecord;
import com.fran.saludconecta.negocio.dto.NegocioDTO;
import com.fran.saludconecta.negocio.mapper.NegocioMapper;
import com.fran.saludconecta.negocio.repository.NegocioRepository;
import com.fran.saludconecta.usuario.mapper.UsuarioMapper;

@Service
public class NegocioServiceImpl implements INegocioService {
    @Autowired
    private DSLContext dsl;

    @Autowired
    private NegocioRepository repository;

    @Override
    public List<NegocioDTO> mostrarTodos() {
        return repository.obtenerTodos().stream().map(NegocioMapper::toDTO).toList();
    }

    @Override
    public NegocioDTO mostrarPorId(Integer id) {
        NegocioDTO dto = NegocioMapper.toDTO(repository.obtenerPorId(id));
        if (dto != null) {
            dto.setUsuarios(repository.obtenerUsuariosPorNegocio(id)
                    .stream()
                    .map(UsuarioMapper::toDTO)
                    .toList());
        }
        return dto;
    }

    @Override
    public boolean crear(NegocioDTO dto) {
        NegocioRecord guardarRecord = NegocioMapper.fromDTO(dto, dsl);
        NegocioRecord comprobarRecord = repository.obtenerPorId(guardarRecord.getId());
        NegocioRecord comprobarRecordPorNombre = repository.obtenerPorNombre(guardarRecord.getNombre());

        if (comprobarRecord == null && comprobarRecordPorNombre == null) {
            repository.guardar(guardarRecord);
            dto.setId(guardarRecord.getId());
            return true;
        }

        return false;
    }

    @Override
    public boolean comprobarCrear(NegocioDTO dto) {
        NegocioRecord guardarRecord = NegocioMapper.fromDTO(dto, dsl);
        NegocioRecord comprobarRecord = repository.obtenerPorId(guardarRecord.getId());
        NegocioRecord comprobarRecordPorNombre = repository.obtenerPorNombre(guardarRecord.getNombre());

        return comprobarRecord == null && comprobarRecordPorNombre == null;
    }

    @Override
    public NegocioDTO modificar(Integer id, NegocioDTO dto) {
        dto.setNombre(dto.getNombre().trim());
        dto.setDireccion(dto.getDireccion().trim());
        dto.setTelefono(dto.getTelefono().trim());
        NegocioRecord record = repository.actualizar(id, dto);
        return NegocioMapper.toDTO(record);
    }

    @Override
    public boolean borrar(Integer id) {
        return repository.eliminar(id);
    }
}
