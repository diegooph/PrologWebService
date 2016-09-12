package br.com.zalf.prolog.webservice.entrega.indicadorOlder;

import br.com.zalf.prolog.entrega.indicador.older.IndicadorHolder;

import br.com.zalf.prolog.models.indicador.IndicadorHolder;

import java.sql.SQLException;
import java.time.LocalDate;

public class IndicadorService {
	private IndicadorDaoImpl dao = new IndicadorDaoImpl();
	
	public IndicadorHolder getIndicadoresByPeriodo(LocalDate dataInicial,
												   LocalDate dataFinal, Long cpf, String token) {
		try {
			return dao.getIndicadoresByPeriodo(dataInicial, dataFinal, cpf, token);
		} catch (SQLException e) {
			e.printStackTrace();
			return null;
		}
	}
}
