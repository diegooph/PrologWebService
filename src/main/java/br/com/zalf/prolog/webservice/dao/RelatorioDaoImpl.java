package br.com.zalf.prolog.webservice.dao;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import br.com.empresa.oprojeto.models.util.TimeUtils;
import br.com.zalf.prolog.models.indicador.IndicadorHolder;
import br.com.zalf.prolog.models.indicador.Meta;
import br.com.zalf.prolog.models.relatorios.ConsolidadoDiaDev;
import br.com.zalf.prolog.models.relatorios.ConsolidadoDiaTempoInterno;
import br.com.zalf.prolog.models.relatorios.ConsolidadoDiaTempoLargada;
import br.com.zalf.prolog.models.relatorios.ConsolidadoDiaTempoRota;
import br.com.zalf.prolog.models.relatorios.ConsolidadoIndicador;
import br.com.zalf.prolog.models.relatorios.ItemExtratoDiaDev;
import br.com.zalf.prolog.models.relatorios.ItemExtratoDiaJornadaLiquida;
import br.com.zalf.prolog.models.relatorios.ItemExtratoDiaTempoInterno;
import br.com.zalf.prolog.models.relatorios.ItemExtratoDiaTempoLargada;
import br.com.zalf.prolog.models.relatorios.ItemExtratoDiaTempoRota;
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
			//consolidadoIndicador.setListDevCx(listConsolidadoTemp); // atribui a lista do consolidado de devCx ao atributo do objeto consolidadoIndicador
			listExtratoTemp = createDevNf(rSet);// cria uma lista extraindo apenas os itens de DevNf do rSet.
			listConsolidadoTemp = getConsolidadoDiaDev(listExtratoTemp); // recebe uma lista com os consolidados dos dias da DevNf
			//consolidadoIndicador.setListDevNf(listConsolidadoTemp);
			listExtratoTemp = createDevHl(rSet);
			listConsolidadoTemp = getConsolidadoDiaDev(listExtratoTemp);
			//consolidadoIndicador.setListDevHl(listConsolidadoTemp);
			List<ItemExtratoDiaTempoLargada> listTempoLargada = createTempoLargada(rSet);
			List<ConsolidadoDiaTempoLargada> listConsolidadoTempoLargada = getConsolidadoTempoLargada(listTempoLargada);
			//consolidadoIndicador.setListTempoLargada(listConsolidadoTempoLargada);
			List<ItemExtratoDiaTempoRota> listTempoRota = createTempoRota(rSet);
			List<ConsolidadoDiaTempoRota> listConsolidadoTempoRota = getConsolidadoTempoRota(listTempoRota);
			//consolidadoIndicador.setListTempoRota(listConsolidadoTempoRota);
			List<ItemExtratoDiaTempoInterno> listTempoInterno = createTempoInterno(rSet);
			List<ConsolidadoDiaTempoInterno> listConsolidadoTempoInterno = getConsolidadoTempoInterno(listTempoInterno);
			consolidadoIndicador.setListTempoInterno(listConsolidadoTempoInterno);
			List<ItemExtratoDiaJornadaLiquida> listJornadaLiquida = createJornadaLiquida(rSet);
			System.out.println(consolidadoIndicador);
		} finally {
			closeConnection(conn, stmt, rSet);
		}

		return  new IndicadorHolder();
	}

	private List<ItemExtratoDiaDev> createDevCx(ResultSet rSet) throws SQLException{
		// percorre o rSet e cria uma lista com os objetos da devolução em caixas
		List<ItemExtratoDiaDev> listDev = new ArrayList<>();
		while(rSet.next()){
			ItemExtratoDiaDev devCx = new ItemExtratoDiaDev();
			devCx.setData(rSet.getDate("DATA"));
			devCx.setPlaca(rSet.getString("PLACA"));
			devCx.setMapa(rSet.getInt("MAPA"));
			devCx.setColab1(rSet.getString("NOMEMOTORISTA"));
			devCx.setColab2(rSet.getString("NOMEAJUD1"));
			devCx.setColab3(rSet.getString("NOMEAJUD2"));
			devCx.setEquipe(rSet.getString("EQUIPE"));
			devCx.setCarreg(rSet.getDouble("CXCARREG"));
			devCx.setEntreg(rSet.getDouble("CXENTREG"));
			devCx.setDev(rSet.getDouble("DEVCX"));
			devCx.setResultado(devCx.getDev() / devCx.getCarreg());
			devCx.setMeta(meta.getMetaDevCx());
			devCx.setBateuMeta(MetaUtils.bateuMeta(devCx.getResultado(), devCx.getMeta()));
			listDev.add(devCx);
		}	
		rSet.beforeFirst();
		return listDev;
	}

	private List<ItemExtratoDiaDev> createDevNf(ResultSet rSet) throws SQLException{
		//percorre o rSet e cria uma lista com os objetos da devolução em Nota Fiscal
		List<ItemExtratoDiaDev> listDev = new ArrayList<>();
		while(rSet.next()){
			ItemExtratoDiaDev devNf = new ItemExtratoDiaDev();
			devNf.setData(rSet.getDate("DATA"));
			devNf.setPlaca(rSet.getString("PLACA"));
			devNf.setMapa(rSet.getInt("MAPA"));
			devNf.setColab1(rSet.getString("NOMEMOTORISTA"));
			devNf.setColab2(rSet.getString("NOMEAJUD1"));
			devNf.setColab3(rSet.getString("NOMEAJUD2"));
			devNf.setEquipe(rSet.getString("EQUIPE"));
			devNf.setCarreg(rSet.getDouble("QTNFCARREGADAS"));
			devNf.setEntreg(rSet.getDouble("QTNFENTREGUES"));
			devNf.setDev(rSet.getDouble("DEVNF"));
			devNf.setResultado(devNf.getDev() / devNf.getCarreg());
			devNf.setMeta(meta.getMetaDevNf());
			devNf.setBateuMeta(MetaUtils.bateuMeta(devNf.getResultado(), devNf.getMeta()));
			listDev.add(devNf);
		}
		rSet.beforeFirst();
		return listDev;
	}

	private List<ItemExtratoDiaDev> createDevHl(ResultSet rSet) throws SQLException{
		//percorre o rSet e cria uma lista com os objetos da devolução em Hectolitro
		List<ItemExtratoDiaDev> listDev = new ArrayList<>();
		while(rSet.next()){
			ItemExtratoDiaDev devHl = new ItemExtratoDiaDev();
			devHl.setData(rSet.getDate("DATA"));
			devHl.setPlaca(rSet.getString("PLACA"));
			devHl.setMapa(rSet.getInt("MAPA"));
			devHl.setColab1(rSet.getString("NOMEMOTORISTA"));
			devHl.setColab2(rSet.getString("NOMEAJUD1"));
			devHl.setColab3(rSet.getString("NOMEAJUD2"));
			devHl.setEquipe(rSet.getString("EQUIPE"));
			devHl.setCarreg(rSet.getDouble("QTHLCARREGADOS"));
			devHl.setEntreg(rSet.getDouble("QTHLENTREGUES"));
			devHl.setDev(rSet.getDouble("DEVHL"));
			devHl.setResultado(devHl.getDev() / devHl.getCarreg());
			devHl.setMeta(meta.getMetaDevHl());
			devHl.setBateuMeta(MetaUtils.bateuMeta(devHl.getResultado(), devHl.getMeta()));
			listDev.add(devHl);
		}	
		rSet.beforeFirst();
		return listDev;
	}

	private List<ItemExtratoDiaTempoLargada> createTempoLargada (ResultSet rSet) throws SQLException{
		List<ItemExtratoDiaTempoLargada> listTempoLargada = new ArrayList<>();
		while(rSet.next()){
			ItemExtratoDiaTempoLargada itemExtratoDiaTempoLargada = new ItemExtratoDiaTempoLargada();
			itemExtratoDiaTempoLargada.setData(rSet.getDate("DATA"));
			itemExtratoDiaTempoLargada.setHrMatinal(rSet.getTime("HRMATINAL"));
			itemExtratoDiaTempoLargada.setHrSaida(TimeUtils.toSqlTime(rSet.getTimestamp("HRSAI")));
			itemExtratoDiaTempoLargada.setResultado(
					MetaUtils.calculaTempoLargada(itemExtratoDiaTempoLargada.getHrSaida(), itemExtratoDiaTempoLargada.getHrMatinal()));
			itemExtratoDiaTempoLargada.setMeta(meta.getMetaTempoLargadaHoras());
			itemExtratoDiaTempoLargada.setBateuMeta(
					MetaUtils.bateuMeta(itemExtratoDiaTempoLargada.getResultado(), meta.getMetaTempoLargadaHoras()));
			itemExtratoDiaTempoLargada.setPlaca(rSet.getString("PLACA"));
			itemExtratoDiaTempoLargada.setMapa(rSet.getInt("MAPA"));
			itemExtratoDiaTempoLargada.setColab1(rSet.getString("NOMEMOTORISTA"));
			itemExtratoDiaTempoLargada.setColab2(rSet.getString("NOMEAJUD1"));
			itemExtratoDiaTempoLargada.setColab3(rSet.getString("NOMEAJUD2"));
			itemExtratoDiaTempoLargada.setEquipe(rSet.getString("EQUIPE"));			
			listTempoLargada.add(itemExtratoDiaTempoLargada);			
		}
		rSet.beforeFirst();
		return listTempoLargada;
	}

	private List<ItemExtratoDiaTempoRota> createTempoRota (ResultSet rSet) throws SQLException{ 
		List<ItemExtratoDiaTempoRota> listTempoRota = new ArrayList<>();
		while(rSet.next()){
			ItemExtratoDiaTempoRota itemExtratoDiaTempoRota = new ItemExtratoDiaTempoRota();
			itemExtratoDiaTempoRota.setData(rSet.getDate("DATA"));
			itemExtratoDiaTempoRota.setHrSaida(TimeUtils.toSqlTime(rSet.getTimestamp("HRSAI")));
			itemExtratoDiaTempoRota.setHrEntrada(TimeUtils.toSqlTime(rSet.getTimestamp("HRENTR")));
			itemExtratoDiaTempoRota.setResultado(
					TimeUtils.differenceBetween(itemExtratoDiaTempoRota.getHrEntrada(), itemExtratoDiaTempoRota.getHrSaida()));		    
			itemExtratoDiaTempoRota.setMeta(meta.getMetaTempoRotaHoras());
			itemExtratoDiaTempoRota.setBateuMeta(MetaUtils.bateuMeta(itemExtratoDiaTempoRota.getResultado(), meta.getMetaTempoRotaHoras()));
			itemExtratoDiaTempoRota.setPlaca(rSet.getString("PLACA"));
			itemExtratoDiaTempoRota.setMapa(rSet.getInt("MAPA"));
			itemExtratoDiaTempoRota.setColab1(rSet.getString("NOMEMOTORISTA"));
			itemExtratoDiaTempoRota.setColab2(rSet.getString("NOMEAJUD1"));
			itemExtratoDiaTempoRota.setColab3(rSet.getString("NOMEAJUD2"));
			itemExtratoDiaTempoRota.setEquipe(rSet.getString("EQUIPE"));
			listTempoRota.add(itemExtratoDiaTempoRota);
		}
		rSet.beforeFirst();
		return listTempoRota;
	}

	private List<ItemExtratoDiaTempoInterno> createTempoInterno (ResultSet rSet) throws SQLException{
	List<ItemExtratoDiaTempoInterno> listTempoInterno = new ArrayList<>();
	while(rSet.next()){
		ItemExtratoDiaTempoInterno itemTempoInterno = new ItemExtratoDiaTempoInterno();
		itemTempoInterno.setData(rSet.getDate("DATA"));
		itemTempoInterno.setPlaca(rSet.getString("PLACA"));
		itemTempoInterno.setMapa(rSet.getInt("MAPA"));
		itemTempoInterno.setColab1(rSet.getString("NOMEMOTORISTA"));
		itemTempoInterno.setColab2(rSet.getString("NOMEAJUD1"));
		itemTempoInterno.setColab3(rSet.getString("NOMEAJUD2"));
		itemTempoInterno.setEquipe(rSet.getString("EQUIPE"));
		itemTempoInterno.setHrEntrada(TimeUtils.toSqlTime(rSet.getTimestamp("HRENTR")));
	    itemTempoInterno.setHrFechamento(TimeUtils.somaHoras(itemTempoInterno.getHrEntrada(), rSet.getTime("TEMPOINTERNO")));
	    itemTempoInterno.setResultado(rSet.getTime("TEMPOINTERNO"));
	    itemTempoInterno.setMeta(meta.getMetaTempoInternoHoras());
	    itemTempoInterno.setBateuMeta(MetaUtils.bateuMeta(itemTempoInterno.getResultado(), meta.getMetaTempoInternoHoras()));
	    listTempoInterno.add(itemTempoInterno);
	}
		rSet.beforeFirst();
		return listTempoInterno;
	}
	
	private List<ItemExtratoDiaJornadaLiquida> createJornadaLiquida (ResultSet rSet) throws SQLException{
		List<ItemExtratoDiaJornadaLiquida> listJornadaLiquida = new ArrayList<>();
		while(rSet.next()){
			ItemExtratoDiaJornadaLiquida itemJornadaLiquida = new ItemExtratoDiaJornadaLiquida();
			itemJornadaLiquida.setData(rSet.getDate("DATA"));
			itemJornadaLiquida.setPlaca(rSet.getString("PLACA"));
			itemJornadaLiquida.setMapa(rSet.getInt("MAPA"));
			itemJornadaLiquida.setColab1(rSet.getString("NOMEMOTORISTA"));
			itemJornadaLiquida.setColab2(rSet.getString("NOMEAJUD1"));
			itemJornadaLiquida.setColab3(rSet.getString("NOMEAJUD2"));
			itemJornadaLiquida.setEquipe(rSet.getString("EQUIPE"));
			itemJornadaLiquida.setTempoInterno(rSet.getTime("TEMPOINTERNO"));		    
		    itemJornadaLiquida.setTempoRota(TimeUtils.differenceBetween(
		    		TimeUtils.toSqlTime(rSet.getTimestamp("HRENTR")), TimeUtils.toSqlTime(rSet.getTimestamp("HRSAI"))));
		    itemJornadaLiquida.setTempoLargada(MetaUtils.calculaTempoLargada(TimeUtils.toSqlTime(rSet.getTimestamp("HRSAI")),
					rSet.getTime("HRMATINAL")));
		    itemJornadaLiquida.setMeta(meta.getMetaJornadaLiquidaHoras());				    
		    
		    
		   // private double meta;
		    //private Time resultado;
		    //private boolean bateuMeta;

			
			
			
			
			
			
			listJornadaLiquida.add(itemJornadaLiquida);
		}
		rSet.beforeFirst();
		return listJornadaLiquida;
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

	private List<ConsolidadoDiaTempoLargada> getConsolidadoTempoLargada (List<ItemExtratoDiaTempoLargada> listaTotal){
		// recebe uma lista com todos os itens de algum tipo de devolução (cx, nf ou hl), consolida por dia.
		List<ConsolidadoDiaTempoLargada> listConsolidadoDia = new ArrayList<>(); // Lista do consolidado dos dias, cada elemento contem uma lista com dias sem repetição
		ConsolidadoDiaTempoLargada consolidadoDia = new ConsolidadoDiaTempoLargada(); // contem uma lista com elementos do mesmo dia
		List<ItemExtratoDiaTempoLargada> listaDia = new ArrayList<>(); // contem itens do mesmo dia
		int totalMapas = 0;
		int mapasOk = 0;
		int mapasNok = 0;
		for(int itemAtual =1; itemAtual < listaTotal.size(); itemAtual++){
			// verifica se o item atual da lista é igual ao anterior, ex: compara a pos 2 com a pos 1
			if(listaTotal.get(itemAtual).getData().getTime() == listaTotal.get(itemAtual - 1).getData().getTime()){
				// adiciona o item de posição anterior à lista temporaria				
				listaDia.add(listaTotal.get(itemAtual-1));
				if(listaTotal.get(itemAtual - 1).isBateuMeta()){
					mapasOk = mapasOk + 1;
				}else{
					mapasNok = mapasNok + 1;
				}
				totalMapas = totalMapas + 1;
				if(listaTotal.size()-1 == itemAtual){
					listaDia.add(listaTotal.get(itemAtual));
					if(listaTotal.get(itemAtual).isBateuMeta()){
						mapasOk = mapasOk + 1;
					}else{
						mapasNok = mapasNok + 1;
					}
					totalMapas = totalMapas + 1;
					consolidadoDia.setListTempoLargada(listaDia);
					// setar acumulados
					consolidadoDia.setTotalMapas(totalMapas);
					setTotaisConsolidadoDiaTempoLargada(consolidadoDia, totalMapas, mapasOk, mapasNok, 
							meta.getMetaTempoLargadaMapas(), listaTotal.get(itemAtual).getEquipe(), listaTotal.get(itemAtual).getData());
					listConsolidadoDia.add(consolidadoDia);
					break;
				}
			}else{
				listaDia.add(listaTotal.get(itemAtual-1));
				if(listaTotal.get(itemAtual-1).isBateuMeta()){
					mapasOk = mapasOk + 1;
				}else{
					mapasNok = mapasNok + 1;
				}
				totalMapas = totalMapas + 1;
				consolidadoDia.setListTempoLargada(listaDia);
				// setar acumulados
				setTotaisConsolidadoDiaTempoLargada(consolidadoDia, totalMapas, mapasOk, mapasNok, 
						meta.getMetaTempoLargadaMapas(), listaTotal.get(itemAtual-1).getEquipe(), listaTotal.get(itemAtual-1).getData());
				listConsolidadoDia.add(consolidadoDia);
				consolidadoDia = new ConsolidadoDiaTempoLargada();
				listaDia = new ArrayList<>();
				totalMapas = 0;
				mapasOk = 0;
				mapasNok = 0;
				if(listaTotal.size() -1 == itemAtual ){
					listaDia.add(listaTotal.get(itemAtual));
					if(listaTotal.get(itemAtual-1).isBateuMeta()){
						mapasOk = mapasOk + 1;
					}else{
						mapasNok = mapasNok + 1;
					}
					totalMapas = totalMapas + 1;
					consolidadoDia.setListTempoLargada(listaDia);
					// setar acumulados
					setTotaisConsolidadoDiaTempoLargada(consolidadoDia, totalMapas, mapasOk, mapasNok, 
							meta.getMetaTempoLargadaMapas(), listaTotal.get(itemAtual).getEquipe(), listaTotal.get(itemAtual).getData());
					listConsolidadoDia.add(consolidadoDia);
					break;
				}
			}
		}
		return listConsolidadoDia;
	}

	private List<ConsolidadoDiaTempoRota> getConsolidadoTempoRota (List<ItemExtratoDiaTempoRota> listaTotal){
		// recebe uma lista com todos os itens de algum tipo de devolução (cx, nf ou hl), consolida por dia.
				List<ConsolidadoDiaTempoRota> listConsolidadoDia = new ArrayList<>(); // Lista do consolidado dos dias, cada elemento contem uma lista com dias sem repetição
				ConsolidadoDiaTempoRota consolidadoDia = new ConsolidadoDiaTempoRota(); // contem uma lista com elementos do mesmo dia
				List<ItemExtratoDiaTempoRota> listaDia = new ArrayList<>(); // contem itens do mesmo dia
				int totalMapas = 0;
				int mapasOk = 0;
				int mapasNok = 0;
				for(int itemAtual =1; itemAtual < listaTotal.size(); itemAtual++){
					// verifica se o item atual da lista é igual ao anterior, ex: compara a pos 2 com a pos 1
					if(listaTotal.get(itemAtual).getData().getTime() == listaTotal.get(itemAtual - 1).getData().getTime()){
						// adiciona o item de posição anterior à lista temporaria				
						listaDia.add(listaTotal.get(itemAtual-1));
						if(listaTotal.get(itemAtual - 1).isBateuMeta()){
							mapasOk = mapasOk + 1;
						}else{
							mapasNok = mapasNok + 1;
						}
						totalMapas = totalMapas + 1;
						if(listaTotal.size()-1 == itemAtual){
							listaDia.add(listaTotal.get(itemAtual));
							if(listaTotal.get(itemAtual).isBateuMeta()){
								mapasOk = mapasOk + 1;
							}else{
								mapasNok = mapasNok + 1;
							}
							totalMapas = totalMapas + 1;
							consolidadoDia.setListTempoRota(listaDia);
							// setar acumulados
							consolidadoDia.setTotalMapas(totalMapas);
							setTotaisConsolidadoDiaTempoRota(consolidadoDia, totalMapas, mapasOk, mapasNok, 
									meta.getMetaTempoRotaMapas(), listaTotal.get(itemAtual).getEquipe(), listaTotal.get(itemAtual).getData());
							listConsolidadoDia.add(consolidadoDia);
							break;
						}
					}else{
						listaDia.add(listaTotal.get(itemAtual-1));
						if(listaTotal.get(itemAtual-1).isBateuMeta()){
							mapasOk = mapasOk + 1;
						}else{
							mapasNok = mapasNok + 1;
						}
						totalMapas = totalMapas + 1;
						consolidadoDia.setListTempoRota(listaDia);
						// setar acumulados
						setTotaisConsolidadoDiaTempoRota(consolidadoDia, totalMapas, mapasOk, mapasNok, 
								meta.getMetaTempoRotaMapas(), listaTotal.get(itemAtual-1).getEquipe(), listaTotal.get(itemAtual-1).getData());
						listConsolidadoDia.add(consolidadoDia);
						consolidadoDia = new ConsolidadoDiaTempoRota();
						listaDia = new ArrayList<>();
						totalMapas = 0;
						mapasOk = 0;
						mapasNok = 0;
						if(listaTotal.size() -1 == itemAtual ){
							listaDia.add(listaTotal.get(itemAtual));
							if(listaTotal.get(itemAtual-1).isBateuMeta()){
								mapasOk = mapasOk + 1;
							}else{
								mapasNok = mapasNok + 1;
							}
							totalMapas = totalMapas + 1;
							consolidadoDia.setListTempoRota(listaDia);
							// setar acumulados
							setTotaisConsolidadoDiaTempoRota(consolidadoDia, totalMapas, mapasOk, mapasNok, 
									meta.getMetaTempoRotaMapas(), listaTotal.get(itemAtual).getEquipe(), listaTotal.get(itemAtual).getData());
							listConsolidadoDia.add(consolidadoDia);
							break;
						}
					}
				}
				return listConsolidadoDia;
			}
	
	private List<ConsolidadoDiaTempoInterno> getConsolidadoTempoInterno (List<ItemExtratoDiaTempoInterno> listaTotal){
		// recebe uma lista com todos os itens de algum tipo de devolução (cx, nf ou hl), consolida por dia.
				List<ConsolidadoDiaTempoInterno> listConsolidadoDia = new ArrayList<>(); // Lista do consolidado dos dias, cada elemento contem uma lista com dias sem repetição
				ConsolidadoDiaTempoInterno consolidadoDia = new ConsolidadoDiaTempoInterno(); // contem uma lista com elementos do mesmo dia
				List<ItemExtratoDiaTempoInterno> listaDia = new ArrayList<>(); // contem itens do mesmo dia
				int totalMapas = 0;
				int mapasOk = 0;
				int mapasNok = 0;
				for(int itemAtual =1; itemAtual < listaTotal.size(); itemAtual++){
					// verifica se o item atual da lista é igual ao anterior, ex: compara a pos 2 com a pos 1
					if(listaTotal.get(itemAtual).getData().getTime() == listaTotal.get(itemAtual - 1).getData().getTime()){
						// adiciona o item de posição anterior à lista temporaria				
						listaDia.add(listaTotal.get(itemAtual-1));
						if(listaTotal.get(itemAtual - 1).isBateuMeta()){
							mapasOk = mapasOk + 1;
						}else{
							mapasNok = mapasNok + 1;
						}
						totalMapas = totalMapas + 1;
						if(listaTotal.size()-1 == itemAtual){
							listaDia.add(listaTotal.get(itemAtual));
							if(listaTotal.get(itemAtual).isBateuMeta()){
								mapasOk = mapasOk + 1;
							}else{
								mapasNok = mapasNok + 1;
							}
							totalMapas = totalMapas + 1;
							consolidadoDia.setListTempoInterno(listaDia);
							// setar acumulados
							consolidadoDia.setTotalMapas(totalMapas);
							setTotaisConsolidadoDiaTempoInterno(consolidadoDia, totalMapas, mapasOk, mapasNok, 
									meta.getMetaTempoInternoMapas(), listaTotal.get(itemAtual).getEquipe(), listaTotal.get(itemAtual).getData());
							listConsolidadoDia.add(consolidadoDia);
							break;
						}
					}else{
						listaDia.add(listaTotal.get(itemAtual-1));
						if(listaTotal.get(itemAtual-1).isBateuMeta()){
							mapasOk = mapasOk + 1;
						}else{
							mapasNok = mapasNok + 1;
						}
						totalMapas = totalMapas + 1;
						consolidadoDia.setListTempoInterno(listaDia);
						// setar acumulados
						setTotaisConsolidadoDiaTempoInterno(consolidadoDia, totalMapas, mapasOk, mapasNok, 
								meta.getMetaTempoInternoMapas(), listaTotal.get(itemAtual-1).getEquipe(), listaTotal.get(itemAtual-1).getData());
						listConsolidadoDia.add(consolidadoDia);
						consolidadoDia = new ConsolidadoDiaTempoInterno();
						listaDia = new ArrayList<>();
						totalMapas = 0;
						mapasOk = 0;
						mapasNok = 0;
						if(listaTotal.size() -1 == itemAtual ){
							listaDia.add(listaTotal.get(itemAtual));
							if(listaTotal.get(itemAtual-1).isBateuMeta()){
								mapasOk = mapasOk + 1;
							}else{
								mapasNok = mapasNok + 1;
							}
							totalMapas = totalMapas + 1;
							consolidadoDia.setListTempoInterno(listaDia);
							// setar acumulados
							setTotaisConsolidadoDiaTempoInterno(consolidadoDia, totalMapas, mapasOk, mapasNok, 
									meta.getMetaTempoInternoMapas(), listaTotal.get(itemAtual).getEquipe(), listaTotal.get(itemAtual).getData());
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

	private void setTotaisConsolidadoDiaTempoLargada (ConsolidadoDiaTempoLargada consolidadoDiaTempoLargada, 
			int totalMapas, int mapasOk, int mapasNok,  double meta, String equipe, Date data){

		consolidadoDiaTempoLargada.setData(data);
		consolidadoDiaTempoLargada.setEquipe(equipe);
		consolidadoDiaTempoLargada.setMeta(meta);
		consolidadoDiaTempoLargada.setTotalMapas(totalMapas);
		consolidadoDiaTempoLargada.setMapasOk(mapasOk);
		consolidadoDiaTempoLargada.setMapasNok(mapasNok);
		consolidadoDiaTempoLargada.setResultado((double) mapasOk / (double) totalMapas);
		consolidadoDiaTempoLargada.setBateuMeta(MetaUtils.bateuMetaMapas(consolidadoDiaTempoLargada.getResultado(), meta));

	}
	
	private void setTotaisConsolidadoDiaTempoRota (ConsolidadoDiaTempoRota consolidadoDiaTempoRota, 
			int totalMapas, int mapasOk, int mapasNok,  double meta, String equipe, Date data){

		consolidadoDiaTempoRota.setData(data);
		consolidadoDiaTempoRota.setEquipe(equipe);
		consolidadoDiaTempoRota.setMeta(meta);
		consolidadoDiaTempoRota.setTotalMapas(totalMapas);
		consolidadoDiaTempoRota.setMapasOk(mapasOk);
		consolidadoDiaTempoRota.setMapasNok(mapasNok);
		consolidadoDiaTempoRota.setResultado((double) mapasOk / (double) totalMapas);
		consolidadoDiaTempoRota.setBateuMeta(MetaUtils.bateuMetaMapas(consolidadoDiaTempoRota.getResultado(), meta));

	}

	private void setTotaisConsolidadoDiaTempoInterno (ConsolidadoDiaTempoInterno consolidadoDiaTempoInterno, 
			int totalMapas, int mapasOk, int mapasNok,  double meta, String equipe, Date data){
		
		consolidadoDiaTempoInterno.setData(data);
		consolidadoDiaTempoInterno.setEquipe(equipe);
		consolidadoDiaTempoInterno.setMeta(meta);
		consolidadoDiaTempoInterno.setTotalMapas(totalMapas);
		consolidadoDiaTempoInterno.setMapasOk(mapasOk);
		consolidadoDiaTempoInterno.setMapasNok(mapasNok);
		consolidadoDiaTempoInterno.setResultado((double) mapasOk / (double) totalMapas);
		consolidadoDiaTempoInterno.setBateuMeta(MetaUtils.bateuMetaMapas(consolidadoDiaTempoInterno.getResultado(), meta));
		
	}
	
	@Override
	public IndicadorHolder getIndicadoresUnidadeByPeriodo(LocalDate dataInicial, LocalDate dataFinal, int codUnidade,
			Long cpf, String token) throws SQLException {
		// TODO Auto-generated method stub
		return null;
	}

}
