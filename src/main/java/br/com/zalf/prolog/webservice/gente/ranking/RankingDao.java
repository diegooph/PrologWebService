package br.com.zalf.prolog.webservice.gente.ranking;

import br.com.zalf.prolog.gente.ranking.ItemPosicao;

import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;

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
	 * @return lista de ItemPosicao, ja rankeada
	 * @throws SQLException caso não seja possível realizar a busca
	 */
	List<ItemPosicao> getRanking (LocalDate dataInicial, LocalDate dataFinal, String equipe,
								  Long codUnidade) throws SQLException;
	
}
