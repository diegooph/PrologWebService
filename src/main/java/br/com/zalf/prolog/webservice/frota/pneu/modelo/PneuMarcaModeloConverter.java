package br.com.zalf.prolog.webservice.frota.pneu.modelo;

import br.com.zalf.prolog.webservice.frota.pneu.modelo._model.PneuMarcaListagem;
import br.com.zalf.prolog.webservice.frota.pneu.modelo._model.PneuModeloListagem;
import br.com.zalf.prolog.webservice.frota.pneu.modelo._model.PneuModeloVisualizacao;
import org.jetbrains.annotations.NotNull;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Created on 26/09/19.
 *
 * @author Thais Francisco (https://github.com/thaisksf)
 */
final class PneuMarcaModeloConverter {

    private PneuMarcaModeloConverter() {
        throw new IllegalStateException(PneuMarcaModeloConverter.class.getSimpleName() + " cannot be instantiated!");
    }

    @NotNull
    public static PneuMarcaListagem createPneuMarcaListagem(@NotNull final ResultSet rSet) throws SQLException {
        return new PneuMarcaListagem(
                rSet.getLong("COD_MARCA_PNEU"),
                rSet.getString("NOME_MARCA_PNEU"), null);
    }

    @NotNull
    static PneuModeloVisualizacao createModeloPneu(@NotNull final ResultSet rSet) throws SQLException {
        return new PneuModeloVisualizacao(
                rSet.getLong("COD_MARCA"),
                rSet.getLong("COD_MODELO"),
                rSet.getString("NOME_MODELO"),
                rSet.getInt("QTD_SULCOS"),
                rSet.getDouble("ALTURA_SULCOS"));
    }

    @NotNull
    static PneuModeloListagem createPneuModeloListagem(@NotNull final ResultSet rSet) throws SQLException {
        return new PneuModeloListagem(
                rSet.getLong("COD_MARCA_PNEU"),
                rSet.getString("NOME_MARCA_PNEU"),
                rSet.getLong("COD_MODELO_PNEU"),
                rSet.getString("NOME_MODELO_PNEU"),
                rSet.getInt("QTD_SULCOS"),
                rSet.getDouble("ALTURA_SULCOS"));
    }
}