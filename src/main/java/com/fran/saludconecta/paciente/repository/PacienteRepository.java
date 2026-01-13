package com.fran.saludconecta.paciente.repository;

import java.util.List;

import org.jooq.DSLContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.fran.saludconecta.jooq.tables.Paciente;
import com.fran.saludconecta.jooq.tables.records.PacienteRecord;
import com.fran.saludconecta.paciente.dto.PacienteDTO;

@Repository
public class PacienteRepository {

    @Autowired
    private DSLContext dsl;

    public List<PacienteRecord> obtenerTodos() {
        return dsl.selectFrom(Paciente.PACIENTE).orderBy(Paciente.PACIENTE.NOMBRE).fetch();
    }

    public PacienteRecord obtenerPorId(Integer id) {
        return dsl.selectFrom(Paciente.PACIENTE)
                .where(Paciente.PACIENTE.ID.eq(id))
                .fetchOne();
    }

    public PacienteRecord obtenerPorDni(String dni) {
        return dsl.selectFrom(Paciente.PACIENTE)
                .where(Paciente.PACIENTE.DNI.eq(dni))
                .fetchOne();
    }

    public PacienteRecord guardar(PacienteRecord guardarRecord) {
        PacienteRecord record = dsl.newRecord(Paciente.PACIENTE);
        record = guardarRecord;
        record.store();
        return record;
    }

    public boolean eliminar(Integer id) {
        PacienteRecord record = obtenerPorId(id);

        if (record != null) {
            record.delete();
            return true;
        }
        return false;
    }

    public PacienteRecord actualizar(Integer id, PacienteDTO dto) {
        PacienteRecord record = obtenerPorId(id);

        if (record != null) {
            record.setNombre(dto.getNombre());
            record.setDni(dto.getDni());
            record.setFechaNacimiento(dto.getFechaNacimiento());
            record.update();
        }
        return record;
    }
}
