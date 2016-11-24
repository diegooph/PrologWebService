package br.com.zalf.prolog.webservice.gente.preContracheque;

import br.com.zalf.prolog.commons.util.DateUtils;
import br.com.zalf.prolog.entrega.indicador.indicadores.acumulado.DevNfAcumulado;
import br.com.zalf.prolog.entrega.indicador.indicadores.acumulado.IndicadorAcumulado;
import br.com.zalf.prolog.gente.pre_contracheque.Contracheque;
import br.com.zalf.prolog.gente.pre_contracheque.ItemContracheque;
import br.com.zalf.prolog.webservice.DatabaseConnection;
import br.com.zalf.prolog.webservice.entrega.indicador.IndicadorDaoImpl;
import br.com.zalf.prolog.webservice.entrega.produtividade.ProdutividadeDaoImpl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Zalf on 21/11/16.
 */
public class ContrachequeDaoImpl extends DatabaseConnection {

    private static final String TAG = ContrachequeDaoImpl.class.getSimpleName();

    public Contracheque getPreContracheque(Long cpf, Long codUnidade, int ano, int mes) throws SQLException{
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rSet = null;
        Contracheque contracheque = null;
        List<ItemContracheque> itens = new ArrayList<>();
        try{
            conn = getConnection();
            // busca apenas os itens que não compões o prêmio
            stmt = conn.prepareStatement("SELECT * FROM PRE_CONTRACHEQUE\n" +
                    "WHERE CPF_COLABORADOR = ? AND ANO_REFERENCIA = ? AND MES_REFERENCIA = ?\n" +
                    "AND CODIGO_ITEM NOT IN (SELECT COD_IMPORT_HE FROM PRE_CONTRACHEQUE_PREMISSAS\n" +
                    "WHERE COD_UNIDADE = ?)\n" +
                    "AND CODIGO_ITEM NOT IN (SELECT COD_IMPORT_DSR FROM PRE_CONTRACHEQUE_PREMISSAS\n" +
                    "WHERE COD_UNIDADE = ?)");
            stmt.setLong(1, cpf);
            stmt.setInt(2, ano);
            stmt.setInt(3, mes);
            stmt.setLong(4, codUnidade);
            stmt.setLong(5, codUnidade);
            rSet = stmt.executeQuery();
            // *** cria apenas os itens que nao fazem parte do calculo do premio ***
            while (rSet.next()){
                itens.add(createItemContracheque(rSet));
            }
            //Calculo do prêmio
            List<ItemContracheque> itensPremio = getPremio(cpf, codUnidade, ano, mes, conn, stmt, rSet);
            if(itensPremio != null){
                itens.addAll(itensPremio);
            }
            ItemContracheque bonusDevolucao = getBonus(cpf, codUnidade, ano, mes, conn, stmt, rSet);
            if(bonusDevolucao != null) {
                itens.add(bonusDevolucao);
            }
            contracheque = new Contracheque();
            contracheque.setItens(itens);
        }finally {
            closeConnection(conn, stmt, rSet);
        }
        return contracheque;
    }

    private ItemContracheque createItemContracheque(ResultSet rSet) throws SQLException{
        ItemContracheque item = new ItemContracheque();
        item.setCodigo(rSet.getLong("CODIGO_ITEM"));
        item.setDescrição(rSet.getString("DESCRICAO"));
        item.setSubDescrição(rSet.getString("SUB_DESCRICAO"));
        item.setValor(rSet.getDouble("VALOR"));
        return item;
    }

