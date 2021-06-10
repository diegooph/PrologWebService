package br.com.zalf.prolog.webservice.customfields._model;

import br.com.zalf.prolog.webservice.commons.util.ListUtils;
import lombok.Data;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

/**
 * Created on 2020-03-19
 *
 * @author Luiz Felipe (https://github.com/luizfp)
 */
@Data
public final class CampoPersonalizadoResposta {
    @NotNull
    private final Long codCampo;
    @NotNull
    private final TipoCampoPersonalizado tipoCampo;
    @Nullable
    private final String resposta;
    @Nullable
    private final List<String> respostaListaSelecao;

    public boolean temResposta() {
        return resposta != null || respostaListaSelecao != null;
    }

    @Nullable
    public String getRespostaAsString() {
        return ListUtils.hasElements(respostaListaSelecao) ? respostaListaSelecao.get(0) : resposta;
    }
}
