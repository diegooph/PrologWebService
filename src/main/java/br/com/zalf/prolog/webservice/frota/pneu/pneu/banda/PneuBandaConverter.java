package br.com.zalf.prolog.webservice.frota.pneu.pneu.banda;

import br.com.zalf.prolog.webservice.commons.util.Log;
import br.com.zalf.prolog.webservice.frota.pneu.pneu.banda.model.PneuMarcaBanda;
import br.com.zalf.prolog.webservice.frota.pneu.pneu.banda.model.PneuModeloBandaVisualizacao;
import br.com.zalf.prolog.webservice.frota.pneu.pneu.model.Pneu;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Created on 08/10/19.
 *
 * @author Thais Francisco (https://github.com/thaisksf)
 */
public class PneuBandaConverter {
    private static final String TAG = PneuBandaConverter.class.getSimpleName();

    @Nullable
    public static PneuMarcaBanda createBanda(@NotNull final Pneu pneu, @NotNull final ResultSet rSet) throws SQLException {
        if (rSet.getString("COD_MODELO_BANDA") != null) {

            PneuModeloBandaVisualizacao pneuModeloBandaVisualizacao = createModeloBanda(rSet);

            return new PneuMarcaBanda(rSet.getLong("COD_MARCA_BANDA"),
                                rSet.getString("NOME_MARCA_BANDA"),
                                pneuModeloBandaVisualizacao );
        } else if (rSet.getInt("VIDA_ATUAL") == 1) {
            final PneuModeloBandaVisualizacao modeloBanda = new PneuModeloBandaVisualizacao(
                    pneu.getModelo().getCodigo(),
                    pneu.getModelo().getNome(),
                    pneu.getModelo().getQuantidadeSulcos(),
                    pneu.getModelo().getAlturaSulcos(),
                    null);

            final PneuMarcaBanda banda = new PneuMarcaBanda(
                    pneu.getMarca().getCodigo(),
                    pneu.getMarca().getNome(),
                    modeloBanda);

            return banda;
        } else {
            // TODO: 12/01/2018 - Atualmente não podemos quebrar o servidor caso atinja esse estado porque possuímos
            // pneus com essa inconsistência em banco. Isso será eliminado no futuro e poderemos lançar uma exceção
            // aqui.
            Log.w(TAG, "Esse estado é uma inconsistência e não deveria acontecer! " +
                    "Algum pneu está acima da primeira vida porém não possui banda associada.");
            return null;
        }
    }

    @NotNull
    private static PneuModeloBandaVisualizacao createModeloBanda(@NotNull final ResultSet rSet) throws SQLException {
        final PneuModeloBandaVisualizacao modeloBanda = new PneuModeloBandaVisualizacao(
                rSet.getLong("COD_MODELO_BANDA"),
                rSet.getString("NOME_MODELO_BANDA"),
                rSet.getInt("QT_SULCOS_MODELO_BANDA"),
                rSet.getDouble("ALTURA_SULCOS_MODELO_BANDA"),
                rSet.getBigDecimal("VALOR_BANDA"));
        return modeloBanda;
    }
}
