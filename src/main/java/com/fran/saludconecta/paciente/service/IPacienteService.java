package com.fran.saludconecta.paciente.service;

import java.util.List;

import com.fran.saludconecta.paciente.dto.PacienteDTO;

public interface IPacienteService {
	List<PacienteDTO> mostrarTodos();

	PacienteDTO mostrarPorId(Integer id);

	PacienteDTO mostrarDetallesPorId(Integer id);

	boolean crear(PacienteDTO dto);

	boolean comprobarCrear(PacienteDTO dto);

	PacienteDTO modificar(Integer id, PacienteDTO dto);

	boolean borrar(Integer id);
}
