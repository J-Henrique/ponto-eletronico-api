package com.jhbb.pontointeligente.api.controller;

import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.jhbb.pontointeligente.api.dto.EmpresaDTO;
import com.jhbb.pontointeligente.api.entities.Empresa;
import com.jhbb.pontointeligente.api.response.Response;
import com.jhbb.pontointeligente.api.services.EmpresaService;

@RestController
@RequestMapping("/api/empresas")
@CrossOrigin(origins = "*")
public class EmpresaController {

	private static final Logger log = LoggerFactory.getLogger(EmpresaController.class);

	@Autowired
	private EmpresaService empresaService;

	public EmpresaController() {
	}

	/**
	 * Retorna uma empresa dado um CNPJ.
	 * 
	 * @param cnpj
	 * @return ResponseEntity<Response<EmpresaDto>>
	 */
	@GetMapping(value = "/cnpj/{cnpj}")
	public ResponseEntity<Response<EmpresaDTO>> buscarPorCnpj(@PathVariable("cnpj") String cnpj) {
		log.info("Buscando empresa por CNPJ: {}", cnpj);
		Response<EmpresaDTO> response = new Response<EmpresaDTO>();
		Optional<Empresa> empresa = empresaService.buscarPorCnpj(cnpj);

		if (!empresa.isPresent()) {
			log.info("Empresa não encontrada para o CNPJ: {}", cnpj);
			response.getErrors().add("Empresa não encontrada para o CNPJ " + cnpj);
			return ResponseEntity.badRequest().body(response);
		}

		response.setData(this.converterEmpresaDto(empresa.get()));
		return ResponseEntity.ok(response);
	}

	/**
	 * Popula um DTO com os dados de uma empresa.
	 * 
	 * @param empresa
	 * @return EmpresaDto
	 */
	private EmpresaDTO converterEmpresaDto(Empresa empresa) {
		EmpresaDTO empresaDto = new EmpresaDTO();
		empresaDto.setId(empresa.getId());
		empresaDto.setCnpj(empresa.getCnpj());
		empresaDto.setRazaoSocial(empresa.getRazaoSocial());

		return empresaDto;
	}

}
