package br.com.zalf.prolog.webservice.integracao.protheusnepomuceno._model.inspecaoremovido;

import com.google.gson.annotations.SerializedName;
import lombok.Data;
import org.jetbrains.annotations.NotNull;

import java.util.List;

@Data
public class PneuRealizacaoInspecaoRemovido {
    @NotNull
    @SerializedName("pneuParaAnalise")
    private final PneuInspecaoRemovido pneuInspecaoRemovido;
    @NotNull
    private final List<LipPneuProtheusNepomuceno> lipsPneus;
    @NotNull
    private final List<FilialProtheusNepomuceno> filiais;
    @NotNull
    private final List<CausaSucataPneuProtheusNepomuceno> causasSucateamentoPneu;
}