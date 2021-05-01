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
    Long codPneu;
    @NotNull
    Integer posicao;
    @Nullable
    BigDecimal psi;
    @NotNull
    Integer vidaMomentoAfericao;
    @Nullable
    BigDecimal alturaSulcoInterno;
    @Nullable
    BigDecimal alturaSulcoCentralInterno;
    @Nullable
    BigDecimal alturaSulcoCentralExterno;
    @Nullable
    BigDecimal alturaSulcoExterno;
}
