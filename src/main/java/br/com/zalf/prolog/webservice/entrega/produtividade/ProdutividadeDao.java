package br.com.zalf.prolog.webservice.entrega.produtividade;

import br.com.zalf.prolog.entrega.produtividade.ItemProdutividade;

import java.sql.SQLException;
import java.util.List;

/**
 * Contém os métodos para consultar a remuneração variável
 */
public interface ProdutividadeDao {
	
	/**
	 * Busca a produtividade de um colaborador, respeitando o período e a função designada por ele.
	 * Exclusivo distribuição (ajudante ou motorista)
	 * @param ano um ano
	 * @param mes um mes
	 * @param cpf cpf do colaborador a ser buscada a remuneração variável (produtividade)
	 * @return lista de ItemProdutividade
	 * @throws SQLException caso não seja possível realizar a busca
	 */
	List<ItemProdutividade> getProdutividadeByPeriodo (int ano, int mes, Long cpf) throws SQLException;

}
