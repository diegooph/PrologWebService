package br.com.zalf.prolog.webservice.dao;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import br.com.zalf.prolog.models.indicador.IndicadorHolder;
import br.com.zalf.prolog.models.indicador.Meta;
import br.com.zalf.prolog.models.relatorios.ConsolidadoDiaDev;
import br.com.zalf.prolog.models.relatorios.ConsolidadoIndicador;
import br.com.zalf.prolog.models.relatorios.ItemExtratoDiaDev;
import br.com.zalf.prolog.models.util.DateUtils;
import br.com.zalf.prolog.models.util.MetaUtils;
import br.com.zalf.prolog.webservice.DatabaseConnection;
import br.com.zalf.prolog.webservice.dao.interfaces.RelatorioDao;

public class RelatorioDaoImpl extends DatabaseConnection implements RelatorioDao {

	public static final String BUSCA_INDICADORES_EQUIPE = "SELECT M.DATA, M.PLACA, M.MAPA, C.NOME AS NOMEMOTORISTA, "
			+ "C1.NOME AS NOMEAJUD1, C2.NOME AS NOMEAJUD2, C.EQUIPE, "
			+ "M.CXCARREG, M.CXENTREG, (M.CXCARREG - M.CXENTREG) AS DEVCX, "
			+ "M.QTHLCARREGADOS, M.QTHLENTREGUES, (M.QTHLCARREGADOS - M.QTHLENTREGUES) AS DEVHL, "
			+ "M.QTNFCARREGADAS, M.QTNFENTREGUES, (M.QTNFCARREGADAS - M.QTNFENTREGUES) AS DEVNF, "
			+ "M.HRSAI, M.HRENTR, M.TEMPOINTERNO, M.HRMATINAL,TRACKING.TOTAL AS TOTAL_TRACKING, TRACKING.APONTAMENTO_OK "
			+ " FROM MAPA M join token_autenticacao ta on ? = ta.cpf_colaborador and ? = ta.token "
			+ "JOIN VEICULO V ON V.PLACA = M.PLACA "
			+ "JOIN COLABORADOR C ON M.MATRICMOTORISTA = C.MATRICULA_AMBEV "
			+ "JOIN COLABORADOR C1 ON M.MATRICAJUD1 = C1.MATRICULA_AMBEV "
			+ "JOIN COLABORADOR C2 ON M.MATRICAJUD2 = C2.MATRICULA_AMBEV "
			+ "LEFT JOIN( SELECT t.mapa AS TRACKING_MAPA, total.total AS TOTAL, ok.APONTAMENTOS_OK AS APONTAMENTO_OK "
			+ "FROM tracking t join MAPA M on m.mapa = t.mapa "
			+ "JOIN (SELECT t.mapa as mapa_ok, count(t.disp_apont_cadastrado) as apontamentos_ok "
			+ "FROM tracking t	WHERE t.disp_apont_cadastrado <= '0.3' "
			+ "GROUP BY t.mapa) as ok on mapa_ok = t.mapa "
			+ "JOIN (SELECT t.mapa as total_entregas, count(t.cod_cliente) as total from tracking t "
			+ "group by t.mapa) as total on total_entregas = t.mapa "
			+ "GROUP BY t.mapa, OK.APONTAMENTOS_OK, total.total) AS TRACKING ON TRACKING_MAPA = M.MAPA "
			+ "WHERE c.equipe = ? AND DATA BETWEEN ? AND ? "
			+ "ORDER BY M.DATA ";


	private Meta meta;
	private ConsolidadoIndicador consolidadoIndicador;


	@Override
	public IndicadorHolder getIndicadoresEquipeByPeriodo(LocalDate dataInicial, LocalDate dataFinal, String equipe,
			Long cpf, String token) throws SQLException {

		Connection conn = null;
		PreparedStatement stmt = null;
		ResultSet rSet = null;
		MetasDao metasDao = new MetasDao();
		meta = metasDao.getMetas(cpf);

		try {
			conn = getConnection();
			stmt = conn.prepareStatement(BUSCA_INDICADORES_EQUIPE, ResultSet.TYPE_SCROLL_SENSITIVE,
					ResultSet.CONCUR_UPDATABLE);
			stmt.setLong(1, cpf); 
			stmt.setString(2, token); 
			stmt.setString(3, equipe);
			stmt.setDate(4, DateUtils.toSqlDate(dataInicial));
			stmt.setDate(5, DateUtils.toSqlDate(dataFinal));
			rSet = stmt.executeQuery();
			consolidadoIndicador = new ConsolidadoIndicador();			
			List<ItemExtratoDiaDev> listExtratoTemp = createDevCx(rSet);// recebe uma lista com todos os resultados do rSet, de todos os dias
			List<ConsolidadoDiaDev> listConsolidadoTemp = getConsolidadoDiaDev(listExtratoTemp);//recebe uma lista dos consolidados de cada dia, dentro de cada consolidado tem uma lista com o extrato de cada dia, ou seja, todos os itens.
			consolidadoIndicador.setListDevCx(listConsolidadoTemp); // atribui a lista do consolidado de devCx ao atributo do objeto consolidadoIndicador
			listExtratoTemp = createDevNf(rSet);
			listConsolidadoTemp = getConsolidadoDiaDev(listExtratoTemp);
			consolidadoIndicador.setListDevNf(listConsolidadoTemp);
			listExtratoTemp = createDevCx(rSet);
			listConsolidadoTemp = getConsolidadoDiaDev(listExtratoTemp);
			consolidadoIndicador.setListDevHl(listConsolidadoTemp);
			System.out.println(consolidadoIndicador);
		} finally {
			closeConnection(conn, stmt, rSet);
		}

		return  new IndicadorHolder();
	}

