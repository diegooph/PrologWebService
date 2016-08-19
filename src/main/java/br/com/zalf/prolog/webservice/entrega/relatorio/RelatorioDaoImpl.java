package br.com.zalf.prolog.webservice.entrega.relatorio;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Time;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import br.com.zalf.prolog.models.indicador.ItemDevolucaoCx;
import br.com.zalf.prolog.models.indicador.ItemDevolucaoHl;
import br.com.zalf.prolog.models.indicador.ItemDevolucaoNf;
import br.com.zalf.prolog.models.indicador.ItemJornadaLiquida;
import br.com.zalf.prolog.models.indicador.ItemTempoInterno;
import br.com.zalf.prolog.models.indicador.ItemTempoLargada;
import br.com.zalf.prolog.models.indicador.ItemTempoRota;
import br.com.zalf.prolog.models.indicador.ItemTracking;
import br.com.zalf.prolog.models.indicador.Meta;
import br.com.zalf.prolog.models.relatorios.ConsolidadoHolder;
import br.com.zalf.prolog.models.relatorios.ConsolidadoMapasDia;
import br.com.zalf.prolog.models.relatorios.Empresa;
import br.com.zalf.prolog.models.relatorios.Mapa;
import br.com.zalf.prolog.models.relatorios.Regional;
import br.com.zalf.prolog.models.relatorios.Unidade;
import br.com.zalf.prolog.models.util.DateUtils;
import br.com.zalf.prolog.models.util.MetaUtils;
import br.com.zalf.prolog.models.util.TimeUtils;
import br.com.zalf.prolog.webservice.DatabaseConnection;
import br.com.zalf.prolog.webservice.metas.MetasDaoImpl;
import br.com.zalf.prolog.webservice.util.L;

public class RelatorioDaoImpl extends DatabaseConnection implements RelatorioDao {

	public static final String BUSCA_RELATORIO = "SELECT M.DATA, M.PLACA, M.MAPA, C.NOME AS NOMEMOTORISTA, "
			+ "C1.NOME AS NOMEAJUD1, C2.NOME AS NOMEAJUD2, EQ.NOME AS EQUIPE, "
			+ "M.CXCARREG, M.CXENTREG, (M.CXCARREG - M.CXENTREG) AS DEVCX, "
			+ "M.QTHLCARREGADOS, M.QTHLENTREGUES, (M.QTHLCARREGADOS - M.QTHLENTREGUES) AS DEVHL, "
			+ "M.QTNFCARREGADAS, M.QTNFENTREGUES, (M.QTNFCARREGADAS - M.QTNFENTREGUES) AS DEVNF, "
			+ "M.HRSAI, M.HRENTR, M.TEMPOINTERNO, M.HRMATINAL,M.COD_UNIDADE, TRACKING.TOTAL AS TOTAL_TRACKING, TRACKING.APONTAMENTO_OK "
			+ " FROM MAPA M join token_autenticacao ta on ta.cpf_colaborador = ? and ta.token = ? "
			+ "JOIN VEICULO V ON V.PLACA = M.PLACA "
			+ "JOIN COLABORADOR C ON M.MATRICMOTORISTA = C.MATRICULA_AMBEV "
			+ "JOIN COLABORADOR C1 ON M.MATRICAJUD1 = C1.MATRICULA_AMBEV "
			+ "JOIN COLABORADOR C2 ON M.MATRICAJUD2 = C2.MATRICULA_AMBEV "
			+ " JOIN UNIDADE E ON E.CODIGO = M.COD_UNIDADE "
			+ "JOIN EQUIPE EQ ON EQ.CODIGO = C.COD_EQUIPE "
			+ "LEFT JOIN( SELECT t.mapa AS TRACKING_MAPA, total.total AS TOTAL, ok.APONTAMENTOS_OK AS APONTAMENTO_OK "
			+ "FROM tracking t join MAPA M on m.mapa = t.mapa "
			+ "JOIN (SELECT t.mapa as mapa_ok, count(t.disp_apont_cadastrado) as apontamentos_ok "
			+ "FROM tracking t	WHERE t.disp_apont_cadastrado <= '0.3' "
			+ "GROUP BY t.mapa) as ok on mapa_ok = t.mapa "
			+ "JOIN (SELECT t.mapa as total_entregas, count(t.cod_cliente) as total from tracking t "
			+ "group by t.mapa) as total on total_entregas = t.mapa "
			+ "GROUP BY t.mapa, OK.APONTAMENTOS_OK, total.total) AS TRACKING ON TRACKING_MAPA = M.MAPA "
			+ "WHERE EQ.NOME LIKE ? AND M.COD_UNIDADE = ? AND DATA BETWEEN ? AND ? AND C.matricula_ambev <> 0 AND C1.matricula_ambev <> 0 " +
            "   AND c2.matricula_ambev <> 0 "
			+ "ORDER BY M.DATA, EQ.NOME ";





