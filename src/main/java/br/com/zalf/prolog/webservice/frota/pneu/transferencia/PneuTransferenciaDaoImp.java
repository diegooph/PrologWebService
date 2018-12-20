package br.com.zalf.prolog.webservice.frota.pneu.transferencia;

import br.com.zalf.prolog.webservice.Injection;
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
public class PneuTransferenciaDaoImp implements PneuTransferenciaDao {


    @Override
    public void insertTransferencia(@NotNull final PneuTransferenciaRealizacao pneuTransferenciaRealizacao,
                                    @NotNull final PneuTransferenciaDao pneuTransferenciaDao) throws Throwable {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rSet = null;
        Long codTransferencia = null;
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
                codTransferencia = rSet.getLong("CODIGO");
                insertTransferenciaValores(conn, pneuTransferenciaRealizacao, codTransferencia);
            }
            conn.commit();
        } catch (final Throwable e) {
            if (conn != null) {
                conn.rollback();
            }
            throw e;
        } finally {
            close(conn, stmt, rSet);
        }
    }

    @Override
    public List<PneuTransferenciaListagem> getListagem(@NotNull final List<Long> codUnidadesOrigem,
                                                       @NotNull final List<Long> codUnidadesDestino,
                                                       @NotNull final LocalDate dataInicial,
                                                       @NotNull final LocalDate dataFinal) throws Throwable {
        List<PneuTransferenciaListagem> list = new ArrayList<>();
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rSet = null;
        try {
            conn = getConnection();
            stmt = conn.prepareStatement("SELECT * FROM FUNC_PNEU_TRANSFERENCIA_LISTAGEM(?,?,?,?)");

            stmt.setArray(1, PostgresUtils.listToArray(conn, SqlType.BIGINT, codUnidadesOrigem));
            stmt.setArray(2, PostgresUtils.listToArray(conn, SqlType.BIGINT, codUnidadesDestino));
            stmt.setObject(3, (dataInicial));
            stmt.setObject(4, (dataFinal));

            rSet = stmt.executeQuery();
            while (rSet.next()) {
                final PneuTransferenciaListagem pneuTransferenciaListagem = createPneuTransferenciaListagem(rSet, conn);
                list.add(pneuTransferenciaListagem);
            }
        } catch (Throwable t) {
            throw new SQLException(t);
        } finally {
            close(conn, stmt, rSet);
        }
        return list;
    }

    @Override
    public PneuTransferenciaProcessoVisualizacao getVisualizacao(@NotNull final Long codTransferenciaProcesso)
            throws Throwable {
        PneuTransferenciaProcessoVisualizacao processoVisualizacao = new PneuTransferenciaProcessoVisualizacao();
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rSet = null;
        try {
            conn = getConnection();
            stmt = conn.prepareStatement("SELECT * FROM FUNC_PNEU_TRANSFERENCIA_VISUALIZACAO(?);");

            stmt.setLong(1, codTransferenciaProcesso);

            rSet = stmt.executeQuery();
            if (rSet.next()) {
                processoVisualizacao.setCodProcessoTransferencia(Long.valueOf(rSet.getLong("COD_TRANSFERENCIA")));
                processoVisualizacao.setNomeRegionalOrigem(rSet.getString(("REGIONAL_ORIGEM")));
                processoVisualizacao.setNomeUnidadeOrigem(rSet.getString("UNIDADE_ORIGEM"));
                processoVisualizacao.setNomeRegionalDestino(rSet.getString("REGIONAL_DESTINO"));
                processoVisualizacao.setNomeUnidadeDestino(rSet.getString("UNIDADE_DESTINO"));
                processoVisualizacao.setNomeColaboradorRealizacaoTransferencia(rSet.getString("NOME_COLABORADOR"));
                processoVisualizacao.setDataHoraTransferencia(rSet.getObject("DATA_TRANSFERENCIA", LocalDateTime.class));
                processoVisualizacao.setObservacao(rSet.getString("OBSERVACAO"));
                processoVisualizacao.setPneusTransferidos(createPneuTransferenciaInformacoes(codTransferenciaProcesso));
            }
        } catch (Throwable t) {
            throw new SQLException(t);
        } finally {
            close(conn, stmt, rSet);
        }
        return processoVisualizacao;
    }

    private List<PneuTransferenciaInformacoes> createPneuTransferenciaInformacoes(Long codTransferenciaProcesso)
            throws Throwable {
        List<PneuTransferenciaInformacoes> listPneuTransferenciaInformacoes = new ArrayList<>();
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rSet = null;
        try {
            conn = getConnection();
            stmt = conn.prepareStatement("SELECT PTI.*, P.CODIGO_CLIENTE FROM PNEU_TRANSFERENCIA_INFORMACOES PTI " +
                    "LEFT JOIN PNEU P ON PTI.COD_PNEU = P.CODIGO WHERE PTI.COD_TRANSFERENCIA = ?;");

            stmt.setLong(1, codTransferenciaProcesso);

            rSet = stmt.executeQuery();
            if (rSet.next()) {
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
                listPneuTransferenciaInformacoes.add(pneuTransferenciaInformacoes);
            }
        } finally {
            close(conn, stmt, rSet);
        }
        return listPneuTransferenciaInformacoes;

    }


    private PneuTransferenciaListagem createPneuTransferenciaListagem(ResultSet rSet, Connection conn) throws Throwable {
        final PneuTransferenciaListagem pneuTransferenciaListagem = new PneuTransferenciaListagem();
        pneuTransferenciaListagem.setCodTransferenciaProcesso(rSet.getLong("COD_TRANSFERENCIA"));
        pneuTransferenciaListagem.setNomeRegionalOrigem(rSet.getString("REGIONAL_ORIGEM"));
        pneuTransferenciaListagem.setNomeUnidadeOrigem(rSet.getString("UNIDADE_ORIGEM"));
        pneuTransferenciaListagem.setNomeRegionalDestino(rSet.getString("REGIONAL_DESTINO"));
        pneuTransferenciaListagem.setNomeUnidadeDestino(rSet.getString("UNIDADE_DESTINO"));
        pneuTransferenciaListagem.setNomeColaboradorRealizacaoTransferencia(rSet.getString("NOME_COLABORADOR"));
        pneuTransferenciaListagem.setDataHoraTransferenciaProcesso(rSet.getObject("DATA_TRANSFERENCIA", LocalDateTime.class));
        pneuTransferenciaListagem.setObservacaoTransferenciaProcesso(rSet.getString("OBSERVACAO"));
        pneuTransferenciaListagem.setCodPneusCliente(createPneusTransferidos(conn, pneuTransferenciaListagem.getCodTransferenciaProcesso()));

        return pneuTransferenciaListagem;
    }

    private List<String> createPneusTransferidos(Connection conn, Long codTransferenciaProcesso) throws Throwable {
        List<String> pneusTransferidos = new ArrayList<>();
        PreparedStatement stmt = null;
        ResultSet rSet = null;
        try {
            stmt = conn.prepareStatement("SELECT P.CODIGO_CLIENTE FROM PNEU P WHERE P.CODIGO IN " +
                    "(SELECT PTI.COD_PNEU FROM PNEU_TRANSFERENCIA_INFORMACOES PTI WHERE PTI.COD_TRANSFERENCIA = ?);");

            stmt.setLong(1, codTransferenciaProcesso);

            rSet = stmt.executeQuery();
            while (rSet.next()) {
                final String codPneuCliente = createListPneusTransferidos(rSet);
                pneusTransferidos.add(codPneuCliente);
            }
        } finally {
            close(stmt, rSet);
        }
        return pneusTransferidos;
    }

    private String createListPneusTransferidos(ResultSet rSet) throws Throwable {
        final String pneuTransferido = rSet.getString("CODIGO_CLIENTE");
        return pneuTransferido;
    }

    private void insertTransferenciaValores(@NotNull final Connection conn,
                                            @NotNull final PneuTransferenciaRealizacao pneuTransferenciaRealizacao,
                                            @NotNull final Long codTransferencia) throws Throwable {
        ResultSet rSet = null;
        final PreparedStatement stmt = conn.prepareStatement("SELECT * FROM FUNC_PNEU_INSERT_TRANSFERENCIA_INFORMACOES(?,?)");
        stmt.setLong(1, codTransferencia);
        stmt.setArray(2, PostgresUtils.listToArray(conn, SqlType.BIGINT, pneuTransferenciaRealizacao.getCodPneus()));

        rSet = stmt.executeQuery();
        Long qtdRowsRSet = null;
        if (rSet.next()) {
            qtdRowsRSet = rSet.getLong(1);
        }
        if (qtdRowsRSet == pneuTransferenciaRealizacao.getCodPneus().size()) {
            updateUnidadeAlocacaoPneu(conn, pneuTransferenciaRealizacao);
        } else {
            throw new SQLException("Não foi possível inserir as informações do(s) pneu(s) transferido(s)");
        }
    }

    private void updateUnidadeAlocacaoPneu(@NotNull final Connection conn,
                                           @NotNull final PneuTransferenciaRealizacao pneuTransferenciaRealizacao)
            throws Throwable {
        ResultSet rSet = null;
        final PreparedStatement stmt = conn.prepareStatement("SELECT * FROM FUNC_PNEU_UPDATE_UNIDADE_ALOCACAO(?,?,?)");
        stmt.setLong(1, pneuTransferenciaRealizacao.getCodUnidadeOrigem());
        stmt.setLong(2, pneuTransferenciaRealizacao.getCodUnidadeDestino());
        stmt.setArray(3, PostgresUtils.listToArray(conn, SqlType.BIGINT, pneuTransferenciaRealizacao.getCodPneus()));
        rSet = stmt.executeQuery();
        Long qtdRowsRSet = null;
        if (rSet.next()) {
            qtdRowsRSet = rSet.getLong(1);
        }
        if (qtdRowsRSet != pneuTransferenciaRealizacao.getCodPneus().size()) {
            throw new SQLException("Não foi possível inserir as informações do(s) pneu(s) transferido(s)");
        }
    }
}