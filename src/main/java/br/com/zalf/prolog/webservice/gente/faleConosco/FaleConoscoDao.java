package br.com.zalf.prolog.webservice.gente.faleConosco;

import java.sql.SQLException;
import java.util.List;


/**
 * Contém os métodos para manipular os fale conosco
 */
public interface FaleConoscoDao {

	Long insert(FaleConosco faleConosco, Long codUnidade) throws SQLException;

	FaleConosco getByCod(Long codigo, Long codUnidade) throws Exception;

	List<FaleConosco> getAll(long dataInicial, long dataFinal, int limit, int offset, String cpf,
							 String equipe, Long codUnidade, String status, String categoria) throws Exception;

	List<FaleConosco> getByColaborador(Long cpf, String status) throws Exception;

	boolean insertFeedback(FaleConosco faleConosco, Long codUnidade) throws SQLException;
}