package com.fran.saludconecta.usuario.controller;

import java.security.Principal;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import com.fran.saludconecta.negocio.dto.NegocioDTO;
import com.fran.saludconecta.negocio.service.INegocioService;
import com.fran.saludconecta.usuario.dto.UsuarioDTO;
import com.fran.saludconecta.usuario.service.IUsuarioService;

import jakarta.validation.Valid;

@Controller
public class UsuarioVistaController {

    @Autowired
    private IUsuarioService service;
    @Autowired
    private INegocioService negocioService;

    @GetMapping("/usuario-perfil/{id}")
    public String mostrarPerfil(Principal principal, @PathVariable Integer id, Model model) {

        UsuarioDTO usuarioDto = service.mostrarTodos().stream()
                .filter(u -> principal.getName().equals(u.getNombre()))
                .findFirst()
                .orElse(null);

        model.addAttribute("usuario", usuarioDto);
        model.addAttribute("usuarioActivo", usuarioDto.getNombre());

        // Añade atributos específicos para el perfil
        String nombreNegocio = "Sin Negocio";
        if (usuarioDto != null && usuarioDto.getNegocioId() != null) {
            try {
                NegocioDTO negocio = negocioService.mostrarPorId(usuarioDto.getNegocioId());
                if (negocio != null) {
                    nombreNegocio = negocio.getNombre();
                }
            } catch (Exception e) {
                nombreNegocio = "Sin Negocio";
            }
        }
        model.addAttribute("nombreNegocio", nombreNegocio);

        return "usuario/usuario-perfil";
    }

    @GetMapping("/usuario-lista")
    public String mostrarLista(Principal principal, Model model) {

        model.addAttribute("usuarioActivo", principal.getName());

        List<UsuarioDTO> lista = service.mostrarTodos();
        model.addAttribute("usuarios", lista);
        return "usuario/usuario-lista";
    }

    @GetMapping("/usuario/ver/{id}")
    public String mostrarDetalle(Principal principal, @PathVariable Integer id, Model model) {

        model.addAttribute("usuarioActivo", principal.getName());

        UsuarioDTO dto = service.mostrarDetallesPorId(id);
        model.addAttribute("usuario", dto);
        return "usuario/usuario-detalle";
    }

    @GetMapping("/usuario/crear")
    public String mostrarFormularioCreacion(Principal principal, Model model) {

        model.addAttribute("usuarioActivo", principal.getName());

        model.addAttribute("usuario", new UsuarioDTO());
        model.addAttribute("negocios", negocioService.mostrarTodos());
        return "usuario/usuario-crear";
    }

    @PostMapping("/usuario/crear")
    public String procesarCreacion(Principal principal, @Valid @ModelAttribute("usuario") UsuarioDTO dto,
            BindingResult result, Model model) {

        model.addAttribute("usuarioActivo", principal.getName());

        // Si hay errores de validación (NotBlank, Size, ...)
        // se vuelve a mostrar el formulario con errores
        if (result.hasErrors()) {
            model.addAttribute("negocios", negocioService.mostrarTodos());
            return "usuario/usuario-crear";
        }

        boolean creado = service.crear(dto);
        if (!creado) {
            result.rejectValue("email", "error.email", "Ya existe un usuario con ese email");
            model.addAttribute("negocios", negocioService.mostrarTodos());
            return "usuario/usuario-crear";
        }

        service.crear(dto);
        return "redirect:/usuario-lista";
    }

    @GetMapping("/usuario/editar/{id}")
    public String mostrarFormularioEdicion(Principal principal, @PathVariable Integer id, Model model) {

        model.addAttribute("usuarioActivo", principal.getName());

        UsuarioDTO dto = service.mostrarPorId(id);
        if (dto != null) {
            model.addAttribute("usuario", dto);
            model.addAttribute("negocios", negocioService.mostrarTodos());
            return "usuario/usuario-editar";

        } else {
            return "redirect:/usuario-lista";
        }
    }

    @PostMapping("/usuario/editar/{id}")
    public String procesarEdicion(Principal principal, @PathVariable Integer id,
            @Valid @ModelAttribute("usuario") UsuarioDTO dto, BindingResult result, Model model) {

        model.addAttribute("usuarioActivo", principal.getName());

        if (result.hasErrors()) {
            model.addAttribute("negocios", negocioService.mostrarTodos());
            return "usuario/usuario-editar";
        }

        boolean puedeModificar = service.comprobarModificar(dto);
        if (!puedeModificar) {
            result.rejectValue("email", "error.email", "Ya existe un usuario con ese email");
            model.addAttribute("negocios", negocioService.mostrarTodos());
            return "usuario/usuario-editar";
        }

        try {
            service.modificar(id, dto);
        } catch (DataIntegrityViolationException ex) {
            result.rejectValue("email", "error.email", "Ese email ya está en uso");
            model.addAttribute("negocios", negocioService.mostrarTodos());
            return "usuario/usuario-editar";
        }

        return "redirect:/usuario-lista";
    }

    @GetMapping("/usuario/eliminar/{id}")
    public String mostrarConfirmacionEliminacion(Principal principal, @PathVariable Integer id, Model model) {

        model.addAttribute("usuarioActivo", principal.getName());

        UsuarioDTO dto = service.mostrarPorId(id);
        if (dto != null) {
            model.addAttribute("usuario", dto);
            return "usuario/usuario-eliminar-confirmar";

        } else {
            return "redirect:/usuario-lista";
        }
    }

    @PostMapping("/usuario/eliminar/{id}")
    public String procesarEliminacion(Principal principal, @PathVariable Integer id, Model model) {

        model.addAttribute("usuarioActivo", principal.getName());

        List<UsuarioDTO> usuarios = service.mostrarTodos();
        UsuarioDTO usuarioAEliminar = null;

        for (UsuarioDTO u : usuarios)
            if (u.getId().equals(id) && !u.getNombre().equals(principal.getName()))
                usuarioAEliminar = u;

        if (usuarioAEliminar == null) {
            return "redirect:/usuario-lista";
        }

        service.borrar(id);
        return "redirect:/usuario-lista";
    }
}
