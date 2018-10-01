package br.com.zalf.prolog.webservice.gente.controleintervalo.ajustes;

import br.com.zalf.prolog.webservice.TimeZoneManager;
import br.com.zalf.prolog.webservice.commons.util.SqlType;
import br.com.zalf.prolog.webservice.commons.util.date.Now;
import br.com.zalf.prolog.webservice.database.DatabaseConnection;
import br.com.zalf.prolog.webservice.gente.controleintervalo.ajustes.model.*;
import br.com.zalf.prolog.webservice.gente.controleintervalo.ajustes.model.exibicao.ConsolidadoMarcacoesDia;
import br.com.zalf.prolog.webservice.gente.controleintervalo.ajustes.model.exibicao.MarcacaoAjusteHistoricoExibicao;
import br.com.zalf.prolog.webservice.gente.controleintervalo.ajustes.model.exibicao.MarcacaoColaboradorAjuste;
import br.com.zalf.prolog.webservice.gente.controleintervalo.ajustes.model.exibicao.MarcacaoInconsistenciaExibicao;
import br.com.zalf.prolog.webservice.gente.controleintervalo.model.TipoInicioFim;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;

import static br.com.zalf.prolog.webservice.commons.util.StatementUtils.bindValueOrNull;

/**
 * Created on 04/09/18.
 *
 * @author Diogenes Vanzela (https://github.com/diogenesvanzella)
 */
public final class ControleJornadaAjusteDaoImpl extends DatabaseConnection implements ControleJornadaAjusteDao {

    @NotNull
    @Override
    public List<ConsolidadoMarcacoesDia> getMarcacoesConsolidadasParaAjuste(
            @NotNull final Long codUnidade,
            @Nullable final Long codTipoMarcacao,
            @Nullable final Long codColaborador,
            @NotNull final LocalDate dataInicial,
            @NotNull final LocalDate dataFinal) throws Throwable {
        PreparedStatement stmt = null;
        ResultSet rSet = null;
        Connection conn = null;
        try {
            conn = getConnection();
            stmt = conn.prepareStatement("SELECT * " +
                    "FROM FUNC_MARCACAO_GET_MARCACOES_CONSOLIDADAS_AJUSTE(?, ?, ?, ?, ?);");
            stmt.setLong(1, codUnidade);
            bindValueOrNull(stmt, 2, codTipoMarcacao, SqlType.BIGINT);
            bindValueOrNull(stmt, 3, codColaborador, SqlType.BIGINT);
            stmt.setObject(4, dataInicial);
            stmt.setObject(5, dataFinal);
            rSet = stmt.executeQuery();
            return ControleJornadaAjusteConverter.createConsolidadoMarcacoesDia(rSet);
        } finally {
            close(stmt, rSet, conn);
        }
    }

    @NotNull
    @Override
    public List<MarcacaoColaboradorAjuste> getMarcacoesColaboradorParaAjuste(
            @NotNull final Long codColaborador,
            @Nullable final Long codTipoMarcacao,
            @NotNull final LocalDate dia) throws Throwable {
        PreparedStatement stmt = null;
        ResultSet rSet = null;
        Connection conn = null;
        try {
            conn = getConnection();
            stmt = conn.prepareStatement("SELECT * FROM FUNC_MARCACAO_GET_MARCACOES_COLABORADOR_AJUSTE(?, ?, ?);");
            stmt.setLong(1, codColaborador);
            bindValueOrNull(stmt, 2, codTipoMarcacao, SqlType.BIGINT);
            stmt.setObject(3, dia);
            rSet = stmt.executeQuery();
            final List<MarcacaoColaboradorAjuste> marcacoes = new ArrayList<>();
            while (rSet.next()) {
                marcacoes.add(ControleJornadaAjusteConverter.createMarcacaoColaboradorAjuste(rSet));
            }
            return marcacoes;
        } finally {
            close(stmt, rSet, conn);
        }
    }

