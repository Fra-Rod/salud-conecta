package com.fran.saludconecta.informe.service;

import java.util.List;

import com.fran.saludconecta.informe.dto.InformeDTO;

public interface IInformeService {
	List<InformeDTO> mostrarTodos();

	InformeDTO mostrarPorId(Integer id);

	boolean crear(InformeDTO dto);

	InformeDTO modificar(Integer id, InformeDTO dto);

	boolean borrar(Integer id);
}
