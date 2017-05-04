package br.com.zalf.prolog.webservice.frota.checklist;

import br.com.zalf.prolog.commons.colaborador.Colaborador;
import br.com.zalf.prolog.commons.util.DateUtils;
import br.com.zalf.prolog.frota.checklist.*;
import br.com.zalf.prolog.webservice.DatabaseConnection;
import br.com.zalf.prolog.webservice.frota.checklist.modelo.ChecklistModeloDao;
import br.com.zalf.prolog.webservice.frota.checklist.modelo.ChecklistModeloDaoImpl;
import br.com.zalf.prolog.webservice.frota.checklist.ordemServico.OrdemServicoDao;
import br.com.zalf.prolog.webservice.frota.checklist.ordemServico.OrdemServicoDaoImpl;
import br.com.zalf.prolog.webservice.frota.veiculo.VeiculoDao;
import br.com.zalf.prolog.webservice.frota.veiculo.VeiculoDaoImpl;

import java.sql.*;
import java.time.LocalDate;
import java.util.*;
import java.util.Date;

public class ChecklistDaoImpl extends DatabaseConnection implements ChecklistDao {

	private static final String TAG = ChecklistDaoImpl.class.getSimpleName();
	private VeiculoDao veiculoDao;


	/**
	 * Insere um checklist no BD salvando na tabela CHECKLIST e chamando métodos
	 * especificos que salvam as respostas do map na tabela CHECKLIST_RESPOSTAS
	 * @return boolean
	 * @version 1.0
	 * @since 7 de dez de 2015 13:52:18
	 * @author Luiz Felipe
	 */
	@Override
	public boolean insert(Checklist checklist) throws SQLException {
		Connection conn = null;
		PreparedStatement stmt = null;
		ResultSet rSet = null;
		veiculoDao = new VeiculoDaoImpl();
		Long codUnidade = null;
		OrdemServicoDao osDao = new OrdemServicoDaoImpl();
		//L.d("ChecklistDaoImpl", "Chamou dao, objeto: " + checklist.toString());
		try {
			conn = getConnection();
			conn.setAutoCommit(false);
			stmt = conn.prepareStatement("INSERT INTO CHECKLIST "
					+ "(COD_UNIDADE,COD_CHECKLIST_MODELO, DATA_HORA, CPF_COLABORADOR, PLACA_VEICULO, TIPO, KM_VEICULO, TEMPO_REALIZACAO) "
					+ "VALUES ((SELECT COD_UNIDADE FROM VEICULO WHERE PLACA = ?),?,?,?,?,?,?,?) RETURNING CODIGO, COD_UNIDADE");
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
				codUnidade = rSet.getLong("cod_unidade");
				insertRespostas(checklist, conn);
				osDao.insertItemOs(checklist, conn, codUnidade);
				veiculoDao.updateKmByPlaca(checklist.getPlacaVeiculo(), checklist.getKmAtualVeiculo(), conn);
			}else{
				throw new SQLException("Erro ao inserir o checklist");
			}
			conn.commit();
		}
		finally {
			closeConnection(conn, stmt, rSet);
		}
		return true;
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
								  Long codUnidade, String placa, long limit, long offset) throws SQLException {
		List<Checklist> checklists = new ArrayList<>();
		Connection conn = null;
		PreparedStatement stmt = null;
		ResultSet rSet = null;
		try {
			conn = getConnection();
			stmt = conn.prepareStatement("SELECT C.CODIGO, C.DATA_HORA, C.cod_checklist_modelo, C.KM_VEICULO, "
					+ "C.TEMPO_REALIZACAO,C.CPF_COLABORADOR, C.PLACA_VEICULO, "
					+ "C.TIPO, CO.NOME FROM CHECKLIST C JOIN COLABORADOR CO ON CO.CPF = C.CPF_COLABORADOR "
					+ "JOIN EQUIPE E ON E.CODIGO = CO.COD_EQUIPE "
					+ "WHERE C.DATA_HORA::DATE >= ? "
					+ "AND C.DATA_HORA::DATE <= ? "
					+ "AND E.NOME LIKE ? "
					+ "AND C.COD_UNIDADE = ? "
					+ "AND C.PLACA_VEICULO LIKE ?"
					+ "ORDER BY DATA_HORA DESC "
					+ "LIMIT ? OFFSET ?");

			stmt.setDate(1, DateUtils.toSqlDate(dataInicial));
			stmt.setDate(2, DateUtils.toSqlDate(dataFinal));
			stmt.setString(3, equipe);
			stmt.setLong(4, codUnidade);
			stmt.setString(5, placa);
			stmt.setLong(6, limit);
			stmt.setLong(7, offset);
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

	@Override
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

	/**
	 * Cria o objeto responsavel por permitir a criação de um novo checklist, fornece as placas ativas de uma unidade e as perguntas do checklist
	 * @param codUnidade
	 * @return
	 * @throws SQLException
	 */
	@Override
	public NovoChecklistHolder getNovoChecklistHolder(Long codUnidade, Long codModelo, String placa) throws SQLException {
		NovoChecklistHolder holder = new NovoChecklistHolder();
		ChecklistModeloDao checklistModeloDaoImpl = new ChecklistModeloDaoImpl();
		veiculoDao = new VeiculoDaoImpl();
		holder.setListPerguntas(checklistModeloDaoImpl.getPerguntas(codUnidade, codModelo));
		holder.setVeiculo(veiculoDao.getVeiculoByPlaca(placa, false));
		return holder;
	}

	@Override
	public Map<ModeloChecklist, List<String>> getSelecaoModeloChecklistPlacaVeiculo(Long codUnidade, Long codFuncao) throws SQLException {
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
				} else {// verificar se o prox modelo é igual ao ja criado
					if (rSet.getLong("CODIGO") == modelo.getCodigo()) {
						placas.add(rSet.getString("PLACA"));
					} else {// modelo diferente, deve setar adicionar tudo ao map e zerar os valores.
						modeloPlaca.put(modelo, placas);
						modelo = new ModeloChecklist();
						placas = new ArrayList<>();
						modelo.setCodigo(rSet.getLong("CODIGO"));
						modelo.setNome(rSet.getString("NOME"));
						placas.add(rSet.getString("PLACA"));
					}
				}
			}
			if(modelo != null) {
				modeloPlaca.put(modelo, placas);
			}
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
	@Override
	public List<VeiculoLiberacao> getStatusLiberacaoVeiculos(Long codUnidade) throws SQLException {
		List<VeiculoLiberacao> listVeiculos = new ArrayList<>();
		List<PerguntaRespostaChecklist> listProblemas = new ArrayList<>();
		Connection conn = null;
		PreparedStatement stmt = null;
		ResultSet rSet = null;
		VeiculoLiberacao veiculo = null;
		PerguntaRespostaChecklist pergunta = null;
		boolean hasCheck = false;
		try {
			conn = getConnection();
			stmt = conn.prepareStatement("SELECT DISTINCT V.PLACA, PLACAS_MANUTENCAO.ITEM_MANUTENCAO, CHECK_HOJE.PLACA_CHECK FROM \n" +
					"(SELECT DISTINCT PLACA_VEICULO AS PLACA_CHECK FROM CHECKLIST C \n" +
					"JOIN VEICULO V ON V.PLACA = C.PLACA_VEICULO WHERE DATA_HORA::DATE = ?\n" +
					"AND V.cod_unidade = ?) AS CHECK_HOJE RIGHT JOIN VEICULO V ON V.PLACA = PLACA_CHECK\n" +
					"LEFT JOIN (SELECT e.placa_veiculo as PLACA_MANUTENCAO, e.pergunta AS ITEM_MANUTENCAO\n" +
					"FROM estratificacao_os e\n" +
					"where e.cod_unidade = ? and e.status_item like 'P' and e.prioridade like 'CRITICA' and e.cpf_mecanico is null\n" +
					"order by e.placa_veiculo) AS PLACAS_MANUTENCAO ON PLACA_MANUTENCAO = V.PLACA\n" +
					"WHERE V.COD_UNIDADE = ?\n" +
					"ORDER BY V.PLACA, PLACAS_MANUTENCAO.ITEM_MANUTENCAO;");
			stmt.setDate(1, DateUtils.toSqlDate(new Date(System.currentTimeMillis())));
			stmt.setLong(2, codUnidade);
			stmt.setLong(3, codUnidade);
			stmt.setLong(4, codUnidade);
			rSet = stmt.executeQuery();

			while (rSet.next()){
				if(veiculo == null){//primeira linha do rSet
					veiculo = new VeiculoLiberacao();
					veiculo.setPlaca(rSet.getString("PLACA"));
					if(rSet.getString("item_manutencao") != null){
						pergunta = new PerguntaRespostaChecklist();
						pergunta.setPergunta(rSet.getString("ITEM_MANUTENCAO"));
						listProblemas.add(pergunta);
					}
					if(veiculo.getPlaca().equals(rSet.getString("PLACA_CHECK"))){
						hasCheck = true;
					}else{
						hasCheck = false;
					}
				}else{//a partir da segunda linha do Rset
					if(veiculo.getPlaca().equals(rSet.getString("placa"))){
						if(rSet.getString("item_manutencao") != null){
							pergunta = new PerguntaRespostaChecklist();
							pergunta.setPergunta(rSet.getString("ITEM_MANUTENCAO"));
							listProblemas.add(pergunta);
						}
					}else{
						verificaInsereListaLiberacao(hasCheck, listProblemas, listVeiculos, veiculo);
						veiculo = new VeiculoLiberacao();
						veiculo.setPlaca(rSet.getString("placa"));
						if(veiculo.getPlaca().equals(rSet.getString("PLACA_CHECK"))){
							hasCheck = true;
						}else{
							hasCheck = false;
						}
						listProblemas = new ArrayList<>();
						if(rSet.getString("item_manutencao") != null){
							pergunta = new PerguntaRespostaChecklist();
							pergunta.setPergunta(rSet.getString("ITEM_MANUTENCAO"));
							listProblemas.add(pergunta);
						}
					}
				}
			}
			verificaInsereListaLiberacao(hasCheck, listProblemas, listVeiculos, veiculo);
		}
		finally{
			closeConnection(conn, stmt, rSet);
		}
		return listVeiculos;
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
			try{
				alternativa.respostaOutros = rSet.getString("resposta");
			}catch (SQLException e){}
		}
		return alternativa;
	}

	/**
	 * Método responsável por salvar as respostas de um checklist na tabela
	 * CHECKLIST_RESPOSTAS. As respostas e perguntas de um checklist vêm em um
	 * map<pergunta, resposta> então precisamos percorrer todo esse map para
	 * adicionar todas as respostas de um checklist ao BD.
	 * @return void
	 * @version 1.0
	 * @since 7 de dez de 2015 14:01:03
	 * @author Luiz Felipe
	 */
	private void insertRespostas(Checklist checklist, Connection conn) throws SQLException {
		PreparedStatement stmt = null;
		try {
			stmt = conn.prepareStatement("INSERT INTO CHECKLIST_RESPOSTAS "
					+ "(COD_UNIDADE, COD_CHECKLIST_MODELO, COD_CHECKLIST, COD_PERGUNTA, COD_ALTERNATIVA, RESPOSTA) "
					+ "VALUES ((SELECT V.COD_UNIDADE FROM VEICULO V WHERE V.PLACA=?), ?, ?, ?, ?, ?)");
			for (PerguntaRespostaChecklist resposta : checklist.getListRespostas()) {
				stmt.setString(1, checklist.getPlacaVeiculo());
				stmt.setLong(2, checklist.getCodModelo());
				stmt.setLong(3, checklist.getCodigo());
				stmt.setLong(4, resposta.getCodigo());
				for(AlternativaChecklist alternativa : resposta.getAlternativasResposta()){
					stmt.setLong(5, alternativa.codigo);
					//se a alternativa esta selecionada
					if(alternativa.selected){
						// se a alternativa é do tipo Outros
						if(alternativa.tipo == AlternativaChecklist.TIPO_OUTROS){
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
					int count = stmt.executeUpdate();
					if (count == 0){
						throw new SQLException("Erro ao inserir resposta");
					}
				}
			}
		} finally {
			closeConnection(null, stmt, null);
		}
	}

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
		List<AlternativaChecklist> alternativas = new ArrayList<>();
		PerguntaRespostaChecklist pergunta = new PerguntaRespostaChecklist();
		AlternativaChecklist alternativa =  new AlternativaChecklist();
		try {
			conn = getConnection();
			stmt = conn.prepareStatement("SELECT CP.CODIGO AS COD_PERGUNTA, CP.ORDEM AS ORDEM_PERGUNTA,\n" +
							"CP.PERGUNTA, CP.SINGLE_CHOICE,CAP.CODIGO AS COD_ALTERNATIVA, CP.PRIORIDADE,\n" +
							"CAP.ORDEM, CP.URL_IMAGEM, CAP.ALTERNATIVA, CR.RESPOSTA\n" +
							"FROM CHECKLIST C\n" +
							"\tJOIN CHECKLIST_RESPOSTAS CR ON\n" +
							"\t\t\tC.CODIGO = CR.COD_CHECKLIST AND\n" +
							"\t\t\tCR.COD_CHECKLIST_MODELO = C.COD_CHECKLIST_MODELO AND\n" +
							"\t\t\tc.cod_unidade = cr.cod_unidade\n" +
							"\tJOIN CHECKLIST_PERGUNTAS CP ON\n" +
							"\t\t\tCP.CODIGO = CR.COD_PERGUNTA AND\n" +
							"\t\t\tCP.COD_UNIDADE = CR.COD_UNIDADE AND\n" +
							"\t\t\tCP.COD_CHECKLIST_MODELO = CR.COD_CHECKLIST_MODELO and\n" +
							"\t\t\tcp.codigo = cr.cod_pergunta\n" +
							"\tJOIN CHECKLIST_ALTERNATIVA_PERGUNTA CAP ON\n" +
							"\t\t\tCAP.CODIGO = CR.COD_ALTERNATIVA AND\n" +
							"\t\t\tCAP.COD_UNIDADE = CR.COD_UNIDADE AND\n" +
							"\t\t\tCAP.COD_CHECKLIST_MODELO = CR.COD_CHECKLIST_MODELO and\n" +
							"\t\t\t\tcap.cod_pergunta = cr.cod_pergunta\n" +
							"\tWHERE c.codigo = ? and c.cpf_colaborador = ? \n" +
							" ORDER BY CP.ORDEM, CAP.ORDEM", ResultSet.TYPE_SCROLL_SENSITIVE,
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
	private void setRespostaAlternativa(AlternativaChecklist alternativa, ResultSet rSet) throws SQLException{

		if(rSet.getString("RESPOSTA").equals("NOK")){
			alternativa.selected = true;
		}else if(rSet.getString("RESPOSTA").equals("OK")){
			alternativa.selected = false;
		}else{
			alternativa.selected = true;
			alternativa.tipo = AlternativaChecklist.TIPO_OUTROS;
			alternativa.respostaOutros = rSet.getString("RESPOSTA");
		}
	}

	/**
	 * Verifica se o veiculo tem problema e se tem check, setando o status e adicionando na lista
	 * @param hasCheck boolean indicando se o veiculo possui checklist realizado no dia corrente
	 * @param listProblemas lista de problemas que o veículo possui
	 * @param listVeiculos lista final com os veiculos {@link VeiculoLiberacao}
     * @param veiculo um veiculo {@link VeiculoLiberacao}
     */
	private void verificaInsereListaLiberacao(boolean hasCheck, List<PerguntaRespostaChecklist> listProblemas,
											  List<VeiculoLiberacao> listVeiculos, VeiculoLiberacao veiculo) {
		if (listProblemas.size() > 0) {
			VeiculoLiberacao v = new VeiculoLiberacao();
			v.setItensCriticos(listProblemas);
			v.setPlaca(veiculo.getPlaca());
			v.setStatus(VeiculoLiberacao.STATUS_NAO_LIBERADO);
			listVeiculos.add(v);
			if (!hasCheck) {
				veiculo.setStatus(VeiculoLiberacao.STATUS_PENDENTE);
				listVeiculos.add(veiculo);
			}
		} else {
			if (hasCheck) {
				veiculo.setStatus(VeiculoLiberacao.STATUS_LIBERADO);
				listVeiculos.add(veiculo);
			} else {
				veiculo.setStatus(VeiculoLiberacao.STATUS_PENDENTE);
				listVeiculos.add(veiculo);
			}
		}
	}
}