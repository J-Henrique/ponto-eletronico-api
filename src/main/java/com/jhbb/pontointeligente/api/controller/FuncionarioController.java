package com.jhbb.pontointeligente.api.controller;

import java.math.BigDecimal;
import java.security.NoSuchAlgorithmException;
import java.util.Optional;

import javax.validation.Valid;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.jhbb.pontointeligente.api.dto.FuncionarioDTO;
import com.jhbb.pontointeligente.api.entities.Funcionario;
import com.jhbb.pontointeligente.api.response.Response;
import com.jhbb.pontointeligente.api.services.FuncionarioService;
import com.jhbb.pontointeligente.api.utils.PasswordUtils;

@RestController
@RequestMapping("/api/funcionarios")
@CrossOrigin(origins = "*")
public class FuncionarioController {

	private static final Logger log = LoggerFactory.getLogger(FuncionarioController.class);

	@Autowired
	private FuncionarioService funcionarioService;

	public FuncionarioController() {
	}

	/**
	 * Atualiza os dados de um funcionário.
	 * 
	 * @param id
	 * @param funcionarioDto
	 * @param result
	 * @return ResponseEntity<Response<FuncionarioDto>>
	 * @throws NoSuchAlgorithmException
	 */
	@PutMapping(value = "/{id}")
	public ResponseEntity<Response<FuncionarioDTO>> atualizar(@PathVariable("id") Long id,
			@Valid @RequestBody FuncionarioDTO funcionarioDto, BindingResult result) throws NoSuchAlgorithmException {
		log.info("Atualizando funcionário: {}", funcionarioDto.toString());
		Response<FuncionarioDTO> response = new Response<FuncionarioDTO>();

		Optional<Funcionario> funcionario = this.funcionarioService.buscarPorId(id);
		if (!funcionario.isPresent()) {
			result.addError(new ObjectError("funcionario", "Funcionário não encontrado."));
		}

		this.atualizarDadosFuncionario(funcionario.get(), funcionarioDto, result);

		if (result.hasErrors()) {
			log.error("Erro validando funcionário: {}", result.getAllErrors());
			result.getAllErrors().forEach(error -> response.getErrors().add(error.getDefaultMessage()));
			return ResponseEntity.badRequest().body(response);
		}

		this.funcionarioService.persistir(funcionario.get());
		response.setData(this.converterFuncionarioDto(funcionario.get()));

		return ResponseEntity.ok(response);
	}

	/**
	 * Atualiza os dados do funcionário com base nos dados encontrados no DTO.
	 * 
	 * @param funcionario
	 * @param funcionarioDto
	 * @param result
	 * @throws NoSuchAlgorithmException
	 */
	private void atualizarDadosFuncionario(Funcionario funcionario, FuncionarioDTO funcionarioDto, BindingResult result)
			throws NoSuchAlgorithmException {
		funcionario.setNome(funcionarioDto.getNome());

		if (!funcionario.getEmail().equals(funcionarioDto.getEmail())) {
			this.funcionarioService.buscarPorEmail(funcionarioDto.getEmail())
					.ifPresent(func -> result.addError(new ObjectError("email", "Email já existente.")));
			funcionario.setEmail(funcionarioDto.getEmail());
		}

		funcionario.setQtdeHorasAlmoco(null);
		funcionarioDto.getQtdHorasAlmoco()
				.ifPresent(qtdHorasAlmoco -> funcionario.setQtdeHorasAlmoco(Float.valueOf(qtdHorasAlmoco)));

		funcionario.setQtdeHorasTrabalhoDia(null);
		funcionarioDto.getQtdHorasTrabalhoDia()
				.ifPresent(qtdHorasTrabDia -> funcionario.setQtdeHorasTrabalhoDia(Float.valueOf(qtdHorasTrabDia)));

		funcionario.setValorHora(null);
		funcionarioDto.getValorHora().ifPresent(valorHora -> funcionario.setValorHora(new BigDecimal(valorHora)));

		if (funcionarioDto.getSenha().isPresent()) {
			funcionario.setSenha(PasswordUtils.gerarBCrypt(funcionarioDto.getSenha().get()));
		}
	}

	/**
	 * Retorna um DTO com os dados de um funcionário.
	 * 
	 * @param funcionario
	 * @return FuncionarioDto
	 */
	private FuncionarioDTO converterFuncionarioDto(Funcionario funcionario) {
		FuncionarioDTO funcionarioDto = new FuncionarioDTO();
		funcionarioDto.setId(funcionario.getId());
		funcionarioDto.setEmail(funcionario.getEmail());
		funcionarioDto.setNome(funcionario.getNome());
		funcionario.getQtdeHorasAlmocoOpt().ifPresent(
				qtdHorasAlmoco -> funcionarioDto.setQtdHorasAlmoco(Optional.of(Float.toString(qtdHorasAlmoco))));
		funcionario.getQtdeHorasTrabalhoDiaOpt().ifPresent(
				qtdHorasTrabDia -> funcionarioDto.setQtdHorasTrabalhoDia(Optional.of(Float.toString(qtdHorasTrabDia))));
		funcionario.getValorHoraOpt()
				.ifPresent(valorHora -> funcionarioDto.setValorHora(Optional.of(valorHora.toString())));

		return funcionarioDto;
	}

}