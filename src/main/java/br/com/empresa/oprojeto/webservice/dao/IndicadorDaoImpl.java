package br.com.empresa.oprojeto.webservice.dao;

import java.sql.Date;
import java.sql.SQLException;
import java.util.List;

import br.com.empresa.oprojeto.models.indicadores.Indicador;
import br.com.empresa.oprojeto.webservice.dao.interfaces.IndicadorDao;

public class IndicadorDaoImpl extends ConnectionFactory implements IndicadorDao {

	@Override
	public List<Indicador> getJornadaByPeriodo(long cpf, Date dataInicial, 
			Date dataFinal) throws SQLException {
		return null;
	}

	@Override
	public List<Indicador> getTempoInternoByPeriod(long cpf, Date dataInicial, 
			Date dataFinal) throws SQLException {
		return null;
	}

	@Override
	public List<Indicador> getTempoRotaByPeriod(long cpf, Date dataInicial, 
			Date dataFinal) throws SQLException {
		return null;
	}

	@Override
	public List<Indicador> getDevCxByPeriod(long cpf, Date dataInicial, 
			Date dataFinal) throws SQLException {
		return null;
	}

	@Override
	public List<Indicador> getDevNfByPeriod(long cpf, Date dataInicial, 
			Date dataFinal) throws SQLException {
		return null;
	}

	@Override
	public List<Indicador> getTrackingByPeriod(long cpf, Date dataInicial, 
			Date dataFinal) throws SQLException {
		return null;
	}	
}
