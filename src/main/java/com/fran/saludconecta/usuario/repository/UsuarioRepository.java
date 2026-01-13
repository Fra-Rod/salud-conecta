package com.fran.saludconecta.usuario.repository;

import java.util.ArrayList;
import java.util.List;

import org.jooq.DSLContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.fran.saludconecta.jooq.tables.Paciente;
import com.fran.saludconecta.jooq.tables.PacienteUsuario;
import com.fran.saludconecta.jooq.tables.Usuario;
import com.fran.saludconecta.jooq.tables.records.PacienteUsuarioRecord;
import com.fran.saludconecta.jooq.tables.records.UsuarioRecord;
import com.fran.saludconecta.paciente.dto.PacienteDTO;
import com.fran.saludconecta.usuario.dto.UsuarioDTO;

@Repository
public class UsuarioRepository {

    @Autowired
    private DSLContext dsl;

    public List<UsuarioRecord> obtenerTodos() {
        return dsl.selectFrom(Usuario.USUARIO).orderBy(Usuario.USUARIO.NOMBRE).fetch();
    }

    public UsuarioRecord obtenerPorId(Integer id) {
        return dsl.selectFrom(Usuario.USUARIO)
                .where(Usuario.USUARIO.ID.eq(id))
                .fetchOne();
    }

    public UsuarioRecord obtenerPorEmail(String email) {
        return dsl.selectFrom(Usuario.USUARIO)
                .where(Usuario.USUARIO.EMAIL.eq(email))
                .fetchOne();
    }

    public List<PacienteDTO> obtenerTodosPacientesUsuarios(Integer usuarioId) {

        List<PacienteUsuarioRecord> records = dsl.selectFrom(PacienteUsuario.PACIENTE_USUARIO)
                .where(PacienteUsuario.PACIENTE_USUARIO.USUARIO_ID.isNotNull())
                .and(PacienteUsuario.PACIENTE_USUARIO.USUARIO_ID.eq(usuarioId))
                .fetch();

        List<PacienteDTO> pacientes = new ArrayList<>();

        for (PacienteUsuarioRecord r : records) {
            PacienteDTO pacienteDto = dsl.selectFrom(Paciente.PACIENTE)
                    .where(Paciente.PACIENTE.ID.eq(r.getPacienteId()))
                    .fetchOne()
                    .into(PacienteDTO.class);
            pacientes.add(pacienteDto);
        }

        return pacientes;
    }

    public UsuarioRecord guardar(UsuarioRecord guardarRecord) {
        UsuarioRecord record = dsl.newRecord(Usuario.USUARIO);
        record = guardarRecord;
        record.store();
        return record;
    }

    public boolean eliminar(Integer id) {
        UsuarioRecord record = obtenerPorId(id);

        if (record != null) {
            record.delete();
            return true;
        }
        return false;
    }

    public UsuarioRecord actualizar(Integer id, UsuarioDTO dto) {
        UsuarioRecord record = obtenerPorId(id);

        if (record != null) {
            record.setNombre(dto.getNombre());
            record.setEmail(dto.getEmail());
            record.setPassword(dto.getPassword());
            record.setRol(dto.getRolUsuario());
            record.setNegocioId(dto.getNegocioId());
            record.update();
        }
        return record;
    }
}