	private Meta meta;
	
	public RelatorioDaoImpl(){}
	
	public RelatorioDaoImpl (Meta meta){
		this.meta = meta;
	}




	
	public ConsolidadoHolder getRelatorioByPeriodo(LocalDate dataInicial, LocalDate dataFinal, String equipe,
			Long codUnidade, Long cpf, String token) throws SQLException {

		System.out.println(dataInicial);
		System.out.println(dataFinal);
		System.out.println(equipe);
		System.out.println(codUnidade);
		System.out.println(cpf);
		System.out.println(token);

		Connection conn = null;
		PreparedStatement stmt = null;
		ResultSet rSet = null;
		MetasDaoImpl metasDao = new MetasDaoImpl();
		meta = metasDao.getMetasByUnidade(codUnidade);
		ConsolidadoHolder consolidadoHolder = new ConsolidadoHolder();

		try {
			conn = getConnection();
			stmt = conn.prepareStatement(BUSCA_RELATORIO, ResultSet.TYPE_SCROLL_SENSITIVE,
					ResultSet.CONCUR_UPDATABLE);
			stmt.setLong(1, cpf); 
			stmt.setString(2, token); 
			stmt.setString(3, equipe);
			stmt.setLong(4, codUnidade);
			stmt.setDate(5, DateUtils.toSqlDate(dataInicial));
			stmt.setDate(6, DateUtils.toSqlDate(dataFinal));
            L.d("RelatorioDaoImpl", stmt.toString());
			rSet = stmt.executeQuery();
			List<ConsolidadoMapasDia> listConsolidadoMapasDia = new ArrayList<>();
			ConsolidadoMapasDia consolidadoMapasDia = new ConsolidadoMapasDia();
			List<Mapa> listMapas = new ArrayList<>();
			Mapa mapa;
			if(rSet.first()){
			    L.d("RelatorioDaoImpl", "rSet nao vazio");
				mapa = createMapa(rSet);
				listMapas.add(mapa);
			}
			while(rSet.next()){
				if(rSet.getDate("DATA").getTime() == listMapas.get(listMapas.size()-1).getData().getTime()){
					mapa = createMapa(rSet);
					listMapas.add(mapa);
					if(rSet.isLast()){
						consolidadoMapasDia.data = listMapas.get(0).getData();
						consolidadoMapasDia.mapas = listMapas;
						setTotaisConsolidadoDia(consolidadoMapasDia);
						listConsolidadoMapasDia.add(consolidadoMapasDia);						
					}
				}else{
					consolidadoMapasDia.data = listMapas.get(0).getData();
					consolidadoMapasDia.mapas = listMapas;
					setTotaisConsolidadoDia(consolidadoMapasDia);
					listConsolidadoMapasDia.add(consolidadoMapasDia);
					consolidadoMapasDia = new ConsolidadoMapasDia();
					listMapas = new ArrayList<>();
					mapa = new Mapa();
					mapa = createMapa(rSet);
					listMapas.add(mapa);
				}
			}
			consolidadoHolder.codUnidade = codUnidade;
			consolidadoHolder.listConsolidadoMapasDia = listConsolidadoMapasDia;
			if(!listConsolidadoMapasDia.isEmpty()){
				setTotaisHolder(consolidadoHolder);	
			}
			
		} finally {
			closeConnection(conn, stmt, rSet);
		}
		System.out.print(consolidadoHolder);
		return  consolidadoHolder;
	}

