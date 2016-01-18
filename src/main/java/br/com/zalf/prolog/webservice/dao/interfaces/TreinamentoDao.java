package br.com.zalf.prolog.webservice.dao.interfaces;

import java.sql.SQLException;
import java.util.List;

import br.com.zalf.prolog.models.treinamento.Treinamento;
import br.com.zalf.prolog.models.treinamento.TreinamentoColaborador;


public interface TreinamentoDao {
	List<Treinamento> getNaoVistosColaborador(Long cpf, String token) throws SQLException;
	List<Treinamento> getVistosColaborador(Long cpf, String token) throws SQLException;
	boolean marcarTreinamentoComoVisto(TreinamentoColaborador treinamentoColaborador) throws SQLException;
}
