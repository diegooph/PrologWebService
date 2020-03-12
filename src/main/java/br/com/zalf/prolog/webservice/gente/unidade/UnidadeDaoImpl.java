package br.com.zalf.prolog.webservice.gente.unidade;

import br.com.zalf.prolog.webservice.database.DatabaseConnection;
import br.com.zalf.prolog.webservice.gente.unidade._model.UnidadeVisualizacao;
import org.jetbrains.annotations.NotNull;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

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
    public List<UnidadeVisualizacao> getAllUnidadeByCodEmpresa(final Long codEmpresa) throws SQLException {
        return null;
    }

    @NotNull
    @Override
    public List<UnidadeVisualizacao> getAllUnidadeByCodEmpresaAndCodRegional(final Long codEmpresa, final Long codRegional) throws SQLException {
        return null;
    }

}