    @Override
    public void adicionarMarcacaoAjuste(@NotNull final String token,
                                        @NotNull final MarcacaoAjusteAdicao marcacaoAjuste) throws Throwable {
        Connection conn = null;
        try {
            conn = getConnection();
            conn.setAutoCommit(false);
            final Long codMarcacaoInserida = insereMarcacaoAjusteAdicao(conn, token, marcacaoAjuste);
            final TipoInicioFim tipoInicioFim = marcacaoAjuste.getTipoInicioFim();
            insereVinculoMarcacaoInicioOuFim(conn, codMarcacaoInserida, tipoInicioFim);
            final Long codMarcacaoInicio = tipoInicioFim.equals(TipoInicioFim.MARCACAO_INICIO)
                    ? codMarcacaoInserida
                    : marcacaoAjuste.getCodMarcacaoVinculo();
            final Long codMarcacaoFim = tipoInicioFim.equals(TipoInicioFim.MARCACAO_FIM)
                    ? codMarcacaoInserida
                    : marcacaoAjuste.getCodMarcacaoVinculo();
            insereVinculoInicioFim(conn, codMarcacaoInicio, codMarcacaoFim);
            insereInformacoesAjusteMarcacao(
                    conn,
                    codMarcacaoInserida,
                    token,
                    marcacaoAjuste,
                    marcacaoAjuste.getDataHoraInserida());
            conn.commit();
        } catch (final Throwable e) {
            if (conn != null) {
                conn.rollback();
            }
            throw e;
        } finally {
            close(conn);
        }
    }

    @Override
    public void adicionarMarcacaoAjusteInicioFim(
            @NotNull final String token,
            @NotNull final MarcacaoAjusteAdicaoInicioFim marcacaoAjuste) throws Throwable {
        Connection conn = null;
        try {
            conn = getConnection();
            conn.setAutoCommit(false);
            final ResultInsertInicioFim codigos = insereMarcacaoAjusteAdicaoInicioFim(conn, token, marcacaoAjuste);
            insereVinculoMarcacaoInicioOuFim(conn, codigos.getCodMarcacaoInicio(), TipoInicioFim.MARCACAO_INICIO);
            insereVinculoMarcacaoInicioOuFim(conn, codigos.getCodMarcacaoFim(), TipoInicioFim.MARCACAO_FIM);
            insereVinculoInicioFim(conn, codigos.getCodMarcacaoInicio(), codigos.getCodMarcacaoFim());
            insereInformacoesAjusteMarcacao(
                    conn,
                    codigos.getCodMarcacaoInicio(),
                    token,
                    marcacaoAjuste,
                    null);
            insereInformacoesAjusteMarcacao(
                    conn,
                    codigos.getCodMarcacaoFim(),
                    token,
                    marcacaoAjuste,
                    null);
            conn.commit();
        } catch (final Throwable e) {
            if (conn != null) {
                conn.rollback();
            }
            throw e;
        } finally {
            close(conn);
        }
    }

    @Override
    public void editarMarcacaoAjuste(@NotNull final String token,
                                     @NotNull final MarcacaoAjusteEdicao marcacaoAjuste) throws Throwable {
        Connection conn = null;
        try {
            conn = getConnection();
            conn.setAutoCommit(false);
            insereInformacoesAjusteMarcacao(
                    conn,
                    marcacaoAjuste.getCodMarcacaoEdicao(),
                    token,
                    marcacaoAjuste,
                    marcacaoAjuste.getDataHoraNovaInserida());
            conn.commit();
        } catch (final Throwable e) {
            if (conn != null) {
                conn.rollback();
            }
            throw e;
        } finally {
            close(conn);
        }
    }

