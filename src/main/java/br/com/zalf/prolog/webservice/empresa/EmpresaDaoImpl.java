package br.com.zalf.prolog.webservice.empresa;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import br.com.zalf.prolog.models.Autenticacao;
import br.com.zalf.prolog.models.Equipe;
import br.com.zalf.prolog.models.Request;
import br.com.zalf.prolog.webservice.DatabaseConnection;
import br.com.zalf.prolog.webservice.autenticacao.AutenticacaoDao;
import br.com.zalf.prolog.webservice.autenticacao.AutenticacaoDaoImpl;

public class EmpresaDaoImpl extends DatabaseConnection {

	private final String BUSCA_EQUIPES_BY_COD_UNIDADE = "SELECT E.CODIGO, E.NOME "
			+ "FROM EQUIPE E JOIN UNIDADE U ON U.CODIGO = E.COD_UNIDADE "
			+ "WHERE U.CODIGO = ?";

	private final String UPDATE_EQUIPE = "UPDATE EQUIPE SET NOME = (?) "
			+ "FROM TOKEN_AUTENTICACAO TA WHERE CODIGO = ?	"
			+ "AND TA.CPF_COLABORADOR=? "
			+ "AND TA.TOKEN=?";

	public List<Equipe> getEquipesByCodUnidade (Request<?> request) throws SQLException{ 
		List<Equipe> listEquipe = new ArrayList<>();
		Connection conn = null;
		PreparedStatement stmt = null;
		ResultSet rSet = null;
		try {
			conn = getConnection();
			stmt = conn.prepareStatement(BUSCA_EQUIPES_BY_COD_UNIDADE);
			stmt.setLong(1, request.getCodUnidade());
			rSet = stmt.executeQuery();
			while(rSet.next()){
				listEquipe.add(createEquipe(rSet));
			}
		}
		finally {
			closeConnection(conn, stmt, rSet);
		}
		return listEquipe;
	}

	public boolean updateEquipe (Request<Equipe> request) throws SQLException{
		Connection conn = null;
		PreparedStatement stmt = null;
		try {
			conn = getConnection();
			stmt = conn.prepareStatement(UPDATE_EQUIPE);
			stmt.setString(1, request.getObject().getNome());
			stmt.setLong(2, request.getObject().getCodigo());
			stmt.setLong(3, request.getCpf());
			stmt.setString(4, request.getToken());
			int count = stmt.executeUpdate();
			if(count == 0){
				throw new SQLException("Erro ao atualizar a equipe");
			}
		}
		finally {
			closeConnection(conn, stmt, null);
		}
		return true;
	}

	private Equipe createEquipe (ResultSet rset) throws SQLException{
		Equipe equipe = new Equipe();
		equipe.setCodigo(rset.getLong("CODIGO"));
		System.out.println(equipe.getCodigo());
		equipe.setNome(rset.getString("NOME"));
		System.out.println(equipe.getNome());
		return equipe;
	}
	
	public boolean createEquipe(Request<Equipe> request) throws SQLException{
		Autenticacao autenticacao = new Autenticacao("", request.getCpf(), 
				request.getToken());
		AutenticacaoDao autenticacaoDao = new AutenticacaoDaoImpl();
		if (autenticacaoDao.verifyIfExists(autenticacao)) {
			Connection conn = null;
			PreparedStatement stmt = null;
			try {
				conn = getConnection();

				stmt = conn.prepareStatement("INSERT INTO EQUIPE "
						+ "(NOME, COD_UNIDADE) VALUES "
						+ "(?,?) ");
				stmt.setString(1, request.getObject().getNome());
				stmt.setLong(2, request.getCodUnidade());
				int count = stmt.executeUpdate();
				if(count == 0){
					throw new SQLException("Erro ao inserir a equipe");
				}	
			}
			finally {
				closeConnection(conn, stmt, null);
			}		
			return true;
		}
		return false;
	}
	
	//TODO: Verificar a viabilidade de implementar um método para exclusão de uma equipe, 
	//a equipe está ligada como fk de colaborador e fk de calendário

}
