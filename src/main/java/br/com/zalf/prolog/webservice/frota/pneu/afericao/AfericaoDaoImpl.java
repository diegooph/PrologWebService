package br.com.zalf.prolog.webservice.frota.pneu.afericao;

import br.com.zalf.prolog.webservice.DatabaseConnection;
import br.com.zalf.prolog.webservice.Injection;
import br.com.zalf.prolog.webservice.TimeZoneManager;
import br.com.zalf.prolog.webservice.colaborador.model.Colaborador;
import br.com.zalf.prolog.webservice.commons.util.DateUtils;
import br.com.zalf.prolog.webservice.commons.util.LogDatabase;
import br.com.zalf.prolog.webservice.commons.util.PostgresUtil;
import br.com.zalf.prolog.webservice.frota.pneu.afericao.model.*;
import br.com.zalf.prolog.webservice.frota.pneu.pneu.PneuDao;
import br.com.zalf.prolog.webservice.frota.pneu.pneu.model.Pneu;
import br.com.zalf.prolog.webservice.frota.pneu.pneu.model.Restricao;
import br.com.zalf.prolog.webservice.frota.pneu.pneu.model.Sulcos;
import br.com.zalf.prolog.webservice.frota.pneu.servico.ServicoDao;
import br.com.zalf.prolog.webservice.frota.pneu.servico.model.TipoServico;
import br.com.zalf.prolog.webservice.frota.veiculo.VeiculoDao;
import br.com.zalf.prolog.webservice.frota.veiculo.model.Veiculo;

