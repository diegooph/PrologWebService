package br.com.zalf.prolog.webservice.gente.ranking;

import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;

import br.com.zalf.prolog.models.ranking.ItemPosicao;

public class RankingService {
	
private RankingDaoImpl dao = new RankingDaoImpl();
	
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
