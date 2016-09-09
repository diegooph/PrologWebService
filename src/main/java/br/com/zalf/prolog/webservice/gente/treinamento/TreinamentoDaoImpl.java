package br.com.zalf.prolog.webservice.gente.treinamento;

import br.com.zalf.prolog.commons.colaborador.Colaborador;
import br.com.zalf.prolog.commons.colaborador.Funcao;
import br.com.zalf.prolog.commons.util.DateUtils;
import br.com.zalf.prolog.gente.treinamento.Treinamento;
import br.com.zalf.prolog.gente.treinamento.TreinamentoColaborador;
import br.com.zalf.prolog.webservice.DatabaseConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class TreinamentoDaoImpl extends DatabaseConnection implements 
TreinamentoDao {


	public List<Treinamento> getAll (LocalDate dataInicial, LocalDate dataFinal, String codFuncao,
									 Long codUnidade, long limit, long offset) throws SQLException{

		List<Treinamento> listTreinamento = new ArrayList<>();
		Connection conn = null;
		PreparedStatement stmt = null;
		ResultSet rSet = null;
		Funcao funcao;
		String treinamentosNaoVistosQuery = 
				"SELECT T.*, F.CODIGO AS COD_FUNCAO, F.NOME AS NOME_FUNCAO "
						+ "FROM TREINAMENTO T JOIN RESTRICAO_TREINAMENTO RT ON T.CODIGO = RT.COD_TREINAMENTO "
						+ "JOIN FUNCAO F ON F.CODIGO = RT.COD_FUNCAO "
						+ "WHERE T.COD_UNIDADE = ? AND T.DATA_HORA_CADASTRO >= ? AND T.DATA_HORA_CADASTRO <= ? "
						+ "AND F.CODIGO::TEXT LIKE ? "
						+ "ORDER BY RT.COD_TREINAMENTO, RT.COD_FUNCAO "
						+ "LIMIT ? OFFSET ?";
		try {
			conn = getConnection();
			stmt = conn.prepareStatement(treinamentosNaoVistosQuery);
			stmt.setLong(1, codUnidade);
			stmt.setDate(2, DateUtils.toSqlDate(dataInicial));
			stmt.setDate(3, DateUtils.toSqlDate(dataFinal));
			stmt.setString(4, String.valueOf(codFuncao));
			stmt.setLong(5, limit);
			stmt.setLong(6, offset);
			rSet = stmt.executeQuery();
			while (rSet.next()) {

				if(listTreinamento.size() == 0){ // caso a lista esteja vazia, cria o primeiro treinamento e a primeira funcao
					Treinamento treinamento = createTreinamento(rSet);
					treinamento.setFuncoesLiberadas(new ArrayList<>());
					funcao = new Funcao();
					funcao = createFuncao(rSet);
					treinamento.getFuncoesLiberadas().add(funcao);
					listTreinamento.add(treinamento);
				}else{// caso a lista ja tenha algum item (treinamento)
					if(listTreinamento.get(listTreinamento.size()-1).getCodigo() == rSet.getLong("CODIGO")){//item anterior == ao do rset atual
						funcao = new Funcao();
						funcao = createFuncao(rSet);
						listTreinamento.get(listTreinamento.size()-1).getFuncoesLiberadas().add(funcao);
					}else{// item anterior != do item atual
						Treinamento treinamento = createTreinamento(rSet);
						treinamento.setFuncoesLiberadas(new ArrayList<>());
						funcao = new Funcao();
						funcao = createFuncao(rSet);
						treinamento.getFuncoesLiberadas().add(funcao);
						listTreinamento.add(treinamento);
					}
				}
			}
		} finally {
			closeConnection(conn, stmt, rSet);
		}
		System.out.println(listTreinamento);
		return listTreinamento;
	}

	private Funcao createFuncao (ResultSet rSet) throws SQLException{
		Funcao funcao = new Funcao();
		funcao.setCodigo(rSet.getLong("COD_FUNCAO"));
		funcao.setNome(rSet.getString("NOME_FUNCAO"));
		return funcao;
	}

	@Override
	public List<Treinamento> getNaoVistosColaborador(Long cpf) throws SQLException {
		List<Treinamento> treinamentos = new ArrayList<>();
		Connection conn = null;
		PreparedStatement stmt = null;
		ResultSet rSet = null;
		String treinamentosNaoVistosQuery = 
				"SELECT * FROM TREINAMENTO T JOIN "
						+ "RESTRICAO_TREINAMENTO RT ON RT.COD_TREINAMENTO = T.CODIGO "
						+ "JOIN COLABORADOR C ON C.COD_FUNCAO = RT.COD_FUNCAO AND C.CPF "
						+ "= ? WHERE T.CODIGO NOT IN (SELECT TC.COD_TREINAMENTO FROM COLABORADOR C JOIN "
						+ "TREINAMENTO_COLABORADOR TC ON C.CPF = TC.CPF_COLABORADOR WHERE "
						+ "C.CPF = ?);";
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
						+ "T.CODIGO = TC.COD_TREINAMENTO WHERE TC.CPF_COLABORADOR = ?;";
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
			stmt.setLong(2, treinamentoColaborador.getColaborador().getCpf());
			stmt.setDate(3, DateUtils.toSqlDate(treinamentoColaborador.getDataVisualizacao()));
			int count = stmt.executeUpdate();
			if(count == 0){
				throw new SQLException("Erro ao marcar o treinamento como visto");
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

	@Override
	public boolean insert(Treinamento treinamento) throws SQLException {
		Connection conn = null;
		PreparedStatement stmt = null;
		try {
			conn = getConnection();
			stmt = conn.prepareStatement("INSERT INTO TREINAMENTO (TITULO, DESCRICAO, URL_ARQUIVO, "
					+ "DATA_LIBERACAO, COD_UNIDADE, data_hora_cadastro) "
					+ "VALUES (?,?,?,?,?,?)");

			stmt.setString(1, treinamento.getTitulo());
			stmt.setString(2, treinamento.getDescricao());
			stmt.setString(3, treinamento.getUrlArquivo());
			stmt.setDate(4, DateUtils.toSqlDate(treinamento.getDataLiberacao()));
			stmt.setLong(5, treinamento.getCodUnidade());
			stmt.setTimestamp(6, DateUtils.toTimestamp(treinamento.getDataHoraCadastro()));
			int count = stmt.executeUpdate();
			if(count == 0 && !insertRestricaoTreinamento(treinamento.getFuncoesLiberadas(), treinamento.getCodigo())){
				throw new SQLException("Erro ao inserir treinamento");
			}	
		}
		finally {
			closeConnection(conn, stmt, null);
		}		
		return true;
	}

	private boolean insertRestricaoTreinamento(List<Funcao> listFuncao, long codTreinamento) throws SQLException{
		Connection conn = null;
		PreparedStatement stmt = null;
		try {
			conn = getConnection();
			stmt = conn.prepareStatement("INSERT INTO RESTRICAO_TREINAMENTO VALUES (?,?)");
			for(Funcao funcao : listFuncao){
				stmt.setLong(1, codTreinamento);
				stmt.setLong(2, funcao.getCodigo());
				int count = stmt.executeUpdate();
				if(count == 0){
					return false;
				}
			}
		}
		finally {
			closeConnection(conn, stmt, null);
		}		
		return true;
	}

	@Override
	public List<TreinamentoColaborador> getVisualizacoesByTreinamento(Long codTreinamento, Long codUnidade) throws SQLException{
		Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rSet = null;
        TreinamentoColaborador tColaborador = null;
        Colaborador colaborador = null;
        List<TreinamentoColaborador> colaboradores = new ArrayList<>();
        try{
            conn = getConnection();
            stmt = conn.prepareStatement("SELECT TC.cod_treinamento, TC.data_visualizacao, C.cpf, C.nome " +
                    "FROM treinamento T JOIN restricao_treinamento RT ON T.codigo = RT.cod_treinamento\n" +
                    "LEFT JOIN colaborador C ON C.cod_unidade = T.cod_unidade AND C.cod_funcao = RT.cod_funcao " +
                    "AND C.status_ativo = TRUE LEFT JOIN treinamento_colaborador TC ON TC.cod_treinamento = T.codigo " +
                    "AND TC.cpf_colaborador = C.cpf WHERE T.cod_unidade = ? AND T.CODIGO = ? \n" +
                    "ORDER BY C.nome");
            stmt.setLong(1, codUnidade);
            stmt.setLong(2, codTreinamento);
            rSet = stmt.executeQuery();
            while (rSet.next()){
                tColaborador = new TreinamentoColaborador();
                colaborador = new Colaborador();
                colaborador.setCpf(rSet.getLong("cpf"));
                colaborador.setNome(rSet.getString("nome"));
                tColaborador.setColaborador(colaborador);
                tColaborador.setDataVisualizacao(rSet.getDate("data_visualizacao"));
                colaboradores.add(tColaborador);
            }
            return colaboradores;
        }finally {
            closeConnection(conn,stmt,rSet);
        }
    }

}
