package com.fran.saludconecta.export.controller;

import java.security.Principal;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

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
}
