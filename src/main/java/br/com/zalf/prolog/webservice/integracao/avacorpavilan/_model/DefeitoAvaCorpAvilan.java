package br.com.zalf.prolog.webservice.integracao.avacorpavilan._model;

import com.google.gson.annotations.SerializedName;
import lombok.Data;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.time.LocalDateTime;
import java.util.List;

import static br.com.zalf.prolog.webservice.integracao.avacorpavilan.AvaCorpAvilanConstants.COD_EMPRESA_AVILAN;
import static br.com.zalf.prolog.webservice.integracao.avacorpavilan.AvaCorpAvilanConstants.COD_GRUPO_AVILAN;


/**
 * Created on 2020-08-20
 *
 * @author Gustavo Navarro (https://github.com/gustavocnp95)
 */
@Data
public final class DefeitoAvaCorpAvilan {
    @SerializedName("Grupo")
    private final short grupo = COD_GRUPO_AVILAN;
    @SerializedName("Empresa")
    private final short empresa = COD_EMPRESA_AVILAN;
    @Nullable
    @SerializedName("Filial")
    private final Integer filial;
    @Nullable
    @SerializedName("Unidade")
    private final Integer unidade;
    @NotNull
    @SerializedName("Dtinc")
    private final LocalDateTime dataHoraInclusaoItem;
    @Nullable
    @SerializedName("Defeito")
    private final Integer defeito;
    @NotNull
    @SerializedName("Complemento")
    private final String complemento;
    @NotNull
    @SerializedName("OrdemServicoDefeitoServicoIn")
    private final List<ServicoAvaCorpAvilan> servicosAvaCorpAvilan;
}
