package br.com.empresa.oprojeto.webservice.services;

import java.sql.SQLException;
import java.time.LocalDate;

import br.com.empresa.oprojeto.models.indicador.IndicadorHolder;
import br.com.empresa.oprojeto.webservice.dao.IndicadorDaoImpl;

public class IndicadorService {
	private IndicadorDaoImpl dao = new IndicadorDaoImpl();
	
	public IndicadorHolder getIndicadoresByPeriodo(LocalDate dataInicial, 
			LocalDate dataFinal, long cpf) {
		try {
			return dao.getIndicadoresByPeriodo(dataInicial, dataFinal, cpf);
		} catch (SQLException e) {
			e.printStackTrace();
			return null;
		}
	}
}
