package br.com.zalf.prolog.webservice.integracao.integrador._model;

import br.com.zalf.prolog.webservice.frota.pneu.afericao._model.TipoMedicaoColetadaAfericao;
import br.com.zalf.prolog.webservice.frota.pneu.afericao._model.TipoProcessoColetaAfericao;
import lombok.Data;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.time.LocalDateTime;

@Data
public final class AfericaoRealizadaAvulsa {
    @NotNull
    private final String codPneuCliente;
    @NotNull
    private final Long codUltimaAfericao;
    @NotNull
    private final LocalDateTime dataHoraUltimaAfericao;
    @NotNull
    private final String nomeColaboradorAfericao;
    @NotNull
    private final TipoMedicaoColetadaAfericao tipoMedicaoColetadaAfericao;
    @NotNull
    private final TipoProcessoColetaAfericao tipoProcessoColetaAfericao;
    @Nullable
    private final String placaAplicadoQuandoAferido;
}
