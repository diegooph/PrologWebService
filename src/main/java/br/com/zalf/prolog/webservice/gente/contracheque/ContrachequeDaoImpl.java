package br.com.zalf.prolog.webservice.gente.contracheque;

import br.com.zalf.prolog.webservice.DatabaseConnection;
import br.com.zalf.prolog.webservice.commons.util.DateUtils;
import br.com.zalf.prolog.webservice.commons.util.L;
import br.com.zalf.prolog.webservice.entrega.indicador.IndicadorDaoImpl;
import br.com.zalf.prolog.webservice.entrega.indicador.acumulado.IndicadorAcumulado;
import br.com.zalf.prolog.webservice.entrega.produtividade.ItemProdutividade;
import br.com.zalf.prolog.webservice.entrega.produtividade.ProdutividadeDaoImpl;
import br.com.zalf.prolog.webservice.gente.contracheque.model.Contracheque;
import br.com.zalf.prolog.webservice.gente.contracheque.model.ItemContracheque;
import br.com.zalf.prolog.webservice.gente.contracheque.model.ItemImportContracheque;
import br.com.zalf.prolog.webservice.gente.contracheque.model.RestricoesContracheque;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Created by Zalf on 21/11/16.
 */
public class ContrachequeDaoImpl extends DatabaseConnection implements ContrachequeDao {

    private static final String TAG = ContrachequeDaoImpl.class.getSimpleName();


    @Override
    public Contracheque getPreContracheque(Long cpf, Long codUnidade, int ano, int mes) throws SQLException {
        Connection conn = null;
        Contracheque contracheque = null;
        RestricoesContracheque restricoes = null;
        ProdutividadeDaoImpl produtividadeDao = new ProdutividadeDaoImpl();
        try {
            conn = getConnection();
            List<ItemContracheque> itens = getItensContracheque(conn, cpf, ano, mes, codUnidade);
            restricoes = getRestricaoCalculoContracheque(conn, cpf);

            //primeiro é verificado se existem itens para o período selecionado
            if (itens != null) {
                contracheque = new Contracheque();
                // depois é verificado se o colaborador recebe premio, ou seja, é um motorista ou um ajudante
                if (restricoes.codFuncaoSolicitante == restricoes.codFuncaoAjudante || restricoes.codFuncaoSolicitante == restricoes.codFuncaoMotorista) {
                    double bonus;
//                    para algumas unidades, a recarga compoe o calculo do premio, para outra é uma verba separada
                    if(recebeBonus(ano, mes, cpf, restricoes.indicadorBonus)) {
                        if(restricoes.codFuncaoSolicitante == restricoes.codFuncaoMotorista){
                            bonus = restricoes.valorBonusMotorista;
                        }else {
                            bonus = restricoes.valorBonusAjudante;
                        }
                    }else{
                        bonus = 0;
                    }

//                    agora temos o valor do bonus para compor o calculo do premio

//                    calcularemos agora o valor das recargas e da produtividade

                    double produtividade;
                    double valorRecargas;
                    int qtRecargas;

                    List<ItemProdutividade> itensProdutividade = produtividadeDao.getProdutividadeByPeriodo(ano, mes, cpf, false);
                    valorRecargas = produtividadeDao.getValorTotalRecargas(itensProdutividade);
                    qtRecargas = produtividadeDao.getQtRecargas(itensProdutividade);
                    produtividade = produtividadeDao.getTotalItens(itensProdutividade) - valorRecargas;

//                    a partir daqui temos todos os valores para realizar o calculo do premio
                    double premio;
                    if(restricoes.recargaPartePremio) {
                        premio = getPremio(conn, codUnidade, itens, bonus, valorRecargas, produtividade);
                        ItemContracheque itemPremio = new ItemContracheque();
                        itemPremio.setDescricao("Prêmio");
                        itemPremio.setSubDescricao("Produtividade + " + qtRecargas + " recargas + Bônus NS R$" + bonus);
                        itemPremio.setValor(premio);
                        itens.add(itemPremio);
                    }else {
                        premio = getPremio(conn, codUnidade, itens, bonus, 0, produtividade);
                        ItemContracheque itemPremio = new ItemContracheque();
                        itemPremio.setDescricao("Prêmio");
                        itemPremio.setSubDescricao("Produtividade + Bônus: R$" + bonus);
                        itemPremio.setValor(premio);
                        itens.add(itemPremio);
                        if(valorRecargas > 0){
                            ItemContracheque itemRecargas = new ItemContracheque();
                            itemRecargas.setDescricao("Recargas");
                            itemRecargas.setSubDescricao(qtRecargas + " recargas realizadas");
                            itemRecargas.setValor(valorRecargas);
                            itens.add(itemRecargas);
                        }
                    }
                //caso ele não receba premio, repassamos diretamente os itens
                } else {
                    contracheque.setItens(itens);
                    return contracheque;
                }
                Collections.sort(itens, new CustomComparator());
                contracheque.setItens(itens);
            }
        } finally {
            closeConnection(conn, null, null);
        }
        return contracheque;
    }

