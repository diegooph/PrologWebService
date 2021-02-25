package br.com.zalf.prolog.webservice.integracao.praxio.ordensservicos.soap;

import br.com.zalf.prolog.webservice.commons.util.Log;
import br.com.zalf.prolog.webservice.integracao.network.OkHttp;
import br.com.zalf.prolog.webservice.integracao.praxio.GlobusPiccoloturConstants;
import br.com.zalf.prolog.webservice.integracao.praxio.ordensservicos.model.error.GlobusPiccoloturException;
import lombok.AllArgsConstructor;
import okhttp3.*;
import org.jetbrains.annotations.NotNull;

/**
 * Created on 2021-02-25
 *
 * @author Luiz Felipe (https://github.com/luizfp)
 */
@AllArgsConstructor
public final class SoapRequesterGlobusPiccolotur {
    @NotNull
    private static final String TAG = SoapRequesterGlobusPiccolotur.class.getSimpleName();
    @NotNull
    private final SoapHandlerGlobusPiccolotur soapHandler;

    @NotNull
    public OrdemServicoResponseDto enviarItensNokOrdemServico(
            @NotNull final OrdemServicoHolderDto ordemServico) throws Throwable {

        Log.d(TAG, "Request: " + ordemServico);
        final String xmlInput = soapHandler.generateSoapRequestOrdemServico(ordemServico);

        final Request request = new Request.Builder()
                .url(GlobusPiccoloturConstants.WSDL_LOCATION)
                .addHeader("SOAPAction", GlobusPiccoloturConstants.METODO_ENVIO_OS_SOAP_ACTION)
                .post(RequestBody.create(MediaType.parse("text/xml"), xmlInput))
                .build();
        final OkHttpClient okHttpClient = OkHttp.provideNetworkDefaultClient();
        final Call call = okHttpClient.newCall(request);
        final Response httpResponse = call.execute();

        if (httpResponse.isSuccessful() && httpResponse.body() != null) {
            final String responseString = httpResponse.body().string();
            final OrdemServicoResponseDto response =
                    soapHandler.parseSoapResponseOrdemServico(responseString);
            Log.d(TAG, "Response: " + response);
            return response;
        } else {
            throw new GlobusPiccoloturException(
                    "[INTEGRAÇÃO] Erro ao enviar O.S. para o sistema Globus-Piccolotur");
        }
    }
}
