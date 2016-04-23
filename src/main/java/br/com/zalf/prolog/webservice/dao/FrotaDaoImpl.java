package br.com.zalf.prolog.webservice.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

import br.com.zalf.prolog.models.checklist.PerguntaRespostaChecklist;
import br.com.zalf.prolog.models.checklist.PerguntaRespostaChecklist.Alternativa;
import br.com.zalf.prolog.models.frota.ItemManutencao;
import br.com.zalf.prolog.models.frota.ManutencaoHolder;
import br.com.zalf.prolog.models.frota.Tempo;
import br.com.zalf.prolog.models.util.DateUtils;
import br.com.zalf.prolog.webservice.DatabaseConnection;
import br.com.zalf.prolog.webservice.dao.interfaces.FrotaDao;

public class FrotaDaoImpl extends DatabaseConnection implements FrotaDao{

	private static final String PRIORIDADE_CRITICA = "CRITICA";
	private static final String PRIORIDADE_ALTA = "ALTA";
	private static final String PRIORIDADE_BAIXA = "BAIXA";

	private static final int MINUTOS_NUM_DIA = 1440;
	private static final int MINUTOS_NUMA_HORA = 60;

	@Override
	public List<ManutencaoHolder> getManutencaoHolder (Long codUnidade, int limit, long offset, boolean isAbertos) throws SQLException{
		Connection conn = null;
		PreparedStatement stmt = null;
		ResultSet rSet = null;
		ManutencaoHolder holder = new ManutencaoHolder();
		List<ItemManutencao> listItemManutencao = new ArrayList<>();
		ItemManutencao itemManutencao = null;
		List<ManutencaoHolder> listManutencaoHolder = new ArrayList<>();
		List<PerguntaRespostaChecklist.Alternativa> listAlternativa = new ArrayList<>();

		try{
			conn = getConnection();
			//query busca todos os itens de manutenção
			String query = "SELECT CM.DATA_APONTAMENTO, CM.PLACA, CM.ITEM, CP.PERGUNTA, CP.PRIORIDADE, PG.PRAZO, "
					+ "CM.QT_APONTAMENTOS, CM.STATUS_RESOLUCAO, CM.DATA_RESOLUCAO, "
					+ "CM.CPF_FROTA, C.NOME FROM CHECKLIST_MANUTENCAO CM	"
					+ "JOIN VEICULO V ON V.PLACA = CM.PLACA "
					+ "JOIN CHECKLIST_PERGUNTAS CP ON CP.CODIGO = CM.ITEM	"
					+ "JOIN PRIORIDADE_PERGUNTA_CHECKLIST PG ON PG.PRIORIDADE = CP.PRIORIDADE "
					+ "JOIN (SELECT DISTINCT(CM.PLACA ) AS LISTA_PLACAS "
					+ "FROM CHECKLIST_MANUTENCAO CM LIMIT ? OFFSET ?) "
					+ "AS PLACAS_PROBLEMAS ON LISTA_PLACAS = CM.PLACA	"
					+ "LEFT JOIN COLABORADOR C ON C.CPF = CM.CPF_FROTA WHERE "
					+ "V.COD_UNIDADE = ? AND CM.CPF_FROTA  %s ORDER BY PLACA, PG.PRAZO, CM.ITEM";
			if(isAbertos){
				query = String.format(query, "IS NULL");
			}else{
				query = String.format(query, ">0");
			}	
			stmt = conn.prepareStatement(query, ResultSet.TYPE_SCROLL_SENSITIVE,
					ResultSet.CONCUR_UPDATABLE);
			stmt.setInt(1, limit);
			stmt.setLong(2, offset);
			stmt.setLong(3, codUnidade);
			rSet = stmt.executeQuery();
			if(rSet.next()){
				if(rSet.first()){
					itemManutencao = createItemManutencao(rSet);
					listItemManutencao.add(itemManutencao);
					holder.setPlaca(rSet.getString("PLACA"));
				}
				while(rSet.next()){
					if(rSet.getString("PLACA").equals(holder.getPlaca())){
						itemManutencao = createItemManutencao(rSet);
						listItemManutencao.add(itemManutencao);
					}else{
						holder.setListManutencao(listItemManutencao);
						setQtItens(holder);
						listManutencaoHolder.add(holder);
						holder = new ManutencaoHolder();
						holder.setPlaca(rSet.getString("PLACA"));
						itemManutencao = createItemManutencao(rSet);
						listItemManutencao = new ArrayList<>();
						listItemManutencao.add(itemManutencao);
					}
				}
				if(!listManutencaoHolder.isEmpty() || rSet.getRow()==0){
					holder.setListManutencao(listItemManutencao);
					setQtItens(holder);
					listManutencaoHolder.add(holder);
					//listAlternativa = getListaAlternativas(codUnidade, conn);
					setDescricaoItens(getListaAlternativas(codUnidade, conn, isAbertos), listManutencaoHolder);
					ordenaLista(listManutencaoHolder);
				}
			}else{
				return listManutencaoHolder;
			}
		}
		finally{
			closeConnection(conn, stmt, rSet);
		}

		return listManutencaoHolder;
	}
	private void setQtItens(ManutencaoHolder holder){
		for(ItemManutencao item : holder.getListManutencao()){
			switch (item.getPrioridade()) {
			case PRIORIDADE_CRITICA:
				holder.setQtdCritica(holder.getQtdCritica() + 1);;
				break;
			case PRIORIDADE_ALTA:
				holder.setQtdAlta(holder.getQtdAlta() + 1);;
				break;
			case PRIORIDADE_BAIXA:
				holder.setQtdBaixa(holder.getQtdBaixa() + 1);;
				break;
			default:
				break;
			}
		}
	}

