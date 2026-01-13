package com.fran.saludconecta.paciente.service;

import java.util.List;

import org.jooq.DSLContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fran.saludconecta.informe.dto.InformeDTO;
import com.fran.saludconecta.informe.mapper.InformeMapper;
import com.fran.saludconecta.informe.repository.InformeRepository;
import com.fran.saludconecta.jooq.tables.records.PacienteRecord;
import com.fran.saludconecta.paciente.dto.PacienteDTO;
import com.fran.saludconecta.paciente.mapper.PacienteMapper;
import com.fran.saludconecta.paciente.repository.PacienteRepository;

@Service
public class PacienteServiceImp implements IPacienteService {

	@Autowired
	private DSLContext dsl;

	@Autowired
	private PacienteRepository repository;
	@Autowired
	private InformeRepository informeRepository;

	@Override
	public List<PacienteDTO> mostrarTodos() {
		return repository.obtenerTodos().stream().map(PacienteMapper::toDTO).toList();
	}

	@Override
	public PacienteDTO mostrarPorId(Integer id) {
		return PacienteMapper.toDTO(repository.obtenerPorId(id));
	}

	@Override
	public boolean crear(PacienteDTO dto) {
		PacienteRecord guardarRecord = PacienteMapper.fromDTO(dto, dsl);
		PacienteRecord comprobarRecordPorId = repository.obtenerPorId(guardarRecord.getId());
		PacienteRecord comprobarRecordPorDni = repository.obtenerPorDni(guardarRecord.getDni());

		if (comprobarRecordPorId == null && comprobarRecordPorDni == null) {
			repository.guardar(guardarRecord);
			dto.setId(guardarRecord.getId());
			return true;
		}

		return false;
	}

	public boolean comprobarCrear(PacienteDTO dto) {
		PacienteRecord guardarRecord = PacienteMapper.fromDTO(dto, dsl);
		PacienteRecord comprobarRecordPorId = repository.obtenerPorId(guardarRecord.getId());
		PacienteRecord comprobarRecordPorDni = repository.obtenerPorDni(guardarRecord.getDni());

		if (comprobarRecordPorId == null && comprobarRecordPorDni == null) {
			return true;
		}

		return false;
	}

	@Override
	public PacienteDTO modificar(Integer id, PacienteDTO dto) {
		dto.setNombre(dto.getNombre().trim());
		dto.setDni(dto.getDni().trim());
		PacienteRecord record = repository.actualizar(id, dto);

		return PacienteMapper.toDTO(record);
	}

	@Override
	public boolean borrar(Integer id) {
		return repository.eliminar(id);
	}

	@Override
	public PacienteDTO mostrarDetallesPorId(Integer id) {
		PacienteDTO dto = mostrarPorId(id);
		List<InformeDTO> listaInformes = informeRepository.obtenerTodos().stream()
				.filter(i -> i.getPacienteId().equals(id)).map(InformeMapper::toDTO).toList();
		dto.setInforme(listaInformes);

		return dto;
	}
}
