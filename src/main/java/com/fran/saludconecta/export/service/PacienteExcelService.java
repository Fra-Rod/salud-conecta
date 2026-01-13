package com.fran.saludconecta.export.service;

import java.io.ByteArrayOutputStream;
import java.util.List;

import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fran.saludconecta.paciente.dto.PacienteDTO;
import com.fran.saludconecta.paciente.service.IPacienteService;

@Service
public class PacienteExcelService {

	@Autowired
	private IPacienteService pacienteService;

	public byte[] generarExcelPacientes() throws Exception {
		Workbook libro = new XSSFWorkbook();
		ByteArrayOutputStream salida = new ByteArrayOutputStream();

		try {
			Sheet hoja = libro.createSheet("Pacientes");

			// Crear fila de cabecera
			Row cabecera = hoja.createRow(0);
			cabecera.createCell(0).setCellValue("ID");
			cabecera.createCell(1).setCellValue("Nombre");
			cabecera.createCell(2).setCellValue("DNI");
			cabecera.createCell(3).setCellValue("Fecha Nacimiento");

			// Llenar filas con datos
			List<PacienteDTO> pacientes = pacienteService.mostrarTodos();
			int numeroFila = 1;

			for (PacienteDTO paciente : pacientes) {
				Row fila = hoja.createRow(numeroFila);
				numeroFila++;

				fila.createCell(0).setCellValue(paciente.getId());
				fila.createCell(1).setCellValue(paciente.getNombre());
				fila.createCell(2).setCellValue(paciente.getDni());
				fila.createCell(3).setCellValue(paciente.getFechaNacimiento().toString());
			}

			libro.write(salida);
			return salida.toByteArray();

		} finally {
			libro.close();
			salida.close();
		}
	}
}
