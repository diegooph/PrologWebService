package br.com.zalf.prolog.webservice.gente.ranking;

import br.com.zalf.prolog.gente.ranking.ItemPosicao;

import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;

/**
 * Classe RankingService responsavel por comunicar-se com a interface DAO
 */
public class RankingService {
	
	private RankingDao dao = new RankingDaoImpl();
	
	public List<ItemPosicao> getRanking (LocalDate dataInicial, LocalDate dataFinal, String equipe,
                                         Long codUnidade) throws SQLException{
		try{
			return dao.getRanking(dataInicial, dataFinal, equipe, codUnidade);
		}catch(SQLException e){
			e.printStackTrace();
			return null;
		}
	}

}
