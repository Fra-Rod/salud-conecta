package com.fran.saludconecta.export.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.fran.saludconecta.export.service.IPacienteEmailService;
import com.fran.saludconecta.export.service.PacienteExcelService;

@Controller
@RequestMapping("/export")
public class ExportController {

    @Autowired
    private PacienteExcelService pacienteExcelService;

    @Autowired
    private IPacienteEmailService pacienteEmailService;

    @PostMapping("/pacientes/email")
    public String enviarPacientesPorEmail(@RequestParam String destinatario) {
        try {
            pacienteEmailService.enviarPacientesPorEmail(destinatario);
        } catch (Exception e) {
            // Error al enviar
        }
        return "redirect:/exports/email";
    }

    @PostMapping("/informes/email")
    public String enviarInformesPorEmail(@RequestParam String destinatario) {
        return "redirect:/exports/email";
    }

    @GetMapping("/pacientes")
    public ResponseEntity<ByteArrayResource> descargarExcelPacientes() {
        try {
            byte[] bytesExcel = pacienteExcelService.generarExcelPacientes();
            ByteArrayResource recurso = new ByteArrayResource(bytesExcel);

            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=pacientes.xlsx")
                    .contentType(MediaType
                            .parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"))
                    .body(recurso);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    @GetMapping("/informes")
    public ResponseEntity<String> descargarExcelInformes() {
        return ResponseEntity.ok("Función no implementada aún");
    }
}
