package br.com.zalf.prolog.webservice.gente.ranking;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import br.com.zalf.prolog.models.indicador.DevolucaoCxHolder;
import br.com.zalf.prolog.models.indicador.DevolucaoHlHolder;
import br.com.zalf.prolog.models.indicador.DevolucaoNfHolder;
import br.com.zalf.prolog.models.indicador.ItemDevolucaoCx;
import br.com.zalf.prolog.models.indicador.ItemDevolucaoHl;
import br.com.zalf.prolog.models.indicador.ItemDevolucaoNf;
import br.com.zalf.prolog.models.indicador.ItemJornadaLiquida;
import br.com.zalf.prolog.models.indicador.ItemTempoInterno;
import br.com.zalf.prolog.models.indicador.ItemTempoLargada;
import br.com.zalf.prolog.models.indicador.ItemTempoRota;
import br.com.zalf.prolog.models.indicador.ItemTracking;
import br.com.zalf.prolog.models.indicador.JornadaLiquidaHolder;
import br.com.zalf.prolog.models.indicador.Meta;
import br.com.zalf.prolog.models.indicador.TempoInternoHolder;
import br.com.zalf.prolog.models.indicador.TempoLargadaHolder;
import br.com.zalf.prolog.models.indicador.TempoRotaHolder;
import br.com.zalf.prolog.models.indicador.TrackingHolder;
import br.com.zalf.prolog.models.ranking.ItemPosicao;
import br.com.zalf.prolog.models.util.DateUtils;
import br.com.zalf.prolog.models.util.MetaUtils;
import br.com.zalf.prolog.webservice.DatabaseConnection;
import br.com.zalf.prolog.webservice.entrega.relatorio.RelatorioDaoImpl;
import br.com.zalf.prolog.webservice.metas.MetasDaoImpl;

public class RankingDaoImpl extends DatabaseConnection {

/**
 * Busca os dados da tabela mapa e tracking para montar todos os indicadores, 
 * respeitando o período selecionado e o cod da unidade.
 */
	private static final String BUSCA_INDICADORES_RANKING = "SELECT C.CPF, C.NOME, F.NOME AS FUNCAO, E.NOME AS EQUIPE, M.DATA, "
			+ " M.CXCARREG, M.CXENTREG,M.QTHLCARREGADOS, M.QTHLENTREGUES, M.QTNFCARREGADAS,	"
			+ "M.QTNFENTREGUES, M.HRSAI, M.HRENTR,M.TEMPOINTERNO, M.HRMATINAL,	"
			+ "TRACKING.TOTAL as TOTAL_TRACKING, TRACKING.APONTAMENTO_OK "
			+ "FROM	MAPA_COLABORADOR MC JOIN COLABORADOR C ON C.COD_UNIDADE = MC.COD_UNIDADE "
			+ "AND MC.COD_AMBEV = C.MATRICULA_AMBEV "
			+ "JOIN EQUIPE E ON E.CODIGO = C.COD_EQUIPE	"
			+ "JOIN MAPA M ON M.MAPA = MC.MAPA "
			+ "JOIN TOKEN_AUTENTICACAO TA ON ? = TA.CPF_COLABORADOR "
			+ "AND ? = TA.TOKEN	"
			+ "JOIN FUNCAO F ON F.CODIGO = C.COD_FUNCAO "
			+ "LEFT JOIN( SELECT t.mapa AS TRACKING_MAPA, total.total AS TOTAL, "
			+ "ok.APONTAMENTOS_OK AS APONTAMENTO_OK from tracking t "
			+ "join mapa_colaborador mc on mc.mapa = t.mapa "
			+ "join (SELECT t.mapa as mapa_ok, count(t.disp_apont_cadastrado) as apontamentos_ok "
			+ "from tracking t where t.disp_apont_cadastrado <= '0.3' group by t.mapa) as ok on mapa_ok = t.mapa "
			+ "join (SELECT t.mapa as total_entregas, count(t.cod_cliente) as total "
			+ "from tracking t group by t.mapa) as total on total_entregas = t.mapa "
			+ "join colaborador c on c.matricula_ambev = mc.cod_ambev "
			+ "GROUP BY t.mapa, OK.APONTAMENTOS_OK, total.total) AS	TRACKING ON TRACKING_MAPA = M.MAPA "
			+ "WHERE E.NOME like ? AND C.COD_UNIDADE = ?	AND "
			+ "DATA BETWEEN ? AND ? ORDER BY C.CPF, m.data";

