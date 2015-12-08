package br.com.empresa.oprojeto.webservice.dao;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import br.com.empresa.oprojeto.models.indicadores.Indicador;
import br.com.empresa.oprojeto.webservice.dao.interfaces.IndicadorDao;

public class IndicadorDaoImpl extends DataBaseConnection implements IndicadorDao {
//Códigos das metas no BD
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
			stmt = conn.prepareStatement("SELECT M.DATA, SUM(M.CXCARREG) AS CAIXAS_CARREGADAS,"
					+ "SUM(M.CXENTREG) AS CAIXAS_ENTREGUES,"
					+ "(SUM(M.CXCARREG)-SUM(M.CXENTREG)) AS CAIXAS_DEVOLVIDAS,"
					+ "MU.VALOR AS META "
					+ "FROM MAPA_COLABORADOR MC JOIN COLABORADOR C "
					+ "ON C.MATRICULA_AMBEV = MC.COD_AMBEV "
					+ "JOIN MAPA M ON M.MAPA = MC.MAPA "
					+ "JOIN META_UNIDADE MU ON MU.COD_UNIDADE = M.COD_UNIDADE "
					+ "WHERE C.CPF = ? AND MU.COD_META=1 "
					+ "AND M.DATA BETWEEN ? AND  ? "
					+ "GROUP BY M.DATA, MU.VALOR "
					+ "ORDER BY M.DATA ");
			stmt.setLong(1, cpf);
			stmt.setDate(2, dataInicial);
			stmt.setDate(3, dataFinal);
			rSet = stmt.executeQuery();
			while (rSet.next()) {
				//DevolucaoCaixa dev = createDevCx(rSet);
				//lIndicador.add(dev);
			}
		}finally{
			closeConnection(conn, stmt, rSet);
		}
		return lIndicador;
	}

	//private DevolucaoCaixa createDevCx(ResultSet rSet) throws SQLException{
	//	DevolucaoCaixa dev = new DevolucaoCaixa();
	//	dev.setCxCarregadas(rSet.getDouble("CAIXAS_CARREGADAS"));
	////	dev.setCxDevolvidas(rSet.getDouble("CAIXAS_DEVOLVIDAS"));
	//	dev.setCxEntregues(rSet.getDouble("CAIXAS_ENTREGUES"));
	//	dev.setMeta(Double.parseDouble(rSet.getString("META")));
	//	System.out.println(rSet.getDouble("CAIXAS_CARREGADAS"));
	//	return dev;		
	//}

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

