package br.com.zalf.prolog.webservice.entrega.produtividade;

import br.com.zalf.prolog.commons.colaborador.Colaborador;
import br.com.zalf.prolog.commons.util.DateUtils;
import br.com.zalf.prolog.commons.util.MetaUtils;
import br.com.zalf.prolog.commons.util.TimeUtils;
import br.com.zalf.prolog.entrega.indicador.older.*;
import br.com.zalf.prolog.entrega.produtividade.ColaboradorProdutividade;
import br.com.zalf.prolog.entrega.produtividade.HolderColaboradorProdutividade;
import br.com.zalf.prolog.entrega.produtividade.ItemProdutividade;
import br.com.zalf.prolog.webservice.DatabaseConnection;
import br.com.zalf.prolog.webservice.metas.MetasDao;
import br.com.zalf.prolog.webservice.metas.MetasDaoImpl;
import br.com.zalf.prolog.webservice.util.L;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class ProdutividadeDaoImpl extends DatabaseConnection implements ProdutividadeDao {

    private static String TAG = ProdutividadeDaoImpl.class.getSimpleName();

	/**
	 * Data do mapa é comparada com a tabela histórico_cargos.
	 * 
	 * Foi criada uma coluna FUNCAO_ANTIGA e outra FUNCAO_ATUAL, é feito o join do mapa 
	 * com a histórico_cargos contendo as duas colunas, caso a coluna funcao_antiga 
	 * esteja vazia, significa que o colaborador não teve mudança de cargo, sendo calculada 
	 * a remuneração de acordo com a função descrita na coluna funcao_atual.
	 * 
	 * Se houver dados na coluna funcao_antiga, é usado a função descrita nela para 
	 * calculas a remuneração deste mapa.
	 */
	private static final String BUSCA_PRODUTIVIDADE="SELECT M.DATA, M.FATOR, M.CXCARREG,M.CXENTREG, "
			+ "M.QTNFCARREGADAS,M.QTNFENTREGUES,M.QTHLCARREGADOS, M.QTHLENTREGUES, M.HRSAI, "
			+ "M.HRENTR, M.TEMPOINTERNO, M.HRMATINAL, M.MATRICMOTORISTA, M.MATRICAJUD1, M.MATRICAJUD2, C.MATRICULA_AMBEV, C.COD_FUNCAO AS FUNCAO_ATUAL, "
			+ "M.VlBateuJornMot, M.VlNaoBateuJornMot, "
			+ "M.VlRecargaMot, M.VlBateuJornAju, M.VlNaoBateuJornAju, M.VlRecargaAju, "
			+ "TRACKING.TOTAL as TOTAL_TRACKING, TRACKING.apontamento_ok "
			+ "FROM MAPA_COLABORADOR MC JOIN COLABORADOR C ON C.COD_UNIDADE = MC.COD_UNIDADE AND "
			+ "MC.COD_AMBEV= C.MATRICULA_AMBEV JOIN MAPA M ON M.MAPA = MC.MAPA JOIN TOKEN_AUTENTICACAO "
			+ "TA ON ? = TA.CPF_COLABORADOR AND ? = TA.TOKEN LEFT JOIN (SELECT t.mapa AS TRACKING_MAPA, total.total_entregas AS TOTAL, "
			+ "ok.apontamento_ok AS APONTAMENTO_OK from tracking t join mapa_colaborador mc on "
			+ "mc.mapa = t.mapa join (SELECT t.mapa as mapa_ok, count(t.disp_apont_cadastrado) as "
			+ "apontamento_ok from tracking t where t.disp_apont_cadastrado <= '0.3' group by t.mapa) as "
			+ "ok on mapa_ok = t.mapa join (SELECT t.mapa as total, count(t.COD_CLIENTE) as total_entregas "
			+ "from tracking t	group by t.mapa) as total on total = t.mapa	join colaborador c on "
			+ "c.matricula_ambev = mc.cod_ambev GROUP BY t.mapa, ok.mapa_ok, total.total_entregas, "
			+ "ok.apontamento_ok) AS TRACKING ON TRACKING_MAPA = M.MAPA	WHERE C.CPF = ? AND DATA BETWEEN ? "
			+ "AND ? ORDER BY M.DATA;";
	private Meta meta;
	
	@Override
	public List<ItemProdutividade> getProdutividadeByPeriodo(LocalDate dataInicial, LocalDate dataFinal, 
			Long cpf, String token) throws SQLException {

		Connection conn = null;
		PreparedStatement stmt = null;
		ResultSet rSet = null;
		List<ItemProdutividade> listItemProdutividade = new ArrayList<>();

		try {
			conn = getConnection();
			stmt = conn.prepareStatement(BUSCA_PRODUTIVIDADE,
					ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);
			
			// Token autenticação
			stmt.setLong(1, cpf);
			stmt.setString(2, token);
			
			stmt.setLong(3, cpf);
			stmt.setDate(4, DateUtils.toSqlDate(dataInicial));
			stmt.setDate(5, DateUtils.toSqlDate(dataFinal));
            L.d(TAG, stmt.toString());
            rSet = stmt.executeQuery();
			
			MetasDao metasDao = new MetasDaoImpl();
			meta = metasDao.getMetasByCpf(cpf);

			while(rSet.next()){
				Date data = rSet.getDate("DATA");
				double valor = createValor(rSet);
				ItemDevolucaoNf devolucaoNf = createDevNf(rSet);
				ItemDevolucaoCx devolucaoCx = createDevCx(rSet);
				ItemDevolucaoHl devolucaoHl = createDevHl(rSet);
				ItemJornadaLiquida jornadaLiquida = createJornadaLiquida(rSet);
				ItemTempoInterno tempoInterno = createTempoInterno(rSet);
				ItemTempoLargada tempoLargada = createTempoLargada(rSet);
				ItemTempoRota tempoRota = createTempoRota(rSet);
				ItemTracking tracking = createTracking(rSet);

				ItemProdutividade itemProdutividade = new ItemProdutividade(data, valor,
						jornadaLiquida, devolucaoCx, devolucaoNf, devolucaoHl, tempoLargada, 
						tempoRota, tempoInterno, tracking);
				
				listItemProdutividade.add(itemProdutividade);
			}
			return listItemProdutividade;
		}
		finally {
			closeConnection(conn, stmt, rSet);
		}
	}

	@Override
	public List<HolderColaboradorProdutividade> getConsolidadoProdutividade(Long codUnidade, String equipe, String codFuncao,
																			long dataInicial, long dataFinal) throws SQLException {
		Connection conn = null;
		PreparedStatement stmt = null;
		ResultSet rSet = null;
		List<HolderColaboradorProdutividade> holders = new ArrayList<>();
		HolderColaboradorProdutividade holder = null;
		List<ColaboradorProdutividade> colaboradores = new ArrayList<>();
		Colaborador c = null;
		try{
			conn = getConnection();
			stmt = conn.prepareStatement("SELECT C.CPF,c.matricula_ambev, C.NOME, c.data_nascimento,F.nome funcao,count(m.mapa) as mapas,sum(m.cxentreg) as caixas,\n" +
					"  --case para verificar automaticamente se é motorista ou ajudante e calcular o valor, em caso de entrega != AS\n" +
					"  sum(CASE when (c.matricula_ambev) = m.matricmotorista and m.entrega <> 'AS' then   (M.vlbateujornmot + M.vlnaobateujornmot + M.vlrecargamot)\n" +
					"  when (c.matricula_ambev) = m.matricajud1 and m.entrega <> 'AS' then   (M.vlbateujornaju + M.vlnaobateujornaju + M.vlrecargaaju)/m.fator\n" +
					"  when (c.matricula_ambev) = m.matricajud2 and m.entrega <> 'AS' then   (M.vlbateujornaju + M.vlnaobateujornaju + M.vlrecargaaju)/m.fator\n" +
					"  else 0\n" +
					"  end) +\n" +
					"  -- case para calcular o valor quando é AS\n" +
					"  sum(CASE when (c.matricula_ambev) = m.matricmotorista and m.entrega = 'AS' then\n" +
					"    --case para calcular o valor com base no número de entregas\n" +
					"    (case when m.entregas = 1 then uv.rm_motorista_valor_as_1_entrega\n" +
					"      when m.entregas = 2 then uv.rm_motorista_valor_as_2_entregas\n" +
					"      when m.entregas > 2 then uv.rm_motorista_valor_as_maior_2_entregas\n" +
					"      else 0\n" +
					"      end)\n" +
					"  when (c.matricula_ambev) = m.matricajud1 and m.entrega = 'AS' then\n" +
					"    --case para calcular o valor com base no número de entregas\n" +
					"    (case when m.entregas = 1 then uv.rm_ajudante_valor_as_1_entrega\n" +
					"      when m.entregas = 2 then uv.rm_ajudante_valor_as_2_entregas\n" +
					"      when m.entregas > 2 then uv.rm_ajudante_valor_as_maior_2_entregas\n" +
					"      else 0\n" +
					"      end)\n" +
					"  when (c.matricula_ambev) = m.matricajud2 and m.entrega = 'AS' then\n" +
					"    --case para calcular o valor com base no número de entregas\n" +
					"    (case when m.entregas = 1 then uv.rm_ajudante_valor_as_1_entrega\n" +
					"    when m.entregas = 2 then uv.rm_ajudante_valor_as_2_entregas\n" +
					"    when m.entregas > 2 then uv.rm_ajudante_valor_as_maior_2_entregas\n" +
					"    else 0\n" +
					"    end)\n" +
					"  else 0\n" +
					"  end) as valor\n" +
					"  FROM mapa_colaborador MC\n" +
					"  JOIN colaborador C ON C.matricula_ambev = MC.cod_ambev\n" +
					"  AND C.cod_unidade = MC.cod_unidade\n" +
					"  JOIN MAPA M ON M.cod_unidade = MC.cod_unidade\n" +
					"  AND M.MAPA = MC.mapa\n" +
					"  JOIN FUNCAO F ON F.codigo = C.cod_funcao\n" +
					"  JOIN equipe e on e.cod_unidade = c.cod_unidade and c.cod_equipe = e.codigo\n" +
					"  left JOIN unidade_valores_rm uv on uv.cod_unidade = m.cod_unidade\n" +
					"  WHERE M.cod_unidade = ? and m.fator >0 and m.data BETWEEN ? and ?\n" +
					"  and f.codigo::text like ? and e.nome::text like ?\n" +
					"  GROUP BY 1,2,3,4,5\n" +
					"  order by f.nome, valor desc, c.nome;");
			stmt.setLong(1, codUnidade);
			stmt.setDate(2, DateUtils.toSqlDate(new Date(dataInicial)));
			stmt.setDate(3, DateUtils.toSqlDate(new Date(dataFinal)));
			stmt.setString(4, codFuncao);
			stmt.setString(5, equipe);
			rSet = stmt.executeQuery();
			while (rSet.next()){
				if (holder == null){
					holder = new HolderColaboradorProdutividade();
					holder.setFuncao(rSet.getString("funcao"));
					colaboradores = new ArrayList<>();
					colaboradores.add(createColaboradorProdutividade(rSet));
				}else{
					if (holder.getFuncao().equals(rSet.getString("funcao"))){
						colaboradores.add(createColaboradorProdutividade(rSet));
					}else{
						holder.setProdutividades(colaboradores);
						holders.add(holder);
						holder = new HolderColaboradorProdutividade();
						holder.setFuncao(rSet.getString("funcao"));
						colaboradores = new ArrayList<>();
						colaboradores.add(createColaboradorProdutividade(rSet));
					}
				}
			}
			if (holder!= null){
				holder.setProdutividades(colaboradores);
				holders.add(holder);
			}
		}finally {
			closeConnection(conn,stmt,rSet);
		}
		return holders;
	}

	private ColaboradorProdutividade createColaboradorProdutividade(ResultSet rSet)throws SQLException{
		ColaboradorProdutividade c = new ColaboradorProdutividade();
		Colaborador co = new Colaborador();
		co.setCpf(rSet.getLong("cpf"));
		co.setNome(rSet.getString("nome"));
		c.setColaborador(co);
		c.setQtdCaixas(rSet.getInt("caixas"));
		c.setQtdMapas(rSet.getInt("mapas"));
		c.setValor(rSet.getDouble("valor"));
		return c;
	}

	private ItemDevolucaoHl createDevHl(ResultSet rSet) throws SQLException {
		ItemDevolucaoHl itemDevolucaoHl = new ItemDevolucaoHl();
		itemDevolucaoHl.setData(rSet.getDate("DATA"));
		itemDevolucaoHl.setCarregadas(rSet.getDouble("QTHLCARREGADOS"));
		itemDevolucaoHl.setEntregues(rSet.getDouble("QTHLENTREGUES"));
		itemDevolucaoHl.setDevolvidas(itemDevolucaoHl.getCarregadas() - itemDevolucaoHl.getEntregues());
		itemDevolucaoHl.setResultado(itemDevolucaoHl.getDevolvidas() / itemDevolucaoHl.getCarregadas());
		itemDevolucaoHl.setMeta(meta.getMetaDevHl());
		itemDevolucaoHl.setBateuMeta(MetaUtils.bateuMeta(itemDevolucaoHl.getResultado(), meta.getMetaDevHl()));
		return itemDevolucaoHl;
	}

	private double createValor(ResultSet rSet) throws NumberFormatException, SQLException {
		
		double valor = 0;
		int funcao = 0;
		
		if(rSet.getInt("MATRICMOTORISTA") == rSet.getInt("MATRICULA_AMBEV")){
			funcao = 1;
		}else{
			funcao = 2;
		}
		
		switch(funcao){
		//caso a função seja cod = 1 = motorista
		case(1):
			valor = rSet.getDouble("VlBateuJornMot");
			valor = valor + rSet.getDouble("VlNaoBateuJornMot");
			valor = valor + rSet.getDouble("VlRecargaMot");
			break;
		// função cod = 2 = ajudante
		case(2):
			valor = rSet.getDouble("VlBateuJornAju");
			valor = valor + rSet.getDouble("VlNaoBateuJornAju");
			valor = valor + rSet.getDouble("VlRecargaAju");
			valor = valor/rSet.getInt("fator");
			break;
		}
		return valor;
	}

	private ItemTracking createTracking(ResultSet rSet) throws SQLException {
		ItemTracking itemTracking = new ItemTracking();
		itemTracking.setData(rSet.getDate("DATA"));
		itemTracking.setTotal(rSet.getDouble("TOTAL_TRACKING"));
		itemTracking.setOk(rSet.getDouble("APONTAMENTO_OK"));
		itemTracking.setNok(itemTracking.getTotal() - itemTracking.getOk());
		if(itemTracking.getTotal() > 0){
		itemTracking.setResultado(itemTracking.getOk() / itemTracking.getTotal());
		}else{
			itemTracking.setResultado(0);
		}
		itemTracking.setMeta(meta.getMetaTracking());
		itemTracking.setBateuMeta(!(MetaUtils.bateuMeta(itemTracking.getResultado(), meta.getMetaTracking())));
		return itemTracking;
	}

	private ItemTempoRota createTempoRota(ResultSet rSet) throws SQLException {
		ItemTempoRota itemTempoRota = new ItemTempoRota();
		itemTempoRota.setData(rSet.getDate("DATA"));
		itemTempoRota.setHrEntrada(rSet.getTime("HRENTR"));
		itemTempoRota.setHrSaida(TimeUtils.toSqlTime(rSet.getTimestamp("HRSAI")));
		// saber o tempo que o caminhão ficou na rua, por isso hora de entrada(volta da rota) = hora de saída( saída para rota)
		itemTempoRota.setResultado(TimeUtils.differenceBetween(itemTempoRota.getHrEntrada(), itemTempoRota.getHrSaida()));
		itemTempoRota.setMeta(meta.getMetaTempoRotaHoras());
		itemTempoRota.setBateuMeta(MetaUtils.bateuMeta(itemTempoRota.getResultado(), meta.getMetaTempoRotaHoras()));
		return itemTempoRota;
	}

	private ItemTempoLargada createTempoLargada(ResultSet rSet) throws SQLException {
		ItemTempoLargada itemTempoLargada = new ItemTempoLargada();
		itemTempoLargada.setData(rSet.getDate("DATA"));
		itemTempoLargada.setHrMatinal(rSet.getTime("HRMATINAL"));
		itemTempoLargada.setHrSaida(TimeUtils.toSqlTime(rSet.getTimestamp("HRSAI")));
		itemTempoLargada.setResultado(MetaUtils.calculaTempoLargada(itemTempoLargada.getHrSaida(), itemTempoLargada.getHrMatinal()));
		itemTempoLargada.setMeta(meta.getMetaTempoLargadaHoras());
		itemTempoLargada.setBateuMeta(MetaUtils.bateuMeta(itemTempoLargada.getResultado(), meta.getMetaTempoLargadaHoras()));
		return itemTempoLargada;
	}

	private ItemTempoInterno createTempoInterno(ResultSet rSet) throws SQLException {
		ItemTempoInterno itemTempoInterno = new ItemTempoInterno();
		itemTempoInterno.setData(rSet.getDate("DATA"));
		itemTempoInterno.setHrEntrada(rSet.getTime("HRENTR"));
		Time tempoInterno = rSet.getTime("TEMPOINTERNO");
		// entrada + tempo interno = horario do fechamento
		itemTempoInterno.setHrFechamento(TimeUtils.somaHoras(
				itemTempoInterno.getHrEntrada(), 
				tempoInterno));
		itemTempoInterno.setResultado(tempoInterno);
		itemTempoInterno.setMeta(meta.getMetaTempoInternoHoras());
		itemTempoInterno.setBateuMeta(MetaUtils.bateuMeta(itemTempoInterno.getResultado(), 
				meta.getMetaTempoInternoHoras()));
		return itemTempoInterno;
	}

	private ItemJornadaLiquida createJornadaLiquida(ResultSet rSet) throws SQLException {
		Time tempoInterno = rSet.getTime("TEMPOINTERNO");
		Time rota = TimeUtils.differenceBetween(
				TimeUtils.toSqlTime(rSet.getTimestamp("HRENTR")),
				TimeUtils.toSqlTime(rSet.getTimestamp("HRSAI")));
		Time matinal = MetaUtils.calculaTempoLargada(
				TimeUtils.toSqlTime(rSet.getTimestamp("HRSAI")),
				rSet.getTime("HRMATINAL"));
		ItemJornadaLiquida itemJornadaLiquida = new ItemJornadaLiquida();
		itemJornadaLiquida.setData(rSet.getDate("DATA"));
		itemJornadaLiquida.setTempoInterno(tempoInterno);
		itemJornadaLiquida.setTempoRota(rota);
		itemJornadaLiquida.setTempoLargada(matinal);
		itemJornadaLiquida.setResultado(TimeUtils.somaHoras(TimeUtils.somaHoras(matinal, rota),tempoInterno));
		itemJornadaLiquida.setMeta(meta.getMetaJornadaLiquidaHoras());
		itemJornadaLiquida.setBateuMeta(MetaUtils.bateuMeta(itemJornadaLiquida.getResultado(), meta.getMetaJornadaLiquidaHoras()));
		return itemJornadaLiquida;
	}

	private ItemDevolucaoCx createDevCx(ResultSet rSet) throws SQLException {
		ItemDevolucaoCx itemDevolucaoCx = new ItemDevolucaoCx();
		itemDevolucaoCx.setData(rSet.getDate("DATA"));
		itemDevolucaoCx.setCarregadas(rSet.getDouble("CXCARREG"));
		itemDevolucaoCx.setEntregues(rSet.getDouble("CXENTREG"));
		itemDevolucaoCx.setDevolvidas(itemDevolucaoCx.getCarregadas() - itemDevolucaoCx.getEntregues());
		itemDevolucaoCx.setResultado(itemDevolucaoCx.getDevolvidas() / itemDevolucaoCx.getCarregadas());
		itemDevolucaoCx.setMeta(meta.getMetaDevCx());
		itemDevolucaoCx.setBateuMeta(MetaUtils.bateuMeta(itemDevolucaoCx.getResultado(), meta.getMetaDevCx()));
		return itemDevolucaoCx;
	}

	private ItemDevolucaoNf createDevNf(ResultSet rSet) throws SQLException {
		ItemDevolucaoNf itemDevolucaoNf = new ItemDevolucaoNf();
		itemDevolucaoNf.setData(rSet.getDate("DATA"));
		itemDevolucaoNf.setCarregadas(rSet.getDouble("QTNFCARREGADAS"));
		itemDevolucaoNf.setEntregues(rSet.getDouble("QTNFENTREGUES"));
		itemDevolucaoNf.setDevolvidas(itemDevolucaoNf.getCarregadas() - itemDevolucaoNf.getEntregues());
		itemDevolucaoNf.setResultado(itemDevolucaoNf.getDevolvidas() / itemDevolucaoNf.getCarregadas());
		itemDevolucaoNf.setMeta(meta.getMetaDevNf());
		itemDevolucaoNf.setBateuMeta(MetaUtils.bateuMeta(itemDevolucaoNf.getResultado(), meta.getMetaDevNf()));
		return itemDevolucaoNf;
	}
}
