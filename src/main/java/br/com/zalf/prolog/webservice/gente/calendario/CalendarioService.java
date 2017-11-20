package br.com.zalf.prolog.webservice.gente.calendario;

import br.com.zalf.prolog.webservice.commons.network.AbstractResponse;
import br.com.zalf.prolog.webservice.commons.network.Response;
import br.com.zalf.prolog.webservice.commons.util.Log;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Classe CalendarioService responsavel por comunicar-se com a interface DAO
 */
public class CalendarioService {

	private CalendarioDao dao = new CalendarioDaoImpl();
	private static final String TAG = CalendarioService.class.getSimpleName();
	
	public List<Evento> getEventosByCpf(Long cpf){
		try{
			return dao.getEventosByCpf(cpf);
		}catch(SQLException e){
			Log.e(TAG, String.format("Erro ao buscar os eventos de um colaborador. \n" +
					"cpf: %s", cpf), e);
			return null;
		}
	}

	public List<Evento> getAll (long dataInicial, long dataFinal, Long codEmpresa, String codUnidade, String nomeEquipe, String codFuncao) throws SQLException{
		try{
			return dao.getAll(dataInicial, dataFinal, codEmpresa, codUnidade, nomeEquipe, codFuncao);
		}catch (SQLException e){
			Log.e(TAG, String.format("Erro ao buscar os eventos. \n" +
					"codEmpresa: %d \n" +
					"codUnidade: %s \n" +
					"nomeEquipe: %s \n" +
					"codFuncao: %s \n" +
					"dataInicial: %s \n" +
					"dataFinal: %s", codEmpresa, codUnidade, nomeEquipe, codFuncao, dataInicial, dataFinal), e);
			return new ArrayList<Evento>();
		}
	}

	public boolean delete (Long codUnidade, Long codEvento){
		try{
			return dao.delete(codUnidade, codEvento);
		}catch (SQLException e){
			Log.e(TAG, String.format("Erro ao deletar um evento. \n" +
					"codUnidade: %d \n" +
					"codEvento: %d", codUnidade, codEvento), e);
			return false;
		}
	}
	public AbstractResponse insert (Evento evento, String codUnidade, String codFuncao, String nomeEquipe){
		try {
			return dao.insert(evento, codUnidade, codFuncao, nomeEquipe);
		}catch (SQLException e){
			Log.e(TAG, String.format("Erro ao inserir um evento. \n" +
					"codUnidade: %s \n" +
					"codFuncao: %s \n" +
					"nomeEquipe: %s", codUnidade, codFuncao, nomeEquipe), e);
			return Response.error("Erro ao inserir o evento");
		}
	}

	public boolean update (Evento evento, String codUnidade, String codFuncao, String nomeEquipe){
		try{
			return dao.update(evento, codUnidade, codFuncao, nomeEquipe);
		}catch (SQLException e){
			Log.e(TAG, String.format("Erro ao atualizar o evento. \n" +
					"codUnidade: %s \n" +
					"codFuncao: %s \n" +
					"nomeEquipe: %s", codUnidade, codFuncao, nomeEquipe), e);
			return false;
		}
	}


}
