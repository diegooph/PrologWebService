package br.com.zalf.prolog.webservice.entrega.produtividade;

import br.com.zalf.prolog.webservice.colaborador.Colaborador;
import br.com.zalf.prolog.webservice.commons.util.DateUtils;
import br.com.zalf.prolog.webservice.DatabaseConnection;
import br.com.zalf.prolog.webservice.entrega.indicador.IndicadorDaoImpl;
import br.com.zalf.prolog.webservice.commons.util.L;

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

	public List<ItemProdutividade> getProdutividadeByPeriodo (int ano, int mes, Long cpf, boolean salvaLog) throws SQLException{
		Connection conn = null;
		PreparedStatement stmt = null;
		ResultSet rSet = null;
		List<ItemProdutividade> itens = new ArrayList<>();
		IndicadorDaoImpl indicadorDao = new IndicadorDaoImpl();
		try{
			conn = getConnection();
			stmt = conn.prepareStatement("SELECT\n" +
					"  M.DATA,\n" +
					"  CASE WHEN MOTORISTA.cpf = SOLICITANTE.CPF AND m.entrega <> 'AS'\n" +
					"    THEN (M.vlbateujornmot + M.vlnaobateujornmot + M.vlrecargamot)\n" +
					"  WHEN AJ1.cpf = SOLICITANTE.cpf AND m.entrega <> 'AS'\n" +
					"    THEN (M.vlbateujornaju + M.vlnaobateujornaju + M.vlrecargaaju) / m.fator\n" +
					"  WHEN AJ2.cpf = SOLICITANTE.cpf AND m.entrega <> 'AS'\n" +
					"    THEN (M.vlbateujornaju + M.vlnaobateujornaju + M.vlrecargaaju) / m.fator\n" +
					"  ELSE 0\n" +
					"  END +\n" +
					"  -- case para calcular o valor quando é AS\n" +
					"  CASE WHEN MOTORISTA.cpf = SOLICITANTE.cpf AND m.entrega = 'AS'\n" +
					"    THEN\n" +
					"      --case para calcular o valor com base no número de entregas\n" +
					"      (CASE WHEN m.entregas = 1\n" +
					"        THEN uv.rm_motorista_valor_as_1_entrega\n" +
					"       WHEN m.entregas = 2\n" +
					"         THEN uv.rm_motorista_valor_as_2_entregas\n" +
					"       WHEN m.entregas = 3\n" +
					"         THEN uv.rm_motorista_valor_as_3_entregas\n" +
					"        WHEN m.entregas > 3\n" +
					"         THEN uv.rm_motorista_valor_as_maior_3_entregas\n" +
					"       ELSE 0\n" +
					"       END)\n" +
					"  WHEN AJ1.CPF = SOLICITANTE.cpf AND m.entrega = 'AS'\n" +
					"    THEN\n" +
					"      --case para calcular o valor com base no número de entregas\n" +
					"      (CASE WHEN m.entregas = 1\n" +
					"        THEN uv.rm_ajudante_valor_as_1_entrega\n" +
					"       WHEN m.entregas = 2\n" +
					"         THEN uv.rm_ajudante_valor_as_2_entregas\n" +
					"        WHEN m.entregas = 3\n" +
					"         THEN uv.rm_ajudante_valor_as_3_entregas\n" +
					"       WHEN m.entregas > 3\n" +
					"         THEN uv.rm_ajudante_valor_as_maior_3_entregas\n" +
					"       ELSE 0\n" +
					"       END)\n" +
					"  WHEN AJ2.CPF = SOLICITANTE.cpf AND m.entrega = 'AS'\n" +
					"    THEN\n" +
					"      --case para calcular o valor com base no número de entregas\n" +
					"      (CASE WHEN m.entregas = 1\n" +
					"        THEN uv.rm_ajudante_valor_as_1_entrega\n" +
					"       WHEN m.entregas = 2\n" +
					"         THEN uv.rm_ajudante_valor_as_2_entregas\n" +
					"        WHEN m.entregas = 3\n" +
					"         THEN uv.rm_ajudante_valor_as_3_entregas\n" +
					"       WHEN m.entregas > 2\n" +
					"         THEN uv.rm_ajudante_valor_as_maior_3_entregas\n" +
					"       ELSE 0\n" +
					"       END)\n" +
					"  ELSE 0\n" +
					"  END AS valor,\n" +
					"  m.fator,\n" +
					"  m.cargaatual,\n" +
					"  m.entrega,\n" +
					"  M.DATA,\n" +
					"  M.mapa,\n" +
					"  M.PLACA,\n" +
					"  M.cxcarreg,\n" +
					"  m.cxentreg,\n" +
					"  M.QTHLCARREGADOS,\n" +
					"  M.QTHLENTREGUES,\n" +
					"  M.QTNFCARREGADAS,\n" +
					"  M.QTNFENTREGUES,\n" +
					"  M.entregascompletas,\n" +
					"  M.entregasnaorealizadas,\n" +
					"  m.entregasparciais,\n" +
					"  M.kmprevistoroad,\n" +
					"  M.kmsai,\n" +
					"  M.kmentr,\n" +
					"  to_seconds(M.tempoprevistoroad :: TEXT) AS tempoprevistoroad,\n" +
					"  M.HRSAI,\n" +
					"  M.HRENTR,\n" +
					"  to_seconds(((M.hrentr - M.hrsai) :: TIME) :: TEXT) AS TEMPO_ROTA,\n" +
					"  to_seconds(M.TEMPOINTERNO :: TEXT) AS tempointerno,\n" +
					"  M.HRMATINAL,\n" +
					"  tracking.apontamentos_ok AS apontamentos_ok,\n" +
					"  tracking.total_apontamentos AS total_tracking,\n" +
					"  to_seconds((CASE WHEN m.hrsai :: TIME < m.hrmatinal\n" +
					"    THEN um.meta_tempo_largada_horas\n" +
					"              ELSE (m.hrsai - m.hrmatinal) :: TIME\n" +
					"              END) :: TEXT) AS tempo_largada,\n" +
					"  um.meta_tracking,\n" +
					"  um.meta_tempo_rota_mapas,\n" +
					"  um.meta_caixa_viagem,\n" +
					"  um.meta_dev_hl,\n" +
					"  um.meta_dev_nf,\n" +
					"  um.meta_dev_pdv,\n" +
					"  um.meta_dispersao_km,\n" +
					"  um.meta_dispersao_tempo,\n" +
					"  um.meta_jornada_liquida_mapas,\n" +
					"  um.meta_raio_tracking,\n" +
					"  um.meta_tempo_interno_mapas,\n" +
					"  um.meta_tempo_largada_mapas,\n" +
					"  to_seconds(um.meta_tempo_rota_horas :: TEXT)       AS meta_tempo_rota_horas,\n" +
					"  to_seconds(um.meta_tempo_interno_horas :: TEXT)    AS meta_tempo_interno_horas,\n" +
					"  to_seconds(um.meta_tempo_largada_horas :: TEXT)    AS meta_tempo_largada_horas,\n" +
					"  to_seconds(um.meta_jornada_liquida_horas :: TEXT)  AS meta_jornada_liquida_horas\n" +
					"FROM\n" +
					"  mapa m\n" +
					"  JOIN unidade_metas um\n" +
					"    ON um.cod_unidade = m.cod_unidade AND M.DATA BETWEEN ? AND ?\n" +
					"  JOIN unidade_valores_rm uv ON uv.cod_unidade = m.cod_unidade\n" +
					"  LEFT JOIN (SELECT\n" +
					"               t.mapa                         AS tracking_mapa,\n" +
					"               sum(CASE WHEN t.disp_apont_cadastrado <= um.meta_raio_tracking\n" +
					"                 THEN 1\n" +
					"                   ELSE 0 END)                AS apontamentos_ok,\n" +
					"               count(t.disp_apont_cadastrado) AS total_apontamentos\n" +
					"             FROM tracking t\n" +
					"               JOIN unidade_metas um ON um.cod_unidade = t.código_transportadora\n" +
					"             GROUP BY 1) AS tracking ON tracking_mapa = m.mapa\n" +
					"  JOIN UNIDADE_FUNCAO_PRODUTIVIDADE UFP ON M.cod_unidade = UFP.COD_UNIDADE\n" +
					"  JOIN COLABORADOR MOTORISTA\n" +
					"    ON MOTORISTA.matricula_ambev = M.matricmotorista AND MOTORISTA.cod_funcao = UFP.COD_FUNCAO_MOTORISTA\n" +
					"  LEFT JOIN COLABORADOR AJ1 ON AJ1.matricula_ambev = M.matricajud1 AND AJ1.cod_funcao = UFP.COD_FUNCAO_AJUDANTE\n" +
					"  LEFT JOIN COLABORADOR AJ2 ON AJ2.matricula_ambev = M.matricajud2 AND AJ2.cod_funcao = UFP.COD_FUNCAO_AJUDANTE\n" +
					"  LEFT JOIN COLABORADOR SOLICITANTE ON SOLICITANTE.CPF = ?\n" +
					"WHERE MOTORISTA.cpf = ? OR AJ1.cpf = ? OR AJ2.cpf = ?\n" +
					"ORDER BY m.data");
			stmt.setDate(1, getDataInicial(ano, mes));
			stmt.setDate(2, DateUtils.toSqlDate(LocalDate.of(ano, mes, 20)));
			stmt.setLong(3, cpf);
			stmt.setLong(4, cpf);
			stmt.setLong(5, cpf);
			stmt.setLong(6, cpf);
			L.d(TAG, stmt.toString());
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
			if(salvaLog){
				insertMesAnoConsultaProdutividade(ano, mes, conn, stmt, cpf);
			}
		}finally {
			closeConnection(conn,stmt,rSet);
		}
		return itens;
	}

	public java.sql.Date getDataInicial(int ano, int mes){
		if(mes == 1){
			return DateUtils.toSqlDate(LocalDate.of(ano-1, 12, 21));
		}else{
			return DateUtils.toSqlDate(LocalDate.of(ano, mes-1, 21));
		}

	}

	private void insertMesAnoConsultaProdutividade(int ano, int mes, Connection conn, PreparedStatement stmt, Long cpf) throws SQLException{
		try{
			stmt = conn.prepareStatement("INSERT INTO ACESSOS_PRODUTIVIDADE VALUES ( " +
					" (SELECT COD_UNIDADE FROM COLABORADOR WHERE CPF = ?), ?, ?, ?);");
			stmt.setLong(1, cpf);
			stmt.setLong(2, cpf);
			stmt.setTimestamp(3, DateUtils.toTimestamp(new Date(System.currentTimeMillis())));
			stmt.setString(4, mes + "/" + ano);
			int count = stmt.executeUpdate();
			if(count == 0){
				throw new SQLException("Erro ao inserir o log de consulta");
			}
		}finally {
		}
	}

	public double getTotalItens(List<ItemProdutividade> itens){
	    double total = 0;
        for(ItemProdutividade item : itens){
            total += item.getValor();
        }
        return total;
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
					"  FROM VIEW_MAPA_COLABORADOR VMC JOIN colaborador C ON VMC.CPF = C.cpf " +
					"  JOIN MAPA M ON M.MAPA = VMC.mapa AND M.cod_unidade = VMC.cod_unidade " +
					"  JOIN FUNCAO F ON F.codigo = C.cod_funcao " +
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
			L.d(TAG, stmt.toString());
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
