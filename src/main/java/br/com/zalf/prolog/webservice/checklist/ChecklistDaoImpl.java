package br.com.zalf.prolog.webservice.checklist;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import br.com.zalf.prolog.models.Colaborador;
import br.com.zalf.prolog.models.Funcao;
import br.com.zalf.prolog.models.checklist.Checklist;
import br.com.zalf.prolog.models.checklist.ModeloChecklist;
import br.com.zalf.prolog.models.checklist.NovoChecklistHolder;
import br.com.zalf.prolog.models.checklist.PerguntaRespostaChecklist;
import br.com.zalf.prolog.models.checklist.VeiculoLiberacao;
import br.com.zalf.prolog.models.util.DateUtils;
import br.com.zalf.prolog.webservice.DatabaseConnection;
import br.com.zalf.prolog.webservice.veiculo.VeiculoDaoImpl;

public class ChecklistDaoImpl extends DatabaseConnection implements ChecklistDao{

	VeiculoDaoImpl veiculoDao;

	/**
	 * Insere um checklist no BD salvando na tabela CHECKLIST e chamando métodos
	 * especificos que salvam as respostas do map na tabela CHECKLIST_RESPOSTAS
	 * 
	 * @return boolean
	 * @version 1.0
	 * @since 7 de dez de 2015 13:52:18
	 * @author Luiz Felipe
	 */

	public boolean insert(Checklist checklist) throws SQLException {
		Connection conn = null;
		PreparedStatement stmt = null;
		ResultSet rSet = null;
		veiculoDao = new VeiculoDaoImpl();
		try {
			conn = getConnection();
			stmt = conn.prepareStatement("INSERT INTO CHECKLIST "
					+ "(COD, UNIDADE, COD_MODELO_CHECKLIST, DATA_HORA, CPF_COLABORADOR, PLACA_VEICULO, TIPO, KM_VEICULO, TEMPO_REALIZACAO) "
					+ "VALUES ((SELECT COD_UNIDADE, FROM VEICULO WHERE PLACA = ?),?,?,?,?,?,?,?) RETURNING CODIGO");						
			stmt.setString(1, checklist.getPlacaVeiculo());
			stmt.setLong(2, checklist.getCodModelo());
			stmt.setTimestamp(3, DateUtils.toTimestamp(checklist.getData()));
			stmt.setLong(4, checklist.getColaborador().getCpf());
			stmt.setString(5, checklist.getPlacaVeiculo());
			stmt.setString(6, String.valueOf(checklist.getTipo()));
			stmt.setLong(7, checklist.getKmAtualVeiculo());
			stmt.setLong(8, checklist.getTempoRealizacaoCheckInMillis());
			rSet = stmt.executeQuery();
			if (rSet.next()) {
				checklist.setCodigo(rSet.getLong("CODIGO"));
				insertRespostas(checklist);
				insertItemManutencao(checklist, conn);
				veiculoDao.updateKmByPlaca(checklist.getPlacaVeiculo(), checklist.getKmAtualVeiculo());
			}else{
				throw new SQLException("Erro ao inserir o checklist");
			}
		}
		finally {
			closeConnection(conn, stmt, rSet);
		}		
		return true;
	}


