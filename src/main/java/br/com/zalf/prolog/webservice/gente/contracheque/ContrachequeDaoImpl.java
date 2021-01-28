package br.com.zalf.prolog.webservice.gente.contracheque;

import br.com.zalf.prolog.webservice.commons.util.PostgresUtils;
import br.com.zalf.prolog.webservice.commons.util.SqlType;
import br.com.zalf.prolog.webservice.database.DatabaseConnection;
import br.com.zalf.prolog.webservice.Injection;
import br.com.zalf.prolog.webservice.commons.util.Log;
import br.com.zalf.prolog.webservice.entrega.indicador.IndicadorDao;
import br.com.zalf.prolog.webservice.entrega.indicador.acumulado.IndicadorAcumulado;
import br.com.zalf.prolog.webservice.entrega.produtividade.ItemProdutividade;
import br.com.zalf.prolog.webservice.entrega.produtividade.PeriodoProdutividade;
import br.com.zalf.prolog.webservice.entrega.produtividade.ProdutividadeDao;
import br.com.zalf.prolog.webservice.entrega.produtividade.ProdutividadeService;
import br.com.zalf.prolog.webservice.gente.contracheque.model.Contracheque;
import br.com.zalf.prolog.webservice.gente.contracheque.model.ItemContracheque;
import br.com.zalf.prolog.webservice.gente.contracheque.model.ItemImportContracheque;
import br.com.zalf.prolog.webservice.gente.contracheque.model.RestricoesContracheque;
import org.jetbrains.annotations.NotNull;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * Created by Zalf on 21/11/16.
 */
public class ContrachequeDaoImpl extends DatabaseConnection implements ContrachequeDao {
    private static final String TAG = ContrachequeDaoImpl.class.getSimpleName();

    public ContrachequeDaoImpl() {

    }

    @NotNull
    @Override
    public Contracheque getPreContracheque(Long cpf, Long codUnidade, int ano, int mes) throws SQLException {
        Connection conn = null;
        Contracheque contracheque = null;
        final ProdutividadeDao produtividadeDao = Injection.provideProdutividadeDao();
        try {
            conn = getConnection();
            final List<ItemContracheque> itens = getItensContracheque(conn, cpf, ano, mes, codUnidade);
            final RestricoesContracheque restricoes = getRestricaoCalculoContracheque(conn, cpf);

            // Primeiro é verificado se existem itens para o período selecionado.
            if (itens != null) {
                contracheque = new Contracheque();
                final List<ItemProdutividade> itensProdutividade =
                        produtividadeDao.getProdutividadeByPeriodo(ano, mes, cpf, false);
                // Depois é verificado se o colaborador recebe prêmio, ou seja, se é um motorista ou um ajudante
                if (restricoes.codFuncaoSolicitante == restricoes.codFuncaoAjudante
                        || restricoes.codFuncaoSolicitante == restricoes.codFuncaoMotorista) {
                    final double bonus;
                    // Para algumas unidades, a recarga compõem o cálculo do prêmio, para outra é uma verba separada.
                    // O bônus só será considerado apenas se número de viagens for acima ou igual o parametrizado.
                    if ((recebeBonus(ano, mes, cpf, restricoes.indicadorBonus))
                            && restricoes.numeroViagensNecessariasParaReceberBonus <= itensProdutividade.size()) {
                        if (restricoes.codFuncaoSolicitante == restricoes.codFuncaoMotorista) {
                            bonus = restricoes.valorBonusMotorista;
                        } else {
                            bonus = restricoes.valorBonusAjudante;
                        }
                    } else {
                        bonus = 0;
                    }
                    // Agora temos o valor do bônus para compor o calculo do prêmio.
                    // Calcularemos agora o valor das recargas e da produtividade.
                    final int qtRecargas = getQtRecargas(itensProdutividade);
                    final double valorRecargas = getValorTotalRecargas(itensProdutividade);
                    final double produtividade = getTotalItens(itensProdutividade) - valorRecargas;

                    // A partir daqui temos todos os valores para realizar o cálculo do prêmio.
                    final double premio;
                    if (restricoes.recargaPartePremio) {
                        premio = getPremio(conn, codUnidade, itens, bonus, valorRecargas, produtividade);
                        final ItemContracheque itemPremio = new ItemContracheque();
                        itemPremio.setDescricao("Prêmio");
                        itemPremio.setSubDescricao("Produtividade + " + qtRecargas + " recargas + Bônus NS R$" + bonus);
                        itemPremio.setValor(premio);
                        itens.add(itemPremio);
                    } else {
                        premio = getPremio(conn, codUnidade, itens, bonus, 0, produtividade);
                        final ItemContracheque itemPremio = new ItemContracheque();
                        itemPremio.setDescricao("Prêmio");
                        itemPremio.setSubDescricao("Produtividade + Bônus: R$" + bonus);
                        itemPremio.setValor(premio);
                        itens.add(itemPremio);
                        if (valorRecargas > 0) {
                            final ItemContracheque itemRecargas = new ItemContracheque();
                            itemRecargas.setDescricao("Recargas");
                            itemRecargas.setSubDescricao(qtRecargas + " recargas realizadas");
                            itemRecargas.setValor(valorRecargas);
                            itens.add(itemRecargas);
                        }
                    }
                } else {
                    // Caso ele não receba prêmio, repassamos diretamente os itens
                    contracheque.setItens(itens);
                    return contracheque;
                }
                itens.sort(new CustomComparator());
                contracheque.setItens(itens);
            }
        } finally {
            close(conn, null, null);
        }
        return contracheque;
    }

