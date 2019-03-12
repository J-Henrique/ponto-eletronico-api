package com.jhbb.pontointeligente.api.interfaces.services;

import java.util.Optional;

import com.jhbb.pontointeligente.api.entities.Empresa;

public interface EmpresaServiceInterface {

	/**
	 * Retorna uma empresa por um CNPJ
	 * @param cnpj
	 * @return Optional<Empresa>
	 */
	Optional<Empresa> buscarPorCnpj(String cnpj);
	
	/**
	 * Cadastra uma nova empresa
	 * @param empresa
	 * @return Empresa
	 */
	Empresa persistir(Empresa empresa);
}
