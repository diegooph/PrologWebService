package br.com.empresa.oprojeto.webservice.dao;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import br.com.empresa.oprojeto.models.Colaborador;
import br.com.empresa.oprojeto.models.indicadores.DevolucaoCaixa;
import br.com.empresa.oprojeto.models.indicadores.Indicador;
import br.com.empresa.oprojeto.webservice.dao.interfaces.IndicadorDao;

public class IndicadorDaoImpl extends DataBaseConnection implements IndicadorDao {

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
		Connection conn = null;
		PreparedStatement stmt = null;
		ResultSet rSet = null;
		List<Indicador> lIndicador = new ArrayList<>();
		try{
			conn = getConnection();
			stmt = conn.prepareStatement("SELECT * FROM COLABORADOR");
			rSet = stmt.executeQuery();
			while (rSet.next()) {
				DevolucaoCaixa c = createDevCx(rSet);
				lIndicador.add(c);
			}
		}





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
	
	private DevolucaoCaixa createDevCx (ResultSet rSet) throws SQLException{
		DevolucaoCaixa dev = new DevolucaoCaixa();
		
		
		
		
		return dev;		
	}
	
	
	
	
	
	
	
	
	
	
	
	
}
