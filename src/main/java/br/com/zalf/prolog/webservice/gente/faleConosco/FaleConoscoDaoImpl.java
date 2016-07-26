package br.com.zalf.prolog.webservice.gente.faleConosco;

import java.sql.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import br.com.zalf.prolog.models.Colaborador;
import br.com.zalf.prolog.models.FaleConosco;
import br.com.zalf.prolog.models.Request;
import br.com.zalf.prolog.models.util.DateUtils;
import br.com.zalf.prolog.webservice.DatabaseConnection;
import br.com.zalf.prolog.webservice.util.L;

public class FaleConoscoDaoImpl extends DatabaseConnection  {

	public boolean insert(FaleConosco faleConosco, Long codUnidade) throws SQLException {
		Connection conn = null;
		PreparedStatement stmt = null;
		try {
			conn = getConnection();
			stmt = conn.prepareStatement("INSERT INTO FALE_CONOSCO "
					+ "(DATA_HORA, DESCRICAO, CATEGORIA, CPF_COLABORADOR, COD_UNIDADE) VALUES "
					+ "(?,?,?,?,?) ");
			// A data do fale conosco é pegada com System.currentTimeMillis()
			// pois assim a data vem do servidor, que sempre estará certa 
			// o que não poderíamos garantir caso viesse do lado do cliente.
			stmt.setTimestamp(1, DateUtils.toTimestamp(new Date(System.currentTimeMillis())));
			stmt.setString(2, faleConosco.getDescricao());
			stmt.setString(3, faleConosco.getCategoria().asString());
			stmt.setLong(4, faleConosco.getColaborador().getCpf());
			stmt.setLong(5, codUnidade);
			int count = stmt.executeUpdate();
			if(count == 0){
				throw new SQLException("Erro ao inserir o fale conosco");
			}	
		}
		finally {
			closeConnection(conn, stmt, null);
		}		
		return true;
	}

	public boolean update(FaleConosco faleConosco) throws SQLException {
		Connection conn = null;
		PreparedStatement stmt = null;
		try {
			conn = getConnection();
			stmt = conn.prepareStatement(" UPDATE FALE_CONOSCO SET "
					+ "DATA_HORA = ?, DESCRICAO = ?, CATEGORIA = ?, CPF_COLABORADOR = ? "
					+ "WHERE CODIGO = ? ");
			stmt.setTimestamp(1, DateUtils.toTimestamp(faleConosco.getData()));
			stmt.setString(2, faleConosco.getDescricao());
			stmt.setString(3, faleConosco.getCategoria().asString());
			stmt.setLong(4, faleConosco.getColaborador().getCpf());
			stmt.setLong(5, faleConosco.getCodigo());
			int count = stmt.executeUpdate();
			if(count == 0){
				throw new SQLException("Erro ao atualizar o fale conosco");
			}	
		}
		finally {
			closeConnection(conn, stmt, null);
		}		
		return true;
	}

	public boolean delete(Request<FaleConosco> request) throws SQLException {
		Connection conn = null;
		PreparedStatement stmt = null;
		try{
			conn = getConnection();
			stmt = conn.prepareStatement("DELETE FROM FALE_CONOSCO "
					+ "WHERE CODIGO = ?");
			stmt.setLong(1, request.getObject().getCodigo());
			return (stmt.executeUpdate() > 0);
		}
		finally {
			closeConnection(conn, stmt, null);
		}	
	}

	public FaleConosco getByCod(Long codigo, Long codUnidade) throws Exception {
		Connection conn = null;
		PreparedStatement stmt = null;
		ResultSet rSet = null;
		try{
			conn = getConnection();
			stmt = conn.prepareStatement("SELECT *, C.cpf AS CPF_COLABORADOR, C.nome AS NOME_COLABORADOR,\n" +
					"C2.cpf AS CPF_FEEDBACK, C2.nome AS NOME_FEEDBACK\n" +
					"FROM FALE_CONOSCO F JOIN colaborador C ON C.cpf = F.CPF_COLABORADOR\n" +
					"LEFT JOIN COLABORADOR C2 ON C2.CPF = F.CPF_FEEDBACK\n" +
					"WHERE F.CODIGO = ? AND F.cod_unidade = ?" );
			stmt.setLong(1, codigo);
			stmt.setLong(2, codUnidade);
			rSet = stmt.executeQuery();
			if(rSet.next()){
				FaleConosco c = createFaleConosco(rSet);
				return c;
			}
		}
		finally {
			closeConnection(conn, stmt, rSet);
		}
		return null;
	}

