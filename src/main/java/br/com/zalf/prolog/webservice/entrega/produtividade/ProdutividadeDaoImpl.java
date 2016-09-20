package br.com.zalf.prolog.webservice.entrega.produtividade;

import br.com.zalf.prolog.commons.colaborador.Colaborador;
import br.com.zalf.prolog.commons.util.DateUtils;
import br.com.zalf.prolog.entrega.produtividade.ColaboradorProdutividade;
import br.com.zalf.prolog.entrega.produtividade.HolderColaboradorProdutividade;
import br.com.zalf.prolog.entrega.produtividade.ItemProdutividade;
import br.com.zalf.prolog.webservice.DatabaseConnection;
import br.com.zalf.prolog.webservice.entrega.indicador.IndicadorDaoImpl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class ProdutividadeDaoImpl extends DatabaseConnection implements ProdutividadeDao {

    private static String TAG = ProdutividadeDaoImpl.class.getSimpleName();

	public List<ItemProdutividade> getProdutividadeByPeriodo (int ano, int mes, Long cpf) throws SQLException{
		Connection conn = null;
		PreparedStatement stmt = null;
		ResultSet rSet = null;
		List<ItemProdutividade> itens = new ArrayList<>();
		IndicadorDaoImpl indicadorDao = new IndicadorDaoImpl();
		try{
			conn = getConnection();
			stmt = conn.prepareStatement("SELECT   CASE when (c.matricula_ambev) = m.matricmotorista and m.entrega <> 'AS' then   (M.vlbateujornmot + M.vlnaobateujornmot + M.vlrecargamot)\n" +
					"when (c.matricula_ambev) = m.matricajud1 and m.entrega <> 'AS' then   (M.vlbateujornaju + M.vlnaobateujornaju + M.vlrecargaaju)/m.fator\n" +
					"when (c.matricula_ambev) = m.matricajud2 and m.entrega <> 'AS' then   (M.vlbateujornaju + M.vlnaobateujornaju + M.vlrecargaaju)/m.fator\n" +
					"else 0\n" +
					"end +\n" +
					"-- case para calcular o valor quando é AS\n" +
					"CASE when (c.matricula_ambev) = m.matricmotorista and m.entrega = 'AS' then\n" +
					"--case para calcular o valor com base no número de entregas\n" +
					"(case when m.entregas = 1 then uv.rm_motorista_valor_as_1_entrega\n" +
					"when m.entregas = 2 then uv.rm_motorista_valor_as_2_entregas\n" +
					"when m.entregas > 2 then uv.rm_motorista_valor_as_maior_2_entregas\n" +
					"else 0\n" +
					"end)\n" +
					"when (c.matricula_ambev) = m.matricajud1 and m.entrega = 'AS' then\n" +
					"--case para calcular o valor com base no número de entregas\n" +
					"(case when m.entregas = 1 then uv.rm_ajudante_valor_as_1_entrega\n" +
					"when m.entregas = 2 then uv.rm_ajudante_valor_as_2_entregas\n" +
					"when m.entregas > 2 then uv.rm_ajudante_valor_as_maior_2_entregas\n" +
					"else 0\n" +
					"end)\n" +
					"when (c.matricula_ambev) = m.matricajud2 and m.entrega = 'AS' then\n" +
					"--case para calcular o valor com base no número de entregas\n" +
					"(case when m.entregas = 1 then uv.rm_ajudante_valor_as_1_entrega\n" +
					"when m.entregas = 2 then uv.rm_ajudante_valor_as_2_entregas\n" +
					"when m.entregas > 2 then uv.rm_ajudante_valor_as_maior_2_entregas\n" +
					"else 0\n" +
					"end)\n" +
					"else 0\n" +
					"end as valor , m.fator, m.cargaatual, m.entrega,\n" +
					"M.DATA,  M.mapa, M.PLACA, M.cxcarreg, m.cxentreg,M.QTHLCARREGADOS,\n" +
					"M.QTHLENTREGUES,  M.entregascompletas,  M.entregasnaorealizadas, M.kmprevistoroad, M.kmsai, M.kmentr,\n" +
					"to_seconds(M.tempoprevistoroad::text) as tempoprevistoroad,\n" +
					"M.HRSAI,  M.HRENTR,\n" +
					"to_seconds(((M.hrentr - M.hrsai)::time)::text) AS TEMPO_ROTA,\n" +
					"to_seconds(M.TEMPOINTERNO::text) as tempointerno,  M.HRMATINAL,\n" +
					"tracking.apontamentos_ok as apontamento_ok,\n" +
					"tracking.total_apontamentos as total_tracking,\n" +
					"to_seconds((case when m.hrsai::time < m.hrmatinal then um.meta_tempo_largada_horas else (m.hrsai - m.hrmatinal)::time\n" +
					"end)::text) as tempo_largada,\n" +
					"um.meta_tracking,um.meta_tempo_rota_mapas, um.meta_caixa_viagem,\n" +
					"um.meta_dev_hl, um.meta_dev_pdv, um.meta_dispersao_km, um.meta_dispersao_tempo, um.meta_jornada_liquida_mapas, um.meta_raio_tracking, um.meta_tempo_interno_mapas, um.meta_tempo_largada_mapas,to_seconds(um.meta_tempo_rota_horas::text) as meta_tempo_rota_horas, to_seconds(um.meta_tempo_interno_horas::text) as meta_tempo_interno_horas, to_seconds(um.meta_tempo_largada_horas::text) as meta_tempo_largada_horas,\n" +
					"to_seconds(um.meta_jornada_liquida_horas::text) as meta_jornada_liquida_horas\n" +
					"FROM mapa_colaborador mc join\n" +
					"colaborador c on c.cod_unidade = mc.cod_unidade and mc.cod_ambev = c.matricula_ambev\n" +
					"join mapa m on m.mapa = mc.mapa\n" +
					"join unidade_metas um on um.cod_unidade = m.cod_unidade\n" +
					"join unidade_valores_rm uv on uv.cod_unidade = m.cod_unidade\n" +
					"LEFT JOIN (SELECT t.mapa as tracking_mapa,\n" +
					"sum(case when t.disp_apont_cadastrado <= um.meta_raio_tracking then 1\n" +
					"else 0 end) as apontamentos_ok,\n" +
					"count(t.disp_apont_cadastrado) as total_apontamentos\n" +
					"from tracking t join unidade_metas um on um.cod_unidade = t.código_transportadora\n" +
					"group by 1) as tracking on tracking_mapa = m.mapa\n" +
					"where c.cpf  = ?\n" +
					"AND M.DATA BETWEEN ? AND ?\n" +
					"order by m.data;");
			stmt.setLong(1, cpf);
			stmt.setDate(2, DateUtils.toSqlDate(LocalDate.of(ano, mes-1, 21)));
			stmt.setDate(3, DateUtils.toSqlDate(LocalDate.of(ano, mes, 20)));
			rSet = stmt.executeQuery();
			while(rSet.next()){
				ItemProdutividade item = new ItemProdutividade();
				item.setData(rSet.getDate("DATA"));
				item.setValor((double) Math.round(rSet.getDouble("VALOR")*100)/100);
				item.setMapa(rSet.getInt("MAPA"));
				item.setFator(rSet.getInt("FATOR"));
				item.setCxsEntregues(rSet.getInt("CXENTREG"));
				item.setValorPorCaixa((double) Math.round(item.getValor() / item.getCxsEntregues()*100)/100);
				item.setCargaAtual(ItemProdutividade.CargaAtual.fromString(rSet.getString("CARGAATUAL")));
				item.setTipoMapa(ItemProdutividade.TipoMapa.fromString(rSet.getString("ENTREGA")));
				item.setIndicadores(indicadorDao.createExtratoDia(rSet));
				itens.add(item);
			}
		}finally {
			closeConnection(conn,stmt,rSet);
		}
		return itens;
	}


	public List<HolderColaboradorProdutividade> getConsolidadoProdutividade(Long codUnidade, String equipe, String codFuncao,
																			long dataInicial, long dataFinal) throws SQLException{
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
}
