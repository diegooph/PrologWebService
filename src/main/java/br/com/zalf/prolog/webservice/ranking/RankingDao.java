package br.com.zalf.prolog.webservice.dao.interfaces;

import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;

import br.com.zalf.prolog.models.ranking.ItemPosicao;
/**
 * Contém os métodos para consulta do ranking
 */
public interface RankingDao {
	/**
	 * Busca do ranking geral, respeitando o período, equipe e unidade selecionados, exclusivo distribuição (ajudante ou motorista)
	 * @param dataInicial uma data
	 * @param dataFinal uma data
	 * @param equipe uma equipe
	 * @param codUnidade um código
	 * @param cpf cpf do solicitante
	 * @param token para verificar se o solicitante esta devidamente logado
	 * @return lista de ItemPosicao, ja rankeada
	 * @throws SQLException caso não seja possível realizar a busca
	 */
	public List<ItemPosicao> getRanking (LocalDate dataInicial, LocalDate dataFinal, String equipe,
			Long codUnidade, Long cpf, String token) throws SQLException;
	
}