    @Override
    public void ativarInativarMarcacaoAjuste(
            @NotNull final String token,
            @NotNull final MarcacaoAjusteAtivacaoInativacao marcacaoAjuste) throws Throwable {
        Connection conn = null;
        try {
            conn = getConnection();
            conn.setAutoCommit(false);
            internalAtivarInativarMarcacaoAjuste(conn, marcacaoAjuste);
            insereInformacoesAjusteMarcacao(
                    conn,
                    marcacaoAjuste.getCodMarcacaoAtivacaoInativacao(),
                    token,
                    marcacaoAjuste,
                    null);
            conn.commit();
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
    public List<MarcacaoAjusteHistoricoExibicao> getMarcacaoAjusteHistorio(
            @NotNull final Long codMarcacao) throws Throwable {
        return null;
    }

    @NotNull
    @Override
    public List<MarcacaoInconsistenciaExibicao> getMarcacoesInconsistentes(
            @NotNull final Long codMarcacao) throws Throwable {
        return null;
    }

    //############################################################################################
    //############################################################################################
    //                                  PRIVATE METHODS
    //############################################################################################
    //############################################################################################
    @NotNull
    private ResultInsertInicioFim insereMarcacaoAjusteAdicaoInicioFim(
            @NotNull final Connection conn,
            @NotNull final String token,
            @NotNull final MarcacaoAjusteAdicaoInicioFim marcacaoAjuste) throws Throwable {
        PreparedStatement stmt = null;
        ResultSet rSet = null;
        try {
            stmt = conn.prepareStatement("SELECT * " +
                    "FROM FUNC_MARCACAO_INSERT_MARCACAO_INICIO_FIM(?, ?, ?, ?, ?, ?);");
            stmt.setLong(1, marcacaoAjuste.getCodColaboradorMarcacao());
            stmt.setLong(2, marcacaoAjuste.getCodTipoMarcacaoReferente());
            final ZoneId zoneId = TimeZoneManager.getZoneIdForToken(token, conn);
            stmt.setObject(3, marcacaoAjuste.getDataHoraInicio().atZone(zoneId));
            stmt.setObject(4, marcacaoAjuste.getDataHoraFim().atZone(zoneId));
            stmt.setObject(5, Now.offsetDateTimeUtc());
            stmt.setString(6, token);
            rSet = stmt.executeQuery();
            if (rSet.next()
                    && rSet.getLong("COD_MARCACAO_INICIO") > 0
                    && rSet.getLong("COD_MARCACAO_FIM") > 0) {
                return new ResultInsertInicioFim(
                        rSet.getLong("COD_MARCACAO_INICIO"),
                        rSet.getLong("COD_MARCACAO_FIM"));
            } else {
                throw new SQLException("Não foi possível inserir as marcações de início e fim");
            }
        } finally {
            close(stmt, rSet);
        }
    }

    @NotNull
    private Long insereMarcacaoAjusteAdicao(@NotNull final Connection conn,
                                            @NotNull final String token,
                                            @NotNull final MarcacaoAjusteAdicao marcacaoAjuste) throws Throwable {
        PreparedStatement stmt = null;
        ResultSet rSet = null;
        try {
            stmt = conn.prepareStatement("SELECT * " +
                    "FROM FUNC_MARCACAO_INSERT_MARCACAO_AVULSA_AJUSTE(?, ?, ?) AS CODIGO;");
            final ZoneId zoneId = TimeZoneManager.getZoneIdForToken(token, conn);
            stmt.setObject(1, marcacaoAjuste.getDataHoraInserida().atZone(zoneId).toLocalDate());
            stmt.setLong(2, marcacaoAjuste.getCodMarcacaoVinculo());
            stmt.setString(3, token);
            rSet = stmt.executeQuery();
            if (rSet.next() && rSet.getLong("CODIGO") > 0) {
                return rSet.getLong("CODIGO");
            } else {
                throw new SQLException("Não foi possível inserir a marcação");
            }
        } finally {
            close(stmt, rSet);
        }
    }

    private void insereVinculoMarcacaoInicioOuFim(@NotNull final Connection conn,
                                                  @NotNull final Long codMarcacaoInserida,
                                                  @NotNull final TipoInicioFim tipoInicioFim) throws Throwable {
        PreparedStatement stmt = null;
        ResultSet rSet = null;
        try {
            if (tipoInicioFim.equals(TipoInicioFim.MARCACAO_INICIO)) {
                stmt = conn.prepareStatement("INSERT INTO MARCACAO_INICIO(COD_MARCACAO_INICIO) " +
                        "VALUES (?) RETURNING COD_MARCACAO_INICIO AS CODIGO");
            } else {
                stmt = conn.prepareStatement("INSERT INTO MARCACAO_FIM(COD_MARCACAO_FIM) " +
                        "VALUES (?) RETURNING COD_MARCACAO_FIM AS CODIGO");
            }
            stmt.setLong(1, codMarcacaoInserida);
            rSet = stmt.executeQuery();
            if (!rSet.next() || rSet.getLong("CODIGO") <= 0) {
                throw new SQLException("Não foi possível inserir o vinculo entre as marcações");
            }
        } finally {
            close(stmt, rSet);
        }
    }

    private void insereVinculoInicioFim(@NotNull final Connection conn,
                                        @NotNull final Long codMarcacaoInicio,
                                        @NotNull final Long codMarcacaoFim) throws Throwable {
        PreparedStatement stmt = null;
        ResultSet rSet = null;
        try {
            stmt = conn.prepareStatement("INSERT INTO " +
                    "MARCACAO_VINCULO_INICIO_FIM(COD_MARCACAO_INICIO, COD_MARCACAO_FIM) " +
                    "VALUES (?, ?) " +
                    "RETURNING CODIGO AS CODIGO_VINCULO");
            stmt.setLong(1, codMarcacaoInicio);
            stmt.setLong(2, codMarcacaoFim);
            rSet = stmt.executeQuery();
            if (!rSet.next() || rSet.getLong("CODIGO_VINCULO") <= 0) {
                throw new SQLException("Não foi possível inserir o vinculo entre as marcações");
            }
        } finally {
            close(stmt, rSet);
        }
    }

    private void insereInformacoesAjusteMarcacao(
            @NotNull final Connection conn,
            @NotNull final Long codMarcacaoInserida,
            @NotNull final String token,
            @NotNull final MarcacaoAjuste marcacaoAjuste,
            @Nullable final LocalDateTime dataHoraInserida) throws Throwable {
        PreparedStatement stmt = null;
        ResultSet rSet = null;
        try {
            stmt = conn.prepareStatement("SELECT * " +
                    "FROM FUNC_MARCACAO_INSERT_INFORMACOES_AJUSTE(?, ?, ?, ?, ?, ?, ?) AS CODIGO;");
            stmt.setLong(1, codMarcacaoInserida);
            final ZoneId zoneId = TimeZoneManager.getZoneIdForToken(token, conn);
            stmt.setObject(2, dataHoraInserida != null
                    ? dataHoraInserida.atZone(zoneId).toLocalDate()
                    : null);
            stmt.setLong(3, marcacaoAjuste.getCodJustificativaAjuste());
            stmt.setString(4, marcacaoAjuste.getObservacaoAjuste());
            stmt.setString(5, marcacaoAjuste.getTipoAcaoAjuste().asString());
            stmt.setString(6, token);
            stmt.setObject(7, Now.offsetDateTimeUtc());
            rSet = stmt.executeQuery();
            if (!rSet.next() || rSet.getLong("CODIGO") <= 0) {
                throw new SQLException("Não foi possível inserir as edições da marcação");
            }
        } finally {
            close(stmt, rSet);
        }
    }

    private void internalAtivarInativarMarcacaoAjuste(
            @NotNull final Connection conn,
            @NotNull final MarcacaoAjusteAtivacaoInativacao marcacaoAjuste) throws Throwable {
        PreparedStatement stmt = null;
        try {
            stmt = conn.prepareStatement("UPDATE INTERVALO SET STATUS_ATIVO = ? WHERE CODIGO = ?;");
            stmt.setBoolean(1, marcacaoAjuste.isDeveAtivar());
            stmt.setLong(2, marcacaoAjuste.getCodMarcacaoAtivacaoInativacao());
            if (stmt.executeUpdate() == 0) {
                throw new SQLException("Não foi possível ativar/inativar a marcação");
            }
        } finally {
            close(stmt);
        }
    }
}