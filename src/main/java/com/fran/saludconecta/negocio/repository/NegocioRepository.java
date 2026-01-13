package com.fran.saludconecta.negocio.repository;

import java.util.List;

import org.jooq.DSLContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.fran.saludconecta.jooq.tables.Negocio;
import com.fran.saludconecta.jooq.tables.Usuario;
import com.fran.saludconecta.jooq.tables.records.NegocioRecord;
import com.fran.saludconecta.jooq.tables.records.UsuarioRecord;
import com.fran.saludconecta.negocio.dto.NegocioDTO;

@Repository
public class NegocioRepository {

    @Autowired
    private DSLContext dsl;

    public List<NegocioRecord> obtenerTodos() {
        return dsl.selectFrom(Negocio.NEGOCIO).orderBy(Negocio.NEGOCIO.NOMBRE).fetch();
    }

    public NegocioRecord obtenerPorId(Integer id) {
        return dsl.selectFrom(Negocio.NEGOCIO)
                .where(Negocio.NEGOCIO.ID.eq(id))
                .fetchOne();
    }

    public NegocioRecord obtenerPorNombre(String nombre) {
        return dsl.selectFrom(Negocio.NEGOCIO)
                .where(Negocio.NEGOCIO.NOMBRE.eq(nombre))
                .fetchOne();
    }

    public List<UsuarioRecord> obtenerUsuariosPorNegocio(Integer negocioId) {
        return dsl.selectFrom(Usuario.USUARIO)
                .where(Usuario.USUARIO.NEGOCIO_ID.eq(negocioId))
                .orderBy(Usuario.USUARIO.NOMBRE)
                .fetch();
    }

    public NegocioRecord guardar(NegocioRecord guardarRecord) {
        NegocioRecord record = dsl.newRecord(Negocio.NEGOCIO);
        record = guardarRecord;
        record.store();
        return record;
    }

    public boolean eliminar(Integer id) {
        NegocioRecord record = obtenerPorId(id);

        if (record != null) {
            record.delete();
            return true;
        }
        return false;
    }

    public NegocioRecord actualizar(Integer id, NegocioDTO dto) {
        NegocioRecord record = obtenerPorId(id);

        if (record != null) {
            record.setNombre(dto.getNombre());
            record.setDireccion(dto.getDireccion());
            record.setTelefono(dto.getTelefono());
            record.update();
        }
        return record;
    }
}
