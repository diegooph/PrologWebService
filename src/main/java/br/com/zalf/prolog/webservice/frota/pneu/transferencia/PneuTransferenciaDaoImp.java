package br.com.zalf.prolog.webservice.frota.pneu.transferencia;

import br.com.zalf.prolog.webservice.commons.util.PostgresUtils;
import br.com.zalf.prolog.webservice.commons.util.SqlType;
import br.com.zalf.prolog.webservice.commons.util.date.Now;
import br.com.zalf.prolog.webservice.frota.pneu.pneu.model.Sulcos;
import br.com.zalf.prolog.webservice.frota.pneu.transferencia.model.listagem.PneuTransferenciaListagem;
import br.com.zalf.prolog.webservice.frota.pneu.transferencia.model.realizacao.PneuTransferenciaRealizacao;
import br.com.zalf.prolog.webservice.frota.pneu.transferencia.model.visualizacao.PneuTransferenciaInformacoes;
import br.com.zalf.prolog.webservice.frota.pneu.transferencia.model.visualizacao.PneuTransferenciaProcessoVisualizacao;
import org.jetbrains.annotations.NotNull;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static br.com.zalf.prolog.webservice.database.DatabaseConnection.close;
import static br.com.zalf.prolog.webservice.database.DatabaseConnection.getConnection;

/**
 * Created on 07/12/18.
 *
 * @author Thais Francisco (https://github.com/thaisksf)
 */
public final class PneuTransferenciaDaoImp implements PneuTransferenciaDao {

    @Override
    public void insertTransferencia(@NotNull final PneuTransferenciaRealizacao pneuTransferenciaRealizacao,
                                    @NotNull final PneuTransferenciaDao pneuTransferenciaDao) throws Throwable {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rSet = null;
        try {
            conn = getConnection();
            conn.setAutoCommit(false);
            stmt = conn.prepareStatement("INSERT INTO PNEU_TRANSFERENCIA_PROCESSO" +
                    "(COD_UNIDADE_ORIGEM," +
                    " COD_UNIDADE_DESTINO," +
                    " COD_COLABORADOR," +
                    " DATA_HORA_TRANSFERENCIA_PROCESSO," +
                    " OBSERVACAO) VALUES (?, ?, ?, ?, ?) RETURNING CODIGO");
            stmt.setLong(1, pneuTransferenciaRealizacao.getCodUnidadeOrigem());
            stmt.setLong(2, pneuTransferenciaRealizacao.getCodUnidadeDestino());
            stmt.setLong(3, pneuTransferenciaRealizacao.getCodColaboradorRealizacaoTransferencia());
            stmt.setObject(4, Now.offsetDateTimeUtc());
            stmt.setString(5, pneuTransferenciaRealizacao.getObservacao());

            rSet = stmt.executeQuery();
            if (rSet.next()) {
                final Long codTransferencia = rSet.getLong("CODIGO");
                insertTransferenciaValores(conn, pneuTransferenciaRealizacao, codTransferencia);
                conn.commit();
            } else {
                throw new IllegalStateException("Erro ao realizar transferência de pneus");
            }
        } catch (final Throwable e) {
            if (conn != null) {
                conn.rollback();
            }
            throw e;
        } finally {
            close(conn, stmt, rSet);
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
            stmt = conn.prepareStatement("SELECT * FROM FUNC_PNEU_TRANSFERENCIA_LISTAGEM(?,?,?,?)");
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
        } catch (Throwable t) {
            throw new SQLException(t);
        } finally {
            close(conn, stmt, rSet);
        }
    }

    @NotNull
    @Override
    public PneuTransferenciaProcessoVisualizacao getVisualizacao(@NotNull final Long codTransferenciaProcesso)
            throws Throwable {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rSet = null;
        try {
            conn = getConnection();
            stmt = conn.prepareStatement("SELECT * FROM FUNC_PNEU_TRANSFERENCIA_VISUALIZACAO(?);");
            stmt.setLong(1, codTransferenciaProcesso);
            rSet = stmt.executeQuery();
            final PneuTransferenciaProcessoVisualizacao processoVisualizacao = new PneuTransferenciaProcessoVisualizacao();
            if (rSet.next()) {
                processoVisualizacao.setCodProcessoTransferencia(rSet.getLong("COD_TRANSFERENCIA"));
                processoVisualizacao.setNomeRegionalOrigem(rSet.getString(("REGIONAL_ORIGEM")));
                processoVisualizacao.setNomeUnidadeOrigem(rSet.getString("UNIDADE_ORIGEM"));
                processoVisualizacao.setNomeRegionalDestino(rSet.getString("REGIONAL_DESTINO"));
                processoVisualizacao.setNomeUnidadeDestino(rSet.getString("UNIDADE_DESTINO"));
                processoVisualizacao.setNomeColaboradorRealizacaoTransferencia(rSet.getString("NOME_COLABORADOR"));
                processoVisualizacao.setDataHoraTransferencia(rSet.getObject("DATA_HORA_TRANSFERENCIA", LocalDateTime.class));
                processoVisualizacao.setObservacao(rSet.getString("OBSERVACAO"));
                processoVisualizacao.setPneusTransferidos(createPneuTransferenciaInformacoes(conn, codTransferenciaProcesso));
            } else {
                throw new IllegalStateException("Erro ao buscar processo de transferência de pneus de código: "
                        + codTransferenciaProcesso);
            }
            return processoVisualizacao;
        } catch (Throwable t) {
            throw new SQLException(t);
        } finally {
            close(conn, stmt, rSet);
        }
    }

