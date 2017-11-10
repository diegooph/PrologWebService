package br.com.zalf.prolog.webservice.entrega.produtividade;

import br.com.zalf.prolog.webservice.DatabaseConnection;
import br.com.zalf.prolog.webservice.colaborador.model.Colaborador;
import br.com.zalf.prolog.webservice.commons.util.DateUtils;
import br.com.zalf.prolog.webservice.commons.util.Log;
import br.com.zalf.prolog.webservice.entrega.indicador.IndicadorDaoImpl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
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
			stmt = conn.prepareStatement("select * from func_get_produtividade_colaborador(?,?,?)");
			stmt.setInt(1, mes);
			stmt.setInt(2, ano);
			stmt.setLong(3, cpf);
			Log.d(TAG, stmt.toString());
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

	public double getValorTotalRecargas(List<ItemProdutividade> itens){
		double total = 0;
		for(ItemProdutividade item : itens){
			if(item.getCargaAtual().equals(ItemProdutividade.CargaAtual.RECARGA)){
				total += item.getValor();
			}
		}
		return total;
	}

	public int getQtRecargas(List<ItemProdutividade> itens){
		int quantidade = 0;
		for(ItemProdutividade item : itens){
			if(item.getCargaAtual().equals(ItemProdutividade.CargaAtual.RECARGA)){
				quantidade ++;
			}
		}
		return quantidade;
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
			stmt = conn.prepareStatement("SELECT cpf, matricula_ambev, nome_colaborador AS nome, data_nascimento, funcao, count(mapa) as mapas, sum(cxentreg) as caixas,\n" +
					"sum(valor) as valor\n" +
					"FROM VIEW_PRODUTIVIDADE_EXTRATO\n" +
					"WHERE data between ? and ? and cod_unidade = ? and nome_equipe like ? and cod_funcao::text like ? \n" +
					"GROUP BY 1,2,3,4,5\n" +
					"order by funcao, valor desc, nome;");
			stmt.setDate(1, DateUtils.toSqlDate(new Date(dataInicial)));
			stmt.setDate(2, DateUtils.toSqlDate(new Date(dataFinal)));
			stmt.setLong(3, codUnidade);
			stmt.setString(4, equipe);
			stmt.setString(5, codFuncao);
			Log.d(TAG, stmt.toString());
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
