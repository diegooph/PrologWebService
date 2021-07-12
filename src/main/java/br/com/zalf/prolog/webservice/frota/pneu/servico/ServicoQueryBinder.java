package br.com.zalf.prolog.webservice.frota.pneu.servico;

import br.com.zalf.prolog.webservice.TimeZoneManager;
import br.com.zalf.prolog.webservice.commons.util.database.SqlType;
import br.com.zalf.prolog.webservice.commons.util.datetime.DateUtils;
import br.com.zalf.prolog.webservice.frota.pneu.servico._model.*;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.time.ZoneId;

import static br.com.zalf.prolog.webservice.commons.util.database.StatementUtils.bindValueOrNull;

/**
 * Essa classe mantém todas as queries utilizadas na {@link ServicoDaoImpl} e faz o bind na query dos atributos
 * necessários. Isso deixa a classe {@link ServicoDaoImpl} menor e mais concisa.
 * <p>
 * Created on 12/6/17
 *
 * @author Luiz Felipe (https://github.com/luizfp)
 */
final class ServicoQueryBinder {

    /**
     * Essa query é utilizada por todos as buscas de serviços. Cada busca concatena sua cláusula where para filtrar
     * como quiser o SELECT.
     */
    private static final String BASE_QUERY_BUSCA_SERVICOS = "SELECT "
            + "AM.CODIGO AS CODIGO_SERVICO, "
            + "AM.CPF_MECANICO AS CPF_RESPONSAVEL_FECHAMENTO, "
            + "AM.DATA_HORA_RESOLUCAO AT TIME ZONE (SELECT FUNC_GET_TIME_ZONE_UNIDADE(AM.COD_UNIDADE)) AS "
            + "DATA_HORA_FECHAMENTO, "
            + "AM.KM_MOMENTO_CONSERTO AS KM_VEICULO_MOMENTO_FECHAMENTO, "
            + "AM.TEMPO_REALIZACAO_MILLIS AS TEMPO_REALIZACAO_MILLIS, "
            + "AM.COD_UNIDADE AS COD_UNIDADE, "
            + "AM.TIPO_SERVICO, "
            + "AM.QT_APONTAMENTOS, "
            + "AM.PSI_APOS_CONSERTO AS PRESSAO_COLETADA_FECHAMENTO, "
            + "AM.FECHADO_AUTOMATICAMENTE_MOVIMENTACAO, "
            + "AM.FECHADO_AUTOMATICAMENTE_INTEGRACAO, "
            + "AM.FECHADO_AUTOMATICAMENTE_AFERICAO, "
            + "AM.COD_AFERICAO_FECHAMENTO_AUTOMATICO, "
            + "AM.FORMA_COLETA_DADOS_FECHAMENTO, "
            + "A.DATA_HORA AT TIME ZONE (SELECT FUNC_GET_TIME_ZONE_UNIDADE(AM.COD_UNIDADE)) AS DATA_HORA_ABERTURA, "
            + "V.CODIGO AS COD_VEICULO, "
            + "V.PLACA AS PLACA_VEICULO, "
            + "V.IDENTIFICADOR_FROTA AS IDENTIFICADOR_FROTA, "
            + "A.CODIGO AS COD_AFERICAO, "
            + "A.CODIGO AS COD_AFERICAO, "
            + "AV.COD_PNEU AS COD_PNEU_PROBLEMA, "
            + "P.CODIGO_CLIENTE AS COD_PNEU_PROBLEMA_CLIENTE, "
            + "AV.ALTURA_SULCO_EXTERNO AS SULCO_EXTERNO_PNEU_PROBLEMA, "
            + "AV.ALTURA_SULCO_CENTRAL_EXTERNO AS SULCO_CENTRAL_EXTERNO_PNEU_PROBLEMA, "
            + "AV.ALTURA_SULCO_CENTRAL_INTERNO AS SULCO_CENTRAL_INTERNO_PNEU_PROBLEMA, "
            + "AV.ALTURA_SULCO_INTERNO AS SULCO_INTERNO_PNEU_PROBLEMA, "
            + "AV.PSI AS PRESSAO_PNEU_PROBLEMA, "
            + "AV.POSICAO AS POSICAO_PNEU_PROBLEMA, "
            + "AV.VIDA_MOMENTO_AFERICAO AS VIDA_PNEU_PROBLEMA, "
            + "C.NOME AS NOME_RESPONSAVEL_FECHAMENTO, "
            + "P.PRESSAO_RECOMENDADA, "
            + "P.ALTURA_SULCO_EXTERNO AS SULCO_EXTERNO_ATUAL, "
            + "P.ALTURA_SULCO_CENTRAL_EXTERNO AS SULCO_CENTRAL_EXTERNO_ATUAL, "
            + "P.ALTURA_SULCO_CENTRAL_INTERNO AS SULCO_CENTRAL_INTERNO_ATUAL, "
            + "P.ALTURA_SULCO_INTERNO AS SULCO_INTERNO_ATUAL, "
            + "P.PRESSAO_ATUAL "
            + "FROM AFERICAO_MANUTENCAO AM "
            + "LEFT JOIN COLABORADOR C ON AM.CPF_MECANICO = C.CPF "
            + "JOIN PNEU P ON AM.COD_PNEU = P.CODIGO "
            + "JOIN AFERICAO A ON A.CODIGO = AM.COD_AFERICAO "
            + "JOIN VEICULO V ON V.CODIGO = A.COD_VEICULO "
            + "JOIN AFERICAO_VALORES AV ON AV.COD_AFERICAO = AM.COD_AFERICAO AND AV.COD_PNEU = AM.COD_PNEU "
            + "JOIN UNIDADE U ON U.CODIGO = AM.COD_UNIDADE ";

