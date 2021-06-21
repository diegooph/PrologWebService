package br.com.zalf.prolog.webservice.integracao.protheusnepomuceno._model.inspecaoremovido;

import lombok.Data;
import org.jetbrains.annotations.NotNull;

@Data
public class MedicaoAtualProtheusNepomuceno {
    @NotNull
    private final Double alturaSulcoInterno;
    @NotNull
    private final Double alturaSulcoCentralInterno;
    @NotNull
    private final Double alturaSulcoCentralExterno;
    @NotNull
    private final Double alturaSulcoExterno;
    @NotNull
    private final Double pressao;
}