	private void ordenaLista (List<ManutencaoHolder> list){

		Collections.sort(list, new CustomComparator());
		Collections.reverse(list);
	}

	private void setDescricaoItens(ResultSet rSetAlternativas, List<ManutencaoHolder> holder) throws SQLException{
		rSetAlternativas.first();
		List<Alternativa> tempListAlternativa;
		for(ManutencaoHolder itemManutencao : holder){ // item manutenção contendo a placa e a lista de itens quebrados dessa placa
			List<ItemManutencao> listItemManutencao = itemManutencao.getListManutencao();
			System.out.println("Verificando o holder da placa: " + itemManutencao.getPlaca() + "/n");
			for(ItemManutencao item : listItemManutencao){// contém cada item quebrado de uma mesma placa
				tempListAlternativa = new ArrayList<>();
				System.out.println("Verificando o item de codigo: " + item.getCodItem());
				while(rSetAlternativas.getString("PLACA_VEICULO").equals(itemManutencao.getPlaca()) && item.getCodItem() == rSetAlternativas.getInt("COD_PERGUNTA")
						/*&& item.getDataResolucao() == null*/){
					System.out.println("entrou no while");

					Alternativa alternativa = new PerguntaRespostaChecklist.Alternativa();
					if(rSetAlternativas.getString("RESPOSTA").equals("NOK")){
						System.out.println("resposta == NOK");
						alternativa.alternativa = rSetAlternativas.getString("ALTERNATIVA");	
					}else{
						System.out.println("resposta == OUTROS");
						alternativa.respostaOutros = rSetAlternativas.getString("RESPOSTA");
						alternativa.tipo = PerguntaRespostaChecklist.Alternativa.TIPO_OUTROS;
					}
					tempListAlternativa.add(alternativa);
					System.out.println("Adicionando alternativa criada na lista temporaria");
					if(!rSetAlternativas.next()){
						System.out.println("final do rset de alternativas");
						break;
					}
				}
				System.out.println("setando a lista de alternativas no item: "  + item.getCodItem());
				item.setListAlternativa(tempListAlternativa);
			}
		}
		closeConnection(null, null, rSetAlternativas);
	}