    private ServicoQueryBinder() {
        throw new IllegalStateException(ServicoQueryBinder.class.getSimpleName() + " cannot be instantiated!");
    }

    @NotNull
    static PreparedStatement getQuantidadeServicosAbertosVeiculo(@NotNull final Connection connection,
                                                                 @NotNull final Long codUnidade) throws SQLException {
        final PreparedStatement stmt = connection.prepareStatement("SELECT " +
                                                                           "  V.CODIGO AS COD_VEICULO," +
                                                                           "  V.PLACA AS PLACA_VEICULO," +
                                                                           "  V.IDENTIFICADOR_FROTA, " +
                                                                           "  SUM(CASE WHEN AM.TIPO_SERVICO = ? THEN " +
                                                                           "1 ELSE 0 END) AS TOTAL_CALIBRAGENS, " +
                                                                           "  SUM(CASE WHEN AM.TIPO_SERVICO = ? THEN " +
                                                                           "1 ELSE 0 END) AS TOTAL_INSPECOES, " +
                                                                           "  SUM(CASE WHEN AM.TIPO_SERVICO = ? THEN " +
                                                                           "1 ELSE 0 END) AS TOTAL_MOVIMENTACOES " +
                                                                           "FROM AFERICAO_MANUTENCAO AS AM " +
                                                                           "  JOIN AFERICAO AS A " +
                                                                           "    ON A.CODIGO = AM.COD_AFERICAO " +
                                                                           "  JOIN VEICULO V" +
                                                                           "    ON V.CODIGO = A.COD_VEICULO" +
                                                                           "  JOIN VEICULO_PNEU AS VP " +
                                                                           "    ON AM.COD_PNEU = VP.COD_PNEU AND AM" +
                                                                           ".COD_UNIDADE = VP.COD_UNIDADE " +
                                                                           "WHERE AM.COD_UNIDADE = ? " +
                                                                           "      AND AM.DATA_HORA_RESOLUCAO IS NULL " +
                                                                           "GROUP BY V.CODIGO, V.PLACA, V" +
                                                                           ".IDENTIFICADOR_FROTA " +
                                                                           "ORDER BY TOTAL_CALIBRAGENS DESC, " +
                                                                           "TOTAL_INSPECOES DESC, TOTAL_MOVIMENTACOES" +
                                                                           " DESC;");
        stmt.setString(1, TipoServico.CALIBRAGEM.asString());
        stmt.setString(2, TipoServico.INSPECAO.asString());
        stmt.setString(3, TipoServico.MOVIMENTACAO.asString());
        stmt.setLong(4, codUnidade);
        return stmt;
    }

    @NotNull
    static PreparedStatement getServicosAbertosByCodVeiculo(@NotNull final Connection connection,
                                                            @NotNull final Long codVeiculo,
                                                            @Nullable final TipoServico tipoServico)
            throws SQLException {
        final PreparedStatement stmt = connection.prepareStatement(BASE_QUERY_BUSCA_SERVICOS
                                                                           + "WHERE V.CODIGO = ? "
                                                                           + "AND AM.DATA_HORA_RESOLUCAO IS NULL "
                                                                           + "AND AM.TIPO_SERVICO LIKE ? "
                                                                           + "ORDER BY AM.TIPO_SERVICO;");
        stmt.setLong(1, codVeiculo);
        stmt.setString(2, tipoServico != null ? tipoServico.asString() : "%");
        return stmt;
    }

