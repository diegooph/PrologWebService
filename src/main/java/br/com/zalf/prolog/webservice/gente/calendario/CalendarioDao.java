package br.com.zalf.prolog.webservice.gente.calendario;

import br.com.zalf.prolog.webservice.commons.network.AbstractResponse;

import java.sql.SQLException;
import java.util.List;

/**
 * Calendário contendo os eventos cadastrados no banco de dados
 */
public interface CalendarioDao {

	List<Evento> getEventosByCpf(Long cpf) throws SQLException;

	List<Evento> getAll (long dataInicial, long dataFinal, Long codEmpresa, String codUnidade,
						 String codEquipe, String codFuncao) throws SQLException;

	boolean delete (Long codUnidade, Long codEvento) throws SQLException;

	AbstractResponse insert (Evento evento, String codUnidade, String codFuncao, String codEquipe) throws SQLException;
	
	boolean update (Evento evento, String codUnidade, String codFuncao, String codEquipe) throws SQLException;

}
