package br.com.zalf.prolog.webservice.frota.pneu.afericao;

import br.com.zalf.prolog.webservice.DatabaseConnection;
import br.com.zalf.prolog.webservice.Injection;
import br.com.zalf.prolog.webservice.colaborador.model.Colaborador;
import br.com.zalf.prolog.webservice.commons.util.DateUtils;
import br.com.zalf.prolog.webservice.commons.util.LogDatabase;
import br.com.zalf.prolog.webservice.commons.util.PostgresUtil;
import br.com.zalf.prolog.webservice.frota.pneu.afericao.model.*;
import br.com.zalf.prolog.webservice.frota.pneu.pneu.PneuDao;
import br.com.zalf.prolog.webservice.frota.pneu.pneu.model.Pneu;
import br.com.zalf.prolog.webservice.frota.pneu.pneu.model.Restricao;
import br.com.zalf.prolog.webservice.frota.pneu.servico.model.Servico;
import br.com.zalf.prolog.webservice.frota.veiculo.VeiculoDao;
import br.com.zalf.prolog.webservice.frota.veiculo.model.Veiculo;

import java.sql.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class AfericaoDaoImpl extends DatabaseConnection implements AfericaoDao {

    private static final String TAG = AfericaoDaoImpl.class.getSimpleName();

    public AfericaoDaoImpl() {

    }

    @Override
    public boolean insert(Afericao afericao, Long codUnidade) throws SQLException {
        LogDatabase.insertLog(afericao);
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rSet = null;
        VeiculoDao veiculoDao = Injection.provideVeiculoDao();
        try {
            conn = getConnection();
            conn.setAutoCommit(false);
            stmt = conn.prepareStatement("INSERT INTO AFERICAO(DATA_HORA, PLACA_VEICULO, CPF_AFERIDOR, KM_VEICULO, "
                    + "TEMPO_REALIZACAO, TIPO_AFERICAO) "
                    + "VALUES (?, ?, ?, ?, ?, ?) RETURNING CODIGO");
            stmt.setTimestamp(1, DateUtils.toTimestamp(afericao.getDataHora()));
            stmt.setString(2, afericao.getVeiculo().getPlaca());
            stmt.setLong(3, afericao.getColaborador().getCpf());
            stmt.setLong(4, afericao.getKmMomentoAfericao());
            stmt.setLong(5, afericao.getTempoRealizacaoAfericaoInMillis());
            stmt.setString(6, afericao.getTipoAfericao().asString());
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

    @Override
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
        VeiculoDao veiculoDao = Injection.provideVeiculoDao();
        final NovaAfericao novaAfericao = new NovaAfericao();
        final Veiculo veiculo = veiculoDao.getVeiculoByPlaca(placa, true);
        final List<Pneu> estepes = veiculo.getEstepes();
        veiculo.removeEstepes();
        novaAfericao.setVeiculo(veiculo);
        novaAfericao.setEstepesVeiculo(estepes);
        novaAfericao.setRestricao(getRestricoesByPlaca(placa));
        return novaAfericao;
    }

    @Override
    public Restricao getRestricaoByCodUnidade(Long codUnidade) throws SQLException {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rSet = null;
        try {
            conn = getConnection();
            stmt = conn.prepareStatement("SELECT ER.SULCO_MINIMO_DESCARTE, ER.SULCO_MINIMO_RECAPAGEM, ER.TOLERANCIA_CALIBRAGEM, ER.TOLERANCIA_INSPECAO, "
                    + "ER.PERIODO_AFERICAO_SULCO, ER.PERIODO_AFERICAO_PRESSAO "
                    + "FROM UNIDADE U JOIN "
                    + "EMPRESA E ON E.CODIGO = U.COD_EMPRESA "
                    + "JOIN EMPRESA_RESTRICAO_PNEU ER ON ER.COD_EMPRESA = E.CODIGO AND U.CODIGO = ER.COD_UNIDADE "
                    + "WHERE U.CODIGO = ?");
            stmt.setLong(1, codUnidade);
            rSet = stmt.executeQuery();
            if (rSet.next()) {
                return createRestricao(rSet);
            } else {
                throw new SQLException("Dados de restrição não encontrados para a unidade: " + codUnidade);
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
                    + "ER.PERIODO_AFERICAO_SULCO, ER.PERIODO_AFERICAO_PRESSAO "
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
    public CronogramaAfericao getCronogramaAfericao(Long codUnidade) throws SQLException {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rSet = null;
        final CronogramaAfericao cronogramaAfericao = new CronogramaAfericao();
        ModeloPlacasAfericao modelo = new ModeloPlacasAfericao();
        final List<ModeloPlacasAfericao> modelos = new ArrayList<>();
        List<ModeloPlacasAfericao.PlacaAfericao> placas = new ArrayList<>();
        try {
            // coalesce - trabalha semenlhante ao IF, verifica se o valor é null.
            conn = getConnection();
            stmt = conn.prepareStatement("SELECT V.placa,\n" +
                    " M.nome,\n" +
                    "    coalesce(INTERVALO_PRESSAO.INTERVALO, -1)::INTEGER as INTERVALO_PRESSAO,\n" +
                    "    coalesce(INTERVALO_SULCO.INTERVALO, -1)::INTEGER as INTERVALO_SULCO,\n" +
                    "    coalesce(numero_pneus.total, 0)::INTEGER AS PNEUS_APLICADOS\n" +
                    "FROM VEICULO V JOIN MODELO_VEICULO M ON M.CODIGO = V.COD_MODELO\n" +
                    "LEFT JOIN\n" +
                    "    (SELECT PLACA_VEICULO AS PLACA_INTERVALO, EXTRACT(DAYS FROM now() - MAX(DATA_HORA)) AS INTERVALO FROM AFERICAO\n" +
                    "        WHERE tipo_afericao = ? OR tipo_afericao = ?\n" +
                    "        GROUP BY PLACA_VEICULO) AS INTERVALO_PRESSAO ON INTERVALO_PRESSAO.PLACA_INTERVALO = V.PLACA\n" +
                    "LEFT JOIN\n" +
                    "    (SELECT PLACA_VEICULO AS PLACA_INTERVALO,  EXTRACT(DAYS FROM now() - MAX(DATA_HORA)) AS INTERVALO FROM AFERICAO\n" +
                    "        WHERE tipo_afericao = ? OR tipo_afericao = ?\n" +
                    "        GROUP BY PLACA_VEICULO) AS INTERVALO_SULCO ON INTERVALO_SULCO.PLACA_INTERVALO = V.PLACA\n" +
                    "LEFT JOIN\n" +
                    "    (SELECT vp.placa as placa_pneus, count(vp.cod_pneu) as total\n" +
                    "        FROM veiculo_pneu vp\n" +
                    "        WHERE cod_unidade = ?\n" +
                    "        GROUP BY 1) as numero_pneus on placa_pneus = v.placa\n" +
                    "WHERE V.STATUS_ATIVO = TRUE AND V.COD_UNIDADE = ?\n" +
                    "ORDER BY M.NOME DESC, INTERVALO_PRESSAO DESC, INTERVALO_SULCO DESC;");

            // Seta para calcular informações de pressão.
            stmt.setString(1, TipoAfericao.PRESSAO.asString());
            stmt.setString(2, TipoAfericao.SULCO_PRESSAO.asString());

            // Seta para calcular informações de sulco.
            stmt.setString(3, TipoAfericao.SULCO.asString());
            stmt.setString(4, TipoAfericao.SULCO_PRESSAO.asString());
            stmt.setLong(5, codUnidade);
            stmt.setLong(6, codUnidade);
            rSet = stmt.executeQuery();
            while (rSet.next()) {
                if (placas.size() == 0) {
                    // Primeiro resultado do resultset.
                    modelo.setNomeModelo(rSet.getString("NOME"));
                } else {
                    if (!modelo.getNomeModelo().equals(rSet.getString("NOME"))) {
                        // Modelo diferente.
                        modelo.setPlacasAfericao(placas);
                        modelos.add(modelo);
                        placas = new ArrayList<>();
                        modelo = new ModeloPlacasAfericao();
                        modelo.setNomeModelo(rSet.getString("NOME"));
                    }
                }
                placas.add(createPlacaAfericao(rSet));
            }
            modelo.setPlacasAfericao(placas);
            modelos.add(modelo);

            // Finaliza criação do Cronograma.
            final Restricao restricao = getRestricaoByCodUnidade(codUnidade);
            cronogramaAfericao.setMetaAfericaoPressao(restricao.getPeriodoDiasAfericaoPressao());
            cronogramaAfericao.setMetaAfericaoSulco(restricao.getPeriodoDiasAfericaoSulco());
            cronogramaAfericao.setModelosPlacasAfericao(modelos);
            calcularQuatidadeSulcosPressaoOk(cronogramaAfericao);
            calcularTotalVeiculos(cronogramaAfericao);
        } finally {
            closeConnection(conn, stmt, rSet);
        }

        return cronogramaAfericao;
    }

    @Override
    public List<Afericao> getAfericoes(Long codUnidade,
                                       String codTipoVeiculo,
                                       String placaVeiculo,
                                       long dataInicial,
                                       long dataFinal,
                                       int limit,
                                       long offset) throws SQLException {
        final List<Afericao> afericoes = new ArrayList<>();
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rSet = null;
        try {
            conn = getConnection();
            stmt = conn.prepareStatement("SELECT A.KM_VEICULO, A.CODIGO AS COD_AFERICAO, A.DATA_HORA, "
                    + "A.PLACA_VEICULO, A.TIPO_AFERICAO, C.CPF, C.NOME, A.TEMPO_REALIZACAO  "
                    + "FROM AFERICAO A "
                    + "JOIN VEICULO V ON V.PLACA = A.PLACA_VEICULO "
                    + "JOIN COLABORADOR C ON C.CPF = A.CPF_AFERIDOR "
                    + "WHERE V.COD_UNIDADE = ? "
                    + "AND V.COD_TIPO::TEXT LIKE ? "
                    + "AND V.PLACA LIKE ? "
                    + "AND A.DATA_HORA BETWEEN ? AND ? "
                    + "ORDER BY A.DATA_HORA DESC "
                    + "LIMIT ? OFFSET ?;");
            stmt.setLong(1, codUnidade);
            stmt.setString(2, codTipoVeiculo);
            stmt.setString(3, placaVeiculo);
            stmt.setDate(4, new java.sql.Date(dataInicial));
            stmt.setDate(5, new java.sql.Date(dataFinal));
            stmt.setInt(6, limit);
            stmt.setLong(7, offset);
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
    public Afericao getByCod(Long codUnidade, Long codAfericao) throws SQLException {
        Connection conn = null;
        ResultSet rSet = null;
        PreparedStatement stmt = null;
        Afericao afericao = null;
        final VeiculoDao veiculoDao = Injection.provideVeiculoDao();
        final List<Pneu> pneus = new ArrayList<>();
        final PneuDao pneuDao = Injection.providePneuDao();
        try {
            conn = getConnection();
            stmt = conn.prepareStatement("SELECT A.KM_VEICULO, A.CODIGO as COD_AFERICAO, A.DATA_HORA, "
                    + "A.PLACA_VEICULO, A.KM_VEICULO, A.TEMPO_REALIZACAO, A.TIPO_AFERICAO, C.CPF, C.NOME, AV.COD_AFERICAO, "
                    + "AV.ALTURA_SULCO_CENTRAL_INTERNO, AV.ALTURA_SULCO_CENTRAL_EXTERNO, AV.ALTURA_SULCO_EXTERNO, "
                    + "AV.ALTURA_SULCO_INTERNO, AV.PSI::INT AS PRESSAO_ATUAL, AV.POSICAO, P.CODIGO, "
                    + "MP.CODIGO AS COD_MARCA, MP.NOME AS MARCA, MO.CODIGO AS COD_MODELO, MO.NOME AS MODELO, "
                    + "MO.QT_SULCOS AS QT_SULCOS_MODELO, DP.ALTURA, DP.LARGURA, DP.ARO, DP.CODIGO AS COD_DIMENSAO, "
                    + "P.PRESSAO_RECOMENDADA, P.ALTURA_SULCOS_NOVOS, P.STATUS, P.VIDA_ATUAL, P.VIDA_TOTAL, P.DOT, "
                    + "MB.codigo AS COD_MODELO_BANDA, MB.nome AS NOME_MODELO_BANDA, MB.qt_sulcos as QT_SULCOS_BANDA, "
                    + "MAB.codigo AS COD_MARCA_BANDA, MAB.nome AS NOME_MARCA_BANDA "
                    + "FROM AFERICAO A JOIN AFERICAO_VALORES AV ON A.CODIGO = AV.COD_AFERICAO "
                    + "JOIN pneu_ordem po on av.posicao = po.posicao_prolog "
                    + "JOIN PNEU P ON P.CODIGO = AV.COD_PNEU AND P.COD_UNIDADE = AV.COD_UNIDADE "
                    + "JOIN MODELO_PNEU MO ON MO.CODIGO = P.COD_MODELO "
                    + "JOIN MARCA_PNEU MP ON MP.CODIGO = MO.COD_MARCA "
                    + "JOIN DIMENSAO_PNEU DP ON DP.CODIGO = P.COD_DIMENSAO "
                    + "JOIN UNIDADE U ON U.CODIGO = P.cod_unidade "
                    + "LEFT JOIN modelo_banda MB ON MB.codigo = P.cod_modelo_banda AND MB.cod_empresa = U.cod_empresa "
                    + "LEFT JOIN marca_banda MAB ON MAB.codigo = MB.cod_marca AND MAB.cod_empresa = MB.cod_empresa "
                    + "JOIN COLABORADOR C ON C.CPF = A.CPF_AFERIDOR "
                    + "WHERE AV.COD_AFERICAO = ? AND AV.COD_UNIDADE = ? "
                    + "ORDER BY po.ordem_exibicao ASC");
            stmt.setLong(1, codAfericao);
            stmt.setLong(2, codUnidade);
            rSet = stmt.executeQuery();

            if (rSet.next()) {
                afericao = createAfericaoResumida(rSet);
                Pneu pneu = pneuDao.createPneu(rSet);
                pneu.setPosicao(rSet.getInt("POSICAO"));
                pneus.add(pneu);
                final Veiculo veiculo =
                        veiculoDao.getVeiculoByPlaca(rSet.getString("PLACA_VEICULO"), false);
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

    @Override
    @Deprecated
    public List<Afericao> getAfericoesByCodUnidadeByPlaca(List<String> codUnidades,
                                                          List<String> placas,
                                                          int limit,
                                                          long offset) throws SQLException {
        final List<Afericao> afericoes = new ArrayList<>();
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rSet = null;
        try {
            conn = getConnection();
            stmt = conn.prepareStatement("SELECT A.KM_VEICULO, A.CODIGO AS COD_AFERICAO, A.DATA_HORA, "
                    + "A.PLACA_VEICULO, A.TIPO_AFERICAO, C.CPF, C.NOME, A.TEMPO_REALIZACAO  "
                    + "FROM AFERICAO A JOIN VEICULO V ON V.PLACA = A.PLACA_VEICULO "
                    + "JOIN COLABORADOR C ON C.CPF = A.CPF_AFERIDOR "
                    + "WHERE V.COD_UNIDADE::TEXT LIKE ANY (ARRAY[?]) AND V.PLACA LIKE ANY (ARRAY[?]) "
                    + "ORDER BY A.DATA_HORA DESC "
                    + "LIMIT ? OFFSET ?");
            stmt.setArray(1, PostgresUtil.ListToArray(conn, codUnidades));
            stmt.setArray(2, PostgresUtil.ListToArray(conn, placas));
            stmt.setInt(3, limit);
            stmt.setLong(4, offset);
            rSet = stmt.executeQuery();
            while (rSet.next()) {
                afericoes.add(createAfericaoResumida(rSet));
            }
        } finally {
            closeConnection(conn, stmt, rSet);
        }
        return afericoes;
    }

    private void calcularTotalVeiculos(CronogramaAfericao cronogramaAfericao) {
        int totalVeiculos = 0;
        for (final ModeloPlacasAfericao modelo : cronogramaAfericao.getModelosPlacasAfericao()) {
            modelo.setTotalVeiculosModelo(modelo.getPlacasAfericao().size());
            totalVeiculos += modelo.getPlacasAfericao().size();
        }
        cronogramaAfericao.setTotalVeiculos(totalVeiculos);
    }

    private void calcularQuatidadeSulcosPressaoOk(CronogramaAfericao cronogramaAfericao) {
        int qtdSulcosOk = 0;
        int qtdPressaoOk = 0;
        int qtdSulcosPressaook = 0;

        int qtdModeloSulcosOk = 0;
        int qtdModeloPressaoOk = 0;
        int qtdModeloSulcosPressaoOk = 0;

        final int metaAfericaoSulco = cronogramaAfericao.getMetaAfericaoSulco();
        final int metaAfericaoPressao = cronogramaAfericao.getMetaAfericaoPressao();

        final List<ModeloPlacasAfericao> modelos = cronogramaAfericao.getModelosPlacasAfericao();
        for (final ModeloPlacasAfericao modelo : modelos) {
            for (final ModeloPlacasAfericao.PlacaAfericao placaAfericao : modelo.getPlacasAfericao()) {
                if (isAfericaoSulcoOk(placaAfericao, metaAfericaoSulco)) {
                    qtdSulcosOk++;
                    qtdModeloSulcosOk++;
                }
                if (isAfericaoPressaoOk(placaAfericao, metaAfericaoPressao)) {
                    qtdPressaoOk++;
                    qtdModeloPressaoOk++;
                }
                if (isAfericaoSulcoOk(placaAfericao, metaAfericaoSulco)
                        && isAfericaoPressaoOk(placaAfericao, metaAfericaoPressao)) {
                    qtdSulcosPressaook++;
                    qtdModeloSulcosPressaoOk++;
                }
            }
            // Devemos setar em cada modelo a quantidade de Sulco/Pressao.
            modelo.setQtdModeloSulcoOk(qtdModeloSulcosOk);
            modelo.setQtdModeloPressaoOk(qtdModeloPressaoOk);
            modelo.setQtdModeloSulcoPressaoOk(qtdModeloSulcosPressaoOk);
            qtdModeloSulcosOk = 0;
            qtdModeloPressaoOk = 0;
            qtdModeloSulcosPressaoOk = 0;
        }
        // Devemos setar a quatidade total de sulcos/pressões no cronograma.
        cronogramaAfericao.setTotalSulcosOk(qtdSulcosOk);
        cronogramaAfericao.setTotalPressaoOk(qtdPressaoOk);
        cronogramaAfericao.setTotalSulcoPressaoOk(qtdSulcosPressaook);
    }

    private boolean isAfericaoPressaoOk(ModeloPlacasAfericao.PlacaAfericao placaAfericao, int metaAfericaoPressao) {
        return placaAfericao.getIntervaloUltimaAfericaoPressao() <= metaAfericaoPressao
                && placaAfericao.getIntervaloUltimaAfericaoPressao() != ModeloPlacasAfericao.PlacaAfericao.INTERVALO_INVALIDO;
    }

    private boolean isAfericaoSulcoOk(ModeloPlacasAfericao.PlacaAfericao placaAfericao, int metaAfericaoSulco) {
        return placaAfericao.getIntervaloUltimaAfericaoSulco() <= metaAfericaoSulco
                && placaAfericao.getIntervaloUltimaAfericaoSulco() != ModeloPlacasAfericao.PlacaAfericao.INTERVALO_INVALIDO;
    }

    private ModeloPlacasAfericao.PlacaAfericao createPlacaAfericao(ResultSet rSet) throws SQLException {
        final ModeloPlacasAfericao.PlacaAfericao placa = new ModeloPlacasAfericao.PlacaAfericao();
        placa.setPlaca(rSet.getString("PLACA"));
        placa.setIntervaloUltimaAfericaoSulco(rSet.getInt("INTERVALO_SULCO"));
        placa.setIntervaloUltimaAfericaoPressao(rSet.getInt("INTERVALO_PRESSAO"));
        placa.setQuantidadePneus(rSet.getInt("PNEUS_APLICADOS"));
        return placa;
    }

    private void insertValores(Afericao afericao, Long codUnidade, Connection conn) throws SQLException {
        PreparedStatement stmt;
        PneuDao pneuDao = Injection.providePneuDao();
        stmt = conn.prepareStatement("INSERT INTO AFERICAO_VALORES "
                + "(COD_AFERICAO, COD_PNEU, COD_UNIDADE, PSI, ALTURA_SULCO_CENTRAL_INTERNO, ALTURA_SULCO_CENTRAL_EXTERNO,ALTURA_SULCO_EXTERNO, " +
                "ALTURA_SULCO_INTERNO, POSICAO, VIDA_MOMENTO_AFERICAO) VALUES "
                + "(?, ?, ?, ?, ?, ?, ?, ?, ?, ?)");
        for (Pneu pneu : afericao.getVeiculo().getListPneus()) {
            stmt.setLong(1, afericao.getCodigo());
            stmt.setString(2, pneu.getCodigo());
            stmt.setLong(3, codUnidade);

            switch (afericao.getTipoAfericao()) {
                case SULCO_PRESSAO:
                    stmt.setDouble(4, pneu.getPressaoAtual());
                    stmt.setDouble(5, pneu.getSulcosAtuais().getCentralInterno());
                    stmt.setDouble(6, pneu.getSulcosAtuais().getCentralExterno());
                    stmt.setDouble(7, pneu.getSulcosAtuais().getExterno());
                    stmt.setDouble(8, pneu.getSulcosAtuais().getInterno());
                    break;
                case SULCO:
                    stmt.setNull(4, Types.REAL);
                    stmt.setDouble(5, pneu.getSulcosAtuais().getCentralInterno());
                    stmt.setDouble(6, pneu.getSulcosAtuais().getCentralExterno());
                    stmt.setDouble(7, pneu.getSulcosAtuais().getExterno());
                    stmt.setDouble(8, pneu.getSulcosAtuais().getInterno());
                    break;
                case PRESSAO:
                    stmt.setDouble(4, pneu.getPressaoAtual());
                    stmt.setNull(5, Types.REAL);
                    stmt.setNull(6, Types.REAL);
                    stmt.setNull(7, Types.REAL);
                    stmt.setNull(8, Types.REAL);
                    break;
            }
            stmt.setInt(9, pneu.getPosicao());
            stmt.setInt(10, pneu.getVidaAtual());
            // Atualiza as informações de Sulco atual e calibragem atual na tabela Pneu do BD.
            pneuDao.updateMedicoes(pneu, codUnidade, conn);
            stmt.executeUpdate();
            final Restricao restricao = getRestricaoByCodUnidade(codUnidade);
            final List<String> listServicosACadastrar = getServicosACadastrar(pneu, codUnidade, restricao);
            if (listServicosACadastrar.size() > 0) {
                // Verifica se o pneu tem alguma anomalia e deve ser inserido na base de serviços.
                insertOrUpdateServico(pneu, afericao.getCodigo(), codUnidade, conn, listServicosACadastrar);
            }
        }
    }

    private List<String> getServicosACadastrar(Pneu pneu, Long codUnidade, Restricao restricao) throws SQLException {
        final List<String> servicos = new ArrayList<>();

        // Verifica se o pneu foi marcado como "com problema" na hora de aferir a pressão.
        if (pneu.getProblemas() != null && pneu.getProblemas().contains(Pneu.Problema.PRESSAO_INDISPONIVEL)) {
            servicos.add(Servico.TIPO_INSPECAO);
        }

        // Caso não tenha sido problema, verifica se está apto a ser inspeção.
        else if (pneu.getPressaoAtual() <= (pneu.getPressaoCorreta() * (1 - restricao.getToleranciaInspecao()))) {
            servicos.add(Servico.TIPO_INSPECAO);
        }

        // Caso não entre em inspeção, verifica se é uma calibragem.
        else if (pneu.getPressaoAtual() <= (pneu.getPressaoCorreta() * (1 - restricao.getToleranciaCalibragem())) ||
                pneu.getPressaoAtual() >= (pneu.getPressaoCorreta() * (1 + restricao.getToleranciaCalibragem()))) {
            servicos.add(Servico.TIPO_CALIBRAGEM);
        }

        // Nessa parte verifica os sulcos, verificando primeiro se esta na ultima vida.
        if (pneu.getVidaAtual() == pneu.getVidasTotal()) {//verifica se o pneu esta na ultima vida, o que reduz o limite de mm
            if (pneu.getSulcosAtuais().getCentralInterno() <= restricao.getSulcoMinimoDescarte()) {// sulco atual é inferior ao minimo para descarte
                servicos.add(Servico.TIPO_MOVIMENTACAO);                // insere a movimentação na lista de serviços pendentes
            }
        } else {
            if (pneu.getSulcosAtuais().getCentralInterno() <= restricao.getSulcoMinimoRecape()) {// sulco atual é inferior ao minimo para recapar
                servicos.add(Servico.TIPO_MOVIMENTACAO);                // insere a movimentação na lista de serviços pendentes
            }
        }
        return servicos;
    }

    private List<String> getServicosCadastradosByPneu(String codPneu, Long codUnidade) throws SQLException {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rSet = null;
        final List<String> listServico = new ArrayList<>();
        try {
            conn = getConnection();
            stmt = conn.prepareStatement("SELECT TIPO_SERVICO, COUNT(TIPO_SERVICO) "
                    + "FROM AFERICAO_MANUTENCAO WHERE COD_PNEU = ? AND COD_UNIDADE = ? AND DATA_HORA_RESOLUCAO IS NULL "
                    + "GROUP BY TIPO_SERVICO "
                    + "ORDER BY TIPO_SERVICO");
            stmt.setString(1, codPneu);
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
            stmt.setString(4, pneu.getCodPneuProblema()); // codigo do pneu instalado no caminhão
            stmt.setString(5, pneu.getCodigo()); // codigo que esta no bd (errado)
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
        restricao.setPeriodoDiasAfericaoSulco(rSet.getInt("PERIODO_AFERICAO_SULCO"));
        restricao.setPeriodoDiasAfericaoPressao(rSet.getInt("PERIODO_AFERICAO_PRESSAO"));
        return restricao;
    }

    private void insertOrUpdateServico(Pneu pneu, long codAfericao, Long codUnidade, Connection conn, List<String> servicosPendentes) throws SQLException {

        List<String> servicosCadastrados = getServicosCadastradosByPneu(pneu.getCodigo(), codUnidade);

        for (String servicoPendente : servicosPendentes) {
            // Se o pneu ja tem uma calibragem cadastrada e é gerada uma inspeção posteriormente, convertemos a antiga
            // calibragem para uma inspeção.
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
        stmt.setString(2, pneu.getCodigo());
        stmt.setLong(3, codUnidade);
        stmt.setString(4, tipoServico);
        if (pneu.getProblemas() != null && pneu.getProblemas().contains(Pneu.Problema.NUMERO_INCORRETO)) {
            stmt.setString(5, pneu.getCodPneuProblema());
        } else {
            stmt.setNull(5, Types.VARCHAR);
        }
        stmt.executeUpdate();
    }

    private void incrementaQtApontamentos(Pneu pneu, Long codUnidade, String tipoServico, Connection conn) throws SQLException {

        PreparedStatement stmt = null;
        stmt = conn.prepareStatement(" UPDATE AFERICAO_MANUTENCAO SET QT_APONTAMENTOS = "
                + "(SELECT QT_APONTAMENTOS FROM AFERICAO_MANUTENCAO WHERE COD_PNEU = ? AND COD_UNIDADE = ? AND TIPO_SERVICO = ? AND DATA_HORA_RESOLUCAO IS NULL) + 1 "
                + "WHERE COD_PNEU = ? AND COD_UNIDADE = ? AND TIPO_SERVICO = ? AND DATA_HORA_RESOLUCAO IS NULL");
        stmt.setString(1, pneu.getCodigo());
        stmt.setLong(2, codUnidade);
        stmt.setString(3, tipoServico);
        stmt.setString(4, pneu.getCodigo());
        stmt.setLong(5, codUnidade);
        stmt.setString(6, tipoServico);
        stmt.executeUpdate();
    }

    /**
     * Método usado para trocar um serviço cadastrado como calibragem para inspeção.
     *
     */
    private void calibragemToInspecao(Pneu pneu, Long codUnidade, Connection conn) throws SQLException {

        PreparedStatement stmt = null;
        stmt = conn.prepareStatement("UPDATE AFERICAO_MANUTENCAO SET QT_APONTAMENTOS = "
                + "(SELECT QT_APONTAMENTOS FROM AFERICAO_MANUTENCAO WHERE COD_PNEU = ? AND COD_UNIDADE = ? AND TIPO_SERVICO = ? AND DATA_HORA_RESOLUCAO IS NULL) + 1, TIPO_SERVICO = ? "
                + "WHERE COD_PNEU = ? AND COD_UNIDADE = ? AND TIPO_SERVICO = ? AND DATA_HORA_RESOLUCAO IS NULL");
        stmt.setString(1, pneu.getCodigo());
        stmt.setLong(2, codUnidade);
        stmt.setString(3, Servico.TIPO_CALIBRAGEM);
        stmt.setString(4, Servico.TIPO_INSPECAO);
        stmt.setString(5, pneu.getCodigo());
        stmt.setLong(6, codUnidade);
        stmt.setString(7, Servico.TIPO_CALIBRAGEM);
        stmt.executeUpdate();
    }

    private Afericao createAfericaoResumida(ResultSet rSet) throws SQLException {
        final Afericao afericao = new Afericao();
        afericao.setCodigo(rSet.getLong("COD_AFERICAO"));
        afericao.setDataHora(rSet.getTimestamp("DATA_HORA"));
        afericao.setKmMomentoAfericao(rSet.getLong("KM_VEICULO"));
        afericao.setTipoAfericao(TipoAfericao.fromString(rSet.getString("TIPO_AFERICAO")));
        afericao.setTempoRealizacaoAfericaoInMillis(rSet.getLong("TEMPO_REALIZACAO"));

        // Veículo no qual aferição foi realizada.
        final Veiculo veiculo = new Veiculo();
        veiculo.setPlaca(rSet.getString("PLACA_VEICULO"));
        afericao.setVeiculo(veiculo);

        // Colaborador que realizou a aferição.
        final Colaborador colaborador = new Colaborador();
        colaborador.setCpf(rSet.getLong("CPF"));
        colaborador.setNome(rSet.getString("NOME"));
        afericao.setColaborador(colaborador);
        return afericao;
    }
}