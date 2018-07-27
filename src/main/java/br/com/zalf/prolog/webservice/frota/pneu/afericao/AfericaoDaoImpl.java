package br.com.zalf.prolog.webservice.frota.pneu.afericao;

import br.com.zalf.prolog.webservice.Injection;
import br.com.zalf.prolog.webservice.TimeZoneManager;
import br.com.zalf.prolog.webservice.colaborador.model.Colaborador;
import br.com.zalf.prolog.webservice.commons.util.PostgresUtils;
import br.com.zalf.prolog.webservice.database.DatabaseConnection;
import br.com.zalf.prolog.webservice.frota.pneu.afericao.model.*;
import br.com.zalf.prolog.webservice.frota.pneu.pneu.PneuDao;
import br.com.zalf.prolog.webservice.frota.pneu.pneu.model.PneuComum;
import br.com.zalf.prolog.webservice.frota.pneu.pneu.model.Restricao;
import br.com.zalf.prolog.webservice.frota.pneu.pneu.model.Sulcos;
import br.com.zalf.prolog.webservice.frota.pneu.servico.ServicoDao;
import br.com.zalf.prolog.webservice.frota.pneu.servico.model.TipoServico;
import br.com.zalf.prolog.webservice.frota.veiculo.VeiculoDao;
import br.com.zalf.prolog.webservice.frota.veiculo.model.Veiculo;
import org.jetbrains.annotations.NotNull;

import java.sql.*;
import java.time.*;
import java.util.ArrayList;
import java.util.List;

public class AfericaoDaoImpl extends DatabaseConnection implements AfericaoDao {

    private static final String TAG = AfericaoDaoImpl.class.getSimpleName();

    public AfericaoDaoImpl() {

    }

