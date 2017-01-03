package br.com.zalf.prolog.webservice.gente.contracheque;

import br.com.zalf.prolog.commons.util.DateUtils;
import br.com.zalf.prolog.entrega.indicador.indicadores.acumulado.DevNfAcumulado;
import br.com.zalf.prolog.entrega.indicador.indicadores.acumulado.IndicadorAcumulado;
import br.com.zalf.prolog.gente.contracheque.Contracheque;
import br.com.zalf.prolog.gente.contracheque.ItemContracheque;
import br.com.zalf.prolog.gente.contracheque.ItemImportContracheque;
import br.com.zalf.prolog.webservice.DatabaseConnection;
import br.com.zalf.prolog.webservice.entrega.indicador.IndicadorDaoImpl;
import br.com.zalf.prolog.webservice.entrega.produtividade.ProdutividadeDaoImpl;
import br.com.zalf.prolog.webservice.util.L;

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
            Collections.sort(itens, new CustomComparator());
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

    /**
     * Busca apenas os itens que compõe o premio, estes itens são indicados na tabela pre_contracheque_premissas
     * @param cpf cpf do colaborador
     * @param codUnidade cod unidade ao qual o colaborado pertence
     * @param ano ano da busca
     * @param mes mes da busca
     * @param conn uma connection
     * @param stmt um statement
     * @param rSet um resultset
     * @return uma lista com os itens
     * @throws SQLException caso não seja possivel realizar a busca
     */
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

    /**
     * Método que calcula o premio, usando os valores de HE + DSR e produtividade
     * @param valorProdutividade valor da produtividade no período selecionado
     * @param itensPremio lista com os itens HE e DSR (quando houver)
     * @return
     */
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

    public boolean insertOrUpdateItemImportContracheque(List<ItemImportContracheque> itens, int ano, int mes, Long codUnidade)throws SQLException{
        Connection conn = null;
        try{
            conn = getConnection();
            for(ItemImportContracheque item : itens){
                if(updateItemImportContracheque(item, ano, mes, codUnidade)){
                    L.d(TAG, "Atualizado o item:" + item.toString());
                }else{
                    insertItemImportContracheque(item, ano, mes, conn, codUnidade);
                }
            }
        }finally {
            closeConnection(conn, null, null);
        }
        return true;
    }

    public boolean updateItemImportContracheque(ItemImportContracheque item, int ano, int mes, Long codUnidade) throws SQLException{
        PreparedStatement stmt = null;
        Connection conn = null;
        try{
            conn = getConnection();
            stmt = conn.prepareStatement("UPDATE PRE_CONTRACHEQUE SET DESCRICAO = ?, SUB_DESCRICAO = ?, VALOR = ?" +
                    " WHERE ANO_REFERENCIA = ? AND MES_REFERENCIA = ? AND CPF_COLABORADOR = ? AND COD_UNIDADE = ? AND CODIGO_ITEM = ?");
            stmt.setString(1, item.getDescrição());
            stmt.setString(2, item.getSubDescrição());
            stmt.setDouble(3, item.getValor());
            stmt.setInt(4, ano);
            stmt.setInt(5, mes);
            stmt.setLong(6, item.getCpf());
            stmt.setLong(7, codUnidade);
            stmt.setLong(8, item.getCodigo());
            int count = stmt.executeUpdate();
            if(count == 0){
                return false;
            }
        }finally {
            closeConnection(null, stmt, null);
        }
        return true;
    }

        public boolean deleteItemImportContracheque(ItemImportContracheque item, int ano, int mes, Long codUnidade) throws SQLException{
        Connection conn = null;
        PreparedStatement stmt = null;
        try{
            conn = getConnection();
            stmt = conn.prepareStatement("DELETE FROM PRE_CONTRACHEQUE WHERE ANO_REFERENCIA = ? AND MES_REFERENCIA = ? AND " +
                    " CPF_COLABORADOR = ? AND COD_UNIDADE = ? AND CODIGO_ITEM = ?");
            stmt.setInt(1, ano);
            stmt.setInt(2, mes);
            stmt.setLong(3, item.getCpf());
            stmt.setLong(4, codUnidade);
            stmt.setLong(5, item.getCodigo());
            int count = stmt.executeUpdate();
            if(count == 0){
                return false;
            }
        }finally {
            closeConnection(conn, stmt, null);
        }
        return true;
    }

    private boolean insertItemImportContracheque(ItemImportContracheque item, int ano, int mes, Connection conn, Long codUnidade) throws SQLException{
        PreparedStatement stmt = null;
        try{
            stmt = conn.prepareStatement("INSERT INTO PRE_CONTRACHEQUE VALUES (?,?,?,?,?,?,?,?)");
            stmt.setLong(1, codUnidade);
            stmt.setLong(2, item.getCpf());
            stmt.setInt(3, mes);
            stmt.setInt(4, ano);
            stmt.setLong(5, item.getCodigo());
            stmt.setString(6, item.getDescrição());
            stmt.setString(7, item.getSubDescrição());
            stmt.setDouble(8, item.getValor());
            int count = stmt.executeUpdate();
            if(count == 0){
                throw new SQLException("Erro ao inserir o item: " + item.toString());
            }
        }finally {
            closeConnection(null, stmt, null);
        }
        return true;
    }

    private class CustomComparator implements Comparator<ItemContracheque> {

		/**
		 * Compara primeiro pela pontuação e depois pela devolução em NF, evitando empates
		 */
		@Override
		public int compare(ItemContracheque o1, ItemContracheque o2) {
			Integer valor1 = Double.compare(o2.getValor(), o1.getValor());
				return valor1;
		}
	}

    /**
     * Método que busca os itens importados previamente
     * @param codUnidade código da unidade
     * @param ano da competancia
     * @param mes da competencia
     * @param cpf especifico a ser buscado, parâmetro opcional
     * @return
     * @throws SQLException
     */
	public List<ItemImportContracheque> getItemImportContracheque (Long codUnidade, int ano, int mes, String cpf) throws SQLException{
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rSet = null;
        List<ItemImportContracheque> itens = new ArrayList<>();
        try{
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
            while(rSet.next()){
                itens.add(createItemImportContracheque(rSet));
            }
        }finally {
            closeConnection(conn, stmt, rSet);
        }
        return itens;
    }

    private ItemImportContracheque createItemImportContracheque(ResultSet rSet) throws SQLException{
        ItemImportContracheque item = new ItemImportContracheque();
        item.setCodigo(rSet.getLong("CODIGO_ITEM"));
        item.setDescrição(rSet.getString("DESCRICAO"));
        item.setSubDescrição(rSet.getString("SUB_DESCRICAO"));
        item.setValor(rSet.getDouble("VALOR"));
        item.setCpf(rSet.getLong("CPF_COLABORADOR"));
        item.setNome(rSet.getString("NOME"));
        // a query de busca faz um left join com a tebela colaborador, o que pode ocasinar
        // em colaboradores não cadastrados, essa verificação é feita e é setado o nome
        // no objeto, permitindo ao cliente visulizar o problema
        if(item.getNome() == null){
            item.setNome("Não cadastrado");
        }
        return item;
    }
}
