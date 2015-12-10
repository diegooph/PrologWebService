package br.com.empresa.oprojeto.webservice.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import br.com.empresa.oprojeto.models.FaleConosco;
import br.com.empresa.oprojeto.models.util.DateUtil;
import br.com.empresa.oprojeto.webservice.dao.interfaces.BaseDao;
import br.com.empresa.oprojeto.webservice.dao.interfaces.FaleConoscoDao;


/*CREATE TABLE IF NOT EXISTS FALE_CONOSCO (
		  CODIGO BIGSERIAL NOT NULL,
		  DATA DATE NOT NULL,
		  DESCRICAO TEXT NOT NULL,
		  CATEGORIA VARCHAR(20) NOT NULL,
		  CPF_COLABORADOR BIGINT NOT NULL,
		  CONSTRAINT PK_FALE_CONOSCO PRIMARY KEY(CODIGO),
		  CONSTRAINT FK_FALE_CONOSCO_COLABORADOR FOREIGN KEY(CPF_COLABORADOR)
		  REFERENCES COLABORADOR(CPF)
		);*/

public class FaleConoscoDaoImpl extends DataBaseConnection implements FaleConoscoDao, 
BaseDao<FaleConosco>  {

	@Override
	public boolean insert(FaleConosco faleConosco) throws SQLException {
		Connection conn = null;
		PreparedStatement stmt = null;
		try {
			conn = getConnection();
			stmt = conn.prepareStatement("INSERT INTO FALE_CONOSCO "
					+ "(DATA_HORA, DESCRICAO, CATEGORIA, CPF_COLABORADOR) VALUES "
					+ "(?,?,?,?) ");						
			stmt.setTimestamp(1, DateUtil.toTimestamp(faleConosco.getData()));
			stmt.setString(2, faleConosco.getDescricao());
			stmt.setString(3, faleConosco.getCategoria());
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
			stmt.setTimestamp(1, DateUtil.toTimestamp(faleConosco.getData()));
			stmt.setString(2, faleConosco.getDescricao());
			stmt.setString(3, faleConosco.getCategoria());
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
	public boolean delete(Long codigo) throws SQLException {
		Connection conn = null;
		PreparedStatement stmt = null;
		try{
			conn = getConnection();
			stmt = conn.prepareStatement("DELETE FROM FALE_CONOSCO "
					+ "WHERE CODIGO = ?");
			stmt.setLong(1, codigo);
			return (stmt.executeUpdate() > 0);
		}
		finally {
			closeConnection(conn, stmt, null);
		}	
	}

	@Override
	public FaleConosco getByCod(Long codigo) throws SQLException {
		Connection conn = null;
		PreparedStatement stmt = null;
		ResultSet rSet = null;
		try{
			conn = getConnection();
			stmt = conn.prepareStatement(" SELECT * FROM FALE_CONOSCO "
					+ "WHERE CODIGO = ?" );
			stmt.setLong(1, codigo);
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
	public List<FaleConosco> getAll() throws SQLException {
		List<FaleConosco> list  = new ArrayList<>();
		Connection conn = null;
		PreparedStatement stmt = null;
		ResultSet rSet = null;
		try {
			conn = getConnection();
			stmt = conn.prepareStatement("SELECT * FROM FALE_CONOSCO");
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
	public List<FaleConosco> getByColaborador(long cpf) throws SQLException {
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

	private FaleConosco createFaleConosco(ResultSet rSet) throws SQLException{
		FaleConosco faleConosco = new FaleConosco();
		faleConosco.setCodigo(rSet.getLong("CODIGO"));
		faleConosco.setData(rSet.getTimestamp("DATA_HORA"));
		faleConosco.setDescricao(rSet.getString("DESCRICAO"));
		faleConosco.setCategoria(rSet.getString("CATEGORIA"));
		faleConosco.setCpfColaborador(rSet.getLong("CPF_COLABORADOR"));
		return faleConosco;
	}
}