    private List<ItemContracheque> getItensContracheque(Connection conn, Long cpf, int ano, int mes, Long codUnidade) throws SQLException {
        PreparedStatement stmt = null;
        ResultSet rSet = null;
        List<ItemContracheque> itens = new ArrayList<>();
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
        ItemContracheque item = new ItemContracheque();
        item.setCodigo(rSet.getString("CODIGO_ITEM"));
        item.setDescricao(rSet.getString("DESCRICAO"));
        item.setSubDescricao(rSet.getString("SUB_DESCRICAO"));
        item.setValor(rSet.getDouble("VALOR"));
        return item;
    }

    private RestricoesContracheque getRestricaoCalculoContracheque(Connection conn, Long cpf) throws SQLException {
        ResultSet rSet;
        RestricoesContracheque restricoes = new RestricoesContracheque();
        PreparedStatement stmt = conn.prepareStatement("SELECT pc.*, c.cod_funcao as COD_FUNCAO_SOLICITANTE\n" +
                "FROM colaborador c JOIN pre_contracheque_informacoes pc on c.cod_unidade = pc.cod_unidade\n" +
                "WHERE c.cpf = ?");
        stmt.setLong(1, cpf);
        rSet = stmt.executeQuery();
        if (rSet.next()) {
            restricoes.codFuncaoAjudante = rSet.getInt("COD_CARGO_AJUDANTE");
            restricoes.codFuncaoMotorista = rSet.getInt("COD_CARGO_MOTORISTA");
            restricoes.valorBonusAjudante = rSet.getDouble("BONUS_AJUDANTE");
            restricoes.valorBonusMotorista = rSet.getDouble("BONUS_MOTORISTA");
            restricoes.indicadorBonus = rSet.getString("INDICADOR");
            restricoes.recargaPartePremio = rSet.getBoolean("RECARGA_PARTE_PREMIO");
            restricoes.codFuncaoSolicitante = rSet.getLong("COD_FUNCAO_SOLICITANTE");
        }
        return restricoes;
    }

    private boolean recebeBonus(int ano, int mes, Long cpf, String indicador) throws SQLException{
        IndicadorDaoImpl indicadorDao = new IndicadorDaoImpl();
        ProdutividadeDaoImpl produtividadeDao = new ProdutividadeDaoImpl();
        List<IndicadorAcumulado> indicadores =
        indicadorDao.getAcumuladoIndicadoresIndividual(produtividadeDao.getDataInicial(ano, mes).getTime(),
                DateUtils.toSqlDate(LocalDate.of(ano, mes, 20)).getTime(), cpf);

        for(IndicadorAcumulado indicadorAcumulado : indicadores) {
            if(indicadorAcumulado.getTipo().equals(indicador)){
                return indicadorAcumulado.isBateuMeta();
            }
        }
        return false;
    }

    private double getPremio(Connection conn, Long codUnidade, List<ItemContracheque> itensContracheque, double bonus, double recarga, double produtividade) throws SQLException {
        PreparedStatement stmt = null;
        ResultSet rSet = null;
        List<String> codigosPremio = new ArrayList<>();
        double acumuladoProdutividade = bonus + recarga + produtividade;
        double outrasVerbas = 0;
        double valorPremio;

        stmt = conn.prepareStatement("SELECT *\n" +
                "FROM pre_contracheque_calculo_premio\n" +
                "WHERE cod_unidade = ?");
        stmt.setLong(1, codUnidade);
        rSet = stmt.executeQuery();
        while (rSet.next()) {
            codigosPremio.add(rSet.getString("COD_ITEM"));
        }

        for(ItemContracheque item : itensContracheque) {
            if(codigosPremio.contains(item.getCodigo())){
                outrasVerbas += item.getValor();
            }
        }

        if(outrasVerbas >= acumuladoProdutividade){
            valorPremio = 0;
        }else{
            valorPremio = acumuladoProdutividade - outrasVerbas;
        }

        return valorPremio;
    }

