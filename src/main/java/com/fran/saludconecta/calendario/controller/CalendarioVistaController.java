package com.fran.saludconecta.calendario.controller;

import java.security.Principal;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.TextStyle;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.fran.saludconecta.cita.dto.CitaDTO;
import com.fran.saludconecta.cita.service.ICitaService;
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

        String usuarioActivo = principal.getName();
        model.addAttribute("usuarioActivo", usuarioActivo);

        Integer usuarioId = null;
        if (principal != null) {
            var usuarioOpt = usuarioService.mostrarTodos().stream()
                    .filter(u -> u.getNombre().equals(usuarioActivo))
                    .findFirst();

            if (usuarioOpt.isPresent()) {
                usuarioId = usuarioOpt.get().getId();
            }
        }

        // Mes y año: usa parámetros si existen, sino el mes actual
        YearMonth ym = (year != null && month != null)
                ? YearMonth.of(year, month)
                : YearMonth.now();

        int currentYear = ym.getYear();
        int currentMonth = ym.getMonthValue();
        String monthName = ym.getMonth().getDisplayName(TextStyle.FULL, new Locale("es", "ES"));

        // Construir grid del mes
        int daysInMonth = ym.lengthOfMonth();
        LocalDate firstOfMonth = ym.atDay(1);
        int startIndex = firstOfMonth.getDayOfWeek().getValue() - 1;

        List<List<Integer>> monthGrid = new ArrayList<>();
        List<Integer> week = new ArrayList<>();
        for (int i = 0; i < startIndex; i++)
            week.add(0);

        int day = 1;
        while (day <= daysInMonth) {
            week.add(day++);
            if (week.size() == 7) {
                monthGrid.add(week);
                week = new ArrayList<>();
            }
        }

        if (!week.isEmpty()) {
            while (week.size() < 7)
                week.add(0);
            monthGrid.add(week);
        }

        // Citas del mes
        List<CitaDTO> citasMes = new ArrayList<>();
        if (usuarioId != null) {
            citasMes = citaService.porUsuario(usuarioId).stream()
                    .filter(c -> c.getFechaCita() != null
                            && c.getFechaCita().getYear() == currentYear
                            && c.getFechaCita().getMonthValue() == currentMonth)
                    .toList();
        }

        // Map de citas por día
        Map<String, List<CitaDTO>> citasPorDia = new HashMap<>();
        for (CitaDTO c : citasMes) {
            int d = c.getFechaCita().getDayOfMonth();
            String key = String.valueOf(d);
            citasPorDia.computeIfAbsent(key, k -> new ArrayList<>()).add(c);
        }

        // Calcular mes anterior y siguiente para navegación
        YearMonth prevMonth = ym.minusMonths(1);
        YearMonth nextMonth = ym.plusMonths(1);

        List<CitaDTO> proximas = usuarioId == null ? List.of() : citaService.proximasPorUsuario(usuarioId, 5);
        List<CitaDTO> citasHoy = usuarioId == null ? List.of() : citaService.citasHoyPorUsuario(usuarioId);

        model.addAttribute("monthGrid", monthGrid);
        model.addAttribute("citasPorDia", citasPorDia);
        model.addAttribute("monthName", monthName);
        model.addAttribute("year", currentYear);
        model.addAttribute("month", currentMonth);
        model.addAttribute("prevYear", prevMonth.getYear());
        model.addAttribute("prevMonth", prevMonth.getMonthValue());
        model.addAttribute("nextYear", nextMonth.getYear());
        model.addAttribute("nextMonth", nextMonth.getMonthValue());
        model.addAttribute("proximasCitas", proximas.size());
        model.addAttribute("citasHoy", citasHoy);

        return "calendario/calendario-ver";
    }
}
