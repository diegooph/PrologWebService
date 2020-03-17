package br.com.zalf.prolog.webservice.geral.unidade;

import br.com.zalf.prolog.webservice.commons.util.SqlType;
import br.com.zalf.prolog.webservice.database.DatabaseConnection;
import br.com.zalf.prolog.webservice.geral.unidade._model.UnidadeEdicao;
import br.com.zalf.prolog.webservice.geral.unidade._model.UnidadeVisualizacao;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import static br.com.zalf.prolog.webservice.commons.util.StatementUtils.bindValueOrNull;

/**
 * Created on 2020-03-12
 *
 * @author Gustavo Navarro (https://github.com/gustavocnp95)
 */
public class UnidadeDaoImpl extends DatabaseConnection implements UnidadeDao {

    @Override
    public void update(@NotNull final UnidadeEdicao unidade) throws Throwable {
        Connection conn = null;
        PreparedStatement stmt = null;
        final ResultSet rSet = null;

        try {
            conn = getConnection();
            conn.setAutoCommit(false);
            stmt = conn.prepareStatement("SELECT FUNC_GENTE_UPDATE_UNIDADE(" +
                    "F_COD_UNIDADE := ?," +
                    "F_NOME_UNIDADE := ?," +
                    "F_COD_AUXILIAR_UNIDADE := ?," +
                    "F_LATITUDE_UNIDADE := ?," +
                    "F_LONGITUDE_UNIDADE := ?);");

            stmt.setLong(1, unidade.getCodUnidade());
            stmt.setString(2, unidade.getNomeUnidade());
            bindValueOrNull(stmt, 3, unidade.getCodAuxiliarUnidade(), SqlType.TEXT);
            bindValueOrNull(stmt, 4, unidade.getLatitudeUnidade(), SqlType.TEXT);
            bindValueOrNull(stmt, 5, unidade.getLongitudeUnidade(), SqlType.TEXT);

            stmt.executeQuery();

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

    @NotNull
    @Override
    public UnidadeVisualizacao getUnidadeByCodigo(@NotNull final Long codUnidade) throws Throwable {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rSet = null;
        try {
            conn = getConnection();

            stmt = conn.prepareStatement("SELECT * FROM FUNC_GENTE_GET_UNIDADE_BY_COD_UNIDADE(" +
                    "F_COD_UNIDADE := ?)");
            stmt.setLong(1, codUnidade);

            rSet = stmt.executeQuery();

            while (rSet.next()) {
                return UnidadeConverter.createUnidadeVisualizacao(rSet);
            }
        } finally {
            close(conn, stmt, rSet);
        }

        return null;
    }

    @NotNull
    @Override
    public List<UnidadeVisualizacao> getUnidadesListagem(@NotNull final Long codEmpresa,
                                                         @Nullable final Long codRegional) throws Throwable {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rSet = null;
        try {
            conn = getConnection();

            stmt = conn.prepareStatement("SELECT * FROM FUNC_GENTE_GET_UNIDADES_BY_COD_EMPRESA_AND_COD_REGIONAL(" +
                    "F_COD_EMPRESA := ?," +
                    "F_COD_REGIONAL := ?);");
            stmt.setLong(1, codEmpresa);
            bindValueOrNull(stmt, 2, codRegional, SqlType.BIGINT);

            rSet = stmt.executeQuery();

            final List<UnidadeVisualizacao> unidadesVisualizacao = new ArrayList<UnidadeVisualizacao>();
            while (rSet.next()) {
                unidadesVisualizacao.add(UnidadeConverter.createUnidadeVisualizacao(rSet));
            }

            return unidadesVisualizacao;
        } finally {
            close(conn, stmt, rSet);
        }
    }

}