    @NotNull
    static PreparedStatement getQuantidadeServicosFechadosPneu(@NotNull final Connection connection,
                                                               @NotNull final Long codUnidade,
                                                               final long dataInicial,
                                                               final long dataFinal) throws SQLException {
        final PreparedStatement stmt = connection.prepareStatement("SELECT " +
                                                                           "  AM.COD_PNEU, " +
                                                                           "  P.CODIGO_CLIENTE AS " +
                                                                           "CODIGO_PNEU_CLIENTE, " +
                                                                           "  SUM(CASE WHEN AM.TIPO_SERVICO = ? THEN " +
                                                                           "1 ELSE 0 END) AS TOTAL_CALIBRAGENS, " +
                                                                           "  SUM(CASE WHEN AM.TIPO_SERVICO = ? THEN " +
                                                                           "1 ELSE 0 END) AS TOTAL_INSPECOES, " +
                                                                           "  SUM(CASE WHEN AM.TIPO_SERVICO = ? THEN " +
                                                                           "1 ELSE 0 END) AS TOTAL_MOVIMENTACOES " +
                                                                           "FROM AFERICAO_MANUTENCAO AM " +
                                                                           "  JOIN AFERICAO A ON A.CODIGO = AM" +
                                                                           ".COD_AFERICAO " +
                                                                           "  JOIN PNEU P ON AM.COD_PNEU = P.CODIGO " +
                                                                           "WHERE AM.COD_UNIDADE = ? " +
                                                                           "      AND AM.DATA_HORA_RESOLUCAO IS NOT " +
                                                                           "NULL " +
                                                                           "      AND (AM.DATA_HORA_RESOLUCAO AT TIME" +
                                                                           " ZONE TZ_UNIDADE(AM.COD_UNIDADE))::DATE " +
                                                                           "BETWEEN ? AND ? " +
                                                                           "GROUP BY P.CODIGO_CLIENTE, AM.COD_PNEU " +
                                                                           "ORDER BY TOTAL_CALIBRAGENS DESC, " +
                                                                           "TOTAL_INSPECOES DESC, TOTAL_MOVIMENTACOES" +
                                                                           " DESC;");
        stmt.setString(1, TipoServico.CALIBRAGEM.asString());
        stmt.setString(2, TipoServico.INSPECAO.asString());
        stmt.setString(3, TipoServico.MOVIMENTACAO.asString());
        stmt.setLong(4, codUnidade);
        stmt.setObject(5, DateUtils.toLocalDate(new java.sql.Date(dataInicial)));
        stmt.setObject(6, DateUtils.toLocalDate(new java.sql.Date(dataFinal)));
        return stmt;
    }

    @NotNull
    static PreparedStatement getQuantidadeServicosFechadosVeiculo(@NotNull final Connection connection,
                                                                  @NotNull final Long codUnidade,
                                                                  final long dataInicial,
                                                                  final long dataFinal) throws SQLException {
        final PreparedStatement stmt = connection.prepareStatement("SELECT" +
                                                                           "  V.CODIGO AS COD_VEICULO," +
                                                                           "  V.PLACA AS PLACA_VEICULO," +
                                                                           "  V.IDENTIFICADOR_FROTA," +
                                                                           "  SUM(CASE WHEN AM.TIPO_SERVICO = ? THEN " +
                                                                           "1 ELSE 0 END) AS TOTAL_CALIBRAGENS," +
                                                                           "  SUM(CASE WHEN AM.TIPO_SERVICO = ? THEN " +
                                                                           "1 ELSE 0 END) AS TOTAL_INSPECOES," +
                                                                           "  SUM(CASE WHEN AM.TIPO_SERVICO = ? THEN " +
                                                                           "1 ELSE 0 END) AS TOTAL_MOVIMENTACOES" +
                                                                           " FROM AFERICAO_MANUTENCAO AM  " +
                                                                           "  JOIN AFERICAO A ON A.CODIGO = AM" +
                                                                           ".COD_AFERICAO" +
                                                                           "  JOIN VEICULO V ON V.CODIGO = A" +
                                                                           ".COD_VEICULO" +
                                                                           " WHERE AM.COD_UNIDADE = ?" +
                                                                           "      AND AM.DATA_HORA_RESOLUCAO IS NOT " +
                                                                           "NULL  " +
                                                                           "      AND (AM.DATA_HORA_RESOLUCAO AT TIME" +
                                                                           " ZONE TZ_UNIDADE(AM.COD_UNIDADE))::DATE " +
                                                                           "BETWEEN ? AND ?" +
                                                                           " GROUP BY V.CODIGO, V.PLACA, V" +
                                                                           ".IDENTIFICADOR_FROTA" +
                                                                           " ORDER BY TOTAL_CALIBRAGENS DESC, " +
                                                                           "TOTAL_INSPECOES DESC, TOTAL_MOVIMENTACOES" +
                                                                           " DESC;");
        stmt.setString(1, TipoServico.CALIBRAGEM.asString());
        stmt.setString(2, TipoServico.INSPECAO.asString());
        stmt.setString(3, TipoServico.MOVIMENTACAO.asString());
        stmt.setLong(4, codUnidade);
        stmt.setObject(5, DateUtils.toLocalDate(new java.sql.Date(dataInicial)));
        stmt.setObject(6, DateUtils.toLocalDate(new java.sql.Date(dataFinal)));
        return stmt;
    }