	private List<ItemExtratoDiaDev> createDevCx(ResultSet rset) throws SQLException{
// percorre o rSet e cria uma lista com os objetos da devolução em caixas
		List<ItemExtratoDiaDev> listDev = new ArrayList<>();
		while(rset.next()){
			ItemExtratoDiaDev devCx = new ItemExtratoDiaDev();
			devCx.setData(rset.getDate("DATA"));
			devCx.setPlaca(rset.getString("PLACA"));
			devCx.setMapa(rset.getInt("MAPA"));
			devCx.setColab1(rset.getString("NOMEMOTORISTA"));
			devCx.setColab2(rset.getString("NOMEAJUD1"));
			devCx.setColab3(rset.getString("NOMEAJUD2"));
			devCx.setEquipe(rset.getString("EQUIPE"));
			devCx.setCarreg(rset.getDouble("CXCARREG"));
			devCx.setEntreg(rset.getDouble("CXENTREG"));
			devCx.setDev(rset.getDouble("DEVCX"));
			devCx.setResultado(devCx.getDev() / devCx.getCarreg());
			devCx.setMeta(meta.getMetaDevCx());
			devCx.setBateuMeta(MetaUtils.bateuMeta(devCx.getResultado(), devCx.getMeta()));
			listDev.add(devCx);
		}	
		return listDev;
	}

	private List<ItemExtratoDiaDev> createDevNf(ResultSet rset) throws SQLException{
		//percorre o rSet e cria uma lista com os objetos da devolução em Nota Fiscal
		List<ItemExtratoDiaDev> listDev = new ArrayList<>();
		while(rset.next()){
			ItemExtratoDiaDev devNf = new ItemExtratoDiaDev();
			devNf.setData(rset.getDate("DATA"));
			devNf.setPlaca(rset.getString("PLACA"));
			devNf.setMapa(rset.getInt("MAPA"));
			devNf.setColab1(rset.getString("NOMEMOTORISTA"));
			devNf.setColab2(rset.getString("NOMEAJUD1"));
			devNf.setColab3(rset.getString("NOMEAJUD2"));
			devNf.setEquipe(rset.getString("EQUIPE"));
			devNf.setCarreg(rset.getDouble("QTNFCARREGADAS"));
			devNf.setEntreg(rset.getDouble("QTNFENTREGUES"));
			devNf.setDev(rset.getDouble("DEVNF"));
			devNf.setResultado(devNf.getDev() / devNf.getCarreg());
			devNf.setMeta(meta.getMetaDevNf());
			devNf.setBateuMeta(MetaUtils.bateuMeta(devNf.getResultado(), devNf.getMeta()));
			listDev.add(devNf);
		}	
		return listDev;
	}

	private List<ItemExtratoDiaDev> createDevHl(ResultSet rset) throws SQLException{
		//percorre o rSet e cria uma lista com os objetos da devolução em Hectolitro
		List<ItemExtratoDiaDev> listDev = new ArrayList<>();
		while(rset.next()){
			ItemExtratoDiaDev devHl = new ItemExtratoDiaDev();
			devHl.setData(rset.getDate("DATA"));
			devHl.setPlaca(rset.getString("PLACA"));
			devHl.setMapa(rset.getInt("MAPA"));
			devHl.setColab1(rset.getString("NOMEMOTORISTA"));
			devHl.setColab2(rset.getString("NOMEAJUD1"));
			devHl.setColab3(rset.getString("NOMEAJUD2"));
			devHl.setEquipe(rset.getString("EQUIPE"));
			devHl.setCarreg(rset.getDouble("QTHLCARREGADOS"));
			devHl.setEntreg(rset.getDouble("QTHLENTREGUES"));
			devHl.setDev(rset.getDouble("DEVHL"));
			devHl.setResultado(devHl.getDev() / devHl.getCarreg());
			devHl.setMeta(meta.getMetaDevHl());
			devHl.setBateuMeta(MetaUtils.bateuMeta(devHl.getResultado(), devHl.getMeta()));
			listDev.add(devHl);
		}	
		return listDev;
	}

