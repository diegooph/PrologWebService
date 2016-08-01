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

import br.com.zalf.prolog.models.Alternativa;
import br.com.zalf.prolog.models.Colaborador;
import br.com.zalf.prolog.models.checklist.Checklist;
import br.com.zalf.prolog.models.checklist.ModeloChecklist;
import br.com.zalf.prolog.models.checklist.NovoChecklistHolder;
import br.com.zalf.prolog.models.checklist.PerguntaRespostaChecklist;
import br.com.zalf.prolog.models.checklist.VeiculoLiberacao;
import br.com.zalf.prolog.models.checklist.os.ItemOrdemServico;
import br.com.zalf.prolog.models.checklist.os.OrdemServico;
import br.com.zalf.prolog.models.checklist.os.OsHolder;
import br.com.zalf.prolog.models.util.DateUtils;
import br.com.zalf.prolog.webservice.DatabaseConnection;
import br.com.zalf.prolog.webservice.frota.checklistModelo.ChecklistModeloDaoImpl;
import br.com.zalf.prolog.webservice.frota.veiculo.VeiculoDaoImpl;
import br.com.zalf.prolog.webservice.util.L;
import com.google.gson.Gson;
import com.sun.org.apache.xpath.internal.operations.Or;

public class ChecklistDaoImpl extends DatabaseConnection implements ChecklistDao{

	VeiculoDaoImpl veiculoDao;

