package com.fran.saludconecta.negocio.mapper;

import java.time.LocalDateTime;

import org.jooq.DSLContext;

import com.fran.saludconecta.jooq.tables.Negocio;
import com.fran.saludconecta.jooq.tables.records.NegocioRecord;
import com.fran.saludconecta.negocio.dto.NegocioDTO;

public class NegocioMapper {

    public static NegocioDTO toDTO(NegocioRecord record) {

        if (record == null)
            return null;

        return NegocioDTO.builder()
                .id(record.get(Negocio.NEGOCIO.ID))
                .nombre(record.get(Negocio.NEGOCIO.NOMBRE))
                .direccion(record.get(Negocio.NEGOCIO.DIRECCION))
                .telefono(record.get(Negocio.NEGOCIO.TELEFONO))
                .fechaCreacion(record.get(Negocio.NEGOCIO.FECHA_CREACION))
                .fechaModificacion(record.get(Negocio.NEGOCIO.FECHA_MODIFICACION))
                .build();
    }

    public static NegocioRecord fromDTO(NegocioDTO dto, DSLContext dsl) {

        if (dto == null)
            return null;

        NegocioRecord record = dsl.newRecord(Negocio.NEGOCIO);
        // record.setId(dto.getId());
        record.setNombre(dto.getNombre().trim());
        record.setDireccion(dto.getDireccion().trim());
        record.setTelefono(dto.getTelefono().trim());
        record.setFechaCreacion(LocalDateTime.now());
        record.setFechaModificacion(LocalDateTime.now());
        return record;
    }
}
