package br.com.empresa.oprojeto.webservice.dao.interfaces;

import java.sql.Date;
import java.sql.SQLException;
import java.util.List;

import br.com.empresa.oprojeto.models.indicadores.Indicador;

public interface IndicadorDao {
	List<Indicador> getJornadaByPeriodo(long cpf, Date dataInicial, Date dataFinal) 
			throws SQLException;
	List<Indicador> getTempoInternoByPeriod(long cpf, Date dataInicial, Date dataFinal) 
			throws SQLException;
	List<Indicador> getTempoRotaByPeriod(long cpf, Date dataInicial, Date dataFinal) 
			throws SQLException;
	List<Indicador> getDevCxByPeriod(long cpf, Date dataInicial, Date dataFinal) 
			throws SQLException;
	List<Indicador> getDevNfByPeriod(long cpf, Date dataInicial, Date dataFinal) 
			throws SQLException;
	List<Indicador> getTrackingByPeriod(long cpf, Date dataInicial, Date dataFinal) 
			throws SQLException;
}
