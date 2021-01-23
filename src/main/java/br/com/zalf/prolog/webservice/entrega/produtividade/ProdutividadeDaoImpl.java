package br.com.zalf.prolog.webservice.entrega.produtividade;

import br.com.zalf.prolog.webservice.Injection;
import br.com.zalf.prolog.webservice.TimeZoneManager;
import br.com.zalf.prolog.webservice.commons.util.Log;
import br.com.zalf.prolog.webservice.commons.util.datetime.DateUtils;
import br.com.zalf.prolog.webservice.database.DatabaseConnection;
import br.com.zalf.prolog.webservice.entrega.indicador.IndicadorDao;
import br.com.zalf.prolog.webservice.gente.colaborador.model.Colaborador;

import java.sql.*;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class ProdutividadeDaoImpl extends DatabaseConnection implements ProdutividadeDao {

    private static final String TAG = ProdutividadeDaoImpl.class.getSimpleName();

    @Override
    public List<ItemProdutividade> getProdutividadeByPeriodo(final int ano, final int mes, final Long cpf, final boolean salvaLog)
            throws SQLException {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rSet = null;
        final List<ItemProdutividade> itens = new ArrayList<>();
        final IndicadorDao indicadorDao = Injection.provideIndicadorDao();
        try {
            conn = getConnection();
            stmt = conn.prepareStatement("select * from func_get_produtividade_colaborador(?,?,?)");
            /*
             * O motivo de subtrairmos 1 do mês é devido aos colaboradores estarem acostumados a buscar pelo fim do
             * período
             * da produtividade, exemplo: Selecionado no app Novembro 2017, o normal é aparecer de 21/10 a 20/11.
             */
            stmt.setInt(1, mes);
            stmt.setInt(2, ano);
            stmt.setLong(3, cpf);
            rSet = stmt.executeQuery();
            while (rSet.next()) {
                final ItemProdutividade item = new ItemProdutividade();
                item.setData(rSet.getDate("DATA"));
                item.setValor((double) Math.round(rSet.getDouble("VALOR") * 100) / 100);
                item.setMapa(rSet.getInt("MAPA"));
                item.setFator(rSet.getInt("FATOR"));
                item.setCxsEntregues(rSet.getInt("CXENTREG"));
                item.setValorPorCaixa((double) Math.round(item.getValor() / item.getCxsEntregues() * 100) / 100);
                item.setCargaAtual(ItemProdutividade.CargaAtual.fromString(rSet.getString("CARGAATUAL")));
                item.setTipoMapa(ItemProdutividade.TipoMapa.fromString(rSet.getString("ENTREGA")));
                item.setIndicadores(indicadorDao.createExtratoDia(rSet));
                itens.add(item);
            }
            if (salvaLog) {
                insertMesAnoConsultaProdutividade(ano, mes, conn, cpf);
            }
        } finally {
            closeConnection(conn, stmt, rSet);
        }
        return itens;
    }

    @Override
    public List<HolderColaboradorProdutividade> getConsolidadoProdutividade(final Long codUnidade, final String equipe, final String codFuncao,
                                                                            final long dataInicial, final long dataFinal) throws SQLException {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rSet = null;
        final List<HolderColaboradorProdutividade> holders = new ArrayList<>();
        HolderColaboradorProdutividade holder = null;
        List<ColaboradorProdutividade> colaboradores = new ArrayList<>();
        final Colaborador c = null;
        try {
            conn = getConnection();
            stmt = conn.prepareStatement("select * from func_get_produtividade_consolidado_colaboradores(?,?,?,?,?)");
            stmt.setDate(1, DateUtils.toSqlDate(new Date(dataInicial)));
            stmt.setDate(2, DateUtils.toSqlDate(new Date(dataFinal)));
            stmt.setLong(3, codUnidade);
            stmt.setString(4, equipe);
            stmt.setString(5, codFuncao);
            Log.d(TAG, stmt.toString());
            rSet = stmt.executeQuery();
            while (rSet.next()) {
                if (holder == null) {
                    holder = new HolderColaboradorProdutividade();
                    holder.setFuncao(rSet.getString("funcao"));
                    colaboradores = new ArrayList<>();
                    colaboradores.add(createColaboradorProdutividade(rSet));
                } else {
                    if (holder.getFuncao().equals(rSet.getString("funcao"))) {
                        colaboradores.add(createColaboradorProdutividade(rSet));
                    } else {
                        holder.setProdutividades(colaboradores);
                        holders.add(holder);
                        holder = new HolderColaboradorProdutividade();
                        holder.setFuncao(rSet.getString("funcao"));
                        colaboradores = new ArrayList<>();
                        colaboradores.add(createColaboradorProdutividade(rSet));
                    }
                }
            }
            if (holder != null) {
                holder.setProdutividades(colaboradores);
                holders.add(holder);
            }
        } finally {
            closeConnection(conn, stmt, rSet);
        }
        return holders;
    }

    @Override
    public PeriodoProdutividade getPeriodoProdutividade(final int ano, final int mes, final Long codUnidade, final Long cpf) throws
			SQLException {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rSet = null;
        try {
            conn = getConnection();
            stmt = conn.prepareStatement("select * from func_get_data_inicio_produtividade(?, ?, ?, ?) as inicio FULL OUTER JOIN\n" +
                    "func_get_data_fim_produtividade(?, ?, ?, ?) as fim on 1 = 1;");
            stmt.setInt(1, ano);
            stmt.setInt(2, mes);
            if (cpf == null) {
                stmt.setNull(3, Types.BIGINT);
            } else {
                stmt.setLong(3, cpf);
            }
            if (codUnidade == null) {
                stmt.setNull(4, Types.BIGINT);
            } else {
                stmt.setLong(4, codUnidade);
            }
            stmt.setInt(5, ano);
            stmt.setInt(6, mes);
            if (cpf == null) {
                stmt.setNull(7, Types.BIGINT);
            } else {
                stmt.setLong(7, cpf);
            }
            if (codUnidade == null) {
                stmt.setNull(8, Types.BIGINT);
            } else {
                stmt.setLong(8, codUnidade);
            }
            rSet = stmt.executeQuery();
            if (rSet.next()) {
                return new PeriodoProdutividade(rSet.getDate("inicio"), rSet.getDate("fim"));
            }
        } finally {
            closeConnection(conn, stmt, rSet);
        }
        return null;
    }

    private void insertMesAnoConsultaProdutividade(final int ano, final int mes, final Connection conn, final Long cpf) throws SQLException {
        final PreparedStatement stmt = conn.prepareStatement("INSERT INTO ACESSOS_PRODUTIVIDADE VALUES ( " +
                " (SELECT COD_UNIDADE FROM COLABORADOR WHERE CPF = ?), ?, ?, ?);");
        stmt.setLong(1, cpf);
        stmt.setLong(2, cpf);
        stmt.setObject(3, OffsetDateTime.now(TimeZoneManager.getZoneIdForCpf(cpf, conn)));
        stmt.setString(4, mes + "/" + ano);
        final int count = stmt.executeUpdate();
        if (count == 0) {
            throw new SQLException("Erro ao inserir o log de consulta");
        }
    }

    private ColaboradorProdutividade createColaboradorProdutividade(final ResultSet rSet) throws SQLException {
        final ColaboradorProdutividade c = new ColaboradorProdutividade();
        final Colaborador co = new Colaborador();
        co.setCpf(rSet.getLong("cpf"));
        co.setNome(rSet.getString("nome"));
        c.setColaborador(co);
        c.setQtdCaixas(rSet.getInt("caixas"));
        c.setQtdMapas(rSet.getInt("mapas"));
        c.setValor(rSet.getDouble("valor"));
        return c;
    }
}