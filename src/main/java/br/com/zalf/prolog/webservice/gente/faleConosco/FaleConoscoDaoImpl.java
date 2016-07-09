package br.com.zalf.prolog.webservice.gente.faleConosco;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import br.com.zalf.prolog.models.FaleConosco;
import br.com.zalf.prolog.models.Request;
import br.com.zalf.prolog.models.util.DateUtils;
import br.com.zalf.prolog.webservice.DatabaseConnection;

public class FaleConoscoDaoImpl extends DatabaseConnection implements FaleConoscoDao  {

	@Override
	public boolean insert(FaleConosco faleConosco) throws SQLException {
		Connection conn = null;
		PreparedStatement stmt = null;
		try {
			conn = getConnection();
			stmt = conn.prepareStatement("INSERT INTO FALE_CONOSCO "
					+ "(DATA_HORA, DESCRICAO, CATEGORIA, CPF_COLABORADOR) VALUES "
					+ "(?,?,?,?) ");
			// A data do fale conosco é pegada com System.currentTimeMillis()
			// pois assim a data vem do servidor, que sempre estará certa 
			// o que não poderíamos garantir caso viesse do lado do cliente.
			stmt.setTimestamp(1, DateUtils.toTimestamp(new Date(System.currentTimeMillis())));
			stmt.setString(2, faleConosco.getDescricao());
			stmt.setString(3, faleConosco.getCategoria().asString());
			stmt.setLong(4, faleConosco.getCpfColaborador());
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

	@Override
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
			stmt.setLong(4, faleConosco.getCpfColaborador());		
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

	@Override
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

	// TODO: Fazer join token 
	@Override
	public FaleConosco getByCod(Request<FaleConosco> request) throws Exception {
		Connection conn = null;
		PreparedStatement stmt = null;
		ResultSet rSet = null;
		try{
			conn = getConnection();
			stmt = conn.prepareStatement(" SELECT * FROM FALE_CONOSCO JOIN "
					+ "TOKEN_AUTANTICACAO TA ON TA.CPF_COLABORADOR = ? AND TA.TOKEN = ? "
					+ "WHERE CODIGO = ?" );
			stmt.setLong(1, request.getCpf());
			stmt.setString(2, request.getToken());
			stmt.setLong(3, request.getObject().getCodigo());
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

	@Override
	public List<FaleConosco> getAll(Request<?> request) throws Exception {
		List<FaleConosco> list  = new ArrayList<>();
		Connection conn = null;
		PreparedStatement stmt = null;
		ResultSet rSet = null;
		try {
			conn = getConnection();
			stmt = conn.prepareStatement("SELECT * FROM FALE_CONOSCO "
					+ "JOIN TOKEN_AUTENTICACAO TA ON TA.CPF_COLABORADOR = ? AND "
					+ "TA.TOKEN = ?");
			stmt.setLong(1, request.getCpf());
			stmt.setString(2, request.getToken());
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

	@Override
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
		faleConosco.setCpfColaborador(rSet.getLong("CPF_COLABORADOR"));
		faleConosco.setCategoria(FaleConosco.Categoria.fromString(rSet.getString("CATEGORIA")));
		return faleConosco;
	}
}