    private ItemContracheque getBonus (Long cpf, Long codUnidade, int ano, int mes, Connection conn, PreparedStatement stmt,
                                       ResultSet rSet) throws SQLException{
        // dao usada para buscar os acumulados
        IndicadorDaoImpl indicadorDao = new IndicadorDaoImpl();
        // dao usada para calcular a data de inicio, mesmo periodo da produtividade
        ProdutividadeDaoImpl produtividadeDao = new ProdutividadeDaoImpl();
        // lista com todos os acumulados
        List<IndicadorAcumulado> indicadores =
        indicadorDao.getAcumuladoIndicadoresIndividual(produtividadeDao.getDataInicial(ano, mes).getTime(),
                DateUtils.toSqlDate(LocalDate.of(ano, mes, 20)).getTime(), cpf);
        ItemContracheque itemContracheque = null;
        try{
            //query que busca o indicador a ser usado no bonus e o valor a ser pago de acordo com o cod do cargo
            stmt = conn.prepareStatement("SELECT PC.INDICADOR,\n" +
                    "  (CASE WHEN PC.COD_CARGO_AJUDANTE = C.COD_FUNCAO THEN PC.BONUS_AJUDANTE\n" +
                    "    WHEN PC.COD_CARGO_MOTORISTA = C.cod_funcao THEN PC.BONUS_MOTORISTA\n" +
                    "  ELSE 0\n" +
                    "    END\n" +
                    ") AS VALOR FROM PRE_CONTRACHEQUE_PREMISSAS PC JOIN colaborador C ON C.cod_unidade = PC.COD_UNIDADE\n" +
                    "WHERE C.CPF = ?");
            stmt.setLong(1, cpf);
            rSet = stmt.executeQuery();
            if(rSet.next()) {
                String indicador;
                double valorPremio;
                indicador = rSet.getString("INDICADOR");
                valorPremio = rSet.getDouble("VALOR");
                if (valorPremio > 0) {
                    itemContracheque = new ItemContracheque();
                    itemContracheque.setDescrição("Bônus devolução");
                    for (IndicadorAcumulado indicadorAcumulado : indicadores) {
                        if (indicadorAcumulado.getTipo().equals(indicador)) {
                            //buscou o indicador na lista de acumulados
                            DevNfAcumulado devNf = (DevNfAcumulado) indicadorAcumulado;
                            itemContracheque.setSubDescrição("Resultado: " + String.format("%1$,.2f", devNf.getResultado() * 100) + "%");
                            if (devNf.isBateuMeta()) {
                                itemContracheque.setValor(valorPremio);
                            }
                        }
                    }
                }
            }
        }finally {}
        return itemContracheque;
    }

    private List<ItemContracheque> getPremio (Long cpf, Long codUnidade, int ano, int mes, Connection conn,
                                              PreparedStatement stmt, ResultSet rSet) throws SQLException{
        List<ItemContracheque> itensPremio = null;
        double valorProdutividade;
        ProdutividadeDaoImpl produtividadeDao;
        try{
            // busca os itens para calcular o premio (HE e DSR)
            stmt = conn.prepareStatement("SELECT * FROM PRE_CONTRACHEQUE\n" +
                    "WHERE CPF_COLABORADOR = ? AND ANO_REFERENCIA = ? AND MES_REFERENCIA = ?\n" +
                    "AND (CODIGO_ITEM IN (SELECT COD_IMPORT_HE FROM PRE_CONTRACHEQUE_PREMISSAS\n" +
                    "WHERE COD_UNIDADE = ?)\n" +
                    "or CODIGO_ITEM IN (SELECT COD_IMPORT_DSR FROM PRE_CONTRACHEQUE_PREMISSAS\n" +
                    "WHERE COD_UNIDADE = ?))");
            stmt.setLong(1, cpf);
            stmt.setInt(2, ano);
            stmt.setInt(3, mes);
            stmt.setLong(4, codUnidade);
            stmt.setLong(5, codUnidade);
            rSet = stmt.executeQuery();
            if(rSet.next()){
                itensPremio = new ArrayList<>();
                itensPremio.add(createItemContracheque(rSet));
                produtividadeDao = new ProdutividadeDaoImpl();
                valorProdutividade = produtividadeDao.getTotalItens(produtividadeDao.getProdutividadeByPeriodo(ano, mes, cpf, false));
                while (rSet.next()){
                    itensPremio.add(createItemContracheque(rSet));
                }
                itensPremio = calculaPremio(valorProdutividade, itensPremio);
            }

        }finally {}
        return itensPremio;
    }

    private List<ItemContracheque> calculaPremio(double valorProdutividade, List<ItemContracheque> itensPremio){
        double valorHoras = 0;
        // calcula a soma da HE + DSR
        for(ItemContracheque item : itensPremio){
            valorHoras += item.getValor();
        }

        ItemContracheque item = new ItemContracheque();
        item.setDescrição("Prêmio produtividade");
        // compara o total de HE com o valor da produtividade, se for >= premio é zerado
        if(valorHoras >= valorProdutividade){
            item.setValor(0.0);
        // caso a produtividade seja maior do que as horas, premio recebe a diferença
        }else{
            item.setValor(valorProdutividade - valorHoras);
        }
        itensPremio.add(item);
        return itensPremio;
    }



}
