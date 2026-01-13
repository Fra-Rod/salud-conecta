package com.fran.saludconecta.export.service;

public interface IPacienteEmailService {
	void enviarPacientesPorEmail(String destinatario) throws Exception;
}
