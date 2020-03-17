package br.com.zalf.prolog.webservice.geral.unidade;

import br.com.zalf.prolog.webservice.commons.util.PostgresUtils;
import br.com.zalf.prolog.webservice.commons.util.SqlType;
import br.com.zalf.prolog.webservice.database.DatabaseConnection;
import br.com.zalf.prolog.webservice.geral.unidade._model.UnidadeEdicao;
import br.com.zalf.prolog.webservice.geral.unidade._model.UnidadeVisualizacaoListagem;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Types;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static br.com.zalf.prolog.webservice.commons.util.StatementUtils.bindValueOrNull;

/**
 * Created on 2020-03-12
 *
 * @author Gustavo Navarro (https://github.com/gustavocnp95)
 */
public final class UnidadeDaoImpl extends DatabaseConnection implements UnidadeDao {

    @Override
    public void update(@NotNull final UnidadeEdicao unidade) throws Throwable {
        Connection conn = null;
        PreparedStatement stmt = null;
        try {
            conn = getConnection();
            stmt = conn.prepareStatement("SELECT FUNC_UNIDADE_ATUALIZA(" +
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
        } finally {
            close(conn, stmt);
        }
    }

    @NotNull
    @Override
    public UnidadeVisualizacaoListagem getUnidadeByCodigo(@NotNull final Long codUnidade) throws Throwable {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rSet = null;
        try {
            conn = getConnection();
            stmt = conn.prepareStatement("SELECT * FROM FUNC_UNIDADE_VISUALIZACAO(" +
                    "F_COD_UNIDADE := ?)");
            stmt.setLong(1, codUnidade);
            rSet = stmt.executeQuery();
            if (rSet.next()) {
                return UnidadeConverter.createUnidadeVisualizacaoListagem(rSet);
            } else {
                throw new IllegalStateException("Nenhuma unidade encontrada com o código: " + codUnidade);
            }
        } finally {
            close(conn, stmt, rSet);
        }
    }

    @NotNull
    @Override
    public List<UnidadeVisualizacaoListagem> getUnidadesListagem(@NotNull final Long codEmpresa,
                                                                 @Nullable final List<Long> codigosRegionais) throws Throwable {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rSet = null;
        try {
            conn = getConnection();
            stmt = conn.prepareStatement("SELECT * FROM FUNC_UNIDADE_LISTAGEM(" +
                    "F_COD_EMPRESA := ?," +
                    "F_COD_REGIONAL := ?);");
            stmt.setLong(1, codEmpresa);

            if (codigosRegionais == null) {

                stmt.setNull(2, Types.NULL);
            } else {
                stmt.setArray(2, PostgresUtils.listToArray(conn, SqlType.BIGINT, codigosRegionais));
            }

            rSet = stmt.executeQuery();

            if (rSet.next()) {
                final List<UnidadeVisualizacaoListagem> unidadesVisualizacao = new ArrayList<UnidadeVisualizacaoListagem>();
                do {
                    unidadesVisualizacao.add(UnidadeConverter.createUnidadeVisualizacaoListagem(rSet));
                } while (rSet.next());
                return unidadesVisualizacao;
            } else {
                return Collections.emptyList();
            }
        } finally {
            close(conn, stmt, rSet);
        }
    }
}
