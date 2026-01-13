package com.fran.saludconecta.informe.repository;

import java.time.LocalDateTime;
import java.util.List;

import org.jooq.DSLContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.fran.saludconecta.informe.dto.InformeDTO;
import com.fran.saludconecta.jooq.tables.Informe;
import com.fran.saludconecta.jooq.tables.PacienteUsuario;
import com.fran.saludconecta.jooq.tables.records.InformeRecord;

@Repository
public class InformeRepository {

    @Autowired
    private DSLContext dsl;

    public List<InformeRecord> obtenerTodos() {
        return dsl.selectFrom(Informe.INFORME).orderBy(Informe.INFORME.NOMBRE_PACIENTE).fetch();
    }

    public InformeRecord obtenerPorId(Integer id) {
        return dsl.selectFrom(Informe.INFORME)
                .where(Informe.INFORME.ID.eq(id))
                .fetchOne();
    }

    public InformeRecord guardar(InformeRecord guardarRecord) {
        InformeRecord record = dsl.newRecord(Informe.INFORME);
        record = guardarRecord;
        record.store();

        // Crear asociación paciente-usuario si no existe
        crearAsociacionPacienteUsuario(record.getUsuarioId(), record.getPacienteId());

        return record;
    }

    private void crearAsociacionPacienteUsuario(Integer usuarioId, Integer pacienteId) {
        if (usuarioId != null && pacienteId != null) {
            // Verificar si la asociación ya existe
            boolean existe = dsl.fetchExists(
                    dsl.selectFrom(PacienteUsuario.PACIENTE_USUARIO)
                            .where(PacienteUsuario.PACIENTE_USUARIO.USUARIO_ID.eq(usuarioId))
                            .and(PacienteUsuario.PACIENTE_USUARIO.PACIENTE_ID.eq(pacienteId)));

            // Si no existe, crearla
            if (!existe) {
                dsl.insertInto(PacienteUsuario.PACIENTE_USUARIO)
                        .set(PacienteUsuario.PACIENTE_USUARIO.USUARIO_ID, usuarioId)
                        .set(PacienteUsuario.PACIENTE_USUARIO.PACIENTE_ID, pacienteId)
                        .execute();
            }
        }
    }

    public boolean eliminar(Integer id) {
        InformeRecord record = obtenerPorId(id);

        if (record != null) {
            record.delete();
            return true;
        }
        return false;
    }

    public InformeRecord actualizar(Integer id, InformeDTO dto) {
        InformeRecord record = obtenerPorId(id);

        if (record != null) {
            record.setUsuarioId(dto.getUsuarioId());
            record.setNombreUsuario(dto.getNombreUsuario());
            record.setPacienteId(dto.getPacienteId());
            record.setNombrePaciente(dto.getNombrePaciente());
            record.setContenido(dto.getContenido());
            // record.setFechaCreacion(dto.getFechaCreacion());
            record.setFechaModificacion(LocalDateTime.now());
            record.update();
        }
        return record;
    }
}