    @NotNull
    static PreparedStatement getServicoByCod(@NotNull final Connection connection,
                                             @NotNull final Long codUnidade,
                                             @NotNull final Long codServico) throws SQLException {
        final PreparedStatement stmt = connection.prepareStatement("SELECT " +
                                                                           "   AM.CODIGO AS CODIGO_SERVICO, " +
                                                                           "   AM.CPF_MECANICO AS " +
                                                                           "CPF_RESPONSAVEL_FECHAMENTO, " +
                                                                           "   AM.DATA_HORA_RESOLUCAO AT TIME ZONE ? " +
                                                                           "AS DATA_HORA_FECHAMENTO, " +
                                                                           "   AM.KM_MOMENTO_CONSERTO AS " +
                                                                           "KM_VEICULO_MOMENTO_FECHAMENTO, " +
                                                                           "   AM.TEMPO_REALIZACAO_MILLIS AS " +
                                                                           "TEMPO_REALIZACAO_MILLIS, " +
                                                                           "   AM.COD_UNIDADE AS COD_UNIDADE, " +
                                                                           "   AM.TIPO_SERVICO, " +
                                                                           "   AM.QT_APONTAMENTOS, " +
                                                                           "   AM.PSI_APOS_CONSERTO AS " +
                                                                           "PRESSAO_COLETADA_FECHAMENTO, " +
                                                                           "   AM.COD_ALTERNATIVA AS " +
                                                                           "COD_ALTERNATIVA_SELECIONADA, " +
                                                                           "   AM" +
                                                                           ".FECHADO_AUTOMATICAMENTE_MOVIMENTACAO, " +
                                                                           "   AM.FECHADO_AUTOMATICAMENTE_INTEGRACAO," +
                                                                           "   AM.FECHADO_AUTOMATICAMENTE_AFERICAO, " +
                                                                           "   AM.COD_AFERICAO_FECHAMENTO_AUTOMATICO," +
                                                                           "   AM.FORMA_COLETA_DADOS_FECHAMENTO, " +
                                                                           "   AAMI.ALTERNATIVA AS " +
                                                                           "DESCRICAO_ALTERNATIVA_SELECIONADA, " +
                                                                           "   M.COD_PNEU AS COD_PNEU_NOVO, " +
                                                                           "   PNEU_NOVO.CODIGO_CLIENTE AS " +
                                                                           "COD_PNEU_NOVO_CLIENTE, " +
                                                                           "   M.SULCO_EXTERNO AS " +
                                                                           "SULCO_EXTERNO_PNEU_NOVO, " +
                                                                           "   M.SULCO_CENTRAL_EXTERNO AS " +
                                                                           "SULCO_CENTRAL_EXTERNO_PNEU_NOVO, " +
                                                                           "   M.SULCO_CENTRAL_INTERNO AS " +
                                                                           "SULCO_CENTRAL_INTERNO_PNEU_NOVO, " +
                                                                           "   M.SULCO_INTERNO AS " +
                                                                           "SULCO_INTERNO_PNEU_NOVO, " +
                                                                           "   M.VIDA AS VIDA_PNEU_NOVO, " +
                                                                           "   A.DATA_HORA AT TIME ZONE ? AS " +
                                                                           "DATA_HORA_ABERTURA, " +
                                                                           "   V.CODIGO AS COD_VEICULO, " +
                                                                           "   V.PLACA AS PLACA_VEICULO, " +
                                                                           "   V.IDENTIFICADOR_FROTA AS " +
                                                                           "IDENTIFICADOR_FROTA, " +
                                                                           "   A.CODIGO AS COD_AFERICAO, " +
                                                                           "   C.NOME AS NOME_RESPONSAVEL_FECHAMENTO," +
                                                                           " " +
                                                                           "   AV.COD_PNEU AS COD_PNEU_PROBLEMA, " +
                                                                           "   PNEU_PROBLEMA.CODIGO_CLIENTE AS " +
                                                                           "COD_PNEU_PROBLEMA_CLIENTE, " +
                                                                           "PNEU_PROBLEMA.ALTURA_SULCO_EXTERNO AS " +
                                                                           "SULCO_EXTERNO_ATUAL, " +
                                                                           "PNEU_PROBLEMA" +
                                                                           ".ALTURA_SULCO_CENTRAL_EXTERNO AS " +
                                                                           "SULCO_CENTRAL_EXTERNO_ATUAL, " +
                                                                           "PNEU_PROBLEMA" +
                                                                           ".ALTURA_SULCO_CENTRAL_INTERNO AS " +
                                                                           "SULCO_CENTRAL_INTERNO_ATUAL, " +
                                                                           "PNEU_PROBLEMA.ALTURA_SULCO_INTERNO AS " +
                                                                           "SULCO_INTERNO_ATUAL, " +
                                                                           "PNEU_PROBLEMA.PRESSAO_ATUAL, " +
                                                                           "   AV.POSICAO AS POSICAO_PNEU_PROBLEMA, " +
                                                                           "   AV.VIDA_MOMENTO_AFERICAO AS " +
                                                                           "VIDA_PNEU_PROBLEMA, " +
                                                                           "   PNEU_PROBLEMA.PRESSAO_RECOMENDADA " +
                                                                           "   FROM AFERICAO_MANUTENCAO AM " +
                                                                           "   JOIN AFERICAO A ON A.CODIGO = AM" +
                                                                           ".COD_AFERICAO " +
                                                                           "   JOIN VEICULO V ON V.CODIGO = A" +
                                                                           ".COD_VEICULO " +
                                                                           "   JOIN AFERICAO_VALORES AV ON AV" +
                                                                           ".COD_AFERICAO = AM.COD_AFERICAO AND AV" +
                                                                           ".COD_PNEU = AM.COD_PNEU " +
                                                                           "   JOIN UNIDADE U ON U.CODIGO = AM" +
                                                                           ".COD_UNIDADE " +
                                                                           "   JOIN PNEU PNEU_PROBLEMA ON AM.COD_PNEU" +
                                                                           " = PNEU_PROBLEMA.CODIGO " +
                                                                           "   LEFT JOIN MOVIMENTACAO M ON M" +
                                                                           ".COD_MOVIMENTACAO_PROCESSO = AM" +
                                                                           ".COD_PROCESSO_MOVIMENTACAO " +
                                                                           "   AND M.COD_PNEU = AM.COD_PNEU_INSERIDO " +
                                                                           "   LEFT JOIN PNEU PNEU_NOVO ON AM" +
                                                                           ".COD_PNEU_INSERIDO = PNEU_NOVO.CODIGO " +
                                                                           "   LEFT JOIN " +
                                                                           "AFERICAO_ALTERNATIVA_MANUTENCAO_INSPECAO " +
                                                                           "AAMI ON AAMI.CODIGO = AM.COD_ALTERNATIVA " +
                                                                           "   LEFT JOIN COLABORADOR C ON AM" +
                                                                           ".CPF_MECANICO = C.CPF " +
                                                                           "   WHERE AM.COD_UNIDADE = ? AND AM.CODIGO" +
                                                                           " = ?;");
        final ZoneId zoneId = TimeZoneManager.getZoneIdForCodUnidade(codUnidade, connection);
        stmt.setString(1, zoneId.getId());
        stmt.setString(2, zoneId.getId());
        stmt.setLong(3, codUnidade);
        stmt.setLong(4, codServico);
        return stmt;
    }

