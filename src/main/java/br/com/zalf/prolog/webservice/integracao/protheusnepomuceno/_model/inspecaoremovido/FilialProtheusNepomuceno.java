package br.com.zalf.prolog.webservice.integracao.protheusnepomuceno._model.inspecaoremovido;

import com.google.gson.annotations.SerializedName;
import lombok.Data;
import org.jetbrains.annotations.NotNull;

import static br.com.zalf.prolog.webservice.integracao.protheusnepomuceno.utils.ProtheusNepomucenoConstants.DEFAULT_COD_RESPOSTA_SEPARATOR;

@Data
public class FilialProtheusNepomuceno implements CamposPersonalizadosResposta {
    @NotNull
    @SerializedName("Codigo")
    private final String codOrigemFilial;
    @NotNull
    @SerializedName("Nome")
    private final String nomeOrigemFilial;

    @Override
    @NotNull
    public String getRespostaFormatada() {
        return this.codOrigemFilial + DEFAULT_COD_RESPOSTA_SEPARATOR + this.nomeOrigemFilial;
    }
}