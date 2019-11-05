package br.com.zalf.prolog.webservice.integracao.api.controlejornada.relatorio;

import br.com.zalf.prolog.webservice.integracao.api.controlejornada.relatorio._model.ApiMarcacaoRelatorio1510;
import org.jetbrains.annotations.NotNull;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Created on 11/5/19
 *
 * @author Diogenes Vanzela (https://github.com/diogenesvanzella)
 */
public final class ApiMarcacaoRelatorioCreator {
    private ApiMarcacaoRelatorioCreator() {
        throw new IllegalStateException(ApiMarcacaoRelatorioCreator.class.getSimpleName() + " cannot be instantiated!");
    }

    @NotNull
    static ApiMarcacaoRelatorio1510 createApiMarcacaoRelatorio1510(
            @NotNull final ResultSet rSet) throws SQLException {
        return new ApiMarcacaoRelatorio1510(
                rSet.getLong("COD_MARCACAO"),
                rSet.getString("NSR"),
                rSet.getString("TIPO_REGISTRO"),
                rSet.getString("DATA_MARCACAO"),
                rSet.getString("HORARIO_MARCACAO"),
                rSet.getString("PIS_COLABORADOR"));
    }
}
