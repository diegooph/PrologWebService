package br.com.zalf.prolog.webservice.dao;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import com.google.gson.Gson;

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
	public boolean update(SolicitacaoFolga solicitacaoFolga) throws SQLException {
		Connection conn = null;
		PreparedStatement stmt = null;
		try {
			conn = getConnection();
			stmt = conn.prepareStatement("UPDATE SOLICITACAO_FOLGA SET "
					+ " CPF_COLABORADOR=? , "
					+ " CPF_FEEDBACK=? , "
					+ " DATA_SOLICITACAO=? , "
					+ " DATA_FOLGA=? , "
					+ " DATA_FEEDBACK=? , "
					+ " MOTIVO_FOLGA=? , "
					+ " JUSTIFICATIVA_FEEDBACK=? , "
					+ " STATUS=? , "
					+ " PERIODO=? "
					+ "WHERE CODIGO=?");

			stmt.setLong(1, solicitacaoFolga.getCpfColaborador());

			if(solicitacaoFolga.getCpfFeedback() != null){
				stmt.setLong(2, solicitacaoFolga.getCpfFeedback());
			}else{
				stmt.setNull(2, java.sql.Types.BIGINT);
			}
			if(solicitacaoFolga.getDataSolicitacao() != null){
				stmt.setDate(3, DateUtils.toSqlDate(solicitacaoFolga.getDataSolicitacao()));
			}else{
				stmt.setDate(3, null);
			}
			if(solicitacaoFolga.getDataFolga() != null){
				stmt.setDate(4, DateUtils.toSqlDate(solicitacaoFolga.getDataFolga()));
			}else{
				stmt.setDate(4, null);
			}
			if(solicitacaoFolga.getDataFeedback() != null){
				stmt.setDate(5, DateUtils.toSqlDate(solicitacaoFolga.getDataFeedback()));
			}else{
				stmt.setDate(5, new Date(System.currentTimeMillis()));
			}
			stmt.setString(6, solicitacaoFolga.getMotivoFolga());
			stmt.setString(7, solicitacaoFolga.getJustificativaFeedback());
			stmt.setString(8, solicitacaoFolga.getStatus());
			stmt.setString(9, solicitacaoFolga.getPeriodo());
			stmt.setLong(10, solicitacaoFolga.getCodigo());
			int count = stmt.executeUpdate();
			if(count > 0){
				return true;
			}else{
				return false;
			}
		}
		finally {
			closeConnection(conn, stmt, null);
		}
	}

	@Override
	public boolean delete(SolicitacaoFolga solicitacaoFolga) throws SQLException {
		Connection conn = null;
		PreparedStatement stmt = null;
		ResultSet rSet = null;
			
		try{
			conn = getConnection();
			stmt = conn.prepareStatement("DELETE FROM SOLICITACAO_FOLGA WHERE CODIGO = ? AND STATUS = 'PENDENTE'");
			stmt.setLong(1, solicitacaoFolga.getCodigo());
			int count = stmt.executeUpdate();
			if(count > 0){
				return true;
			}
		}	
		finally{
			closeConnection(conn, stmt, rSet);
		}
		return false;
	}

	@Override
	public SolicitacaoFolga getByCod(Request<?> request) throws SQLException {
		throw new UnsupportedOperationException("Operation not supported yet");
	}

	@Override
	public List<SolicitacaoFolga> getAll(LocalDate dataInicial, LocalDate dataFinal, 
			Long codUnidade, String codEquipe, String status, Long cpfColaborador) throws SQLException {

		Connection conn = null;
		PreparedStatement stmt = null;
		ResultSet rSet = null;
		List<SolicitacaoFolga> list = new ArrayList<>();
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
		System.out.println(new Gson().toJson(solicitacaoFolga));
		return solicitacaoFolga;
	}

}
