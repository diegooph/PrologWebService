package br.com.zalf.prolog.webservice.gente.calendario;

import br.com.zalf.prolog.webservice.commons.network.AbstractResponse;
import br.com.zalf.prolog.webservice.commons.network.Response;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Classe CalendarioService responsavel por comunicar-se com a interface DAO
 */
public class CalendarioService {

	private CalendarioDao dao = new CalendarioDaoImpl();
	
	public List<Evento> getEventosByCpf(Long cpf){
		try{
			return dao.getEventosByCpf(cpf);
		}catch(SQLException e){
			e.printStackTrace();
			return null;
		}
	}

	public List<Evento> getAll (long dataInicial, long dataFinal, Long codEmpresa, String codUnidade, String nomeEquipe, String codFuncao) throws SQLException{
		try{
			return dao.getAll(dataInicial, dataFinal, codEmpresa, codUnidade, nomeEquipe, codFuncao);
		}catch (SQLException e){
			e.printStackTrace();
			return new ArrayList<Evento>();
		}
	}

	public boolean delete (Long codUnidade, Long codEvento){
		try{
			return dao.delete(codUnidade, codEvento);
		}catch (SQLException e){
			e.printStackTrace();
			return false;
		}
	}
	public AbstractResponse insert (Evento evento, String codUnidade, String codFuncao, String nomeEquipe){
		try {
			return dao.insert(evento, codUnidade, codFuncao, nomeEquipe);
		}catch (SQLException e){
			e.printStackTrace();
			return Response.Error("Erro ao inserir o evento");
		}
	}

	public boolean update (Evento evento, String codUnidade, String codFuncao, String nomeEquipe){
		try{
			return dao.update(evento, codUnidade, codFuncao, nomeEquipe);
		}catch (SQLException e){
			e.printStackTrace();
			return false;
		}
	}


}