	//Valor máximo de atingimento de uma meta de tempo (jornada, tml..)
	private static final double MAX_META = 1;
	//usado no calculo das faixas de atingimento para saber qual a medalha será creditada
	private static final double FAIXAS = 3.5;
	//pontuação de cada medalha
	private static final int PONTOS_OURO = 3;
	private static final int PONTOS_PRATA = 2;
	private static final int PONTOS_BRONZE = 1;

	private Meta meta;
	private RelatorioDaoImpl create;



	public List<ItemPosicao> getRanking (LocalDate dataInicial, LocalDate dataFinal, String equipe,
			Long codUnidade, Long cpf, String token) throws SQLException{

		Connection conn = null;
		PreparedStatement stmt = null;
		ResultSet rSet = null;
		MetasDaoImpl metasDao = new MetasDaoImpl();
		meta = metasDao.getMetasByUnidade(codUnidade);
		create = new RelatorioDaoImpl(meta);
		List<ItemPosicao> listPosicao = new ArrayList<>();

		try{
			conn = getConnection();
			stmt = conn.prepareStatement(BUSCA_INDICADORES_RANKING, ResultSet.TYPE_SCROLL_SENSITIVE,
					ResultSet.CONCUR_UPDATABLE);
			stmt.setLong(1, cpf);
			stmt.setString(2, token);
			stmt.setString(3, equipe);
			stmt.setLong(4, codUnidade);
			stmt.setDate(5, DateUtils.toSqlDate(dataInicial));
			stmt.setDate(6, DateUtils.toSqlDate(dataFinal));
			rSet = stmt.executeQuery();
			listPosicao = createRanking(rSet);
			setMedalhas(listPosicao);
			calculaPontuacao(listPosicao);
			setPosicao(listPosicao);
		}
		finally {
			closeConnection(conn, stmt, rSet);
		}
		//System.out.print(listPosicao);
		return listPosicao;
	}
/**
 * Cria uma lista de objetos ItemPosicao, contendo os indicadores e resultados 
 * de cada colaborador
 * @param rSet um ResultSet contendo o resultado da busca
 * @return uma lista de ItemPosicao, contendo todos os colaboradores e seus resultados
 * @throws SQLException caso ocorra erro ao percorrer o ResultSet
 */
	public List<ItemPosicao> createRanking(ResultSet rSet) throws SQLException{
		List<ItemPosicao> listPosicao = new ArrayList<>();
		ItemPosicao itemPosicao = new ItemPosicao();
		if(rSet.first()){
			itemPosicao = createItemPosicao(rSet);
			listPosicao.add(itemPosicao);
		}
		while(rSet.next()){
			if(rSet.getLong("CPF") == listPosicao.get(listPosicao.size()-1).getCpf()){
				incrementaTotais(listPosicao.get(listPosicao.size()-1), rSet);
			}else{
				itemPosicao = createItemPosicao(rSet);
				listPosicao.add(itemPosicao);
			}
		}
		//System.out.print(listPosicao);
		return listPosicao;
	}

	/**
	 * Cria o item do rankin, contém os dados do colaborador e seus indicadores
	 * @param rSet um ResultSet, contém o resultado da busca
	 * @return um ItemPosicao
	 * @throws SQLException caso ocorra erro ao percorrer o ResultSet
	 */
	private ItemPosicao createItemPosicao(ResultSet rSet) throws SQLException{
		ItemPosicao itemPosicao = new ItemPosicao();
		itemPosicao.setCpf(rSet.getLong("CPF"));
		itemPosicao.setNome(rSet.getString("NOME"));
		itemPosicao.setFuncao(rSet.getString("FUNCAO"));
		itemPosicao.setEquipe(rSet.getString("EQUIPE"));
		itemPosicao.setDevCx(createDevCx(rSet));
		itemPosicao.setDevHl(createDevHl(rSet));
		itemPosicao.setDevNf(createDevNf(rSet));
		itemPosicao.setTempoLargada(createTempoLargada(rSet));
		itemPosicao.setTempoRota(createTempoRota(rSet));
		itemPosicao.setTempoInterno(createTempoInterno(rSet));
		itemPosicao.setJornada(createJornada(rSet));
		itemPosicao.setTracking(createTracking(rSet));
		return itemPosicao;	
	}