    @Override
    public boolean insertOrUpdateItemImportContracheque(List<ItemImportContracheque> itens, int ano, int mes, Long codUnidade) throws SQLException {
        Connection conn = null;
        try {
            conn = getConnection();
            for (ItemImportContracheque item : itens) {
                if (updateItemImportContracheque(item, ano, mes, codUnidade)) {
                    Log.d(TAG, "Atualizado o item:" + item.toString());
                } else {
                    insertItemImportContracheque(item, ano, mes, conn, codUnidade);
                }
            }
        } finally {
            close(conn, null, null);
        }
        return true;
    }

    @Override
    public boolean updateItemImportContracheque(ItemImportContracheque item, int ano, int mes, Long codUnidade) throws SQLException {
        PreparedStatement stmt = null;
        Connection conn = null;
        try {
            conn = getConnection();
            stmt = conn.prepareStatement("UPDATE PRE_CONTRACHEQUE_ITENS SET DESCRICAO = ?, SUB_DESCRICAO = ?, VALOR = ?" +
                    " WHERE ANO_REFERENCIA = ? AND MES_REFERENCIA = ? AND CPF_COLABORADOR = ? AND COD_UNIDADE = ? AND CODIGO_ITEM = ?");
            stmt.setString(1, item.getDescricao());
            stmt.setString(2, item.getSubDescricao());
            stmt.setDouble(3, item.getValor());
            stmt.setInt(4, ano);
            stmt.setInt(5, mes);
            stmt.setLong(6, item.getCpf());
            stmt.setLong(7, codUnidade);
            stmt.setString(8, item.getCodigoItem());
            int count = stmt.executeUpdate();
            if (count == 0) {
                return false;
            }
        } finally {
            close(conn, stmt, null);
        }
        return true;
    }

    @Override
    public boolean deleteItemImportContracheque(ItemImportContracheque item, int ano, int mes, Long codUnidade, Long cpf, String codItem) throws SQLException {
        Connection conn = null;
        PreparedStatement stmt = null;
        try {
            conn = getConnection();
            stmt = conn.prepareStatement("DELETE FROM PRE_CONTRACHEQUE_ITENS WHERE ANO_REFERENCIA = ? AND MES_REFERENCIA = ? AND " +
                    " CPF_COLABORADOR = ? AND COD_UNIDADE = ? AND CODIGO_ITEM = ?");
            stmt.setInt(1, ano);
            stmt.setInt(2, mes);
            stmt.setLong(3, cpf);
            stmt.setLong(4, codUnidade);
            stmt.setString(5, codItem);
            int count = stmt.executeUpdate();
            if (count == 0) {
                return false;
            }
        } finally {
            close(conn, stmt, null);
        }
        return true;
    }

    @NotNull
    @Override
    public List<ItemImportContracheque> getItemImportContracheque(Long codUnidade, int ano, int mes, String cpf) throws SQLException {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rSet = null;
        final List<ItemImportContracheque> itens = new ArrayList<>();
        try {
            conn = getConnection();
            stmt = conn.prepareStatement("SELECT pc.*, c.nome FROM pre_contracheque_itens pc left join colaborador c ON\n" +
                    "\t pc.cpf_colaborador = c.cpf and pc.cod_unidade = c.cod_unidade\n" +
                    "WHERE pc.cod_unidade = ? \n" +
                    "AND pc.ano_referencia = ?\n" +
                    "AND pc.mes_referencia = ?\n" +
                    "AND pc.cpf_colaborador::text LIKE ?\n" +
                    "ORDER BY c.nome asc, pc.valor desc");
            stmt.setLong(1, codUnidade);
            stmt.setInt(2, ano);
            stmt.setInt(3, mes);
            stmt.setString(4, cpf);
            rSet = stmt.executeQuery();
            while (rSet.next()) {
                itens.add(createItemImportContracheque(rSet));
            }
        } finally {
            close(conn, stmt, rSet);
        }
        return itens;
    }

