package br.com.zalf.prolog.webservice.frota.pneu.afericao;

import br.com.zalf.prolog.webservice.commons.colaborador.Colaborador;
import br.com.zalf.prolog.webservice.commons.util.DateUtils;
import br.com.zalf.prolog.webservice.commons.veiculo.Veiculo;
import br.com.zalf.prolog.webservice.frota.pneu.pneu.Pneu;
import br.com.zalf.prolog.webservice.frota.pneu.pneu.Restricao;
import br.com.zalf.prolog.webservice.frota.pneu.servico.Servico;
import br.com.zalf.prolog.webservice.DatabaseConnection;
import br.com.zalf.prolog.webservice.frota.veiculo.VeiculoDao;
import br.com.zalf.prolog.webservice.frota.veiculo.VeiculoDaoImpl;
import br.com.zalf.prolog.webservice.frota.pneu.pneu.PneuDao;
import br.com.zalf.prolog.webservice.frota.pneu.pneu.PneuDaoImpl;
import br.com.zalf.prolog.webservice.util.LogDatabase;
import br.com.zalf.prolog.webservice.util.PostgresUtil;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class AfericaoDaoImpl extends DatabaseConnection implements AfericaoDao {

    private static final String TAG = AfericaoDaoImpl.class.getSimpleName();

    @Override
    public boolean insert(Afericao afericao, Long codUnidade) throws SQLException {
        LogDatabase.insertLog(afericao);
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rSet = null;
        VeiculoDao veiculoDao = new VeiculoDaoImpl();
        try {
            conn = getConnection();
            conn.setAutoCommit(false);
            stmt = conn.prepareStatement("INSERT INTO AFERICAO(DATA_HORA, PLACA_VEICULO, CPF_AFERIDOR, KM_VEICULO, TEMPO_REALIZACAO) "
                    + "VALUES (?, ?, ?, ?, ?) RETURNING CODIGO");
            stmt.setTimestamp(1, DateUtils.toTimestamp(afericao.getDataHora()));
            stmt.setString(2, afericao.getVeiculo().getPlaca());
            stmt.setLong(3, afericao.getColaborador().getCpf());
            stmt.setLong(4, afericao.getKmMomentoAfericao());
            stmt.setLong(5, afericao.getTempoRealizacaoAfericaoInMillis());
            rSet = stmt.executeQuery();
            if (rSet.next()) {
                afericao.setCodigo(rSet.getLong("CODIGO"));
                insertValores(afericao, codUnidade, conn);
                veiculoDao.updateKmByPlaca(afericao.getVeiculo().getPlaca(), afericao.getKmMomentoAfericao(), conn);
            }
            conn.commit();
        } catch (SQLException e) {
            e.printStackTrace();
            conn.rollback();
            return false;
        } finally {
            closeConnection(conn, stmt, rSet);
        }

        return true;
    }

    public boolean update(Afericao afericao) throws SQLException {
        Connection conn = null;
        PreparedStatement stmt = null;
        try {
            conn = getConnection();
            stmt = conn.prepareStatement("UPDATE afericao SET km_veiculo = ? WHERE codigo = ?");
            stmt.setLong(1, afericao.getVeiculo().getKmAtual());
            stmt.setLong(2, afericao.getCodigo());
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
    public NovaAfericao getNovaAfericao(String placa) throws SQLException {
        VeiculoDao veiculoDao = new VeiculoDaoImpl();
        NovaAfericao afericaoHolder = new NovaAfericao();
        afericaoHolder.setVeiculo(veiculoDao.getVeiculoByPlaca(placa, true));
        if (afericaoHolder.getVeiculo().getPlaca() != null) {
            afericaoHolder.setRestricao(getRestricoesByPlaca(placa));
            return afericaoHolder;
        }
        return new NovaAfericao();
    }

    @Override
    public Restricao getRestricoesByCodUnidade(Long codUnidade) throws SQLException {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rSet = null;
        try {
            conn = getConnection();
            stmt = conn.prepareStatement("SELECT ER.SULCO_MINIMO_DESCARTE, ER.SULCO_MINIMO_RECAPAGEM, ER.TOLERANCIA_CALIBRAGEM, ER.TOLERANCIA_INSPECAO, "
                    + "ER.PERIODO_AFERICAO "
                    + "FROM UNIDADE U JOIN "
                    + "EMPRESA E ON E.CODIGO = U.COD_EMPRESA "
                    + "JOIN EMPRESA_RESTRICAO_PNEU ER ON ER.COD_EMPRESA = E.CODIGO AND U.CODIGO = ER.COD_UNIDADE "
                    + "WHERE U.CODIGO = ?");
            stmt.setLong(1, codUnidade);
            rSet = stmt.executeQuery();
            if (rSet.next()) {
                return createRestricao(rSet);
            } else {
                throw new SQLException("Erro ao buscar os dados de restrição");
            }
        } finally {
            closeConnection(conn, stmt, rSet);
        }
    }

    @Override
    public Restricao getRestricoesByPlaca(String placa) throws SQLException {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rSet = null;
        try {
            conn = getConnection();
            stmt = conn.prepareStatement("SELECT ER.SULCO_MINIMO_DESCARTE, ER.SULCO_MINIMO_RECAPAGEM,ER.TOLERANCIA_INSPECAO, ER.TOLERANCIA_CALIBRAGEM, "
                    + "ER.PERIODO_AFERICAO "
                    + "FROM VEICULO V JOIN UNIDADE U ON U.CODIGO = V.COD_UNIDADE "
                    + "JOIN EMPRESA E ON E.CODIGO = U.COD_EMPRESA "
                    + "JOIN EMPRESA_RESTRICAO_PNEU ER ON ER.COD_EMPRESA = E.CODIGO AND ER.cod_unidade = U.codigo "
                    + "WHERE V.PLACA = ?");
            stmt.setString(1, placa);
            rSet = stmt.executeQuery();
            if (rSet.next() && rSet.isLast()) {
                return createRestricao(rSet);
            } else {
                throw new SQLException("Erro ao buscar os dados de restrição");
            }
        } finally {
            closeConnection(conn, stmt, rSet);
        }
    }

    @Override
    public SelecaoPlacaAfericao getSelecaoPlacaAfericao(Long codUnidade) throws SQLException {

        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rSet = null;
        SelecaoPlacaAfericao selecaoPlacaAfericao = new SelecaoPlacaAfericao();
        PlacaModeloHolder holder = new PlacaModeloHolder(); //possui a lista de placaStatus
        List<PlacaModeloHolder> listModelo = new ArrayList<>(); // possui a lista de modelos
        List<PlacaModeloHolder.PlacaStatus> listPlacasMesmoModelo = new ArrayList<>(); //lista das placas de um mesmo modelo
        try {
            //caolesce - trabalha semenlhante ao IF, verifica se o valor é null
            conn = getConnection();
            stmt = conn.prepareStatement("	SELECT V.PLACA,\n" +
                    "  M.NOME,\n" +
                    "  coalesce(INTERVALO.INTERVALO, -1)::INTEGER as INTERVALO,\n" +
                    "  coalesce(numero_pneus.total, 0)::INTEGER AS PNEUS_APLICADOS\n" +
                    "FROM VEICULO V JOIN MODELO_VEICULO M ON M.CODIGO = V.COD_MODELO\n" +
                    "LEFT JOIN (SELECT PLACA_VEICULO AS PLACA_INTERVALO,  EXTRACT(DAYS FROM ? -  MAX(DATA_HORA)) AS INTERVALO\n" +
                    "          FROM AFERICAO \n" +
                    "          GROUP BY PLACA_VEICULO) AS INTERVALO ON PLACA_INTERVALO = V.PLACA\n" +
                    "LEFT JOIN\n" +
                    "        (SELECT vp.placa as placa_pneus, count(vp.cod_pneu) as total\n" +
                    "        FROM veiculo_pneu vp\n" +
                    "        WHERE cod_unidade = ?\n" +
                    "        GROUP BY 1) as numero_pneus on placa_pneus = v.placa\n" +
                    "WHERE V.STATUS_ATIVO = TRUE AND V.COD_UNIDADE = ?\n" +
                    "ORDER BY M.NOME, INTERVALO DESC;");
            stmt.setTimestamp(1, DateUtils.toTimestamp(new Date(System.currentTimeMillis())));
            stmt.setLong(2, codUnidade);
            stmt.setLong(3, codUnidade);
            rSet = stmt.executeQuery();
            while (rSet.next()) {
                if (listPlacasMesmoModelo.size() == 0) {//primeiro resultado do resultset
                    holder.setModelo(rSet.getString("NOME"));
                    listPlacasMesmoModelo.add(createPlacaStatus(rSet));
                } else {
                    if (holder.getModelo().equals(rSet.getString("NOME"))) {// caso o resultado seja do mesmo modelo do anterior
                        listPlacasMesmoModelo.add(createPlacaStatus(rSet));
                    } else { // modelo diferente
                        holder.setPlacaStatus(listPlacasMesmoModelo);
                        listModelo.add(holder);
                        listPlacasMesmoModelo = new ArrayList<>();
                        holder = new PlacaModeloHolder();
                        holder.setModelo(rSet.getString("NOME"));
                        listPlacasMesmoModelo.add(createPlacaStatus(rSet));
                    }
                }
            }
            holder.setPlacaStatus(listPlacasMesmoModelo);
            listModelo.add(holder);
            selecaoPlacaAfericao.setPlacas(listModelo);
            selecaoPlacaAfericao.setMeta(getRestricoesByCodUnidade(codUnidade).getPeriodoDiasAfericao());
        } finally {
            closeConnection(conn, stmt, rSet);
        }
        return selecaoPlacaAfericao;
    }

    private PlacaModeloHolder.PlacaStatus createPlacaStatus(ResultSet rSet) throws SQLException {
        PlacaModeloHolder.PlacaStatus placa = new PlacaModeloHolder.PlacaStatus();
        placa.placa = rSet.getString("PLACA");
        placa.intervaloUltimaAfericao = rSet.getInt("INTERVALO");
        placa.quantidadePneus = rSet.getInt("PNEUS_APLICADOS");
        return placa;
    }

    /**
     * Busca uma lista com informações reduzidas sobre as aferições, usado para exibir uma lista resumida dos dados (data, nome, tempo, placa)
     *
     * @param codUnidades lista com os codigos das unidades a serem buscadas as aferições
     * @param placas
     * @param limit
     * @param offset
     * @return
     * @throws SQLException
     */
    @Override
    public List<Afericao> getAfericoesByCodUnidadeByPlaca(List<String> codUnidades, List<String> placas, long limit, long offset) throws SQLException {
        //LIKE ANY (ARRAY[?])
        List<Afericao> afericoes = new ArrayList<>();
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rSet = null;
        try {
            conn = getConnection();
            stmt = conn.prepareStatement("SELECT A.KM_VEICULO, A.CODIGO AS COD_AFERICAO, A.DATA_HORA, A.PLACA_VEICULO, C.CPF, C.NOME, A.TEMPO_REALIZACAO  "
                    + "FROM AFERICAO A JOIN VEICULO V ON V.PLACA = A.PLACA_VEICULO "
                    + "JOIN COLABORADOR C ON C.CPF = A.CPF_AFERIDOR "
                    + "WHERE V.COD_UNIDADE::TEXT LIKE ANY (ARRAY[?]) AND V.PLACA LIKE ANY (ARRAY[?]) "
                    + "ORDER BY A.DATA_HORA DESC "
                    + "LIMIT ? OFFSET ?");
            stmt.setArray(1, PostgresUtil.ListToArray(conn, codUnidades));
            stmt.setArray(2, PostgresUtil.ListToArray(conn, placas));
            stmt.setLong(3, limit);
            stmt.setLong(4, offset);
            System.out.println(stmt.toString());
            rSet = stmt.executeQuery();
            while (rSet.next()) {
                afericoes.add(createAfericaoResumida(rSet));
            }
        } finally {
            closeConnection(conn, stmt, rSet);
        }
        return afericoes;
    }

    @Override
    public Afericao getByCod(Long codAfericao, Long codUnidade) throws SQLException {
        Connection conn = null;
        ResultSet rSet = null;
        PreparedStatement stmt = null;
        Afericao afericao = new Afericao();
        Veiculo veiculo = new Veiculo();
        VeiculoDaoImpl veiculoDao = new VeiculoDaoImpl();
        List<Pneu> pneus = new ArrayList<>();
        PneuDaoImpl pneuDao = new PneuDaoImpl();
        try {
            conn = getConnection();
            stmt = conn.prepareStatement("SELECT A.KM_VEICULO, A.CODIGO as COD_AFERICAO, A.DATA_HORA, A.PLACA_VEICULO, A.KM_VEICULO, A.TEMPO_REALIZACAO, C.CPF, C.NOME, AV.COD_AFERICAO, AV.ALTURA_SULCO_CENTRAL, AV.ALTURA_SULCO_EXTERNO, AV.ALTURA_SULCO_INTERNO, \n" +
                    "AV.PSI::INT AS PRESSAO_ATUAL, AV.POSICAO, P.CODIGO, MP.CODIGO AS COD_MARCA, MP.NOME AS MARCA, MO.CODIGO AS COD_MODELO, MO.NOME AS MODELO,\n" +
                    "DP.ALTURA, DP.LARGURA, DP.ARO, DP.CODIGO AS COD_DIMENSAO, P.PRESSAO_RECOMENDADA, P.ALTURA_SULCOS_NOVOS, P.STATUS, P.VIDA_ATUAL, P.VIDA_TOTAL,\n" +
                    "MB.codigo AS COD_MODELO_BANDA, MB.nome AS NOME_MODELO_BANDA, MAB.codigo AS COD_MARCA_BANDA, MAB.nome AS NOME_MARCA_BANDA\n" +
                    "FROM AFERICAO A JOIN AFERICAO_VALORES AV ON A.CODIGO = AV.COD_AFERICAO\n" +
                    "JOIN pneu_ordem po on av.posicao = po.posicao_prolog\n" +
                    "JOIN PNEU P ON P.CODIGO = AV.COD_PNEU AND P.COD_UNIDADE = AV.COD_UNIDADE\n" +
                    "JOIN MODELO_PNEU MO ON MO.CODIGO = P.COD_MODELO JOIN MARCA_PNEU MP ON MP.CODIGO = MO.COD_MARCA\n" +
                    "JOIN DIMENSAO_PNEU DP ON DP.CODIGO = P.COD_DIMENSAO\n" +
                    "JOIN UNIDADE U ON U.CODIGO = P.cod_unidade\n" +
                    "LEFT JOIN modelo_banda MB ON MB.codigo = P.cod_modelo_banda AND MB.cod_empresa = U.cod_empresa\n" +
                    "LEFT JOIN marca_banda MAB ON MAB.codigo = MB.cod_marca AND MAB.cod_empresa = MB.cod_empresa\n" +
                    "JOIN COLABORADOR C ON C.CPF = A.CPF_AFERIDOR\n" +
                    "WHERE AV.COD_AFERICAO = ? AND AV.COD_UNIDADE = ?\n" +
                    "ORDER BY po.ordem_exibicao ASC");
            stmt.setLong(1, codAfericao);
            stmt.setLong(2, codUnidade);
            rSet = stmt.executeQuery();

            if (rSet.next()) {
                afericao = createAfericaoResumida(rSet);
                Pneu pneu = pneuDao.createPneu(rSet);
                pneu.setPosicao(rSet.getInt("POSICAO"));
                pneus.add(pneu);
                veiculo = veiculoDao.getVeiculoByPlaca(rSet.getString("PLACA_VEICULO"), false);
                while (rSet.next()) {
                    pneu = pneuDao.createPneu(rSet);
                    pneu.setPosicao(rSet.getInt("POSICAO"));
                    pneus.add(pneu);
                }
                veiculo.setListPneus(pneus);
                afericao.setVeiculo(veiculo);
            }
        } finally {
            closeConnection(conn, stmt, rSet);
        }
        return afericao;
    }

    private void insertValores(Afericao afericao, Long codUnidade, Connection conn) throws SQLException {

        PreparedStatement stmt = null;
        PneuDao pneuDao = new PneuDaoImpl();

        stmt = conn.prepareStatement("INSERT INTO AFERICAO_VALORES "
                + "(COD_AFERICAO, COD_PNEU, COD_UNIDADE, PSI, ALTURA_SULCO_CENTRAL,ALTURA_SULCO_EXTERNO, " +
                "ALTURA_SULCO_INTERNO, POSICAO, VIDA_MOMENTO_AFERICAO) VALUES "
                + "(?, ?, ?, ?, ?, ?, ?, ?, ?)");
        for (Pneu pneu : afericao.getVeiculo().getListPneus()) {
            stmt.setLong(1, afericao.getCodigo());
            stmt.setLong(2, pneu.getCodigo());
            stmt.setLong(3, codUnidade);
            stmt.setDouble(4, pneu.getPressaoAtual());
            stmt.setDouble(5, pneu.getSulcoAtual().getCentral());
            stmt.setDouble(6, pneu.getSulcoAtual().getExterno());
            stmt.setDouble(7, pneu.getSulcoAtual().getInterno());
            stmt.setInt(8, pneu.getPosicao());
            stmt.setInt(9, pneu.getVidaAtual());
            //Atualiza as informações de Sulco atual e calibragem atual na tabela Pneu do BD
            pneuDao.updateMedicoes(pneu, codUnidade, conn);
            stmt.executeUpdate();
            Restricao restricao = getRestricoesByCodUnidade(codUnidade);
            List<String> listServicosACadastrar = getServicosACadastrar(pneu, codUnidade, restricao);
            if (listServicosACadastrar.size() > 0) {// verifica se o pneu tem alguma anomalia e deve ser inserido na base de serviços
                insertOrUpdateServico(pneu, afericao.getCodigo(), codUnidade, conn, listServicosACadastrar);
            }
        }
    }

    /**
     * Cria uma lista com os serviços necessários que sejam cadastrados para o pneu verificado
     *
     * @param pneu
     * @param codUnidade
     * @return
     * @throws SQLException
     */
    private List<String> getServicosACadastrar(Pneu pneu, Long codUnidade, Restricao restricao) throws SQLException {

        List<String> servicos = new ArrayList<>();

        // verifica se o pneu foi marcado como "com problema" na hora de aferir a pressão
        if (pneu.getProblemas() != null && pneu.getProblemas().contains(Pneu.Problema.PRESSAO_INDISPONIVEL)) {
            servicos.add(Servico.TIPO_INSPECAO);
        }
        // caso não tenha sido problema, verifica se está apto a ser inspeção
        else if (pneu.getPressaoAtual() <= (pneu.getPressaoCorreta() * (1 - restricao.getToleranciaInspecao()))) {
            servicos.add(Servico.TIPO_INSPECAO);
        }
        // caso não entre em inspeção, verifica se é uma calibragem
        else if (pneu.getPressaoAtual() <= (pneu.getPressaoCorreta() * (1 - restricao.getToleranciaCalibragem())) ||
                pneu.getPressaoAtual() >= (pneu.getPressaoCorreta() * (1 + restricao.getToleranciaCalibragem()))) {
            servicos.add(Servico.TIPO_CALIBRAGEM);
        }

        // Nessa parte verifica os sulcos, verificando primeiro se esta na ultima vida.
        if (pneu.getVidaAtual() == pneu.getVidasTotal()) {//verifica se o pneu esta na ultima vida, o que reduz o limite de mm
            if (pneu.getSulcoAtual().getCentral() <= restricao.getSulcoMinimoDescarte()) {// sulco atual é inferior ao minimo para descarte
                servicos.add(Servico.TIPO_MOVIMENTACAO);                // insere a movimentação na lista de serviços pendentes
            }
        } else {
            if (pneu.getSulcoAtual().getCentral() <= restricao.getSulcoMinimoRecape()) {// sulco atual é inferior ao minimo para recapar
                servicos.add(Servico.TIPO_MOVIMENTACAO);                // insere a movimentação na lista de serviços pendentes
            }
        }
        return servicos;
    }

    /**
     * retorna uma lista com os serviços ja cadastrados para uma placa, resutados distintos, apenas serviços em aberto aparecerão
     *
     * @param codPneu
     * @param codUnidade
     * @return
     * @throws SQLException
     */
    private List<String> getServicosCadastradosByPneu(Long codPneu, Long codUnidade) throws SQLException {

        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rSet = null;
        List<String> listServico = new ArrayList<>();

        try {
            conn = getConnection();
            stmt = conn.prepareStatement("SELECT TIPO_SERVICO, COUNT(TIPO_SERVICO) "
                    + "FROM AFERICAO_MANUTENCAO WHERE COD_PNEU = ? AND COD_UNIDADE = ? AND DATA_HORA_RESOLUCAO IS NULL "
                    + "GROUP BY TIPO_SERVICO "
                    + "ORDER BY TIPO_SERVICO");

            stmt.setLong(1, codPneu);
            stmt.setLong(2, codUnidade);
            rSet = stmt.executeQuery();
            while (rSet.next()) {
                listServico.add(rSet.getString("TIPO_SERVICO"));
            }
        } finally {
            closeConnection(conn, stmt, rSet);
        }

        return listServico;
    }

    private void insertInconsistencia(Long codAfericao, String placa, Pneu pneu, Long codUnidade, Connection conn) throws SQLException {
        PreparedStatement stmt = null;
        try {
            stmt = conn.prepareStatement("INSERT INTO VEICULO_PNEU_INCONSISTENCIA(DATA_HORA, "
                    + "COD_AFERICAO, PLACA, COD_PNEU_CORRETO, COD_PNEU_INCORRETO, POSICAO, COD_UNIDADE) VALUES (?,?,?,?,?,?,?)");
            stmt.setTimestamp(1, DateUtils.toTimestamp(new Date(System.currentTimeMillis())));
            stmt.setLong(2, codAfericao);
            stmt.setString(3, placa);
            stmt.setLong(4, pneu.getCodPneuProblema()); // codigo do pneu instalado no caminhão
            stmt.setLong(5, pneu.getCodigo()); // codigo que esta no bd (errado)
            stmt.setInt(6, pneu.getPosicao());
            stmt.setLong(7, codUnidade);
            stmt.executeQuery();
        } finally {
            closeConnection(null, stmt, null);
        }
    }

    private Restricao createRestricao(ResultSet rSet) throws SQLException {
        Restricao restricao = new Restricao();
        restricao.setSulcoMinimoDescarte(rSet.getDouble("SULCO_MINIMO_DESCARTE"));
        restricao.setSulcoMinimoRecape(rSet.getDouble("SULCO_MINIMO_RECAPAGEM"));
        restricao.setToleranciaCalibragem(rSet.getDouble("TOLERANCIA_CALIBRAGEM"));
        restricao.setToleranciaInspecao(rSet.getDouble("TOLERANCIA_INSPECAO"));
        restricao.setPeriodoDiasAfericao(rSet.getInt("PERIODO_AFERICAO"));
        return restricao;
    }

    private void insertOrUpdateServico(Pneu pneu, long codAfericao, Long codUnidade, Connection conn, List<String> servicosPendentes) throws SQLException {

        List<String> servicosCadastrados = getServicosCadastradosByPneu(new Long(pneu.getCodigo()), codUnidade);

        for (String servicoPendente : servicosPendentes) {
            //se o pneu ja tem uma calibragem cadastrada e é gerada uma inspeção posteriormente, convertemos a antiga calibragem para uma inspeção
            if (servicoPendente.equals(Servico.TIPO_INSPECAO) && servicosCadastrados.contains(Servico.TIPO_CALIBRAGEM)) {
                calibragemToInspecao(pneu, codUnidade, conn);
            } else {
                if (servicosCadastrados.contains(servicoPendente)) {
                    incrementaQtApontamentos(pneu, codUnidade, servicoPendente, conn);
                } else {
                    insertServico(pneu, codAfericao, servicoPendente, codUnidade, conn);
                }
            }
        }
    }

    private void insertServico(Pneu pneu, long codAfericao, String tipoServico, Long codUnidade, Connection conn) throws SQLException {
        PreparedStatement stmt = null;
        stmt = conn.prepareStatement("INSERT INTO AFERICAO_MANUTENCAO(COD_AFERICAO, COD_PNEU, COD_UNIDADE, TIPO_SERVICO, COD_PNEU_INSERIDO)"
                + " VALUES(?,?,?,?,?)");
        stmt.setLong(1, codAfericao);
        stmt.setLong(2, pneu.getCodigo());
        stmt.setLong(3, codUnidade);
        stmt.setString(4, tipoServico);
        if (pneu.getProblemas() != null && pneu.getProblemas().contains(Pneu.Problema.NUMERO_INCORRETO)) {
            stmt.setInt(5, pneu.getCodPneuProblema());
        } else {
            stmt.setNull(5, java.sql.Types.INTEGER);
        }
        stmt.executeUpdate();
    }

    private void incrementaQtApontamentos(Pneu pneu, Long codUnidade, String tipoServico, Connection conn) throws SQLException {

        PreparedStatement stmt = null;
        stmt = conn.prepareStatement(" UPDATE AFERICAO_MANUTENCAO SET QT_APONTAMENTOS = "
                + "(SELECT QT_APONTAMENTOS FROM AFERICAO_MANUTENCAO WHERE COD_PNEU = ? AND COD_UNIDADE = ? AND TIPO_SERVICO = ? AND DATA_HORA_RESOLUCAO IS NULL) + 1 "
                + "WHERE COD_PNEU = ? AND COD_UNIDADE = ? AND TIPO_SERVICO = ? AND DATA_HORA_RESOLUCAO IS NULL");
        stmt.setLong(1, pneu.getCodigo());
        stmt.setLong(2, codUnidade);
        stmt.setString(3, tipoServico);
        stmt.setLong(4, pneu.getCodigo());
        stmt.setLong(5, codUnidade);
        stmt.setString(6, tipoServico);
        stmt.executeUpdate();
    }

    /**
     * Método usado para trocar um serviço cadastrado como calibragem para inspeção
     *
     * @param pneu
     * @param codUnidade
     * @param conn
     * @throws SQLException
     */
    private void calibragemToInspecao(Pneu pneu, Long codUnidade, Connection conn) throws SQLException {

        PreparedStatement stmt = null;
        stmt = conn.prepareStatement("UPDATE AFERICAO_MANUTENCAO SET QT_APONTAMENTOS = "
                + "(SELECT QT_APONTAMENTOS FROM AFERICAO_MANUTENCAO WHERE COD_PNEU = ? AND COD_UNIDADE = ? AND TIPO_SERVICO = ? AND DATA_HORA_RESOLUCAO IS NULL) + 1, TIPO_SERVICO = ? "
                + "WHERE COD_PNEU = ? AND COD_UNIDADE = ? AND TIPO_SERVICO = ? AND DATA_HORA_RESOLUCAO IS NULL");
        stmt.setLong(1, pneu.getCodigo());
        stmt.setLong(2, codUnidade);
        stmt.setString(3, Servico.TIPO_CALIBRAGEM);
        stmt.setString(4, Servico.TIPO_INSPECAO);
        stmt.setLong(5, pneu.getCodigo());
        stmt.setLong(6, codUnidade);
        stmt.setString(7, Servico.TIPO_CALIBRAGEM);
        stmt.executeUpdate();
    }

    private Afericao createAfericaoResumida(ResultSet rSet) throws SQLException {
        Afericao afericao = new Afericao();
        afericao.setCodigo(rSet.getLong("COD_AFERICAO"));
        afericao.setDataHora(rSet.getTimestamp("DATA_HORA"));
        afericao.setKmMomentoAfericao(rSet.getLong("KM_VEICULO"));
        afericao.setTempoRealizacaoAfericaoInMillis(rSet.getLong("TEMPO_REALIZACAO"));
        Veiculo veiculo = new Veiculo();
        veiculo.setPlaca(rSet.getString("PLACA_VEICULO"));
        afericao.setVeiculo(veiculo);
        Colaborador colaborador = new Colaborador();
        colaborador.setCpf(rSet.getLong("CPF"));
        colaborador.setNome(rSet.getString("NOME"));
        afericao.setColaborador(colaborador);
        return afericao;
    }
}


