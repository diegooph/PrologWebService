package br.com.zalf.prolog.webservice.integracao.protheusnepomuceno.data;

import br.com.zalf.prolog.webservice.integracao.network.RestClient;
import br.com.zalf.prolog.webservice.integracao.protheusnepomuceno.model.AfericaoAvulsaProtheusNepomuceno;
import br.com.zalf.prolog.webservice.integracao.protheusnepomuceno.model.AfericaoPlacaProtheusNepomuceno;
import org.jetbrains.annotations.NotNull;
import retrofit2.Call;
import retrofit2.Response;

/**
 * Created on 3/10/20
 *
 * @author Wellington Moraes (https://github.com/wvinim)
 */
public final class ProtheusNepomucenoRequesterImpl implements ProtheusNepomucenoRequester {
    @NotNull
    @Override
    public ResponseAfericaoProtheusNepomuceno insertAfericaoPlaca(
            @NotNull final AfericaoPlacaProtheusNepomuceno afericaoPlaca) throws Throwable {
        final ProtheusNepomucenoRest service = RestClient.getService(ProtheusNepomucenoRest.class);
        final Call<ResponseAfericaoProtheusNepomuceno> call =
                service.insertAfericaoPlaca(afericaoPlaca);
        return handleResponse(call.execute());
    }

    @NotNull
    @Override
    public ResponseAfericaoProtheusNepomuceno insertAfericaoAvulsa(
            @NotNull final AfericaoAvulsaProtheusNepomuceno afericaoAvulsa) throws Throwable {
        final ProtheusNepomucenoRest service = RestClient.getService(ProtheusNepomucenoRest.class);
        final Call<ResponseAfericaoProtheusNepomuceno> call =
                service.insertAfericaoAvulsa(afericaoAvulsa);
        return handleResponse(call.execute());
    }

    private ResponseAfericaoProtheusNepomuceno handleResponse(final Response<ResponseAfericaoProtheusNepomuceno> execute) {
        return null;
    }
}
