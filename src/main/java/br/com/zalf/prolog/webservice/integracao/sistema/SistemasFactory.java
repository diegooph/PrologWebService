package br.com.zalf.prolog.webservice.integracao.sistema;

import br.com.zalf.prolog.webservice.integracao.IntegradorProLog;
import br.com.zalf.prolog.webservice.integracao.RecursoIntegrado;
import br.com.zalf.prolog.webservice.integracao.api.SistemaApiProLog;
import br.com.zalf.prolog.webservice.integracao.avacorpavilan.SistemaAvaCorpAvilan;
import br.com.zalf.prolog.webservice.integracao.praxio.SistemaGlobusPiccolotur;
import br.com.zalf.prolog.webservice.integracao.praxio.data.GlobusPiccoloturRequesterImpl;
import br.com.zalf.prolog.webservice.integracao.praxio.ordensservicos.soap.SoapHandlerGlobusPiccolotur;
import br.com.zalf.prolog.webservice.integracao.praxio.ordensservicos.soap.SoapRequesterGlobusPiccolotur;
import br.com.zalf.prolog.webservice.integracao.protheusnepomuceno.SistemaProtheusNepomuceno;
import br.com.zalf.prolog.webservice.integracao.protheusnepomuceno.data.ProtheusNepomucenoRequesterImpl;
import br.com.zalf.prolog.webservice.integracao.rodoparhorizonte.SistemaRodoparHorizonte;
import br.com.zalf.prolog.webservice.integracao.rodoparhorizonte.data.RodoparHorizonteRequesterImpl;
import br.com.zalf.prolog.webservice.integracao.transport.SistemaTransportTranslecchi;
import br.com.zalf.prolog.webservice.integracao.webfinatto.SistemaWebFinatto;
import br.com.zalf.prolog.webservice.integracao.webfinatto.data.SistemaWebFinattoRequesterImpl;
import org.jetbrains.annotations.NotNull;

/**
 * Created by luiz on 7/17/17.
 */
public final class SistemasFactory {

    private SistemasFactory() {
        throw new IllegalStateException(SistemasFactory.class.getSimpleName() + " cannot be instantiated!");
    }

    public static Sistema createSistema(@NotNull final SistemaKey sistemaKey,
                                        @NotNull final RecursoIntegrado recursoIntegrado,
                                        @NotNull final IntegradorProLog integradorProLog,
                                        @NotNull final String userToken) {
        switch (sistemaKey) {
            case AVACORP_AVILAN:
                return new SistemaAvaCorpAvilan(sistemaKey, recursoIntegrado, integradorProLog, userToken);
            case TRANSPORT_TRANSLECCHI:
                return new SistemaTransportTranslecchi(
                        sistemaKey,
                        recursoIntegrado,
                        integradorProLog,
                        userToken);
            case PROTHEUS_NEPOMUCENO:
                return new SistemaProtheusNepomuceno(
                        new ProtheusNepomucenoRequesterImpl(),
                        sistemaKey,
                        recursoIntegrado,
                        integradorProLog,
                        userToken);
            case GLOBUS_PICCOLOTUR:
                final SoapRequesterGlobusPiccolotur soapRequester =
                        new SoapRequesterGlobusPiccolotur(new SoapHandlerGlobusPiccolotur());
                return new SistemaGlobusPiccolotur(
                        new GlobusPiccoloturRequesterImpl(soapRequester),
                        sistemaKey,
                        recursoIntegrado,
                        integradorProLog,
                        userToken);
            case RODOPAR_HORIZONTE:
                return new SistemaRodoparHorizonte(
                        new RodoparHorizonteRequesterImpl(),
                        integradorProLog,
                        sistemaKey,
                        recursoIntegrado,
                        userToken);
            case API_PROLOG:
                return new SistemaApiProLog(
                        integradorProLog,
                        sistemaKey,
                        recursoIntegrado,
                        userToken);
            case WEB_FINATTO:
                return new SistemaWebFinatto(
                        new SistemaWebFinattoRequesterImpl(),
                        sistemaKey,
                        recursoIntegrado,
                        integradorProLog,
                        userToken);
            default:
                throw new IllegalStateException("Nenhum sistema encontrado com a chave: " + sistemaKey.getKey());
        }
    }
}