package br.com.zalf.prolog.webservice.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import br.com.zalf.prolog.models.treinamento.Treinamento;
import br.com.zalf.prolog.models.treinamento.TreinamentoColaborador;
import br.com.zalf.prolog.models.util.DateUtils;
import br.com.zalf.prolog.webservice.DatabaseConnection;
import br.com.zalf.prolog.webservice.dao.interfaces.TreinamentoDao;

public class TreinamentoDaoImpl extends DatabaseConnection implements 
	TreinamentoDao {
	
	@Override
	public List<Treinamento> getNaoVistosColaborador(Long cpf, String token) throws SQLException {
		List<Treinamento> treinamentos = new ArrayList<>();
		Connection conn = null;
		PreparedStatement stmt = null;
		ResultSet rSet = null;
		String treinamentosNaoVistosQuery = 
				"SELECT * FROM TREINAMENTO T JOIN TOKEN_AUTENTICACAO TA "
				+ "ON ? = TA.CPF_COLABORADOR AND ? = TA.TOKEN JOIN "
				+ "RESTRICAO_TREINAMENTO RT ON RT.COD_TREINAMENTO = T.CODIGO "
				+ "JOIN COLABORADOR C ON C.COD_FUNCAO = RT.COD_FUNCAO AND C.CPF "
				+ "= ? WHERE T.CODIGO NOT IN (SELECT TC.COD_TREINAMENTO FROM COLABORADOR C JOIN "
				+ "TREINAMENTO_COLABORADOR TC ON C.CPF = TC.CPF_COLABORADOR WHERE "
				+ "C.CPF = ?);";
		try {
			conn = getConnection();
			stmt = conn.prepareStatement(treinamentosNaoVistosQuery);
			stmt.setLong(1, cpf);
			stmt.setString(2, token);
			stmt.setLong(3, cpf);
			stmt.setLong(4, cpf);
			rSet = stmt.executeQuery();
			while (rSet.next()) {
				Treinamento treinamento = createTreinamento(rSet);
				treinamentos.add(treinamento);
			}
		} finally {
			closeConnection(conn, stmt, rSet);
		}
		return treinamentos;
	}
	
	@Override
	public List<Treinamento> getVistosColaborador(Long cpf, String token) throws SQLException {
		List<Treinamento> treinamentos = new ArrayList<>();
		Connection conn = null;
		PreparedStatement stmt = null;
		ResultSet rSet = null;
		String treinamentosVistosQuery = 
				"SELECT * FROM TREINAMENTO T JOIN TOKEN_AUTENTICACAO TA ON ? = "
				+ "TA.CPF_COLABORADOR AND ? = TA.TOKEN JOIN TREINAMENTO_COLABORADOR TC ON "
				+ "T.CODIGO = TC.COD_TREINAMENTO WHERE TC.CPF_COLABORADOR = ?;";
		try {
			conn = getConnection();
			stmt = conn.prepareStatement(treinamentosVistosQuery);
			stmt.setLong(1, cpf);
			stmt.setString(2, token);
			stmt.setLong(3, cpf);
			rSet = stmt.executeQuery();
			while (rSet.next()) {
				Treinamento treinamento = createTreinamento(rSet);
				treinamentos.add(treinamento);
			}
		} finally {
			closeConnection(conn, stmt, rSet);
		}
		return treinamentos;
	}
	
	@Override
	public boolean marcarTreinamentoComoVisto(TreinamentoColaborador treinamentoColaborador) throws SQLException {
		Connection conn = null;
		PreparedStatement stmt = null;
		try {
			conn = getConnection();
			stmt = conn.prepareStatement("INSERT INTO TREINAMENTO_COLABORADOR "
					+ "(COD_TREINAMENTO, CPF_COLABORADOR, DATA_VISUALIZACAO) VALUES "
					+ "(?, ?, ?)");
			stmt.setLong(1, treinamentoColaborador.getCodTreinamento());
			stmt.setLong(2, treinamentoColaborador.getCpfColaborador());
			stmt.setDate(3, DateUtils.toSqlDate(treinamentoColaborador.getDataVisualizacao()));
			int count = stmt.executeUpdate();
			if(count == 0){
				throw new SQLException("Erro ao inserir o fale conosco");
			}
			
		} finally {
			closeConnection(conn, stmt, null);
		}
		return true;
	}


	private Treinamento createTreinamento(ResultSet rSet) throws SQLException {
		Treinamento treinamento = new Treinamento();
		treinamento.setCodigo(rSet.getLong("CODIGO"));
		treinamento.setTitulo(rSet.getString("TITULO"));
		treinamento.setDescricao(rSet.getString("DESCRICAO"));
		treinamento.setUrlArquivo(rSet.getString("URL_ARQUIVO"));
		treinamento.setDataLiberacao(rSet.getDate("DATA_LIBERACAO"));
		treinamento.setCodUnidade(rSet.getLong("COD_UNIDADE"));
		return treinamento;
	}
}
