package br.com.zalf.prolog.webservice.integracao.avacorpavilan._model;

import com.google.gson.annotations.SerializedName;
import lombok.Data;
import org.jetbrains.annotations.Nullable;

import java.time.LocalDateTime;

import static br.com.zalf.prolog.webservice.integracao.avacorpavilan.AvaCorpAvilanConstants.*;

/**
 * Created on 2020-08-20
 *
 * @author Gustavo Navarro (https://github.com/gustavocnp95)
 */
@Data
public final class ServicoAvaCorpAvilan {
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
    @Nullable
    @SerializedName("Dtinc")
    private final LocalDateTime dataHoraInclusaoServico;
    @Nullable
    @SerializedName("ServicoRealizado")
    private final Integer servicoRealizado;
    @Nullable
    @SerializedName("Complemento")
    private final String complemento;
    @SerializedName("ObjetivoOrdemServico")
    private final short objetivoOrdemServico = COD_OBJETIVO_ORDEM_SERVICO_AVILAN;
}