    @Override
    public void deleteItensImportPreContracheque(@NotNull final List<Long> codItensDelecao) throws Throwable {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rSet = null;
        try {
            conn = getConnection();
            stmt = conn.prepareStatement("SELECT FUNC_PRE_CONTRACHEQUE_DELETA_ITENS(?) AS TOTAL_ITENS_DELETADOS;");
            stmt.setArray(1, PostgresUtils.listToArray(conn, SqlType.BIGINT, codItensDelecao));
            rSet = stmt.executeQuery();
            if (!rSet.next() || rSet.getLong("TOTAL_ITENS_DELETADOS") <= 0) {
                throw new IllegalStateException("Erro ao deletar itens de pré contracheque");
            }
        } finally {
            close(conn, stmt, rSet);
        }
    }

    private List<ItemContracheque> getItensContracheque(Connection conn, Long cpf, int ano, int mes, Long codUnidade) throws SQLException {
        PreparedStatement stmt = null;
        ResultSet rSet = null;
        final List<ItemContracheque> itens = new ArrayList<>();
        try {
            stmt = conn.prepareStatement("SELECT codigo_item, descricao, sub_descricao, valor\n" +
                    "FROM pre_contracheque_itens\n" +
                    "WHERE cpf_colaborador = ? and ano_referencia = ? and mes_referencia = ? and cod_unidade = ? " +
                    "ORDER BY VALOR DESC;");
            stmt.setLong(1, cpf);
            stmt.setInt(2, ano);
            stmt.setInt(3, mes);
            stmt.setLong(4, codUnidade);
            rSet = stmt.executeQuery();
            while (rSet.next()) {
                //cria e adiciona todos os itens na lista
                itens.add(createItemContracheque(rSet));
            }
        } finally {
            closeConnection(null, stmt, rSet);
        }
        return itens;
    }

    private ItemContracheque createItemContracheque(ResultSet rSet) throws SQLException {
        final ItemContracheque item = new ItemContracheque();
        item.setCodigo(rSet.getString("CODIGO_ITEM"));
        item.setDescricao(rSet.getString("DESCRICAO"));
        item.setSubDescricao(rSet.getString("SUB_DESCRICAO"));
        item.setValor(rSet.getDouble("VALOR"));
        return item;
    }

    private RestricoesContracheque getRestricaoCalculoContracheque(Connection conn, Long cpf) throws SQLException {
        ResultSet rSet = null;
        PreparedStatement stmt = null;
        try {
            final RestricoesContracheque restricoes = new RestricoesContracheque();
            stmt = conn.prepareStatement("SELECT pc.*, c.cod_funcao as COD_FUNCAO_SOLICITANTE\n" +
                                                 "FROM colaborador c JOIN pre_contracheque_informacoes pc on c" +
                                                 ".cod_unidade = pc.cod_unidade\n" +
                                                 "WHERE c.cpf = ?");
            stmt.setLong(1, cpf);
            rSet = stmt.executeQuery();
            if (rSet.next()) {
                restricoes.codUnidade = rSet.getLong("COD_UNIDADE");
                restricoes.codFuncaoAjudante = rSet.getInt("COD_CARGO_AJUDANTE");
                restricoes.codFuncaoMotorista = rSet.getInt("COD_CARGO_MOTORISTA");
                restricoes.valorBonusAjudante = rSet.getDouble("BONUS_AJUDANTE");
                restricoes.valorBonusMotorista = rSet.getDouble("BONUS_MOTORISTA");
                restricoes.indicadorBonus = rSet.getString("INDICADOR");
                restricoes.recargaPartePremio = rSet.getBoolean("RECARGA_PARTE_PREMIO");
                restricoes.codFuncaoSolicitante = rSet.getLong("COD_FUNCAO_SOLICITANTE");
                restricoes.numeroViagensNecessariasParaReceberBonus =
                        getNumeroViagensParaReceberBonus(conn, restricoes.codUnidade);
            }
            return restricoes;
        } finally {
            close(stmt, rSet);
        }
    }

    private Short getNumeroViagensParaReceberBonus(final Connection conn,
                                                   final Long codUnidade)
            throws SQLException {
        final String sql = "select uvrm.rm_numero_viagens " +
                "from unidade_valores_rm uvrm " +
                "where uvrm.cod_unidade = ?;";

        try (final PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setLong(1, codUnidade);
            try (final ResultSet rSet = stmt.executeQuery()) {
                if (rSet.next()) {
                    return rSet.getShort("RM_NUMERO_VIAGENS");
                } else {
                    throw new SQLException("Erro buscar o numero de viagens necessarias para receber bônus.");
                }
            }
        }
    }

