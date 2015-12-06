package br.com.empresa.oprojeto.webservice.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import br.com.empresa.oprojeto.models.FaleConosco;
import br.com.empresa.oprojeto.webservice.dao.interfaces.BaseDao;
import br.com.empresa.oprojeto.webservice.dao.interfaces.FaleConoscoDao;
import br.com.empresa.oprojeto.webservice.util.DateUtil;



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

public class FaleConoscoDaoImpl extends ConnectionFactory implements FaleConoscoDao, BaseDao  {

	@Override
	public boolean save(Object object) throws SQLException {
		Connection conn = null;
		PreparedStatement stmt = null;
		FaleConosco faleConosco = (FaleConosco) object;
		try {
			conn = getConnection();
			if(faleConosco.getCodigo() == null){
				stmt = conn.prepareStatement("INSERT INTO FALE_CONOSCO "
						+ "(DATA, DESCRICAO, CATEGORIA, CPF_COLABORADOR) VALUES "
						+ "(?,?,?,?) ");						
			}else{
				stmt = conn.prepareStatement(" UPDATE FALE_CONOSCO SET "
						+ "DATA = ?, DESCRICAO = ?, CATEGORIA = ?, CPF_COLABORADOR = ? "
						+ "WHERE CODIGO = ? ");
			}
			stmt.setTimestamp(1, DateUtil.toTimestamp(faleConosco.getData()));
			stmt.setString(2, faleConosco.getDescricao());
			stmt.setString(3, faleConosco.getCategoria());
			stmt.setLong(4, faleConosco.getCpfColaborador());
			if(faleConosco.getCodigo() != null){		
				stmt.setLong(5, faleConosco.getCodigo());
			}
			int count = stmt.executeUpdate();
			if(count == 0){
				throw new SQLException("Erro ao inserir o formulário");
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
			stmt.executeQuery();
		}
		finally {
			closeConnection(conn, stmt, null);
		}	
		return true;
	}

	@Override
	public Object getByCod(Long codigo) throws SQLException {

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

	public FaleConosco createFaleConosco(ResultSet rSet) throws SQLException{
		FaleConosco faleConosco = new FaleConosco();
		faleConosco.setCodigo(rSet.getLong("CODIGO"));
		faleConosco.setData(rSet.getTimestamp("DATA")); //TODO: VERIFICAR SAPORRA
		faleConosco.setDescricao(rSet.getString("DESCRICAO"));
		faleConosco.setCategoria(rSet.getString("CATEGORIA"));
		faleConosco.setCpfColaborador(rSet.getLong("CPF_COLABORADOR"));
		return faleConosco;
	}

	@Override
	public List<Object> getAll() throws SQLException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<FaleConosco> getPorColaborador(long cpf) throws SQLException {
		// TODO Auto-generated method stub
		return null;
	}



}