	public Mapa createMapa(ResultSet rSet) throws SQLException{
	    Mapa mapa = new Mapa();
		mapa.setNumeroMapa(rSet.getInt("MAPA"));

        L.d("tag", "Criou o mapa: " + mapa.getNumeroMapa());
		mapa.setData(rSet.getDate("DATA"));
		mapa.setEquipe(rSet.getString("EQUIPE"));
		mapa.setMotorista(rSet.getString("NOMEMOTORISTA"));
		mapa.setAjudante1(rSet.getString("NOMEAJUD1"));
		mapa.setAjudante2(rSet.getString("NOMEAJUD2"));
		mapa.setPlaca(rSet.getString("PLACA"));
		mapa.setDevCx(createDevCx(rSet));
		mapa.setDevNf(createDevNf(rSet));
		mapa.setDevHl(createDevHl(rSet));
		mapa.setTempoInterno(createTempoInterno(rSet));
		mapa.setTempoRota(createTempoRota(rSet));
		mapa.setTempoLargada(createTempoLargada(rSet));
		mapa.setJornadaLiquida(createJornadaLiquida(rSet));
		mapa.setTracking(createTracking(rSet));
		mapa.setCodUnidade(rSet.getLong("COD_UNIDADE"));

		return mapa;
	}

	public ItemDevolucaoCx createDevCx(ResultSet rSet) throws SQLException{
		ItemDevolucaoCx itemDevolucaoCx = new ItemDevolucaoCx();
		itemDevolucaoCx.setData(rSet.getDate("DATA"));
		itemDevolucaoCx.setCarregadas(rSet.getDouble("CXCARREG"));
		itemDevolucaoCx.setEntregues(rSet.getDouble("CXENTREG"));
		itemDevolucaoCx.setDevolvidas(itemDevolucaoCx.getCarregadas() - itemDevolucaoCx.getEntregues());
		if(itemDevolucaoCx.getCarregadas() > 0){itemDevolucaoCx.setResultado(itemDevolucaoCx.getDevolvidas() / itemDevolucaoCx.getCarregadas());}
		itemDevolucaoCx.setMeta(meta.getMetaDevCx());
		itemDevolucaoCx.setBateuMeta(MetaUtils.bateuMeta(itemDevolucaoCx.getResultado(), meta.getMetaDevCx()));
		return itemDevolucaoCx;
	}

	public ItemDevolucaoNf createDevNf(ResultSet rSet) throws SQLException{
		ItemDevolucaoNf itemDevolucaoNf = new ItemDevolucaoNf();
		itemDevolucaoNf.setData(rSet.getDate("DATA"));
		itemDevolucaoNf.setCarregadas(rSet.getDouble("QTNFCARREGADAS"));
		itemDevolucaoNf.setEntregues(rSet.getDouble("QTNFENTREGUES"));
		itemDevolucaoNf.setDevolvidas(itemDevolucaoNf.getCarregadas() - itemDevolucaoNf.getEntregues());
		if(itemDevolucaoNf.getCarregadas()>0){itemDevolucaoNf.setResultado(itemDevolucaoNf.getDevolvidas() / itemDevolucaoNf.getCarregadas());}
		itemDevolucaoNf.setMeta(meta.getMetaDevNf());
		itemDevolucaoNf.setBateuMeta(MetaUtils.bateuMeta(itemDevolucaoNf.getResultado(), meta.getMetaDevNf()));
		return itemDevolucaoNf;
	}

	public ItemDevolucaoHl createDevHl(ResultSet rSet) throws SQLException{
		ItemDevolucaoHl itemDevolucaoHl = new ItemDevolucaoHl();
		itemDevolucaoHl.setData(rSet.getDate("DATA"));
		itemDevolucaoHl.setCarregadas(rSet.getDouble("QTHLCARREGADOS"));
		itemDevolucaoHl.setEntregues(rSet.getDouble("QTHLENTREGUES"));
		itemDevolucaoHl.setDevolvidas(itemDevolucaoHl.getCarregadas() - itemDevolucaoHl.getEntregues());
		if(itemDevolucaoHl.getCarregadas() > 0){itemDevolucaoHl.setResultado(itemDevolucaoHl.getDevolvidas() / itemDevolucaoHl.getCarregadas());}
		itemDevolucaoHl.setMeta(meta.getMetaDevHl());
		itemDevolucaoHl.setBateuMeta(MetaUtils.bateuMeta(itemDevolucaoHl.getResultado(), meta.getMetaDevHl()));
		return itemDevolucaoHl;
	}