import java.sql.*;
import java.time.LocalDateTime;
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
            stmt.setObject(1,afericao.getDataHora());
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
    public NovaAfericao getNovaAfericao(String placa, String tipoAfericao) throws SQLException {
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
                    "ORDER BY M.NOME ASC, INTERVALO_PRESSAO DESC, INTERVALO_SULCO DESC;");

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
            cronogramaAfericao.calcularQuatidadeSulcosPressaoOk(cronogramaAfericao);
            cronogramaAfericao.calcularTotalVeiculos(cronogramaAfericao);
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
                    + "AND A.DATA_HORA::DATE BETWEEN ? AND ? "
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
        try {
            conn = getConnection();
            stmt = conn.prepareStatement("SELECT " +
                    "A.KM_VEICULO, " +
                    "A.CODIGO AS COD_AFERICAO, " +
                    "A.DATA_HORA, " +
                    "A.PLACA_VEICULO, " +
                    "A.KM_VEICULO, " +
                    "A.TEMPO_REALIZACAO, " +
                    "A.TIPO_AFERICAO, " +
                    "C.CPF, " +
                    "C.NOME, " +
                    "AV.COD_AFERICAO, " +
                    "AV.ALTURA_SULCO_CENTRAL_INTERNO, " +
                    "AV.ALTURA_SULCO_CENTRAL_EXTERNO, " +
                    "AV.ALTURA_SULCO_EXTERNO, " +
                    "AV.ALTURA_SULCO_INTERNO, " +
                    "AV.PSI::INT AS PRESSAO_PNEU, " +
                    "AV.POSICAO AS POSICAO_PNEU, " +
                    "P.CODIGO AS CODIGO_PNEU, " +
                    "P.PRESSAO_RECOMENDADA " +
                    "FROM AFERICAO A " +
                    "JOIN AFERICAO_VALORES AV ON A.CODIGO = AV.COD_AFERICAO " +
                    "JOIN PNEU_ORDEM PO ON AV.POSICAO = PO.POSICAO_PROLOG " +
                    "JOIN PNEU P ON P.CODIGO = AV.COD_PNEU AND P.COD_UNIDADE = AV.COD_UNIDADE " +
                    "JOIN MODELO_PNEU MO ON MO.CODIGO = P.COD_MODELO " +
                    "JOIN MARCA_PNEU MP ON MP.CODIGO = MO.COD_MARCA " +
                    "JOIN UNIDADE U ON U.CODIGO = P.COD_UNIDADE " +
                    "JOIN COLABORADOR C ON C.CPF = A.CPF_AFERIDOR " +
                    "WHERE AV.COD_AFERICAO = ? AND AV.COD_UNIDADE = ? " +
                    "ORDER BY PO.ORDEM_EXIBICAO ASC");
            stmt.setLong(1, codAfericao);
            stmt.setLong(2, codUnidade);
            rSet = stmt.executeQuery();

            if (rSet.next()) {
                afericao = createAfericaoResumida(rSet);
                pneus.add(createPneuAfericao(rSet));
                final Veiculo veiculo =
                        veiculoDao.getVeiculoByPlaca(rSet.getString("PLACA_VEICULO"), false);
                while (rSet.next()) {
                    pneus.add(createPneuAfericao(rSet));
                }
                veiculo.setListPneus(pneus);
                afericao.setVeiculo(veiculo);
            }
        } finally {
            closeConnection(conn, stmt, rSet);
        }
        return afericao;
    }

    private Pneu createPneuAfericao(ResultSet rSet) throws SQLException {
        final Pneu pneu = new Pneu();
        pneu.setCodigo(rSet.getString("CODIGO_PNEU"));
        pneu.setPosicao(rSet.getInt("POSICAO_PNEU"));
        pneu.setPressaoCorreta(rSet.getDouble("PRESSAO_RECOMENDADA"));
        pneu.setPressaoAtual(rSet.getDouble("PRESSAO_PNEU"));

        final Sulcos sulcos = new Sulcos();
        sulcos.setInterno(rSet.getDouble("ALTURA_SULCO_INTERNO"));
        sulcos.setCentralInterno(rSet.getDouble("ALTURA_SULCO_CENTRAL_INTERNO"));
        sulcos.setCentralExterno(rSet.getDouble("ALTURA_SULCO_CENTRAL_EXTERNO"));
        sulcos.setExterno(rSet.getDouble("ALTURA_SULCO_EXTERNO"));
        pneu.setSulcosAtuais(sulcos);
        return pneu;
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
        final ServicoDao servicoDao = Injection.provideServicoDao();
        for (Pneu pneu : afericao.getVeiculo().getListPneus()) {
            stmt.setLong(1, afericao.getCodigo());
            stmt.setString(2, pneu.getCodigo());
            stmt.setLong(3, codUnidade);

            // Já aproveitamos esse switch para atualizar as medições do pneu na tabela PNEU.
            switch (afericao.getTipoAfericao()) {
                case SULCO_PRESSAO:
                    pneuDao.updateMedicoes(pneu, codUnidade, conn);
                    stmt.setDouble(4, pneu.getPressaoAtual());
                    stmt.setDouble(5, pneu.getSulcosAtuais().getCentralInterno());
                    stmt.setDouble(6, pneu.getSulcosAtuais().getCentralExterno());
                    stmt.setDouble(7, pneu.getSulcosAtuais().getExterno());
                    stmt.setDouble(8, pneu.getSulcosAtuais().getInterno());
                    break;
                case SULCO:
                    pneuDao.updateSulcos(pneu.getCodigo(), pneu.getSulcosAtuais(), codUnidade, conn);
                    stmt.setNull(4, Types.REAL);
                    stmt.setDouble(5, pneu.getSulcosAtuais().getCentralInterno());
                    stmt.setDouble(6, pneu.getSulcosAtuais().getCentralExterno());
                    stmt.setDouble(7, pneu.getSulcosAtuais().getExterno());
                    stmt.setDouble(8, pneu.getSulcosAtuais().getInterno());
                    break;
                case PRESSAO:
                    pneuDao.updatePressao(pneu.getCodigo(), pneu.getPressaoAtual(), codUnidade, conn);
                    stmt.setDouble(4, pneu.getPressaoAtual());
                    stmt.setNull(5, Types.REAL);
                    stmt.setNull(6, Types.REAL);
                    stmt.setNull(7, Types.REAL);
                    stmt.setNull(8, Types.REAL);
                    break;
            }
            stmt.setInt(9, pneu.getPosicao());
            stmt.setInt(10, pneu.getVidaAtual());
            stmt.executeUpdate();

            // Insere/atualiza os serviços que os pneus aferidos possam ter gerado.
            final Restricao restricao = getRestricaoByCodUnidade(codUnidade);
            final List<TipoServico> listServicosACadastrar = getServicosACadastrar(pneu, restricao, afericao.getTipoAfericao());
            insertOrUpdateServicos(pneu, afericao.getCodigo(), codUnidade, listServicosACadastrar, conn, servicoDao);
        }
    }

    private List<TipoServico> getServicosACadastrar(Pneu pneu, Restricao restricao, TipoAfericao tipoAfericao) throws SQLException {
        final List<TipoServico> servicos = new ArrayList<>();

        // Verifica se o pneu foi marcado como "com problema" na hora de aferir a pressão.
        if (pneu.getProblemas() != null && pneu.getProblemas().contains(Pneu.Problema.PRESSAO_INDISPONIVEL)) {
            servicos.add(TipoServico.INSPECAO);
        }

        // Caso não tenha sido problema, verifica se está apto a ser inspeção.
        else if (pneu.getPressaoAtual() <= (pneu.getPressaoCorreta() * (1 - restricao.getToleranciaInspecao()))) {
            servicos.add(TipoServico.INSPECAO);
        }

        // Caso não entre em inspeção, verifica se é uma calibragem.
        else if (pneu.getPressaoAtual() <= (pneu.getPressaoCorreta() * (1 - restricao.getToleranciaCalibragem())) ||
                pneu.getPressaoAtual() >= (pneu.getPressaoCorreta() * (1 + restricao.getToleranciaCalibragem()))) {
            servicos.add(TipoServico.CALIBRAGEM);
        }

        // Verifica se precisamos abrir serviço de movimentação.
        if (pneu.getVidaAtual() == pneu.getVidasTotal()) {
            // Se o pneu esta na ultima vida, então ele irá para descarte, por isso devemos considerar o sulco mínimo
            // para esse caso.
            if (pneu.getValorMenorSulcoAtual() <= restricao.getSulcoMinimoDescarte()) {
                servicos.add(TipoServico.MOVIMENTACAO);
            }
        } else {
            if (pneu.getValorMenorSulcoAtual() <= restricao.getSulcoMinimoRecape()) {
                servicos.add(TipoServico.MOVIMENTACAO);
            }
        }

        if (!servicos.isEmpty()) {
            // Serviços devem ser abertos levando-se em conta o tipo da aferição:
            // Uma aferição de SULCO_PRESSAO pode abrir qualquer tipo de serviço.
            // Uma aferição de SULCO pode abrir apenas serviço de movimentação.
            // Uma aferição de PRESSAO pode abrir serviço de calibragem e de inspeção.
            // Para facilitar o código e não poluir a criação dos serviços, é mais simples deixar criar qualquer tipo
            // de serviço e apenas remover depois de acordo com o tipo da aferição.
            switch (tipoAfericao) {
                case SULCO:
                    servicos.removeIf(s -> !s.equals(TipoServico.MOVIMENTACAO));
                    break;
                case PRESSAO:
                    servicos.removeIf(s -> s.equals(TipoServico.MOVIMENTACAO));
                    break;
            }
        }

        return servicos;
    }

    private Restricao createRestricao(ResultSet rSet) throws SQLException {
        final Restricao restricao = new Restricao();
        restricao.setSulcoMinimoDescarte(rSet.getDouble("SULCO_MINIMO_DESCARTE"));
        restricao.setSulcoMinimoRecape(rSet.getDouble("SULCO_MINIMO_RECAPAGEM"));
        restricao.setToleranciaCalibragem(rSet.getDouble("TOLERANCIA_CALIBRAGEM"));
        restricao.setToleranciaInspecao(rSet.getDouble("TOLERANCIA_INSPECAO"));
        restricao.setPeriodoDiasAfericaoSulco(rSet.getInt("PERIODO_AFERICAO_SULCO"));
        restricao.setPeriodoDiasAfericaoPressao(rSet.getInt("PERIODO_AFERICAO_PRESSAO"));
        return restricao;
    }

    private void insertOrUpdateServicos(Pneu pneu, long codAfericao, Long codUnidade, List<TipoServico> servicosPendentes,
                                        Connection conn, ServicoDao servicoDao) throws SQLException {
        // Se não houver nenhum serviço para inserir/atualizar podemos retornar e poupar uma consulta ao banco.
        if (servicosPendentes.isEmpty())
            return;

        final List<TipoServico> servicosCadastrados = servicoDao.getServicosCadastradosByPneu(pneu.getCodigo(), codUnidade);

        for (TipoServico servicoPendente : servicosPendentes) {
            // Se o pneu ja tem uma calibragem cadastrada e é gerada uma inspeção posteriormente, convertemos a antiga
            // calibragem para uma inspeção.
            if (servicoPendente.equals(TipoServico.INSPECAO) && servicosCadastrados.contains(TipoServico.CALIBRAGEM)) {
                servicoDao.calibragemToInspecao(pneu.getCodigo(), codUnidade, conn);
            } else {
                if (servicosCadastrados.contains(servicoPendente)) {
                    servicoDao.incrementaQtdApontamentosServico(pneu.getCodigo(), codUnidade, servicoPendente, conn);

                    // Não podemos criar um serviço de calibragem caso já exista um de inspeção aberto.
                    // Já que calibragens (se existirem) são convertidas para inspeções quando um serviço de inspeção
                    // precisa ser aberto se deixassemos esse caso passar, geraria um erro bizarro onde acabariamos
                    // com duas inspeções abertas para o mesmo pneu. :s
                } else if(!(servicosCadastrados.contains(TipoServico.INSPECAO) && servicoPendente.equals(TipoServico.CALIBRAGEM))) {
                    servicoDao.criaServico(pneu.getCodigo(), codAfericao, servicoPendente, codUnidade, conn);
                }
            }
        }
    }

    private Afericao createAfericaoResumida(ResultSet rSet) throws SQLException {
        final Afericao afericao = new Afericao();
        afericao.setCodigo(rSet.getLong("COD_AFERICAO"));
        afericao.setDataHora(rSet.getObject("DATA_HORA", LocalDateTime.class));
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

    private void insertInconsistencia(Long codAfericao, String placa, Pneu pneu, Long codUnidade, Connection conn) throws SQLException {
        PreparedStatement stmt = null;
        try {
            stmt = conn.prepareStatement("INSERT INTO VEICULO_PNEU_INCONSISTENCIA(DATA_HORA, "
                    + "COD_AFERICAO, PLACA, COD_PNEU_CORRETO, COD_PNEU_INCORRETO, POSICAO, COD_UNIDADE) VALUES (?,?,?,?,?,?,?)");
            stmt.setObject(1, TimeZoneManager.getZonedLocalDateTimeForCodUnidade(codUnidade, conn));
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
}