	private DevolucaoCxHolder createDevCx(ResultSet rSet) throws SQLException{
		DevolucaoCxHolder devCx = new DevolucaoCxHolder();
		devCx.setCarregadasTotal(rSet.getDouble("CXCARREG"));
		devCx.setEntreguesTotal(rSet.getDouble("CXENTREG"));
		devCx.setDevolvidasTotal(devCx.getCarregadasTotal() - devCx.getEntreguesTotal());
		devCx.setMeta(meta.getMetaDevCx());
		devCx.setResultadoTotal(devCx.getDevolvidasTotal() / devCx.getCarregadasTotal());
		devCx.setBateuMeta(MetaUtils.bateuMeta(devCx.getResultadoTotal(), meta.getMetaDevCx()));
		return devCx;
	}

	private DevolucaoHlHolder createDevHl(ResultSet rSet) throws SQLException{
		DevolucaoHlHolder devHl = new DevolucaoHlHolder();
		devHl.setCarregadasTotal(rSet.getDouble("QTHLCARREGADOS"));
		devHl.setEntreguesTotal(rSet.getDouble("QTHLENTREGUES"));
		devHl.setDevolvidasTotal(devHl.getCarregadasTotal() - devHl.getEntreguesTotal());
		devHl.setMeta(meta.getMetaDevHl());
		devHl.setResultadoTotal(devHl.getDevolvidasTotal() / devHl.getCarregadasTotal());
		devHl.setBateuMeta(MetaUtils.bateuMeta(devHl.getResultadoTotal(), meta.getMetaDevHl()));
		return devHl;
	}

	private DevolucaoNfHolder createDevNf(ResultSet rSet) throws SQLException{
		DevolucaoNfHolder devNf = new DevolucaoNfHolder();
		devNf.setCarregadasTotal(rSet.getInt("QTNFCARREGADAS"));
		devNf.setEntreguesTotal(rSet.getInt("QTNFENTREGUES"));
		devNf.setDevolvidasTotal(devNf.getCarregadasTotal() - devNf.getEntreguesTotal());
		devNf.setMeta(meta.getMetaDevNf());
		devNf.setResultadoTotal(devNf.getDevolvidasTotal() / devNf.getCarregadasTotal());
		devNf.setBateuMeta(MetaUtils.bateuMeta(devNf.getResultadoTotal(), meta.getMetaDevNf()));
		return devNf;
	}

	private TempoLargadaHolder createTempoLargada(ResultSet rSet) throws SQLException{
		ItemTempoLargada itemTempoLargada = create.createTempoLargada(rSet);
		TempoLargadaHolder tempoLargada = new TempoLargadaHolder();
		tempoLargada.setTotalMapas(1);
		tempoLargada.setMeta(meta.getMetaTempoLargadaMapas());
		if(itemTempoLargada.isBateuMeta()){
			tempoLargada.setMapasOk(1);
		}else{
			tempoLargada.setMapasNok(1);
		}
		return tempoLargada;
	}

	private TempoRotaHolder createTempoRota(ResultSet rSet) throws SQLException{
		ItemTempoRota itemTempoRota = create.createTempoRota(rSet);
		TempoRotaHolder tempoRota = new TempoRotaHolder();
		tempoRota.setMeta(meta.getMetaTempoRotaMapas());
		tempoRota.setTotalMapas(1);
		if(itemTempoRota.isBateuMeta()){
			tempoRota.setMapasOk(1);
		}else{
			tempoRota.setMapasNok(1);
		}
		return tempoRota;
	}

	private TempoInternoHolder createTempoInterno(ResultSet rSet) throws SQLException{
		ItemTempoInterno itemTempoInterno = create.createTempoInterno(rSet);
		TempoInternoHolder interno = new TempoInternoHolder();
		interno.setMeta(meta.getMetaTempoInternoMapas());
		interno.setTotalMapas(1);
		if(itemTempoInterno.isBateuMeta()){
			interno.setMapasOk(1);
		}else{
			interno.setMapasNok(1);
		}
		return interno;
	}

