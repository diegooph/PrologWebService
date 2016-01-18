package br.com.zalf.prolog.webservice.dao.interfaces;

import java.sql.SQLException;
import java.time.LocalDate;

import br.com.zalf.prolog.models.indicador.IndicadorHolder;


public interface IndicadorDao {
	
	
IndicadorHolder getIndicadoresByPeriodo(LocalDate dataInicial, LocalDate dataFinal,
								Long cpf, String token) throws SQLException;

			
}
