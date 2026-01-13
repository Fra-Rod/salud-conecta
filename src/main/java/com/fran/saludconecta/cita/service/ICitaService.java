package com.fran.saludconecta.cita.service;

import java.util.List;

import com.fran.saludconecta.cita.dto.CitaDTO;

public interface ICitaService {
    List<CitaDTO> mostrarTodos();

    CitaDTO mostrarPorId(Integer id);

    CitaDTO mostrarDetallesPorId(Integer id);

    boolean crear(CitaDTO dto);

    boolean comprobarCrear(CitaDTO dto);

    boolean borrar(Integer id);

    // Calendario
    List<CitaDTO> porUsuario(Integer usuarioId);

    List<CitaDTO> proximasPorUsuario(Integer usuarioId, int limit);

    List<CitaDTO> citasHoyPorUsuario(Integer usuarioId);
}
