package br.com.zalf.prolog.webservice.gente.calendario;

import br.com.zalf.prolog.commons.network.AbstractResponse;
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
	 * @return uma lista com todos os eventos visíveis 
	 * @throws SQLException caso não consiga acessar a tabela com os eventos
	 */
	List<Evento> getEventosByCpf(Long cpf) throws SQLException;

	/**
	 * busca todos os eventos de uma data até outra
	 * @param dataInicial data inicial
	 * @param dataFinal data final
	 * @param codEmpresa código da empresa
	 * @param codUnidade código da unidade
	 * @param equipe equipe
	 * @param funcao função
	 * @return uma lista de eventos
	 * @throws SQLException
	 */
	List<Evento> getAll (long dataInicial, long dataFinal, Long codEmpresa, String codUnidade,
						 String equipe, String funcao) throws SQLException;

	/**
	 * deleta um evento
	 * @param codUnidade código da unidade
	 * @param codEvento código do evento
	 * @return valor da operação
	 * @throws SQLException
	 */
	boolean delete (Long codUnidade, Long codEvento) throws SQLException;

	/**
	 * insere um novo evento
	 * @param evento um evento
	 * @param codUnidade código da unidade
	 * @param codFuncao código da função
	 * @param codEquipe código da equipe
	 * @return uma resposta abstrata, contendo o valor da operação
	 * @throws SQLException
	 */
	AbstractResponse insert (Evento evento, String codUnidade, String codFuncao, String codEquipe) throws SQLException;

	/**
	 * atualiza um evento
	 * @param evento um evento
	 * @param codUnidade código da unidade
	 * @param codFuncao código da função
	 * @param codEquipe código da equipe
	 * @return o valor da operação
	 * @throws SQLException
	 */
	boolean update (Evento evento, String codUnidade, String codFuncao, String codEquipe) throws SQLException;

}