    @Override
    public boolean insertOrUpdateItemImportContracheque(List<ItemImportContracheque> itens, int ano, int mes, Long codUnidade) throws SQLException {
        Connection conn = null;
        try {
            conn = getConnection();
            for (ItemImportContracheque item : itens) {
                if (updateItemImportContracheque(item, ano, mes, codUnidade)) {
                    L.d(TAG, "Atualizado o item:" + item.toString());
                } else {
                    insertItemImportContracheque(item, ano, mes, conn, codUnidade);
                }
            }
        } finally {
            closeConnection(conn, null, null);
        }
        return true;
    }

    @Override
    public boolean updateItemImportContracheque(ItemImportContracheque item, int ano, int mes, Long codUnidade) throws SQLException {
        PreparedStatement stmt = null;
        Connection conn = null;
        try {
            conn = getConnection();
            stmt = conn.prepareStatement("UPDATE PRE_CONTRACHEQUE SET DESCRICAO = ?, SUB_DESCRICAO = ?, VALOR = ?" +
                    " WHERE ANO_REFERENCIA = ? AND MES_REFERENCIA = ? AND CPF_COLABORADOR = ? AND COD_UNIDADE = ? AND CODIGO_ITEM = ?");
            stmt.setString(1, item.getDescricao());
            stmt.setString(2, item.getSubDescricao());
            stmt.setDouble(3, item.getValor());
            stmt.setInt(4, ano);
            stmt.setInt(5, mes);
            stmt.setLong(6, item.getCpf());
            stmt.setLong(7, codUnidade);
            stmt.setString(8, item.getCodigo());
            int count = stmt.executeUpdate();
            if (count == 0) {
                return false;
            }
        } finally {
            closeConnection(conn, stmt, null);
        }
        return true;
    }

    @Override
    public boolean deleteItemImportContracheque(ItemImportContracheque item, int ano, int mes, Long codUnidade) throws SQLException {
        Connection conn = null;
        PreparedStatement stmt = null;
        try {
            conn = getConnection();
            stmt = conn.prepareStatement("DELETE FROM PRE_CONTRACHEQUE WHERE ANO_REFERENCIA = ? AND MES_REFERENCIA = ? AND " +
                    " CPF_COLABORADOR = ? AND COD_UNIDADE = ? AND CODIGO_ITEM = ?");
            stmt.setInt(1, ano);
            stmt.setInt(2, mes);
            stmt.setLong(3, item.getCpf());
            stmt.setLong(4, codUnidade);
            stmt.setString(5, item.getCodigo());
            int count = stmt.executeUpdate();
            if (count == 0) {
                return false;
            }
        } finally {
            closeConnection(conn, stmt, null);
        }
        return true;
    }

    private boolean insertItemImportContracheque(ItemImportContracheque item, int ano, int mes, Connection conn, Long codUnidade) throws SQLException {
        PreparedStatement stmt = null;
        try {
            stmt = conn.prepareStatement("INSERT INTO PRE_CONTRACHEQUE VALUES (?,?,?,?,?,?,?,?)");
            stmt.setLong(1, codUnidade);
            stmt.setLong(2, item.getCpf());
            stmt.setInt(3, mes);
            stmt.setInt(4, ano);
            stmt.setString(5, item.getCodigo());
            stmt.setString(6, item.getDescricao());
            stmt.setString(7, item.getSubDescricao());
            stmt.setDouble(8, item.getValor());
            int count = stmt.executeUpdate();
            if (count == 0) {
                throw new SQLException("Erro ao inserir o item: " + item.toString());
            }
        } finally {
            closeConnection(null, stmt, null);
        }
        return true;
    }

    private class CustomComparator implements Comparator<ItemContracheque> {

        @Override
        public int compare(ItemContracheque o1, ItemContracheque o2) {
            return Double.compare(o2.getValor(), o1.getValor());
        }
    }

    /**
     * Método que busca os itens importados previamente
     *
     * @param codUnidade código da unidade
     * @param ano        da competancia
     * @param mes        da competencia
     * @param cpf        especifico a ser buscado, parâmetro opcional
     * @return
     * @throws SQLException
     */
    @Override
    public List<ItemImportContracheque> getItemImportContracheque(Long codUnidade, int ano, int mes, String cpf) throws SQLException {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rSet = null;
        List<ItemImportContracheque> itens = new ArrayList<>();
        try {
            conn = getConnection();
            stmt = conn.prepareStatement("SELECT pc.*, c.nome FROM pre_contracheque pc left join colaborador c ON\n" +
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
            closeConnection(conn, stmt, rSet);
        }
        return itens;
    }

    private ItemImportContracheque createItemImportContracheque(ResultSet rSet) throws SQLException {
        ItemImportContracheque item = new ItemImportContracheque();
        item.setCodigo(rSet.getString("CODIGO_ITEM"));
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
}
