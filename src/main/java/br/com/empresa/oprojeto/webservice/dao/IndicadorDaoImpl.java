package br.com.empresa.oprojeto.webservice.dao;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import br.com.empresa.oprojeto.models.indicadores.DevolucaoCaixa;
import br.com.empresa.oprojeto.models.indicadores.Indicador;
import br.com.empresa.oprojeto.webservice.dao.interfaces.IndicadorDao;

public class IndicadorDaoImpl extends DataBaseConnection implements IndicadorDao {
	
	private static final int COD_DEVCX = 1;
	private static final int COD_DEVNF = 2;
	private static final int COD_JORNADA = 3;
	private static final int COD_TEMPO_INTERNO = 4;
	private static final int COD_TEMPO_EM_ROTA = 5;
	private static final int COD_TEMPO_LARGADA = 6;
	private static final int COD_TRACKING = 7;
	

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
				Indicador c = createDevCx(rSet);
				lIndicador.add(c);
			}
		}

finally{
	
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
	
	private Indicador createDevCx (ResultSet rSet) throws SQLException{
		Indicador dev = new DevolucaoCaixa();
			
		
		return dev;		
	}
	
	private void getMeta (Indicador i, int cod_meta){
		
	}
	
	
	
	
	
	
	
	
	
	
	
	
}
