package br.com.zalf.prolog.webservice.frota.pneu.servico;

import br.com.zalf.prolog.webservice.frota.pneu.servico.model.*;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.sql.*;

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
            + "AM.DATA_HORA_RESOLUCAO AS DATA_HORA_FECHAMENTO, "
            + "AM.KM_MOMENTO_CONSERTO AS KM_VEICULO_MOMENTO_FECHAMENTO, "
            + "AM.TEMPO_REALIZACAO_MILLIS AS TEMPO_REALIZACAO_MILLIS, "
            + "AM.COD_UNIDADE AS COD_UNIDADE, "
            + "AM.TIPO_SERVICO, "
            + "AM.QT_APONTAMENTOS, "
            + "AM.PSI_APOS_CONSERTO AS PRESSAO_COLETADA_FECHAMENTO, "
            + "A.DATA_HORA AS DATA_HORA_ABERTURA, "
            + "A.PLACA_VEICULO AS PLACA_VEICULO, "
            + "A.CODIGO AS COD_AFERICAO, "
            + "AV.POSICAO, "
            + "MAP.NOME AS MARCA, "
            + "MAP.CODIGO AS COD_MARCA, "
            + "MP.NOME AS MODELO, "
            + "MP.CODIGO AS COD_MODELO, "
            + "MP.QT_SULCOS AS QT_SULCOS_MODELO, "
            + "C.NOME AS NOME_RESPONSAVEL_FECHAMENTO, "
            + "DP.ALTURA, "
            + "DP.LARGURA, "
            + "DP.ARO, "
            + "P.*, "
            + "MB.codigo AS COD_MODELO_BANDA, "
            + "MB.nome AS NOME_MODELO_BANDA, "
            + "MB.QT_SULCOS AS QT_SULCOS_BANDA, "
            + "MAB.codigo AS COD_MARCA_BANDA, "
            + "MAB.nome AS NOME_MARCA_BANDA "
            + "FROM AFERICAO_MANUTENCAO AM "
            + "LEFT JOIN COLABORADOR C ON AM.CPF_MECANICO = C.CPF "
            + "JOIN PNEU P ON AM.COD_PNEU = P.CODIGO "
            + "JOIN MODELO_PNEU MP ON MP.CODIGO = P.COD_MODELO "
            + "JOIN MARCA_PNEU MAP ON MAP.CODIGO = MP.COD_MARCA "
            + "JOIN DIMENSAO_PNEU DP ON DP.CODIGO = P.COD_DIMENSAO "
            + "JOIN AFERICAO A ON A.CODIGO = AM.COD_AFERICAO "
            + "JOIN AFERICAO_VALORES AV ON AV.COD_AFERICAO = AM.COD_AFERICAO AND AV.COD_PNEU = AM.COD_PNEU "
            + "JOIN UNIDADE U ON U.CODIGO = P.cod_unidade "
            + "LEFT JOIN modelo_banda MB ON MB.codigo = P.cod_modelo_banda AND MB.cod_empresa = U.cod_empresa "
            + "LEFT JOIN marca_banda MAB ON MAB.codigo = MB.cod_marca AND MAB.cod_empresa = MB.cod_empresa ";

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
                "GROUP BY A.PLACA_VEICULO;");
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
                "GROUP BY AM.COD_PNEU;");
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
                "GROUP BY A.PLACA_VEICULO;");
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
        final PreparedStatement stmt = connection.prepareStatement("SELECT "
                + "AM.CODIGO AS CODIGO_SERVICO, "
                + "AM.CPF_MECANICO AS CPF_RESPONSAVEL_FECHAMENTO, "
                + "AM.DATA_HORA_RESOLUCAO AS DATA_HORA_FECHAMENTO, "
                + "AM.KM_MOMENTO_CONSERTO AS KM_VEICULO_MOMENTO_FECHAMENTO, "
                + "AM.TEMPO_REALIZACAO_MILLIS AS TEMPO_REALIZACAO_MILLIS, "
                + "AM.COD_UNIDADE AS COD_UNIDADE, "
                + "AM.TIPO_SERVICO, "
                + "AM.QT_APONTAMENTOS, "
                + "AM.PSI_APOS_CONSERTO AS PRESSAO_COLETADA_FECHAMENTO, "
                + "AM.COD_ALTERNATIVA AS COD_ALTERNATIVA_SELECIONADA, "
                + "AAMI.ALTERNATIVA AS DESCRICAO_ALTERNATIVA_SELECIONADA, "
                + "M.SULCO_EXTERNO AS SULCO_EXTERNO_PNEU_NOVO, "
                + "M.SULCO_CENTRAL_EXTERNO AS SULCO_CENTRAL_EXTERNO_PNEU_NOVO, "
                + "M.SULCO_CENTRAL_INTERNO AS SULCO_CENTRAL_INTERNO_PNEU_NOVO, "
                + "M.SULCO_INTERNO AS SULCO_INTERNO_PNEU_NOVO, "
                + "M.VIDA AS VIDA_PNEU_NOVO, "
                + "A.DATA_HORA AS DATA_HORA_ABERTURA, "
                + "A.PLACA_VEICULO AS PLACA_VEICULO, "
                + "A.CODIGO AS COD_AFERICAO, "
                + "AV.POSICAO, "
                + "MAP.NOME AS MARCA, "
                + "MAP.CODIGO AS COD_MARCA, "
                + "MP.NOME AS MODELO, "
                + "MP.CODIGO AS COD_MODELO, "
                + "MP.QT_SULCOS AS QT_SULCOS_MODELO, "
                + "C.NOME AS NOME_RESPONSAVEL_FECHAMENTO, "
                + "DP.ALTURA, "
                + "DP.LARGURA, "
                + "DP.ARO, "
                + "P.*, "
                + "MB.codigo AS COD_MODELO_BANDA, "
                + "MB.nome AS NOME_MODELO_BANDA, "
                + "MB.QT_SULCOS AS QT_SULCOS_BANDA, "
                + "MAB.codigo AS COD_MARCA_BANDA, "
                + "MAB.nome AS NOME_MARCA_BANDA "
                + "FROM AFERICAO_MANUTENCAO AM "
                + "JOIN PNEU P ON AM.COD_PNEU = P.CODIGO "
                + "JOIN MODELO_PNEU MP ON MP.CODIGO = P.COD_MODELO "
                + "JOIN MARCA_PNEU MAP ON MAP.CODIGO = MP.COD_MARCA "
                + "JOIN DIMENSAO_PNEU DP ON DP.CODIGO = P.COD_DIMENSAO "
                + "JOIN AFERICAO A ON A.CODIGO = AM.COD_AFERICAO "
                + "JOIN AFERICAO_VALORES AV ON AV.COD_AFERICAO = AM.COD_AFERICAO AND AV.COD_PNEU = AM.COD_PNEU "
                + "JOIN UNIDADE U ON U.CODIGO = P.cod_unidade "
                + "LEFT JOIN MOVIMENTACAO M ON M.COD_MOVIMENTACAO_PROCESSO = AM.COD_PROCESSO_MOVIMENTACAO "
                + "AND M.COD_PNEU = AM.COD_PNEU "
                + "LEFT JOIN AFERICAO_ALTERNATIVA_MANUTENCAO_INSPECAO AAMI ON AAMI.CODIGO = AM.COD_ALTERNATIVA "
                + "LEFT JOIN COLABORADOR C ON AM.CPF_MECANICO = C.CPF "
                + "LEFT JOIN modelo_banda MB ON MB.codigo = P.cod_modelo_banda AND MB.cod_empresa = U.cod_empresa "
                + "LEFT JOIN marca_banda MAB ON MAB.codigo = MB.cod_marca AND MAB.cod_empresa = MB.cod_empresa "
                + "WHERE AM.COD_UNIDADE = ? AND "
                + "AM.CODIGO = ?;");
        stmt.setLong(1, codUnidade);
        stmt.setLong(2, codServico);
        return stmt;
    }

    static PreparedStatement getServicosFechados(final Long codUnidade,
                                                 final long dataInicial,
                                                 final long dataFinal,
                                                 final Connection connection) throws SQLException {
        final PreparedStatement stmt = connection.prepareStatement(BASE_QUERY_BUSCA_SERVICOS
                + "WHERE AM.COD_UNIDADE = ? "
                + "AND AM.DATA_HORA_RESOLUCAO IS NOT NULL "
                + "AND AM.DATA_HORA_RESOLUCAO::DATE BETWEEN ? AND ?;");
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
                + "AND AM.DATA_HORA_RESOLUCAO::DATE BETWEEN ? AND ?;");
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
                + "AND AM.DATA_HORA_RESOLUCAO::DATE BETWEEN ? AND ?;");
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
                "  A.KM_VEICULO, " +
                "  AV.COD_PNEU, " +
                "  AV.ALTURA_SULCO_EXTERNO, " +
                "  AV.ALTURA_SULCO_CENTRAL_EXTERNO, " +
                "  AV.ALTURA_SULCO_CENTRAL_INTERNO, " +
                "  AV.ALTURA_SULCO_INTERNO, " +
                "  AV.PSI, " +
                "  AV.POSICAO, " +
                "  AV.VIDA_MOMENTO_AFERICAO " +
                "FROM AFERICAO_MANUTENCAO AM " +
                "  JOIN AFERICAO A " +
                "    ON AM.COD_AFERICAO = A.CODIGO " +
                "  JOIN AFERICAO_VALORES AV " +
                "    ON AM.COD_AFERICAO = AV.COD_AFERICAO " +
                "       AND A.CODIGO = AV.COD_AFERICAO " +
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

    static PreparedStatement insertCalibragem(ServicoCalibragem servico, Connection connection)
            throws SQLException {
        final PreparedStatement stmt = connection.prepareStatement("UPDATE AFERICAO_MANUTENCAO SET "
                + "DATA_HORA_RESOLUCAO = ?, "
                + "CPF_MECANICO = ?, "
                + "PSI_APOS_CONSERTO = ?, "
                + "KM_MOMENTO_CONSERTO = ?, "
                + "TEMPO_REALIZACAO_MILLIS = ? "
                + "WHERE COD_AFERICAO = ? AND "
                + "DATA_HORA_RESOLUCAO IS NULL AND "
                + "COD_PNEU = ? "
                + "AND TIPO_SERVICO = ?");
        stmt.setTimestamp(1, new Timestamp(System.currentTimeMillis()));
        stmt.setLong(2, servico.getCpfResponsavelFechamento());
        stmt.setDouble(3, servico.getPressaoColetadaFechamento());
        stmt.setLong(4, servico.getKmVeiculoMomentoFechamento());
        stmt.setLong(5, servico.getTempoRealizacaoServicoInMillis());
        stmt.setLong(6, servico.getCodAfericao());
        stmt.setString(7, servico.getPneuComProblema().getCodigo());
        stmt.setString(8, servico.getTipoServico().asString());
        return stmt;
    }

    static PreparedStatement insertInspecao(ServicoInspecao servico, Connection connection)
            throws SQLException {
        final PreparedStatement stmt = connection.prepareStatement("UPDATE AFERICAO_MANUTENCAO SET "
                + "DATA_HORA_RESOLUCAO = ?, "
                + "CPF_MECANICO = ?, "
                + "PSI_APOS_CONSERTO = ?, "
                + "KM_MOMENTO_CONSERTO = ?, "
                + "COD_ALTERNATIVA = ?, "
                + "TEMPO_REALIZACAO_MILLIS = ? "
                + "WHERE COD_AFERICAO = ? AND "
                + "COD_PNEU = ? AND "
                + "DATA_HORA_RESOLUCAO IS NULL "
                + "AND TIPO_SERVICO = ?");
        stmt.setTimestamp(1, new Timestamp(System.currentTimeMillis()));
        stmt.setLong(2, servico.getCpfResponsavelFechamento());
        stmt.setDouble(3, servico.getPressaoColetadaFechamento());
        stmt.setLong(4, servico.getKmVeiculoMomentoFechamento());
        stmt.setLong(5, servico.getAlternativaSelecionada().codigo);
        stmt.setLong(6, servico.getTempoRealizacaoServicoInMillis());
        stmt.setLong(7, servico.getCodAfericao());
        stmt.setString(8, servico.getPneuComProblema().getCodigo());
        stmt.setString(9, servico.getTipoServico().asString());
        return stmt;
    }

    static PreparedStatement insertMovimentacao(ServicoMovimentacao servico, Connection connection)
            throws SQLException {
        final PreparedStatement stmt = connection.prepareStatement("UPDATE AFERICAO_MANUTENCAO SET "
                + "DATA_HORA_RESOLUCAO = ?, "
                + "CPF_MECANICO = ?, "
                + "KM_MOMENTO_CONSERTO = ?, "
                + "COD_PROCESSO_MOVIMENTACAO = ?, "
                + "PSI_APOS_CONSERTO = ?, "
                + "TEMPO_REALIZACAO_MILLIS = ? "
                + "WHERE COD_AFERICAO = ? AND "
                + "COD_PNEU = ? AND "
                + "DATA_HORA_RESOLUCAO IS NULL "
                + "AND TIPO_SERVICO = ?");
        // Salva também o PSI após o conserto, já que os sulcos são salvos na tabela de movimentaçao.
        stmt.setTimestamp(1, new Timestamp(System.currentTimeMillis()));
        stmt.setLong(2, servico.getCpfResponsavelFechamento());
        stmt.setLong(3, servico.getKmVeiculoMomentoFechamento());
        stmt.setLong(4, servico.getCodProcessoMovimentacao());
        stmt.setDouble(5, servico.getPressaoColetadaFechamento());
        stmt.setLong(6, servico.getTempoRealizacaoServicoInMillis());
        stmt.setLong(7, servico.getCodAfericao());
        stmt.setString(8, servico.getPneuComProblema().getCodigo());
        stmt.setString(9, servico.getTipoServico().asString());
        return stmt;
    }
}