	/**
	 * Método para inserir itens com apontados como problema no checklist em uma tabela destinada ao controle de manutenção
	 * @param checklist um Checklist
	 * @throws SQLException caso não seja possível realizar as buscas e inserts
	 */
	public void insertItemManutencao(Checklist checklist, Connection conn) throws SQLException{
		PreparedStatement stmt = null;
		ResultSet rSet = null;
		try{
			// verifica se já existe item em aberto na tabela manutenção
			stmt = conn.prepareStatement("SELECT * FROM CHECKLIST_MANUTENCAO CM WHERE PLACA = ? AND ITEM = ? AND COD_CHECKLIST_MODELO = ? "
					+ "AND DATA_RESOLUCAO IS NULL");
			for (PerguntaRespostaChecklist resposta : checklist.getListRespostas()) {
				// verifica apenas os itens cuja resposta foi negativa (tem problema)
				if(respostaTemProblema(resposta)){
					stmt.setString(1, checklist.getPlacaVeiculo());
					stmt.setLong(2, resposta.getCodigo());
					stmt.setLong(3, checklist.getCodModelo());
					rSet = stmt.executeQuery();
					if(rSet.next()){ //caso o item já exista e ainda não tenha sido resolvido, devemos incrementar a coluna qt_apontamentos
						int tempApontamentos = rSet.getInt("QT_APONTAMENTOS");
						tempApontamentos += 1;
						updateQtApontamentos(checklist.getPlacaVeiculo(), checklist.getCodModelo(), resposta.getCodigo(), tempApontamentos, conn);
					}else{ //item não existe, incluir na lista de manutenção
						insertApontamento(checklist.getPlacaVeiculo(),checklist.getCodModelo(), resposta.getCodigo(), DateUtils.toTimestamp(checklist.getData()), conn);
					}
				}
			}
		}finally{
			closeConnection(conn, stmt, rSet);
		}
	}
	// Verifica se alguma alternativa da pergunta foi marcada
	private boolean respostaTemProblema(PerguntaRespostaChecklist perguntaRespostaChecklist){
		//Percorre a lista de alternativas de uma pergunta, se alguma estiver selecionada retorna true.
		for(PerguntaRespostaChecklist.Alternativa alternativa : perguntaRespostaChecklist.getAlternativasResposta()){
			if(alternativa.selected == true){
				return true;
			}
		}
		return false;
	}


	public void insertApontamento(String placa, long codModelo, long codPergunta, Timestamp dataApontamento, Connection conn) throws SQLException{
		PreparedStatement stmt = null;
		stmt = conn.prepareStatement("INSERT INTO CHECKLIST_MANUTENCAO VALUES ((SELECT COD_UNIDADE FROM VEICULO WHERE PLACA=?),?,?,?,?)");
		stmt.setString(1, placa);
		stmt.setLong(2, codModelo);
		stmt.setTimestamp(3, dataApontamento);
		stmt.setString(4, placa);
		stmt.setLong(5, codPergunta);
		int count = stmt.executeUpdate();
		if(count == 0){
			throw new SQLException("Erro ao inserir item na tabela de manutenção");
		}
		closeConnection(null, stmt, null);
	}

	public void updateQtApontamentos(String placa, Long codModelo, long codPergunta, int apontamentos, Connection conn) throws SQLException{
		Connection connection = conn;
		PreparedStatement stmt = null;
		stmt = conn.prepareStatement("UPDATE CHECKLIST_MANUTENCAO SET QT_APONTAMENTOS = ? WHERE PLACA = ? AND ITEM = ? AND COD_UNIDADE = ? "
				+ "AND COD_CHECKLIST_MODELO = (SELECT COD_UNIDADE FROM VEICULO WHERE PLACA = ?) AND DATA_RESOLUCAO IS NULL");
		stmt.setInt(1, apontamentos);
		stmt.setString(2, placa);
		stmt.setLong(3, codPergunta);
		stmt.setString(4, placa);
		stmt.setLong(5, codModelo);
		int count = stmt.executeUpdate();
		if(count == 0){
			throw new SQLException("Erro ao atualizar a quantidade de apontamentos");
		}
		closeConnection(null, stmt, null);
	}

	// implementar novamente, seguindo os padrões do novo check
	@Override
	public boolean update(Checklist checklist) throws SQLException {
		Connection conn = null;
		PreparedStatement stmt = null;
		try {
			conn = getConnection();
			stmt = conn.prepareStatement("UPDATE CHECKLIST SET DATA_HORA = ?, "
					+ "CPF_COLABORADOR = ?, PLACA_VEICULO = ?, TIPO = ? "
					+ "WHERE CODIGO = ?");
			//			stmt.setTimestamp(1, DateUtils.toTimestamp(checklist.getData()));
			//			stmt.setLong(2, checklist.getCpfColaborador());
			//			stmt.setString(3, checklist.getPlacaVeiculo());
			//			stmt.setString(4, String.valueOf(checklist.getTipo()));
			//			stmt.setLong(5, checklist.getCodigo());
			//			int count = stmt.executeUpdate();
			//			if(count == 0){
			//				throw new SQLException("Erro ao atualizar o checklist");
			//			}
			//			updateRespostas(checklist);
		}
		finally {
			closeConnection(conn, stmt, null);
		}		
		return true;
	}