	public ItemTempoInterno createTempoInterno(ResultSet rSet) throws SQLException{
		ItemTempoInterno itemTempoInterno = new ItemTempoInterno();
		Time tempoInterno;
		itemTempoInterno.setData(rSet.getDate("DATA"));
		itemTempoInterno.setHrEntrada((rSet.getTime("HRENTR")));
		L.d("t", "Pegando tempo interno");
		tempoInterno = new Time(0);
		tempoInterno = rSet.getTime("TEMPOINTERNO");
		if (tempoInterno != null) {
			// entrada + tempo interno = horario do fechamento
			itemTempoInterno.setHrFechamento(TimeUtils.somaHoras(itemTempoInterno.getHrEntrada(), tempoInterno));
			itemTempoInterno.setResultado(tempoInterno);
			itemTempoInterno.setMeta(meta.getMetaTempoInternoHoras());
			itemTempoInterno.setBateuMeta(MetaUtils.bateuMeta(tempoInterno, meta.getMetaTempoInternoHoras()));
		}else{
			itemTempoInterno.setHrFechamento(new Time(0));
			itemTempoInterno.setResultado(new Time(0));
			itemTempoInterno.setMeta(meta.getMetaTempoInternoHoras());
			itemTempoInterno.setBateuMeta(false);
		}
		return itemTempoInterno;
	}

	public ItemTempoRota createTempoRota(ResultSet rSet) throws SQLException{
		ItemTempoRota itemTempoRota = new ItemTempoRota();
		itemTempoRota.setData(rSet.getDate("DATA"));
		itemTempoRota.setHrEntrada(rSet.getTime("HRENTR"));
		itemTempoRota.setHrSaida(TimeUtils.toSqlTime(rSet.getTimestamp("HRSAI")));
		// saber o tempo que o caminhão ficou na rua, por isso hora de
		// entrada(volta da rota) - hora de saída( saída para rota)
		itemTempoRota.setResultado(
				TimeUtils.differenceBetween(itemTempoRota.getHrEntrada(), itemTempoRota.getHrSaida()));
		itemTempoRota.setMeta(meta.getMetaTempoRotaHoras());
		itemTempoRota.setBateuMeta(MetaUtils.bateuMeta(itemTempoRota.getResultado(), meta.getMetaTempoRotaHoras()));
		return itemTempoRota;
	}

	public ItemTempoLargada createTempoLargada(ResultSet rSet) throws SQLException{
		ItemTempoLargada itemTempoLargada = new ItemTempoLargada();
		itemTempoLargada.setData(rSet.getDate("DATA"));
		itemTempoLargada.setHrMatinal(rSet.getTime("HRMATINAL"));
		itemTempoLargada.setHrSaida(TimeUtils.toSqlTime(rSet.getTimestamp("HRSAI")));
		itemTempoLargada.setResultado(
				MetaUtils.calculaTempoLargada(itemTempoLargada.getHrSaida(), itemTempoLargada.getHrMatinal()));
		itemTempoLargada.setMeta(meta.getMetaTempoLargadaHoras());
		itemTempoLargada.setBateuMeta(
				MetaUtils.bateuMeta(itemTempoLargada.getResultado(), meta.getMetaTempoLargadaHoras()));
		return itemTempoLargada;
	}

	public ItemJornadaLiquida createJornadaLiquida(ResultSet rSet) throws SQLException{
		Time matinal;
		Time rota;
		Time tempoInterno;
		tempoInterno = new Time(rSet.getTime("TEMPOINTERNO").getTime()+0) ;
		rota = TimeUtils.differenceBetween(TimeUtils.toSqlTime(rSet.getTimestamp("HRENTR")),
				TimeUtils.toSqlTime(rSet.getTimestamp("HRSAI")));
		matinal = MetaUtils.calculaTempoLargada(TimeUtils.toSqlTime(rSet.getTimestamp("HRSAI")),
				rSet.getTime("HRMATINAL"));
		ItemJornadaLiquida itemJornadaLiquida = new ItemJornadaLiquida();
		itemJornadaLiquida.setData(rSet.getDate("DATA"));
		itemJornadaLiquida.setTempoInterno(tempoInterno);
		itemJornadaLiquida.setTempoRota(rota);
		itemJornadaLiquida.setTempoLargada(matinal);
		itemJornadaLiquida.setResultado(TimeUtils.somaHoras(TimeUtils.somaHoras(matinal, rota), tempoInterno));
		itemJornadaLiquida.setMeta(meta.getMetaJornadaLiquidaHoras());
		itemJornadaLiquida.setBateuMeta(
				MetaUtils.bateuMeta(itemJornadaLiquida.getResultado(), meta.getMetaJornadaLiquidaHoras()));
		return itemJornadaLiquida;
	}

