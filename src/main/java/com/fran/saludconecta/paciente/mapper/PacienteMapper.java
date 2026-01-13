package com.fran.saludconecta.paciente.mapper;

import org.jooq.DSLContext;

import com.fran.saludconecta.jooq.tables.Paciente;
import com.fran.saludconecta.jooq.tables.records.PacienteRecord;
import com.fran.saludconecta.paciente.dto.PacienteDTO;

public class PacienteMapper {

	public static PacienteDTO toDTO(PacienteRecord record) {

		if (record == null)
			return null;

		return PacienteDTO.builder()
				.id(record.get(Paciente.PACIENTE.ID))
				.nombre(record.get(Paciente.PACIENTE.NOMBRE))
				.dni(record.get(Paciente.PACIENTE.DNI))
				.fechaNacimiento(record.get(Paciente.PACIENTE.FECHA_NACIMIENTO))
				.alta(record.get(Paciente.PACIENTE.ALTA))
				.build();
	}

	public static PacienteRecord fromDTO(PacienteDTO dto, DSLContext dsl) {

		if (dto == null)
			return null;

		PacienteRecord record = dsl.newRecord(Paciente.PACIENTE);
		// record.setId(dto.getId());
		record.setNombre(dto.getNombre().trim());
		record.setDni(dto.getDni().trim());
		record.setFechaNacimiento(dto.getFechaNacimiento());
		record.setAlta(true);
		return record;
	}
}
