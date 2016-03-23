package br.com.zalf.prolog.webservice.dao;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import br.com.zalf.prolog.models.Request;
import br.com.zalf.prolog.models.SolicitacaoFolga;
import br.com.zalf.prolog.models.util.DateUtils;
import br.com.zalf.prolog.webservice.DatabaseConnection;
import br.com.zalf.prolog.webservice.dao.interfaces.SolicitacaoFolgaDao;

public class SolicitacaoFolgaDaoImpl extends DatabaseConnection implements SolicitacaoFolgaDao {
	
		
	@Override
	public boolean insert(SolicitacaoFolga s) throws SQLException {
		Connection conn = null;
		PreparedStatement stmt = null;
		try {
			conn = getConnection();
			stmt = conn.prepareStatement("INSERT INTO SOLICITACAO_FOLGA ( "
					+ "CPF_COLABORADOR, DATA_SOLICITACAO, DATA_FOLGA, "
					+ "MOTIVO_FOLGA, STATUS, PERIODO) VALUES (?, ?, ?, ?, ?, ?);");
			
			stmt.setLong(1, s.getCpfColaborador());
			stmt.setDate(2, new Date(System.currentTimeMillis()));
			stmt.setDate(3, DateUtils.toSqlDate(s.getDataFolga()));
			stmt.setString(4, s.getMotivoFolga());
			stmt.setString(5, SolicitacaoFolga.STATUS_PENDENTE);
			stmt.setString(6, s.getPeriodo());
			
			int count = stmt.executeUpdate();
			if(count == 0){
				throw new SQLException("Erro ao inserir a solicitação de folga");
			}	
		}
		finally {
			closeConnection(conn, stmt, null);
		}		
		
		return true;
	}

	@Override
	public boolean update(Request<SolicitacaoFolga> object) throws SQLException {
		throw new UnsupportedOperationException("Operation not supported yet");
	}

	@Override
	public boolean delete(Request<SolicitacaoFolga> object) throws SQLException {
		throw new UnsupportedOperationException("Operation not supported yet");
	}

	@Override
	public SolicitacaoFolga getByCod(Request<?> request) throws SQLException {
		throw new UnsupportedOperationException("Operation not supported yet");
	}

	
	public List<SolicitacaoFolga> getAll(LocalDate dataInicial, LocalDate dataFinal, Long codUnidade, String codEquipe, String status, Long cpfColaborador) throws SQLException {
		List<SolicitacaoFolga> list = new ArrayList<>();
		
		Connection conn = null;
		PreparedStatement stmt = null;
		ResultSet rSet = null;
		try {
			conn = getConnection();
			String query = "SELECT SF.*, C.NOME FROM SOLICITACAO_FOLGA SF "
					+ "JOIN COLABORADOR C ON C.CPF = SF.CPF_COLABORADOR "
					+ "WHERE SF.DATA_SOLICITACAO BETWEEN ? AND ? "
					+ "AND C.COD_UNIDADE = ? "
					+ "AND C.COD_EQUIPE::TEXT LIKE ? "
					+ "AND SF.STATUS LIKE ? "
					+ "AND SF.CPF_COLABORADOR::TEXT LIKE ?"
					+ "ORDER BY SF.DATA_SOLICITACAO";
						
			
			stmt = conn.prepareStatement(query);
			stmt.setDate(1, DateUtils.toSqlDate(dataInicial));
			stmt.setDate(2, DateUtils.toSqlDate(dataFinal));
			stmt.setLong(3, codUnidade);
			stmt.setString(4, codEquipe);
			stmt.setString(5, status);
			if(cpfColaborador != null){
			stmt.setString(6, String.valueOf(cpfColaborador));
			}else{
				stmt.setString(6, "%");
			}
			rSet = stmt.executeQuery();
			while(rSet.next()){
				list.add(createSolicitacaoFolga(rSet));
			}				
		}
		finally {
			closeConnection(conn, stmt, null);
		}
		System.out.println(list);
		return list;
	}
	
	@Override
	public List<SolicitacaoFolga> getByColaborador(Long cpf, String token) throws SQLException {
		List<SolicitacaoFolga> list  = new ArrayList<>();
		Connection conn = null;
		PreparedStatement stmt = null;
		ResultSet rSet = null;
		try {
			conn = getConnection();
			stmt = conn.prepareStatement("SELECT SF.CODIGO, SF.CPF_COLABORADOR, "
					+ "SF.CPF_FEEDBACK, SF.DATA_FEEDBACK, SF.DATA_FOLGA, "
					+ "SF.DATA_SOLICITACAO, SF.MOTIVO_FOLGA, "
					+ "SF.JUSTIFICATIVA_FEEDBACK, SF.PERIODO, SF.STATUS, C.NOME "
					+ "FROM SOLICITACAO_FOLGA SF JOIN COLABORADOR C ON "
					+ "? = C.CPF JOIN TOKEN_AUTENTICACAO TA ON "
					+ "? = TA.CPF_COLABORADOR AND ? = TA.TOKEN WHERE "
					+ "SF.CPF_COLABORADOR = ?;");
			stmt.setLong(1, cpf);
			stmt.setLong(2, cpf);
			stmt.setString(3, token);
			stmt.setLong(4, cpf);
			rSet = stmt.executeQuery();
			while (rSet.next()) {
				SolicitacaoFolga solicitacaoFolga = createSolicitacaoFolga(rSet);
				list.add(solicitacaoFolga);
			}
		} finally {
			closeConnection(conn, stmt, rSet);
		}
		return list;
	}

	private SolicitacaoFolga createSolicitacaoFolga(ResultSet rSet) throws SQLException {
		SolicitacaoFolga solicitacaoFolga = new SolicitacaoFolga();
		solicitacaoFolga.setCodigo(rSet.getLong("CODIGO"));
		solicitacaoFolga.setCpfColaborador(rSet.getLong("CPF_COLABORADOR"));
		solicitacaoFolga.setCpfFeedback(rSet.getLong("CPF_FEEDBACK"));
		solicitacaoFolga.setDataFeedback(rSet.getDate("DATA_FEEDBACK"));
		solicitacaoFolga.setDataFolga(rSet.getDate("DATA_FOLGA"));
		solicitacaoFolga.setDataSolicitacao(rSet.getDate("DATA_SOLICITACAO"));
		solicitacaoFolga.setMotivoFolga(rSet.getString("MOTIVO_FOLGA"));
		solicitacaoFolga.setJustificativaFeedback(rSet.getString("JUSTIFICATIVA_FEEDBACK"));
		solicitacaoFolga.setNomeColaborador(rSet.getString("NOME"));
		solicitacaoFolga.setPeriodo(rSet.getString("PERIODO"));
		solicitacaoFolga.setStatus(rSet.getString("STATUS"));
		return solicitacaoFolga;
	}

}