    @NotNull
    static PreparedStatement getServicosFechados(@NotNull final Connection connection,
                                                 @NotNull final Long codUnidade,
                                                 final long dataInicial,
                                                 final long dataFinal) throws SQLException {
        final PreparedStatement stmt = connection.prepareStatement(BASE_QUERY_BUSCA_SERVICOS
                                                                           + "WHERE AM.COD_UNIDADE = ? "
                                                                           + "AND AM.DATA_HORA_RESOLUCAO IS NOT NULL "
                                                                           + "AND (AM.DATA_HORA_RESOLUCAO AT TIME " +
                                                                           "ZONE TZ_UNIDADE(AM.COD_UNIDADE))::DATE " +
                                                                           "BETWEEN ? AND ? "
                                                                           + "ORDER BY DATA_HORA_RESOLUCAO DESC;");
        stmt.setLong(1, codUnidade);
        stmt.setObject(2, DateUtils.toLocalDate(new java.sql.Date(dataInicial)));
        stmt.setObject(3, DateUtils.toLocalDate(new java.sql.Date(dataFinal)));
        return stmt;
    }

    @NotNull
    static PreparedStatement getServicosFechadosPneu(@NotNull final Connection connection,
                                                     @NotNull final Long codUnidade,
                                                     @NotNull final Long codPneu,
                                                     final long dataInicial,
                                                     final long dataFinal) throws SQLException {
        final PreparedStatement stmt = connection.prepareStatement(BASE_QUERY_BUSCA_SERVICOS
                                                                           + "WHERE AM.COD_UNIDADE = ? "
                                                                           + "AND AM.COD_PNEU = ? "
                                                                           + "AND AM.DATA_HORA_RESOLUCAO IS NOT NULL "
                                                                           + "AND (AM.DATA_HORA_RESOLUCAO AT TIME " +
                                                                           "ZONE TZ_UNIDADE(AM.COD_UNIDADE))::DATE " +
                                                                           "BETWEEN ? AND ? "
                                                                           + "ORDER BY DATA_HORA_RESOLUCAO DESC;");
        stmt.setLong(1, codUnidade);
        stmt.setLong(2, codPneu);
        stmt.setObject(3, DateUtils.toLocalDate(new java.sql.Date(dataInicial)));
        stmt.setObject(4, DateUtils.toLocalDate(new java.sql.Date(dataFinal)));
        return stmt;
    }

