package com.fran.saludconecta.negocio.dto;

import java.time.LocalDateTime;
import java.util.List;

import com.fran.saludconecta.usuario.dto.UsuarioDTO;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class NegocioDTO {

    private Integer id;

    @NotBlank(message = "El nombre no puede estar vacío")
    @Size(max = 50, message = "El nombre no puede tener más de 100 caracteres")
    private String nombre;

    private String direccion;

    @Size(max = 20, message = "El teléfono no puede tener más de 20 caracteres")
    private String telefono;

    private LocalDateTime fechaCreacion;
    private LocalDateTime fechaModificacion;

    private List<UsuarioDTO> usuarios;
}
