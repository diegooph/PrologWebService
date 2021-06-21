package br.com.zalf.prolog.webservice.integracao.protheusnepomuceno._model.inspecaoremovido;

import com.google.gson.annotations.SerializedName;
import lombok.Data;
import org.jetbrains.annotations.NotNull;

import static br.com.zalf.prolog.webservice.integracao.protheusnepomuceno.utils.ProtheusNepomucenoConstants.DEFAULT_COD_RESPOSTA_SEPARATOR;

@Data
public class CausaSucataPneuProtheusNepomuceno implements CamposPersonalizadosResposta {
    @NotNull
    @SerializedName("Ocorrencia")
    private final String codCausaSacata;
    @NotNull
    @SerializedName("NomeProb")
    private final String nomeCausaSucata;

    @Override
    @NotNull
    public String getRespostaFormatada() {
        return this.codCausaSacata + DEFAULT_COD_RESPOSTA_SEPARATOR + this.nomeCausaSucata;
    }
}