	private JornadaLiquidaHolder createJornada(ResultSet rSet) throws SQLException{
		ItemJornadaLiquida itemJornadaLiquida = create.createJornadaLiquida(rSet);
		JornadaLiquidaHolder jornada = new JornadaLiquidaHolder();
		jornada.setMeta(meta.getMetaJornadaLiquidaMapas());
		if(itemJornadaLiquida.isBateuMeta()){
			jornada.setMapasOk(1);
		}else{
			jornada.setMapasNok(1);
		}
		return jornada;
	}

	private TrackingHolder createTracking(ResultSet rSet) throws SQLException{
		TrackingHolder tracking = new TrackingHolder();
		tracking.setTotal(rSet.getInt("TOTAL_TRACKING"));
		tracking.setOk(rSet.getInt("APONTAMENTO_OK"));
		tracking.setNok(tracking.getTotal() - tracking.getOk());
		tracking.setMeta(meta.getMetaTracking());
		tracking.setResultado(tracking.getOk() / tracking.getResultado());
		tracking.setBateuMeta(MetaUtils.bateuMetaMapas(tracking.getResultado(), meta.getMetaTracking()));
		return tracking;
	}

	/**
	 * Incrementa os totais dos indicadores
	 * @param itemPosicao um ItemPosicao ao qual será somado os valores extraidos do ResultSet
	 * @param rSet um ResultSet, contém o resultado da busca
	 * @throws SQLException caso ocorra erro ao percorrer o ResultSet
	 */
	private void incrementaTotais(ItemPosicao itemPosicao, ResultSet rSet) throws SQLException{
		ItemDevolucaoCx itemDevolucaoCx = create.createDevCx(rSet);
		ItemDevolucaoNf itemDevolucaoNf = create.createDevNf(rSet);
		ItemDevolucaoHl itemDevolucaoHl = create.createDevHl(rSet);
		ItemTempoLargada itemTempoLargada = create.createTempoLargada(rSet);
		ItemTempoRota itemTempoRota = create.createTempoRota(rSet);
		ItemTempoInterno itemTempoInterno = create.createTempoInterno(rSet);
		ItemJornadaLiquida itemJornadaLiquida = create.createJornadaLiquida(rSet);
		ItemTracking itemTracking = create.createTracking(rSet);

		itemPosicao.setDevCx(incrementaDevCx(itemPosicao.getDevCx(), itemDevolucaoCx));
		itemPosicao.setDevHl(incrementaDevHl(itemPosicao.getDevHl(), itemDevolucaoHl));
		itemPosicao.setDevNf(incrementaDevNf(itemPosicao.getDevNf(), itemDevolucaoNf));
		itemPosicao.setTempoLargada(incrementaLargada(itemPosicao.getTempoLargada(), itemTempoLargada));
		itemPosicao.setTempoRota(incrementaRota(itemPosicao.getTempoRota(), itemTempoRota));
		itemPosicao.setTempoInterno(incrementaInterno(itemPosicao.getTempoInterno(), itemTempoInterno));
		itemPosicao.setJornada(incrementaJornada(itemPosicao.getJornada(), itemJornadaLiquida));
	}

	private DevolucaoCxHolder incrementaDevCx(DevolucaoCxHolder holder, ItemDevolucaoCx item){
		holder.setCarregadasTotal(holder.getCarregadasTotal() + item.getCarregadas());
		holder.setDevolvidasTotal(holder.getDevolvidasTotal() + item.getDevolvidas());
		holder.setEntreguesTotal(holder.getEntreguesTotal() + item.getEntregues());
		holder.setResultadoTotal(holder.getDevolvidasTotal() / holder.getCarregadasTotal());
		holder.setBateuMeta(MetaUtils.bateuMeta(holder.getResultadoTotal(), holder.getMeta()));	
		return holder;
	}

	private DevolucaoHlHolder incrementaDevHl(DevolucaoHlHolder holder, ItemDevolucaoHl item){
		holder.setCarregadasTotal(holder.getCarregadasTotal() + item.getCarregadas());
		holder.setDevolvidasTotal(holder.getDevolvidasTotal() + item.getDevolvidas());
		holder.setEntreguesTotal(holder.getEntreguesTotal() + item.getEntregues());
		holder.setResultadoTotal(holder.getDevolvidasTotal() / holder.getCarregadasTotal());
		holder.setBateuMeta(MetaUtils.bateuMeta(holder.getResultadoTotal(), holder.getMeta()));	
		return holder;
	}

