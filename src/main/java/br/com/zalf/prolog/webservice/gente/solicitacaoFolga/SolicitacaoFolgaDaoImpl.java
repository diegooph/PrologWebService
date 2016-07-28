package br.com.zalf.prolog.webservice.gente.solicitacaoFolga;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

import br.com.zalf.prolog.models.*;
import br.com.zalf.prolog.models.util.DateUtils;
import br.com.zalf.prolog.webservice.DatabaseConnection;

public class SolicitacaoFolgaDaoImpl extends DatabaseConnection implements SolicitacaoFolgaDao {


	@Override
	public AbstractResponse insert(SolicitacaoFolga s) throws SQLException {
		Connection conn = null;
		PreparedStatement stmt = null;
		ResultSet rSet = null;
		try {
			// verifica se a folga esta sendo solicitada com 48h de antecedência (2 dias)
			if (ChronoUnit.DAYS.between(LocalDate.now(), DateUtils.toLocalDate(s.getDataFolga())) < 2) {
				return Response.Error("Erro ao inserir a solicitação de folga");				
			}
			conn = getConnection();
			stmt = conn.prepareStatement("INSERT INTO SOLICITACAO_FOLGA ( "
					+ "CPF_COLABORADOR, DATA_SOLICITACAO, DATA_FOLGA, "
					+ "MOTIVO_FOLGA, STATUS, PERIODO) VALUES (?, ?, ?, ?, ?, ?) RETURNING CODIGO");
			stmt.setLong(1, s.getColaborador().getCpf());
			stmt.setDate(2, new Date(System.currentTimeMillis()));
			stmt.setDate(3, DateUtils.toSqlDate(s.getDataFolga()));
			stmt.setString(4, s.getMotivoFolga());
			stmt.setString(5, SolicitacaoFolga.STATUS_PENDENTE);
			stmt.setString(6, s.getPeriodo());
			rSet = stmt.executeQuery();
			if(rSet.next()){
				return ResponseWithCod.Ok("Solicitação inserida com sucesso", rSet.getLong("CODIGO"));
			}else{
				return Response.Error("Erro ao inserir a solicitação de folga");
			}
		}
		finally {
			closeConnection(conn, stmt, rSet);
		}		
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

			stmt.setLong(1, solicitacaoFolga.getColaborador().getCpf());

			if(solicitacaoFolga.getColaboradorFeedback() != null){
				stmt.setLong(2, solicitacaoFolga.getColaboradorFeedback().getCpf());
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
	public boolean delete(Long codigo) throws SQLException {
		Connection conn = null;
		PreparedStatement stmt = null;

		try {
			conn = getConnection();
			stmt = conn.prepareStatement("DELETE FROM SOLICITACAO_FOLGA WHERE CODIGO = ? AND STATUS = 'PENDENTE'");
			stmt.setLong(1, codigo);
			int count = stmt.executeUpdate();
			if(count > 0){
				return true;
			}
		}	
		finally{
			closeConnection(conn, stmt, null);
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
			String query = "SELECT SF.*, C.NOME AS NOME_SOLICITANTE, C_FEEDBACK.NOME AS NOME_FEEDBACK FROM "
					+ "SOLICITACAO_FOLGA SF "
					+ "JOIN COLABORADOR C ON C.CPF = SF.CPF_COLABORADOR "
					+ "LEFT JOIN COLABORADOR C_FEEDBACK ON C_FEEDBACK.CPF = SF.CPF_FEEDBACK "
					+ "JOIN EQUIPE E ON E.CODIGO = C.COD_EQUIPE "
					+ "WHERE SF.DATA_FOLGA BETWEEN ? AND ? "
					+ "AND C.COD_UNIDADE = ? "
					+ "AND E.NOME LIKE ? "
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
			closeConnection(conn, stmt, rSet);
		}
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
			stmt = conn.prepareStatement("SELECT SF.CODIGO, SF.CPF_COLABORADOR, " +
					"SF.CPF_FEEDBACK, SF.DATA_FEEDBACK, SF.DATA_FOLGA, " +
					"SF.DATA_SOLICITACAO, SF.MOTIVO_FOLGA, " +
					"SF.JUSTIFICATIVA_FEEDBACK, SF.PERIODO, SF.STATUS, C.NOME AS NOME_SOLICITANTE, " +
					"C_FEEDBACK.NOME AS NOME_FEEDBACK " +
					"FROM SOLICITACAO_FOLGA SF JOIN COLABORADOR C ON " +
					"SF.CPF_COLABORADOR = C.CPF LEFT JOIN COLABORADOR C_FEEDBACK ON " +
					"SF.CPF_FEEDBACK = C_FEEDBACK.CPF JOIN TOKEN_AUTENTICACAO TA ON " +
					"? = TA.CPF_COLABORADOR AND ? = TA.TOKEN WHERE " +
					"SF.CPF_COLABORADOR = ?;");
			stmt.setLong(1, cpf);
			stmt.setString(2, token);
			stmt.setLong(3, cpf);
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

		Colaborador colaborador = new Colaborador();
		colaborador.setCpf(rSet.getLong("CPF_COLABORADOR"));
		colaborador.setNome(rSet.getString("NOME_SOLICITANTE"));
		solicitacaoFolga.setColaborador(colaborador);

		Colaborador colaboradorFeedback = new Colaborador();
		colaboradorFeedback.setCpf(rSet.getLong("CPF_FEEDBACK"));
		colaboradorFeedback.setNome(rSet.getString("NOME_FEEDBACK"));
		solicitacaoFolga.setColaboradorFeedback(colaboradorFeedback);

		solicitacaoFolga.setDataFeedback(rSet.getDate("DATA_FEEDBACK"));
		solicitacaoFolga.setDataFolga(rSet.getDate("DATA_FOLGA"));
		solicitacaoFolga.setDataSolicitacao(rSet.getDate("DATA_SOLICITACAO"));
		solicitacaoFolga.setMotivoFolga(rSet.getString("MOTIVO_FOLGA"));
		solicitacaoFolga.setJustificativaFeedback(rSet.getString("JUSTIFICATIVA_FEEDBACK"));
		solicitacaoFolga.setPeriodo(rSet.getString("PERIODO"));
		solicitacaoFolga.setStatus(rSet.getString("STATUS"));
		return solicitacaoFolga;
	}
}