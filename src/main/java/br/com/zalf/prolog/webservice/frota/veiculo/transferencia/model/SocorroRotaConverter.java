package br.com.zalf.prolog.webservice.frota.veiculo.transferencia.model;

import br.com.zalf.prolog.webservice.frota.socorrorota._model.UnidadeAberturaSocorro;
import org.jetbrains.annotations.NotNull;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Created on 12/19/19
 *
 * @author Wellington Moraes (https://github.com/wvinim)
 */
public class SocorroRotaConverter {
    private SocorroRotaConverter() {
        throw new IllegalStateException(VeiculoTransferenciaConverter.class.getSimpleName()
                + "cannot be instantiated!");
    }

    @NotNull
    public static UnidadeAberturaSocorro createUnidadeAberturaSocorro(
            @NotNull final ResultSet rSet) throws SQLException {
        return new UnidadeAberturaSocorro(
                rSet.getLong("CODIGO_UNIDADE"),
                rSet.getString("NOME_UNIDADE"));
    }
}
