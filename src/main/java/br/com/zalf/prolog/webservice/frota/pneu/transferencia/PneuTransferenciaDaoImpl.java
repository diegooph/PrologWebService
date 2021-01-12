package br.com.zalf.prolog.webservice.frota.pneu.transferencia;

import br.com.zalf.prolog.webservice.commons.util.database.PostgresUtils;
import br.com.zalf.prolog.webservice.commons.util.database.SqlType;
import br.com.zalf.prolog.webservice.frota.pneu._model.Sulcos;
import br.com.zalf.prolog.webservice.frota.veiculo.transferencia.model.LinkTransferenciaVeiculo;
import br.com.zalf.prolog.webservice.frota.pneu.transferencia._model.TipoProcessoTransferenciaPneu;
import br.com.zalf.prolog.webservice.frota.pneu.transferencia._model.listagem.PneuTransferenciaListagem;
import br.com.zalf.prolog.webservice.frota.pneu.transferencia._model.realizacao.PneuTransferenciaRealizacao;
import br.com.zalf.prolog.webservice.frota.pneu.transferencia._model.visualizacao.PneuTransferenciaInformacoes;
import br.com.zalf.prolog.webservice.frota.pneu.transferencia._model.visualizacao.PneuTransferenciaProcessoVisualizacao;
import org.jetbrains.annotations.NotNull;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;

import static br.com.zalf.prolog.webservice.database.DatabaseConnection.close;
import static br.com.zalf.prolog.webservice.database.DatabaseConnection.getConnection;

/**
 * Created on 07/12/18.
 *
 * @author Thais Francisco (https://github.com/thaisksf)
 */
public final class PneuTransferenciaDaoImpl implements PneuTransferenciaDao {

    @NotNull
    @Override
    public Long insertTransferencia(
            @NotNull final PneuTransferenciaRealizacao pneuTransferenciaRealizacao,
            @NotNull final OffsetDateTime dataHoraSincronizacao,
            final boolean isTransferenciaFromVeiculo) throws Throwable {
        Connection conn = null;
        try {
            conn = getConnection();
            conn.setAutoCommit(false);
            final Long codProcessoTransferencia =
                    insertTransferencia(
                            conn,
                            pneuTransferenciaRealizacao,
                            dataHoraSincronizacao,
                            isTransferenciaFromVeiculo);
            conn.commit();
            return codProcessoTransferencia;
        } catch (final Throwable t) {
            if (conn != null) {
                conn.rollback();
            }
            throw t;
        } finally {
            close(conn);
        }
    }

    @NotNull
    @Override
    public Long insertTransferencia(@NotNull final Connection conn,
                                    @NotNull final PneuTransferenciaRealizacao pneuTransferenciaRealizacao,
                                    @NotNull final OffsetDateTime dataHoraSincronizacao,
                                    final boolean isTransferenciaFromVeiculo) throws Throwable {
        PreparedStatement stmt = null;
        ResultSet rSet = null;
        try {
            stmt = conn.prepareStatement("INSERT INTO PNEU_TRANSFERENCIA_PROCESSO(" +
                    "  COD_UNIDADE_ORIGEM, " +
                    "  COD_UNIDADE_DESTINO, " +
                    "  COD_UNIDADE_COLABORADOR, " +
                    "  COD_COLABORADOR, " +
                    "  DATA_HORA_TRANSFERENCIA_PROCESSO, " +
                    "  OBSERVACAO, " +
                    "  TIPO_PROCESSO_TRANSFERENCIA) " +
                    "VALUES (?, " +
                    "        ?, " +
                    "        (SELECT COD_UNIDADE FROM COLABORADOR WHERE CODIGO = ?), " +
                    "        ?, " +
                    "        ?, " +
                    "        ?, " +
                    "        CAST(? AS TIPO_PROCESSO_TRANSFERENCIA_PNEU)) " +
                    "RETURNING CODIGO;");
            stmt.setLong(1, pneuTransferenciaRealizacao.getCodUnidadeOrigem());
            stmt.setLong(2, pneuTransferenciaRealizacao.getCodUnidadeDestino());
            stmt.setLong(3, pneuTransferenciaRealizacao.getCodColaboradorRealizacaoTransferencia());
            stmt.setLong(4, pneuTransferenciaRealizacao.getCodColaboradorRealizacaoTransferencia());
            stmt.setObject(5, dataHoraSincronizacao);
            stmt.setString(6, pneuTransferenciaRealizacao.getObservacao());
            stmt.setString(7, isTransferenciaFromVeiculo
                    ? TipoProcessoTransferenciaPneu.TRANSFERENCIA_JUNTO_A_VEICULO.asString()
                    : TipoProcessoTransferenciaPneu.TRANSFERENCIA_APENAS_PNEUS.asString());
            rSet = stmt.executeQuery();
            if (rSet.next()) {
                final Long codProcessoTransferencia = rSet.getLong("CODIGO");
                insertTransferenciaValores(conn, codProcessoTransferencia, pneuTransferenciaRealizacao);
                return codProcessoTransferencia;
            } else {
                throw new IllegalStateException("Erro ao realizar transferência de pneus");
            }
        } finally {
            close(stmt, rSet);
        }
    }

