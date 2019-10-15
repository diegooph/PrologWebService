package br.com.zalf.prolog.webservice.frota.pneu.pneu.banda;

import br.com.zalf.prolog.webservice.frota.pneu.pneu.banda.model.PneuModeloBandaVisualizacao;
import org.jetbrains.annotations.NotNull;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Created on 08/10/19.
 *
 * @author Thais Francisco (https://github.com/thaisksf)
 */
public class PneuBandaConverter {
    private static final String TAG = PneuBandaConverter.class.getSimpleName();

    @NotNull
    public static PneuModeloBandaVisualizacao createModeloBanda(@NotNull final ResultSet rSet) throws SQLException {
        return new PneuModeloBandaVisualizacao(
                rSet.getLong("COD_MODELO_BANDA"),
                rSet.getString("NOME_MODELO_BANDA"),
                rSet.getInt("QT_SULCOS_MODELO_BANDA"),
                rSet.getDouble("ALTURA_SULCOS_MODELO_BANDA"));
    }
}
