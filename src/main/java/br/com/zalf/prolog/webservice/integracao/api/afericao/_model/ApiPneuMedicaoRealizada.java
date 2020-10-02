package br.com.zalf.prolog.webservice.integracao.api.afericao._model;

import br.com.zalf.prolog.webservice.frota.pneu.afericao._model.TipoMedicaoColetadaAfericao;
import br.com.zalf.prolog.webservice.frota.pneu.afericao._model.TipoProcessoColetaAfericao;
import lombok.Data;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.time.LocalDateTime;

/**
 * Created on 2020-05-06
 *
 * @author Natan Rotta (https://github.com/natanrotta)
 */
@Data
public class ApiPneuMedicaoRealizada {
    @NotNull
    private final Long codigoProcessoAfericao;
    @NotNull
    private final Long codUnidadeAfericao;
    @NotNull
    private final String cpfColaborador;
    @Nullable
    private final String identificadorFrota;
    @Nullable
    private final String placaVeiculoAferido;
    @NotNull
    private final Long codPneuAferido;
    @NotNull
    private final String numeroFogoPneu;
    @Nullable
    private final Double alturaSulcoInternoEmMilimetros;
    @Nullable
    private final Double alturaSulcoCentralInternoEmMilimetros;
    @Nullable
    private final Double alturaSulcoCentralExternoEmMilimetros;
    @Nullable
    private final Double alturaSulcoExternoEmMilimetros;
    @Nullable
    private final Double pressaoEmPsi;
    @Nullable
    private final Long kmVeiculoMomentoAfericao;
    @NotNull
    private final Long tempoRealizacaoEmSegundos;
    @NotNull
    private final Integer vidaPneuMomentoAfericao;
    @Nullable
    private final Integer posicaoPneuMomentoAfericao;
    @NotNull
    private final LocalDateTime dataHoraAfericaoEmUtc;
    @NotNull
    private final TipoMedicaoColetadaAfericao tipoMedicaoColetadaAfericao;
    @NotNull
    private final TipoProcessoColetaAfericao tipoProcessoColetaAfericao;
}
