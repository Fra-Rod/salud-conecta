package com.fran.saludconecta.informe.controller;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.fran.saludconecta.dto.ErrorResponse;
import com.fran.saludconecta.informe.dto.InformeDTO;
import com.fran.saludconecta.informe.service.IInformeService;

import jakarta.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/api/informes")
public class InformeController {

	@Autowired
	private IInformeService service;

	private ErrorResponse mostrarError(HttpServletRequest request, HttpStatus status, String message) {
		ErrorResponse error = ErrorResponse.builder()
				.timeStamp(LocalDateTime.now())
				.status(status.value())
				.error("Algo ha ido mal")
				.message(message)
				.path(request.getRequestURI())
				.build();

		return error;
	}

	@GetMapping
	public ResponseEntity<?> listarTodos(HttpServletRequest request) {
		List<InformeDTO> lista = service.mostrarTodos();

		if (!lista.isEmpty()) {
			return ResponseEntity.ok(lista);

		} else {
			ErrorResponse error = mostrarError(request, HttpStatus.OK, "La lista está vacía");
			return ResponseEntity.status(HttpStatus.OK).body(error);
		}
	}

	@GetMapping("/{id}")
	public ResponseEntity<?> obtener(@PathVariable Integer id, HttpServletRequest request) {
		InformeDTO dtoEncontrado = service.mostrarPorId(id);

		if (dtoEncontrado != null) {
			return ResponseEntity.ok(dtoEncontrado);

		} else {
			ErrorResponse error = mostrarError(request, HttpStatus.OK, "ID " + id + " no encontrado");
			return ResponseEntity.status(HttpStatus.OK).body(error);
		}
	}
}
