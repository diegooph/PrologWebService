package br.com.zalf.prolog.webservice.gente.controleintervalo.ajustes;

import br.com.zalf.prolog.webservice.commons.util.date.Now;
import br.com.zalf.prolog.webservice.database.DatabaseConnection;
import br.com.zalf.prolog.webservice.gente.controleintervalo.ajustes.model.MarcacaoAjusteAdicao;
import br.com.zalf.prolog.webservice.gente.controleintervalo.ajustes.model.MarcacaoAjusteAdicaoInicioFim;
import br.com.zalf.prolog.webservice.gente.controleintervalo.ajustes.model.MarcacaoAjusteAtivacaoInativacao;
import br.com.zalf.prolog.webservice.gente.controleintervalo.ajustes.model.MarcacaoAjusteEdicao;
import br.com.zalf.prolog.webservice.gente.controleintervalo.ajustes.model.exibicao.ConsolidadoMarcacoesDia;
import br.com.zalf.prolog.webservice.gente.controleintervalo.ajustes.model.exibicao.MarcacaoAjusteHistoricoExibicao;
import br.com.zalf.prolog.webservice.gente.controleintervalo.ajustes.model.exibicao.MarcacaoColaboradorAjuste;
import br.com.zalf.prolog.webservice.gente.controleintervalo.ajustes.model.exibicao.MarcacaoInconsistenciaExibicao;
import br.com.zalf.prolog.webservice.gente.controleintervalo.model.MarcacaoInicioFim;
import org.jetbrains.annotations.NotNull;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;

/**
 * Created on 04/09/18.
 *
 * @author Diogenes Vanzela (https://github.com/diogenesvanzella)
 */
public final class ControleJornadaAjusteDaoImpl extends DatabaseConnection implements ControleJornadaAjusteDao {

    @Override
    public void adicionarMarcacaoAjuste(@NotNull final String token,
                                        @NotNull final MarcacaoAjusteAdicao marcacaoAjuste) throws Throwable {
        Connection conn = null;
        try {
            conn = getConnection();
            conn.setAutoCommit(false);
            final Long codMarcacaoInserida = insereMarcacaoAjusteAdicao(conn, token, marcacaoAjuste);
            final MarcacaoInicioFim marcacaoInicioFim = marcacaoAjuste.getMarcacaoInicioFim();
            insereVinculoNaMarcacao(
                    conn,
                    codMarcacaoInserida,
                    marcacaoAjuste.getMarcacaoInicioFim());
            final Long codMarcacaoInicio = marcacaoInicioFim.equals(MarcacaoInicioFim.MARCACAO_INICIO)
                    ? codMarcacaoInserida
                    : marcacaoAjuste.getCodMarcacaoVinculo();
            final Long codMarcacaoFim = marcacaoInicioFim.equals(MarcacaoInicioFim.MARCACAO_FIM)
                    ? codMarcacaoInserida
                    : marcacaoAjuste.getCodMarcacaoVinculo();
            insereVinculoInicioFim(
                    conn,
                    codMarcacaoInicio,
                    codMarcacaoFim);
            insereInformacoesEdicaoMarcacao(conn, codMarcacaoInserida, token, marcacaoAjuste);
            conn.commit();
        } catch (final Throwable e) {
            if (conn != null) {
                conn.rollback();
            }
            throw e;
        } finally {
            closeConnection(conn);
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
            final Long codMarcacaoInicioInserida =
                    insereMarcacaoAjusteAdicaoInicioFim(conn, token, marcacaoAjuste, MarcacaoInicioFim.MARCACAO_INICIO);
            final Long codMarcacaoFimInserida =
                    insereMarcacaoAjusteAdicaoInicioFim(conn, token, marcacaoAjuste, MarcacaoInicioFim.MARCACAO_FIM);
            insereVinculoNaMarcacao(conn, codMarcacaoInicioInserida, MarcacaoInicioFim.MARCACAO_INICIO);
            insereVinculoNaMarcacao(conn, codMarcacaoFimInserida, MarcacaoInicioFim.MARCACAO_FIM);
            insereVinculoInicioFim(conn, codMarcacaoInicioInserida, codMarcacaoFimInserida);
            conn.commit();
        } catch (final Throwable e) {
            if (conn != null) {
                conn.rollback();
            }
            throw e;
        } finally {
            closeConnection(conn);
        }
    }

    @Override
    public void ativarInativarMarcacaoAjuste(@NotNull final MarcacaoAjusteAtivacaoInativacao marcacaoAjuste,
                                             @NotNull final String token) throws Throwable {

    }

    @Override
    public void editarMarcacaoAjuste(@NotNull final MarcacaoAjusteEdicao marcacaoAjuste,
                                     @NotNull final String token) throws Throwable {

    }

