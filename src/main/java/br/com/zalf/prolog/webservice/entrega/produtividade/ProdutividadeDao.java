package br.com.zalf.prolog.webservice.entrega.produtividade;

import java.sql.SQLException;
import java.util.List;

/**
 * Contém os métodos para consultar a remuneração variável
 */
public interface ProdutividadeDao {
	
	/**
	 * Busca a produtividade de um colaborador, respeitando o período e a função designada por ele.
	 * Exclusivo distribuição (ajudante ou motorista).
	 *
	 * @param ano um número inteiro que representa um ano
	 * @param mes um número inteiro que representa um mês
	 * @param cpf cpf do colaborador a ser buscada a remuneração variável (produtividade)
	 * @param salvaLog boolean para indicar se deve ou não salvar o log da consulta
	 * @return lista de {@link ItemProdutividade}
	 * @throws SQLException caso não seja possível realizar a busca
	 */
	List<ItemProdutividade> getProdutividadeByPeriodo (int ano, int mes, Long cpf, boolean salvaLog) throws SQLException;

	/**
	 * Busca a produtividade associada ao colaborador.
	 *
	 * @param codUnidade  código da unidade
	 * @param equipe      equipe do colaborador
	 * @param codFuncao   código da função
	 * @param dataInicial data inicial da busca em milissegundos
	 * @param dataFinal   data fincal da busca em milissegundos
	 * @return um objeto {@link HolderColaboradorProdutividade} que possui a produtividade do colaborador
	 * @throws SQLException caso ocorrer erro no banco
	 */
	List<HolderColaboradorProdutividade> getConsolidadoProdutividade(Long codUnidade,
																	 String equipe,
																	 String codFuncao,
																	 long dataInicial,
																	 long dataFinal) throws SQLException;

	PeriodoProdutividade getPeriodoProdutividade(int ano, int mes, Long codUnidade, Long cpf) throws SQLException;
}
