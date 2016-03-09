package br.com.zalf.prolog.webservice.services;

import java.sql.SQLException;
import java.util.List;

import br.com.zalf.prolog.models.Request;
import br.com.zalf.prolog.models.frota.ManutencaoHolder;
import br.com.zalf.prolog.webservice.dao.FrotaDaoImpl;

public class FrotaService {
	private FrotaDaoImpl dao = new FrotaDaoImpl();

	public List<ManutencaoHolder> getManutencaoHolder(Long cpf, String token, Long codUnidade, int limit, long offset, boolean isAbertos) {
System.out.println(cpf);
System.out.println(token);
System.out.println(codUnidade);
System.out.println(limit);
System.out.println(offset);
System.out.println(isAbertos);
		try {
			return dao.getManutencaoHolder(cpf, token, codUnidade, limit, offset, isAbertos);					
		} catch (SQLException e) {
			e.printStackTrace();
			return null;
		}
	}

	public boolean consertaItem (Request<?> request){
		try{
			return dao.consertaItem(request);
		} catch(SQLException e){
			e.printStackTrace();
			return false;
		}
	}

}