    @NotNull
    @Override
    public List<ConsolidadoMarcacoesDia> getMarcacoesConsolidadasParaAjuste(@NotNull final Long codUnidade,
                                                                            @NotNull final String codColaborador,
                                                                            @NotNull final String codTipoIntervalo,
                                                                            @NotNull final LocalDate dataInicial,
                                                                            @NotNull final LocalDate dataFinal) throws Throwable {
        return null;
    }

    @NotNull
    @Override
    public List<MarcacaoColaboradorAjuste> getMarcacoesColaboradorParaAjuste(
            @NotNull final Long codUnidade,
            @NotNull final String codColaborador,
            @NotNull final LocalDate data) throws Throwable {
        return null;
    }

    @NotNull
    @Override
    public List<MarcacaoAjusteHistoricoExibicao> getMarcacaoAjusteHistorio(@NotNull final Long codMarcacao) throws
            Throwable {
        return null;
    }

    @NotNull
    @Override
    public List<MarcacaoInconsistenciaExibicao> getMarcacoesInconsistentes(@NotNull final Long codMarcacao) throws
            Throwable {
        return null;
    }

    @NotNull
    private Long insereMarcacaoAjusteAdicaoInicioFim(
            @NotNull final Connection conn,
            @NotNull final String token,
            @NotNull final MarcacaoAjusteAdicaoInicioFim marcacaoAjuste,
            @NotNull final MarcacaoInicioFim marcacaoInicioFim) throws SQLException {
        PreparedStatement stmt = null;
        ResultSet rSet = null;
        try {
            stmt = conn.prepareStatement("INSERT INTO INTERVALO (COD_UNIDADE, " +
                    "                       COD_TIPO_INTERVALO, " +
                    "                       CPF_COLABORADOR, " +
                    "                       DATA_HORA, " +
                    "                       TIPO_MARCACAO, " +
                    "                       FONTE_DATA_HORA, " +
                    "                       JUSTIFICATIVA_TEMPO_RECOMENDADO, " +
                    "                       JUSTIFICATIVA_ESTOURO, " +
                    "                       LATITUDE_MARCACAO, " +
                    "                       LONGITUDE_MARCACAO, " +
                    "                       DATA_HORA_SINCRONIZACAO, " +
                    "                       COD_COLABORADOR_INSERCAO) " +
                    "VALUES (" +
                    "  (SELECT COD_UNIDADE FROM COLABORADOR WHERE CODIGO = ?), " +
                    "  ?, " +
                    "  (SELECT CPF FROM COLABORADOR WHERE CODIGO = ?), " +
                    "  ?, " +
                    "  ?, " +
                    "  'SERVIDOR', " +
                    "  NULL, " +
                    "  NULL, " +
                    "  NULL, " +
                    "  NULL, " +
                    "  ?, " +
                    "  (SELECT CODIGO FROM COLABORADOR " +
                    "WHERE CPF = (SELECT CPF_COLABORADOR FROM TOKEN_AUTENTICACAO WHERE TOKEN = ?))) " +
                    "RETURNING CODIGO AS COD_MARCACAO_INSERIDA;");
            stmt.setLong(1, marcacaoAjuste.getCodColaboradorMarcacao());
            stmt.setLong(2, marcacaoAjuste.getCodTipoMarcacaoReferente());
            stmt.setLong(3, marcacaoAjuste.getCodColaboradorMarcacao());
            stmt.setObject(4, marcacaoInicioFim.equals(MarcacaoInicioFim.MARCACAO_INICIO)
                    ? marcacaoAjuste.getDataHoraInicio()
                    : marcacaoAjuste.getDataHoraFim());
            stmt.setString(5, marcacaoInicioFim.asString());
            stmt.setObject(6, Now.localDateTimeUtc());
            stmt.setString(7, token);
            rSet = stmt.executeQuery();
            if (rSet.next() && rSet.getLong("COD_MARCACAO_INSERIDA") != 0) {
                return rSet.getLong("COD_MARCACAO_INSERIDA");
            } else {
                throw new SQLException("Não foi possível inserir de "
                        + marcacaoInicioFim.asString() +
                        " da marcação completa");
            }
        } finally {
            closeStatement(stmt);
            closeResultSet(rSet);
        }
    }

