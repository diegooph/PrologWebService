package br.com.zalf.prolog.webservice.integracao.protheusnepomuceno.data;

import br.com.zalf.prolog.webservice.integracao.protheusnepomuceno.model.AfericaoAvulsaProtheusNepomuceno;
import br.com.zalf.prolog.webservice.integracao.protheusnepomuceno.model.AfericaoPlacaProtheusNepomuceno;
import br.com.zalf.prolog.webservice.integracao.sistema.Requester;
import org.jetbrains.annotations.NotNull;

/**
 * Created on 3/10/20
 *
 * @author Wellington Moraes (https://github.com/wvinim)
 */
public interface ProtheusNepomucenoRequester extends Requester {
    @NotNull
    ResponseAfericaoProtheusNepomuceno insertAfericaoPlaca(
            @NotNull final AfericaoPlacaProtheusNepomuceno afericaoPlaca) throws Throwable;

    @NotNull
    ResponseAfericaoProtheusNepomuceno insertAfericaoAvulsa(
            @NotNull final AfericaoAvulsaProtheusNepomuceno afericaoAvulsa) throws Throwable;
}