	private static final String BUSCA_ITENS_OS = "select os.codigo AS COD_OS, os.status as status_os, os.cod_checklist, cp.codigo as cod_pergunta, cp.ordem as ordem_pergunta, cp.pergunta,\n" +
			"  cp.single_choice, null as url_imagem, cp.prioridade,\n" +
			"  cap.codigo as cod_alternativa, cap.alternativa, cr.resposta, cosi.status_resolucao as status_item from\n" +
			"  checklist c join checklist_ordem_servico os\n" +
			"      on c.codigo = os.cod_checklist AND\n" +
			"      c.cod_unidade = os.cod_unidade\n" +
			"  join checklist_ordem_servico_itens cosi on\n" +
			"      os.codigo = cosi.cod_os AND\n" +
			"      os.cod_unidade = cosi.cod_unidade\n" +
			"  join checklist_perguntas cp on cp.cod_unidade = os.cod_unidade AND\n" +
			"    cp.codigo = cosi.cod_pergunta AND\n" +
			"    cp.cod_checklist_modelo = c.cod_checklist_modelo\n" +
			"  join checklist_alternativa_pergunta cap on cap.cod_unidade = cp.cod_unidade AND\n" +
			"    cap.cod_checklist_modelo = cp.cod_checklist_modelo AND\n" +
			"    cap.cod_pergunta = cp.codigo AND\n" +
			"    cap.codigo = cosi.cod_alternativa\n" +
			"  join checklist_respostas cr on c.cod_unidade = cr.cod_unidade AND\n" +
			"    cr.cod_checklist_modelo = c.cod_checklist_modelo AND\n" +
			"    cr.cod_checklist = c.codigo AND\n" +
			"    cr.cod_pergunta = cp.codigo AND\n" +
			"    cr.cod_alternativa = cap.codigo\n" +
			"where os.codigo = ? and os.cod_unidade = (SELECT COD_UNIDADE FROM VEICULO WHERE PLACA = ?) and cosi.status_resolucao = ? \n" +
			"order by os.codigo, cp.codigo, cap.codigo";

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
		//L.d("ChecklistDaoImpl", "Chamou dao, objeto: " + checklist.toString());
		try {
			conn = getConnection();
			conn.setAutoCommit(false);
			stmt = conn.prepareStatement("INSERT INTO CHECKLIST "
					+ "(COD_UNIDADE,COD_CHECKLIST_MODELO, DATA_HORA, CPF_COLABORADOR, PLACA_VEICULO, TIPO, KM_VEICULO, TEMPO_REALIZACAO) "
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
				insertRespostas(checklist, conn);
				insertItemManutencao(checklist, conn);
				insertItemOs(checklist, conn);
				veiculoDao.updateKmByPlaca(checklist.getPlacaVeiculo(), checklist.getKmAtualVeiculo());
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

//	/**
//	 * Método para inserir itens com apontados como problema no checklist em uma tabela destinada ao controle de manutenção
//	 * @param checklist um Checklist
//	 * @throws SQLException caso não seja possível realizar as buscas e inserts
//	 */
//	public void insertItemManutencao2(Checklist checklist, Connection conn) throws SQLException{
//		PreparedStatement stmt = null;
//		ResultSet rSet = null;
//		boolean possuiOsAberta = false;
//		List<PerguntaRespostaChecklist> itensAbertos = getItensEmAberto(checklist.getPlacaVeiculo(), conn);
//		try{
//			for(PerguntaRespostaChecklist pergunta: checklist.getListRespostas()) {
//				if(respostaTemProblema(pergunta)){
//					for(PerguntaRespostaChecklist.Alternativa alternativa : pergunta.getAlternativasResposta()) {
//						if (possuiItemAberto(pergunta.getCodigo(), alternativa.codigo, itensAbertos)){
//							// placa ja tem um item em aberto, pergunta + alternativa
//							// incrementar a qtApontamentos na tabela te itens OS
//						}
//					}
//				}
//			}
//		}finally{
//			closeConnection(null, stmt, null);
//		}
//	}

	public void insertItemOs(Checklist checklist, Connection conn) throws SQLException{
		PreparedStatement stmt = null;
		ResultSet rSet = null;
		Long tempCodOs = null;
		Long gerouOs = null;
		// vem apenas um holder, ja que a busca foi feita apenas para uma placa
		List<OsHolder> oss = getResumoOs(checklist.getPlacaVeiculo(), OrdemServico.Status.ABERTA.asString(), conn);
		// todas as os de uma unica placa
		List<OrdemServico> ordens = null;
		if (oss != null) {
			ordens = oss.get(0).getOs();
			L.d("ordens", ordens.toString());
		}
		try{
			for (PerguntaRespostaChecklist pergunta: checklist.getListRespostas()) { //verifica cada pergunta do checklist
				L.d("Pergunta", pergunta.getCodigo().toString());
				for (PerguntaRespostaChecklist.Alternativa alternativa: pergunta.getAlternativasResposta()) { // varre cada alternativa de uma pergunta
					L.d("Verificando Alternativa:", String.valueOf(alternativa.codigo));
					if (alternativa.selected) {
						L.d("Alternativa esta elecionada", String.valueOf(alternativa.codigo));
						if (ordens != null) {//verifica se ja tem algum item em aberto
							tempCodOs = jaPossuiItemEmAberto(pergunta.getCodigo(), alternativa.codigo, ordens);
							if (tempCodOs != null) {
								L.d("tempCodOs", tempCodOs.toString());
							}
						}
						if (tempCodOs != null) {
							incrementaQtApontamento2(checklist.getPlacaVeiculo(), tempCodOs, pergunta.getCodigo(), alternativa.codigo, conn);
							L.d("incrementa", "chamou metodo para incrementar a qt de apontamentos");
						} else {
							if (gerouOs != null) { //checklist ja gerou uma os -> deve inserir o item nessa os gerada
								insertServicoOs(pergunta.getCodigo(), alternativa.codigo, gerouOs, checklist.getPlacaVeiculo(), conn);
							} else {
								gerouOs = createOs(checklist.getPlacaVeiculo(), checklist.getCodigo(), conn);
								insertServicoOs(pergunta.getCodigo(), alternativa.codigo, gerouOs, checklist.getPlacaVeiculo(), conn);
							}
						}
					}
				}
			}
		}finally{
			closeConnection(null, stmt, null);
		}
	}

	private Long createOs (String placa, Long codChecklist, Connection conn) throws SQLException{
		L.d("criando OS", "Placa: " + placa + "checklist: " + codChecklist);
		ResultSet rSet = null;
		PreparedStatement stmt = null;
		try{
			stmt = conn.prepareStatement("INSERT INTO checklist_ordem_servico(CODIGO, cod_unidade, cod_checklist, status) VALUES\n" +
					"((SELECT COALESCE(MAX(CODIGO), MAX(CODIGO), 0) +1 AS CODIGO\n" +
					"  FROM checklist_ordem_servico\n" +
					"  WHERE cod_unidade = (SELECT COD_UNIDADE FROM VEICULO WHERE PLACA = ?)),\n" +
					" (SELECT COD_UNIDADE FROM VEICULO WHERE PLACA = ?),?,?) RETURNING CODIGO");
			stmt.setString(1, placa);
			stmt.setString(2, placa);
			stmt.setLong(3, codChecklist);
			stmt.setString(4, OrdemServico.Status.ABERTA.asString());
			rSet = stmt.executeQuery();
			if (rSet.next()){
				return rSet.getLong("codigo");
			}else{
				throw new SQLException("Erro ao criar nova OS");
			}
		}finally {
			closeConnection(null, stmt, rSet);
		}
	}

	private void insertServicoOs(Long codPergunta, Long codAlternativa, Long codOs, String placa, Connection conn) throws SQLException{
		L.d("Inserindo serviço: ", "Pergunta: " + codPergunta + " codAlternativa: " + codAlternativa + " codOs: " + codOs);
		PreparedStatement stmt = null;
		try{
			stmt = conn.prepareStatement("INSERT INTO checklist_ordem_servico_itens(COD_UNIDADE, COD_OS, cod_pergunta, cod_alternativa, status_resolucao)\n" +
					"VALUES ((SELECT COD_UNIDADE FROM VEICULO WHERE PLACA = ?),?,?,?,?)");
			stmt.setString(1, placa);
			stmt.setLong(2, codOs);
			stmt.setLong(3, codPergunta);
			stmt.setLong(4, codAlternativa);
			stmt.setString(5, ItemOrdemServico.Status.PENDENTE.asString());
			int count = stmt.executeUpdate();
			if(count == 0){
				throw new SQLException("Erro ao inserir o serviço");
			}
		}finally {
			closeConnection(null, stmt, null);
		}
	}

	private void incrementaQtApontamento2(String placa, Long codOs, Long codPergunta, Long codAlternativa, Connection conn) throws SQLException{
		L.d("incrementandoQt", "Placa: " + placa + "codOs: " + codOs + "Pergunta: " + codPergunta + "Alternativa: " + codAlternativa);
		PreparedStatement stmt = null;
		ResultSet rSet = null;
		try{
			stmt = conn.prepareStatement("UPDATE checklist_ordem_servico_itens SET qt_apontamentos =\n" +
					"(SELECT qt_apontamentos FROM\n" +
					"checklist_ordem_servico_itens WHERE\n" +
					"cod_unidade = (SELECT COD_UNIDADE FROM VEICULO WHERE PLACA = ?)\n" +
					"AND cod_os = ?\n" +
					"AND cod_pergunta = ?\n" +
					"AND cod_alternativa = ?\n" +
					"AND status_resolucao = ? ) + 1\n" +
					"WHERE cod_unidade = (SELECT COD_UNIDADE FROM VEICULO WHERE PLACA = ?)\n" +
					"AND cod_os = ?\n" +
					"AND cod_pergunta = ?\n" +
					"AND cod_alternativa = ?\n" +
					"AND status_resolucao = ?");
			stmt.setString(1, placa);
			stmt.setLong(2, codOs);
			stmt.setLong(3, codPergunta);
			stmt.setLong(4, codAlternativa);
			stmt.setString(5, ItemOrdemServico.Status.PENDENTE.asString());
			stmt.setString(6, placa);
			stmt.setLong(7, codOs);
			stmt.setLong(8, codPergunta);
			stmt.setLong(9, codAlternativa);
			stmt.setString(10, ItemOrdemServico.Status.PENDENTE.asString());
			int count = stmt.executeUpdate();
			if (count == 0){
				throw new SQLException("Erro ao incrementar a quantidade de apontamentos");
			}
		}finally {
			closeConnection(null, stmt, rSet);
		}
	}

	private Long jaPossuiItemEmAberto(Long codPergunta, Long codAlternativa, List<OrdemServico> oss){
		L.d("verificando se possui item em aberto", "Pergunta: " + codPergunta + "Alternativa: " + codAlternativa);
		for (OrdemServico os:oss) {
			for (ItemOrdemServico item:os.getItens()) {
				for (Alternativa alternativa: item.getPergunta().getAlternativasResposta()) {
					if (item.getPergunta().getCodigo().equals(codPergunta) && alternativa.codigo == codAlternativa){
						L.d("item existe", "item existe na lista");
						return os.getCodigo();
					}
				}
			}
		}
		return null;
	}

	public List<OsHolder> getResumoOs(String placa, String status, Connection conn) throws SQLException{
		PreparedStatement stmt = null;
		ResultSet rSet = null;
		List<OsHolder> holders = null;
		List<OrdemServico> oss = null;
		OsHolder holder = null;
		OrdemServico os = null;
		try{
			stmt = conn.prepareStatement("SELECT cos.codigo as cod_os, cos.cod_checklist, cos.status, C.placa_veiculo " +
					"FROM checklist_ordem_servico cos join checklist c ON cos.cod_checklist = C.codigo\n" +
					"and c.cod_unidade = cos.cod_unidade\n" +
					"where c.placa_veiculo LIKE ? and cos.status LIKE ?");
			stmt.setString(1, placa);
			stmt.setString(2, status);
			rSet = stmt.executeQuery();
			while(rSet.next()){
				if (holder == null){//primeiro item do ResultSet
					holders = new ArrayList<>();
					oss = new ArrayList<>();
					holder = new OsHolder();
					holder.setPlaca(rSet.getString("placa_veiculo"));
					os = createOrdemServico(rSet);
					os.setItens(getItensOs(holder.getPlaca(), os.getCodigo(), ItemOrdemServico.Status.PENDENTE.asString(), conn));
					oss.add(os);
				}else{ // Próximos itens
					if (rSet.getString("placa_veiculo").equals(holder.getPlaca())){ // caso a placa seja igual ao item anterior, criar nova os e add na lista
						os = new OrdemServico();
						os = createOrdemServico(rSet);
						os.setItens(getItensOs(holder.getPlaca(), os.getCodigo(), ItemOrdemServico.Status.PENDENTE.asString(), conn));
						oss.add(os);
					}else{//placa diferente, fechar a lista, setar no holder, criar novo holder, nova os e add na lista
						holder.setOs(oss);
						holders.add(holder);
						holder = new OsHolder();
						holder.setPlaca(rSet.getString("placa_veiculo"));
						os = new OrdemServico();
						os = createOrdemServico(rSet);
						oss = new ArrayList<>();
						os.setItens(getItensOs(holder.getPlaca(), os.getCodigo(), ItemOrdemServico.Status.PENDENTE.asString(), conn));
						oss.add(os);
					}
				}
			}
			if (holder != null) {
				holder.setOs(oss);
				holders.add(holder);
			}
		}finally {
			closeConnection(null, stmt, rSet);
		}
		return holders;
	}

	public List<ItemOrdemServico> getItensOs(String placa, Long codOs, String status, Connection conn) throws SQLException{
		PreparedStatement stmt = null;
		ResultSet rSet = null;
		List<ItemOrdemServico> itens = new ArrayList<>();
		ItemOrdemServico item = null;
		PerguntaRespostaChecklist pergunta = null;
		PerguntaRespostaChecklist.Alternativa alternativa = null;
		List<PerguntaRespostaChecklist.Alternativa> alternativas = null;
		try{
			stmt = conn.prepareStatement(BUSCA_ITENS_OS);
			stmt.setLong(1, codOs);
			stmt.setString(2, placa);
			stmt.setString(3, ItemOrdemServico.Status.PENDENTE.asString());
			rSet = stmt.executeQuery();
			while (rSet.next()){
				item = new ItemOrdemServico();
				pergunta = new PerguntaRespostaChecklist();
				pergunta = createPergunta(rSet);
				alternativa = new PerguntaRespostaChecklist.Alternativa();
				alternativa = createAlternativa(rSet);
				alternativas = new ArrayList<>();
				alternativas.add(alternativa);
				pergunta.setAlternativasResposta(alternativas);
				item.setPergunta(pergunta);
				itens.add(item);
			}
		}finally {
			closeConnection(null, stmt, rSet);
		}
		return itens;
	}

	private OrdemServico createOrdemServico(ResultSet rSet) throws SQLException{
		OrdemServico os = new OrdemServico();
		os.setCodChecklist(rSet.getLong("cod_checklist"));
		os.setCodigo(rSet.getLong("cod_os"));
		os.setStatus(OrdemServico.Status.fromString(rSet.getString("status")));
		return os;
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
			closeConnection(null, stmt, rSet);
		}
	}

	private boolean possuiItemAberto(long codPergunta, long codAlternativa, List<PerguntaRespostaChecklist> itensAbertos){
		for (PerguntaRespostaChecklist perguntaAberto: itensAbertos){
			for (PerguntaRespostaChecklist.Alternativa alternativa: perguntaAberto.getAlternativasResposta()) {
				if (perguntaAberto.getCodigo() == codPergunta && alternativa.codigo == codAlternativa){
					return true;
				}
			}
		}
		return false;
	}

	private List<PerguntaRespostaChecklist> getOs(String placa, Connection conn) throws SQLException{
		List<PerguntaRespostaChecklist> itensAbertos = new ArrayList<>();
		PerguntaRespostaChecklist pergunta = null;
		List<PerguntaRespostaChecklist.Alternativa> alternativas = null;
		PerguntaRespostaChecklist.Alternativa alternativa = null;
		PreparedStatement stmt = null;
		ResultSet rSet = null;
		try{
			stmt = conn.prepareStatement("SELECT I.cod_pergunta,  I.cod_alternativa, cp.pergunta, cap.alternativa FROM checklist C\n" +
					"  JOIN checklist_ordem_servico OS on c.cod_unidade = os.cod_unidade AND C.CODIGO = OS.COD_CHECKLIST\n" +
					"  JOIN checklist_ordem_servico_itens I ON I.COD_OS = OS.CODIGO AND I.cod_unidade = OS.COD_UNIDADE\n" +
					"  JOIN checklist_perguntas cp on cp.cod_checklist_modelo = c.cod_checklist_modelo\n" +
					"   and cp.cod_unidade = c.cod_unidade\n" +
					"   and cp.codigo = i.cod_pergunta\n" +
					"  JOIN checklist_alternativa_pergunta cap on cap.cod_unidade = i.cod_unidade\n" +
					"    and cap.cod_checklist_modelo = c.cod_checklist_modelo\n" +
					"    and cap.cod_pergunta = i.cod_pergunta\n" +
					"    and cap.codigo = i.cod_alternativa\n" +
					"WHERE C.PLACA_VEICULO = ? AND OS.STATUS = ? AND I.status_resolucao = ?;");
			stmt.setString(1,placa);
			stmt.setString(2, OrdemServico.Status.ABERTA.asString());
			stmt.setString(3, ItemOrdemServico.Status.PENDENTE.asString());
			rSet = stmt.executeQuery();
			if(rSet.next()){
				if (pergunta == null){
					pergunta = new PerguntaRespostaChecklist();
					pergunta.setCodigo(rSet.getLong("cod_pergunta"));
					pergunta.setPergunta(rSet.getString("pergunta"));
					alternativa = new PerguntaRespostaChecklist.Alternativa();
					alternativa.codigo = rSet.getLong("cod_alternativa");
					alternativa.alternativa = rSet.getString("alternativa");
					alternativas = new ArrayList<>();
					alternativas.add(alternativa);
				}else{
					if (rSet.getLong("cod_pergunta") == pergunta.getCodigo()){
						alternativa = new PerguntaRespostaChecklist.Alternativa();
						alternativa.codigo = rSet.getLong("cod_alternativa");
						alternativa.alternativa = rSet.getString("alternativa");
						alternativas.add(alternativa);
					}else{
						pergunta.setAlternativasResposta(alternativas);
						itensAbertos.add(pergunta);
						pergunta = new PerguntaRespostaChecklist();
						pergunta.setCodigo(rSet.getLong("cod_pergunta"));
						pergunta.setPergunta(rSet.getString("pergunta"));
						alternativa = new PerguntaRespostaChecklist.Alternativa();
						alternativa.codigo = rSet.getLong("cod_alternativa");
						alternativa.alternativa = rSet.getString("alternativa");
						alternativas = new ArrayList<>();
						alternativas.add(alternativa);
					}
				}
			}
			pergunta.setAlternativasResposta(alternativas);
			itensAbertos.add(pergunta);
		}finally {
			closeConnection(null, stmt, rSet);
		}
		return itensAbertos;
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
			stmt = conn.prepareStatement("SELECT C.CODIGO, C.DATA_HORA, C.cod_checklist_modelo, C.KM_VEICULO, "
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
	 *
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
		List<PerguntaRespostaChecklist.Alternativa> alternativas = new ArrayList<>();
		PerguntaRespostaChecklist pergunta = new PerguntaRespostaChecklist();
		PerguntaRespostaChecklist.Alternativa alternativa =  new PerguntaRespostaChecklist.Alternativa();
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
				}else {// verificar se o prox modelo é igual ao ja criado
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


