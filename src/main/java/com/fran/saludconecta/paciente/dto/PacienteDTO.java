package com.fran.saludconecta.paciente.dto;

import java.time.LocalDate;
import java.util.List;

import com.fran.saludconecta.informe.dto.InformeDTO;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Past;
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
public class PacienteDTO {
	private Integer id;

	@NotBlank(message = "El nombre no puede estar vacío")
	@Size(max = 50, message = "El nombre no puede tener más de 50 caracteres")
	private String nombre;

	@NotBlank(message = "El DNI no puede estar vacío")
	@Size(max = 20, message = "El DNI no puede tener más de 20 caracteres")
	private String dni;

	@Past(message = "La fecha de nacimiento debe ser anterior a hoy")
	private LocalDate fechaNacimiento;

	private Boolean alta;
	private LocalDate fechaCreacion;
	private LocalDate fechaModificacion;
	private List<InformeDTO> informe;
}
