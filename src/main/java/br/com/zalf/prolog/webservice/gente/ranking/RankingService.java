package br.com.zalf.prolog.webservice.gente.ranking;

import br.com.zalf.prolog.webservice.commons.util.Log;

import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;

/**
 * Classe RankingService responsavel por comunicar-se com a interface DAO
 */
public class RankingService {
	
	private RankingDao dao = new RankingDaoImpl();
	private static final String TAG = RankingService.class.getSimpleName();
	
	public List<ItemPosicao> getRanking (LocalDate dataInicial, LocalDate dataFinal, String equipe,
                                         Long codUnidade) throws SQLException{
		try{
			return dao.getRanking(dataInicial, dataFinal, equipe, codUnidade);
		}catch(SQLException e){
			Log.e(TAG, String.format("Erro ao buscar o ranking. \n" +
					"codUnidade: %d \n" +
					"equipe: %s \n" +
					"dataInicial: %s \n" +
					"dataFinal: %s", codUnidade, equipe, dataInicial.toString(), dataFinal.toString()), e);
			return null;
		}
	}

}
