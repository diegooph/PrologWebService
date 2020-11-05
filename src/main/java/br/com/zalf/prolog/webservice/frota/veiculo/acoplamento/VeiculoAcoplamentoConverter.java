package br.com.zalf.prolog.webservice.frota.veiculo.acoplamento;

import br.com.zalf.prolog.webservice.frota.veiculo.acoplamento._model.VeiculoAcoplamento;
import org.jetbrains.annotations.NotNull;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Created on 2020-11-04
 *
 * @author Thais Francisco (https://github.com/thaisksf)
 */
public final class VeiculoAcoplamentoConverter {
    @NotNull
    public static VeiculoAcoplamento createVeiculoAcoplamento(@NotNull final ResultSet rSet) throws SQLException {
        return new VeiculoAcoplamento(
                rSet.getString("placa"),
                rSet.getString("identificador_frota"),
                rSet.getLong("km"),
                rSet.getString("posicao"),
                rSet.getString("acao"));
    }
}