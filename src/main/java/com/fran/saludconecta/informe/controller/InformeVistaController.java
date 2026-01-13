package com.fran.saludconecta.informe.controller;

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

import com.fran.saludconecta.informe.dto.InformeDTO;
import com.fran.saludconecta.informe.service.IInformeService;
import com.fran.saludconecta.paciente.dto.PacienteDTO;
import com.fran.saludconecta.paciente.service.IPacienteService;
import com.fran.saludconecta.usuario.dto.UsuarioDTO;
import com.fran.saludconecta.usuario.service.IUsuarioService;

import jakarta.validation.Valid;

@Controller
public class InformeVistaController {

    @Autowired
    private IInformeService service;

    @Autowired
    private IUsuarioService usuarioService;

    @Autowired
    private IPacienteService pacienteService;

    @GetMapping("/informe-lista")
    public String mostrarLista(Principal principal, Model model) {

        model.addAttribute("usuarioActivo", principal.getName());

        List<InformeDTO> lista = service.mostrarTodos();
        model.addAttribute("informes", lista);
        return "informe/informe-lista";
    }

    @GetMapping("/informe/ver/{id}")
    public String mostrarDetalle(@PathVariable Integer id, Principal principal, Model model) {

        model.addAttribute("usuarioActivo", principal.getName());

        InformeDTO elemento = service.mostrarPorId(id);
        model.addAttribute("elemento", elemento);
        return "informe/informe-detalles";
    }

    @GetMapping("/informe/crear")
    public String mostrarFormularioCreacion(Principal principal, Model model) {

        model.addAttribute("usuarioActivo", principal.getName());
        model.addAttribute("informe", new InformeDTO());

        List<UsuarioDTO> usuarios = usuarioService.mostrarTodos();
        List<PacienteDTO> pacientes = pacienteService.mostrarTodos();
        model.addAttribute("usuarios", usuarios);
        model.addAttribute("pacientes", pacientes);

        return "informe/informe-crear";
    }

    @PostMapping("/informe/crear")
    public String procesarCreacion(Principal principal, @Valid @ModelAttribute("informe") InformeDTO dto,
            BindingResult result, Model model) {

        model.addAttribute("usuarioActivo", principal.getName());

        if (result.hasErrors()) {
            List<UsuarioDTO> usuarios = usuarioService.mostrarTodos();
            List<PacienteDTO> pacientes = pacienteService.mostrarTodos();
            model.addAttribute("usuarios", usuarios);
            model.addAttribute("pacientes", pacientes);
            return "informe/informe-crear";
        }

        service.crear(dto);
        return "redirect:/informe-lista";
    }

    @GetMapping("/informe/editar/{id}")
    public String mostrarFormularioEdicion(Principal principal, @PathVariable Integer id, Model model) {

        model.addAttribute("usuarioActivo", principal.getName());

        InformeDTO dto = service.mostrarPorId(id);
        if (dto != null) {
            model.addAttribute("informe", dto);
            List<UsuarioDTO> usuarios = usuarioService.mostrarTodos();
            List<PacienteDTO> pacientes = pacienteService.mostrarTodos();
            model.addAttribute("usuarios", usuarios);
            model.addAttribute("pacientes", pacientes);

            return "informe/informe-editar";

        } else {
            return "redirect:/informe-lista";
        }
    }

    @PostMapping("/informe/editar/{id}")
    public String procesarEdicion(Principal principal, @PathVariable Integer id,
            @Valid @ModelAttribute("informe") InformeDTO dto, BindingResult result, Model model) {

        model.addAttribute("usuarioActivo", principal.getName());

        if (result.hasErrors()) {
            List<UsuarioDTO> usuarios = usuarioService.mostrarTodos();
            List<PacienteDTO> pacientes = pacienteService.mostrarTodos();
            model.addAttribute("usuarios", usuarios);
            model.addAttribute("pacientes", pacientes);
            return "informe/informe-editar";
        }

        service.modificar(id, dto);
        return "redirect:/informe-lista";
    }

    @GetMapping("/informe/eliminar/{id}")
    public String mostrarConfirmacionEliminacion(Principal principal, @PathVariable Integer id, Model model) {

        model.addAttribute("usuarioActivo", principal.getName());

        InformeDTO dto = service.mostrarPorId(id);
        if (dto != null) {
            model.addAttribute("informe", dto);
            return "informe/informe-eliminar-confirmar";

        } else {
            return "redirect:/informe-lista";
        }
    }

    @PostMapping("/informe/eliminar/{id}")
    public String procesarEliminacion(Principal principal, @PathVariable Integer id, Model model) {

        model.addAttribute("usuarioActivo", principal.getName());

        service.borrar(id);
        return "redirect:/informe-lista";
    }
}
