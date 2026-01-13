package com.fran.saludconecta.export.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import jakarta.mail.internet.MimeMessage;

@Service
public class PacienteEmailServiceImpl implements IPacienteEmailService {

	@Autowired
	private JavaMailSender mailSender;

	@Autowired
	private PacienteExcelService pacienteExcelService;

	@Value("${spring.mail.username:}")
	private String mailUsername;

	@Override
	public void enviarPacientesPorEmail(String destinatario) throws Exception {

		// Generar Excel
		byte[] bytesExcel = pacienteExcelService.generarExcelPacientes();

		// Enviar correo
		MimeMessage mensaje = mailSender.createMimeMessage();
		MimeMessageHelper ayudante = new MimeMessageHelper(mensaje, true);

		ayudante.setFrom(mailUsername);
		ayudante.setReplyTo(mailUsername);
		ayudante.setTo(destinatario);
		ayudante.setSubject("Listado de Pacientes - SaludConecta");
		ayudante.setText("Adjunto el listado de pacientes en Excel.");
		ayudante.addAttachment("pacientes.xlsx", new ByteArrayResource(bytesExcel));

		mailSender.send(mensaje);
	}
}
