package br.com.zalf.prolog.webservice.empresa;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;

import br.com.zalf.prolog.models.*;
import br.com.zalf.prolog.webservice.DatabaseConnection;
import br.com.zalf.prolog.webservice.autenticacao.AutenticacaoDao;
import br.com.zalf.prolog.webservice.autenticacao.AutenticacaoDaoImpl;

public class EmpresaDaoImpl extends DatabaseConnection implements EmpresaDao {

	private final String BUSCA_EQUIPES_BY_COD_UNIDADE = "SELECT E.CODIGO, E.NOME "
			+ "FROM EQUIPE E JOIN UNIDADE U ON U.CODIGO = E.COD_UNIDADE "
			+ "WHERE U.CODIGO = ?";

	private final String UPDATE_EQUIPE = "UPDATE EQUIPE SET NOME = (?) "
			+ "FROM TOKEN_AUTENTICACAO TA WHERE CODIGO = ?	"
			+ "AND TA.CPF_COLABORADOR=? "
			+ "AND TA.TOKEN=?";
	
	private final String BUSCA_FUNCOES_BY_COD_UNIDADE = "SELECT F.CODIGO, F.NOME "
			+ "FROM UNIDADE_FUNCAO UF JOIN FUNCAO F ON F.CODIGO = UF.COD_FUNCAO "
			+ "WHERE UF.COD_UNIDADE = ? "
			+ "ORDER BY F.NOME";

	public List<Equipe> getEquipesByCodUnidade (Long codUnidade) throws SQLException{
		List<Equipe> listEquipe = new ArrayList<>();
		Connection conn = null;
		PreparedStatement stmt = null;
		ResultSet rSet = null;
		try {
			conn = getConnection();
			stmt = conn.prepareStatement(BUSCA_EQUIPES_BY_COD_UNIDADE);
			stmt.setLong(1, codUnidade);
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
		equipe.setNome(rset.getString("NOME"));
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

	public List<Funcao> getFuncoesByCodUnidade (long codUnidade) throws SQLException{ 
		List<Funcao> listFuncao = new ArrayList<>();
		Connection conn = null;
		PreparedStatement stmt = null;
		ResultSet rSet = null;
		try {
			conn = getConnection();
			stmt = conn.prepareStatement(BUSCA_FUNCOES_BY_COD_UNIDADE);
			stmt.setLong(1, codUnidade);
			rSet = stmt.executeQuery();
			while(rSet.next()){
				listFuncao.add(createFuncao(rSet));
			}
		}
		finally {
			closeConnection(conn, stmt, rSet);
		}
		return listFuncao;
	}
	
	private Funcao createFuncao(ResultSet rSet) throws SQLException{
		Funcao funcao = new Funcao();
		funcao.setCodigo(rSet.getLong("CODIGO"));
		funcao.setNome(rSet.getString("NOME"));
		return funcao;
	}

	public List<Setor> getSetorByCodUnidade(Long codUnidade) throws SQLException{
		Connection conn = null;
		PreparedStatement stmt = null;
		ResultSet rSet = null;
		List<Setor> setores = new ArrayList<>();
		Setor setor = null;
		try{
			conn = getConnection();
			stmt = conn.prepareStatement("SELECT * FROM setor WHERE cod_unidade = ?\n" +
					"ORDER BY nome");
			stmt.setLong(1, codUnidade);
			rSet = stmt.executeQuery();
			while (rSet.next()){
				setor = new Setor();
				setor.setCodigo(rSet.getLong("codigo"));
				setor.setNome(rSet.getString("nome"));
				setores.add(setor);
			}
		}finally {
			closeConnection(conn, stmt, rSet);
		}
		return setores;
	}

	public AbstractResponse insertSetor(String nome, Long codUnidade)throws SQLException{
		Connection conn = null;
		PreparedStatement stmt = null;
		ResultSet rSet = null;
		try{
			conn = getConnection();
			stmt = conn.prepareStatement("INSERT INTO SETOR(cod_unidade, nome) VALUES (?,?) RETURNING CODIGO;");
			stmt.setLong(1, codUnidade);
			stmt.setString(2, nome);
			rSet = stmt.executeQuery();
			if (rSet.next()){
				return ResponseWithCod.Ok("Setor inserido com sucesso", rSet.getLong("codigo"));
			}else{
				return Response.Error("Erro ao inserir o setor");
			}
		}finally {
			closeConnection(conn, stmt, rSet);
		}
	}

//	public ResumoMapaTracking getResumoAtualizacaoDados(int ano, int mes){
//		Connection conn = null;
//		PreparedStatement stmt= null;
//		ResultSet rSet = null;
//		List<Date> mapas = null;
//		List<Date> trackings = null;
//		ResumoMapaTracking resumo = null;
//		try{
//			conn = getConnection();
//			stmt = conn.prepareStatement("");
//		}finally {
//			closeConnection(conn,stmt,rSet);
//		}
//
//
//	}
}
