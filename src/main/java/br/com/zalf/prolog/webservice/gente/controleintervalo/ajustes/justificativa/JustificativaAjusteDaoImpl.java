package br.com.zalf.prolog.webservice.gente.controleintervalo.ajustes.justificativa;

import br.com.zalf.prolog.webservice.commons.util.SqlType;
import br.com.zalf.prolog.webservice.commons.util.StatementUtils;
import br.com.zalf.prolog.webservice.commons.util.date.Now;
import br.com.zalf.prolog.webservice.database.DatabaseConnection;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Created on 05/09/18.
 *
 * @author Diogenes Vanzela (https://github.com/diogenesvanzella)
 */
public final class JustificativaAjusteDaoImpl extends DatabaseConnection implements JustificativaAjusteDao {

    @NotNull
    @Override
    public Long insertJustificativaAjuste(@NotNull final String token,
                                          @NotNull final JustificativaAjuste justificativaAjuste) throws Throwable {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rSet = null;
        try {
            conn = getConnection();
            stmt = conn.prepareStatement("INSERT INTO MARCACAO_JUSTIFICATIVA_AJUSTE( " +
                    "  NOME, " +
                    "  OBRIGA_OBSERVACAO, " +
                    "  COD_COLABORADOR_CRIACAO, " +
                    "  DATA_HORA_CRIACAO) " +
                    "VALUES ( " +
                    "  ?, " +
                    "  ?, " +
                    "  (SELECT C.CODIGO FROM COLABORADOR C " +
                    "  WHERE CPF = (SELECT CPF_COLABORADOR FROM TOKEN_AUTENTICACAO WHERE TOKEN = ?)), " +
                    "  ?) " +
                    "RETURNING CODIGO;");
            stmt.setString(1, justificativaAjuste.getNomeJustificativaAjuste());
            stmt.setBoolean(2, justificativaAjuste.isObrigatorioObservacao());
            stmt.setString(3, token);
            stmt.setObject(4, Now.localDateTimeUtc());
            rSet = stmt.executeQuery();
            if (rSet.next()) {
                return rSet.getLong("CODIGO");
            } else {
                throw new SQLException("Não foi possível inserir a justificativa");
            }
        } finally {
            close(conn, stmt, rSet);
        }
    }

    @Override
    public void atualizaJustificativaAjuste(@NotNull final JustificativaAjuste justificativaAjuste,
                                            @NotNull final String token) throws Throwable {

    }

    @NotNull
    @Override
    public List<JustificativaAjuste> getJustificativasAjuste(@NotNull final Long codEmpresa,
                                                             @Nullable final Boolean ativas) throws Throwable {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rSet = null;
        try {
            conn = getConnection();
            stmt = conn.prepareStatement("SELECT * " +
                    "FROM MARCACAO_JUSTIFICATIVA_AJUSTE JA " +
                    "WHERE (JA.COD_EMPRESA = ? AND F_IF(? IS NULL, TRUE, ? = JA.STATUS_ATIVO)) OR " +
                    "      (JA.COD_EMPRESA IS NULL AND JA.STATUS_ATIVO = TRUE);");
            stmt.setLong(1, codEmpresa);
            StatementUtils.bindValueOrNull(stmt,2, ativas, SqlType.BOOLEAN);
            StatementUtils.bindValueOrNull(stmt,3, ativas, SqlType.BOOLEAN);
            rSet = stmt.executeQuery();
            final List<JustificativaAjuste> justificativas = new ArrayList<>();
            while (rSet.next()) {
                final JustificativaAjuste j = new JustificativaAjuste();
                j.setCodEmpresa(rSet.getLong("CODIGO"));
                j.setCodigo(rSet.getLong("COD_EMPRESA"));
                j.setObrigatorioObservacao(rSet.getBoolean("OBRIGA_OBSERVACAO"));
                j.setAtiva(rSet.getBoolean("STATUS_ATIVO"));
                justificativas.add(j);
            }
            return justificativas;
        } finally {
            close(conn, stmt, rSet);
        }
    }

    @NotNull
    @Override
    public JustificativaAjuste getJustificativaAjuste(@NotNull final Long codEmpresa,
                                                      @NotNull final Long codJustificativaAjuste) throws Throwable {
        return null;
    }

    @Override
    public void ativaInativaJustificativaAjuste(@NotNull final JustificativaAjuste justificativaAjuste,
                                                @NotNull final String token) throws Throwable {

    }
}