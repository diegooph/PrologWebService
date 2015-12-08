package br.com.empresa.oprojeto.webservice.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import br.com.empresa.oprojeto.models.treinamento.Treinamento;
import br.com.empresa.oprojeto.webservice.dao.interfaces.TreinamentoDao;

public class TreinamentoDaoImpl extends DataBaseConnection implements 
	TreinamentoDao {
	
	@Override
	public List<Treinamento> getNaoVistosColaborador(Long cpf) throws SQLException {
		List<Treinamento> treinamentos = new ArrayList<>();
		Connection conn = null;
		PreparedStatement stmt = null;
		ResultSet rSet = null;
		String treinamentosNaoVistosQuery = 
				"SELECT * FROM TREINAMENTO T JOIN RESTRICAO_TREINAMENTO RT ON "
				+ "RT.COD_TREINAMENTO = T.CODIGO JOIN COLABORADOR C ON "
				+ "C.COD_FUNCAO = RT.COD_FUNCAO AND C.CPF= ? WHERE T.CODIGO NOT IN "
				+ "(SELECT TC.COD_TREINAMENTO FROM COLABORADOR C JOIN "
				+ "TREINAMENTO_COLABORADOR TC ON C.CPF = TC.CPF_COLABORADOR WHERE "
				+ "C.CPF=?)";
		try {
			conn = getConnection();
			stmt = conn.prepareStatement(treinamentosNaoVistosQuery);
			stmt.setLong(1, cpf);
			stmt.setLong(2, cpf);
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
	public List<Treinamento> getVistosColaborador(Long cpf) throws SQLException {
		List<Treinamento> treinamentos = new ArrayList<>();
		Connection conn = null;
		PreparedStatement stmt = null;
		ResultSet rSet = null;
		String treinamentosVistosQuery = 
				"SELECT * FROM TREINAMENTO T JOIN TREINAMENTO_COLABORADOR TC ON "
				+ "T.CODIGO = TC.COD_TREINAMENTO WHERE CPF_COLABORADOR = ?";
		try {
			conn = getConnection();
			stmt = conn.prepareStatement(treinamentosVistosQuery);
			stmt.setLong(1, cpf);
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
