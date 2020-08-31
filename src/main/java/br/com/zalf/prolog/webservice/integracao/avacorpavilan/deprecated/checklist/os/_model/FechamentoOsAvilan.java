package br.com.zalf.prolog.webservice.integracao.avacorpavilan.deprecated.checklist.os._model;

import com.google.gson.annotations.SerializedName;
import lombok.Data;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.time.LocalDateTime;

import static br.com.zalf.prolog.webservice.integracao.avacorpavilan.deprecated.AvaCorpAvilanConstants.*;

/**
 * Created on 2020-08-20
 *
 * @author Gustavo Navarro (https://github.com/gustavocnp95)
 */
@Data
public class FechamentoOsAvilan {
    @NotNull
    @SerializedName("Grupo")
    private final String grupo = COD_GRUPO_AVILAN;
    @NotNull
    @SerializedName("Empresa")
    private final String empresa = COD_EMPRESA_AVILAN;
    @Nullable
    @SerializedName("Filial")
    private final String filial;
    @Nullable
    @SerializedName("Unidade")
    private final String unidade;
    @Nullable
    @SerializedName("Dtinc")
    private final LocalDateTime dataHoraInclusaoServico;
    @Nullable
    @SerializedName("ServicoRealizado")
    private final String servicoRealizado;
    @Nullable
    @SerializedName("Complemento")
    private final String complemento;
    @NotNull
    @SerializedName("ObjetivoOrdemServico")
    private final String objetivoOrdemServico = COD_OBJETIVO_ORDEM_SERVICO_AVILAN;
}
