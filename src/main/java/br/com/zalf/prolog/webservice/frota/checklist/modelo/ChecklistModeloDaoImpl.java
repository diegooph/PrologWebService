package br.com.zalf.prolog.webservice.frota.checklist.modelo;

import br.com.zalf.prolog.webservice.commons.colaborador.Funcao;
import br.com.zalf.prolog.webservice.commons.veiculo.TipoVeiculo;
import br.com.zalf.prolog.webservice.frota.checklist.model.AlternativaChecklist;
import br.com.zalf.prolog.webservice.frota.checklist.model.PerguntaRespostaChecklist;
import br.com.zalf.prolog.webservice.DatabaseConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class ChecklistModeloDaoImpl extends DatabaseConnection implements ChecklistModeloDao {

	@Override
	public List<PerguntaRespostaChecklist> getPerguntas(Long codUnidade, Long codModelo) throws SQLException {
		List<PerguntaRespostaChecklist> perguntas = new ArrayList<>();
		List<AlternativaChecklist> alternativas = new ArrayList<>();
		PerguntaRespostaChecklist pergunta = new PerguntaRespostaChecklist();
		AlternativaChecklist alternativa =  new AlternativaChecklist();
		Connection conn = null;
		PreparedStatement stmt = null;
		ResultSet rSet = null;

		try {
			conn = getConnection();
			stmt = conn.prepareStatement("SELECT CP.CODIGO AS COD_PERGUNTA,CP.PRIORIDADE, CP.URL_IMAGEM, CP.PERGUNTA, CP.ORDEM AS ORDEM_PERGUNTA, "
					+ "CP.SINGLE_CHOICE, CAP.CODIGO AS COD_ALTERNATIVA, "
					+ "CAP.ALTERNATIVA, CAP.ORDEM AS ORDEM_ALTERNATIVA "
					+ "FROM CHECKLIST_PERGUNTAS CP "
					+ "JOIN CHECKLIST_ALTERNATIVA_PERGUNTA CAP ON CP.CODIGO = CAP.COD_PERGUNTA AND CAP.COD_UNIDADE = CP.COD_UNIDADE "
					+ "AND CAP.COD_CHECKLIST_MODELO = CP.COD_CHECKLIST_MODELO "
					+ "WHERE CP.COD_UNIDADE = ? AND CP.COD_CHECKLIST_MODELO = ? AND CP.STATUS_ATIVO = TRUE "
					+ "ORDER BY CP.ORDEM, Cp.PERGUNTA, CAP.ORDEM", ResultSet.TYPE_SCROLL_SENSITIVE,
					ResultSet.CONCUR_UPDATABLE);
			stmt.setLong(1, codUnidade);
			stmt.setLong(2, codModelo);
			rSet = stmt.executeQuery();
			if(rSet.first()){
				pergunta = createPergunta(rSet);
				alternativa = createAlternativa(rSet);
				alternativas.add(alternativa);			
			}
			while (rSet.next()) {
				if(rSet.getLong("COD_PERGUNTA") == pergunta.getCodigo()){
					alternativa = createAlternativa(rSet);
					alternativas.add(alternativa);			
				}else{
					pergunta.setAlternativasResposta(alternativas);
					perguntas.add(pergunta);
					alternativas = new ArrayList<>();

					pergunta = createPergunta(rSet);

					alternativa = createAlternativa(rSet);
					alternativas.add(alternativa);			
				}
			}
			pergunta.setAlternativasResposta(alternativas);
			perguntas.add(pergunta);
		} finally {
			closeConnection(conn, stmt, rSet);
		}
		return perguntas;
	}

	@Override
	public List<ModeloChecklist> getModelosChecklistByCodUnidadeByCodFuncao(Long codUnidade, String codFuncao) throws SQLException{

		Connection conn = null;
		PreparedStatement stmt = null;
		ResultSet rSet = null;
		List<ModeloChecklist> listModelos = new ArrayList<>();

		try {
			conn = getConnection();
			stmt = conn.prepareStatement("SELECT DISTINCT CM.NOME AS MODELO, CM.CODIGO AS COD_MODELO "
					+ "FROM CHECKLIST_MODELO_FUNCAO CMF JOIN CHECKLIST_MODELO CM ON CM.COD_UNIDADE = CMF.COD_UNIDADE AND CM.CODIGO = CMF.COD_CHECKLIST_MODELO "
					+ "WHERE CMF.COD_UNIDADE = ? AND CMF.COD_FUNCAO::TEXT LIKE ? AND CM.STATUS_ATIVO = TRUE "
					+ "ORDER BY MODELO");
			stmt.setLong(1, codUnidade);
			stmt.setString(2, codFuncao);
			rSet = stmt.executeQuery();
			while (rSet.next()) {
				ModeloChecklist modeloChecklist = new ModeloChecklist();
				modeloChecklist.setCodigo(rSet.getLong("COD_MODELO"));
				modeloChecklist.setNome(rSet.getString("MODELO"));
				listModelos.add(modeloChecklist);
			}
		} finally {
			closeConnection(conn, stmt, rSet);
		}
		return listModelos;
	}

	@Override
	public ModeloChecklist getModeloChecklist(Long codModelo, Long codUnidade) throws SQLException{
		Connection conn = null;
		PreparedStatement stmt = null;
		ResultSet rSet = null;
		ModeloChecklist  modeloChecklist = null;
		try {
			conn = getConnection();
			stmt = conn.prepareStatement("SELECT DISTINCT CM.NOME AS MODELO, CM.CODIGO AS COD_MODELO "
					+ "FROM CHECKLIST_MODELO_FUNCAO CMF JOIN CHECKLIST_MODELO CM ON CM.COD_UNIDADE = CMF.COD_UNIDADE AND CM.CODIGO = CMF.COD_CHECKLIST_MODELO "
					+ "WHERE CMF.COD_UNIDADE = ? AND CM.CODIGO = ? "
					+ "ORDER BY MODELO");
			stmt.setLong(1, codUnidade);
			stmt.setLong(2, codModelo);
			rSet = stmt.executeQuery();
			if (rSet.next()) {
				modeloChecklist = new ModeloChecklist();
				modeloChecklist.setCodigo(rSet.getLong("COD_MODELO"));
				modeloChecklist.setNome(rSet.getString("MODELO"));
				modeloChecklist.setListPerguntas(getPerguntas(codUnidade, codModelo));
				modeloChecklist.setListTipoVeiculo(getTipoVeiculoByCodModeloChecklist(codUnidade, codModelo));
				modeloChecklist.setListFuncao(getFuncaoByCodModelo(codUnidade, codModelo));

			}
		} finally {
			closeConnection(conn, stmt, rSet);
		}

		return modeloChecklist;
	}

	@Override
	public boolean insertModeloChecklist(ModeloChecklist modeloChecklist) throws SQLException{

		Connection conn = null;
		PreparedStatement stmt = null;
		ResultSet rSet = null;
		try {
			conn = getConnection();
			conn.setAutoCommit(false);
			stmt = conn.prepareStatement("INSERT INTO CHECKLIST_MODELO(COD_UNIDADE, NOME, STATUS_ATIVO) VALUES (?,?,?) RETURNING CODIGO");
			stmt.setLong(1, modeloChecklist.getCodUnidade());
			stmt.setString(2, modeloChecklist.getNome());
			stmt.setBoolean(3, true);
			rSet = stmt.executeQuery();
			if(rSet.next()){
				System.out.println("Entrou no rSet.next: " + rSet.getLong("CODIGO"));
				modeloChecklist.setCodigo(rSet.getLong("CODIGO"));
				insertModeloTipoVeiculo(conn, modeloChecklist);
				insertModeloFuncao(conn, modeloChecklist);
				insertModeloPerguntas(conn, modeloChecklist);
				insertModeloAlternativas(conn, modeloChecklist);
			}
			conn.commit();
			System.out.println("comitado!");
		}catch(SQLException e){
			e.printStackTrace();
			conn.rollback();
			return false;
		}finally{
			closeConnection(conn, stmt, null);
		}
		return true;
	}

	@Override
	public boolean setModeloChecklistInativo (Long codUnidade, Long codModelo) throws SQLException{

		Connection conn = null;
		PreparedStatement stmt = null;
		ResultSet rSet = null;
		List<Funcao> listFuncao = new ArrayList<>();

		try {
			conn = getConnection();
			stmt = conn.prepareStatement("UPDATE CHECKLIST_MODELO SET STATUS_ATIVO = FALSE WHERE COD_UNIDADE  = ? AND CODIGO = ?");
			stmt.setLong(1, codUnidade);
			stmt.setLong(2, codModelo);
			int count = stmt.executeUpdate();
			if(count == 0){
				return false;
			}
		} finally {
			closeConnection(conn, stmt, rSet);
		}
		return true;
	}

	private List<TipoVeiculo> getTipoVeiculoByCodModeloChecklist(Long codUnidade, Long codModelo) throws SQLException{

		Connection conn = null;
		PreparedStatement stmt = null;
		ResultSet rSet = null;
		List<TipoVeiculo> listTipos = new ArrayList<>();

		try {
			conn = getConnection();
			stmt = conn.prepareStatement("SELECT VT.NOME AS TIPO_VEICULO, VT.CODIGO "
					+ "FROM CHECKLIST_MODELO_VEICULO_TIPO CM "
					+ "JOIN VEICULO_TIPO VT ON CM.COD_UNIDADE = VT.COD_UNIDADE "
					+ "AND CM.COD_TIPO_VEICULO = VT.CODIGO "
					+ "WHERE CM.COD_UNIDADE = ? "
					+ "AND CM.COD_MODELO = ? "
					+ "ORDER BY VT.NOME");
			stmt.setLong(1, codUnidade);
			stmt.setLong(2, codModelo);
			rSet = stmt.executeQuery();
			while (rSet.next()) {
				TipoVeiculo tipo = new TipoVeiculo();
				tipo.setCodigo(rSet.getLong("CODIGO"));
				tipo.setNome(rSet.getString("TIPO_VEICULO"));
				listTipos.add(tipo);
			}
		} finally {
			closeConnection(conn, stmt, rSet);
		}
		return listTipos;
	}

	private List<Funcao> getFuncaoByCodModelo(Long codUnidade, Long codModelo) throws SQLException{

		Connection conn = null;
		PreparedStatement stmt = null;
		ResultSet rSet = null;
		List<Funcao> listFuncao = new ArrayList<>();

		try {
			conn = getConnection();
			stmt = conn.prepareStatement("SELECT F.CODIGO, F.NOME "
					+ "FROM CHECKLIST_MODELO_FUNCAO CM "
					+ "JOIN FUNCAO F ON F.CODIGO = CM.COD_FUNCAO "
					+ "WHERE CM.COD_UNIDADE = ? "
					+ "AND CM.COD_CHECKLIST_MODELO = ? "
					+ "ORDER BY F.NOME");
			stmt.setLong(1, codUnidade);
			stmt.setLong(2, codModelo);
			rSet = stmt.executeQuery();
			while (rSet.next()) {
				Funcao funcao = new Funcao();
				funcao.setCodigo(rSet.getLong("CODIGO"));
				funcao.setNome(rSet.getString("NOME"));
				listFuncao.add(funcao);
			}
		} finally {
			closeConnection(conn, stmt, rSet);
		}
		return listFuncao;
	}

	private PerguntaRespostaChecklist createPergunta(ResultSet rSet) throws SQLException{
		PerguntaRespostaChecklist pergunta = new PerguntaRespostaChecklist();
		pergunta.setCodigo(rSet.getLong("COD_PERGUNTA"));
		pergunta.setOrdemExibicao(rSet.getInt("ORDEM_PERGUNTA"));
		pergunta.setPergunta(rSet.getString("PERGUNTA"));
		pergunta.setSingleChoice(rSet.getBoolean("SINGLE_CHOICE"));
		pergunta.setUrl(rSet.getString("URL_IMAGEM"));
		pergunta.setPrioridade(rSet.getString("PRIORIDADE"));
		return pergunta;
	}

	private AlternativaChecklist createAlternativa(ResultSet rSet) throws SQLException{
		AlternativaChecklist alternativa = new AlternativaChecklist();
		alternativa.codigo = rSet.getLong("COD_ALTERNATIVA");
		alternativa.alternativa = rSet.getString("ALTERNATIVA");
		if(alternativa.alternativa.equals("Outros")){
			alternativa.tipo = AlternativaChecklist.TIPO_OUTROS;
		}
		return alternativa;
	}

	private void insertModeloTipoVeiculo(Connection conn, ModeloChecklist modeloChecklist) throws SQLException{
		PreparedStatement stmt = null;
		for(TipoVeiculo tipoVeiculo : modeloChecklist.getListTipoVeiculo()){
			stmt = conn.prepareStatement("INSERT INTO CHECKLIST_MODELO_VEICULO_TIPO VALUES (?,?,?)");
			stmt.setLong(1, modeloChecklist.getCodUnidade());
			stmt.setLong(2, modeloChecklist.getCodigo());
			stmt.setLong(3, tipoVeiculo.getCodigo());
			stmt.executeUpdate();
		}
	};

	private void insertModeloFuncao(Connection conn, ModeloChecklist modeloChecklist) throws SQLException{
		PreparedStatement stmt = null;
		for(Funcao funcao : modeloChecklist.getListFuncao()){
			stmt = conn.prepareStatement("INSERT INTO CHECKLIST_MODELO_FUNCAO VALUES (?,?,?)");
			stmt.setLong(1, modeloChecklist.getCodUnidade());
			stmt.setLong(2, modeloChecklist.getCodigo());
			stmt.setLong(3, funcao.getCodigo());
			stmt.executeUpdate();
		}
	};

	private void insertModeloPerguntas(Connection conn, ModeloChecklist modeloChecklist) throws SQLException{
		PreparedStatement stmt = null;
		ResultSet rSet = null;
		for(PerguntaRespostaChecklist pergunta : modeloChecklist.getListPerguntas()){

			stmt = conn.prepareStatement("INSERT INTO CHECKLIST_PERGUNTAS ("
					+ "COD_CHECKLIST_MODELO, COD_UNIDADE, ORDEM, PERGUNTA, URL_IMAGEM, "
					+ "STATUS_ATIVO, PRIORIDADE, SINGLE_CHOICE) VALUES (?,?,?,?,?,?,?,?) RETURNING CODIGO");
			stmt.setLong(1, modeloChecklist.getCodigo());
			stmt.setLong(2, modeloChecklist.getCodUnidade());
			stmt.setInt(3, pergunta.getOrdemExibicao());
			stmt.setString(4, pergunta.getPergunta());
			stmt.setString(5, pergunta.getUrl());
			stmt.setBoolean(6, true);
			stmt.setString(7, pergunta.getPrioridade());
			stmt.setBoolean(8, pergunta.isSingleChoice());
			rSet = stmt.executeQuery();
			if(rSet.next()){
				pergunta.setCodigo(rSet.getLong("CODIGO"));
			}
		}
	};

	private void insertModeloAlternativas(Connection conn, ModeloChecklist modeloChecklist) throws SQLException{
		PreparedStatement stmt = null;
		ResultSet rSet = null;
		for(PerguntaRespostaChecklist pergunta : modeloChecklist.getListPerguntas()){

			for(AlternativaChecklist alternativa : pergunta.getAlternativasResposta()){

				stmt = conn.prepareStatement("INSERT INTO CHECKLIST_ALTERNATIVA_PERGUNTA ( "
						+ "COD_CHECKLIST_MODELO, COD_UNIDADE, COD_PERGUNTA, ALTERNATIVA, ORDEM, "
						+ "STATUS_ATIVO) VALUES (?,?,?,?,?,?) RETURNING CODIGO");
				stmt.setLong(1, modeloChecklist.getCodigo());
				stmt.setLong(2, modeloChecklist.getCodUnidade());
				stmt.setLong(3, pergunta.getCodigo());
				stmt.setString(4, alternativa.alternativa);
				stmt.setInt(5, alternativa.ordemExibicao);
				stmt.setBoolean(6, true);
				stmt.executeQuery();
			}
		}
	};

}
