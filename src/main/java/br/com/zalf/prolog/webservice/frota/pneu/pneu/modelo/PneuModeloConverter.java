package br.com.zalf.prolog.webservice.frota.pneu.pneu.modelo;

import br.com.zalf.prolog.webservice.frota.pneu.pneu.modelo.model.PneuMarcaModelo;
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
    public static PneuMarcaModelo createMarcaPneu(@NotNull final ResultSet rSet) throws SQLException {
        final PneuMarcaModelo marca = new PneuMarcaModelo();
        marca.setCodigo(rSet.getLong("COD_MARCA_PNEU"));
        marca.setNome(rSet.getString("NOME_MARCA_PNEU"));
        return marca;
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
