package br.com.zalf.prolog.webservice.gente.solicitacaoFolga;

import br.com.zalf.prolog.webservice.commons.network.AbstractResponse;

import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;

/**
 * Contém os métodos para manipular as solicitações de folga
 */
public interface SolicitacaoFolgaDao {

	AbstractResponse insert(SolicitacaoFolga solicitacao) throws SQLException;

	boolean update(SolicitacaoFolga solicitacaoFolga) throws SQLException;

	boolean delete(Long codigo) throws SQLException;

	List<SolicitacaoFolga> getAll(LocalDate dataInicial, LocalDate dataFinal, Long codUnidade, String codEquipe,
								  String status, String cpfColaborador) throws SQLException;

	List<SolicitacaoFolga> getByColaborador(Long cpf) throws SQLException;
}