	public List<FaleConosco> getAll(long dataInicial, long dataFinal, int limit, int offset,
									String equipe, Long codUnidade, String status, String categoria) throws Exception {
		List<FaleConosco> list  = new ArrayList<>();
		Connection conn = null;
		PreparedStatement stmt = null;
		ResultSet rSet = null;
		try {
			conn = getConnection();
			stmt = conn.prepareStatement("SELECT F.*, C.cpf AS CPF_COLABORADOR, C.nome AS NOME_COLABORADOR,\n" +
					"C2.cpf AS CPF_FEEDBACK, C2.nome AS NOME_FEEDBACK\n" +
					"FROM FALE_CONOSCO F JOIN colaborador C ON C.cpf = F.CPF_COLABORADOR\n" +
					"JOIN EQUIPE E ON E.codigo = C.cod_equipe\n" +
					"LEFT JOIN COLABORADOR C2 ON C2.CPF = F.CPF_FEEDBACK\n" +
					"WHERE E.nome LIKE ? AND F.cod_unidade = ? AND F.status LIKE ? AND F.categoria LIKE ? " +
					"AND F.DATA_HORA BETWEEN ? AND ? " +
					"ORDER BY F.DATA_HORA " +
					"LIMIT ? OFFSET ?");
			stmt.setString(1, equipe);
			stmt.setLong(2, codUnidade);
			stmt.setString(3, status);
			stmt.setString(4, categoria);
			stmt.setTimestamp(5, new Timestamp(dataInicial));
			stmt.setTimestamp(6, new Timestamp(dataFinal));
			stmt.setInt(7, limit);
			stmt.setInt(8, offset);
			L.d("tag", stmt.toString());
			rSet = stmt.executeQuery();
			while (rSet.next()) {
				FaleConosco faleConosco = createFaleConosco(rSet);
				list.add(faleConosco);
			}
		} finally {
			closeConnection(conn, stmt, rSet);
		}
		return list;
	}

	public List<FaleConosco> getByColaborador(long cpf) throws Exception {
		List<FaleConosco> list  = new ArrayList<>();
		Connection conn = null;
		PreparedStatement stmt = null;
		ResultSet rSet = null;
		try {
			conn = getConnection();
			stmt = conn.prepareStatement("SELECT * FROM FALE_CONOSCO WHERE "
					+ "CPF_COLABORADOR = ?");
			stmt.setLong(1, cpf);
			rSet = stmt.executeQuery();
			while (rSet.next()) {
				FaleConosco faleConosco = createFaleConosco(rSet);
				list.add(faleConosco);
			}
		} finally {
			closeConnection(conn, stmt, rSet);
		}
		return list;
	}

	private FaleConosco createFaleConosco(ResultSet rSet) throws Exception{
		
		FaleConosco faleConosco = new FaleConosco();
		faleConosco.setCodigo(rSet.getLong("CODIGO"));
		faleConosco.setData(rSet.getTimestamp("DATA_HORA"));
		faleConosco.setDescricao(rSet.getString("DESCRICAO"));
		Colaborador realizador = new Colaborador();
		realizador.setCpf(rSet.getLong("CPF_COLABORADOR"));
		faleConosco.setColaborador(realizador);
		faleConosco.setCategoria(FaleConosco.Categoria.fromString(rSet.getString("CATEGORIA")));
		String temp = rSet.getString("feedback");
		L.d("tag", String.valueOf(faleConosco.getCodigo()));
		L.d("tag", rSet.getString("feedback"));
		if(temp != null){
			faleConosco.setFeedback(rSet.getString("FEEDBACK"));
			Colaborador colaboradorFeedback = new Colaborador();
			colaboradorFeedback.setCpf(rSet.getLong("cpf_feedback"));
			colaboradorFeedback.setNome(rSet.getString("nome_feedback"));
			faleConosco.setColaboradorFeedback(colaboradorFeedback);
			faleConosco.setDataFeedback(rSet.getTimestamp("DATA_HORA_FEEDBACK"));
			faleConosco.setFeedback(rSet.getString("FEEDBACK"));
		}
		return faleConosco;
	}
}
