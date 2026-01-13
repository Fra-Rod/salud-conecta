package com.fran.saludconecta.usuario.controller;

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
import com.fran.saludconecta.usuario.dto.UsuarioDTO;
import com.fran.saludconecta.usuario.service.IUsuarioService;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;

@Controller
@RequestMapping("/api/usuarios")
public class UsuarioController {

	@Autowired
	private IUsuarioService usuarioService;

	@GetMapping
	public ResponseEntity<?> listarTodos(HttpServletRequest request) {
		List<UsuarioDTO> listaUsuarios = usuarioService.mostrarTodos();

		if (!listaUsuarios.isEmpty()) {
			return ResponseEntity.ok(listaUsuarios);

		} else {
			ErrorResponse error = mostrarError(request, HttpStatus.OK,
					"La lista de usuarios está vacía");
			return ResponseEntity.status(HttpStatus.OK).body(error);
		}
	}

	@GetMapping("/{id}")
	public ResponseEntity<?> obtener(@PathVariable Integer id, HttpServletRequest request) {
		UsuarioDTO usuarioEncontrado = usuarioService.mostrarPorId(id);

		if (usuarioEncontrado != null) {
			return ResponseEntity.ok(usuarioEncontrado);

		} else {
			ErrorResponse error = mostrarError(request, HttpStatus.OK,
					"Usuario con ID " + id + " no encontrado");
			return ResponseEntity.status(HttpStatus.OK).body(error);
		}
	}

	@GetMapping("detalles/{id}")
	public ResponseEntity<?> mostrarDetalles(@PathVariable Integer id, HttpServletRequest request) {
		UsuarioDTO detallesDTO = usuarioService.mostrarDetallesPorId(id);

		if (detallesDTO != null) {
			return ResponseEntity.ok(detallesDTO);

		} else {
			ErrorResponse error = mostrarError(request, HttpStatus.OK,
					"ID " + id + " no encontrado");
			return ResponseEntity.status(HttpStatus.OK).body(error);
		}
	}

	@PostMapping
	public ResponseEntity<?> crear(@Valid @RequestBody UsuarioDTO dto, BindingResult result,
			HttpServletRequest request) {

		if (result.hasErrors()) {
			String mensaje = result.getFieldErrors().stream()
					.map(error -> error.getField() + ": " + error.getDefaultMessage())
					.collect(Collectors.joining(" | "));
			ErrorResponse error = mostrarError(request, HttpStatus.BAD_REQUEST, mensaje);

			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);

		} else {

			if (!usuarioService.crear(dto)) {
				ErrorResponse error = mostrarError(request, HttpStatus.BAD_REQUEST,
						"Ya existe un usuario con el email: " + dto.getEmail());
				return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);

			} else {
				usuarioService.crear(dto);
				return ResponseEntity.status(HttpStatus.CREATED).body(dto);
			}
		}
	}

	@PutMapping("/{id}")
	public ResponseEntity<?> actualizar(@Valid @RequestBody UsuarioDTO dto, BindingResult result,
			@PathVariable Integer id, HttpServletRequest request) {

		if (result.hasErrors()) {
			String mensaje = result.getFieldErrors().stream()
					.map(error -> error.getField() + ": " + error.getDefaultMessage())
					.collect(Collectors.joining(" | "));
			ErrorResponse error = mostrarError(request, HttpStatus.BAD_REQUEST, mensaje);

			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
		} else {
			Boolean existe = usuarioService.mostrarTodos().stream().anyMatch(p -> p.getId().equals(id));

			if (!existe) {
				ErrorResponse error = mostrarError(request, HttpStatus.BAD_REQUEST,
						"Usuario con ID " + id + " no encontrado");
				return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);

			} else {
				UsuarioDTO actualizarUsuario = usuarioService.modificar(id, dto);
				return ResponseEntity.ok(actualizarUsuario);
			}
		}
	}

	@DeleteMapping("/eliminar")
	public ResponseEntity<?> eliminar(@RequestParam Integer id, HttpServletRequest request) {
		boolean usuarioEliminado = usuarioService.borrar(id);

		if (usuarioEliminado) {
			return ResponseEntity.status(HttpStatus.OK).body("Usuario " + id + " eliminado");

		} else {
			ErrorResponse error = mostrarError(request, HttpStatus.BAD_REQUEST,
					"Usuario con ID " + id + " no encontrado");
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
		}
	}

	private ErrorResponse mostrarError(HttpServletRequest request, HttpStatus status, String message) {
		ErrorResponse error = ErrorResponse.builder() // response personalizado
				.timeStamp(LocalDateTime.now())
				.status(status.value())
				.error("Algo ha ido mal")
				.message(message)
				.path(request.getRequestURI())
				.build();

		return error;
	}
}