    private boolean recebeBonus(int ano, int mes, Long cpf, String indicador) throws SQLException {
        final IndicadorDao indicadorDao = Injection.provideIndicadorDao();
        final ProdutividadeService produtividadeService = new ProdutividadeService();
        final PeriodoProdutividade periodoProdutividade = produtividadeService.getPeriodoProdutividade(ano, mes, null, cpf);
        final List<IndicadorAcumulado> indicadores =
                indicadorDao.getAcumuladoIndicadoresIndividual(periodoProdutividade.getDataInicio().getTime(),
                        periodoProdutividade.getDataTermino().getTime(), cpf);

        for (IndicadorAcumulado indicadorAcumulado : indicadores) {
            if (indicadorAcumulado.getTipo().equals(indicador)) {
                return indicadorAcumulado.isBateuMeta();
            }
        }
        return false;
    }

    private double getPremio(Connection conn, Long codUnidade, List<ItemContracheque> itensContracheque,
                             double bonus, double recarga, double produtividade) throws SQLException {
        PreparedStatement stmt= null;
        ResultSet rSet = null;
        try {
            final List<String> codigosPremio = new ArrayList<>();
            final double acumuladoProdutividade = bonus + recarga + produtividade;

            stmt = conn.prepareStatement("SELECT *\n" +
                    "FROM pre_contracheque_calculo_premio\n" +
                    "WHERE cod_unidade = ?");
            stmt.setLong(1, codUnidade);
            rSet = stmt.executeQuery();
            while (rSet.next()) {
                codigosPremio.add(rSet.getString("COD_ITEM"));
            }

            double outrasVerbas = 0;
            for (ItemContracheque item : itensContracheque) {
                if (codigosPremio.contains(item.getCodigo())) {
                    outrasVerbas += item.getValor();
                }
            }

            final double valorPremio;
            if (outrasVerbas >= acumuladoProdutividade) {
                valorPremio = 0;
            } else {
                valorPremio = acumuladoProdutividade - outrasVerbas;
            }

            return valorPremio;
        } finally {
            closeConnection(null, stmt, rSet);
        }
    }

    private boolean insertItemImportContracheque(ItemImportContracheque item, int ano, int mes, Connection conn, Long codUnidade) throws SQLException {
        PreparedStatement stmt = null;
        try {
            stmt = conn.prepareStatement("INSERT INTO PRE_CONTRACHEQUE_ITENS VALUES (?,?,?,?,?,?,?,?)");
            stmt.setLong(1, codUnidade);
            stmt.setLong(2, item.getCpf());
            stmt.setInt(3, mes);
            stmt.setInt(4, ano);
            stmt.setString(5, item.getCodigoItem());
            stmt.setString(6, item.getDescricao());
            stmt.setString(7, item.getSubDescricao());
            stmt.setDouble(8, item.getValor());
            int count = stmt.executeUpdate();
            if (count == 0) {
                throw new SQLException("Erro ao inserir o item: " + item.toString());
            }
        } finally {
            closeStatement(stmt);
        }
        return true;
    }

    private class CustomComparator implements Comparator<ItemContracheque> {

        @Override
        public int compare(ItemContracheque o1, ItemContracheque o2) {
            return Double.compare(o2.getValor(), o1.getValor());
        }
    }



    private ItemImportContracheque createItemImportContracheque(ResultSet rSet) throws SQLException {
        final ItemImportContracheque item = new ItemImportContracheque();
        item.setCodigo(rSet.getString("CODIGO"));
        item.setCodigoItem(rSet.getString("CODIGO_ITEM"));
        item.setDescricao(rSet.getString("DESCRICAO"));
        item.setSubDescricao(rSet.getString("SUB_DESCRICAO"));
        item.setValor(rSet.getDouble("VALOR"));
        item.setCpf(rSet.getLong("CPF_COLABORADOR"));
        item.setNome(rSet.getString("NOME"));
        // a query de busca faz um left join com a tebela colaborador, o que pode ocasinar
        // em colaboradores não cadastrados, essa verificação é feita e é setado o nome
        // no objeto, permitindo ao cliente visulizar o problema
        if (item.getNome() == null) {
            item.setNome("Não cadastrado");
        }
        return item;
    }

    private double getTotalItens(List<ItemProdutividade> itens) {
        double total = 0;
        for (ItemProdutividade item : itens) {
            total += item.getValor();
        }
        return total;
    }

    private double getValorTotalRecargas(List<ItemProdutividade> itens) {
        double total = 0;
        for (ItemProdutividade item : itens) {
            if (item.getCargaAtual().equals(ItemProdutividade.CargaAtual.RECARGA)) {
                total += item.getValor();
            }
        }
        return total;
    }

    private int getQtRecargas(List<ItemProdutividade> itens) {
        int quantidade = 0;
        for (ItemProdutividade item : itens) {
            if (item.getCargaAtual().equals(ItemProdutividade.CargaAtual.RECARGA)) {
                quantidade++;
            }
        }
        return quantidade;
    }


}
