package com.fran.saludconecta.negocio.service;

import java.util.List;

import com.fran.saludconecta.negocio.dto.NegocioDTO;

public interface INegocioService {
	List<NegocioDTO> mostrarTodos();

	NegocioDTO mostrarPorId(Integer id);

	boolean crear(NegocioDTO dto);

	boolean comprobarCrear(NegocioDTO dto);

	NegocioDTO modificar(Integer id, NegocioDTO dto);

	boolean borrar(Integer id);
}
