package br.com.zalf.prolog.webservice.dao;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
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
	private ConsolidadoDiaDev consolidadoDia;


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
			arrumLista(createDevCx(rSet));
			

		} finally {
			closeConnection(conn, stmt, rSet);
		}

		return  new IndicadorHolder();
	}

	private List<ItemExtratoDiaDev> createDevCx(ResultSet rset) throws SQLException{

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
			devCx.setCxCarreg(rset.getDouble("CXCARREG"));
			devCx.setCxEntreg(rset.getDouble("CXENTREG"));
			devCx.setCxDev(rset.getDouble("DEVCX"));
			devCx.setResultado(devCx.getCxDev() / devCx.getCxCarreg());
			devCx.setMeta(meta.getMetaDevCx());
			devCx.setBateuMeta(MetaUtils.bateuMeta(devCx.getResultado(), devCx.getMeta()));
			listDev.add(devCx);
		}	
		return listDev;
	}

	private void arrumLista (List<ItemExtratoDiaDev> listDev){
		ConsolidadoIndicador consolidadoIndicador = new ConsolidadoIndicador(); // contem uma lista com todos os consolidados
		List<ConsolidadoDiaDev> listConsolidadoDiaDev = new ArrayList<>(); // Lista do consolidado dos dias, cada elemento contem uma lista com dias sem repetição
		
		ConsolidadoDiaDev consolidadoDia = new ConsolidadoDiaDev(); // contem uma lista com elementos do mesmo dia
		List<ItemExtratoDiaDev> listaAtual = new ArrayList<>(); // contem itens do mesmo dia
		
		System.out.println(listDev);
		
		for(int itemAtual =1; itemAtual < listDev.size(); itemAtual++){
			
			System.out.println("Entrou no for com os valores: " + "itemAtual=" + itemAtual + " listDev.size= " + listDev.size());
			
			if(listDev.get(itemAtual).getData().getTime() == listDev.get(itemAtual - 1).getData().getTime()){
				
				System.out.println("Entrou no IF1 comparando os seguinte valoes: "+ " itemAtual " + listDev.get(itemAtual).getData() + "itemAtual -1 " +listDev.get(itemAtual -1 ).getData());
								
				listaAtual.add(listDev.get(itemAtual-1));
				
				if(listDev.size() -1 == itemAtual){
					listaAtual.add(listDev.get(itemAtual));
					consolidadoDia.setDevolucaoRelList(listaAtual);
					// setar acumulados
					listConsolidadoDiaDev.add(consolidadoDia);
					break;
				}
				
			}else{
				listaAtual.add(listDev.get(itemAtual-1));
				consolidadoDia.setDevolucaoRelList(listaAtual);
				// setar acumulados
				listConsolidadoDiaDev.add(consolidadoDia);
				consolidadoDia = new ConsolidadoDiaDev();
				listaAtual = new ArrayList<>();
				
				if(listDev.size() -1 == itemAtual ){
					listaAtual.add(listDev.get(itemAtual));
					consolidadoDia.setDevolucaoRelList(listaAtual);
					// setar acumulados
					listConsolidadoDiaDev.add(consolidadoDia);
					break;
				}
			}
						
		}
		consolidadoIndicador.setListDevCx(listConsolidadoDiaDev);
		System.out.print(consolidadoIndicador);
	}




	@Override
	public IndicadorHolder getIndicadoresUnidadeByPeriodo(LocalDate dataInicial, LocalDate dataFinal, int codUnidade,
			Long cpf, String token) throws SQLException {
		// TODO Auto-generated method stub
		return null;
	}

}