	@Override
	public boolean delete(long codChecklist) throws SQLException {
		Connection conn = null;
		PreparedStatement stmt = null;
		try {
			conn = getConnection();
			stmt = conn.prepareStatement("DELETE FROM CHECKLIST WHERE CODIGO = ?");
			stmt.setLong(1, codChecklist);
			return (stmt.executeUpdate() > 0);
		} finally {
			closeConnection(conn, stmt, null);
		}
	}

	@Override
	public Checklist getByCod(long codChecklist) throws SQLException {
		Connection conn = null;
		PreparedStatement stmt = null;
		ResultSet rSet = null;
		try {
			conn = getConnection();
			stmt = conn.prepareStatement("SELECT C.CODIGO, C.COD_CHECKLIST_MODELO, C.DATA_HORA, C.KM_VEICULO, "
					+ "C.TEMPO_REALIZACAO,C.CPF_COLABORADOR, C.PLACA_VEICULO, "
					+ "C.TIPO, CO.NOME FROM CHECKLIST C JOIN COLABORADOR CO ON CO.CPF = C.CPF_COLABORADOR "
					+ "WHERE C.CODIGO =  ? ");
			stmt.setLong(1, codChecklist);
			rSet = stmt.executeQuery();
			if (rSet.next()) {
				Checklist checklist = createChecklist(rSet);
				return checklist;
			}
		} finally {
			closeConnection(conn, stmt, rSet);
		}
		return null;
	}

	@Override
	public List<Checklist> getAll(LocalDate dataInicial, LocalDate dataFinal, String equipe,
			Long codUnidade, long limit, long offset) throws SQLException {

		List<Checklist> checklists = new ArrayList<>();
		//TODO verificar token e buscar apenas checklists da unidade informada no request
		Connection conn = null;
		PreparedStatement stmt = null;
		ResultSet rSet = null;

		System.out.println(dataInicial + " data final: ");
		System.out.println(dataFinal);
		try {
			conn = getConnection();
			stmt = conn.prepareStatement("SELECT C.CODIGO, C.DATA_HORA, C.KM_VEICULO, "
					+ "C.TEMPO_REALIZACAO,C.CPF_COLABORADOR, C.PLACA_VEICULO, "
					+ "C.TIPO, CO.NOME FROM CHECKLIST C JOIN COLABORADOR CO ON CO.CPF = C.CPF_COLABORADOR "
					+ "JOIN EQUIPE E ON E.CODIGO = CO.COD_EQUIPE "
					+ "WHERE C.DATA_HORA::DATE >= ? "
					+ "AND C.DATA_HORA::DATE <= ? "
					+ "AND E.NOME LIKE ? "
					+ "AND CO.COD_UNIDADE = ? "
					+ "ORDER BY DATA_HORA DESC "
					+ "LIMIT ? OFFSET ?");

			stmt.setDate(1, DateUtils.toSqlDate(dataInicial));
			stmt.setDate(2, DateUtils.toSqlDate(dataFinal));
			stmt.setString(3, equipe);
			stmt.setLong(4, codUnidade);
			stmt.setLong(5, limit);
			stmt.setLong(6, offset);
			rSet = stmt.executeQuery();
			while (rSet.next()) {
				Checklist checklist = createChecklist(rSet);
				checklists.add(checklist);
			}
		} finally {
			closeConnection(conn, stmt, rSet);
		}
		return checklists;
	}

	@Override
	public List<Checklist> getByColaborador(Long cpf, int limit, long offset) throws SQLException {
		List<Checklist> checklists = new ArrayList<>();
		Connection conn = null;
		PreparedStatement stmt = null;
		ResultSet rSet = null;
		try {
			conn = getConnection();
			stmt = conn.prepareStatement("SELECT C.CODIGO, C.DATA_HORA, "
					+ "C.CPF_COLABORADOR, C.PLACA_VEICULO, C.KM_VEICULO, C.TIPO , C.TEMPO_REALIZACAO, CO.NOME FROM CHECKLIST C "
					+ "JOIN COLABORADOR CO ON CO.CPF = C.CPF_COLABORADOR "
					+ "WHERE C.CPF_COLABORADOR = ? "
					+ "ORDER BY C.DATA_HORA DESC "
					+ "LIMIT ? OFFSET ?");
			stmt.setLong(1, cpf);
			stmt.setInt(2, limit);
			stmt.setLong(3, offset);
			rSet = stmt.executeQuery();
			while (rSet.next()) {
				Checklist checklist = createChecklist(rSet);
				checklists.add(checklist);
			}
		} finally {
			closeConnection(conn, stmt, rSet);
		}
		return checklists;
	}

