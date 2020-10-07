package br.com.zalf.prolog.webservice.frota.veiculo.evolucaoKm;

import br.com.zalf.prolog.webservice.commons.util.NullIf;
import br.com.zalf.prolog.webservice.frota.veiculo.evolucaoKm._model.VeiculoEvolucaoKm;
import br.com.zalf.prolog.webservice.frota.veiculo.evolucaoKm._model.ProcessoEvolucaoKmEnum;
import org.jetbrains.annotations.NotNull;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;

/**
 * Created on 2020-10-07
 *
 * @author Thais Francisco (https://github.com/thaisksf)
 */
public final class VeiculoEvolucaoKmConverter {
    @NotNull
    public static VeiculoEvolucaoKm createVeiculoEvolucaoKm(@NotNull final ResultSet rSet) throws SQLException {
        return new VeiculoEvolucaoKm(
                ProcessoEvolucaoKmEnum.fromString(rSet.getString("processo")),
                rSet.getLong("cod_processo"),
                rSet.getObject("data_hora", LocalDateTime.class),
                rSet.getString("placa"),
                rSet.getLong("km_coletado"),
                NullIf.equalOrLess(rSet.getLong("variacao_km_entre_coletas"), 0),
                rSet.getLong("diferenca_km_atual_km_coletado"));
    }
}