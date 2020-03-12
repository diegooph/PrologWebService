package br.com.zalf.prolog.webservice.gente.unidade;

import br.com.zalf.prolog.webservice.commons.util.SqlType;
import br.com.zalf.prolog.webservice.database.DatabaseConnection;
import br.com.zalf.prolog.webservice.gente.unidade._model.UnidadeVisualizacao;
import org.jetbrains.annotations.NotNull;

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

    @NotNull
    @Override
    public UnidadeVisualizacao getUnidadeByCodUnidade(final Long codUnidade) throws Throwable {
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
    public List<UnidadeVisualizacao> getAllUnidadeByCodEmpresaAndCodRegional(final Long codEmpresa,
                                                                             final Long codRegional) throws Throwable {
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