	private ItemManutencao createItemManutencao(ResultSet rSet) throws SQLException{
		ItemManutencao item = new ItemManutencao();
		item.setData(rSet.getTimestamp("DATA_APONTAMENTO"));
		item.setCodItem(rSet.getInt("ITEM"));
		item.setItem(rSet.getString("PERGUNTA"));
		item.setTempoLimiteResolucao(createTempo(TimeUnit.HOURS.toMinutes(rSet.getLong("PRAZO"))));
		item.setQtApontamentos(rSet.getInt("QT_APONTAMENTOS"));
		item.setDataResolucao(rSet.getTimestamp("DATA_RESOLUCAO"));
		item.setFeedbackResolucao(rSet.getString("STATUS_RESOLUCAO"));
		item.setCpfMecanico(rSet.getLong("CPF_FROTA"));
		item.setNomeMecanico(rSet.getString("NOME"));
		item.setPrioridade(rSet.getString("PRIORIDADE"));
		setTempoRestante(item, rSet.getInt("PRAZO"));
		return item;
	}
	/**
	 * Marca como resolvido um item em aberto, salvando o cpf de quem consertou e a data/hora
	 * @param request contém o item que foi consertado e dados do solicitante
	 * @return 
	 * @throws SQLException 
	 */
	@Override
	public boolean consertaItem (ItemManutencao itemManutencao) throws SQLException{
		Connection conn = null;
		PreparedStatement stmt = null;
		try{
			conn = getConnection();

			stmt = conn.prepareStatement("UPDATE CHECKLIST_MANUTENCAO SET CPF_FROTA = ?, "
					+ "DATA_RESOLUCAO = ?, "
					+ "STATUS_RESOLUCAO = ? "
					+ "WHERE PLACA = ? AND "
					+ "ITEM = ? AND "
					+ "CPF_FROTA IS NULL");

			stmt.setLong(1, itemManutencao.getCpfMecanico());
			System.out.println("Cpf: " + itemManutencao.getCpfMecanico());
			stmt.setTimestamp(2, DateUtils.toTimestamp(new Date(System.currentTimeMillis())));
			System.out.println("Data: " + DateUtils.toTimestamp(new Date(System.currentTimeMillis())));
			if(itemManutencao.getFeedbackResolucao() == null){
				stmt.setNull(3, java.sql.Types.VARCHAR);
				System.out.println(itemManutencao.getFeedbackResolucao());
			}else{
				stmt.setString(3, itemManutencao.getFeedbackResolucao());
				System.out.println(itemManutencao.getFeedbackResolucao());
			}
			stmt.setString(4, itemManutencao.getPlaca());
			System.out.println(itemManutencao.getPlaca());
			stmt.setInt(5, itemManutencao.getCodItem());
			System.out.println(itemManutencao.getCodItem());
			int count = stmt.executeUpdate();
			if(count == 0){
				throw new SQLException("Erro ao marcar o item como resolvido");
			}	
		}finally{
			closeConnection(conn, stmt, null);
		}
		return true;
	}