    @Override
    public boolean insert(AfericaoPlaca afericaoPlaca, Long codUnidade) throws SQLException {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rSet = null;
        final VeiculoDao veiculoDao = Injection.provideVeiculoDao();
        try {
            conn = getConnection();
            conn.setAutoCommit(false);
            stmt = conn.prepareStatement("INSERT INTO AFERICAO(DATA_HORA, PLACA_VEICULO, CPF_AFERIDOR, KM_VEICULO, "
                    + "TEMPO_REALIZACAO, TIPO_AFERICAO, COD_UNIDADE) "
                    + "VALUES (?, ?, ?, ?, ?, ?, ?) RETURNING CODIGO");
            stmt.setObject(1, afericaoPlaca.getDataHora().atOffset(ZoneOffset.UTC));
            stmt.setString(2, afericaoPlaca.getVeiculo().getPlaca());
            stmt.setLong(3, afericaoPlaca.getColaborador().getCpf());
            stmt.setLong(4, afericaoPlaca.getKmMomentoAfericao());
            stmt.setLong(5, afericaoPlaca.getTempoRealizacaoAfericaoInMillis());
            stmt.setString(6, afericaoPlaca.getTipoMedicaoColetadaAfericao().asString());
            stmt.setLong(7, codUnidade);
            rSet = stmt.executeQuery();
            if (rSet.next()) {
                afericaoPlaca.setCodigo(rSet.getLong("CODIGO"));
                insertValores(afericaoPlaca, codUnidade, conn);
                veiculoDao.updateKmByPlaca(afericaoPlaca.getVeiculo().getPlaca(), afericaoPlaca.getKmMomentoAfericao(), conn);
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
    public boolean update(AfericaoPlaca afericaoPlaca) throws SQLException {
        Connection conn = null;
        PreparedStatement stmt = null;
        try {
            conn = getConnection();
            stmt = conn.prepareStatement("UPDATE afericao SET km_veiculo = ? WHERE codigo = ?");
            stmt.setLong(1, afericaoPlaca.getVeiculo().getKmAtual());
            stmt.setLong(2, afericaoPlaca.getCodigo());
            int count = stmt.executeUpdate();
            if (count == 0) {
                return false;
            }
        } finally {
            closeConnection(conn, stmt, null);
        }
        return true;
    }

    @NotNull
    @Override
    public NovaAfericaoPlaca getNovaAfericaoPlaca(@NotNull final Long codUnidade,
                                                  @NotNull final String placa,
                                                  @NotNull final String tipoAfericao) throws SQLException {
        final VeiculoDao veiculoDao = Injection.provideVeiculoDao();
        final NovaAfericaoPlaca novaAfericao = new NovaAfericaoPlaca();
        final Veiculo veiculo = veiculoDao.getVeiculoByPlaca(placa, true);
        final List<PneuComum> estepes = veiculo.getEstepes();
        novaAfericao.setEstepesVeiculo(estepes);
        novaAfericao.setVeiculo(veiculo);
        final Restricao restricao = getRestricoesByPlaca(placa);
        novaAfericao.setRestricao(restricao);
        novaAfericao.setDeveAferirEstepes(getConfiguracaTiposVeiculosAfericaoEstepe(placa).isPodeAferirEstepe());
        return novaAfericao;
    }

    @NotNull
    @Override
    public NovaAfericaoAvulsa getNovaAfericaoAvulsa(@NotNull final Long codUnidade,
                                                    @NotNull final Long codPneu,
                                                    @NotNull final TipoMedicaoColetadaAfericao tipoMedicaoColetadaAfericao) throws Throwable {
        final NovaAfericaoAvulsa novaAfericao = new NovaAfericaoAvulsa();
        final Restricao restricao = getRestricaoByCodUnidade(codUnidade);
        novaAfericao.setRestricao(restricao);
        return novaAfericao;
    }

    @Override
    public Restricao getRestricaoByCodUnidade(Long codUnidade) throws SQLException {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rSet = null;
        try {
            conn = getConnection();
            stmt = conn.prepareStatement("SELECT ER.SULCO_MINIMO_DESCARTE, ER.SULCO_MINIMO_RECAPAGEM, ER" +
                    ".TOLERANCIA_CALIBRAGEM, ER.TOLERANCIA_INSPECAO, "
                    + "ER.PERIODO_AFERICAO_SULCO, ER.PERIODO_AFERICAO_PRESSAO "
                    + "FROM UNIDADE U JOIN "
                    + "EMPRESA E ON E.CODIGO = U.COD_EMPRESA "
                    + "JOIN PNEU_RESTRICAO_UNIDADE ER ON ER.COD_EMPRESA = E.CODIGO AND U.CODIGO = ER.COD_UNIDADE "
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
            stmt = conn.prepareStatement("SELECT ER.SULCO_MINIMO_DESCARTE, ER.SULCO_MINIMO_RECAPAGEM,ER" +
                    ".TOLERANCIA_INSPECAO, ER.TOLERANCIA_CALIBRAGEM, "
                    + "ER.PERIODO_AFERICAO_SULCO, ER.PERIODO_AFERICAO_PRESSAO "
                    + "FROM VEICULO V JOIN UNIDADE U ON U.CODIGO = V.COD_UNIDADE "
                    + "JOIN EMPRESA E ON E.CODIGO = U.COD_EMPRESA "
                    + "JOIN PNEU_RESTRICAO_UNIDADE ER ON ER.COD_EMPRESA = E.CODIGO AND ER.cod_unidade = U.codigo "
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
            stmt = conn.prepareStatement("SELECT V.placa, " +
                    " M.nome, " +
                    "    coalesce(INTERVALO_PRESSAO.INTERVALO, -1)::INTEGER as INTERVALO_PRESSAO, " +
                    "    coalesce(INTERVALO_SULCO.INTERVALO, -1)::INTEGER as INTERVALO_SULCO, " +
                    "    coalesce(numero_pneus.total, 0)::INTEGER AS PNEUS_APLICADOS, " +
                    "  VCTA.STATUS_ATIVO, " +
                    "  VCTA.PODE_AFERIR_SULCO, " +
                    "  VCTA.PODE_AFERIR_PRESSAO, " +
                    "  VCTA.PODE_AFERIR_SULCO_PRESSAO, " +
                    "  VCTA.PODE_AFERIR_ESTEPE " +
                    "FROM VEICULO V " +
                    "  JOIN MODELO_VEICULO M ON M.CODIGO = V.COD_MODELO " +
                    "  JOIN VIEW_AFERICAO_CONFIGURACAO_TIPO_AFERICAO VCTA ON VCTA.COD_TIPO_VEICULO = V.COD_TIPO " +
                    "LEFT JOIN " +
                    "    (SELECT PLACA_VEICULO AS PLACA_INTERVALO, EXTRACT(DAYS FROM (?) - MAX(DATA_HORA AT TIME ZONE" +
                    " ?)) AS INTERVALO FROM AFERICAO " +
                    "        WHERE tipo_afericao = ? OR tipo_afericao = ? " +
                    "        GROUP BY PLACA_VEICULO) AS INTERVALO_PRESSAO ON INTERVALO_PRESSAO.PLACA_INTERVALO = V" +
                    ".PLACA " +
                    "LEFT JOIN " +
                    "    (SELECT PLACA_VEICULO AS PLACA_INTERVALO,  EXTRACT(DAYS FROM (?) - MAX(DATA_HORA AT TIME " +
                    "ZONE ?)) AS INTERVALO FROM AFERICAO " +
                    "        WHERE tipo_afericao = ? OR tipo_afericao = ? " +
                    "        GROUP BY PLACA_VEICULO) AS INTERVALO_SULCO ON INTERVALO_SULCO.PLACA_INTERVALO = V.PLACA " +
                    "LEFT JOIN " +
                    "    (SELECT vp.placa as placa_pneus, count(vp.cod_pneu) as total " +
                    "        FROM veiculo_pneu vp " +
                    "        WHERE cod_unidade = ? " +
                    "        GROUP BY 1) as numero_pneus on placa_pneus = v.placa " +
                    "WHERE V.STATUS_ATIVO = TRUE AND V.COD_UNIDADE = ? " +
                    "ORDER BY M.NOME ASC, INTERVALO_PRESSAO DESC, INTERVALO_SULCO DESC;");
            final ZoneId zoneId = TimeZoneManager.getZoneIdForCodUnidade(codUnidade, conn);
            // Seta para calcular informações de pressão.
            stmt.setObject(1, OffsetDateTime.now(Clock.system(zoneId)));
            stmt.setString(2, zoneId.getId());
            stmt.setString(3, TipoMedicaoColetadaAfericao.PRESSAO.asString());
            stmt.setString(4, TipoMedicaoColetadaAfericao.SULCO_PRESSAO.asString());

            // Seta para calcular informações de sulco.
            stmt.setObject(5, OffsetDateTime.now(Clock.system(zoneId)));
            stmt.setString(6, zoneId.getId());
            stmt.setString(7, TipoMedicaoColetadaAfericao.SULCO.asString());
            stmt.setString(8, TipoMedicaoColetadaAfericao.SULCO_PRESSAO.asString());
            stmt.setLong(9, codUnidade);
            stmt.setLong(10, codUnidade);
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
            cronogramaAfericao.removerPlacasNaoAferiveis(cronogramaAfericao);
            cronogramaAfericao.removerModelosSemPlacas(cronogramaAfericao);
            cronogramaAfericao.calcularQuatidadeSulcosPressaoOk(cronogramaAfericao);
            cronogramaAfericao.calcularTotalVeiculos(cronogramaAfericao);
        } finally {
            closeConnection(conn, stmt, rSet);
        }

        return cronogramaAfericao;
    }

    @Override
    public List<AfericaoPlaca> getAfericoes(Long codUnidade,
                                            String codTipoVeiculo,
                                            String placaVeiculo,
                                            long dataInicial,
                                            long dataFinal,
                                            int limit,
                                            long offset) throws SQLException {
        final List<AfericaoPlaca> afericoes = new ArrayList<>();
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rSet = null;
        try {
            conn = getConnection();
            stmt = conn.prepareStatement("SELECT " +
                    "A.KM_VEICULO, " +
                    "A.CODIGO AS COD_AFERICAO, " +
                    "A.COD_UNIDADE AS COD_UNIDADE, " +
                    "A.DATA_HORA AT TIME ZONE ? AS DATA_HORA, " +
                    "A.PLACA_VEICULO, " +
                    "A.TIPO_AFERICAO, " +
                    "C.CPF, " +
                    "C.NOME, " +
                    "A.TEMPO_REALIZACAO  "
                    + "FROM AFERICAO A "
                    + "JOIN VEICULO V ON V.PLACA = A.PLACA_VEICULO "
                    + "JOIN COLABORADOR C ON C.CPF = A.CPF_AFERIDOR "
                    + "WHERE A.COD_UNIDADE = ? "
                    + "AND V.COD_TIPO::TEXT LIKE ? "
                    + "AND V.PLACA LIKE ? "
                    + "AND A.DATA_HORA::DATE BETWEEN (? AT TIME ZONE ?) AND (? AT TIME ZONE ?) "
                    + "ORDER BY A.DATA_HORA DESC "
                    + "LIMIT ? OFFSET ?;");
            final String zoneId = TimeZoneManager.getZoneIdForCodUnidade(codUnidade, conn).getId();
            stmt.setString(1, zoneId);
            stmt.setLong(2, codUnidade);
            stmt.setString(3, codTipoVeiculo);
            stmt.setString(4, placaVeiculo);
            stmt.setDate(5, new java.sql.Date(dataInicial));
            stmt.setString(6, zoneId);
            stmt.setDate(7, new java.sql.Date(dataFinal));
            stmt.setString(8, zoneId);
            stmt.setInt(9, limit);
            stmt.setLong(10, offset);
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
    public AfericaoPlaca getByCod(Long codUnidade, Long codAfericao) throws SQLException {
        Connection conn = null;
        ResultSet rSet = null;
        PreparedStatement stmt = null;
        AfericaoPlaca afericaoPlaca = null;
        final VeiculoDao veiculoDao = Injection.provideVeiculoDao();
        final List<PneuComum> pneus = new ArrayList<>();
        try {
            conn = getConnection();
            stmt = conn.prepareStatement("SELECT " +
                    "A.KM_VEICULO, " +
                    "A.CODIGO AS COD_AFERICAO, " +
                    "A.COD_UNIDADE AS COD_UNIDADE, " +
                    "A.DATA_HORA AT TIME ZONE ? AS DATA_HORA, " +
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
                    "P.CODIGO_CLIENTE AS CODIGO_PNEU_CLIENTE, " +
                    "P.PRESSAO_RECOMENDADA " +
                    "FROM AFERICAO A " +
                    "JOIN AFERICAO_VALORES AV ON A.CODIGO = AV.COD_AFERICAO " +
                    "JOIN PNEU_ORDEM PO ON AV.POSICAO = PO.POSICAO_PROLOG " +
                    "JOIN PNEU P ON P.CODIGO = AV.COD_PNEU " +
                    "JOIN MODELO_PNEU MO ON MO.CODIGO = P.COD_MODELO " +
                    "JOIN MARCA_PNEU MP ON MP.CODIGO = MO.COD_MARCA " +
                    "JOIN COLABORADOR C ON C.CPF = A.CPF_AFERIDOR " +
                    "WHERE AV.COD_AFERICAO = ? AND AV.COD_UNIDADE = ? " +
                    "ORDER BY PO.ORDEM_EXIBICAO ASC");
            stmt.setString(1, TimeZoneManager.getZoneIdForCodUnidade(codUnidade, conn).getId());
            stmt.setLong(2, codAfericao);
            stmt.setLong(3, codUnidade);
            rSet = stmt.executeQuery();

            if (rSet.next()) {
                afericaoPlaca = createAfericaoResumida(rSet);
                pneus.add(createPneuAfericao(rSet));
                final Veiculo veiculo =
                        veiculoDao.getVeiculoByPlaca(rSet.getString("PLACA_VEICULO"), false);
                while (rSet.next()) {
                    pneus.add(createPneuAfericao(rSet));
                }
                veiculo.setListPneus(pneus);
                afericaoPlaca.setVeiculo(veiculo);
            }
        } finally {
            closeConnection(conn, stmt, rSet);
        }
        return afericaoPlaca;
    }

    @Override
    @Deprecated
    public List<AfericaoPlaca> getAfericoesByCodUnidadeByPlaca(List<String> codUnidades,
                                                               List<String> placas,
                                                               int limit,
                                                               long offset) throws SQLException {
        final List<AfericaoPlaca> afericoes = new ArrayList<>();
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rSet = null;
        try {
            conn = getConnection();
            stmt = conn.prepareStatement("SELECT " +
                    "A.KM_VEICULO, " +
                    "A.CODIGO AS COD_AFERICAO, " +
                    "A.DATA_HORA AT TIME ZONE (SELECT TIMEZONE FROM UNIDADE U WHERE U.CODIGO = V.COD_UNIDADE) AS " +
                    "DATA_HORA, " +
                    "A.PLACA_VEICULO, " +
                    "A.TIPO_AFERICAO, " +
                    "C.CPF, " +
                    "C.NOME, " +
                    "A.TEMPO_REALIZACAO  "
                    + "FROM AFERICAO A JOIN VEICULO V ON V.PLACA = A.PLACA_VEICULO "
                    + "JOIN COLABORADOR C ON C.CPF = A.CPF_AFERIDOR "
                    + "WHERE V.COD_UNIDADE::TEXT LIKE ANY (ARRAY[?]) AND V.PLACA LIKE ANY (ARRAY[?]) "
                    + "ORDER BY A.DATA_HORA DESC "
                    + "LIMIT ? OFFSET ?");
            stmt.setArray(1, PostgresUtils.ListToArray(conn, codUnidades));
            stmt.setArray(2, PostgresUtils.ListToArray(conn, placas));
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

    private ConfiguracaoTipoVeiculoAfericao getConfiguracaTiposVeiculosAfericaoEstepe(final String placa)
            throws SQLException {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rSet = null;
        try {
            conn = getConnection();
            stmt = conn.prepareStatement("SELECT " +
                    "  VACTA.STATUS_ATIVO, " +
                    "  VACTA.PODE_AFERIR_SULCO, " +
                    "  VACTA.PODE_AFERIR_PRESSAO, " +
                    "  VACTA.PODE_AFERIR_SULCO_PRESSAO," +
                    "  VACTA.PODE_AFERIR_ESTEPE " +
                    "FROM VIEW_AFERICAO_CONFIGURACAO_TIPO_AFERICAO AS VACTA " +
                    "WHERE COD_UNIDADE = (SELECT COD_UNIDADE " +
                    "                     FROM VEICULO " +
                    "                     WHERE PLACA = ?) " +
                    "      AND COD_TIPO_VEICULO = (SELECT COD_TIPO " +
                    "                              FROM VEICULO " +
                    "                              WHERE PLACA = ?);");
            stmt.setString(1, placa);
            stmt.setString(2, placa);
            rSet = stmt.executeQuery();
            if (rSet.next()) {
                return createConfiguracaoTipoAfericao(rSet);
            } else {
                throw new SQLException("Erro ao buscar as configurações de aferição para a placa: " + placa);
            }
        } finally {
            closeConnection(conn, stmt, rSet);
        }
    }

    private ConfiguracaoTipoVeiculoAfericao createConfiguracaoTipoAfericao(ResultSet rSet) throws SQLException {
        final ConfiguracaoTipoVeiculoAfericao config = new ConfiguracaoTipoVeiculoAfericao();
        config.setPodeAferirSulco(rSet.getBoolean("PODE_AFERIR_SULCO"));
        config.setPodeAferirPressao(rSet.getBoolean("PODE_AFERIR_PRESSAO"));
        config.setPodeAferirSulcoPressao(rSet.getBoolean("PODE_AFERIR_SULCO_PRESSAO"));
        config.setPodeAferirEstepe(rSet.getBoolean("PODE_AFERIR_ESTEPE"));
        return config;
    }

    private PneuComum createPneuAfericao(ResultSet rSet) throws SQLException {
        final PneuComum pneu = new PneuComum();
        pneu.setCodigo(rSet.getLong("CODIGO_PNEU"));
        pneu.setCodigoCliente(rSet.getString("CODIGO_PNEU_CLIENTE"));
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

    private ModeloPlacasAfericao.PlacaAfericao createPlacaAfericao(ResultSet rSet) throws SQLException {
        final ModeloPlacasAfericao.PlacaAfericao placa = new ModeloPlacasAfericao.PlacaAfericao();
        placa.setPlaca(rSet.getString("PLACA"));
        placa.setIntervaloUltimaAfericaoSulco(rSet.getInt("INTERVALO_SULCO"));
        placa.setIntervaloUltimaAfericaoPressao(rSet.getInt("INTERVALO_PRESSAO"));
        placa.setQuantidadePneus(rSet.getInt("PNEUS_APLICADOS"));
        placa.setPodeAferirSulco(rSet.getBoolean("PODE_AFERIR_SULCO"));
        placa.setPodeAferirPressao(rSet.getBoolean("PODE_AFERIR_PRESSAO"));
        placa.setPodeAferirSulcoPressao(rSet.getBoolean("PODE_AFERIR_SULCO_PRESSAO"));
        placa.setPodeAferirEstepe(rSet.getBoolean("PODE_AFERIR_ESTEPE"));
        return placa;
    }

    private void insertValores(AfericaoPlaca afericaoPlaca, Long codUnidade, Connection conn) throws SQLException {
        final PneuDao pneuDao = Injection.providePneuDao();
        final PreparedStatement stmt = conn.prepareStatement("INSERT INTO AFERICAO_VALORES "
                + "(COD_AFERICAO, COD_PNEU, COD_UNIDADE, PSI, ALTURA_SULCO_CENTRAL_INTERNO, " +
                "ALTURA_SULCO_CENTRAL_EXTERNO, ALTURA_SULCO_EXTERNO, " +
                "ALTURA_SULCO_INTERNO, POSICAO, VIDA_MOMENTO_AFERICAO) VALUES "
                + "(?, ?, ?, ?, ?, ?, ?, ?, ?, ?)");
        final ServicoDao servicoDao = Injection.provideServicoDao();
        for (PneuComum pneu : afericaoPlaca.getVeiculo().getListPneus()) {
            stmt.setLong(1, afericaoPlaca.getCodigo());
            stmt.setLong(2, pneu.getCodigo());
            stmt.setLong(3, codUnidade);

            // Já aproveitamos esse switch para atualizar as medições do pneu na tabela PNEU.
            switch (afericaoPlaca.getTipoMedicaoColetadaAfericao()) {
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
            final List<TipoServico> listServicosACadastrar = getServicosACadastrar(pneu, restricao, afericaoPlaca
                    .getTipoMedicaoColetadaAfericao());
            insertOrUpdateServicos(pneu, afericaoPlaca.getCodigo(), codUnidade, listServicosACadastrar, conn, servicoDao);
        }
    }

    private List<TipoServico> getServicosACadastrar(PneuComum pneu, Restricao restricao, TipoMedicaoColetadaAfericao tipoMedicaoColetadaAfericao) {
        final List<TipoServico> servicos = new ArrayList<>();

        // Verifica se o pneu foi marcado como "com problema" na hora de aferir a pressão.
        if (pneu.getProblemas() != null && pneu.getProblemas().contains(PneuComum.Problema.PRESSAO_INDISPONIVEL)) {
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
            switch (tipoMedicaoColetadaAfericao) {
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

    private void insertOrUpdateServicos(PneuComum pneu, long codAfericao, Long codUnidade, List<TipoServico>
            servicosPendentes,
                                        Connection conn, ServicoDao servicoDao) throws SQLException {
        // Se não houver nenhum serviço para inserir/atualizar podemos retornar e poupar uma consulta ao banco.
        if (servicosPendentes.isEmpty())
            return;

        final List<TipoServico> servicosCadastrados = servicoDao.getServicosCadastradosByPneu(pneu.getCodigo(),
                codUnidade);

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
                } else if (!(servicosCadastrados.contains(TipoServico.INSPECAO) && servicoPendente.equals(TipoServico
                        .CALIBRAGEM))) {
                    servicoDao.criaServico(pneu.getCodigo(), codAfericao, servicoPendente, codUnidade, conn);
                }
            }
        }
    }

    private AfericaoPlaca createAfericaoResumida(ResultSet rSet) throws SQLException {
        final AfericaoPlaca afericaoPlaca = new AfericaoPlaca();
        afericaoPlaca.setCodigo(rSet.getLong("COD_AFERICAO"));
        afericaoPlaca.setCodUnidade(rSet.getLong("COD_UNIDADE"));
        afericaoPlaca.setDataHora(rSet.getObject("DATA_HORA", LocalDateTime.class));
        afericaoPlaca.setKmMomentoAfericao(rSet.getLong("KM_VEICULO"));
        afericaoPlaca.setTipoMedicaoColetadaAfericao(TipoMedicaoColetadaAfericao.fromString(rSet.getString("TIPO_AFERICAO")));
        afericaoPlaca.setTempoRealizacaoAfericaoInMillis(rSet.getLong("TEMPO_REALIZACAO"));

        // Veículo no qual aferição foi realizada.
        final Veiculo veiculo = new Veiculo();
        veiculo.setPlaca(rSet.getString("PLACA_VEICULO"));
        afericaoPlaca.setVeiculo(veiculo);

        // Colaborador que realizou a aferição.
        final Colaborador colaborador = new Colaborador();
        colaborador.setCpf(rSet.getLong("CPF"));
        colaborador.setNome(rSet.getString("NOME"));
        afericaoPlaca.setColaborador(colaborador);
        return afericaoPlaca;
    }

    private void insertInconsistencia(Long codAfericao, String placa, PneuComum pneu, Long codUnidade, Connection
            conn) throws SQLException {
        PreparedStatement stmt = null;
        try {
            stmt = conn.prepareStatement("INSERT INTO VEICULO_PNEU_INCONSISTENCIA(DATA_HORA, "
                    + "COD_AFERICAO, PLACA, COD_PNEU_CORRETO, COD_PNEU_INCORRETO, POSICAO, COD_UNIDADE) VALUES (?,?," +
                    "?,?,?,?,?)");
            stmt.setObject(1, Instant.now().atOffset(ZoneOffset.UTC));
            stmt.setLong(2, codAfericao);
            stmt.setString(3, placa);
            stmt.setString(4, pneu.getCodPneuProblema()); // codigo do pneu instalado no caminhão
            stmt.setLong(5, pneu.getCodigo()); // codigo que esta no bd (errado)
            stmt.setInt(6, pneu.getPosicao());
            stmt.setLong(7, codUnidade);
            stmt.executeQuery();
        } finally {
            closeStatement(stmt);
        }
    }
}