	private DevolucaoNfHolder incrementaDevNf(DevolucaoNfHolder holder, ItemDevolucaoNf item){
		holder.setCarregadasTotal(holder.getCarregadasTotal() + item.getCarregadas());
		holder.setDevolvidasTotal(holder.getDevolvidasTotal() + item.getDevolvidas());
		holder.setEntreguesTotal(holder.getEntreguesTotal() + item.getEntregues());
		holder.setResultadoTotal(holder.getDevolvidasTotal() / holder.getCarregadasTotal());
		holder.setBateuMeta(MetaUtils.bateuMeta(holder.getResultadoTotal(), holder.getMeta()));	
		return holder;
	}

	private TempoLargadaHolder incrementaLargada(TempoLargadaHolder holder, ItemTempoLargada item){
		holder.setTotalMapas(holder.getTotalMapas() + 1);
		if(item.isBateuMeta()){
			holder.setMapasOk(holder.getMapasOk() + 1);
		}else{
			holder.setMapasNok(holder.getMapasNok() + 1);
		}
		holder.setResultado((double)holder.getMapasOk()/(double)holder.getTotalMapas());
		holder.setBateuMeta(MetaUtils.bateuMetaMapas(holder.getResultado(), holder.getMeta()));
		return holder;
	}

	private TempoRotaHolder incrementaRota(TempoRotaHolder holder, ItemTempoRota item){
		holder.setTotalMapas(holder.getTotalMapas() + 1);
		if(item.isBateuMeta()){
			holder.setMapasOk(holder.getMapasOk() + 1);
		}else{
			holder.setMapasNok(holder.getMapasNok() + 1);
		}
		holder.setResultado((double)holder.getMapasOk()/(double)holder.getTotalMapas());
		holder.setBateuMeta(MetaUtils.bateuMetaMapas(holder.getResultado(), holder.getMeta()));
		return holder;
	}

	private TempoInternoHolder incrementaInterno(TempoInternoHolder holder, ItemTempoInterno item){
		holder.setTotalMapas(holder.getTotalMapas() + 1);
		if(item.isBateuMeta()){
			holder.setMapasOk(holder.getMapasOk() + 1);
		}else{
			holder.setMapasNok(holder.getMapasNok() + 1);
		}
		holder.setResultado((double)holder.getMapasOk()/(double)holder.getTotalMapas());
		holder.setBateuMeta(MetaUtils.bateuMetaMapas(holder.getResultado(), holder.getMeta()));
		return holder;
	}

	private JornadaLiquidaHolder incrementaJornada(JornadaLiquidaHolder holder, ItemJornadaLiquida item){
		holder.setTotalMapas(holder.getTotalMapas() + 1);
		if(item.isBateuMeta()){
			holder.setMapasOk(holder.getMapasOk() + 1);
		}else{
			holder.setMapasNok(holder.getMapasNok() + 1);
		}
		holder.setResultado((double)holder.getMapasOk()/(double)holder.getTotalMapas());
		holder.setBateuMeta(MetaUtils.bateuMetaMapas(holder.getResultado(), holder.getMeta()));
		return holder;
	}


