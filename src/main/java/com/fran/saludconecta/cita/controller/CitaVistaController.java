package com.fran.saludconecta.cita.controller;

import java.security.Principal;
import java.time.LocalDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.fran.saludconecta.cita.dto.CitaDTO;
import com.fran.saludconecta.cita.service.ICitaService;
import com.fran.saludconecta.jooq.enums.EstadoCita;
import com.fran.saludconecta.paciente.dto.PacienteDTO;
import com.fran.saludconecta.paciente.service.IPacienteService;
import com.fran.saludconecta.usuario.dto.UsuarioDTO;
import com.fran.saludconecta.usuario.service.IUsuarioService;

@Controller
public class CitaVistaController {

    @Autowired
    private ICitaService citaService;

    @Autowired
    private IPacienteService pacienteService;

    @Autowired
    private IUsuarioService usuarioService;

    @GetMapping("/cita/crear")
    public String mostrarFormularioCrear(Principal principal, Model model) {

        model.addAttribute("usuarioActivo", principal.getName());

        CitaDTO cita = new CitaDTO();
        cita.setEstado(EstadoCita.pendiente);

        model.addAttribute("cita", cita);
        model.addAttribute("pacientes", pacienteService.mostrarTodos());
        model.addAttribute("usuarios", usuarioService.mostrarTodos());
        model.addAttribute("estados", EstadoCita.values());

        return "cita/cita-crear";
    }

    @PostMapping("/cita/crear")
    public String crearCita(
            @ModelAttribute("cita") CitaDTO cita,
            BindingResult result,
            Principal principal,
            Model model) {

        // Validaciones manuales
        if (cita.getPacienteId() == null) {
            result.rejectValue("pacienteId", "error.cita", "Debe seleccionar un paciente");
        }

        if (cita.getUsuarioId() == null) {
            result.rejectValue("usuarioId", "error.cita", "Debe seleccionar un profesional");
        }

        if (cita.getFechaCita() == null) {
            result.rejectValue("fechaCita", "error.cita", "La fecha de la cita es obligatoria");

        } else if (cita.getFechaCita().isBefore(LocalDateTime.now())) {
            result.rejectValue("fechaCita", "error.cita", "La fecha de la cita debe ser en el futuro");
        }

        if (cita.getMotivo() != null && cita.getMotivo().length() > 500) {
            result.rejectValue("motivo", "error.cita", "El motivo no puede superar los 500 caracteres");
        }

        // DEBUG: mostrar todos los errores
        // System.out.println("=== DEBUG CREAR CITA ===");
        // System.out.println("Has errors: " + result.hasErrors());
        // System.out.println("CitaDTO recibido: " + cita);
        // System.out.println(" - pacienteId: " + cita.getPacienteId());
        // System.out.println(" - usuarioId: " + cita.getUsuarioId());
        // System.out.println(" - fechaCita: " + cita.getFechaCita());
        // System.out.println(" - motivo: " + cita.getMotivo());
        // System.out.println(" - estado: " + cita.getEstado());

        // if (result.hasErrors()) {
        // System.out.println("Errores encontrados:");
        // result.getAllErrors().forEach(error -> {
        // System.out.println(" - " + error);
        // });
        // }
        // System.out.println("========================");

        // Si hay errores, volver al formulario
        if (result.hasErrors()) {
            model.addAttribute("usuarioActivo", principal.getName());
            model.addAttribute("pacientes", pacienteService.mostrarTodos());
            model.addAttribute("usuarios", usuarioService.mostrarTodos());
            model.addAttribute("estados", EstadoCita.values());
            return "cita/cita-crear";
        }

        // Poblar nombres desde los IDs
        PacienteDTO paciente = pacienteService.mostrarPorId(cita.getPacienteId());
        if (paciente != null) {
            cita.setNombrePaciente(paciente.getNombre());
        }

        UsuarioDTO usuario = usuarioService.mostrarPorId(cita.getUsuarioId());
        if (usuario != null) {
            cita.setNombreUsuario(usuario.getNombre());
        }

        // Establecer fechas
        LocalDateTime now = LocalDateTime.now();
        cita.setFechaCreacion(now);
        cita.setFechaModificacion(now);

        // Crear la cita
        citaService.crear(cita);
        return "redirect:/calendario-vista";
    }

