package br.com.zalf.prolog.webservice.gente.calendario;

import br.com.zalf.prolog.gente.calendario.Evento;

import java.sql.SQLException;
import java.util.List;

/**
 * Calendário contendo os eventos cadastrados no banco de dados
 */
public interface CalendarioDao {
	/**
	 * Busca os eventos visíveis a um usuário
	 * @param cpf a ser buscando os eventos
	 * @param token para verificar se o usuário esta logado
	 * @return uma lista com todos os eventos visíveis 
	 * @throws SQLException caso não consiga acessar a tabela com os eventos
	 */
	public List<Evento> getEventosByCpf(Long cpf) throws SQLException;

}
