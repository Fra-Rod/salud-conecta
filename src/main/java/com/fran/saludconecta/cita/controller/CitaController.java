package com.fran.saludconecta.cita.controller;

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
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.fran.saludconecta.cita.dto.CitaDTO;
import com.fran.saludconecta.cita.service.ICitaService;
import com.fran.saludconecta.dto.ErrorResponse;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/citas")
public class CitaController {

    @Autowired
    private ICitaService service;

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
        List<CitaDTO> lista = service.mostrarTodos();

        if (!lista.isEmpty()) {
            return ResponseEntity.ok(lista);

        } else {
            ErrorResponse error = mostrarError(request, HttpStatus.OK,
                    "La lista de citas está vacía");
            return ResponseEntity.status(HttpStatus.OK).body(error);
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> obtener(@PathVariable Integer id, HttpServletRequest request) {
        CitaDTO dtoEncontrado = service.mostrarPorId(id);

        if (dtoEncontrado != null) {
            return ResponseEntity.ok(dtoEncontrado);

        } else {
            ErrorResponse error = mostrarError(request, HttpStatus.OK,
                    "Cita con ID " + id + " no encontrada");
            return ResponseEntity.status(HttpStatus.OK).body(error);
        }
    }

    @GetMapping("detalles/{id}")
    public ResponseEntity<?> mostrarDetalles(@PathVariable Integer id, HttpServletRequest request) {
        CitaDTO detalles = service.mostrarDetallesPorId(id);

        if (detalles != null) {
            return ResponseEntity.ok(detalles);

        } else {
            ErrorResponse error = mostrarError(request, HttpStatus.OK,
                    "Detalles para ID " + id + " no encontrados");
            return ResponseEntity.status(HttpStatus.OK).body(error);
        }
    }

    @PostMapping
    public ResponseEntity<?> crear(@Valid @RequestBody CitaDTO dto, BindingResult result, HttpServletRequest request) {
        if (result.hasErrors()) {
            String mensaje = result.getFieldErrors().stream()
                    .map(error -> error.getField() + ": " + error.getDefaultMessage())
                    .collect(Collectors.joining(" | "));
            ErrorResponse error = mostrarError(request, HttpStatus.BAD_REQUEST, mensaje);

            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);

        } else {
            service.crear(dto);
            return ResponseEntity.status(HttpStatus.CREATED).body(dto);
        }
    }

    @DeleteMapping("/eliminar")
    public ResponseEntity<?> eliminar(@RequestParam Integer id, HttpServletRequest request) {
        boolean eliminado = service.borrar(id);

        if (eliminado) {
            return ResponseEntity.status(HttpStatus.OK).body("Cita " + id + " eliminada");

        } else {
            ErrorResponse error = mostrarError(request, HttpStatus.BAD_REQUEST,
                    "Cita con ID " + id + " no encontrada");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
        }
    }
}
