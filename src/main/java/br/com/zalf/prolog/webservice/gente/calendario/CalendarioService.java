package br.com.zalf.prolog.webservice.gente.calendario;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import br.com.zalf.prolog.models.Evento;

public class CalendarioService {

	private CalendarioDaoImpl dao = new CalendarioDaoImpl();
	
	public List<Evento> getEventosByCpf(Long cpf){
		try{
			return dao.getEventosByCpf(cpf);
		}catch(SQLException e){
			e.printStackTrace();
			return null;
		}
	}

	public List<Evento> getAll (long dataInicial, long dataFinal, int limit, int offset,
								Long codEmpresa, String codUnidade, String equipe, String funcao) throws SQLException{
		try{
			return dao.getAll(dataInicial, dataFinal, limit, offset, codEmpresa, codUnidade, equipe, funcao);
		}catch (SQLException e){
			e.printStackTrace();
			return new ArrayList<Evento>();
		}
	}

}
