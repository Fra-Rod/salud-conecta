package com.fran.saludconecta.usuario.service;

import java.util.List;

import com.fran.saludconecta.usuario.dto.UsuarioDTO;

public interface IUsuarioService {
	List<UsuarioDTO> mostrarTodos();

	UsuarioDTO mostrarPorId(Integer id);

	UsuarioDTO mostrarDetallesPorId(Integer id);

	boolean crear(UsuarioDTO dto);

	boolean comprobarCrear(UsuarioDTO dto);

	UsuarioDTO modificar(Integer id, UsuarioDTO dto);

	boolean borrar(Integer id);
}