	private ResultSet getListaAlternativas(Long codUnidade, Connection conn, boolean isAbertos) throws SQLException{
		PreparedStatement stmt = null;

		try{
			
			String query = "SELECT  DISTINCT C.PLACA_VEICULO,CR.COD_PERGUNTA, "
					+ "CR.COD_ALTERNATIVA, CR.RESPOSTA, CAP.ALTERNATIVA, PG.PRAZO "
					+ "FROM CHECKLIST_RESPOSTAS CR JOIN	CHECKLIST C ON C.CODIGO = CR.COD_CHECKLIST	"
					+ "JOIN COLABORADOR CO ON CO.CPF = C.CPF_COLABORADOR "
					+ "JOIN CHECKLIST_PERGUNTAS CP ON CP.CODIGO = CR.COD_PERGUNTA "
					+ "JOIN CHECKLIST_ALTERNATIVA_PERGUNTA CAP ON CAP.CODIGO = CR.COD_ALTERNATIVA	"
					+ "JOIN VEICULO V ON V.PLACA = C.PLACA_VEICULO	"
					+ "JOIN PRIORIDADE_PERGUNTA_CHECKLIST PG ON PG.PRIORIDADE = CP.PRIORIDADE "
					+ "JOIN CHECKLIST_MANUTENCAO CM ON CM.ITEM = CR.COD_PERGUNTA AND CM.PLACA = C.PLACA_VEICULO	"
					+ "WHERE CR.RESPOSTA <> 'OK' "
					+ "AND V.COD_UNIDADE = ? "
					+ "AND	CM.CPF_FROTA %s "
					+ "AND C.DATA_HORA >= CM.DATA_APONTAMENTO	"
					+ "ORDER BY PLACA_VEICULO, PG.PRAZO, CR.COD_PERGUNTA";
			
			if(isAbertos){
				query = String.format(query, "IS NULL");
			}else{
				query = String.format(query, ">0");
			}
			
			stmt = conn.prepareStatement(query, ResultSet.TYPE_SCROLL_SENSITIVE,
					ResultSet.CONCUR_UPDATABLE);
							
			stmt.setLong(1, codUnidade);
			return stmt.executeQuery();
		}finally{
			//closeConnection(null, stmt, null);
		}
	}

	public void setTempoRestante(ItemManutencao itemManutencao, int prazoHoras) {
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(itemManutencao.getData());
		calendar.add(Calendar.HOUR, prazoHoras);// data apontamento + prazo
		Date dataMaxima = calendar.getTime(); // data máxima de resolução
		long tempoRestante = dataMaxima.getTime() - System.currentTimeMillis();
		itemManutencao.setTempoRestante(createTempo(TimeUnit.MILLISECONDS.toMinutes(tempoRestante)));
	}

	private Tempo createTempo(long temp){
		Tempo tempo = new Tempo();
		if (temp < MINUTOS_NUMA_HORA) {
			tempo.setMinuto((int)temp);
		} else if (temp < MINUTOS_NUM_DIA) {
			long hours = TimeUnit.MINUTES.toHours(temp);
			temp = hours % 60;
			tempo.setHora((int)hours);
			tempo.setMinuto((int)temp);
		} else if (temp >= MINUTOS_NUM_DIA) {
			long days = TimeUnit.MINUTES.toDays(temp);
			long hours = TimeUnit.MINUTES.toHours(temp) % 24;
			tempo.setDia((int)days);
			tempo.setHora((int)hours);;
		}
		return tempo;
	}

	public class ItemChecklist{

		@Override
		public String toString() {
			return "ItemChecklist [placa=" + placa + ", codPergunta=" + codPergunta + ", data=" + data + ", cpf=" + cpf
					+ ", nome=" + nome + ", descricao=" + descricao + "]";
		}
		public String placa;
		public int codPergunta;
		public Date data;
		public Long cpf;
		public String nome;
		public String descricao;

		public ItemChecklist() {
		}
	}

	private class CustomComparator implements Comparator<ManutencaoHolder>{

		/**
		 * Compara primeiro pela pontuação e depois pela devolução em NF, evitando empates
		 */
		@Override
		public int compare(ManutencaoHolder o1, ManutencaoHolder o2) {
			Integer valor1 = Double.compare(o1.getQtdCritica(), o2.getQtdCritica());
			if(valor1!=0){
				return valor1;
			}
			Integer valor2 = Double.compare(o1.getQtdAlta(), o2.getQtdAlta());
			if(valor2 != 0){
				return valor2;
			}
			Integer valor3 = Double.compare(o1.getQtdBaixa(), o2.getQtdBaixa());
			return valor3;

		}
	}
	
}
