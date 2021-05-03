package br.com.zalf.prolog.webservice.v3.frota.afericao._model;

import lombok.Value;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.math.BigDecimal;

/**
 * Created on 2021-04-12
 *
 * @author Guilherme Steinert (https://github.com/steinert999)
 */
@Value(staticConstructor = "of")
public class MedidaDto {
    @NotNull
    Long codPneuAfericao;
    @NotNull
    Integer posicaoPneuAplicadoMomentoAfericao;
    @NotNull
    Integer vidaPneuMomentoAfericao;
    @Nullable
    BigDecimal pressaoPneuEmPsi;
    @Nullable
    BigDecimal alturaSulcoInternoEmMilimetros;
    @Nullable
    BigDecimal alturaSulcoCentralInternoEmMilimetros;
    @Nullable
    BigDecimal alturaSulcoCentralExternoEmMilimetros;
    @Nullable
    BigDecimal alturaSulcoExternoEmMilimetros;
}
