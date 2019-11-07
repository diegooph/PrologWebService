package br.com.zalf.prolog.webservice.frota.pneu.banda;

import br.com.zalf.prolog.webservice.frota.pneu.banda._model.PneuMarcaBandaListagem;
import br.com.zalf.prolog.webservice.frota.pneu.banda._model.PneuMarcaBandaVisualizacao;
import br.com.zalf.prolog.webservice.frota.pneu.banda._model.PneuModeloBandaListagem;
import br.com.zalf.prolog.webservice.frota.pneu.banda._model.PneuModeloBandaVisualizacao;
import org.jetbrains.annotations.NotNull;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Created on 08/10/19.
 *
 * @author Thais Francisco (https://github.com/thaisksf)
 */
final class PneuMarcaModeloBandaConverter {

    @NotNull
    static PneuMarcaBandaListagem createPneuMarcaBandaListagem(@NotNull final ResultSet rSet)
            throws SQLException {
        return new PneuMarcaBandaListagem(
                rSet.getLong("CODIGO"),
                rSet.getString("NOME"), null);
    }

    @NotNull
    static PneuMarcaBandaVisualizacao createPneuMarcaBandaVisualizacao(@NotNull final ResultSet rSet)
            throws SQLException {
        return new PneuMarcaBandaVisualizacao(
                rSet.getLong("CODIGO"),
                rSet.getString("NOME"));
    }

    @NotNull
    static PneuModeloBandaListagem createPneuModeloBandaListagem(@NotNull final ResultSet rSet) throws SQLException {
        return new PneuModeloBandaListagem(
                rSet.getLong("COD_MARCA_BANDA"),
                rSet.getString("NOME_MARCA_BANDA"),
                rSet.getLong("COD_MODELO_BANDA"),
                rSet.getString("NOME_MODELO_BANDA"),
                rSet.getInt("QTD_SULCOS"),
                rSet.getDouble("ALTURA_SULCOS"));
    }

    @NotNull
    static PneuModeloBandaVisualizacao createPneuModeloBandaVisualizacao(@NotNull final ResultSet rSet) throws SQLException {
        return new PneuModeloBandaVisualizacao(
                rSet.getLong("COD_MARCA_BANDA"),
                rSet.getString("NOME_MARCA_BANDA"),
                rSet.getLong("COD_MODELO_BANDA"),
                rSet.getString("NOME_MODELO_BANDA"),
                rSet.getInt("QT_SULCOS_MODELO_BANDA"),
                rSet.getDouble("ALTURA_SULCOS_MODELO_BANDA"));
    }
}
