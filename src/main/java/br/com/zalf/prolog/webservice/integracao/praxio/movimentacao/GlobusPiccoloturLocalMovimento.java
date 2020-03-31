package br.com.zalf.prolog.webservice.integracao.praxio.movimentacao;

import com.google.gson.annotations.SerializedName;
import lombok.Data;
import org.jetbrains.annotations.NotNull;

import static br.com.zalf.prolog.webservice.integracao.praxio.GlobusPiccoloturConstants.COD_UNIDADE_NOME_LOCAL_SEPARATOR;

/**
 * Created on 2020-03-28
 *
 * @author Diogenes Vanzela (https://github.com/diogenesvanzella)
 */
@Data
public final class GlobusPiccoloturLocalMovimento {
    @SerializedName("unidade")
    @NotNull
    private final Long codUnidadeProlog;
    @SerializedName("codigo")
    @NotNull
    private final Long codLocalGlobus;
    @SerializedName("descricao")
    @NotNull
    private final String nomeLocalGlobus;

    @NotNull
    public String getLocalAsOpcaoSelecao() {
        return this.codUnidadeProlog.toString().concat(COD_UNIDADE_NOME_LOCAL_SEPARATOR).concat(this.nomeLocalGlobus);
    }
}