    @NotNull
    @Override
    public List<PneuTransferenciaListagem> getListagem(@NotNull final List<Long> codUnidadesOrigem,
                                                       @NotNull final List<Long> codUnidadesDestino,
                                                       @NotNull final LocalDate dataInicial,
                                                       @NotNull final LocalDate dataFinal) throws Throwable {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rSet = null;
        try {
            conn = getConnection();
            stmt = conn.prepareStatement("SELECT * FROM FUNC_PNEU_TRANSFERENCIA_LISTAGEM(?, ?, ?, ?);");
            stmt.setArray(1, PostgresUtils.listToArray(conn, SqlType.BIGINT, codUnidadesOrigem));
            stmt.setArray(2, PostgresUtils.listToArray(conn, SqlType.BIGINT, codUnidadesDestino));
            stmt.setObject(3, dataInicial);
            stmt.setObject(4, dataFinal);
            rSet = stmt.executeQuery();
            final List<PneuTransferenciaListagem> transferencias = new ArrayList<>();
            while (rSet.next()) {
                transferencias.add(createPneuTransferenciaListagem(conn, rSet));
            }
            return transferencias;
        } finally {
            close(conn, stmt, rSet);
        }
    }

    @NotNull
    @Override
    public PneuTransferenciaProcessoVisualizacao getVisualizacao(
            @NotNull final Long codTransferenciaProcesso) throws Throwable {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rSet = null;
        try {
            conn = getConnection();
            stmt = conn.prepareStatement("SELECT * FROM FUNC_PNEU_TRANSFERENCIA_VISUALIZACAO(?);");
            stmt.setLong(1, codTransferenciaProcesso);
            rSet = stmt.executeQuery();
            if (rSet.next()) {
                final long codProcessoTransferenciaVeiculo =
                        rSet.getLong("COD_PROCESSO_TRANSFERENCIA_VEICULO");
                LinkTransferenciaVeiculo linkPlacaTransferida = null;
                if (codProcessoTransferenciaVeiculo > 0) {
                    linkPlacaTransferida = new LinkTransferenciaVeiculo(
                            codProcessoTransferenciaVeiculo,
                            rSet.getString("PLACA_TRANSFERIDA"));
                }
                return new PneuTransferenciaProcessoVisualizacao(
                        rSet.getLong("COD_PROCESSO_TRANSFERENCIA_PNEU"),
                        rSet.getString("NOME_COLABORADOR"),
                        rSet.getString("REGIONAL_ORIGEM"),
                        rSet.getString("UNIDADE_ORIGEM"),
                        rSet.getString("REGIONAL_DESTINO"),
                        rSet.getString("UNIDADE_DESTINO"),
                        createPneuTransferenciaInformacoes(conn, codTransferenciaProcesso),
                        rSet.getString("OBSERVACAO"),
                        rSet.getObject("DATA_HORA_TRANSFERENCIA", LocalDateTime.class),
                        linkPlacaTransferida);
            } else {
                throw new IllegalStateException("Erro ao buscar processo de transferência de pneus de código: "
                        + codTransferenciaProcesso);
            }
        } finally {
            close(conn, stmt, rSet);
        }
    }