    @NotNull
    static PreparedStatement getServicosFechadosVeiculo(@NotNull final Connection connection,
                                                        @NotNull final Long codUnidade,
                                                        @NotNull final Long codVeiculo,
                                                        @NotNull final LocalDate dataInicial,
                                                        @NotNull final LocalDate dataFinal) throws SQLException {
        final PreparedStatement stmt = connection.prepareStatement(BASE_QUERY_BUSCA_SERVICOS
                                                                           + "WHERE AM.COD_UNIDADE = ? "
                                                                           + "AND V.CODIGO = ? "
                                                                           + "AND AM.DATA_HORA_RESOLUCAO IS NOT NULL "
                                                                           + "AND (AM.DATA_HORA_RESOLUCAO AT TIME " +
                                                                           "ZONE TZ_UNIDADE(AM.COD_UNIDADE))::DATE " +
                                                                           "BETWEEN ? AND ? "
                                                                           + "ORDER BY DATA_HORA_RESOLUCAO DESC;");
        stmt.setLong(1, codUnidade);
        stmt.setLong(2, codVeiculo);
        stmt.setObject(3, dataInicial);
        stmt.setObject(4, dataFinal);
        return stmt;
    }

    @NotNull
    static PreparedStatement getVeiculoAberturaServico(@NotNull final Connection connection,
                                                       @NotNull final Long codVeiculo,
                                                       @NotNull final Long codServico) throws Throwable {
        final PreparedStatement stmt = connection.prepareStatement("SELECT " +
                                                                           "  V.PLACA AS PLACA_VEICULO, " +
                                                                           "  V.IDENTIFICADOR_FROTA, " +
                                                                           "  V.CODIGO AS COD_VEICULO, " +
                                                                           "  V.COD_UNIDADE AS COD_UNIDADE, " +
                                                                           "  A.KM_VEICULO AS KM_ABERTURA_SERVICO, " +
                                                                           "  AV.COD_PNEU AS COD_PNEU, " +
                                                                           "  P.CODIGO_CLIENTE AS COD_PNEU_CLIENTE, " +
                                                                           "  AV.ALTURA_SULCO_EXTERNO, " +
                                                                           "  AV.ALTURA_SULCO_CENTRAL_EXTERNO, " +
                                                                           "  AV.ALTURA_SULCO_CENTRAL_INTERNO, " +
                                                                           "  AV.ALTURA_SULCO_INTERNO, " +
                                                                           "  AV.PSI, " +
                                                                           "  AV.POSICAO, " +
                                                                           "  AV.VIDA_MOMENTO_AFERICAO, " +
                                                                           "  V.KM AS KM_ATUAL_VEICULO " +
                                                                           "FROM AFERICAO_MANUTENCAO AM " +
                                                                           "  JOIN AFERICAO A " +
                                                                           "    ON AM.COD_AFERICAO = A.CODIGO " +
                                                                           "  JOIN AFERICAO_VALORES AV " +
                                                                           "    ON AM.COD_AFERICAO = AV.COD_AFERICAO " +
                                                                           "       AND A.CODIGO = AV.COD_AFERICAO " +
                                                                           "  JOIN VEICULO V " +
                                                                           "    ON V.CODIGO = A.COD_VEICULO " +
                                                                           "  JOIN PNEU P " +
                                                                           "    ON P.CODIGO = AV.COD_PNEU " +
                                                                           "WHERE AM.CODIGO = ? " +
                                                                           "      AND V.CODIGO = ?;");
        stmt.setLong(1, codServico);
        stmt.setLong(2, codVeiculo);
        return stmt;
    }

