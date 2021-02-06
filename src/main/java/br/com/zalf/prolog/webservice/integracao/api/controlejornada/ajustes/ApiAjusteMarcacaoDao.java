package br.com.zalf.prolog.webservice.integracao.api.controlejornada.ajustes;

import br.com.zalf.prolog.webservice.integracao.api.controlejornada.ajustes.model.ApiAjusteMarcacao;
import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * Created on 02/09/19.
 *
 * @author Diogenes Vanzela (https://github.com/diogenesvanzella)
 */
public interface ApiAjusteMarcacaoDao {

    @NotNull
    List<ApiAjusteMarcacao> getAjustesMarcacaoRealizados(
            @NotNull final String tokenIntegracao,
            @NotNull final Long codUltimoAjusteMarcacaoSincronizado) throws Throwable;
}