    @NotNull
    private Long insereMarcacaoAjusteAdicao(@NotNull final Connection conn,
                                            @NotNull final String token,
                                            @NotNull final MarcacaoAjusteAdicao marcacaoAjuste) throws SQLException {
        PreparedStatement stmt = null;
        ResultSet rSet = null;
        try {
            stmt = conn.prepareStatement("INSERT INTO INTERVALO(COD_UNIDADE, " +
                    "                        COD_TIPO_INTERVALO, " +
                    "                        CPF_COLABORADOR, " +
                    "                        DATA_HORA, " +
                    "                        TIPO_MARCACAO, " +
                    "                        FONTE_DATA_HORA, " +
                    "                        JUSTIFICATIVA_TEMPO_RECOMENDADO, " +
                    "                        JUSTIFICATIVA_ESTOURO, " +
                    "                        LATITUDE_MARCACAO, " +
                    "                        LONGITUDE_MARCACAO, " +
                    "                        DATA_HORA_SINCRONIZACAO, " +
                    "                        COD_COLABORADOR_INSERCAO) " +
                    "    SELECT " +
                    "      I.COD_UNIDADE, " +
                    "      I.COD_TIPO_INTERVALO, " +
                    "      I.CPF_COLABORADOR, " +
                    "      ?, " +
                    "      (CASE WHEN I.TIPO_MARCACAO = 'MARCACAO_INICIO' THEN 'MARCACAO_FIM' ELSE 'MARCACAO_INICIO' END), " +
                    "      I.FONTE_DATA_HORA, " +
                    "      NULL, " +
                    "      NULL, " +
                    "      NULL, " +
                    "      NULL, " +
                    "      NULL, " +
                    "      (SELECT C.CODIGO FROM COLABORADOR C " +
                    "      WHERE CPF = (SELECT CPF_COLABORADOR FROM TOKEN_AUTENTICACAO WHERE TOKEN = ?)) " +
                    "    FROM INTERVALO I WHERE I.CODIGO = ? " +
                    "  RETURNING CODIGO AS NEW_COD_MARCACAO");
            stmt.setObject(1, marcacaoAjuste.getDataHoraInserida());
            stmt.setString(2, token);
            stmt.setLong(3, marcacaoAjuste.getCodMarcacaoVinculo());
            rSet = stmt.executeQuery();
            if (rSet.next() && rSet.getLong("NEW_COD_MARCACAO") != 0) {
                return rSet.getLong("NEW_COD_MARCACAO");
            } else {
                throw new SQLException("Não foi possível inserir a marcação");
            }
        } finally {
            closeStatement(stmt);
            closeResultSet(rSet);
        }
    }

    private void insereVinculoNaMarcacao(@NotNull final Connection conn,
                                         @NotNull final Long codMarcacaoInserida,
                                         @NotNull final MarcacaoInicioFim marcacaoInicioFim) throws SQLException {
        PreparedStatement stmt = null;
        ResultSet rSet = null;
        try {
            if (marcacaoInicioFim.equals(MarcacaoInicioFim.MARCACAO_INICIO)) {
                stmt = conn.prepareStatement("INSERT INTO MARCACAO_INICIO(COD_MARCACAO_INICIO) " +
                        "VALUES (?) RETURNING CODIGO");
            } else {
                stmt = conn.prepareStatement("INSERT INTO MARCACAO_FIM(COD_MARCACAO_FIM) " +
                        "VALUES (?) RETURNING CODIGO");
            }
            stmt.setLong(1, codMarcacaoInserida);
            rSet = stmt.executeQuery();
            if (!rSet.next() || rSet.getLong("CODIGO") == 0) {
                throw new SQLException("Não foi possível inserir o vinculo entre as marcações");
            }
        } finally {
            closeStatement(stmt);
            closeResultSet(rSet);
        }
    }

    private void insereVinculoInicioFim(@NotNull final Connection conn,
                                        @NotNull final Long codMarcacaoInicio,
                                        @NotNull final Long codMarcacaoFim) throws SQLException {
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
            if (!rSet.next() || rSet.getLong("CODIGO_VINCULO") == 0) {
                throw new SQLException("Não foi possível inserir o vinculo entre as marcações");
            }
        } finally {
            closeStatement(stmt);
            closeResultSet(rSet);
        }
    }

    private void insereInformacoesEdicaoMarcacao(@NotNull final Connection conn,
                                                 @NotNull final Long codMarcacaoInserida,
                                                 @NotNull final String token,
                                                 @NotNull final MarcacaoAjusteAdicao marcacaoAjuste) throws SQLException {
        PreparedStatement stmt = null;
        ResultSet rSet = null;
        try {
            stmt = conn.prepareStatement("SELECT * " +
                    "FROM FUNC_MARCACAO_INSERT_AJUSTE_MARCACAO(?, ?, ?, ?, ?, ?, ?) AS CODIGO;");
            stmt.setLong(1, codMarcacaoInserida);
            stmt.setObject(2, marcacaoAjuste.getDataHoraInserida());
            stmt.setLong(3, marcacaoAjuste.getCodJustificativaAjuste());
            stmt.setString(4, marcacaoAjuste.getObservacaoAjuste());
            stmt.setString(5, marcacaoAjuste.getTipoMarcacaoAjuste().asString());
            stmt.setString(6, token);
            stmt.setObject(7, Now.localDateTimeUtc());
            rSet = stmt.executeQuery();
            if (!rSet.next() || rSet.getLong("CODIGO") == 0) {
                throw new SQLException("Não foi possível inserir as edições da marcação");
            }
        } finally {
            closeStatement(stmt);
            closeResultSet(rSet);
        }
    }
}