package br.com.zalf.prolog.webservice.integracao.protheusnepomuceno._model.inspecaoremovido;

import com.google.gson.annotations.SerializedName;
import lombok.Data;
import org.jetbrains.annotations.NotNull;

@Data
public class PneuInspecaoRemovido {
    @NotNull
    private final String codSistemaPneu;
    @NotNull
    private final String codFogoPneu;
    @NotNull
    private final String codDimensaoPneu;
    @NotNull
    private final String nomeDimensaoPneu;
    @NotNull
    private final String codModeloPneu;
    @NotNull
    private final String nomeModeloPneu;
    @NotNull
    private final String codCicloPneu;
    @NotNull
    private final String nomeCicloPneu;
    @NotNull
    private final String codBandaPneu;
    @NotNull
    private final String nomeBandaPneu;
    @NotNull
    private final String dot;
    @NotNull
    @SerializedName("medicoesAtuais")
    private final MedicaoAtualProtheusNepomuceno medicaoAtual;
}