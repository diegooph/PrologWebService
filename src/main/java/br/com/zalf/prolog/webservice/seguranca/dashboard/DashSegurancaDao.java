package br.com.zalf.prolog.webservice.seguranca.dashboard;

import java.sql.SQLException;
import java.time.LocalDate;

/**
 * Created by didi on 9/15/16.
 */
public interface DashSegurancaDao {

	/**
	 * busca uma dashboard de segurança
	 * @param dataInicial uma data
	 * @param dataFinal uma data
	 * @param codUnidade código da unidade
	 * @param equipe nome da equipe
	 * @return um Dashboard
	 * @throws SQLException se ocorrer erro no banco de dados
	 */
	DashSeguranca getDashSeguranca(LocalDate dataInicial, LocalDate dataFinal, Long codUnidade, String equipe) throws SQLException;

}
