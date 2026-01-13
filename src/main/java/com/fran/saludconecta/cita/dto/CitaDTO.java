package com.fran.saludconecta.cita.dto;

import java.time.LocalDateTime;

import com.fran.saludconecta.jooq.enums.EstadoCita;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CitaDTO {

    private Integer id;

    private Integer pacienteId;
    private Integer usuarioId;

    @NotBlank(message = "El nombre del paciente no puede estar vacío")
    @Size(max = 50, message = "El nombre del paciente no puede tener más de 50 caracteres")
    private String nombrePaciente;

    @NotBlank(message = "El nombre del profesional no puede estar vacío")
    @Size(max = 50, message = "El nombre del profesional no puede tener más de 50 caracteres")
    private String nombreUsuario;

    private LocalDateTime fechaCita;

    @Size(max = 500, message = "El motivo no puede tener más de 500 caracteres")
    private String motivo;

    private EstadoCita estado;

    private LocalDateTime fechaCreacion;
    private LocalDateTime fechaModificacion;
}
