package br.com.zalf.prolog.webservice.integracao.protheusnepomuceno._model.inspecaoremovido;

import com.google.gson.annotations.SerializedName;
import lombok.Data;
import org.jetbrains.annotations.NotNull;

import static br.com.zalf.prolog.webservice.integracao.protheusnepomuceno.utils.ProtheusNepomucenoConstants.DEFAULT_COD_RESPOSTA_SEPARATOR;

@Data
public class LipPneuProtheusNepomuceno implements CamposPersonalizadosResposta {
    @NotNull
    @SerializedName("Ocorrencia")
    private final String codLipPneu;
    @NotNull
    @SerializedName("NomeProb")
    private final String nomeLipPneu;

    @Override
    @NotNull
    public String getRespostaFormatada() {
        return this.codLipPneu + DEFAULT_COD_RESPOSTA_SEPARATOR + this.nomeLipPneu;
    }
}