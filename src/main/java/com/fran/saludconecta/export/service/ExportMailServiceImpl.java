package com.fran.saludconecta.export.service;

import java.io.ByteArrayOutputStream;
import java.time.LocalDate;
import java.time.Period;
import java.util.List;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import com.fran.saludconecta.paciente.dto.PacienteDTO;
import com.fran.saludconecta.paciente.service.IPacienteService;

import jakarta.mail.internet.MimeMessage;

@Service
public class ExportMailServiceImpl implements IExportMailService {

    @Autowired
    private JavaMailSender mailSender;
    @Autowired
    private IPacienteService pacienteService;

    private static final Logger logger = LoggerFactory.getLogger(ExportMailServiceImpl.class);

    @Value("${spring.mail.username:}")
    private String mailUsername;

    @Override
    public void enviarPacientesExcel(String destinatario) throws Exception {

        // Generar Excel en memoria
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        try (Workbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet("Pacientes");
            CellStyle headerStyle = workbook.createCellStyle();
            Font font = workbook.createFont();
            font.setBold(true);
            headerStyle.setFont(font);
            headerStyle.setFillForegroundColor(IndexedColors.LIGHT_BLUE.getIndex());
            headerStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
            Row header = sheet.createRow(0);
            String[] columnas = { "ID", "Nombre", "Apellidos", "DNI", "Teléfono", "Email", "Fecha Nacimiento", "Edad" };
            for (int i = 0; i < columnas.length; i++) {
                Cell cell = header.createCell(i);
                cell.setCellValue(columnas[i]);
                cell.setCellStyle(headerStyle);
                sheet.setColumnWidth(i, 20 * 256);
            }
            List<PacienteDTO> pacientes = pacienteService.mostrarTodos();
            int rowIdx = 1;
            for (PacienteDTO p : pacientes) {
                Row row = sheet.createRow(rowIdx++);
                row.createCell(0).setCellValue(p.getId());
                row.createCell(1).setCellValue(p.getNombre());
                row.createCell(2).setCellValue(p.getNombre());
                row.createCell(3).setCellValue(p.getDni());
                row.createCell(4).setCellValue(p.getDni());
                row.createCell(5).setCellValue(p.getDni());
                row.createCell(6).setCellValue(p.getFechaNacimiento() != null ? p.getFechaNacimiento().toString() : "");
                if (p.getFechaNacimiento() != null) {
                    int edad = Period.between(p.getFechaNacimiento(), LocalDate.now()).getYears();
                    row.createCell(7).setCellValue(edad);
                } else {
                    row.createCell(7).setCellValue("");
                }
            }
            workbook.write(out);
        }
        // Enviar correo con el Excel adjunto
        try {
            // Activar debug de JavaMail si podemos (volcará la conversación SMTP en logs)
            if (mailSender instanceof JavaMailSenderImpl) {
                JavaMailSenderImpl impl = (JavaMailSenderImpl) mailSender;
                try {
                    impl.getSession().setDebug(true);
                    logger.debug("JavaMail session debug enabled");
                } catch (Exception ex) {
                    logger.warn("No se pudo activar JavaMail debug: {}", ex.getMessage());
                }
            }

            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true);
            // Asegurar From y Reply-To con la cuenta autenticada para evitar filtros
            if (mailUsername != null && !mailUsername.isBlank()) {
                helper.setFrom(mailUsername);
                helper.setReplyTo(mailUsername);
            }
            helper.setTo(destinatario);
            helper.setSubject("Listado de Pacientes - SaludConecta");
            helper.setText("Adjunto el listado de pacientes en Excel.");
            helper.addAttachment("pacientes.xlsx", new ByteArrayResource(out.toByteArray()));
            mailSender.send(message);

            // Intentar obtener Message-ID para facilitar rastreo
            String messageId = null;
            try {
                messageId = message.getMessageID();
            } catch (Exception ex) {
                logger.debug("No se pudo obtener Message-ID: {}", ex.getMessage());
            }

            logger.info("Correo enviado correctamente a {} (Message-ID={})", destinatario, messageId);
        } catch (Exception e) {
            logger.error("Error enviando correo a {}: {}", destinatario, e.getMessage(), e);
            throw new RuntimeException("Error enviando correo: " + e.getMessage(), e);
        }
    }
}
