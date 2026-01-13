package com.fran.saludconecta.usuario.service;

import java.util.List;

import org.jooq.DSLContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fran.saludconecta.jooq.tables.records.UsuarioRecord;
import com.fran.saludconecta.paciente.dto.PacienteDTO;
import com.fran.saludconecta.usuario.dto.UsuarioDTO;
import com.fran.saludconecta.usuario.mapper.UsuarioMapper;
import com.fran.saludconecta.usuario.repository.UsuarioRepository;

@Service
public class UsuarioServiceImpl implements IUsuarioService {

    @Autowired
    private DSLContext dsl;
    @Autowired
    private UsuarioRepository repository;

    @Override
    public List<UsuarioDTO> mostrarTodos() {
        return repository.obtenerTodos().stream().map(UsuarioMapper::toDTO).toList();
    }

    @Override
    public UsuarioDTO mostrarPorId(Integer id) {
        return UsuarioMapper.toDTO(repository.obtenerPorId(id));
    }

    @Override
    public UsuarioDTO mostrarDetallesPorId(Integer id) {
        UsuarioDTO dto = mostrarPorId(id);

        if (dto == null) {
            return null;
        }

        List<PacienteDTO> listaPacientes = repository.obtenerTodosPacientesUsuarios(id);
        dto.setPacientes(listaPacientes);
        return dto;
    }

    @Override
    public boolean crear(UsuarioDTO dto) {
        UsuarioRecord guardarRecord = UsuarioMapper.fromDTO(dto, dsl);
        UsuarioRecord comprobarRecord = repository.obtenerPorId(guardarRecord.getId());
        UsuarioRecord comprobarRecordPorEmail = repository.obtenerPorEmail(guardarRecord.getEmail());

        if (comprobarRecord == null && comprobarRecordPorEmail == null) {
            repository.guardar(guardarRecord);
            dto.setId(guardarRecord.getId());
            return true;
        }

        return false;
    }

    @Override
    public boolean comprobarModificar(UsuarioDTO dto) {
        String email = dto.getEmail().trim().toLowerCase();
        UsuarioRecord usuarioConEsteEmail = repository.obtenerPorEmail(email);

        if (usuarioConEsteEmail == null) {
            return true;
        }

        return usuarioConEsteEmail.getId().equals(dto.getId());
    }

    @Override
    public UsuarioDTO modificar(Integer id, UsuarioDTO dto) {
        dto.setNombre(dto.getNombre().trim());
        dto.setEmail(dto.getEmail().trim());

        UsuarioRecord record = repository.actualizar(id, dto);
        return UsuarioMapper.toDTO(record);
    }

    @Override
    public boolean borrar(Integer id) {
        return repository.eliminar(id);
    }
}