	public ItemTracking createTracking (ResultSet rSet) throws SQLException{
		ItemTracking itemTracking = new ItemTracking();
		itemTracking.setData(rSet.getDate("DATA"));
		itemTracking.setTotal(rSet.getDouble("TOTAL_TRACKING"));
		itemTracking.setOk(rSet.getDouble("APONTAMENTO_OK"));
		itemTracking.setNok(itemTracking.getTotal() - itemTracking.getOk());
		if(itemTracking.getTotal() == 0){
			itemTracking.setResultado(0);
		}else{itemTracking.setResultado(itemTracking.getOk() / itemTracking.getTotal());}
		itemTracking.setMeta(meta.getMetaTracking());
		itemTracking.setBateuMeta(!(MetaUtils.bateuMeta(itemTracking.getResultado(), itemTracking.getMeta())));
		return itemTracking;
	}

	private void setTotaisConsolidadoDia (ConsolidadoMapasDia consolidadoMapasDia){
		List<Mapa> listMapas = consolidadoMapasDia.mapas;
		consolidadoMapasDia.data = listMapas.get(0).getData();

		for(Mapa mapa : listMapas){
			consolidadoMapasDia.cxCarregadas += mapa.getDevCx().getCarregadas();
			consolidadoMapasDia.cxEntregues += mapa.getDevCx().getEntregues();
			consolidadoMapasDia.cxDevolvidas += mapa.getDevCx().getDevolvidas();

			consolidadoMapasDia.nfCarregadas += mapa.getDevNf().getCarregadas();
			consolidadoMapasDia.nfEntregues += mapa.getDevNf().getEntregues();
			consolidadoMapasDia.nfDevolvidas += mapa.getDevNf().getDevolvidas();

			consolidadoMapasDia.hlCarregadas += mapa.getDevHl().getCarregadas();
			consolidadoMapasDia.hlEntregues += mapa.getDevHl().getEntregues();
			consolidadoMapasDia.hlDevolvidas += mapa.getDevHl().getDevolvidas();
			
			consolidadoMapasDia.OkTracking += mapa.getTracking().getOk();
			consolidadoMapasDia.NokTracking += mapa.getTracking().getNok();
			consolidadoMapasDia.totalTracking += mapa.getTracking().getTotal();

			if(mapa.getTempoLargada().isBateuMeta()){
				consolidadoMapasDia.mapasOkLargada += 1;
			}else{consolidadoMapasDia.mapasNokLargada += 1;}
			consolidadoMapasDia.totalMapasLargada += 1;

			if(mapa.getTempoRota().isBateuMeta()){
				consolidadoMapasDia.mapasOkRota += 1;
			}else{consolidadoMapasDia.mapasNokRota += 1;}
			consolidadoMapasDia.totalMapasRota += 1;

			if(mapa.getTempoInterno().isBateuMeta()){
				consolidadoMapasDia.mapasOkInterno += 1;
			}else{consolidadoMapasDia.mapasNokInterno += 1;}
			consolidadoMapasDia.totalMapasInterno += 1;

			if(mapa.getJornadaLiquida().isBateuMeta()){
				consolidadoMapasDia.mapasOkJornadaLiquida += 1;
			}else{consolidadoMapasDia.mapasNokJornadaLiquida += 1;}
			consolidadoMapasDia.totalMapasJornadaLiquida += 1;
						
		}
		consolidadoMapasDia.codUnidade = consolidadoMapasDia.mapas.get(0).getCodUnidade();

		
		if(consolidadoMapasDia.cxCarregadas > 0){
		consolidadoMapasDia.resultadoDevCx = (double)consolidadoMapasDia.cxDevolvidas / (double)consolidadoMapasDia.cxCarregadas;
		consolidadoMapasDia.metaDevCx = meta.getMetaDevCx();
		consolidadoMapasDia.bateuDevCx = MetaUtils.bateuMeta(consolidadoMapasDia.resultadoDevCx, meta.getMetaDevCx());
		}
		
		if(consolidadoMapasDia.nfCarregadas > 0){
		consolidadoMapasDia.resultadoDevNf = (double)consolidadoMapasDia.nfDevolvidas / (double)consolidadoMapasDia.nfCarregadas;
		consolidadoMapasDia.metaDevNf = meta.getMetaDevNf();
		consolidadoMapasDia.bateuDevNf = MetaUtils.bateuMeta(consolidadoMapasDia.resultadoDevNf, meta.getMetaDevNf());
		}

		if(consolidadoMapasDia.hlCarregadas > 0){
		consolidadoMapasDia.resultadoDevHl = (double)consolidadoMapasDia.hlDevolvidas / (double)consolidadoMapasDia.hlCarregadas;
		consolidadoMapasDia.metaDevHl = meta.getMetaDevHl();
		consolidadoMapasDia.bateuDevHl = MetaUtils.bateuMeta(consolidadoMapasDia.resultadoDevHl, meta.getMetaDevHl());
		}
		
		consolidadoMapasDia.resultadoLargada = (double)consolidadoMapasDia.mapasOkLargada / (double)consolidadoMapasDia.totalMapasLargada;
		consolidadoMapasDia.metaTempoLargada = meta.getMetaTempoLargadaMapas();
		consolidadoMapasDia.bateuLargada = MetaUtils.bateuMetaMapas(consolidadoMapasDia.resultadoLargada, meta.getMetaTempoLargadaMapas());

		consolidadoMapasDia.resultadoRota = (double)consolidadoMapasDia.mapasOkRota / (double)consolidadoMapasDia.totalMapasRota;
		consolidadoMapasDia.metaTempoRota = meta.getMetaTempoRotaMapas();
		consolidadoMapasDia.bateuRota = MetaUtils.bateuMetaMapas(consolidadoMapasDia.resultadoRota, meta.getMetaTempoRotaMapas());

		consolidadoMapasDia.resultadoInterno = (double)consolidadoMapasDia.mapasOkInterno / (double)consolidadoMapasDia.totalMapasInterno;
		consolidadoMapasDia.metaTempoInterno = meta.getMetaTempoInternoMapas();
		consolidadoMapasDia.bateuInterno = MetaUtils.bateuMetaMapas(consolidadoMapasDia.resultadoInterno, meta.getMetaTempoInternoMapas());

		consolidadoMapasDia.resultadoJornada = (double)consolidadoMapasDia.mapasOkJornadaLiquida / (double)consolidadoMapasDia.totalMapasJornadaLiquida;
		consolidadoMapasDia.metaJornada = meta.getMetaJornadaLiquidaMapas();
		consolidadoMapasDia.bateuJornada = MetaUtils.bateuMetaMapas(consolidadoMapasDia.resultadoJornada, meta.getMetaJornadaLiquidaMapas());
		
		if(consolidadoMapasDia.totalTracking > 0){
		consolidadoMapasDia.resultadoTracking = (double)consolidadoMapasDia.OkTracking / (double)consolidadoMapasDia.totalTracking;
		consolidadoMapasDia.metaTracking = meta.getMetaTracking();
		consolidadoMapasDia.bateuTracking = MetaUtils.bateuMetaMapas(consolidadoMapasDia.resultadoTracking, meta.getMetaTracking());
		}
	}

