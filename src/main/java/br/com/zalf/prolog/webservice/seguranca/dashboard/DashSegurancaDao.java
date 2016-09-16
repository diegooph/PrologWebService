package br.com.zalf.prolog.webservice.seguranca.dashboard;

import br.com.zalf.prolog.seguranca.dashboard.DashSeguranca;

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
	 * @throws SQLException
	 */
	DashSeguranca getDashSeguranca(LocalDate dataInicial, LocalDate dataFinal, Long codUnidade, String equipe) throws SQLException;

}
