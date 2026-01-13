package com.fran.saludconecta.negocio.controller;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.fran.saludconecta.dto.ErrorResponse;
import com.fran.saludconecta.negocio.dto.NegocioDTO;
import com.fran.saludconecta.negocio.service.INegocioService;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;

@Controller
@RequestMapping("/api/negocios")
public class NegocioController {

	@Autowired
	private INegocioService negocioService;

	@GetMapping
	public ResponseEntity<?> listarTodos(HttpServletRequest request) {
		List<NegocioDTO> listaNegocios = negocioService.mostrarTodos();

		if (!listaNegocios.isEmpty()) {
			return ResponseEntity.ok(listaNegocios);

		} else {
			ErrorResponse error = mostrarError(request, HttpStatus.OK,
					"La lista de negocios está vacía");

			return ResponseEntity.status(HttpStatus.OK).body(error);
		}
	}

	@GetMapping("/{id}")
	public ResponseEntity<?> obtener(@PathVariable Integer id, HttpServletRequest request) {
		NegocioDTO negocioEncontrado = negocioService.mostrarPorId(id);

		if (negocioEncontrado != null) {
			return ResponseEntity.ok(negocioEncontrado);

		} else {
			ErrorResponse error = mostrarError(request, HttpStatus.OK,
					"Negocio con ID " + id + " no encontrado");

			return ResponseEntity.status(HttpStatus.OK).body(error);
		}
	}

	@PostMapping
	public ResponseEntity<?> crear(@Valid @RequestBody NegocioDTO dto, BindingResult result,
			HttpServletRequest request) {

		if (result.hasErrors()) {
			String mensaje = result.getFieldErrors().stream()
					.map(error -> error.getField() + ": " + error.getDefaultMessage())
					.collect(Collectors.joining(" | "));
			ErrorResponse error = mostrarError(request, HttpStatus.BAD_REQUEST, mensaje);

			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);

		} else {
			negocioService.crear(dto);
			return ResponseEntity.status(HttpStatus.CREATED).body(dto);
		}
	}

	@PutMapping("/{id}")
	public ResponseEntity<?> actualizar(@Valid @RequestBody NegocioDTO dto, BindingResult result,
			@PathVariable Integer id, HttpServletRequest request) {

		if (result.hasErrors()) {
			String mensaje = result.getFieldErrors().stream()
					.map(error -> error.getField() + ": " + error.getDefaultMessage())
					.collect(Collectors.joining(" | "));
			ErrorResponse error = mostrarError(request, HttpStatus.BAD_REQUEST, mensaje);

			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);

		} else {
			Boolean existe = negocioService.mostrarTodos().stream().anyMatch(p -> p.getId().equals(id));

			if (!existe) {
				ErrorResponse error = mostrarError(request, HttpStatus.BAD_REQUEST,
						"Negocio con ID " + id + " no encontrado");
				return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);

			} else {
				NegocioDTO actualizarNegocio = negocioService.modificar(id, dto);
				return ResponseEntity.ok(actualizarNegocio);
			}
		}
	}

	@DeleteMapping("/eliminar")
	public ResponseEntity<?> eliminar(@RequestParam Integer id, HttpServletRequest request) {
		boolean negocioEliminado = negocioService.borrar(id);

		if (negocioEliminado) {
			return ResponseEntity.status(HttpStatus.OK).body("Negocio " + id + " eliminado");

		} else {
			ErrorResponse error = mostrarError(request, HttpStatus.BAD_REQUEST,
					"Negocio con ID " + id + " no encontrado");

			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
		}
	}

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
}