    private void insertTransferenciaValores(@NotNull final Connection conn,
                                            @NotNull final PneuTransferenciaRealizacao pneuTransferenciaRealizacao,
                                            @NotNull final Long codTransferencia) throws Throwable {
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

    private void updateUnidadeAlocacaoPneu(@NotNull final Connection conn,
                                           @NotNull final PneuTransferenciaRealizacao pneuTransferenciaRealizacao)
            throws Throwable {
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
            stmt = conn.prepareStatement("SELECT PTI.*, P.CODIGO_CLIENTE FROM PNEU_TRANSFERENCIA_INFORMACOES PTI " +
                    "LEFT JOIN PNEU P ON PTI.COD_PNEU = P.CODIGO WHERE PTI.COD_TRANSFERENCIA = ?;");
            stmt.setLong(1, codTransferenciaProcesso);
            rSet = stmt.executeQuery();
            final List<PneuTransferenciaInformacoes> transferenciaInformacoes = new ArrayList<>();
            while (rSet.next()) {
                final PneuTransferenciaInformacoes pneuTransferenciaInformacoes = new PneuTransferenciaInformacoes();
                pneuTransferenciaInformacoes.setCodPneuCliente(rSet.getString("CODIGO_CLIENTE"));
                pneuTransferenciaInformacoes.setCodPneuTransferenciaInformacoes(rSet.getLong("CODIGO"));
                pneuTransferenciaInformacoes.setVidaMomentoTransferencia(rSet.getInt("VIDA_MOMENTO_TRANSFERENCIA"));
                pneuTransferenciaInformacoes.setPressaoMomentoTransferencia(rSet.getDouble("PSI"));
                final Sulcos sulcos = new Sulcos();
                sulcos.setInterno(rSet.getDouble("ALTURA_SULCO_INTERNO"));
                sulcos.setCentralInterno(rSet.getDouble("ALTURA_SULCO_CENTRAL_INTERNO"));
                sulcos.setCentralExterno(rSet.getDouble("ALTURA_SULCO_CENTRAL_EXTERNO"));
                sulcos.setExterno(rSet.getDouble("ALTURA_SULCO_EXTERNO"));
                pneuTransferenciaInformacoes.setSulcosMomentoTransferencia(sulcos);
                transferenciaInformacoes.add(pneuTransferenciaInformacoes);
            }
            return transferenciaInformacoes;
        } finally {
            close(stmt, rSet);
        }
    }

    @NotNull
    private PneuTransferenciaListagem createPneuTransferenciaListagem(@NotNull final Connection conn,
                                                                      @NotNull final ResultSet rSet) throws Throwable {
        final PneuTransferenciaListagem pneuTransferenciaListagem = new PneuTransferenciaListagem();
        pneuTransferenciaListagem.setCodTransferenciaProcesso(rSet.getLong("COD_TRANSFERENCIA"));
        pneuTransferenciaListagem.setNomeRegionalOrigem(rSet.getString("REGIONAL_ORIGEM"));
        pneuTransferenciaListagem.setNomeUnidadeOrigem(rSet.getString("UNIDADE_ORIGEM"));
        pneuTransferenciaListagem.setNomeRegionalDestino(rSet.getString("REGIONAL_DESTINO"));
        pneuTransferenciaListagem.setNomeUnidadeDestino(rSet.getString("UNIDADE_DESTINO"));
        pneuTransferenciaListagem.setNomeColaboradorRealizacaoTransferencia(rSet.getString("NOME_COLABORADOR"));
        pneuTransferenciaListagem.setDataHoraTransferenciaProcesso(rSet.getObject("DATA_HORA_TRANSFERENCIA", LocalDateTime.class));
        pneuTransferenciaListagem.setObservacaoTransferenciaProcesso(rSet.getString("OBSERVACAO"));
        pneuTransferenciaListagem.setCodPneusCliente(createPneusTransferidos(conn, pneuTransferenciaListagem.getCodTransferenciaProcesso()));
        return pneuTransferenciaListagem;
    }

    @NotNull
    private List<String> createPneusTransferidos(@NotNull final Connection conn,
                                                 @NotNull final Long codTransferenciaProcesso) throws Throwable {
        PreparedStatement stmt = null;
        ResultSet rSet = null;
        try {
            stmt = conn.prepareStatement("SELECT P.CODIGO_CLIENTE FROM PNEU P WHERE P.CODIGO IN " +
                    "(SELECT PTI.COD_PNEU FROM PNEU_TRANSFERENCIA_INFORMACOES PTI WHERE PTI.COD_TRANSFERENCIA = ?);");
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