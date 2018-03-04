package br.com.zalf.prolog.webservice.frota.pneu.servico;

import br.com.zalf.prolog.webservice.TimeZoneManager;
import br.com.zalf.prolog.webservice.frota.pneu.servico.model.*;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.sql.*;
import java.time.Clock;
import java.time.OffsetDateTime;
import java.time.ZoneId;

/**
 * Essa classe mantém todas as queries utilizadas na {@link ServicoDaoImpl} e faz o bind na query dos atributos
 * necessários. Isso deixa a classe {@link ServicoDaoImpl} menor e mais concisa.
 *
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
            + "AM.DATA_HORA_RESOLUCAO AT TIME ZONE (SELECT FUNC_GET_TIME_ZONE_UNIDADE(AM.COD_UNIDADE)) AS DATA_HORA_FECHAMENTO, "
            + "AM.KM_MOMENTO_CONSERTO AS KM_VEICULO_MOMENTO_FECHAMENTO, "
            + "AM.TEMPO_REALIZACAO_MILLIS AS TEMPO_REALIZACAO_MILLIS, "
            + "AM.COD_UNIDADE AS COD_UNIDADE, "
            + "AM.TIPO_SERVICO, "
            + "AM.QT_APONTAMENTOS, "
            + "AM.PSI_APOS_CONSERTO AS PRESSAO_COLETADA_FECHAMENTO, "
            + "AM.FECHADO_AUTOMATICAMENTE_MOVIMENTACAO, "
            + "A.DATA_HORA AT TIME ZONE (SELECT FUNC_GET_TIME_ZONE_UNIDADE(AM.COD_UNIDADE)) AS DATA_HORA_ABERTURA, "
            + "A.PLACA_VEICULO AS PLACA_VEICULO, "
            + "A.CODIGO AS COD_AFERICAO, "
            + "A.CODIGO AS COD_AFERICAO, "
            + "AV.COD_PNEU AS COD_PNEU_PROBLEMA, "
            + "AV.ALTURA_SULCO_EXTERNO AS SULCO_EXTERNO_PNEU_PROBLEMA, "
            + "AV.ALTURA_SULCO_CENTRAL_EXTERNO AS SULCO_CENTRAL_EXTERNO_PNEU_PROBLEMA, "
            + "AV.ALTURA_SULCO_CENTRAL_INTERNO AS SULCO_CENTRAL_INTERNO_PNEU_PROBLEMA, "
            + "AV.ALTURA_SULCO_INTERNO AS SULCO_INTERNO_PNEU_PROBLEMA, "
            + "AV.PSI AS PRESSAO_PNEU_PROBLEMA, "
            + "AV.POSICAO AS POSICAO_PNEU_PROBLEMA, "
            + "AV.VIDA_MOMENTO_AFERICAO AS VIDA_PNEU_PROBLEMA, "
            + "C.NOME AS NOME_RESPONSAVEL_FECHAMENTO, "
            + "P.PRESSAO_RECOMENDADA "
            + "FROM AFERICAO_MANUTENCAO AM "
            + "LEFT JOIN COLABORADOR C ON AM.CPF_MECANICO = C.CPF "
            + "JOIN PNEU P ON AM.COD_UNIDADE = P.COD_UNIDADE AND AM.COD_PNEU = P.CODIGO "
            + "JOIN AFERICAO A ON A.CODIGO = AM.COD_AFERICAO "
            + "JOIN AFERICAO_VALORES AV ON AV.COD_AFERICAO = AM.COD_AFERICAO AND AV.COD_PNEU = AM.COD_PNEU "
            + "JOIN UNIDADE U ON U.CODIGO = AM.COD_UNIDADE ";

    private ServicoQueryBinder() {
        throw new IllegalStateException(ServicoQueryBinder.class.getSimpleName() + " cannot be instantiated!");
    }

    static PreparedStatement getQuantidadeServicosAbertosVeiculo(Long codUnidade, Connection connection)
            throws SQLException {
        final PreparedStatement stmt = connection.prepareStatement("SELECT " +
                "  A.PLACA_VEICULO, " +
                "  SUM(CASE WHEN AM.TIPO_SERVICO = ? THEN 1 ELSE 0 END) AS TOTAL_CALIBRAGENS, " +
                "  SUM(CASE WHEN AM.TIPO_SERVICO = ? THEN 1 ELSE 0 END) AS TOTAL_INSPECOES, " +
                "  SUM(CASE WHEN AM.TIPO_SERVICO = ? THEN 1 ELSE 0 END) AS TOTAL_MOVIMENTACOES " +
                "FROM AFERICAO_MANUTENCAO AM " +
                "  JOIN AFERICAO A ON A.CODIGO = AM.COD_AFERICAO " +
                "WHERE AM.COD_UNIDADE = ? " +
                "      AND AM.DATA_HORA_RESOLUCAO IS NULL " +
                "GROUP BY A.PLACA_VEICULO " +
                "ORDER BY TOTAL_CALIBRAGENS DESC, TOTAL_INSPECOES DESC, TOTAL_MOVIMENTACOES DESC;");
        stmt.setString(1, TipoServico.CALIBRAGEM.asString());
        stmt.setString(2, TipoServico.INSPECAO.asString());
        stmt.setString(3, TipoServico.MOVIMENTACAO.asString());
        stmt.setLong(4, codUnidade);
        return stmt;
    }

    static PreparedStatement getServicosAbertosByPlaca(@NotNull String placa,
                                                       @Nullable TipoServico tipoServico,
                                                       @NotNull Connection connection)
            throws SQLException {
        final PreparedStatement stmt = connection.prepareStatement(BASE_QUERY_BUSCA_SERVICOS
                + "WHERE A.PLACA_VEICULO = ? "
                + "AND AM.DATA_HORA_RESOLUCAO IS NULL "
                + "AND AM.TIPO_SERVICO LIKE ? "
                + "ORDER BY AM.TIPO_SERVICO;");
        stmt.setString(1, placa);
        stmt.setString(2, tipoServico != null ? tipoServico.asString() : "%");
        return stmt;
    }

    static PreparedStatement getQuantidadeServicosFechadosPneu(Long codUnidade,
                                                               long dataInicial,
                                                               long dataFinal,
                                                               Connection connection) throws SQLException {
        final PreparedStatement stmt = connection.prepareStatement("SELECT " +
                "  AM.COD_PNEU, " +
                "  SUM(CASE WHEN AM.TIPO_SERVICO = ? THEN 1 ELSE 0 END) AS TOTAL_CALIBRAGENS, " +
                "  SUM(CASE WHEN AM.TIPO_SERVICO = ? THEN 1 ELSE 0 END) AS TOTAL_INSPECOES, " +
                "  SUM(CASE WHEN AM.TIPO_SERVICO = ? THEN 1 ELSE 0 END) AS TOTAL_MOVIMENTACOES " +
                "FROM AFERICAO_MANUTENCAO AM " +
                "  JOIN AFERICAO A ON A.CODIGO = AM.COD_AFERICAO " +
                "WHERE AM.COD_UNIDADE = ?" +
                "      AND AM.DATA_HORA_RESOLUCAO IS NOT NULL " +
                "      AND AM.DATA_HORA_RESOLUCAO::DATE BETWEEN ? AND ? " +
                "GROUP BY AM.COD_PNEU "+
                "ORDER BY TOTAL_CALIBRAGENS DESC, TOTAL_INSPECOES DESC, TOTAL_MOVIMENTACOES DESC;");
        stmt.setString(1, TipoServico.CALIBRAGEM.asString());
        stmt.setString(2, TipoServico.INSPECAO.asString());
        stmt.setString(3, TipoServico.MOVIMENTACAO.asString());
        stmt.setLong(4, codUnidade);
        stmt.setDate(5, new java.sql.Date(dataInicial));
        stmt.setDate(6, new java.sql.Date(dataFinal));
        return stmt;
    }

    static PreparedStatement getQuantidadeServicosFechadosVeiculo(Long codUnidade,
                                                                  long dataInicial,
                                                                  long dataFinal,
                                                                  Connection connection) throws SQLException {
        final PreparedStatement stmt = connection.prepareStatement("SELECT " +
                "  A.PLACA_VEICULO, " +
                "  SUM(CASE WHEN AM.TIPO_SERVICO = ? THEN 1 ELSE 0 END) AS TOTAL_CALIBRAGENS, " +
                "  SUM(CASE WHEN AM.TIPO_SERVICO = ? THEN 1 ELSE 0 END) AS TOTAL_INSPECOES, " +
                "  SUM(CASE WHEN AM.TIPO_SERVICO = ? THEN 1 ELSE 0 END) AS TOTAL_MOVIMENTACOES " +
                "FROM AFERICAO_MANUTENCAO AM " +
                "  JOIN AFERICAO A ON A.CODIGO = AM.COD_AFERICAO " +
                "WHERE AM.COD_UNIDADE = ?" +
                "      AND AM.DATA_HORA_RESOLUCAO IS NOT NULL " +
                "      AND AM.DATA_HORA_RESOLUCAO::DATE BETWEEN ? AND ? " +
                "GROUP BY A.PLACA_VEICULO " +
                "ORDER BY TOTAL_CALIBRAGENS DESC, TOTAL_INSPECOES DESC, TOTAL_MOVIMENTACOES DESC;");
        stmt.setString(1, TipoServico.CALIBRAGEM.asString());
        stmt.setString(2, TipoServico.INSPECAO.asString());
        stmt.setString(3, TipoServico.MOVIMENTACAO.asString());
        stmt.setLong(4, codUnidade);
        stmt.setDate(5, new java.sql.Date(dataInicial));
        stmt.setDate(6, new java.sql.Date(dataFinal));
        return stmt;
    }

    static PreparedStatement getServicoByCod(final Long codUnidade,
                                             final Long codServico,
                                             Connection connection) throws SQLException {
        final PreparedStatement stmt = connection.prepareStatement("SELECT " +
                "   AM.CODIGO AS CODIGO_SERVICO, " +
                "   AM.CPF_MECANICO AS CPF_RESPONSAVEL_FECHAMENTO, " +
                "   AM.DATA_HORA_RESOLUCAO AT TIME ZONE ? AS DATA_HORA_FECHAMENTO, " +
                "   AM.KM_MOMENTO_CONSERTO AS KM_VEICULO_MOMENTO_FECHAMENTO, " +
                "   AM.TEMPO_REALIZACAO_MILLIS AS TEMPO_REALIZACAO_MILLIS, " +
                "   AM.COD_UNIDADE AS COD_UNIDADE, " +
                "   AM.TIPO_SERVICO, " +
                "   AM.QT_APONTAMENTOS, " +
                "   AM.PSI_APOS_CONSERTO AS PRESSAO_COLETADA_FECHAMENTO, " +
                "   AM.COD_ALTERNATIVA AS COD_ALTERNATIVA_SELECIONADA, " +
                "   AM.FECHADO_AUTOMATICAMENTE_MOVIMENTACAO, " +
                "   AAMI.ALTERNATIVA AS DESCRICAO_ALTERNATIVA_SELECIONADA, " +
                "   M.COD_PNEU AS COD_PNEU_NOVO, " +
                "   M.SULCO_EXTERNO AS SULCO_EXTERNO_PNEU_NOVO, " +
                "   M.SULCO_CENTRAL_EXTERNO AS SULCO_CENTRAL_EXTERNO_PNEU_NOVO, " +
                "   M.SULCO_CENTRAL_INTERNO AS SULCO_CENTRAL_INTERNO_PNEU_NOVO, " +
                "   M.SULCO_INTERNO AS SULCO_INTERNO_PNEU_NOVO, " +
                "   M.VIDA AS VIDA_PNEU_NOVO, " +
                "   A.DATA_HORA AT TIME ZONE ? AS DATA_HORA_ABERTURA, " +
                "   A.PLACA_VEICULO AS PLACA_VEICULO, " +
                "   A.CODIGO AS COD_AFERICAO, " +
                "   C.NOME AS NOME_RESPONSAVEL_FECHAMENTO, " +
                "   AV.COD_PNEU AS COD_PNEU_PROBLEMA, " +
                "   AV.ALTURA_SULCO_EXTERNO AS SULCO_EXTERNO_PNEU_PROBLEMA, " +
                "   AV.ALTURA_SULCO_CENTRAL_EXTERNO AS SULCO_CENTRAL_EXTERNO_PNEU_PROBLEMA, " +
                "   AV.ALTURA_SULCO_CENTRAL_INTERNO AS SULCO_CENTRAL_INTERNO_PNEU_PROBLEMA, " +
                "   AV.ALTURA_SULCO_INTERNO AS SULCO_INTERNO_PNEU_PROBLEMA, " +
                "   AV.PSI AS PRESSAO_PNEU_PROBLEMA, " +
                "   AV.POSICAO AS POSICAO_PNEU_PROBLEMA, " +
                "   AV.VIDA_MOMENTO_AFERICAO AS VIDA_PNEU_PROBLEMA, " +
                "   P.PRESSAO_RECOMENDADA " +
                "   FROM AFERICAO_MANUTENCAO AM " +
                "   JOIN AFERICAO A ON A.CODIGO = AM.COD_AFERICAO " +
                "   JOIN AFERICAO_VALORES AV ON AV.COD_AFERICAO = AM.COD_AFERICAO AND AV.COD_PNEU = AM.COD_PNEU " +
                "   JOIN UNIDADE U ON U.CODIGO = AM.COD_UNIDADE " +
                "   JOIN PNEU P ON AM.COD_UNIDADE = P.COD_UNIDADE AND AM.COD_PNEU = P.CODIGO " +
                "   LEFT JOIN MOVIMENTACAO M ON M.COD_MOVIMENTACAO_PROCESSO = AM.COD_PROCESSO_MOVIMENTACAO " +
                "   AND M.COD_PNEU = AM.COD_PNEU_INSERIDO " +
                "   LEFT JOIN AFERICAO_ALTERNATIVA_MANUTENCAO_INSPECAO AAMI ON AAMI.CODIGO = AM.COD_ALTERNATIVA " +
                "   LEFT JOIN COLABORADOR C ON AM.CPF_MECANICO = C.CPF " +
                "   WHERE AM.COD_UNIDADE = ? AND AM.CODIGO = ?;");
        final ZoneId zoneId = TimeZoneManager.getZoneIdForCodUnidade(codUnidade, connection);
        stmt.setString(1, zoneId.getId());
        stmt.setString(2, zoneId.getId());
        stmt.setLong(3, codUnidade);
        stmt.setLong(4, codServico);
        return stmt;
    }

    static PreparedStatement getServicosFechados(final Long codUnidade,
                                                 final long dataInicial,
                                                 final long dataFinal,
                                                 final Connection connection) throws SQLException {
        final PreparedStatement stmt = connection.prepareStatement(BASE_QUERY_BUSCA_SERVICOS
                + "WHERE AM.COD_UNIDADE = ? "
                + "AND AM.DATA_HORA_RESOLUCAO IS NOT NULL "
                + "AND AM.DATA_HORA_RESOLUCAO::DATE BETWEEN ? AND ? "
                + "ORDER BY DATA_HORA_RESOLUCAO DESC;");
        stmt.setLong(1, codUnidade);
        stmt.setDate(2, new Date(dataInicial));
        stmt.setDate(3, new Date(dataFinal));
        return stmt;
    }


    static PreparedStatement getServicosFechadosPneu(final Long codUnidade,
                                                     final String codPneu,
                                                     final long dataInicial,
                                                     final long dataFinal,
                                                     Connection connection) throws SQLException {
        final PreparedStatement stmt = connection.prepareStatement(BASE_QUERY_BUSCA_SERVICOS
                + "WHERE AM.COD_UNIDADE = ? "
                + "AND AM.COD_PNEU = ? "
                + "AND AM.DATA_HORA_RESOLUCAO IS NOT NULL "
                + "AND AM.DATA_HORA_RESOLUCAO::DATE BETWEEN ? AND ? "
                + "ORDER BY DATA_HORA_RESOLUCAO DESC;");
        stmt.setLong(1, codUnidade);
        stmt.setString(2, codPneu);
        stmt.setDate(3, new Date(dataInicial));
        stmt.setDate(4, new Date(dataFinal));
        return stmt;
    }

    static PreparedStatement getServicosFechadosVeiculo(final Long codUnidade,
                                                        final String placaVeiculo,
                                                        final long dataInicial,
                                                        final long dataFinal,
                                                        Connection connection) throws SQLException {
        final PreparedStatement stmt = connection.prepareStatement(BASE_QUERY_BUSCA_SERVICOS
                + "WHERE AM.COD_UNIDADE = ? "
                + "AND A.PLACA_VEICULO = ? "
                + "AND AM.DATA_HORA_RESOLUCAO IS NOT NULL "
                + "AND AM.DATA_HORA_RESOLUCAO::DATE BETWEEN ? AND ? "
                + "ORDER BY DATA_HORA_RESOLUCAO DESC;");
        stmt.setLong(1, codUnidade);
        stmt.setString(2, placaVeiculo);
        stmt.setDate(3, new Date(dataInicial));
        stmt.setDate(4, new Date(dataFinal));
        return stmt;
    }

    static PreparedStatement getVeiculoAberturaServico(@NotNull final Long codServico,
                                                       @NotNull final String placaVeiculo,
                                                       @NotNull final  Connection connection)
            throws SQLException {
        final PreparedStatement stmt = connection.prepareStatement("SELECT " +
                "  A.PLACA_VEICULO, " +
                "  A.KM_VEICULO AS KM_ABERTURA_SERVICO, " +
                "  AV.COD_PNEU, " +
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
                "    ON V.PLACA = A.PLACA_VEICULO " +
                "WHERE AM.CODIGO = ? " +
                "      AND A.PLACA_VEICULO = ?;");
        stmt.setLong(1, codServico);
        stmt.setString(2, placaVeiculo);
        return stmt;
    }

    static PreparedStatement getAlternativasInspecao(Connection connection) throws SQLException {
        return connection.prepareStatement("SELECT * FROM AFERICAO_ALTERNATIVA_MANUTENCAO_INSPECAO A "
                + "WHERE A.STATUS_ATIVO = TRUE");
    }

    static PreparedStatement fechaCalibragem(ServicoCalibragem servico, Connection connection)
            throws SQLException {
        final PreparedStatement stmt = connection.prepareStatement("UPDATE AFERICAO_MANUTENCAO SET "
                + "DATA_HORA_RESOLUCAO = ?, "
                + "CPF_MECANICO = ?, "
                + "PSI_APOS_CONSERTO = ?, "
                + "KM_MOMENTO_CONSERTO = ?, "
                + "TEMPO_REALIZACAO_MILLIS = ? "
                + "WHERE CODIGO = ? "
                + "AND TIPO_SERVICO = ? "
                + "AND DATA_HORA_RESOLUCAO IS NULL;");
        stmt.setObject(1, OffsetDateTime.now(Clock.systemUTC()));
        stmt.setLong(2, servico.getCpfResponsavelFechamento());
        stmt.setDouble(3, servico.getPressaoColetadaFechamento());
        stmt.setLong(4, servico.getKmVeiculoMomentoFechamento());
        stmt.setLong(5, servico.getTempoRealizacaoServicoInMillis());
        stmt.setLong(6, servico.getCodigo());
        stmt.setString(7, servico.getTipoServico().asString());
        return stmt;
    }

    static PreparedStatement fechaInspecao(ServicoInspecao servico, Connection connection)
            throws SQLException {
        final PreparedStatement stmt = connection.prepareStatement("UPDATE AFERICAO_MANUTENCAO SET "
                + "DATA_HORA_RESOLUCAO = ?, "
                + "CPF_MECANICO = ?, "
                + "PSI_APOS_CONSERTO = ?, "
                + "KM_MOMENTO_CONSERTO = ?, "
                + "COD_ALTERNATIVA = ?, "
                + "TEMPO_REALIZACAO_MILLIS = ? "
                + "WHERE CODIGO = ? "
                + "AND TIPO_SERVICO = ? "
                + "AND DATA_HORA_RESOLUCAO IS NULL;");
        stmt.setObject(1, OffsetDateTime.now(Clock.systemUTC()));
        stmt.setLong(2, servico.getCpfResponsavelFechamento());
        stmt.setDouble(3, servico.getPressaoColetadaFechamento());
        stmt.setLong(4, servico.getKmVeiculoMomentoFechamento());
        stmt.setLong(5, servico.getAlternativaSelecionada().codigo);
        stmt.setLong(6, servico.getTempoRealizacaoServicoInMillis());
        stmt.setLong(7, servico.getCodigo());
        stmt.setString(8, servico.getTipoServico().asString());
        return stmt;
    }

    static PreparedStatement fechaMovimentacao(ServicoMovimentacao servico, Connection connection)
            throws SQLException {
        final PreparedStatement stmt = connection.prepareStatement("UPDATE AFERICAO_MANUTENCAO SET "
                + "DATA_HORA_RESOLUCAO = ?, "
                + "CPF_MECANICO = ?, "
                + "KM_MOMENTO_CONSERTO = ?, "
                + "COD_PROCESSO_MOVIMENTACAO = ?, "
                + "PSI_APOS_CONSERTO = ?, "
                + "TEMPO_REALIZACAO_MILLIS = ?, "
                + "COD_PNEU_INSERIDO = ? "
                + "WHERE CODIGO = ? "
                + "AND TIPO_SERVICO = ? "
                + "AND DATA_HORA_RESOLUCAO IS NULL;");
        stmt.setObject(1, OffsetDateTime.now(Clock.systemUTC()));
        stmt.setLong(2, servico.getCpfResponsavelFechamento());
        stmt.setLong(3, servico.getKmVeiculoMomentoFechamento());
        stmt.setLong(4, servico.getCodProcessoMovimentacao());
        // Salva também o PSI após o conserto, já que os sulcos são salvos na tabela de movimentaçao.
        stmt.setDouble(5, servico.getPressaoColetadaFechamento());
        stmt.setLong(6, servico.getTempoRealizacaoServicoInMillis());
        stmt.setString(7, servico.getPneuNovo().getCodigo());
        stmt.setLong(8, servico.getCodigo());
        stmt.setString(9, servico.getTipoServico().asString());
        return stmt;
    }

    static PreparedStatement getQuantidadeServicosEmAbertoPneu(Long codUnidade, String codPneu, Connection conn)
            throws SQLException {
        final PreparedStatement stmt = conn.prepareStatement("SELECT COUNT(CODIGO) AS QTD_SERVICOS_ABERTOS FROM " +
                "AFERICAO_MANUTENCAO AM " +
                "WHERE AM.COD_UNIDADE = ? AND AM.COD_PNEU = ? AND DATA_HORA_RESOLUCAO IS NULL;");
        stmt.setLong(1, codUnidade);
        stmt.setString(2, codPneu);
        return stmt;
    }

    static PreparedStatement fecharAutomaticamenteServicosPneu(final Long codUnidade,
                                                               final String codPneu,
                                                               final Long codProcessoMovimentacao,
                                                               Connection conn) throws SQLException {
        final PreparedStatement stmt = conn.prepareStatement("UPDATE AFERICAO_MANUTENCAO SET "
                + "DATA_HORA_RESOLUCAO = ?, "
                + "COD_PROCESSO_MOVIMENTACAO = ?, "
                + "FECHADO_AUTOMATICAMENTE_MOVIMENTACAO = TRUE "
                + "WHERE COD_UNIDADE = ? "
                + "AND COD_PNEU = ? "
                + "AND DATA_HORA_RESOLUCAO IS NULL;");
        stmt.setObject(1, OffsetDateTime.now(Clock.systemUTC()));
        stmt.setLong(2, codProcessoMovimentacao);
        stmt.setLong(3, codUnidade);
        stmt.setString(4, codPneu);
        return stmt;
    }
}