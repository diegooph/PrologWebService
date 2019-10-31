package br.com.zalf.prolog.webservice.frota.pneu.banda;

import br.com.zalf.prolog.webservice.frota.pneu.banda._model.PneuMarcaBandaListagemVisualizacao;
import org.jetbrains.annotations.NotNull;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Created on 08/10/19.
 *
 * @author Thais Francisco (https://github.com/thaisksf)
 */
final class PneuBandaConverter {

    @NotNull
    static PneuMarcaBandaListagemVisualizacao createPneuMarcaBandaListagemVisualizacao(@NotNull final ResultSet rSet)
            throws SQLException {
        return new PneuMarcaBandaListagemVisualizacao(
                rSet.getLong("CODIGO"),
                rSet.getString("NOME"));
    }
}
