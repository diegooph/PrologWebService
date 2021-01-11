package br.com.zalf.prolog.webservice.integracao.api.controlejornada;

import br.com.zalf.prolog.webservice.integracao.api.controlejornada._model.ApiMarcacao;
import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * Created on 30/08/19.
 *
 * @author Diogenes Vanzela (https://github.com/diogenesvanzella)
 */
public interface ApiMarcacaoDao {

    @NotNull
    List<ApiMarcacao> getMarcacoesRealizadas(@NotNull final String tokenIntegracao,
                                             @NotNull final Long codUltimaMarcacaoSincronizada) throws Throwable;
}
