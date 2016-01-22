package br.com.zalf.prolog.webservice.dao.interfaces;

import java.sql.SQLException;
import java.time.LocalDate;

import br.com.zalf.prolog.models.indicador.IndicadorHolder;

public interface RelatorioDao {

	IndicadorHolder getIndicadoresEquipeByPeriodo(LocalDate dataInicial, LocalDate dataFinal,
			String equipe,Long cpf, String token) throws SQLException;
	
	IndicadorHolder getIndicadoresUnidadeByPeriodo(LocalDate dataInicial, LocalDate dataFinal,
			int codUnidade, Long cpf, String token) throws SQLException;
	
}