    @NotNull
    static PreparedStatement getAlternativasInspecao(@NotNull final Connection connection) throws SQLException {
        return connection.prepareStatement("SELECT * FROM AFERICAO_ALTERNATIVA_MANUTENCAO_INSPECAO A "
                                                   + "WHERE A.STATUS_ATIVO = TRUE");
    }

    @NotNull
    static PreparedStatement fechaCalibragem(@NotNull final Connection connection,
                                             @NotNull final OffsetDateTime dataHorafechamentoServico,
                                             @NotNull final ServicoCalibragem servico,
                                             @NotNull final Long kmFinal) throws SQLException {
        final PreparedStatement stmt = connection.prepareStatement("UPDATE AFERICAO_MANUTENCAO SET "
                                                                           + "DATA_HORA_RESOLUCAO = ?, "
                                                                           + "CPF_MECANICO = ?, "
                                                                           + "PSI_APOS_CONSERTO = ?, "
                                                                           + "KM_MOMENTO_CONSERTO = ?, "
                                                                           + "TEMPO_REALIZACAO_MILLIS = ?, "
                                                                           + "FORMA_COLETA_DADOS_FECHAMENTO = ? "
                                                                           + "WHERE CODIGO = ? "
                                                                           + "AND TIPO_SERVICO = ? "
                                                                           + "AND DATA_HORA_RESOLUCAO IS NULL " +
                                                                           "returning codigo;");
        stmt.setObject(1, dataHorafechamentoServico);
        stmt.setLong(2, servico.getCpfResponsavelFechamento());
        stmt.setDouble(3, servico.getPressaoColetadaFechamento());
        stmt.setLong(4, kmFinal);
        stmt.setLong(5, servico.getTempoRealizacaoServicoInMillis());
        stmt.setString(6, servico.getFormaColetaDadosFechamentoAsStringOrEquipamentoIfNull());
        stmt.setLong(7, servico.getCodigo());
        stmt.setString(8, servico.getTipoServico().asString());
        return stmt;
    }

    @NotNull
    static PreparedStatement fechaInspecao(@NotNull final Connection connection,
                                           @NotNull final OffsetDateTime dataHorafechamentoServico,
                                           @NotNull final ServicoInspecao servico,
                                           @NotNull final Long kmFinal) throws SQLException {
        final PreparedStatement stmt = connection.prepareStatement("UPDATE AFERICAO_MANUTENCAO SET "
                                                                           + "DATA_HORA_RESOLUCAO = ?, "
                                                                           + "CPF_MECANICO = ?, "
                                                                           + "PSI_APOS_CONSERTO = ?, "
                                                                           + "KM_MOMENTO_CONSERTO = ?, "
                                                                           + "COD_ALTERNATIVA = ?, "
                                                                           + "TEMPO_REALIZACAO_MILLIS = ?, "
                                                                           + "FORMA_COLETA_DADOS_FECHAMENTO = ? "
                                                                           + "WHERE CODIGO = ? "
                                                                           + "AND TIPO_SERVICO = ? "
                                                                           + "AND DATA_HORA_RESOLUCAO IS NULL " +
                                                                           "returning codigo;");
        stmt.setObject(1, dataHorafechamentoServico);
        stmt.setLong(2, servico.getCpfResponsavelFechamento());
        stmt.setDouble(3, servico.getPressaoColetadaFechamento());
        stmt.setLong(4, kmFinal);
        stmt.setLong(5, servico.getAlternativaSelecionada().codigo);
        stmt.setLong(6, servico.getTempoRealizacaoServicoInMillis());
        stmt.setString(7, servico.getFormaColetaDadosFechamentoAsStringOrEquipamentoIfNull());
        stmt.setLong(8, servico.getCodigo());
        stmt.setString(9, servico.getTipoServico().asString());
        return stmt;
    }