	/**
	 * Seta as medalhas de um ItemPosicao de acordo com o método específico de cálculo 
	 * para cada indicador
	 * @param list uma lista de ItemPosicao, ao qual serão setadas as medalhas de cada item
	 */
	private void setMedalhas(List<ItemPosicao> list){

		for(ItemPosicao itemPosicao : list){
			setMedalhaDev(itemPosicao.getDevCx().getResultadoTotal(), itemPosicao.getDevCx().getMeta(), itemPosicao);
			setMedalhaDev(itemPosicao.getDevHl().getResultadoTotal(), itemPosicao.getDevHl().getMeta(), itemPosicao);
			setMedalhaDev(itemPosicao.getDevNf().getResultadoTotal(), itemPosicao.getDevNf().getMeta(), itemPosicao);
			setMedalhaTempo(itemPosicao.getTempoLargada().getResultado(), itemPosicao.getTempoLargada().getMeta(), itemPosicao);
			setMedalhaTempo(itemPosicao.getTempoRota().getResultado(), itemPosicao.getTempoRota().getMeta(), itemPosicao);
			setMedalhaTempo(itemPosicao.getTempoInterno().getResultado(), itemPosicao.getTempoInterno().getMeta(), itemPosicao);
			setMedalhaTempo(itemPosicao.getJornada().getResultado(), itemPosicao.getJornada().getMeta(), itemPosicao);
			setMedalhaTempo(itemPosicao.getTracking().getResultado(), itemPosicao.getTracking().getMeta(), itemPosicao);
		}
	}
	/**
	 * Calcula qual medalha sera creditada com base na meta e no resultado, este serve apenas para indicadores
	 * em que o resultado tem que ser MENOR do que a meta.
	 * @param resultado - recebe o resultado do indicador em questão
	 * @param meta - recebe a meta do indicador em questão
	 * @param itemPosicao - recebe o item ao qual será creditada a medalha
	 */
	private void setMedalhaDev(double resultado, Double meta, ItemPosicao itemPosicao){
		// gap é a diferença entre a meta e o resultado, ex: meta = 3, resultado = 2, gap = 1
		// dividimos o gap pela constante FAIXAS para calcular as faixas de cada medalha.
		double gap = meta / FAIXAS;
		double bronze = meta;
		double prata = meta - (2*gap);
		double ouro = meta - (3*gap);
		if(resultado <= ouro){
			itemPosicao.setOuro(itemPosicao.getOuro() + 1);
		}
		else if(resultado <= prata){
			itemPosicao.setPrata(itemPosicao.getPrata() + 1);
		}
		else if(resultado <= bronze){
			itemPosicao.setBronze(itemPosicao.getBronze() + 1);
		}
	}

	/**
	 * Calcula qual medalha sera creditada com base na meta e no resultado, este serve apenas para indicadores
	 * em que o resultado tem que ser MAIOR do que a meta.
	 * @param resultado - recebe o resultado do indicador em questão
	 * @param meta - recebe a meta do indicador em questão
	 * @param itemPosicao - recebe o item ao qual será creditada a medalha
	 */
	private void setMedalhaTempo(double resultado, Double meta, ItemPosicao itemPosicao){
		double gap = (MAX_META - meta) / FAIXAS;
		double bronze = meta;
		double prata = meta + (2*gap);
		double ouro = meta + (3*gap);
		if(resultado >= ouro){
			itemPosicao.setOuro(itemPosicao.getOuro() + 1);
		}
		else if(resultado >= prata){
			itemPosicao.setPrata(itemPosicao.getPrata() + 1);
		}
		else if(resultado >= bronze){
			itemPosicao.setBronze(itemPosicao.getBronze() + 1);
		}
	}

	/**
	 * Converte as medalhas para uma pontuação inteira, com base no valor de cada medalha
	 * @param list lista de ItemPosicao
	 */
	private void calculaPontuacao(List<ItemPosicao> list){
		for(ItemPosicao item : list){
			item.setPontuacao(
					item.getOuro() * PONTOS_OURO +
					item.getPrata() * PONTOS_PRATA +
					item.getBronze() * PONTOS_BRONZE);
		}
	}

	/**
	 * 1-compara a lista de acordo com o CustomComparator
	 * 2-inverte a posição da lista
	 * 3-seta o atributo posição
	 * @param list uma Lista de ItemPosicao
	 * @see ItemPosicao
	 */
	private void setPosicao (List<ItemPosicao> list){

		Collections.sort(list, new CustomComparator());
		Collections.reverse(list);
		
		int posicao = 1;
		for(ItemPosicao item : list){
			item.setPosicao(posicao);
			posicao ++;
		}
	}

	private class CustomComparator implements Comparator<ItemPosicao>{

		/**
		 * Compara primeiro pela pontuação e depois pela devolução em NF, evitando empates
		 */
		@Override
		public int compare(ItemPosicao o1, ItemPosicao o2) {
			Integer valor1 = Double.compare(o1.getPontuacao(), o2.getPontuacao());
			if(valor1!=0){
				return valor1;
			}
			Integer valor2 = Double.compare(o2.getDevNf().getResultadoTotal(), o1.getDevNf().getResultadoTotal());
			return valor2;
			
		}
	}
}
