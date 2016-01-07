package br.com.zalf.oprojeto.webservice.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import br.com.empresa.oprojeto.models.Relato;
import br.com.empresa.oprojeto.models.util.DateUtils;
import br.com.zalf.oprojeto.webservice.dao.interfaces.BaseDao;
import br.com.zalf.oprojeto.webservice.dao.interfaces.RelatoDao;

/*
-- RELATOS
CREATE TABLE IF NOT EXISTS RELATO (
  CODIGO BIGSERIAL NOT NULL,
  DATA TIMESTAMP NOT NULL,
  ASSUNTO TEXT NOT NULL,
  DESCRICAO TEXT NOT NULL,
  LATITUTE VARCHAR(255) NOT NULL,
  LONGITUDE VARCHAR(255) NOT NULL,
  URL_FOTO_1 TEXT NOT NULL,
  URL_FOTO_2 TEXT NOT NULL,
  URL_FOTO_3 TEXT NOT NULL,
  CPF_COLABORADOR BIGINT NOT NULL,
  CONSTRAINT PK_RELATO PRIMARY KEY (CODIGO),
  CONSTRAINT FK_RELATO_COLABORADOR FOREIGN KEY (CPF_COLABORADOR)
  REFERENCES COLABORADOR(CPF)
);
 */
