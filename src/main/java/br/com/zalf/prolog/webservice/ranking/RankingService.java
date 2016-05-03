package br.com.zalf.prolog.webservice.services;

import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;

import br.com.zalf.prolog.models.ranking.ItemPosicao;
import br.com.zalf.prolog.webservice.dao.RankingDaoImpl;

public class RankingService {
	
private RankingDaoImpl dao = new RankingDaoImpl();
	
	public List<ItemPosicao> getRanking (LocalDate dataInicial, LocalDate dataFinal, String equipe,
			Long codUnidade, Long cpf, String token) throws SQLException{
		try{
			return dao.getRanking(dataInicial, dataFinal, equipe, codUnidade, cpf, token);
		}catch(SQLException e){
			e.printStackTrace();
			return null;
		}
	}

}
