package br.com.zalf.oprojeto.webservice.dao.interfaces;

import java.sql.SQLException;
import java.time.LocalDate;

import br.com.empresa.oprojeto.models.indicador.IndicadorHolder;


public interface IndicadorDao {
	
	
IndicadorHolder getIndicadoresByPeriodo(LocalDate dataInicial, LocalDate dataFinal,
								long cpf) throws SQLException;

			
}
