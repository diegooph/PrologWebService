package br.com.zalf.prolog.webservice.frota.checklist;

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
import br.com.zalf.prolog.models.checklist.Checklist;
import br.com.zalf.prolog.models.checklist.ModeloChecklist;
import br.com.zalf.prolog.models.checklist.NovoChecklistHolder;
import br.com.zalf.prolog.models.checklist.PerguntaRespostaChecklist;
import br.com.zalf.prolog.models.checklist.VeiculoLiberacao;
import br.com.zalf.prolog.models.util.DateUtils;
import br.com.zalf.prolog.webservice.DatabaseConnection;
import br.com.zalf.prolog.webservice.frota.checklistModelo.ChecklistModeloDaoImpl;
import br.com.zalf.prolog.webservice.frota.veiculo.VeiculoDaoImpl;

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
					+ "(COD_UNIDADE, COD_CHECKLIST_MODELO, DATA_HORA, CPF_COLABORADOR, PLACA_VEICULO, TIPO, KM_VEICULO, TEMPO_REALIZACAO) "
					+ "VALUES ((SELECT COD_UNIDADE FROM VEICULO WHERE PLACA = ?),?,?,?,?,?,?,?) RETURNING CODIGO");						
			stmt.setString(1, checklist.getPlacaVeiculo());
			stmt.setLong(2, checklist.getCodModelo());
			stmt.setTimestamp(3, DateUtils.toTimestamp(new Date(System.currentTimeMillis())));
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
		stmt = conn.prepareStatement("INSERT INTO CHECKLIST_MANUTENCAO(COD_UNIDADE, COD_CHECKLIST_MODELO, DATA_APONTAMENTO, PLACA, ITEM) "
				+ "VALUES ((SELECT COD_UNIDADE FROM VEICULO WHERE PLACA=?),?,?,?,?)");
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
		stmt = conn.prepareStatement("UPDATE CHECKLIST_MANUTENCAO SET QT_APONTAMENTOS = ? WHERE PLACA LIKE ? AND ITEM = ? AND "
				+ "COD_UNIDADE = (SELECT COD_UNIDADE FROM VEICULO WHERE PLACA LIKE ?) "
				+ "AND COD_CHECKLIST_MODELO = ?  AND DATA_RESOLUCAO IS NULL");
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
			stmt = conn.prepareStatement("SELECT C.CODIGO, C.COD_CHECKLIST_MODELO, C.DATA_HORA, "
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
					+ "VALUES ((SELECT V.COD_UNIDADE FROM VEICULO V WHERE V.PLACA=?), ?, ?, ?, ?, ?)");
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
					+ "CP.PERGUNTA, CP.SINGLE_CHOICE,CAP.CODIGO AS COD_ALTERNATIVA, CP.PRIORIDADE,  "
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
	public NovoChecklistHolder getNovoChecklistHolder(Long codUnidade, Long codModelo, String placa) throws SQLException{
		NovoChecklistHolder holder = new NovoChecklistHolder();
		ChecklistModeloDaoImpl checklistModeloDaoImpl = new ChecklistModeloDaoImpl();
		veiculoDao = new VeiculoDaoImpl();
		holder.setListPerguntas(checklistModeloDaoImpl.getPerguntas(codUnidade, codModelo));
		holder.setVeiculo(veiculoDao.getVeiculoByPlaca(placa, false));
		return holder;
	}

	public Map<ModeloChecklist, List<String>> getSelecaoModeloChecklistPlacaVeiculo(Long codUnidade, Long codFuncao) throws SQLException{
		Connection conn = null;
		PreparedStatement stmt = null;
		ResultSet rSet = null;
		Map<ModeloChecklist, List<String>> modeloPlaca = new LinkedHashMap<>();
		ModeloChecklist modelo = null;
		List<String> placas = new ArrayList<>();

		try{
			conn = getConnection();
			stmt = conn.prepareStatement("SELECT CM.CODIGO, CM.NOME, V.PLACA, V.KM FROM " 
					+ "CHECKLIST_MODELO CM " 
					+ "JOIN CHECKLIST_MODELO_FUNCAO CMF ON CMF.COD_CHECKLIST_MODELO = CM.CODIGO AND CM.COD_UNIDADE = CMF.COD_UNIDADE "
					+ "JOIN CHECKLIST_MODELO_VEICULO_TIPO CMVT ON CMVT.COD_MODELO = CM.CODIGO AND CMVT.COD_UNIDADE = CM.COD_UNIDADE "
					+ "JOIN VEICULO_TIPO VT ON VT.CODIGO = CMVT.COD_TIPO_VEICULO "
					+ "JOIN VEICULO V ON V.COD_TIPO = VT.CODIGO "
					+ "WHERE CM.COD_UNIDADE = ? AND CMF.COD_FUNCAO = ? "
					+ "ORDER BY CM.NOME, V.PLACA", ResultSet.TYPE_SCROLL_SENSITIVE,
					ResultSet.CONCUR_UPDATABLE);
			stmt.setLong(1, codUnidade);
			stmt.setLong(2, codFuncao);
			rSet = stmt.executeQuery();
			while(rSet.next()){
				// primeira liha do Rset, cria o modelo, add a primeira placa
				if (modelo == null){
					modelo = new ModeloChecklist();
					modelo.setCodigo(rSet.getLong("CODIGO"));
					modelo.setNome(rSet.getString("NOME"));
					placas.add(rSet.getString("PLACA"));	
				}// verificar se o prox modelo é igual ao ja criado
				if (rSet.getLong("CODIGO") == modelo.getCodigo()) {
					placas.add(rSet.getString("PLACA"));					
				}else{// modelo diferente, deve setar adicionar tudo ao map e zerar os valores.
					modeloPlaca.put(modelo, placas);
					modelo = new ModeloChecklist();
					placas = new ArrayList<>();
					modelo.setCodigo(rSet.getLong("CODIGO"));
					modelo.setNome(rSet.getString("NOME"));
					placas.add(rSet.getString("PLACA"));
				}
			}
			modeloPlaca.put(modelo, placas);
		}finally{
			closeConnection(conn, stmt, rSet);
		}	
		System.out.println(modeloPlaca);
		return modeloPlaca;
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
			while (rSet.next()) { new ModeloChecklist();
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

}


