package br.com.zalf.prolog.webservice.gente.controleintervalo.ajustes;

import br.com.zalf.prolog.webservice.Injection;
import br.com.zalf.prolog.webservice.TimeZoneManager;
import br.com.zalf.prolog.webservice.commons.util.PostgresUtils;
import br.com.zalf.prolog.webservice.commons.util.SqlType;
import br.com.zalf.prolog.webservice.commons.util.StringUtils;
import br.com.zalf.prolog.webservice.commons.util.date.Now;
import br.com.zalf.prolog.webservice.database.DatabaseConnection;
import br.com.zalf.prolog.webservice.gente.controleintervalo.ControleIntervaloDao;
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
import java.time.OffsetDateTime;
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
            bindValueOrNull(stmt, 1, codTipoMarcacao, SqlType.BIGINT);
            stmt.setLong(2, codColaborador);
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
    public void adicionarMarcacaoAjuste(@NotNull final String tokenResponsavelAjuste,
                                        @NotNull final MarcacaoAjusteAdicao marcacaoAjuste) throws Throwable {
        Connection conn = null;
        try {
            conn = getConnection();
            conn.setAutoCommit(false);
            final ZoneId zoneId = TimeZoneManager.getZoneIdForToken(tokenResponsavelAjuste, conn);
            final Long codMarcacaoInserida = insereMarcacaoAjusteAdicao(conn, tokenResponsavelAjuste, marcacaoAjuste, zoneId);
            final TipoInicioFim tipoInicioFim = marcacaoAjuste.getTipoInicioFim();
            final ControleIntervaloDao controleIntervaloDao = Injection.provideControleIntervaloDao();
            controleIntervaloDao.insereMarcacaoInicioOuFim(conn, codMarcacaoInserida, tipoInicioFim);
            final Long codMarcacaoInicio = tipoInicioFim.equals(TipoInicioFim.MARCACAO_INICIO)
                    ? codMarcacaoInserida
                    : marcacaoAjuste.getCodMarcacaoVinculo();
            final Long codMarcacaoFim = tipoInicioFim.equals(TipoInicioFim.MARCACAO_FIM)
                    ? codMarcacaoInserida
                    : marcacaoAjuste.getCodMarcacaoVinculo();
            controleIntervaloDao.insereVinculoInicioFim(conn, codMarcacaoInicio, codMarcacaoFim);
            insereInformacoesAjusteMarcacao(
                    conn,
                    codMarcacaoInserida,
                    tokenResponsavelAjuste,
                    marcacaoAjuste,
                    marcacaoAjuste.getDataHoraInserida() != null
                            ? marcacaoAjuste.getDataHoraInserida().atZone(zoneId).toOffsetDateTime()
                            : null);
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
            @NotNull final String tokenResponsavelAjuste,
            @NotNull final MarcacaoAjusteAdicaoInicioFim marcacaoAjuste) throws Throwable {
        Connection conn = null;
        try {
            conn = getConnection();
            conn.setAutoCommit(false);
            final ResultInsertInicioFim codigos =
                    insereMarcacaoAjusteAdicaoInicioFim(conn, tokenResponsavelAjuste, marcacaoAjuste);
            final ControleIntervaloDao controleIntervaloDao = Injection.provideControleIntervaloDao();
            controleIntervaloDao
                    .insereMarcacaoInicioOuFim(conn, codigos.getCodMarcacaoInicio(), TipoInicioFim.MARCACAO_INICIO);
            controleIntervaloDao
                    .insereMarcacaoInicioOuFim(conn, codigos.getCodMarcacaoFim(), TipoInicioFim.MARCACAO_FIM);
            controleIntervaloDao
                    .insereVinculoInicioFim(conn, codigos.getCodMarcacaoInicio(), codigos.getCodMarcacaoFim());
            insereInformacoesAjusteMarcacao(
                    conn,
                    codigos.getCodMarcacaoFim(),
                    tokenResponsavelAjuste,
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
    public void editarMarcacaoAjuste(@NotNull final String tokenResponsavelAjuste,
                                     @NotNull final MarcacaoAjusteEdicao marcacaoAjuste) throws Throwable {
        Connection conn = null;
        try {
            conn = getConnection();
            conn.setAutoCommit(false);
            final ZoneId zoneId = TimeZoneManager.getZoneIdForToken(tokenResponsavelAjuste, conn);
            insereInformacoesAjusteMarcacao(
                    conn,
                    marcacaoAjuste.getCodMarcacaoEdicao(),
                    tokenResponsavelAjuste,
                    marcacaoAjuste,
                    marcacaoAjuste.getDataHoraNovaInserida().atZone(zoneId).toOffsetDateTime());
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
            @NotNull final String tokenResponsavelAjuste,
            @NotNull final MarcacaoAjusteAtivacaoInativacao marcacaoAjuste) throws Throwable {
        Connection conn = null;
        try {
            conn = getConnection();
            conn.setAutoCommit(false);
            internalAtivarInativarMarcacaoAjuste(conn, marcacaoAjuste);
            insereInformacoesAjusteMarcacao(
                    conn,
                    marcacaoAjuste.getCodMarcacaoAtivacaoInativacao(),
                    tokenResponsavelAjuste,
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
    public List<MarcacaoAjusteHistoricoExibicao> getHistoricoAjusteMarcacoes(
            @NotNull final List<Long> codMarcacoes) throws Throwable {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rSet = null;
        try {
            conn = getConnection();
            stmt = conn.prepareStatement("SELECT * FROM FUNC_MARCACAO_GET_HISTORICO_AJUSTES(?);");
            stmt.setArray(1, PostgresUtils.listToArray(conn, SqlType.BIGINT, codMarcacoes));
            rSet = stmt.executeQuery();
            final List<MarcacaoAjusteHistoricoExibicao> historicos = new ArrayList<>();
            while (rSet.next()) {
                historicos.add(ControleJornadaAjusteConverter.createHistoricoAjuste(rSet));
            }
            return historicos;
        } finally {
            close(conn, stmt, rSet);
        }
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
            final ZoneId zoneId = TimeZoneManager.getZoneIdForToken(token, conn);
            stmt = conn.prepareStatement("SELECT * " +
                    "FROM FUNC_MARCACAO_INSERT_MARCACAO_INICIO_FIM(?, ?, ?, ?, ?, ?);");
            stmt.setLong(1, marcacaoAjuste.getCodColaboradorMarcacao());
            stmt.setLong(2, marcacaoAjuste.getCodTipoMarcacaoReferente());
            stmt.setObject(3, marcacaoAjuste.getDataHoraInicio().atZone(zoneId).toOffsetDateTime());
            stmt.setObject(4, marcacaoAjuste.getDataHoraFim().atZone(zoneId).toOffsetDateTime());
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
                                            @NotNull final MarcacaoAjusteAdicao marcacaoAjuste,
                                            @NotNull final ZoneId zoneIdCliente) throws Throwable {
        PreparedStatement stmt = null;
        ResultSet rSet = null;
        try {
            stmt = conn.prepareStatement("SELECT * " +
                    "FROM FUNC_MARCACAO_INSERT_MARCACAO_AVULSA_AJUSTE(?, ?, ?, ?) AS CODIGO;");
            stmt.setObject(1, marcacaoAjuste.getDataHoraInserida().atZone(zoneIdCliente).toOffsetDateTime());
            stmt.setLong(2, marcacaoAjuste.getCodMarcacaoVinculo());
            stmt.setObject(3, Now.offsetDateTimeUtc());
            stmt.setString(4, token);
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

    private void insereInformacoesAjusteMarcacao(
            @NotNull final Connection conn,
            @NotNull final Long codMarcacaoAjustada,
            @NotNull final String tokenResponsavelAjuste,
            @NotNull final MarcacaoAjuste marcacaoAjuste,
            @Nullable final OffsetDateTime dataHoraInserida) throws Throwable {
        PreparedStatement stmt = null;
        ResultSet rSet = null;
        try {
            stmt = conn.prepareStatement("SELECT * " +
                    "FROM FUNC_MARCACAO_INSERT_INFORMACOES_AJUSTE(?, ?, ?, ?, ?, ?, ?) AS CODIGO;");
            stmt.setLong(1, codMarcacaoAjustada);
            stmt.setObject(2, dataHoraInserida);
            stmt.setLong(3, marcacaoAjuste.getCodJustificativaAjuste());
            bindValueOrNull(stmt, 4, StringUtils.emptyToNull(marcacaoAjuste.getObservacaoAjuste()), SqlType.TEXT);
            stmt.setString(5, marcacaoAjuste.getTipoAcaoAjuste().asString());
            stmt.setString(6, tokenResponsavelAjuste);
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