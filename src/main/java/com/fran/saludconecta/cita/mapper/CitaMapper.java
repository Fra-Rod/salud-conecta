package com.fran.saludconecta.cita.mapper;

import java.time.LocalDateTime;

import org.jooq.DSLContext;

import com.fran.saludconecta.cita.dto.CitaDTO;
import com.fran.saludconecta.jooq.tables.Cita;
import com.fran.saludconecta.jooq.tables.records.CitaRecord;

public class CitaMapper {
    public static CitaDTO toDTO(CitaRecord record) {
        if (record == null)
            return null;

        return CitaDTO.builder()
                .id(record.get(Cita.CITA.ID))
                .pacienteId(record.get(Cita.CITA.PACIENTE_ID))
                .usuarioId(record.get(Cita.CITA.USUARIO_ID))
                .fechaCita(record.get(Cita.CITA.FECHA_CITA))
                .motivo(record.get(Cita.CITA.MOTIVO))
                .estado(record.get(Cita.CITA.ESTADO))
                .fechaCreacion(record.get(Cita.CITA.FECHA_CREACION))
                .fechaModificacion(record.get(Cita.CITA.FECHA_MODIFICACION))
                .build();
    }

    public static CitaRecord fromDTO(CitaDTO dto, DSLContext dsl) {
        if (dto == null)
            return null;

        CitaRecord record = dsl.newRecord(Cita.CITA);
        // record.setId(dto.getId()); // id is auto-generated

        record.setPacienteId(dto.getPacienteId());
        record.setUsuarioId(dto.getUsuarioId());
        record.setFechaCita(dto.getFechaCita());
        record.setMotivo(dto.getMotivo() == null ? null : dto.getMotivo().trim());
        record.setEstado(dto.getEstado());
        record.setFechaCreacion(LocalDateTime.now());
        record.setFechaModificacion(LocalDateTime.now());

        return record;
    }
}