public class RelatoDaoImpl extends DataBaseConnection implements RelatoDao, 
BaseDao<Relato> {

	@Override
	public boolean insert(Relato relato) throws SQLException {
		Connection conn = null;
		PreparedStatement stmt = null;
		try {
			conn = getConnection();
			stmt = conn.prepareStatement("INSERT INTO RELATO "
					+ "(DATA_HORA_LOCAL, ASSUNTO, DESCRICAO, LATITUDE, LONGITUDE, "
					+ "URL_FOTO_1, URL_FOTO_2, URL_FOTO_3, CPF_COLABORADOR) "
					+ "VALUES (?,?,?,?,?,?,?,?,?)");						
			stmt.setTimestamp(1, DateUtils.toTimestamp(relato.getDataLocal()));
			stmt.setString(2, relato.getAssunto());
			stmt.setString(3, relato.getDescricao());
			stmt.setString(4, relato.getLatitude());
			stmt.setString(5, relato.getLongitude());
			stmt.setString(6, relato.getUrlFoto1());
			stmt.setString(7, relato.getUrlFoto2());
			stmt.setString(8, relato.getUrlFoto3());
			stmt.setLong(9, relato.getCpfColaborador());
			int count = stmt.executeUpdate();
			if(count == 0){
				throw new SQLException("Erro ao inserir o relato");
			}	
		}
		finally {
			closeConnection(conn, stmt, null);
		}		
		return true;
	}

	@Override
	public boolean update(Relato relato) throws SQLException {
		Connection conn = null;
		PreparedStatement stmt = null;
		try {
			conn = getConnection();
			stmt = conn.prepareStatement("UPDATE RELATO SET DATA_HORA_LOCAL = ?, "
					+ "ASSUNTO = ?, DESCRICAO = ?, LATITUDE = ?, LONGITUDE = ?, "
					+ "URL_FOTO_1 = ?, URL_FOTO_2 = ?, URL_FOTO_3 = ?, "
					+ "CPF_COLABORADOR = ? WHERE CODIGO = ?");
			stmt.setTimestamp(1, DateUtils.toTimestamp(relato.getDataLocal()));
			stmt.setString(2, relato.getAssunto());
			stmt.setString(3, relato.getDescricao());
			stmt.setString(4, relato.getLatitude());
			stmt.setString(5, relato.getLongitude());
			stmt.setString(6, relato.getUrlFoto1());
			stmt.setString(7, relato.getUrlFoto2());
			stmt.setString(8, relato.getUrlFoto3());
			stmt.setLong(9, relato.getCpfColaborador());
			stmt.setLong(10, relato.getCodigo());
			int count = stmt.executeUpdate();
			if(count == 0){
				throw new SQLException("Erro ao atualizar o relato");
			}	
		}
		finally {
			closeConnection(conn, stmt, null);
		}		
		return true;
	}

	@Override
	public boolean delete(Long codigo) throws SQLException {
		Connection conn = null;
		PreparedStatement stmt = null;
		try {
			conn = getConnection();
			stmt = conn.prepareStatement("DELETE FROM RELATO WHERE CODIGO = ?");
			stmt.setLong(1, codigo);
			return (stmt.executeUpdate() > 0);
		} finally {
			closeConnection(conn, stmt, null);
		}
	}

	@Override
	public Relato getByCod(Long codigo) throws SQLException {
		Connection conn = null;
		PreparedStatement stmt = null;
		ResultSet rSet = null;
		try {
			conn = getConnection();
			stmt = conn.prepareStatement("SELECT * FROM RELATO WHERE CODIGO = ?");
			stmt.setLong(1, codigo);
			rSet = stmt.executeQuery();
			if (rSet.next()) {
				Relato relato = createRelato(rSet);
				return relato;
			}
		} finally {
			closeConnection(conn, stmt, rSet);
		}
		return null;
	}

	@Override
	public List<Relato> getAll() throws SQLException {
		List<Relato> relatos = new ArrayList<>();
		Connection conn = null;
		PreparedStatement stmt = null;
		ResultSet rSet = null;
		try {
			conn = getConnection();
			stmt = conn.prepareStatement("SELECT * FROM RELATO");
			rSet = stmt.executeQuery();
			while (rSet.next()) {
				Relato relato = createRelato(rSet);
				relatos.add(relato);
			}
		} finally {
			closeConnection(conn, stmt, rSet);
		}
		return relatos;
	}

	@Override
	public List<Relato> getByColaborador(Long cpf) throws SQLException {
		List<Relato> relatos = new ArrayList<>();
		Connection conn = null;
		PreparedStatement stmt = null;
		ResultSet rSet = null;
		try {
			conn = getConnection();
			stmt = conn.prepareStatement("SELECT * FROM RELATO WHERE "
					+ "CPF_COLABORADOR = ?");
			stmt.setLong(1, cpf);
			rSet = stmt.executeQuery();
			while (rSet.next()) {
				Relato relato = createRelato(rSet);
				relatos.add(relato);
			}
		} finally {
			closeConnection(conn, stmt, rSet);
		}
		return relatos;
	}
	
	@Override
	public List<Relato> getAllExcetoColaborador(Long cpf) throws SQLException {
		List<Relato> relatos = new ArrayList<>();
		Connection conn = null;
		PreparedStatement stmt = null;
		ResultSet rSet = null;
		try {
			conn = getConnection();
			stmt = conn.prepareStatement("SELECT * FROM RELATO WHERE "
					+ "CPF_COLABORADOR != ?");
			stmt.setLong(1, cpf);
			rSet = stmt.executeQuery();
			while (rSet.next()) {
				Relato relato = createRelato(rSet);
				relatos.add(relato);
			}
		} finally {
			closeConnection(conn, stmt, rSet);
		}
		return relatos;
	}

	private Relato createRelato(ResultSet rSet) throws SQLException{
		Relato relato = new Relato();
		relato.setCodigo(rSet.getLong("CODIGO"));
		relato.setCpfColaborador(rSet.getLong("CPF_COLABORADOR"));
		relato.setDataLocal(rSet.getTimestamp("DATA_HORA_LOCAL"));
		relato.setDataDatabase(rSet.getTimestamp("DATA_HORA_DATABASE"));
		relato.setAssunto(rSet.getString("ASSUNTO"));
		relato.setDescricao(rSet.getString("DESCRICAO"));
		relato.setLatitude(rSet.getString("LATITUDE"));
		relato.setLongitude(rSet.getString("LONGITUDE"));
		relato.setUrlFoto1(rSet.getString("URL_FOTO_1"));
		relato.setUrlFoto2(rSet.getString("URL_FOTO_2"));
		relato.setUrlFoto3(rSet.getString("URL_FOTO_3"));
		return relato;
	}
}