	private List<ConsolidadoDiaDev> getConsolidadoDiaDev (List<ItemExtratoDiaDev> listaTotal){
// recebe uma lista com todos os itens de algum tipo de devolução (cx, nf ou hl), consolida por dia.
		List<ConsolidadoDiaDev> listConsolidadoDia = new ArrayList<>(); // Lista do consolidado dos dias, cada elemento contem uma lista com dias sem repetição
		ConsolidadoDiaDev consolidadoDia = new ConsolidadoDiaDev(); // contem uma lista com elementos do mesmo dia
		List<ItemExtratoDiaDev> listaDia = new ArrayList<>(); // contem itens do mesmo dia
		double totalCarreg = 0;
		double totalEntreg = 0;
		for(int itemAtual =1; itemAtual < listaTotal.size(); itemAtual++){
			// verifica se o item atual da lista é igual ao anterior, ex: compara a pos 2 com a pos 1
			if(listaTotal.get(itemAtual).getData().getTime() == listaTotal.get(itemAtual - 1).getData().getTime()){
				// adiciona o item de posição anterior à lista temporaria				
				listaDia.add(listaTotal.get(itemAtual-1));
				totalCarreg = totalCarreg + listaTotal.get(itemAtual - 1).getCarreg();
				totalEntreg = totalEntreg + listaTotal.get(itemAtual - 1).getEntreg();
				if(listaTotal.size()-1 == itemAtual){
					listaDia.add(listaTotal.get(itemAtual));
					totalCarreg = totalCarreg + listaTotal.get(itemAtual).getCarreg();
					totalEntreg = totalEntreg + listaTotal.get(itemAtual).getEntreg();
					consolidadoDia.setDevolucaoRelList(listaDia);
					// setar acumulados
					setTotaisConsolidadoDiaDev(consolidadoDia, totalCarreg, totalEntreg, listaTotal.get(itemAtual).getMeta(), 
							listaTotal.get(itemAtual).getEquipe(), listaTotal.get(itemAtual).getData());
					listConsolidadoDia.add(consolidadoDia);
					break;
				}
			}else{
				listaDia.add(listaTotal.get(itemAtual-1));
				totalCarreg = totalCarreg + listaTotal.get(itemAtual - 1).getCarreg();
				totalEntreg = totalEntreg + listaTotal.get(itemAtual - 1).getEntreg();
				consolidadoDia.setDevolucaoRelList(listaDia);
				// setar acumulados
				setTotaisConsolidadoDiaDev(consolidadoDia, totalCarreg, totalEntreg, listaTotal.get(itemAtual-1).getMeta(), 
						listaTotal.get(itemAtual-1).getEquipe(), listaTotal.get(itemAtual-1).getData());
				listConsolidadoDia.add(consolidadoDia);
				consolidadoDia = new ConsolidadoDiaDev();
				listaDia = new ArrayList<>();
				totalCarreg = 0;
				totalEntreg = 0;
				if(listaTotal.size() -1 == itemAtual ){
					listaDia.add(listaTotal.get(itemAtual));
					totalCarreg = totalCarreg + listaTotal.get(itemAtual).getCarreg();
					totalEntreg = totalEntreg + listaTotal.get(itemAtual).getEntreg();
					consolidadoDia.setDevolucaoRelList(listaDia);
					// setar acumulados
					setTotaisConsolidadoDiaDev(consolidadoDia, totalCarreg, totalEntreg, listaTotal.get(itemAtual).getMeta(), 
							listaTotal.get(itemAtual).getEquipe(), listaTotal.get(itemAtual).getData());
					listConsolidadoDia.add(consolidadoDia);
					break;
				}
			}
		}
		return listConsolidadoDia;
	}

	private void setTotaisConsolidadoDiaDev (ConsolidadoDiaDev consolidadoDiaDev, double carregadas, double entregues,double meta, String equipe, Date data){
		consolidadoDiaDev.setData(data);
		consolidadoDiaDev.setTotalCarreg(carregadas);
		consolidadoDiaDev.setTotalEntreg(entregues);
		consolidadoDiaDev.setTotalDev(carregadas - entregues);
		consolidadoDiaDev.setResultado(consolidadoDiaDev.getTotalDev() / carregadas);
		consolidadoDiaDev.setMeta(meta);
		consolidadoDiaDev.setBateuMeta(MetaUtils.bateuMeta(consolidadoDiaDev.getResultado(), meta));
		consolidadoDiaDev.setEquipe(equipe);
	}




	@Override
	public IndicadorHolder getIndicadoresUnidadeByPeriodo(LocalDate dataInicial, LocalDate dataFinal, int codUnidade,
			Long cpf, String token) throws SQLException {
		// TODO Auto-generated method stub
		return null;
	}

}
