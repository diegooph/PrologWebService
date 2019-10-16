package br.com.zalf.prolog.webservice.frota.pneu.pneu.modelo;

import br.com.zalf.prolog.webservice.frota.pneu.pneu.modelo.model.PneuModeloVisualizacao;
import org.jetbrains.annotations.NotNull;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Created on 26/09/19.
 *
 * @author Thais Francisco (https://github.com/thaisksf)
 */
public final class PneuModeloConverter {

    private PneuModeloConverter() {
        throw new IllegalStateException(PneuModeloConverter.class.getSimpleName() + " cannot be instantiated!");
    }

    @NotNull
    public static PneuModeloVisualizacao createModeloPneu(@NotNull final ResultSet rSet) throws SQLException {
        return new PneuModeloVisualizacao(
                rSet.getLong("COD_MARCA"),
                rSet.getLong("CODIGO"),
                rSet.getString("NOME"),
                rSet.getInt("QT_SULCOS"),
                rSet.getDouble("ALTURA_SULCOS"));
    }
}