    private void insertTransferenciaValores(@NotNull final Connection conn,
                                            @NotNull final Long codTransferencia,
                                            @NotNull final PneuTransferenciaRealizacao pneuTransferenciaRealizacao) throws Throwable {
        PreparedStatement stmt = null;
        ResultSet rSet = null;
        try {
            stmt = conn.prepareStatement("SELECT * FROM FUNC_PNEU_TRANSFERENCIA_INSERT_INFORMACOES(?, ?)");
            stmt.setLong(1, codTransferencia);
            stmt.setArray(2, PostgresUtils.listToArray(conn, SqlType.BIGINT, pneuTransferenciaRealizacao.getCodPneus()));
            rSet = stmt.executeQuery();

            long insertCount = -1;
            if (rSet.next()) {
                insertCount = rSet.getLong(1);
            }
            if (insertCount == pneuTransferenciaRealizacao.getCodPneus().size()) {
                updateUnidadeAlocacaoPneu(conn, pneuTransferenciaRealizacao);
            } else {
                throw new SQLException("Não foi possível inserir as informações do(s) pneu(s) transferido(s)");
            }
        } finally {
            close(stmt, rSet);
        }
    }

    private void updateUnidadeAlocacaoPneu(
            @NotNull final Connection conn,
            @NotNull final PneuTransferenciaRealizacao pneuTransferenciaRealizacao) throws Throwable {
        PreparedStatement stmt = null;
        ResultSet rSet = null;
        try {
            stmt = conn.prepareStatement("SELECT * FROM FUNC_PNEU_TRANSFERENCIA_ALTERAR_UNIDADE_ALOCADO(?, ?, ?)");
            stmt.setLong(1, pneuTransferenciaRealizacao.getCodUnidadeOrigem());
            stmt.setLong(2, pneuTransferenciaRealizacao.getCodUnidadeDestino());
            stmt.setArray(3, PostgresUtils.listToArray(conn, SqlType.BIGINT, pneuTransferenciaRealizacao.getCodPneus()));
            rSet = stmt.executeQuery();

            long updateCount = -1;
            if (rSet.next()) {
                updateCount = rSet.getLong(1);
            }
            if (updateCount != pneuTransferenciaRealizacao.getCodPneus().size()) {
                throw new SQLException("Não foi possível atualizar a unidade alocado do(s) pneu(s) transferido(s)");
            }
        } finally {
            close(stmt, rSet);
        }
    }

