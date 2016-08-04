package br.com.zalf.prolog.webservice.gente.calendario;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import br.com.zalf.prolog.models.AbstractResponse;
import br.com.zalf.prolog.models.Evento;
import br.com.zalf.prolog.models.Response;

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

	public List<Evento> getAll (long dataInicial, long dataFinal, Long codEmpresa, String codUnidade, String equipe, String funcao) throws SQLException{
		try{
			return dao.getAll(dataInicial, dataFinal, codEmpresa, codUnidade, equipe, funcao);
		}catch (SQLException e){
			e.printStackTrace();
			return new ArrayList<Evento>();
		}
	}

	private boolean delete (Long codUnidade, Long codEvento){
		try{
			return dao.delete(codUnidade, codEvento);
		}catch (SQLException e){
			e.printStackTrace();
			return false;
		}
	}
	public AbstractResponse insert (Evento evento, String codUnidade, String codFuncao, String codEquipe){
		try {
			return dao.insert(evento, codUnidade, codFuncao, codEquipe);
		}catch (SQLException e){
			e.printStackTrace();
			return Response.Error("Erro ao inserir o evento");
		}
	}


}
