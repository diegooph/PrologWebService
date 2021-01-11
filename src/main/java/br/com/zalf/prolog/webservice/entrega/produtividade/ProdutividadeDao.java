package br.com.zalf.prolog.webservice.entrega.produtividade;

import java.sql.SQLException;
import java.util.List;

/**
 * Contém os métodos para consultar a remuneração variável
 */
public interface ProdutividadeDao {

	List<ItemProdutividade> getProdutividadeByPeriodo (int ano, int mes, Long cpf, boolean salvaLog) throws SQLException;

	List<HolderColaboradorProdutividade> getConsolidadoProdutividade(Long codUnidade,
																	 String equipe,
																	 String codFuncao,
																	 long dataInicial,
																	 long dataFinal) throws SQLException;

	PeriodoProdutividade getPeriodoProdutividade(int ano, int mes, Long codUnidade, Long cpf) throws SQLException;
}
