package com.fran.saludconecta.export.controller;

import java.security.Principal;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/exports")
public class ExportVistaController {

    @GetMapping("/excel")
    public String mostrarVistaExcel(Principal principal, Model model) {
        model.addAttribute("usuarioActivo", principal.getName());
        return "exports/excel-export";
    }

    @GetMapping("/email")
    public String mostrarVistaEmail(Principal principal, Model model) {
        model.addAttribute("usuarioActivo", principal.getName());
        return "exports/email-export";
    }

    @PostMapping("/pacientes/email")
    public String enviarPacientesEmail(Principal principal,
            @RequestParam("destinatario") String destinatario,
            RedirectAttributes redirectAttributes) {

        // Validación mínima
        if (destinatario == null || destinatario.isBlank()) {
            redirectAttributes.addFlashAttribute("mensaje", "El email destinatario es obligatorio");
            redirectAttributes.addFlashAttribute("tipoMensaje", "danger");
            return "redirect:/exports/email";
        }

        try {
            redirectAttributes.addFlashAttribute("mensaje", "Lista de pacientes enviada correctamente");
            redirectAttributes.addFlashAttribute("tipoMensaje", "success");
        } catch (Exception ex) {
            redirectAttributes.addFlashAttribute("mensaje", "Error al enviar pacientes: " + ex.getMessage());
            redirectAttributes.addFlashAttribute("tipoMensaje", "danger");
        }

        return "redirect:/exports/email";
    }

    @PostMapping("/informes/email")
    public String enviarInformesEmail(Principal principal,
            @RequestParam("destinatario") String destinatario,
            RedirectAttributes redirectAttributes) {

        if (destinatario == null || destinatario.isBlank()) {
            redirectAttributes.addFlashAttribute("mensaje", "El email destinatario es obligatorio");
            redirectAttributes.addFlashAttribute("tipoMensaje", "danger");
            return "redirect:/exports/email";
        }

        try {
            redirectAttributes.addFlashAttribute("mensaje", "Lista de informes enviada correctamente");
            redirectAttributes.addFlashAttribute("tipoMensaje", "success");
        } catch (Exception ex) {
            redirectAttributes.addFlashAttribute("mensaje", "Error al enviar informes: " + ex.getMessage());
            redirectAttributes.addFlashAttribute("tipoMensaje", "danger");
        }

        return "redirect:/exports/email";
    }
}