	private void setTotaisHolder (ConsolidadoHolder consolidadoHolder){
		List<ConsolidadoMapasDia> listConsolidados = consolidadoHolder.listConsolidadoMapasDia;

		for(ConsolidadoMapasDia consolidado : listConsolidados){

			consolidadoHolder.cxCarregadas += consolidado.cxCarregadas;
			consolidadoHolder.cxEntregues += consolidado.cxEntregues;
			consolidadoHolder.cxDevolvidas += consolidado.cxDevolvidas;

			consolidadoHolder.nfCarregadas += consolidado.nfCarregadas;
			consolidadoHolder.nfEntregues += consolidado.nfEntregues;
			consolidadoHolder.nfDevolvidas += consolidado.nfDevolvidas;

			consolidadoHolder.hlCarregadas += consolidado.hlCarregadas;
			consolidadoHolder.hlEntregues += consolidado.hlEntregues;
			consolidadoHolder.hlDevolvidas += consolidado.hlDevolvidas;

			consolidadoHolder.mapasOkLargada += consolidado.mapasOkLargada;
			consolidadoHolder.mapasNokLargada += consolidado.mapasNokLargada;
			consolidadoHolder.totalMapasLargada += consolidado.totalMapasLargada;

			consolidadoHolder.mapasOkRota += consolidado.mapasOkRota;
			consolidadoHolder.mapasNokRota += consolidado.mapasNokRota;
			consolidadoHolder.totalMapasRota += consolidado.totalMapasRota;

			consolidadoHolder.mapasOkInterno += consolidado.mapasOkInterno;
			consolidadoHolder.mapasNokInterno += consolidado.mapasNokInterno;
			consolidadoHolder.totalMapasInterno += consolidado.totalMapasInterno;

			consolidadoHolder.mapasOkJornadaLiquida += consolidado.mapasOkJornadaLiquida;
			consolidadoHolder.mapasNokJornadaLiquida += consolidado.mapasNokJornadaLiquida;
			consolidadoHolder.totalMapasJornadaLiquida += consolidado.totalMapasJornadaLiquida;
			
			consolidadoHolder.OkTracking += consolidado.OkTracking;
			consolidadoHolder.NokTracking += consolidado.NokTracking;
			consolidadoHolder.totalTracking += consolidado.totalTracking;
		}

		
		if(consolidadoHolder.cxCarregadas > 0){
		consolidadoHolder.resultadoDevCx = (double)consolidadoHolder.cxDevolvidas / (double)consolidadoHolder.cxCarregadas;
		consolidadoHolder.metaDevCx = meta.getMetaDevCx();
		consolidadoHolder.bateuDevCx = MetaUtils.bateuMeta(consolidadoHolder.resultadoDevCx, meta.getMetaDevCx());
		}

		if(consolidadoHolder.nfCarregadas > 0){
		consolidadoHolder.resultadoDevNf = (double)consolidadoHolder.nfDevolvidas / (double)consolidadoHolder.nfCarregadas;
		consolidadoHolder.metaDevNf = meta.getMetaDevNf();
		consolidadoHolder.bateuDevNf = MetaUtils.bateuMeta(consolidadoHolder.resultadoDevNf, meta.getMetaDevNf());
		}

		if(consolidadoHolder.hlCarregadas > 0){
		consolidadoHolder.resultadoDevHl = (double)consolidadoHolder.hlDevolvidas / (double)consolidadoHolder.hlCarregadas;
		consolidadoHolder.metaDevHl = meta.getMetaDevHl();
		consolidadoHolder.bateuDevHl = MetaUtils.bateuMeta(consolidadoHolder.resultadoDevHl, meta.getMetaDevHl());
		}
		
		consolidadoHolder.resultadoLargada = (double)consolidadoHolder.mapasOkLargada / (double)consolidadoHolder.totalMapasLargada;
		consolidadoHolder.metaTempoLargada = meta.getMetaTempoLargadaMapas();
		consolidadoHolder.bateuLargada = MetaUtils.bateuMetaMapas(consolidadoHolder.resultadoLargada, meta.getMetaTempoLargadaMapas());

		consolidadoHolder.resultadoRota = (double)consolidadoHolder.mapasOkRota / (double)consolidadoHolder.totalMapasRota;
		consolidadoHolder.metaTempoRota = meta.getMetaTempoRotaMapas();
		consolidadoHolder.bateuRota = MetaUtils.bateuMetaMapas(consolidadoHolder.resultadoRota, meta.getMetaTempoRotaMapas());

		consolidadoHolder.resultadoInterno = (double)consolidadoHolder.mapasOkInterno / (double)consolidadoHolder.totalMapasInterno;
		consolidadoHolder.metaTempoInterno = meta.getMetaTempoInternoMapas();
		consolidadoHolder.bateuInterno = MetaUtils.bateuMetaMapas(consolidadoHolder.resultadoInterno, meta.getMetaTempoInternoMapas());

		consolidadoHolder.resultadoJornada = (double)consolidadoHolder.mapasOkJornadaLiquida / (double)consolidadoHolder.totalMapasJornadaLiquida;
		consolidadoHolder.metaJornada = meta.getMetaJornadaLiquidaMapas();
		consolidadoHolder.bateuJornada = MetaUtils.bateuMetaMapas(consolidadoHolder.resultadoJornada, meta.getMetaJornadaLiquidaMapas());
		
		if(consolidadoHolder.totalTracking > 0){
		consolidadoHolder.resultadoTracking = (double)consolidadoHolder.OkTracking / (double)consolidadoHolder.totalTracking;
		consolidadoHolder.metaTracking = meta.getMetaTracking();
		consolidadoHolder.bateuTracking = MetaUtils.bateuMetaMapas(consolidadoHolder.resultadoTracking, meta.getMetaTracking());
		}
	}

}