    @GetMapping("/cita/buscar")
    public String buscarCitas(
            @RequestParam(required = false) Integer pacienteId,
            @RequestParam(required = false) Integer usuarioId,
            @RequestParam(required = false) String fecha,
            Principal principal,
            Model model) {

        model.addAttribute("usuarioActivo", principal.getName());
        model.addAttribute("pacientes", pacienteService.mostrarTodos());
        model.addAttribute("usuarios", usuarioService.mostrarTodos());

        // Mantener los valores de los filtros en el formulario
        model.addAttribute("pacienteId", pacienteId);
        model.addAttribute("usuarioId", usuarioId);
        model.addAttribute("fecha", fecha);

        // Si hay al menos un filtro, buscar citas
        if (pacienteId != null || usuarioId != null || fecha != null) {
            List<CitaDTO> todasLasCitas = citaService.mostrarTodos();
            List<CitaDTO> citasFiltradas = new java.util.ArrayList<>();

            for (CitaDTO c : todasLasCitas) {
                boolean cumpleFiltros = true;

                if (pacienteId != null && !c.getPacienteId().equals(pacienteId)) {
                    cumpleFiltros = false;
                }

                if (usuarioId != null && !c.getUsuarioId().equals(usuarioId)) {
                    cumpleFiltros = false;
                }

                if (fecha != null && !fecha.isEmpty()) {
                    if (c.getFechaCita() == null || !c.getFechaCita().toLocalDate().toString().equals(fecha)) {
                        cumpleFiltros = false;
                    }
                }

                if (cumpleFiltros) {
                    citasFiltradas.add(c);
                }
            }

            citasFiltradas.sort((a, b) -> a.getFechaCita().compareTo(b.getFechaCita()));

            // Poblar nombres desde los IDs
            for (CitaDTO c : citasFiltradas) {
                if (c.getPacienteId() != null) {
                    PacienteDTO paciente = pacienteService.mostrarPorId(c.getPacienteId());
                    if (paciente != null) {
                        c.setNombrePaciente(paciente.getNombre());
                    }
                }

                if (c.getUsuarioId() != null) {
                    UsuarioDTO usuario = usuarioService.mostrarPorId(c.getUsuarioId());
                    if (usuario != null) {
                        c.setNombreUsuario(usuario.getNombre());
                    }
                }
            }

            model.addAttribute("citas", citasFiltradas);
        }

        return "cita/cita-buscar";
    }

    @GetMapping("/cita/eliminar/{id}")
    public String confirmarEliminar(@PathVariable Integer id, Principal principal, Model model) {
        model.addAttribute("usuarioActivo", principal.getName());
        CitaDTO cita = citaService.mostrarPorId(id);

        if (cita.getPacienteId() != null) {
            PacienteDTO paciente = pacienteService.mostrarPorId(cita.getPacienteId());

            if (paciente != null) {
                cita.setNombrePaciente(paciente.getNombre());
            }
        }

        if (cita.getUsuarioId() != null) {
            UsuarioDTO usuario = usuarioService.mostrarPorId(cita.getUsuarioId());
            if (usuario != null) {
                cita.setNombreUsuario(usuario.getNombre());
            }
        }

        model.addAttribute("cita", cita);
        return "cita/cita-eliminar-confirmar";
    }

    @PostMapping("/cita/eliminar/{id}")
    public String eliminarCita(@PathVariable Integer id) {
        citaService.borrar(id);
        return "redirect:/cita/buscar";
    }
}
