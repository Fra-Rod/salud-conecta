package com.fran.saludconecta.paciente.controller;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.fran.saludconecta.dto.ErrorResponse;
import com.fran.saludconecta.paciente.dto.PacienteDTO;
import com.fran.saludconecta.paciente.service.IPacienteService;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/pacientes")
public class PacienteController {

	@Autowired
	private IPacienteService service;

	@GetMapping
	public ResponseEntity<?> listarTodos(HttpServletRequest request) {
		List<PacienteDTO> listaPacientes = service.mostrarTodos();

		if (!listaPacientes.isEmpty()) {
			return ResponseEntity.ok(listaPacientes);

		} else {
			ErrorResponse error = mostrarError(request, HttpStatus.OK,
					"La lista de pacientes está vacía");

			return ResponseEntity.status(HttpStatus.OK).body(error);
		}
	}

	@GetMapping("/{id}")
	public ResponseEntity<?> obtener(@PathVariable Integer id, HttpServletRequest request) {
		PacienteDTO pacienteEncontrado = service.mostrarPorId(id);

		if (pacienteEncontrado != null) {
			return ResponseEntity.ok(pacienteEncontrado);

		} else {
			ErrorResponse error = mostrarError(request, HttpStatus.OK,
					"Paciente con ID " + id + " no encontrado");

			return ResponseEntity.status(HttpStatus.OK).body(error);
		}
	}

	@PostMapping
	public ResponseEntity<?> crear(@Valid @RequestBody PacienteDTO dto, BindingResult result,
			HttpServletRequest request) {

		if (result.hasErrors()) {

			String mensaje = result.getFieldErrors()
					.stream()
					.map(error -> error.getField() + ": " +
							error.getDefaultMessage())
					.collect(Collectors.joining(" | "));

			ErrorResponse error = mostrarError(request, HttpStatus.BAD_REQUEST, mensaje);

			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);

		} else {

			service.crear(dto);
			return ResponseEntity.status(HttpStatus.CREATED).body(dto);
		}
	}

	@PutMapping("/{id}")
	public ResponseEntity<?> actualizar(@Valid @RequestBody PacienteDTO dto, BindingResult result,
			@PathVariable Integer id, HttpServletRequest request) {

		if (result.hasErrors()) {
			String mensaje = result.getFieldErrors().stream()
					.map(error -> error.getField() + ": " + error.getDefaultMessage())
					.collect(Collectors.joining(" | "));
			ErrorResponse error = mostrarError(request, HttpStatus.BAD_REQUEST, mensaje);

			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
		} else {
			Boolean existe = service.mostrarTodos().stream().anyMatch(p -> p.getId().equals(id));

			if (!existe) {
				ErrorResponse error = mostrarError(request, HttpStatus.BAD_REQUEST,
						"Paciente con ID " + id + " no encontrado");
				return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
			} else {
				PacienteDTO actualizarPaciente = service.modificar(id, dto);
				return ResponseEntity.ok(actualizarPaciente);
			}
		}
	}

	@GetMapping("detalles/{id}")
	public ResponseEntity<?> detallesPaciente(@PathVariable Integer id, HttpServletRequest request) {
		PacienteDTO detallesDTO = service.mostrarDetallesPorId(id);

		if (detallesDTO != null) {
			return ResponseEntity.ok(detallesDTO);

		} else {
			ErrorResponse error = mostrarError(request, HttpStatus.OK,
					"ID " + id + " no encontrado");

			return ResponseEntity.status(HttpStatus.OK).body(error);
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

	@DeleteMapping("/eliminar")
	public ResponseEntity<?> eliminar(@RequestParam Integer id, HttpServletRequest request) {
		boolean pacienteEliminado = service.borrar(id);

		if (pacienteEliminado) {
			return ResponseEntity.status(HttpStatus.OK).body("Paciente " + id + " eliminado");

		} else {
			ErrorResponse error = mostrarError(request, HttpStatus.BAD_REQUEST,
					"Paciente con ID " + id + " no encontrado");

			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
		}
	}
}
