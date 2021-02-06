package br.com.zalf.prolog.webservice.integracao.api.controlejornada.tipomarcacao;

import br.com.zalf.prolog.webservice.integracao.api.controlejornada.tipomarcacao._model.ApiTipoMarcacao;
import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * Created on 29/08/19.
 *
 * @author Diogenes Vanzela (https://github.com/diogenesvanzella)
 */
public interface ApiTipoMarcacaoDao {

    @NotNull
    List<ApiTipoMarcacao> getTiposMarcacoes(@NotNull final String tokenIntegracao,
                                            final boolean apenasTiposMarcacoesAtivos) throws Throwable;
}