    @NotNull
    static PreparedStatement fechaMovimentacao(@NotNull final Connection connection,
                                               @NotNull final OffsetDateTime dataHorafechamentoServico,
                                               @NotNull final ServicoMovimentacao servico,
                                               @NotNull final Long kmFinal) throws SQLException {
        final PreparedStatement stmt = connection.prepareStatement("update afericao_manutencao set "
                                                                           + "data_hora_resolucao = ?, "
                                                                           + "cpf_mecanico = ?, "
                                                                           + "km_momento_conserto = ?, "
                                                                           + "cod_processo_movimentacao = ?, "
                                                                           + "psi_apos_conserto = ?, "
                                                                           + "tempo_realizacao_millis = ?, "
                                                                           + "cod_pneu_inserido = ?, "
                                                                           + "forma_coleta_dados_fechamento = ? "
                                                                           + "where codigo = ? "
                                                                           + "and tipo_servico = ? "
                                                                           + "and data_hora_resolucao is null " +
                                                                           "returning codigo;");
        stmt.setObject(1, dataHorafechamentoServico);
        stmt.setLong(2, servico.getCpfResponsavelFechamento());
        stmt.setLong(3, kmFinal);
        stmt.setLong(4, servico.getCodProcessoMovimentacao());
        // Salva também o PSI após o conserto, já que os sulcos são salvos na tabela de movimentaçao.
        stmt.setDouble(5, servico.getPressaoColetadaFechamento());
        stmt.setLong(6, servico.getTempoRealizacaoServicoInMillis());
        stmt.setLong(7, servico.getPneuNovo().getCodigo());
        stmt.setString(8, servico.getFormaColetaDadosFechamentoAsStringOrEquipamentoIfNull());
        stmt.setLong(9, servico.getCodigo());
        stmt.setString(10, servico.getTipoServico().asString());
        return stmt;
    }

    @NotNull
    static PreparedStatement getQuantidadeServicosEmAbertoPneu(@NotNull final Connection conn,
                                                               @NotNull final Long codUnidade,
                                                               @NotNull final Long codPneu) throws SQLException {
        final PreparedStatement stmt = conn.prepareStatement("SELECT COUNT(CODIGO) AS QTD_SERVICOS_ABERTOS FROM " +
                                                                     "AFERICAO_MANUTENCAO AM " +
                                                                     "WHERE AM.COD_UNIDADE = ? AND AM.COD_PNEU = ? " +
                                                                     "AND DATA_HORA_RESOLUCAO IS NULL;");
        stmt.setLong(1, codUnidade);
        stmt.setLong(2, codPneu);
        return stmt;
    }

    @NotNull
    static PreparedStatement fecharAutomaticamenteServicosPneu(
            @NotNull final Connection conn,
            @NotNull final Long codUnidade,
            @NotNull final Long codProcesso,
            @NotNull final Long codPneu,
            @NotNull final OffsetDateTime dataHorafechamentoServico,
            @NotNull final Long kmColetadoVeiculo,
            @NotNull final OrigemFechamentoAutomaticoEnum origemFechamentoServico,
            @NotNull final TipoServico tipoServico) throws SQLException {
        final String sql = "UPDATE AFERICAO_MANUTENCAO SET "
                + "DATA_HORA_RESOLUCAO = ?, "
                + origemFechamentoServico.getCodigoColumnName() + " = ?, "
                + "KM_MOMENTO_CONSERTO = ?, "
                + origemFechamentoServico.getFlagColumnName() + " = TRUE "
                + "WHERE COD_UNIDADE = ? "
                + "AND TIPO_SERVICO = '" + tipoServico.asString() + "' "
                + "AND COD_PNEU = ? "
                + "AND DATA_HORA_RESOLUCAO IS NULL";

        final PreparedStatement stmt = conn.prepareStatement(sql);
        stmt.setObject(1, dataHorafechamentoServico);
        stmt.setLong(2, codProcesso);
        bindValueOrNull(stmt, 3, kmColetadoVeiculo, SqlType.BIGINT);
        stmt.setLong(4, codUnidade);
        stmt.setLong(5, codPneu);
        return stmt;
    }
}