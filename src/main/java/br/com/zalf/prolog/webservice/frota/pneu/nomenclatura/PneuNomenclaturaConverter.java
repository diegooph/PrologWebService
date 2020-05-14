package br.com.zalf.prolog.webservice.frota.pneu.nomenclatura;

import br.com.zalf.prolog.webservice.frota.pneu.nomenclatura._model.PneuNomenclaturaItemVisualizacao;
import org.jetbrains.annotations.NotNull;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Created on 2019-09-12
 *
 * @author Luiz Felipe (https://github.com/luizfp)
 */
public final class PneuNomenclaturaConverter {

    private PneuNomenclaturaConverter() {
        throw new IllegalStateException(PneuNomenclaturaConverter.class.getSimpleName() + " cannot be instantiated!");
    }

    @NotNull
    public static PneuNomenclaturaItemVisualizacao createNomenclaturaItemVisualizacao(@NotNull final ResultSet rSet)
            throws SQLException {
        return new PneuNomenclaturaItemVisualizacao(
                rSet.getString("NOMENCLATURA"),
                rSet.getString("COD_AUXILIAR"),
                rSet.getInt("POSICAO_PROLOG"));
    }
}