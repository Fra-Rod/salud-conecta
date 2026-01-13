package com.fran.saludconecta.paciente.controller;

import java.security.Principal;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import com.fran.saludconecta.paciente.dto.PacienteDTO;
import com.fran.saludconecta.paciente.service.IPacienteService;

import jakarta.validation.Valid;

@Controller
public class PacienteVistaController {

    @Autowired
    private IPacienteService service;

    @GetMapping("/paciente-lista")
    public String pacientes(Principal principal, Model model) {
        model.addAttribute("usuarioActivo", principal.getName());

        List<PacienteDTO> listaPacientes = service.mostrarTodos();
        model.addAttribute("pacientes", listaPacientes);

        return "paciente/paciente-lista";
    }

    @GetMapping("/paciente/ver/{id}")
    public String pacientesDetalles(Principal principal, @PathVariable Integer id, Model model) {

        model.addAttribute("usuarioActivo", principal.getName());

        PacienteDTO elemento = service.mostrarDetallesPorId(id);
        model.addAttribute("paciente", elemento);

        return "paciente/paciente-detalle";
    }

    @GetMapping("/paciente/crear")
    public String mostrarFormularioCreacion(Principal principal, Model model) {

        model.addAttribute("usuarioActivo", principal.getName());

        model.addAttribute("paciente", new PacienteDTO());
        return "paciente/paciente-crear";
    }

    @PostMapping("/paciente/crear")
    public String procesarCreacion(Principal principal, @Valid @ModelAttribute("paciente") PacienteDTO dto,
            BindingResult result, Model model) {

        model.addAttribute("usuarioActivo", principal.getName());

        if (result.hasErrors()) {
            return "paciente/paciente-crear";
        }

        boolean creado = service.crear(dto);
        if (!creado) {
            result.rejectValue("dni", "error.dni", "Ya existe un paciente con ese DNI");
            return "paciente/paciente-crear";
        }

        service.crear(dto);
        return "redirect:/paciente-lista";
    }

    @GetMapping("/paciente/editar/{id}")
    public String mostrarFormularioEdicion(Principal principal, @PathVariable Integer id, Model model) {

        model.addAttribute("usuarioActivo", principal.getName());

        PacienteDTO paciente = service.mostrarPorId(id);
        if (paciente != null) {
            model.addAttribute("paciente", paciente);
            return "paciente/paciente-editar";

        } else {
            return "redirect:/paciente-lista";
        }
    }

    @PostMapping("/paciente/editar/{id}")
    public String procesarEdicion(Principal principal, @PathVariable Integer id,
            @Valid @ModelAttribute("paciente") PacienteDTO dto, BindingResult result, Model model) {

        model.addAttribute("usuarioActivo", principal.getName());

        if (result.hasErrors()) {
            return "paciente/paciente-editar";
        }

        boolean creado = service.comprobarCrear(dto);
        if (!creado) {
            result.rejectValue("dni", "error.dni", "Ya existe un paciente con ese DNI");
            return "paciente/paciente-editar";
        }

        service.modificar(id, dto);
        return "redirect:/paciente-lista";
    }

    @GetMapping("/paciente/eliminar/{id}")
    public String mostrarConfirmacionEliminacion(Principal principal, @PathVariable Integer id, Model model) {

        model.addAttribute("usuarioActivo", principal.getName());

        PacienteDTO paciente = service.mostrarPorId(id);
        if (paciente != null) {
            model.addAttribute("paciente", paciente);
            return "paciente/paciente-eliminar-confirmar";

        } else {
            return "redirect:/paciente-lista";
        }
    }

    @PostMapping("/paciente/eliminar/{id}")
    public String procesarEliminacion(Principal principal, @PathVariable Integer id, Model model) {

        model.addAttribute("usuarioActivo", principal.getName());

        service.borrar(id);
        return "redirect:/paciente-lista";
    }
}
