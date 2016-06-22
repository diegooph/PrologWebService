package br.com.zalf.prolog.webservice.entrega.produtividade;

import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;

import br.com.zalf.prolog.models.produtividade.ItemProdutividade;
/**
 * Contém os métodos para consultar a remuneração variável
 */
public interface ProdutividadeDao {
	
	/**
	 * Busca a produtividade de um colaborador, respeitando o período e a função designada por ele.
	 * Exclusivo distribuição (ajudante ou motorista)
	 * @param dataInicial uma data
	 * @param dataFinal uma data
	 * @param cpf cpf do colaborador a ser buscada a remuneração variável (produtividade)
	 * @param token para verificar se o solicitante esta devidamente logado
	 * @return lista de ItemProdutividade
	 * @throws SQLException caso não seja possível realizar a busca
	 */
	List<ItemProdutividade> getProdutividadeByPeriodo (LocalDate dataInicial, LocalDate dataFinal,
			Long cpf, String token) throws SQLException;

}
