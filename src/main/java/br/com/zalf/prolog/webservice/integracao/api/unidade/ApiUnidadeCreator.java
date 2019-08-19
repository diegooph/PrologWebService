package br.com.zalf.prolog.webservice.integracao.api.unidade;

import org.jetbrains.annotations.NotNull;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Created on 18/08/19.
 *
 * @author Diogenes Vanzela (https://github.com/diogenesvanzella)
 */
final class ApiUnidadeCreator {

    private ApiUnidadeCreator() {
        throw new IllegalStateException(ApiUnidadeCreator.class.getSimpleName() + "cannot be instantiated");
    }

    @NotNull
    static ApiUnidade createApiUnidade(@NotNull final ResultSet rSet) throws SQLException {
        return new ApiUnidade(
                rSet.getLong("COD_EMPRESA"),
                rSet.getLong("CODIGO"),
                rSet.getString("NOME"),
                rSet.getBoolean("STATUS_ATIVO"));
    }
}