	/**
	 * Busca as perguntas e suas respectivas alternativas.
	 * @param codUnidade codigo da unidade a ser realizada a busca
	 * @return lista de PerguntaRespostaChecklist
	 * @throws SQLException caso não seja possivel realizar a busca
	 */
	@Override
	public List<PerguntaRespostaChecklist> getPerguntas(Long codUnidade, Long codModelo) throws SQLException {
		List<PerguntaRespostaChecklist> perguntas = new ArrayList<>();
		List<PerguntaRespostaChecklist.Alternativa> alternativas = new ArrayList<>();
		PerguntaRespostaChecklist pergunta = new PerguntaRespostaChecklist();
		PerguntaRespostaChecklist.Alternativa alternativa =  new PerguntaRespostaChecklist.Alternativa();
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
					+ "ORDER BY CP.ORDEM, CAP.ORDEM", ResultSet.TYPE_SCROLL_SENSITIVE,
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

	public Map<Long, String> getModelosChecklistByCodUnidadeByCodFuncao(Long codUnidade, Long codFuncao) throws SQLException{
		Map<Long, String> mapModelos = new LinkedHashMap<>();

		Connection conn = null;
		PreparedStatement stmt = null;
		ResultSet rSet = null;

		try {
			conn = getConnection();
			stmt = conn.prepareStatement("SELECT CM.NOME AS MODELO, CM.CODIGO AS COD_MODELO "
					+ "FROM CHECKLIST_MODELO_FUNCAO CMF JOIN CHECKLIST_MODELO CM ON CM.COD_UNIDADE = CMF.COD_UNIDADE AND CM.CODIGO = CMF.COD_CHECKLIST_MODELO "
					+ "WHERE CMF.COD_UNIDADE = ? AND CMF.COD_FUNCAO = ? AND CM.STATUS_ATIVO = TRUE "
					+ "ORDER BY MODELO");
			stmt.setLong(1, codUnidade);
			stmt.setLong(2, codFuncao);
			rSet = stmt.executeQuery();
			while (rSet.next()) {
				mapModelos.put(rSet.getLong("COD_MODELO"), rSet.getString("MODELO"));
			}
		} finally {
			closeConnection(conn, stmt, rSet);
		}
		return mapModelos;
	}

	public Map<Long, String> getAllModelosChecklistByCodUnidade(Long codUnidade) throws SQLException{
		Map<Long, String> mapModelos = new LinkedHashMap<>();

		Connection conn = null;
		PreparedStatement stmt = null;
		ResultSet rSet = null;

		try {
			conn = getConnection();
			stmt = conn.prepareStatement("SELECT CM.NOME AS MODELO, CM.CODIGO AS COD_MODELO "
					+ "FROM CHECKLIST_MODELO_FUNCAO CMF JOIN CHECKLIST_MODELO CM ON CM.COD_UNIDADE = CMF.COD_UNIDADE AND CM.CODIGO = CMF.COD_CHECKLIST_MODELO "
					+ "WHERE CMF.COD_UNIDADE = ?"
					+ "ORDER BY MODELO");
			stmt.setLong(1, codUnidade);
			rSet = stmt.executeQuery();
			while (rSet.next()) {
				mapModelos.put(rSet.getLong("COD_MODELO"), rSet.getString("MODELO"));
			}
		} finally {
			closeConnection(conn, stmt, rSet);
		}
		return mapModelos;
	}

	public List<String> getUrlImagensPerguntas(Long codUnidade, Long codFuncao) throws SQLException {

		List<String> listUrl = new ArrayList<>();
		Connection conn = null;
		PreparedStatement stmt = null;
		ResultSet rSet = null;

		try {
			conn = getConnection();
			stmt = conn.prepareStatement("SELECT DISTINCT CP.URL_IMAGEM FROM CHECKLIST_MODELO_FUNCAO CMF "
					+ "JOIN CHECKLIST_PERGUNTAS CP ON CP.COD_UNIDADE = CMF.COD_UNIDADE "
					+ "AND CP.COD_CHECKLIST_MODELO = CMF.COD_CHECKLIST_MODELO "
					+ "WHERE CMF.COD_UNIDADE = ? "
					+ "AND CMF.COD_FUNCAO = ? "
					+ "AND CP.STATUS_ATIVO = TRUE");

			stmt.setLong(1, codUnidade);
			stmt.setLong(2, codFuncao);
			rSet = stmt.executeQuery();
			while (rSet.next()) {
				listUrl.add(rSet.getString("URL_IMAGEM"));
			}
		} finally {
			closeConnection(conn, stmt, rSet);
		}
		return listUrl;
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

	private PerguntaRespostaChecklist.Alternativa createAlternativa(ResultSet rSet) throws SQLException{
		PerguntaRespostaChecklist.Alternativa alternativa = new PerguntaRespostaChecklist.Alternativa();
		alternativa.codigo = rSet.getLong("COD_ALTERNATIVA");
		alternativa.alternativa = rSet.getString("ALTERNATIVA");
		if(alternativa.alternativa.equals("Outros")){
			alternativa.tipo = PerguntaRespostaChecklist.Alternativa.TIPO_OUTROS;
		}
		return alternativa;
	}

	/**
	 * Método responsável por salvar as respostas de um checklist na tabela
	 * CHECKLIST_RESPOSTAS. As respostas e perguntas de um checklist vêm em um 
	 * map<pergunta, resposta> então precisamos percorrer todo esse map para
	 * adicionar todas as respostas de um checklist ao BD.
	 * 
	 * @return void
	 * @version 1.0
	 * @since 7 de dez de 2015 14:01:03
	 * @author Luiz Felipe
	 */
	private void insertRespostas(Checklist checklist) throws SQLException {
		Connection conn = null;
		PreparedStatement stmt = null;
		try {
			conn = getConnection();
			stmt = conn.prepareStatement("INSERT INTO CHECKLIST_RESPOSTAS "
					+ "(COD_UNIDADE, COD_CHECKLIST_MODELO, COD_CHECKLIST, COD_PERGUNTA, COD_ALTERNATIVA, RESPOSTA) "
					+ "VALUES ((SELECT V.COD_UNIDADE FROM VEICULO V WHERE V.PLACA=?), ?, ?, ?, ?)");
			for (PerguntaRespostaChecklist resposta : checklist.getListRespostas()) {
				stmt.setString(1, checklist.getPlacaVeiculo());
				stmt.setLong(2, checklist.getCodModelo());
				stmt.setLong(3, checklist.getCodigo());
				stmt.setLong(4, resposta.getCodigo());
				for(PerguntaRespostaChecklist.Alternativa alternativa : resposta.getAlternativasResposta()){
					stmt.setLong(5, alternativa.codigo);
					//se a alternativa esta selecionada
					if(alternativa.selected == true){
						// se a alternativa é do tipo Outros
						if(alternativa.tipo == PerguntaRespostaChecklist.Alternativa.TIPO_OUTROS){
							// salva a resposta escrita do usuário
							stmt.setString(6, alternativa.respostaOutros);
						}else{
							// se a alternativa esta MARCADA e não é do tipo Outros
							stmt.setString(6, "NOK");
						}
						// alternativa esta desmarcada
					}else{
						// salva OK, indicando que o item NÃO tem problema
						stmt.setString(6, "OK");
					}
					stmt.executeUpdate();
				}
			}
		} finally {
			closeConnection(conn, stmt, null);
		}
	}

	/*	private void updateRespostas(Checklist checklist) throws SQLException {
			Connection conn = null;
			PreparedStatement stmt = null;
			try {
				conn = getConnection();
				stmt = conn.prepareStatement("UPDATE CHECKLIST_RESPOSTAS SET "
						+ "COD_PERGUNTA = ?, RESPOSTA = ? WHERE COD_CHECKLIST = ?");
				for (Map.Entry<Pergunta, Resposta> entry : checklist.getPerguntaRespostaMap().entrySet()) {
					Pergunta pergunta = entry.getKey();
					Resposta resposta = entry.getValue();
					stmt.setLong(1, pergunta.getCodigo());
					stmt.setString(2, resposta.getResposta());
					stmt.setLong(3, checklist.getCodigo());
					stmt.executeUpdate();
				}
			} finally {
				closeConnection(conn, stmt, null);
			}
		}*/

	private Checklist createChecklist(ResultSet rSet) throws SQLException {
		Checklist checklist = new Checklist();
		checklist.setCodigo(rSet.getLong("CODIGO"));
		checklist.setCodModelo(rSet.getLong("COD_CHECKLIST_MODELO"));
		checklist.setColaborador(createColaborador(rSet.getLong("CPF_COLABORADOR"), rSet.getString("NOME")));
		checklist.setData(rSet.getTimestamp("DATA_HORA"));
		checklist.setPlacaVeiculo(rSet.getString("PLACA_VEICULO"));
		checklist.setTipo(rSet.getString("TIPO").charAt(0));
		checklist.setKmAtualVeiculo(rSet.getLong("KM_VEICULO"));
		checklist.setTempoRealizacaoCheckInMillis(rSet.getLong("TEMPO_REALIZACAO"));
		createPerguntasRespostas(checklist);
		return checklist;
	}

	private Colaborador createColaborador(Long cpf, String nome){
		Colaborador colaborador = new Colaborador();
		colaborador.setCpf(cpf);
		colaborador.setNome(nome);
		return colaborador;
	}

	private void createPerguntasRespostas(Checklist checklist) throws SQLException {
		Connection conn = null;
		PreparedStatement stmt = null;
		ResultSet rSet = null;
		List<PerguntaRespostaChecklist> perguntas = new ArrayList<>();
		List<PerguntaRespostaChecklist.Alternativa> alternativas = new ArrayList<>();
		PerguntaRespostaChecklist pergunta = new PerguntaRespostaChecklist();
		PerguntaRespostaChecklist.Alternativa alternativa =  new PerguntaRespostaChecklist.Alternativa();
		try {
			conn = getConnection();
			stmt = conn.prepareStatement("SELECT CP.CODIGO AS COD_PERGUNTA, CP.ORDEM AS ORDEM_PERGUNTA, "
					+ "CP.PERGUNTA, CP.SINGLE_CHOICE,CAP.CODIGO AS COD_ALTERNATIVA, "
					+ "CAP.ORDEM, CP.URL_IMAGEM, CAP.ALTERNATIVA, CR.RESPOSTA  "
					+ "FROM CHECKLIST C JOIN CHECKLIST_RESPOSTAS CR ON C.CODIGO = CR.COD_CHECKLIST AND CR.COD_CHECKLIST_MODELO = C.COD_CHECKLIST_MODELO "
					+ "JOIN CHECKLIST_PERGUNTAS CP ON CP.CODIGO = CR.COD_PERGUNTA AND CP.COD_UNIDADE = CR.COD_UNIDADE AND "
					+ "CP.COD_CHECKLIST_MODELO = CR.COD_CHECKLIST_MODELO JOIN "
					+ "CHECKLIST_ALTERNATIVA_PERGUNTA CAP ON CAP.CODIGO = CR.COD_ALTERNATIVA AND CAP.COD_UNIDADE = CR.COD_UNIDADE "
					+ "AND CAP.COD_CHECKLIST_MODELO = CR.COD_CHECKLIST_MODELO "
					+ "WHERE C.CODIGO = ? AND C.CPF_COLABORADOR = ? "
					+ "ORDER BY CP.ORDEM, CAP.ORDEM", ResultSet.TYPE_SCROLL_SENSITIVE,
					ResultSet.CONCUR_UPDATABLE);
			stmt.setLong(1, checklist.getCodigo());
			stmt.setLong(2, checklist.getColaborador().getCpf());
			rSet = stmt.executeQuery();
			if(rSet.first()){
				pergunta = createPergunta(rSet);
				alternativa = createAlternativa(rSet);
				setRespostaAlternativa(alternativa, rSet);
				alternativas.add(alternativa);			
			}
			while (rSet.next()) {
				if(rSet.getLong("COD_PERGUNTA") == pergunta.getCodigo()){
					alternativa = createAlternativa(rSet);
					setRespostaAlternativa(alternativa, rSet);
					alternativas.add(alternativa);			
				}else{
					pergunta.setAlternativasResposta(alternativas);
					perguntas.add(pergunta);
					alternativas = new ArrayList<>();

					pergunta = createPergunta(rSet);

					alternativa = createAlternativa(rSet);
					setRespostaAlternativa(alternativa, rSet);
					alternativas.add(alternativa);			
				}
			}
			pergunta.setAlternativasResposta(alternativas);
			perguntas.add(pergunta);

		} finally {
			closeConnection(conn, stmt, rSet);
		}
		checklist.setListRespostas(perguntas);
	}

	// remonta as alternativas de uma Pergunta
	private void setRespostaAlternativa(PerguntaRespostaChecklist.Alternativa alternativa, ResultSet rSet) throws SQLException{

		if(rSet.getString("RESPOSTA").equals("NOK")){
			alternativa.selected = true;
		}else if(rSet.getString("RESPOSTA").equals("OK")){
			alternativa.selected = false;
		}else{
			alternativa.selected = true;
			alternativa.tipo = PerguntaRespostaChecklist.Alternativa.TIPO_OUTROS;
			alternativa.respostaOutros = rSet.getString("RESPOSTA");
		}
	}

	/**
	 * Cria o objeto responsavel por permitir a criação de um novo checklist, fornece as placas ativas de uma unidade e as perguntas do checklist
	 * @param codUnidade
	 * @return
	 * @throws SQLException
	 */
	public NovoChecklistHolder getNovoChecklistHolder(Long codUnidade, Long codModelo) throws SQLException{
		NovoChecklistHolder holder = new NovoChecklistHolder();
		veiculoDao = new VeiculoDaoImpl();
		holder.setListPerguntas(getPerguntas(codUnidade, codModelo));
		holder.setListVeiculos(veiculoDao.getVeiculosAtivosByUnidade(codUnidade));
		return holder;
	}

	/**
	 * Busca uma lista de todas as placas da unidade, separando em 3 status:
	 * PENDENTE: não tem checklist realizado no dia atual e não tem itens críticos a serem arrumados
	 * NÃO LIBERADO: placa tem itens críticos que necessitam de conserto imediato, não sendo permitida a liberação do veículo
	 * LIBERADO: checklist foi realizado e não tem problemas críticos a serem resolvidos
	 * @param codUnidade
	 * @return
	 * @throws SQLException
	 */
	public List<VeiculoLiberacao> getStatusLiberacaoVeiculos(Long codUnidade) throws SQLException{
		List<VeiculoLiberacao> listVeiculos = new ArrayList<>();
		List<PerguntaRespostaChecklist> listProblemas = new ArrayList<>();
		List<String> listPlacasComCheck = new ArrayList<>();				
		Connection conn = null;
		PreparedStatement stmt = null;
		ResultSet rSet = null;
		VeiculoLiberacao veiculoLiberacao = new VeiculoLiberacao();
		PerguntaRespostaChecklist pergunta = new PerguntaRespostaChecklist();
		try {
			conn = getConnection();
			stmt = conn.prepareStatement("SELECT V.PLACA, PLACAS_MANUTENCAO.ITEM_MANUTENCAO, CHECK_HOJE.PLACA_CHECK FROM "
					+ "(SELECT DISTINCT PLACA_VEICULO AS PLACA_CHECK FROM CHECKLIST C "
					+ "JOIN VEICULO V ON V.PLACA = C.PLACA_VEICULO WHERE DATA_HORA::DATE = ? "
					+ "AND V.cod_unidade = ?) AS CHECK_HOJE RIGHT JOIN VEICULO V ON V.PLACA = PLACA_CHECK "
					+ "LEFT JOIN (SELECT PLACA AS PLACA_MANUTENCAO, CP.PERGUNTA AS ITEM_MANUTENCAO FROM CHECKLIST_MANUTENCAO CM "
					+ "JOIN CHECKLIST_PERGUNTAS CP ON CP.CODIGO = CM.ITEM AND CP.PRIORIDADE = 'CRITICA' AND CP.COD_UNIDADE = CM.COD_UNIDADE "
					+ "WHERE CM.CPF_FROTA IS NULL) AS PLACAS_MANUTENCAO ON PLACA_MANUTENCAO = V.PLACA "
					+ "WHERE V.COD_UNIDADE = ? "
					+ "ORDER BY V.PLACA, PLACAS_MANUTENCAO.ITEM_MANUTENCAO", ResultSet.TYPE_SCROLL_SENSITIVE,	ResultSet.CONCUR_UPDATABLE);
			stmt.setDate(1, DateUtils.toSqlDate(new Date(System.currentTimeMillis())));
			stmt.setLong(2, codUnidade);
			stmt.setLong(3, codUnidade);
			rSet = stmt.executeQuery();

			if(rSet.first()){
				pergunta.setPergunta(rSet.getString("ITEM_MANUTENCAO"));
				veiculoLiberacao.setPlaca(rSet.getString("PLACA"));
				if(pergunta.getPergunta() != null){
					listProblemas.add(pergunta);}
				if(rSet.getString("PLACA_CHECK") != null){
					listPlacasComCheck.add(rSet.getString("PLACA_CHECK"));}
			}
			while (rSet.next()) {
				if(rSet.getString("PLACA").equals(veiculoLiberacao.getPlaca())){
					pergunta = new PerguntaRespostaChecklist();
					pergunta.setPergunta(rSet.getString("ITEM_MANUTENCAO"));
					if(pergunta.getPergunta() != null){
						listProblemas.add(pergunta);}	
				}else{
					if(rSet.getString("PLACA_CHECK") != null){
						listPlacasComCheck.add(rSet.getString("PLACA_CHECK"));}
					veiculoLiberacao.setItensCriticos(listProblemas);
					listVeiculos.add(veiculoLiberacao);
					listProblemas = new ArrayList<>();

					pergunta = new PerguntaRespostaChecklist();
					pergunta.setPergunta(rSet.getString("ITEM_MANUTENCAO"));

					veiculoLiberacao = new VeiculoLiberacao();
					veiculoLiberacao.setPlaca(rSet.getString("PLACA"));
					if(pergunta.getPergunta() != null){
						listProblemas.add(pergunta);}
				}
			}
			veiculoLiberacao.setItensCriticos(listProblemas);
			listVeiculos.add(veiculoLiberacao);
		}
		finally{
			closeConnection(conn, stmt, rSet);
		}
		setStatusLiberacao(listVeiculos, listPlacasComCheck);
		return listVeiculos;
	}

	private void setStatusLiberacao(List<VeiculoLiberacao> list, List<String> listPlacasComCheck){
		for(VeiculoLiberacao veiculoLiberacao : list){
			if(veiculoLiberacao.getItensCriticos().size() > 0){
				veiculoLiberacao.setStatus(VeiculoLiberacao.STATUS_NAO_LIBERADO);
			}else{

				if(placaTemCheck(veiculoLiberacao.getPlaca(), listPlacasComCheck)){
					veiculoLiberacao.setStatus(VeiculoLiberacao.STATUS_LIBERADO);
				}else{
					veiculoLiberacao.setStatus(VeiculoLiberacao.STATUS_PENDENTE);
				}
			}
		}
	}

	private boolean placaTemCheck (String placa, List<String> listPlacasComCheck){
		List<String> listPlaca = new ArrayList<>();
		listPlaca = listPlacasComCheck.stream().filter(c -> c.equals(placa)).collect(Collectors.toCollection(ArrayList::new));
		if(listPlaca.isEmpty()){
			return false;
		}else{
			return true;
		}
	}

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
			}

			insertModeloFuncao(conn, modeloChecklist);
			insertPerguntasModelo(conn, modeloChecklist);
			insertAlternativas(conn, modeloChecklist);

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

	private void insertPerguntasModelo(Connection conn, ModeloChecklist modeloChecklist) throws SQLException{
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

	private void insertAlternativas(Connection conn, ModeloChecklist modeloChecklist) throws SQLException{
		PreparedStatement stmt = null;
		ResultSet rSet = null;
		for(PerguntaRespostaChecklist pergunta : modeloChecklist.getListPerguntas()){

			for(PerguntaRespostaChecklist.Alternativa alternativa : pergunta.getAlternativasResposta()){

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


