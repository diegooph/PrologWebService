package br.com.zalf.prolog.webservice.integracao.sistema;

import br.com.zalf.prolog.webservice.integracao.IntegradorProLog;
import br.com.zalf.prolog.webservice.integracao.api.SistemaApiProLog;
import br.com.zalf.prolog.webservice.integracao.avacorpavilan.AvaCorpAvilan;
import br.com.zalf.prolog.webservice.integracao.avacorpavilan.requester.AvaCorpAvilanRequesterImpl;
import br.com.zalf.prolog.webservice.integracao.praxio.SistemaGlobusPiccolotur;
import br.com.zalf.prolog.webservice.integracao.praxio.data.GlobusPiccoloturRequesterImpl;
import br.com.zalf.prolog.webservice.integracao.protheusnepomuceno.data.ProtheusNepomucenoRequesterImpl;
import br.com.zalf.prolog.webservice.integracao.protheusnepomuceno.SistemaProtheusNepomuceno;
import br.com.zalf.prolog.webservice.integracao.protheusrodalog.ProtheusRodalogRequesterImpl;
import br.com.zalf.prolog.webservice.integracao.protheusrodalog.SistemaProtheusRodalog;
import br.com.zalf.prolog.webservice.integracao.rodoparhorizonte.SistemaRodoparHorizonte;
import br.com.zalf.prolog.webservice.integracao.rodoparhorizonte.data.RodoparHorizonteRequesterImpl;
import br.com.zalf.prolog.webservice.integracao.transport.SistemaTransportTranslecchi;
import org.jetbrains.annotations.NotNull;

/**
 * Created by luiz on 7/17/17.
 */
public final class SistemasFactory {

    private SistemasFactory() {
        throw new IllegalStateException(SistemasFactory.class.getSimpleName() + " cannot be instantiated!");
    }

    public static Sistema createSistema(
            @NotNull final SistemaKey sistemaKey,
            @NotNull final IntegradorProLog integradorProLog,
            @NotNull final String userToken) {

        switch (sistemaKey) {
            case AVACORP_AVILAN:
                return new AvaCorpAvilan(
                        new AvaCorpAvilanRequesterImpl(),
                        sistemaKey,
                        integradorProLog,
                        userToken);
            case TRANSPORT_TRANSLECCHI:
                return new SistemaTransportTranslecchi(
                        sistemaKey,
                        integradorProLog,
                        userToken);
            case PROTHEUS_RODALOG:
                return new SistemaProtheusRodalog(
                        new ProtheusRodalogRequesterImpl(),
                        sistemaKey,
                        integradorProLog,
                        userToken);
            case PROTHEUS_NEPOMUCENO:
                return new SistemaProtheusNepomuceno(
                        new ProtheusNepomucenoRequesterImpl(),
                        sistemaKey,
                        integradorProLog,
                        userToken);
            case GLOBUS_PICCOLOTUR:
                return new SistemaGlobusPiccolotur(
                        new GlobusPiccoloturRequesterImpl(),
                        sistemaKey,
                        integradorProLog,
                        userToken);
            case RODOPAR_HORIZONTE:
                return new SistemaRodoparHorizonte(
                        new RodoparHorizonteRequesterImpl(),
                        integradorProLog,
                        sistemaKey,
                        userToken);
            case API_PROLOG:
                return new SistemaApiProLog(
                        integradorProLog,
                        sistemaKey,
                        userToken);
            default:
                throw new IllegalStateException("Nenhum sistema encontrado com a chave: " + sistemaKey.getKey());
        }
    }
}