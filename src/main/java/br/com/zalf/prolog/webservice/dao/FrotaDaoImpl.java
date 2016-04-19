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

import br.com.zalf.prolog.models.Request;
import br.com.zalf.prolog.models.frota.ItemDescricao;
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
		List<ItemManutencao> list = new ArrayList<>();
		ItemManutencao itemManutencao = null;
		List<ManutencaoHolder> listHolder = new ArrayList<>();
		List<ItemChecklist> listItemChecklist = new ArrayList<>();

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
					+ "V.COD_UNIDADE = ? AND CM.CPF_FROTA  %s ORDER BY PLACA, PG.PRAZO";
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
			if(rSet.first()){
				itemManutencao = createItemManutencao(rSet);
				System.out.println(itemManutencao);
				list.add(itemManutencao);
				holder.setPlaca(rSet.getString("PLACA"));
			}
			while(rSet.next()){
				if(rSet.getString("PLACA").equals(holder.getPlaca())){
					itemManutencao = createItemManutencao(rSet);
					list.add(itemManutencao);
				}else{
					holder.setListManutencao(list);
					setQtItens(holder);
					listHolder.add(holder);
					holder = new ManutencaoHolder();
					holder.setPlaca(rSet.getString("PLACA"));
					itemManutencao = createItemManutencao(rSet);
					list = new ArrayList<>();
					list.add(itemManutencao);
					
				}
			}
			if(!listHolder.isEmpty() || rSet.getRow()==0){
			holder.setListManutencao(list);
			setQtItens(holder);
			listHolder.add(holder);
			listItemChecklist = getListaDescricao(codUnidade, conn);
			}
			//System.out.println(listItemChecklist);
		}
		finally{
			closeConnection(conn, stmt, rSet);
		}
		setDescricaoItens(listItemChecklist, listHolder);
		ordenaLista(listHolder);
		System.out.print(listHolder);
		return listHolder;
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

	private void setDescricaoItens(List<ItemChecklist> listItemChecklist, List<ManutencaoHolder> holder){

		int i = 0;
		for(ManutencaoHolder itemManutencao : holder){ // item manutenção contendo a placa e a lista de itens quebrados dessa placa
			List<ItemManutencao> listItemManutencao = itemManutencao.getListManutencao(); 
			for(ItemManutencao item : listItemManutencao){// contém cada item quebrado de uma mesma placa
				List<ItemDescricao> tempList = new ArrayList<>();
				while(i < listItemChecklist.size()){
					if(listItemChecklist.get(i).placa.equals(itemManutencao.getPlaca()) && item.getCodItem() == listItemChecklist.get(i).codPergunta
							 && item.getDataResolucao() == null){
												
						ItemDescricao itemDescricao = new ItemDescricao();
						itemDescricao.setData(listItemChecklist.get(i).data);
						itemDescricao.setCpf(listItemChecklist.get(i).cpf);
						itemDescricao.setNome(listItemChecklist.get(i).nome);
						itemDescricao.setDescricao(listItemChecklist.get(i).descricao);
						tempList.add(itemDescricao);
						listItemChecklist.remove(i);
						i = i-1;
					}
					i = i + 1;
					item.setListDescricao(tempList);
				}
				i = 0;
			}
		}
	}

	private ItemManutencao createItemManutencao(ResultSet rSet) throws SQLException{
		ItemManutencao item = new ItemManutencao();
		item.setData(rSet.getTimestamp("DATA_APONTAMENTO"));
		item.setCodItem(rSet.getInt("ITEM"));
		item.setItem(rSet.getString("PERGUNTA"));
		Tempo tempoLimiteResolucao = new Tempo();
		tempoLimiteResolucao.setHora(rSet.getInt("PRAZO"));
		item.setTempoLimiteResolucao(tempoLimiteResolucao);
		item.setQtApontamentos(rSet.getInt("QT_APONTAMENTOS"));
		item.setDataResolucao(rSet.getTimestamp("DATA_RESOLUCAO"));
		item.setFeedbackResolucao(rSet.getString("STATUS_RESOLUCAO"));
		item.setCpfMecanico(rSet.getLong("CPF_FROTA"));
		item.setNomeMecanico(rSet.getString("NOME"));
		item.setPrioridade(rSet.getString("PRIORIDADE"));
		setLongTempoRestante(item);
		return item;
	}
	/**
	 * Marca como resolvido um item em aberto, salvando o cpf de quem consertou e a data/hora
	 * @param request contém o item que foi consertado e dados do solicitante
	 * @return 
	 * @throws SQLException 
	 */
	@Override
	public boolean consertaItem (Request<?> request) throws SQLException{
		Connection conn = null;
		PreparedStatement stmt = null;
		ItemManutencao itemManutencao = (ItemManutencao) request.getObject();
		try{
			conn = getConnection();

			stmt = conn.prepareStatement(""
					+ "UPDATE CHECKLIST_MANUTENCAO SET CPF_FROTA = ?, "
					+ "DATA_RESOLUCAO = ?, "
					+ "STATUS_RESOLUCAO = ? "
					+ "FROM TOKEN_AUTENTICACAO TA "
					+ "WHERE PLACA = ? AND "
					+ "ITEM = ? AND "
					+ "TA.CPF_COLABORADOR = ? AND "
					+ "TA.TOKEN = ? AND"
					+ "CPF_FROTA IS NULL");

			stmt.setLong(1, itemManutencao.getCpfMecanico());
			stmt.setTimestamp(2, DateUtils.toTimestamp(itemManutencao.getDataResolucao()));
			stmt.setString(3, itemManutencao.getFeedbackResolucao());
			stmt.setString(4, itemManutencao.getPlaca());
			stmt.setInt(5, itemManutencao.getCodItem());
			stmt.setLong(6, request.getCpf());
			stmt.setString(7, request.getToken());
			int count = stmt.executeUpdate();
			if(count == 0){
				throw new SQLException("Erro ao marcar o item como resolvido");
			}	
		}finally{
			closeConnection(conn, stmt, null);
		}
		return true;
	}

	private List<ItemChecklist> getListaDescricao(Long codUnidade, Connection conn) throws SQLException{
		PreparedStatement stmt = null;
		ResultSet rSet = null;
		List<ItemChecklist> list = new ArrayList<>();

		try{
			stmt = conn.prepareStatement("SELECT C.PLACA_VEICULO, CR.COD_PERGUNTA, "
					+ "C.DATA_HORA, CO.NOME, CO.CPF, CR.RESPOSTA  "
					+ "FROM CHECKLIST_RESPOSTAS CR JOIN "
					+ "CHECKLIST C ON C.CODIGO = CR.COD_CHECKLIST "
					+ "JOIN COLABORADOR CO ON CO.CPF = C.CPF_COLABORADOR "
					+ "JOIN VEICULO V ON V.PLACA = C.PLACA_VEICULO "
					+ "JOIN CHECKLIST_MANUTENCAO CM ON CM.ITEM = CR.COD_PERGUNTA AND CM.PLACA = C.PLACA_VEICULO "
					+ "WHERE CR.RESPOSTA <> 'S' AND V.COD_UNIDADE = ? AND "
					+ "CM.CPF_FROTA IS NULL AND C.DATA_HORA >= CM.DATA_APONTAMENTO "
					+ "ORDER BY PLACA_VEICULO, COD_PERGUNTA");
			stmt.setLong(1, codUnidade);
			rSet = stmt.executeQuery();
			while(rSet.next()){
				ItemChecklist item = new ItemChecklist();
				item.codPergunta = rSet.getInt("COD_PERGUNTA");
				item.placa = rSet.getString("PLACA_VEICULO");
				item.data = rSet.getTimestamp("DATA_HORA");
				item.cpf = rSet.getLong("CPF");
				item.nome = rSet.getString("NOME");
				item.descricao = rSet.getString("RESPOSTA");
				list.add(item);
			}
		}finally{
			closeConnection(null, stmt, rSet);
		}
		return list;
	}
	
    public void setLongTempoRestante(ItemManutencao itemManutencao) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(itemManutencao.getData());
        calendar.add(Calendar.HOUR, itemManutencao.getTempoLimiteResolucao().getHora());// data apontamento + prazo
        Date dataMaxima = calendar.getTime(); // data máxima de resolução
        long tempoRestante = dataMaxima.getTime() - System.currentTimeMillis();
        Tempo tempo = new Tempo();
        long temp = TimeUnit.MILLISECONDS.toMinutes(tempoRestante);
        itemManutencao.setTempoRestante(tempo);
        //TODO setar tempo restante
        
/*        if (temp < MINUTOS_NUMA_HORA) {
            tempo.setMinuto((int)temp);
        } else if (temp < MINUTOS_NUM_DIA) {
            long hours = TimeUnit.MINUTES.toHours(temp);
            temp = hours % 60;
            // TODO: Se minutos for zero não precisa mostrar
            tempo.setHora((int)hours);
            tempo.setMinuto((int)temp);
        } else if (minutes >= MINUTOS_NUM_DIA) {
            long days = TimeUnit.MINUTES.toDays(minutes);
            long hours = TimeUnit.MINUTES.toHours(minutes) % 24;
            // TODO: Se horas for zero não precisa mostrar
            return days + "dia(s) e " + hours + " hora(s)";
        }
        return "";*/
        
        
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
