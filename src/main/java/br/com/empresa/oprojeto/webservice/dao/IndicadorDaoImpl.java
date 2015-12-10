package br.com.empresa.oprojeto.webservice.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;

import br.com.empresa.oprojeto.models.indicador.DevolucaoCxHolder;
import br.com.empresa.oprojeto.models.indicador.IndicadorHolder;
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
	
	private static final String BUSCA_INDICADORES = "SELECT M.DATA, M.CXCARREG, "
			+ "M.CXENTREG, M.QTNFCARREGADAS, M.QTNFENTREGUES, M.HRSAI, M.HRENTR,"
			+ " M.TEMPOINTERNO, M.HRMATINAL FROM MAPA_COLABORADOR MC JOIN "
			+ "COLABORADOR C ON C.COD_UNIDADE = MC.COD_UNIDADE AND MC.COD_AMBEV "
			+ "= C.MATRICULA_AMBEV JOIN MAPA M ON M.MAPA = MC.MAPA WHERE C.CPF = "
			+ "? AND DATA BETWEEN ? AND ? ORDER BY M.DATA";
	
			private IndicadorHolder indicadorHolder = new IndicadorHolder();

	@Override
	public IndicadorHolder getIndicadoresByPeriodo(LocalDate dataInicial, LocalDate dataFinal, long cpf)
			throws SQLException {

		Connection conn = null;
		PreparedStatement stmt = null;
		ResultSet rSet = null;
		/*
		 * 
		try {
			conn = getConnection();
			stmt = conn.prepareStatement(BUSCA_INDICADORES);
			stmt.setLong(1, cpf);
			stmt.setDate(2, DateUtil.toSqlDate(dataInicial));
			stmt.setDate(3, DateUtil.toSqlDate(dataFinal));
			stmt.setLong(1, c.getCpf());
			rSet = stmt.executeQuery();
			
			
			}	
		
		
		*/
		
		
		
		return null;
	}
	
	
	
	public void getDevCxByPeriodo (ResultSet rSet){
		DevolucaoCxHolder devCaixa = new DevolucaoCxHolder();

		indicadorHolder.setDevCaixa(devCaixa);
	
	}
	
	
	
	
	
	
	
	

	}

