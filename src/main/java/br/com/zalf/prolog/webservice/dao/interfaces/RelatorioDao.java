package br.com.zalf.prolog.webservice.dao.interfaces;

import java.sql.SQLException;
import java.time.LocalDate;

import br.com.zalf.prolog.models.relatorios.ConsolidadoHolder;

public interface RelatorioDao {

	public ConsolidadoHolder getIndicadoresEquipeByPeriodo(LocalDate dataInicial, LocalDate dataFinal, String equipe,
			int codUnidade, Long cpf, String token) throws SQLException;
	
	public ConsolidadoHolder getIndicadoresUnidadeByPeriodo(LocalDate dataInicial, LocalDate dataFinal,
			int codUnidade, Long cpf, String token) throws SQLException;
	
}
