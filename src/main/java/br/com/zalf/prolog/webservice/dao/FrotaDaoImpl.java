package br.com.zalf.prolog.webservice.dao;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import br.com.zalf.prolog.models.Request;
import br.com.zalf.prolog.models.frota.ItemManutencao;
import br.com.zalf.prolog.models.frota.ManutencaoHolder;
import br.com.zalf.prolog.models.util.DateUtils;
import br.com.zalf.prolog.webservice.DatabaseConnection;
import br.com.zalf.prolog.webservice.dao.interfaces.FrotaDao;

public class FrotaDaoImpl extends DatabaseConnection implements FrotaDao{

	@Override
	public List<ManutencaoHolder> getManutencaoHolder (Long cpf, String token, Long codUnidade, int limit, long offset, boolean isAbertos) throws SQLException{
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
			//query busca todos os itens de manutenção, independente se estão abertos ou fechados(com cpf_frota e data_fechamento)
			String query = "SELECT CM.DATA_APONTAMENTO, CM.PLACA, CM.ITEM, CP.PERGUNTA, CM.PRAZO, CM.QT_APONTAMENTOS, CM.STATUS_RESOLUCAO, "
					+ "CM.DATA_RESOLUCAO, CM.CPF_FROTA, C.NOME "
					+ "FROM CHECKLIST_MANUTENCAO CM "
					+ "JOIN VEICULO V ON V.PLACA = CM.PLACA "
					+ "JOIN CHECKLIST_PERGUNTAS CP ON CP.CODIGO = CM.ITEM "
					+ "JOIN TOKEN_AUTENTICACAO TA ON TA.CPF_COLABORADOR = ? AND TA.TOKEN = ? "
					+ "LEFT JOIN COLABORADOR C ON C.CPF = CM.CPF_FROTA "
					+ "WHERE V.COD_UNIDADE = ? "
					+ "AND CM.CPF_FROTA %s "
					+ "ORDER BY PLACA "
					+ "LIMIT ? OFFSET ?";
			if(isAbertos){
				query = String.format(query, "IS NULL");
			}else{
				query = String.format(query, ">0");
			}	
			stmt = conn.prepareStatement(query, ResultSet.TYPE_SCROLL_SENSITIVE,
					ResultSet.CONCUR_UPDATABLE);
			stmt.setLong(1, cpf);
			stmt.setString(2, token);
			stmt.setLong(3, codUnidade);
			stmt.setInt(4, limit);
			stmt.setLong(5, offset);
			rSet = stmt.executeQuery();

			if(rSet.first()){
				itemManutencao = createItemManutencao(rSet);
				list.add(itemManutencao);
				holder.setPlaca(rSet.getString("PLACA"));
			}
			while(rSet.next()){
				if(rSet.getString("PLACA").equals(holder.getPlaca())){
					itemManutencao = createItemManutencao(rSet);
					list.add(itemManutencao);
				}else{
					holder.setListManutencao(list);
					listHolder.add(holder);
					//TODO SETAR QUANTIDADE DE ITENS PELA PRIORIDADE
					holder = new ManutencaoHolder();
					holder.setPlaca(rSet.getString("PLACA"));
					itemManutencao = createItemManutencao(rSet);
					list = new ArrayList<>();
					list.add(itemManutencao);
				}
			}
			holder.setListManutencao(list);
			//TODO SETAR QUANTIDADE DE ITENS PELA PRIORIDADE
			listHolder.add(holder);
			listItemChecklist = getListaDescricao(codUnidade, conn);
		}
		finally{
			closeConnection(conn, stmt, rSet);
		}
		System.out.print(listHolder);
		//setDescricaoItens(listItemChecklist, listHolder);
		return listHolder;
	}
	
//	private void setDescricaoItens(List<ItemChecklist> listItemChecklist, List<ManutencaoHolder> holder){
//		
//		for(ManutencaoHolder itemManutencao : holder){ // item manutenção contendo a placa e a lista de itens quebrados dessa placa
//			List<ItemManutencao> listItemManutencao = itemManutencao.getListManutencao(); // contém cada item quebrado de uma mesma placa
//			for(ItemManutencao item : listItemManutencao){
//				
//				
//					
//				}
//			}
//		}
//		
//		
//		
//	}

	private ItemManutencao createItemManutencao(ResultSet rSet) throws SQLException{
		ItemManutencao item = new ItemManutencao();
		item.setData(rSet.getTimestamp("DATA_APONTAMENTO"));
		item.setCodItem(rSet.getInt("ITEM"));
		item.setItem(rSet.getString("PERGUNTA"));
		item.setPrazo(rSet.getInt("PRAZO"));
		item.setQtApontamentos(rSet.getInt("QT_APONTAMENTOS"));
		item.setDataResolucao(rSet.getTimestamp("DATA_RESOLUCAO"));
		item.setStatusResolucao(rSet.getString("STATUS_RESOLUCAO"));
		item.setCpfFrota(rSet.getLong("CPF_FROTA"));
		item.setNomeFrota(rSet.getString("NOME"));
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

			stmt.setLong(1, itemManutencao.getCpfFrota());
			stmt.setTimestamp(2, DateUtils.toTimestamp(itemManutencao.getDataResolucao()));
			stmt.setString(3, itemManutencao.getStatusResolucao());
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
					+ "C.DATA_HORA, CO.NOME, CR.RESPOSTA  "
					+ "FROM CHECKLIST_RESPOSTAS CR JOIN "
					+ "CHECKLIST C ON C.CODIGO = CR.COD_CHECKLIST "
					+ "JOIN COLABORADOR CO ON CO.CPF = C.CPF_COLABORADOR "
					+ "JOIN VEICULO V ON V.PLACA = C.PLACA_VEICULO "
					+ "JOIN CHECKLIST_MANUTENCAO CM ON CM.ITEM = CR.COD_PERGUNTA "
					+ "WHERE CR.RESPOSTA <> 'S' AND V.COD_UNIDADE = ? AND "
					+ "CM.CPF_FROTA IS NULL ORDER BY PLACA_VEICULO, COD_PERGUNTA");
			stmt.setLong(1, codUnidade);
			rSet = stmt.executeQuery();
			while(rSet.next()){
				ItemChecklist item = new ItemChecklist();
				item.codPergunta = rSet.getInt("COD_PERGUNTA");
				item.placa = rSet.getString("PLACA_VEICULO");
				item.data = DateUtils.toSqlDate(rSet.getTimestamp("DATA_HORA"));
				item.nome = rSet.getString("NOME");
				item.descricao = rSet.getString("RESPOSTA");
				list.add(item);
			}
		}finally{
			closeConnection(null, stmt, rSet);
		}
		return list;
	}

	public class ItemChecklist{

		public String placa;
		public int codPergunta;
		public Date data;
		public String nome;
		public String descricao;

		public ItemChecklist() {
			// TODO Auto-generated constructor stub
		}

	}

}