    @NotNull
    private List<PneuTransferenciaInformacoes> createPneuTransferenciaInformacoes(
            @NotNull final Connection conn,
            @NotNull final Long codTransferenciaProcesso) throws Throwable {
        ResultSet rSet = null;
        PreparedStatement stmt = null;
        try {
            stmt = conn.prepareStatement("SELECT " +
                    "  PTI.CODIGO, " +
                    "  PTI.ALTURA_SULCO_INTERNO, " +
                    "  PTI.ALTURA_SULCO_CENTRAL_INTERNO, " +
                    "  PTI.ALTURA_SULCO_CENTRAL_EXTERNO, " +
                    "  PTI.ALTURA_SULCO_EXTERNO, " +
                    "  PTI.PSI AS PRESSAO_PNEU, " +
                    "  PTI.VIDA_MOMENTO_TRANSFERENCIA, " +
                    "  P.CODIGO_CLIENTE " +
                    "FROM PNEU_TRANSFERENCIA_INFORMACOES PTI " +
                    "  LEFT JOIN PNEU P ON PTI.COD_PNEU = P.CODIGO " +
                    "WHERE PTI.COD_PROCESSO_TRANSFERENCIA = ?;");
            stmt.setLong(1, codTransferenciaProcesso);
            rSet = stmt.executeQuery();
            final List<PneuTransferenciaInformacoes> transferenciaInformacoes = new ArrayList<>();
            while (rSet.next()) {
                final Sulcos sulcos = new Sulcos();
                sulcos.setInterno(rSet.getDouble("ALTURA_SULCO_INTERNO"));
                sulcos.setCentralInterno(rSet.getDouble("ALTURA_SULCO_CENTRAL_INTERNO"));
                sulcos.setCentralExterno(rSet.getDouble("ALTURA_SULCO_CENTRAL_EXTERNO"));
                sulcos.setExterno(rSet.getDouble("ALTURA_SULCO_EXTERNO"));
                transferenciaInformacoes.add(new PneuTransferenciaInformacoes(
                        rSet.getLong("CODIGO"),
                        rSet.getString("CODIGO_CLIENTE"),
                        sulcos,
                        rSet.getDouble("PRESSAO_PNEU"),
                        rSet.getInt("VIDA_MOMENTO_TRANSFERENCIA")));
            }
            return transferenciaInformacoes;
        } finally {
            close(stmt, rSet);
        }
    }

    @NotNull
    private PneuTransferenciaListagem createPneuTransferenciaListagem(@NotNull final Connection conn,
                                                                      @NotNull final ResultSet rSet) throws Throwable {
        final long codProcessoTransferenciaPneu = rSet.getLong("COD_PROCESSO_TRANSFERENCIA_PNEU");
        final long codProcessoTransferenciaVeiculo = rSet.getLong("COD_PROCESSO_TRANSFERENCIA_VEICULO");
        LinkTransferenciaVeiculo linkPlacaTransferida = null;
        if (codProcessoTransferenciaVeiculo > 0) {
            linkPlacaTransferida = new LinkTransferenciaVeiculo(
                    codProcessoTransferenciaVeiculo,
                    rSet.getString("PLACA_TRANSFERIDA"));
        }
        return new PneuTransferenciaListagem(
                codProcessoTransferenciaPneu,
                rSet.getString("NOME_COLABORADOR"),
                rSet.getString("REGIONAL_ORIGEM"),
                rSet.getString("UNIDADE_ORIGEM"),
                rSet.getString("REGIONAL_DESTINO"),
                rSet.getString("UNIDADE_DESTINO"),
                createPneusTransferidos(conn, codProcessoTransferenciaPneu),
                rSet.getString("OBSERVACAO"),
                rSet.getObject("DATA_HORA_TRANSFERENCIA", LocalDateTime.class),
                linkPlacaTransferida);
    }

    @NotNull
    private List<String> createPneusTransferidos(@NotNull final Connection conn,
                                                 @NotNull final Long codTransferenciaProcesso) throws Throwable {
        PreparedStatement stmt = null;
        ResultSet rSet = null;
        try {
            stmt = conn.prepareStatement("SELECT P.CODIGO_CLIENTE FROM PNEU P WHERE P.CODIGO IN " +
                    "(SELECT PTI.COD_PNEU FROM PNEU_TRANSFERENCIA_INFORMACOES PTI WHERE PTI.COD_PROCESSO_TRANSFERENCIA = ?);");
            stmt.setLong(1, codTransferenciaProcesso);
            rSet = stmt.executeQuery();
            final List<String> pneusTransferidos = new ArrayList<>();
            while (rSet.next()) {
                pneusTransferidos.add(rSet.getString("CODIGO_CLIENTE"));
            }
            return pneusTransferidos;
        } finally {
            close(stmt, rSet);
        }
    }
}