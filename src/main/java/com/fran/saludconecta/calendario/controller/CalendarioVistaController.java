package com.fran.saludconecta.calendario.controller;

import java.security.Principal;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.fran.saludconecta.cita.dto.CitaDTO;
import com.fran.saludconecta.cita.service.ICitaService;
import com.fran.saludconecta.usuario.dto.UsuarioDTO;
import com.fran.saludconecta.usuario.service.IUsuarioService;

@Controller
public class CalendarioVistaController {

    @Autowired
    private ICitaService citaService;

    @Autowired
    private IUsuarioService usuarioService;

    @GetMapping("/calendario-vista")
    public String calendarioVista(
            @RequestParam(required = false) Integer year,
            @RequestParam(required = false) Integer month,
            Principal principal,
            Model model) {

        model.addAttribute("usuarioActivo", principal.getName());

        UsuarioDTO usuarioDto = usuarioService.mostrarTodos().stream()
                .filter(u -> principal.getName().equals(u.getNombre()))
                .findFirst()
                .orElse(null);

        YearMonth yearMonth;
        if (year != null && month != null) {
            yearMonth = YearMonth.of(year, month);
        } else {
            yearMonth = YearMonth.now();
        }

        int anyoActual = yearMonth.getYear();
        int mesActual = yearMonth.getMonthValue();

        String[] nombresMeses = { "Enero", "Febrero", "Marzo", "Abril", "Mayo", "Junio",
                "Julio", "Agosto", "Septiembre", "Octubre", "Noviembre", "Diciembre" };
        String nombreMes = nombresMeses[mesActual - 1];

        // Construir grid del mes
        int diasEnMes = yearMonth.lengthOfMonth();
        LocalDate diaUnoMes = yearMonth.atDay(1);
        int inicio = diaUnoMes.getDayOfWeek().getValue() - 1;

        List<List<Integer>> gridMes = new ArrayList<>();
        List<Integer> semana = new ArrayList<>();
        for (int i = 0; i < inicio; i++)
            semana.add(0);

        int dia = 1;
        while (dia <= diasEnMes) {
            semana.add(dia++);
            if (semana.size() == 7) {
                gridMes.add(semana);
                semana = new ArrayList<>();
            }
        }

        if (!semana.isEmpty()) {
            while (semana.size() < 7)
                semana.add(0);
            gridMes.add(semana);
        }

        List<CitaDTO> citasMes = new ArrayList<>();
        if (usuarioDto != null && usuarioDto.getId() != null) {
            List<CitaDTO> todasCitasUsuario = citaService.porUsuario(usuarioDto.getId());

            for (CitaDTO cita : todasCitasUsuario) {
                if (cita.getFechaCita() != null) {
                    if (cita.getFechaCita().getYear() == anyoActual &&
                            cita.getFechaCita().getMonthValue() == mesActual) {
                        citasMes.add(cita);
                    }
                }
            }
        }

        Map<String, List<CitaDTO>> citasPorDia = new HashMap<>();
        for (CitaDTO cita : citasMes) {
            int numeroDia = cita.getFechaCita().getDayOfMonth();
            String claveDia = String.valueOf(numeroDia);

            if (!citasPorDia.containsKey(claveDia)) {
                citasPorDia.put(claveDia, new ArrayList<>());
            }
            citasPorDia.get(claveDia).add(cita);
        }

        YearMonth mesAnterior = yearMonth.minusMonths(1);
        YearMonth mesSiguiente = yearMonth.plusMonths(1);

        List<CitaDTO> proximas = new ArrayList<>();
        if (usuarioDto != null && usuarioDto.getId() != null) {
            proximas = citaService.proximasPorUsuario(usuarioDto.getId(), 5);
        }

        List<CitaDTO> citasHoy = new ArrayList<>();
        if (usuarioDto != null && usuarioDto.getId() != null) {
            citasHoy = citaService.citasHoyPorUsuario(usuarioDto.getId());
        }

        model.addAttribute("gridMes", gridMes);
        model.addAttribute("citasPorDia", citasPorDia);
        model.addAttribute("nombreMes", nombreMes);
        model.addAttribute("anyo", anyoActual);
        model.addAttribute("mes", mesActual);
        model.addAttribute("anyoAnterior", mesAnterior.getYear());
        model.addAttribute("mesAnterior", mesAnterior.getMonthValue());
        model.addAttribute("anyoSiguiente", mesSiguiente.getYear());
        model.addAttribute("mesSiguiente", mesSiguiente.getMonthValue());
        model.addAttribute("totalProximas", proximas.size());
        model.addAttribute("citasHoy", citasHoy);

        return "calendario/calendario-ver";
    }
}
