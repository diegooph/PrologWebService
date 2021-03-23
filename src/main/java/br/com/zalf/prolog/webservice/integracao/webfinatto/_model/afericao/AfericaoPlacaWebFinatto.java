package br.com.zalf.prolog.webservice.integracao.webfinatto._model.afericao;

import lombok.Data;
import org.jetbrains.annotations.NotNull;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class AfericaoPlacaWebFinatto {
    @NotNull
    private final String codEmpresa;
    @NotNull
    private final String codFilial;
    @NotNull
    private final String placaAfericao;
    @NotNull
    private final String cpfColaboradorAfericao;
    @NotNull
    private final Long kmMomentoAfericao;
    @NotNull
    private final Long tempoRealizacaoAfericaoEmMillis;
    @NotNull
    private final LocalDateTime dataHoraAfericaoUtc;
    @NotNull
    private final LocalDateTime dataHoraAfericaoTimeZoneAplicado;
    @NotNull
    private final String tipoMedicaoColetadaAfericao;
    @NotNull
    private final List<MedicaoAfericaoWebFinatto> medicoes;
}
