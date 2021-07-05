package br.com.zalf.prolog.webservice.frota.pneu.afericao;

import br.com.zalf.prolog.webservice.Filtros;
import br.com.zalf.prolog.webservice.Injection;
import br.com.zalf.prolog.webservice.TimeZoneManager;
import br.com.zalf.prolog.webservice.commons.report.Report;
import br.com.zalf.prolog.webservice.commons.report.ReportTransformer;
import br.com.zalf.prolog.webservice.commons.util.database.PostgresUtils;
import br.com.zalf.prolog.webservice.commons.util.database.SqlType;
import br.com.zalf.prolog.webservice.commons.util.datetime.Now;
import br.com.zalf.prolog.webservice.customfields._model.CampoPersonalizadoFuncaoProlog;
import br.com.zalf.prolog.webservice.customfields._model.CampoPersonalizadoResposta;
import br.com.zalf.prolog.webservice.customfields._model.ColunaTabelaResposta;
import br.com.zalf.prolog.webservice.customfields._model.ColunaTabelaRespostaBuilder;
import br.com.zalf.prolog.webservice.database.DatabaseConnection;
import br.com.zalf.prolog.webservice.frota.pneu.PneuConverter;
import br.com.zalf.prolog.webservice.frota.pneu.PneuDao;
import br.com.zalf.prolog.webservice.frota.pneu._model.*;
import br.com.zalf.prolog.webservice.frota.pneu.afericao._model.*;
import br.com.zalf.prolog.webservice.frota.pneu.afericao.configuracao._model.FormaColetaDadosAfericaoEnum;
import br.com.zalf.prolog.webservice.frota.pneu.servico.ServicoDao;
import br.com.zalf.prolog.webservice.frota.pneu.servico._model.OrigemFechamentoAutomaticoEnum;
import br.com.zalf.prolog.webservice.frota.pneu.servico._model.TipoServico;
import br.com.zalf.prolog.webservice.frota.veiculo.VeiculoDao;
import br.com.zalf.prolog.webservice.frota.veiculo.model.Veiculo;
import br.com.zalf.prolog.webservice.gente.colaborador.model.Colaborador;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.sql.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class AfericaoDaoV2Impl extends DatabaseConnection implements AfericaoDaoV2 {

    public AfericaoDaoV2Impl() {

    }

    @NotNull
    @Override
    public Long insert(@NotNull final Connection conn,
                       @NotNull final Long codUnidade,
                       @NotNull final Afericao afericao,
                       final boolean deveAbrirServico) throws Throwable {
        return internalInsertAfericao(conn, codUnidade, afericao, deveAbrirServico);
    }

    @NotNull
    @Override
    public Long insert(@NotNull final Long codUnidade,
                       @NotNull final Afericao afericao,
                       final boolean deveAbrirServico) throws Throwable {
        Connection conn = null;
        try {
            conn = getConnection();
            conn.setAutoCommit(false);
            final Long codAfericao = internalInsertAfericao(conn, codUnidade, afericao, deveAbrirServico);
            conn.commit();
            return codAfericao;
        } catch (final Throwable e) {
            if (conn != null) {
                conn.rollback();
            }
            throw e;
        } finally {
            close(conn);
        }
    }

    @NotNull
    @Override
    public NovaAfericaoPlaca getNovaAfericaoPlaca(@NotNull final AfericaoBuscaFiltro afericaoBusca) throws Throwable {
        Connection conn = null;
        try {
            conn = getConnection();
            final NovaAfericaoPlaca novaAfericao = new NovaAfericaoPlaca();
            final Veiculo veiculo =
                    Injection.provideVeiculoDao().getVeiculoByPlaca(conn,
                                                                    afericaoBusca.getPlacaVeiculo(),
                                                                    afericaoBusca.getCodUnidade(),
                                                                    true);
            novaAfericao.setEstepesVeiculo(veiculo.getEstepes());
            novaAfericao.setVeiculo(veiculo);
            // Configurações/parametrizações necessárias para a aferição.
            final ConfiguracaoNovaAfericaoPlaca configuracao =
                    getConfiguracaoNovaAfericaoPlaca(conn, afericaoBusca.getPlacaVeiculo());
            novaAfericao.setRestricao(Restricao.createRestricaoFrom(configuracao));
            novaAfericao.setDeveAferirEstepes(configuracao.isPodeAferirEstepe());
            novaAfericao.setVariacaoAceitaSulcoMenorMilimetros(configuracao.getVariacaoAceitaSulcoMenorMilimetros());
            novaAfericao.setVariacaoAceitaSulcoMaiorMilimetros(configuracao.getVariacaoAceitaSulcoMaiorMilimetros());
            novaAfericao.setBloqueiaValoresMenores(configuracao.isBloqueiaValoresMenores());
            novaAfericao.setBloqueiaValoresMaiores(configuracao.isBloqueiaValoresMaiores());
            novaAfericao.setFormaColetaDadosSulco(configuracao.getFormaColetaDadosSulco());
            novaAfericao.setFormaColetaDadosPressao(configuracao.getFormaColetaDadosPressao());
            novaAfericao.setFormaColetaDadosSulcoPressao(configuracao.getFormaColetaDadosSulcoPressao());
            return novaAfericao;
        } finally {
            close(conn);
        }
    }

    @NotNull
    @Override
    public NovaAfericaoAvulsa getNovaAfericaoAvulsa(
            @NotNull final Long codUnidade,
            @NotNull final Long codPneu,
            @NotNull final TipoMedicaoColetadaAfericao tipoMedicaoColetadaAfericao) throws Throwable {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rSet = null;
        try {
            conn = getConnection();
            stmt = conn.prepareStatement("SELECT * FROM FUNC_AFERICAO_GET_PNEU_PARA_AFERICAO_AVULSA(?, ?);");
            stmt.setLong(1, codPneu);
            stmt.setString(2, TimeZoneManager.getZoneIdForCodUnidade(codUnidade, conn).getId());
            rSet = stmt.executeQuery();
            final NovaAfericaoAvulsa novaAfericao = new NovaAfericaoAvulsa();
            if (rSet.next()) {
                final ConfiguracaoNovaAfericao config = getConfiguracaoNovaAfericaoAvulsa(conn, codPneu);
                novaAfericao.setRestricao(Restricao.createRestricaoFrom(config));
                novaAfericao.setPneuParaAferir(createPneuAfericaoAvulsa(rSet));
                novaAfericao.setVariacaoAceitaSulcoMenorMilimetros(config.getVariacaoAceitaSulcoMenorMilimetros());
                novaAfericao.setVariacaoAceitaSulcoMaiorMilimetros(config.getVariacaoAceitaSulcoMaiorMilimetros());
                novaAfericao.setBloqueiaValoresMenores(config.isBloqueiaValoresMenores());
                novaAfericao.setBloqueiaValoresMaiores(config.isBloqueiaValoresMaiores());
            }
            return novaAfericao;
        } finally {
            close(conn, stmt, rSet);
        }
    }

    @NotNull
    @Override
    public Restricao getRestricaoByCodUnidade(@NotNull final Long codUnidade) throws Throwable {
        Connection conn = null;
        try {
            conn = getConnection();
            return getRestricaoByCodUnidade(conn, codUnidade);
        } finally {
            close(conn);
        }
    }

    @Override
    @NotNull
    public Restricao getRestricaoByCodUnidade(@NotNull final Connection conn,
                                              @NotNull final Long codUnidade) throws Throwable {
        PreparedStatement stmt = null;
        ResultSet rSet = null;
        try {
            stmt = conn.prepareStatement("SELECT * FROM FUNC_AFERICAO_GET_RESTRICAO_BY_UNIDADE(?);");
            stmt.setLong(1, codUnidade);
            rSet = stmt.executeQuery();
            if (rSet.next()) {
                return createRestricao(rSet);
            } else {
                throw new Throwable("Dados de restrição não encontrados para a unidade: " + codUnidade);
            }
        } finally {
            close(stmt, rSet);
        }
    }

    @Override
    @NotNull
    public CronogramaAfericao getCronogramaAfericao(@NotNull final List<Long> codUnidades) throws Throwable {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rSet = null;
        try {
            conn = getConnection();
            stmt = conn.prepareStatement("SELECT * FROM FUNC_AFERICAO_GET_CRONOGRAMA_AFERICOES_PLACAS(" +
                                                 "F_COD_UNIDADES := ?," +
                                                 "F_DATA_HORA_ATUAL := ?);");
            stmt.setArray(1, PostgresUtils.listToArray(conn, SqlType.BIGINT, codUnidades));
            stmt.setObject(2, Now.getLocalDateTimeUtc());
            rSet = stmt.executeQuery();

            final boolean multiUnidades = codUnidades.size() > 1;

            final CronogramaAfericao cronogramaAfericao = new CronogramaAfericao();
            ModeloPlacasAfericao modelo = new ModeloPlacasAfericao();
            final List<ModeloPlacasAfericao> modelos = new ArrayList<>();
            List<ModeloPlacasAfericao.PlacaAfericao> placas = new ArrayList<>();
            while (rSet.next()) {
                if (placas.size() == 0) {
                    // Primeiro resultado do resultset.
                    modelo.setNomeModelo(rSet.getString("NOME_MODELO"));
                } else {
                    if (!modelo.getNomeModelo().equals(rSet.getString("NOME_MODELO"))) {
                        // Modelo diferente.
                        modelo.setPlacasAfericao(placas);
                        modelos.add(modelo);
                        placas = new ArrayList<>();
                        modelo = new ModeloPlacasAfericao();
                        modelo.setNomeModelo(rSet.getString("NOME_MODELO"));
                    }
                }
                placas.add(createPlacaAfericao(rSet));
            }
            modelo.setPlacasAfericao(placas);
            modelos.add(modelo);

            // Os atributos de meta são mantidos no cronograma ainda a nível de compatibilidade com apps antigos.
            if (!multiUnidades) {
                // Finaliza criação do Cronograma.
                final Restricao restricao = getRestricaoByCodUnidade(conn, codUnidades.get(0));
                cronogramaAfericao.setMetaAfericaoPressao(restricao.getPeriodoDiasAfericaoPressao());
                cronogramaAfericao.setMetaAfericaoSulco(restricao.getPeriodoDiasAfericaoSulco());
            }
            cronogramaAfericao.setModelosPlacasAfericao(modelos);
            cronogramaAfericao.removerPlacasNaoAferiveis();
            cronogramaAfericao.removerModelosSemPlacas();
            cronogramaAfericao.calcularQuatidadeSulcosPressaoOk(true);
            cronogramaAfericao.calcularTotalVeiculos();
            return cronogramaAfericao;
        } finally {
            close(conn, stmt, rSet);
        }
    }

    @NotNull
    @Override
    public List<PneuAfericaoAvulsa> getPneusAfericaoAvulsa(@NotNull final Long codUnidade) throws Throwable {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rSet = null;
        try {
            conn = getConnection();
            stmt = conn.prepareStatement("SELECT * FROM FUNC_AFERICAO_GET_PNEUS_DISPONIVEIS_AFERICAO_AVULSA(?);");
            stmt.setLong(1, codUnidade);
            rSet = stmt.executeQuery();
            final List<PneuAfericaoAvulsa> pneus = new ArrayList<>();
            while (rSet.next()) {
                pneus.add(createPneuAfericaoAvulsa(rSet));
            }
            return pneus;
        } finally {
            close(conn, stmt, rSet);
        }
    }

    @NotNull
    @Override
    public List<AfericaoPlaca> getAfericoesPlacas(@NotNull final Long codUnidade,
                                                  @NotNull final String codTipoVeiculo,
                                                  @NotNull final String placaVeiculo,
                                                  @NotNull final LocalDate dataInicial,
                                                  @NotNull final LocalDate dataFinal,
                                                  final int limit,
                                                  final long offset) throws Throwable {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rSet = null;
        try {
            conn = getConnection();
            stmt = conn.prepareStatement("SELECT * FROM FUNC_AFERICAO_GET_AFERICOES_PLACAS_PAGINADA(?, ?, ?, ?, ?, ?," +
                                                 " ?);");
            final String zoneId = TimeZoneManager.getZoneIdForCodUnidade(codUnidade, conn).getId();
            stmt.setArray(1, PostgresUtils.listToArray(conn,
                                                                      SqlType.BIGINT,
                                                                      Collections.singletonList(codUnidade)));
            if (Filtros.isFiltroTodos(codTipoVeiculo)) {
                stmt.setNull(2, Types.BIGINT);
            } else {
                stmt.setLong(2, Long.valueOf(codTipoVeiculo));
            }
            if (Filtros.isFiltroTodos(placaVeiculo)) {
                stmt.setNull(3, Types.VARCHAR);
            } else {
                stmt.setString(3, placaVeiculo);
            }
            stmt.setObject(4, dataInicial);
            stmt.setObject(5, dataFinal);
            stmt.setInt(6, limit);
            stmt.setLong(7, offset);
            rSet = stmt.executeQuery();
            final List<AfericaoPlaca> afericoes = new ArrayList<>();
            while (rSet.next()) {
                afericoes.add(createAfericaoPlacaResumida(rSet));
            }
            return afericoes;
        } finally {
            close(conn, stmt, rSet);
        }
    }

    @NotNull
    @Override
    public List<AfericaoAvulsa> getAfericoesAvulsas(@NotNull final Long codUnidade,
                                                    @NotNull final LocalDate dataInicial,
                                                    @NotNull final LocalDate dataFinal,
                                                    final int limit,
                                                    final long offset) throws Throwable {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rSet = null;
        try {
            conn = getConnection();
            stmt = conn.prepareStatement("SELECT * FROM FUNC_AFERICAO_GET_AFERICOES_AVULSAS_PAGINADA(?, ?, ?, ?, ?);");
            stmt.setArray(1, PostgresUtils.listToArray(conn,
                                                                      SqlType.BIGINT,
                                                                     Collections.singletonList(codUnidade)));
            stmt.setObject(2, dataInicial);
            stmt.setObject(3, dataFinal);
            stmt.setInt(4, limit);
            stmt.setLong(5, offset);
            rSet = stmt.executeQuery();
            final List<AfericaoAvulsa> afericoes = new ArrayList<>();
            while (rSet.next()) {
                afericoes.add(createAfericaoAvulsaResumida(rSet));
            }
            return afericoes;
        } finally {
            close(conn, stmt, rSet);
        }
    }

    @NotNull
    @Override
    public Report getAfericoesAvulsas(@NotNull final Long codUnidade,
                                      @Nullable final Long codColaborador,
                                      @NotNull final LocalDate dataInicial,
                                      @NotNull final LocalDate dataFinal) throws Throwable {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rSet = null;
        try {
            conn = getConnection();
            if (codColaborador != null) {
                stmt = conn.prepareStatement(
                        "SELECT * FROM FUNC_RELATORIO_PNEU_AFERICOES_AVULSAS_BY_COLABORADOR(?, ?, ?, ?);");
                stmt.setLong(1, codUnidade);
                stmt.setLong(2, codColaborador);
                stmt.setObject(3, dataInicial);
                stmt.setObject(4, dataFinal);
            } else {
                stmt = conn.prepareStatement(
                        "SELECT * FROM FUNC_RELATORIO_PNEU_AFERICOES_AVULSAS(?, ?, ?);");
                final List<Long> codUnidades = new ArrayList<>();
                codUnidades.add(codUnidade);
                stmt.setArray(1, PostgresUtils.listToArray(conn, SqlType.BIGINT, codUnidades));
                stmt.setObject(2, dataInicial);
                stmt.setObject(3, dataFinal);
            }
            rSet = stmt.executeQuery();
            return ReportTransformer.createReport(rSet);
        } finally {
            close(conn, stmt, rSet);
        }
    }

    @NotNull
    @Override
    public Afericao getByCod(@NotNull final Long codUnidade, @NotNull final Long codAfericao) throws Throwable {
        Connection conn = null;
        ResultSet rSet = null;
        PreparedStatement stmt = null;
        try {
            conn = getConnection();
            stmt = conn.prepareStatement("SELECT * FROM FUNC_AFERICAO_GET_AFERICAO_BY_CODIGO(?, ?, ?);");
            stmt.setLong(1, codUnidade);
            stmt.setLong(2, codAfericao);
            stmt.setString(3, TimeZoneManager.getZoneIdForCodUnidade(codUnidade, conn).getId());
            rSet = stmt.executeQuery();
            final Afericao afericao;
            if (rSet.next()) {
                afericao = createAfericaoPlacaResumida(rSet);
                // TODO: Quando essa busca suportar também a busca de aferições avulsas, isso deverá ser refatorado.
                if (afericao instanceof AfericaoPlaca) {
                    final AfericaoPlaca afericaoPlaca = (AfericaoPlaca) afericao;
                    final List<Pneu> pneus = new ArrayList<>();
                    do {
                        pneus.add(createPneuAfericao(rSet));
                    } while (rSet.next());
                    final VeiculoDao veiculoDao = Injection.provideVeiculoDao();
                    final Veiculo veiculo = afericaoPlaca.getVeiculo();
                    veiculo.setListPneus(pneus);
                    veiculoDao.getDiagramaVeiculoByPlaca(conn, veiculo.getPlaca()).ifPresent(veiculo::setDiagrama);
                }
            } else {
                throw new SQLException("Erro ao buscar aferição de código: " + codAfericao);
            }
            return afericao;
        } finally {
            close(conn, stmt, rSet);
        }
    }

    @NotNull
    @Override
    public ConfiguracaoNovaAfericao getConfiguracaoNovaAfericao(@NotNull final String placa) throws Throwable {
        Connection conn = null;
        try {
            conn = getConnection();
            return getConfiguracaoNovaAfericaoPlaca(conn, placa);
        } finally {
            close(conn);
        }
    }

    @NotNull
    @Override
    public Restricao getRestricoesByPlaca(@NotNull final String placa) throws Throwable {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rSet = null;
        try {
            conn = getConnection();
            stmt = conn.prepareStatement("SELECT * FROM FUNC_AFERICAO_GET_RESTRICAO_BY_PLACA(?);");
            stmt.setString(1, placa);
            rSet = stmt.executeQuery();
            if (rSet.next() && rSet.isLast()) {
                return createRestricao(rSet);
            } else {
                throw new Throwable("Dados de restrição não encontrados para a placa: " + placa);
            }
        } finally {
            closeConnection(conn, stmt, rSet);
        }
    }

    @NotNull
    private Long internalInsertAfericao(@NotNull final Connection conn,
                                        @NotNull final Long codUnidade,
                                        @NotNull final Afericao afericao,
                                        final boolean deveAbrirServico) throws Throwable {
        PreparedStatement stmt = null;
        ResultSet rSet = null;
        try {
            stmt = conn.prepareStatement("SELECT * FROM FUNC_AFERICAO_INSERT_AFERICAO(" +
                    "F_COD_UNIDADE => ?," +
                    "F_DATA_HORA => ?, " +
                    "F_CPF_AFERIDOR => ?, " +
                    "F_TEMPO_REALIZACAO => ?, " +
                    "F_TIPO_MEDICAO_COLETADA => ?, " +
                    "F_TIPO_PROCESSO_COLETA => ?, " +
                    "F_FORMA_COLETA_DADOS => ?," +
                    "F_COD_VEICULO => ?, " +
                    "F_KM_VEICULO => ?) AS COD_AFERICAO;");
            stmt.setLong(1, codUnidade);
            stmt.setObject(2, afericao.getDataHora().atOffset(ZoneOffset.UTC));
            stmt.setLong(3, afericao.getColaborador().getCpf());
            stmt.setLong(4, afericao.getTempoRealizacaoAfericaoInMillis());
            stmt.setString(5, afericao.getTipoMedicaoColetadaAfericao().asString());
            stmt.setString(6, afericao.getTipoProcessoColetaAfericao().asString());
            // Os apps antigos não enviam essa informação, então pode vir nulo.
            stmt.setString(7, afericao.getFormaColetaDadosAfericao() != null
                    ? afericao.getFormaColetaDadosAfericao().toString()
                    : FormaColetaDadosAfericaoEnum.EQUIPAMENTO.toString());

            if (afericao instanceof AfericaoPlaca) {
                final AfericaoPlaca afericaoPlaca = (AfericaoPlaca) afericao;
                stmt.setLong(8, afericaoPlaca.getVeiculo().getCodigo());
                stmt.setLong(9, afericaoPlaca.getKmMomentoAfericao());
            } else {
                stmt.setNull(8, Types.BIGINT);
                stmt.setNull(9, Types.BIGINT);
            }
            Long codAfericao = null;
            rSet = stmt.executeQuery();
            if (rSet.next()) {
                codAfericao = rSet.getLong("COD_AFERICAO");
                afericao.setCodigo(codAfericao);
                insertValores(conn, codUnidade, afericao, deveAbrirServico, afericao instanceof AfericaoPlaca);
            }
            if (codAfericao != null && codAfericao != 0) {
                final List<CampoPersonalizadoResposta> respostas = afericao.getRespostasCamposPersonalizados();
                if (respostas != null && !respostas.isEmpty()) {
                    Injection.provideCampoPersonalizadoDao().salvaRespostasCamposPersonalizados(
                            conn,
                            CampoPersonalizadoFuncaoProlog.AFERICAO,
                            respostas,
                            new ColunaTabelaRespostaBuilder()
                                    .addColunaEspecifica(
                                            new ColunaTabelaResposta("cod_processo_afericao", codAfericao))
                                    .getColunas());
                }
                return codAfericao;
            } else {
                throw new IllegalStateException("Não foi possível retornar o código da aferição realizada");
            }
        } finally {
            close(stmt, rSet);
        }
    }

    @NotNull
    private ConfiguracaoNovaAfericaoPlaca getConfiguracaoNovaAfericaoPlaca(@NotNull final Connection conn,
                                                                           @NotNull final String placa)
            throws Throwable {
        PreparedStatement stmt = null;
        ResultSet rSet = null;
        try {
            stmt = conn.prepareStatement("SELECT * FROM FUNC_AFERICAO_GET_CONFIGURACOES_NOVA_AFERICAO_PLACA(?);");
            stmt.setString(1, placa);
            rSet = stmt.executeQuery();
            if (rSet.next()) {
                return createConfiguracaoNovaAfericaoPlaca(rSet);
            } else {
                throw new IllegalStateException("Dados de configurações de aferição não encontrados para a placa: "
                                                        + placa);
            }
        } finally {
            close(stmt, rSet);
        }
    }

    @NotNull
    private ConfiguracaoNovaAfericaoAvulsa getConfiguracaoNovaAfericaoAvulsa(@NotNull final Connection conn,
                                                                             @NotNull final Long codPneu)
            throws Throwable {
        PreparedStatement stmt = null;
        ResultSet rSet = null;
        try {
            stmt = conn.prepareStatement("SELECT * FROM FUNC_AFERICAO_GET_CONFIGURACOES_NOVA_AFERICAO_AVULSA(?);");
            stmt.setLong(1, codPneu);
            rSet = stmt.executeQuery();
            if (rSet.next()) {
                return createConfiguracaoNovaAfericaoAvulsa(rSet);
            } else {
                throw new IllegalStateException("Dados de configurações de aferição não encontrados para o pneu: "
                                                        + codPneu);
            }
        } finally {
            close(stmt, rSet);
        }
    }

    @NotNull
    private ConfiguracaoNovaAfericaoPlaca createConfiguracaoNovaAfericaoPlaca(
            @NotNull final ResultSet rSet) throws SQLException {
        return new ConfiguracaoNovaAfericaoPlaca(
                FormaColetaDadosAfericaoEnum.fromString(rSet.getString("FORMA_COLETA_DADOS_SULCO")),
                FormaColetaDadosAfericaoEnum.fromString(rSet.getString("FORMA_COLETA_DADOS_PRESSAO")),
                FormaColetaDadosAfericaoEnum.fromString(rSet.getString("FORMA_COLETA_DADOS_SULCO_PRESSAO")),
                rSet.getBoolean("PODE_AFERIR_ESTEPE"),
                rSet.getDouble("SULCO_MINIMO_DESCARTE"),
                rSet.getDouble("SULCO_MINIMO_RECAPAGEM"),
                rSet.getDouble("TOLERANCIA_INSPECAO"),
                rSet.getDouble("TOLERANCIA_CALIBRAGEM"),
                rSet.getInt("PERIODO_AFERICAO_SULCO"),
                rSet.getInt("PERIODO_AFERICAO_PRESSAO"),
                rSet.getDouble("VARIACAO_ACEITA_SULCO_MENOR_MILIMETROS"),
                rSet.getDouble("VARIACAO_ACEITA_SULCO_MAIOR_MILIMETROS"),
                rSet.getBoolean("VARIACOES_SULCO_DEFAULT_PROLOG"),
                rSet.getBoolean("BLOQUEAR_VALORES_MENORES"),
                rSet.getBoolean("BLOQUEAR_VALORES_MAIORES"));
    }

    @NotNull
    private ConfiguracaoNovaAfericaoAvulsa createConfiguracaoNovaAfericaoAvulsa(
            @NotNull final ResultSet rSet) throws SQLException {
        return new ConfiguracaoNovaAfericaoAvulsa(
                rSet.getDouble("SULCO_MINIMO_DESCARTE"),
                rSet.getDouble("SULCO_MINIMO_RECAPAGEM"),
                rSet.getDouble("TOLERANCIA_INSPECAO"),
                rSet.getDouble("TOLERANCIA_CALIBRAGEM"),
                rSet.getInt("PERIODO_AFERICAO_SULCO"),
                rSet.getInt("PERIODO_AFERICAO_PRESSAO"),
                rSet.getDouble("VARIACAO_ACEITA_SULCO_MENOR_MILIMETROS"),
                rSet.getDouble("VARIACAO_ACEITA_SULCO_MAIOR_MILIMETROS"),
                rSet.getBoolean("VARIACOES_SULCO_DEFAULT_PROLOG"),
                rSet.getBoolean("BLOQUEAR_VALORES_MENORES"),
                rSet.getBoolean("BLOQUEAR_VALORES_MAIORES"));
    }

    @NotNull
    private PneuAfericaoAvulsa createPneuAfericaoAvulsa(@NotNull final ResultSet rSet) throws Throwable {
        final PneuAfericaoAvulsa pneuAvulso = new PneuAfericaoAvulsa();
        pneuAvulso.setPneu(PneuConverter.createPneuCompleto(rSet, PneuTipo.PNEU_ESTOQUE, false));

        // Se já foi aferido seta todas as informações da última aferição.
        final LocalDateTime dataHoraUltimaAfericao = rSet.getObject("DATA_HORA_ULTIMA_AFERICAO", LocalDateTime.class);
        if (!rSet.wasNull()) {
            pneuAvulso.setDataHoraUltimaAfericao(dataHoraUltimaAfericao);
            pneuAvulso.setNomeColaboradorAfericao(rSet.getString("NOME_COLABORADOR_ULTIMA_AFERICAO"));
            final TipoMedicaoColetadaAfericao tipoMedicao = TipoMedicaoColetadaAfericao
                    .fromString(rSet.getString("TIPO_MEDICAO_COLETADA_ULTIMA_AFERICAO"));
            pneuAvulso.setTipoMedicaoColetadaUltimaAfericao(tipoMedicao);
            pneuAvulso.setCodigoUltimaAfericao(rSet.getLong("COD_ULTIMA_AFERICAO"));
            final TipoProcessoColetaAfericao tipoProcesso = TipoProcessoColetaAfericao
                    .fromString(rSet.getString("TIPO_PROCESSO_COLETA_ULTIMA_AFERICAO"));
            pneuAvulso.setTipoProcessoAfericao(tipoProcesso);
            pneuAvulso.setPlacaAplicadoQuandoAferido(rSet.getString("PLACA_VEICULO_ULTIMA_AFERICAO"));
            pneuAvulso.setIdentificadorFrotaAplicadoQuandoAferido(rSet.getString("IDENTIFICADOR_FROTA_ULTIMA_AFERICAO"));
        }
        return pneuAvulso;
    }

    @NotNull
    private PneuComum createPneuAfericao(@NotNull final ResultSet rSet) throws Throwable {
        final PneuComum pneu = new PneuComum();
        pneu.setCodigo(rSet.getLong("CODIGO_PNEU"));
        pneu.setCodigoCliente(rSet.getString("CODIGO_PNEU_CLIENTE"));
        pneu.setPosicao(rSet.getInt("POSICAO_PNEU"));
        pneu.setPressaoCorreta(rSet.getDouble("PRESSAO_RECOMENDADA"));
        pneu.setPressaoAtual(rSet.getDouble("PRESSAO_PNEU"));
        pneu.setVidaAtual(rSet.getInt("VIDA_PNEU_MOMENTO_AFERICAO"));
        pneu.setVidasTotal(rSet.getInt("VIDAS_TOTAL_PNEU"));

        final Sulcos sulcos = new Sulcos();
        sulcos.setInterno(rSet.getDouble("ALTURA_SULCO_INTERNO"));
        sulcos.setCentralInterno(rSet.getDouble("ALTURA_SULCO_CENTRAL_INTERNO"));
        sulcos.setCentralExterno(rSet.getDouble("ALTURA_SULCO_CENTRAL_EXTERNO"));
        sulcos.setExterno(rSet.getDouble("ALTURA_SULCO_EXTERNO"));
        pneu.setSulcosAtuais(sulcos);
        return pneu;
    }

    @NotNull
    private ModeloPlacasAfericao.PlacaAfericao createPlacaAfericao(@NotNull final ResultSet rSet) throws Throwable {
        final ModeloPlacasAfericao.PlacaAfericao placa = new ModeloPlacasAfericao.PlacaAfericao();
        placa.setPlaca(rSet.getString("PLACA"));
        placa.setCodigoVeiculo(rSet.getLong("COD_VEICULO"));
        placa.setIdentificadorFrota(rSet.getString("IDENTIFICADOR_FROTA"));
        placa.setCodUnidadePlaca(rSet.getLong("COD_UNIDADE_PLACA"));
        placa.setIntervaloUltimaAfericaoSulco(rSet.getInt("INTERVALO_SULCO"));
        placa.setIntervaloUltimaAfericaoPressao(rSet.getInt("INTERVALO_PRESSAO"));
        placa.setQuantidadePneus(rSet.getInt("PNEUS_APLICADOS"));
        placa.setFormaColetaDadosSulco(FormaColetaDadosAfericaoEnum.fromString(rSet.getString(
                "FORMA_COLETA_DADOS_SULCO")));
        placa.setFormaColetaDadosPressao(FormaColetaDadosAfericaoEnum.fromString(rSet.getString(
                "FORMA_COLETA_DADOS_PRESSAO")));
        placa.setFormaColetaDadosSulcoPressao(FormaColetaDadosAfericaoEnum.fromString(rSet.getString(
                "FORMA_COLETA_DADOS_SULCO_PRESSAO")));
        placa.setPodeAferirEstepe(rSet.getBoolean("PODE_AFERIR_ESTEPE"));
        placa.setMetaAfericaoPressao(rSet.getInt("PERIODO_AFERICAO_PRESSAO"));
        placa.setMetaAfericaoSulco(rSet.getInt("PERIODO_AFERICAO_SULCO"));
        return placa;
    }

    private void insertValores(@NotNull final Connection conn,
                               @NotNull final Long codUnidade,
                               @NotNull final Afericao afericao,
                               final boolean deveAbrirServico,
                               final boolean afericaoPlaca) throws Throwable {
        final PneuDao pneuDao = Injection.providePneuDao();
        final ServicoDao servicoDao = Injection.provideServicoDao();
        final Restricao restricao = getRestricaoByCodUnidade(conn, codUnidade);

        final PreparedStatement stmt = conn.prepareStatement("INSERT INTO AFERICAO_VALORES "
                                                                     + "(COD_AFERICAO, COD_PNEU, COD_UNIDADE, PSI, " +
                                                                     "ALTURA_SULCO_CENTRAL_INTERNO, " +
                                                                     "ALTURA_SULCO_CENTRAL_EXTERNO, " +
                                                                     "ALTURA_SULCO_EXTERNO, " +
                                                                     "ALTURA_SULCO_INTERNO, POSICAO, " +
                                                                     "VIDA_MOMENTO_AFERICAO) VALUES "
                                                                     + "(?, ?, ?, ?, ?, ?, ?, ?, ?, ?)");

        final List<Pneu> pneusAferidos = afericao.getPneusAferidos();
        for (final Pneu pneu : pneusAferidos) {
            stmt.setLong(1, afericao.getCodigo());
            stmt.setLong(2, pneu.getCodigo());
            stmt.setLong(3, codUnidade);

            // Já aproveitamos esse switch para atualizar as medições do pneu na tabela PNEU.
            switch (afericao.getTipoMedicaoColetadaAfericao()) {
                case SULCO_PRESSAO:
                    pneuDao.updateMedicoes(conn, pneu.getCodigo(), pneu.getSulcosAtuais(), pneu.getPressaoAtual());
                    stmt.setDouble(4, pneu.getPressaoAtual());
                    stmt.setDouble(5, pneu.getSulcosAtuais().getCentralInterno());
                    stmt.setDouble(6, pneu.getSulcosAtuais().getCentralExterno());
                    stmt.setDouble(7, pneu.getSulcosAtuais().getExterno());
                    stmt.setDouble(8, pneu.getSulcosAtuais().getInterno());
                    break;
                case SULCO:
                    pneuDao.updateSulcos(conn, pneu.getCodigo(), pneu.getSulcosAtuais());
                    stmt.setNull(4, Types.REAL);
                    stmt.setDouble(5, pneu.getSulcosAtuais().getCentralInterno());
                    stmt.setDouble(6, pneu.getSulcosAtuais().getCentralExterno());
                    stmt.setDouble(7, pneu.getSulcosAtuais().getExterno());
                    stmt.setDouble(8, pneu.getSulcosAtuais().getInterno());
                    break;
                case PRESSAO:
                    pneuDao.updatePressao(conn, pneu.getCodigo(), pneu.getPressaoAtual());
                    stmt.setDouble(4, pneu.getPressaoAtual());
                    stmt.setNull(5, Types.REAL);
                    stmt.setNull(6, Types.REAL);
                    stmt.setNull(7, Types.REAL);
                    stmt.setNull(8, Types.REAL);
                    break;
            }
            stmt.setInt(9, pneu.getPosicao());
            stmt.setInt(10, pneu.getVidaAtual());
            if (stmt.executeUpdate() == 0) {
                throw new SQLException("Não foi possível atualizar as medidas para o pneu: " + pneu.getCodigo());
            }

            // Se não devemos abrir serviços para as medições coletadas,
            // então podemos encerrar o processo aqui.
            if (!deveAbrirServico || !afericaoPlaca) {
                continue;
            }

            // Insere/atualiza os serviços que os pneus aferidos possam ter gerado.
            final List<TipoServico> servicosACadastrar = getServicosACadastrar(
                    pneu,
                    restricao,
                    afericao.getTipoMedicaoColetadaAfericao());
            atualizaServicos(
                    conn,
                    servicoDao,
                    codUnidade,
                    pneu.getCodigo(),
                    (AfericaoPlaca) afericao,
                    servicosACadastrar);
        }
    }

    @NotNull
    private List<TipoServico> getServicosACadastrar(
            @NotNull final Pneu pneu,
            @NotNull final Restricao restricao,
            @NotNull final TipoMedicaoColetadaAfericao tipoMedicaoColetadaAfericao) {
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
        else if (pneu.getPressaoAtual() <= (pneu.getPressaoCorreta() * (1 - restricao.getToleranciaCalibragem()))
                || pneu.getPressaoAtual() >= (pneu.getPressaoCorreta() * (1 + restricao.getToleranciaCalibragem()))) {
            servicos.add(TipoServico.CALIBRAGEM);
        }

        // Verifica se precisamos abrir serviço de movimentação.
        if (pneu.getVidaAtual() == pneu.getVidasTotal()) {
            // Se o pneu esta na última vida, então ele irá para descarte,
            // por isso devemos considerar o sulco mínimo para esse caso.
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
                case SULCO_PRESSAO:
                    // Não precisamos remover nenhum serviço criado aqui.
                    break;
            }
        }
        return servicos;
    }

    @NotNull
    private Restricao createRestricao(@NotNull final ResultSet rSet) throws Throwable {
        final Restricao restricao = new Restricao();
        restricao.setSulcoMinimoDescarte(rSet.getDouble("SULCO_MINIMO_DESCARTE"));
        restricao.setSulcoMinimoRecape(rSet.getDouble("SULCO_MINIMO_RECAPAGEM"));
        restricao.setToleranciaCalibragem(rSet.getDouble("TOLERANCIA_CALIBRAGEM"));
        restricao.setToleranciaInspecao(rSet.getDouble("TOLERANCIA_INSPECAO"));
        restricao.setPeriodoDiasAfericaoSulco(rSet.getInt("PERIODO_AFERICAO_SULCO"));
        restricao.setPeriodoDiasAfericaoPressao(rSet.getInt("PERIODO_AFERICAO_PRESSAO"));
        return restricao;
    }

    private void atualizaServicos(@NotNull final Connection conn,
                                  @NotNull final ServicoDao servicoDao,
                                  @NotNull final Long codUnidade,
                                  @NotNull final Long codPneu,
                                  @NotNull final AfericaoPlaca afericao,
                                  @NotNull final List<TipoServico> servicosPendentes) throws Throwable {
        final List<TipoServico> servicosCadastrados = servicoDao.getServicosCadastradosByPneu(codUnidade, codPneu);

        for (final TipoServico servicoCadastrado : servicosCadastrados) {
            if (afericao.getTipoMedicaoColetadaAfericao().equals(TipoMedicaoColetadaAfericao.SULCO)) {
                fechaAutomaticamenteServicosMovimentacao(conn,
                                                         servicoDao,
                                                         codUnidade,
                                                         codPneu,
                                                         afericao,
                                                         servicosPendentes,
                                                         servicoCadastrado);
            } else if (afericao.getTipoMedicaoColetadaAfericao().equals(TipoMedicaoColetadaAfericao.PRESSAO)) {
                fechaAutomaticamenteServicosInspecaoCalibragem(conn,
                                                               servicoDao,
                                                               codUnidade,
                                                               codPneu,
                                                               afericao,
                                                               servicosPendentes,
                                                               servicoCadastrado);
            } else {
                fechaAutomaticamenteServicosMovimentacao(conn,
                                                         servicoDao,
                                                         codUnidade,
                                                         codPneu,
                                                         afericao,
                                                         servicosPendentes,
                                                         servicoCadastrado);
                fechaAutomaticamenteServicosInspecaoCalibragem(conn,
                                                               servicoDao,
                                                               codUnidade,
                                                               codPneu,
                                                               afericao,
                                                               servicosPendentes,
                                                               servicoCadastrado);
            }
        }

        for (final TipoServico servicoPendente : servicosPendentes) {
            if (servicoPendente.equals(TipoServico.INSPECAO)
                    && servicosCadastrados.contains(TipoServico.CALIBRAGEM)) {
                servicoDao.convertServico(conn, codUnidade, codPneu, TipoServico.CALIBRAGEM, TipoServico.INSPECAO);
            } else if (servicoPendente.equals(TipoServico.CALIBRAGEM)
                    && servicosCadastrados.contains(TipoServico.INSPECAO)) {
                servicoDao.convertServico(conn, codUnidade, codPneu, TipoServico.INSPECAO, TipoServico.CALIBRAGEM);
            } else if (servicosCadastrados.contains(servicoPendente)) {
                servicoDao.incrementaQtdApontamentosServico(conn, codUnidade, codPneu, servicoPendente);
            } else {
                servicoDao.criaServico(conn, codUnidade, codPneu, afericao.getCodigo(), servicoPendente);
            }
        }
    }

    private void fechaAutomaticamenteServicosInspecaoCalibragem(@NotNull final Connection conn,
                                                                @NotNull final ServicoDao servicoDao,
                                                                @NotNull final Long codUnidade,
                                                                @NotNull final Long codPneu,
                                                                @NotNull final AfericaoPlaca afericao,
                                                                @NotNull final List<TipoServico> servicosPendentes,
                                                                @NotNull final TipoServico servicoCadastrado)
            throws SQLException {
        if (servicoCadastrado.equals(TipoServico.CALIBRAGEM)
                || servicoCadastrado.equals(TipoServico.INSPECAO)) {
            if (!servicosPendentes.contains(TipoServico.INSPECAO)
                    && !servicosPendentes.contains(TipoServico.CALIBRAGEM)) {
                servicoDao.fecharAutomaticamenteServicosCalibragemPneu(conn,
                                                                       codUnidade,
                                                                       codPneu,
                                                                       afericao.getCodigo(),
                                                                       afericao.getDataHora()
                                                                               .atOffset(ZoneOffset.UTC),
                                                                       afericao.getKmMomentoAfericao(),
                                                                       OrigemFechamentoAutomaticoEnum.AFERICAO);
                servicoDao.fecharAutomaticamenteServicosInspecaoPneu(conn,
                                                                     codUnidade,
                                                                     codPneu,
                                                                     afericao.getCodigo(),
                                                                     afericao.getDataHora()
                                                                             .atOffset(ZoneOffset.UTC),
                                                                     afericao.getKmMomentoAfericao(),
                                                                     OrigemFechamentoAutomaticoEnum.AFERICAO);
            }
        }
    }

    private void fechaAutomaticamenteServicosMovimentacao(@NotNull final Connection conn,
                                                          @NotNull final ServicoDao servicoDao,
                                                          @NotNull final Long codUnidade,
                                                          @NotNull final Long codPneu,
                                                          @NotNull final AfericaoPlaca afericao,
                                                          @NotNull final List<TipoServico> servicosPendentes,
                                                          @NotNull final TipoServico servicoCadastrado)
            throws SQLException {
        if (servicoCadastrado.equals(TipoServico.MOVIMENTACAO)) {
            if (!servicosPendentes.contains(TipoServico.MOVIMENTACAO)) {
                servicoDao.fecharAutomaticamenteServicosMovimentacaoPneu(conn,
                                                                         codUnidade,
                                                                         codPneu,
                                                                         afericao.getCodigo(),
                                                                         afericao.getDataHora()
                                                                                 .atOffset(ZoneOffset.UTC),
                                                                         afericao.getKmMomentoAfericao(),
                                                                         OrigemFechamentoAutomaticoEnum.AFERICAO);
            }
        }
    }

    @NotNull
    private AfericaoPlaca createAfericaoPlacaResumida(@NotNull final ResultSet rSet) throws Throwable {
        final AfericaoPlaca afericaoPlaca = new AfericaoPlaca();
        // Veículo no qual aferição foi realizada.
        final Veiculo veiculo = new Veiculo();
        veiculo.setPlaca(rSet.getString("PLACA_VEICULO"));
        veiculo.setIdentificadorFrota(rSet.getString("IDENTIFICADOR_FROTA"));
        afericaoPlaca.setKmMomentoAfericao(rSet.getLong("KM_VEICULO"));
        afericaoPlaca.setVeiculo(veiculo);
        setDadosComunsAfericaoResumida(rSet, afericaoPlaca);
        return afericaoPlaca;
    }

    @NotNull
    private AfericaoAvulsa createAfericaoAvulsaResumida(@NotNull final ResultSet rSet) throws Throwable {
        final AfericaoAvulsa afericaoAvulsa = new AfericaoAvulsa();
        // TODO - É necessário setar as informações da aferição avulsa aqui.
        setDadosComunsAfericaoResumida(rSet, afericaoAvulsa);
        return afericaoAvulsa;
    }

    private void setDadosComunsAfericaoResumida(@NotNull final ResultSet rSet,
                                                @NotNull final Afericao afericao) throws Throwable {
        // Atributos em comum.
        afericao.setCodigo(rSet.getLong("COD_AFERICAO"));
        afericao.setCodUnidade(rSet.getLong("COD_UNIDADE"));
        afericao.setDataHora(rSet.getObject("DATA_HORA", LocalDateTime.class));
        afericao.setTipoMedicaoColetadaAfericao(TipoMedicaoColetadaAfericao.fromString(rSet.getString
                ("TIPO_MEDICAO_COLETADA")));
        afericao.setTempoRealizacaoAfericaoInMillis(rSet.getLong("TEMPO_REALIZACAO"));
        afericao.setFormaColetaDadosAfericao(FormaColetaDadosAfericaoEnum
                                                     .fromString(rSet.getString("FORMA_COLETA_DADOS")));
        // Colaborador que realizou a aferição.
        final Colaborador colaborador = new Colaborador();
        colaborador.setCpf(rSet.getLong("CPF"));
        colaborador.setNome(rSet.getString("NOME"));
        afericao.setColaborador(colaborador);
    }
}