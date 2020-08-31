package br.com.zalf.prolog.webservice.integracao.sistema;

import br.com.zalf.prolog.webservice.integracao.IntegradorProLog;
import br.com.zalf.prolog.webservice.integracao.RecursoIntegrado;
import br.com.zalf.prolog.webservice.integracao.api.SistemaApiProLog;
import br.com.zalf.prolog.webservice.integracao.avacorpavilan.SistemaAvaCorpAvilan;
import br.com.zalf.prolog.webservice.integracao.avacorpavilan.deprecated.AvaCorpAvilan;
import br.com.zalf.prolog.webservice.integracao.avacorpavilan.deprecated.requester.AvaCorpAvilanRequesterImpl;
import br.com.zalf.prolog.webservice.integracao.praxio.SistemaGlobusPiccolotur;
import br.com.zalf.prolog.webservice.integracao.praxio.data.GlobusPiccoloturRequesterImpl;
import br.com.zalf.prolog.webservice.integracao.protheusnepomuceno.SistemaProtheusNepomuceno;
import br.com.zalf.prolog.webservice.integracao.protheusnepomuceno.data.ProtheusNepomucenoRequesterImpl;
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
            @NotNull final RecursoIntegrado recursoIntegrado,
            @NotNull final IntegradorProLog integradorProLog,
            @NotNull final String userToken) {
        switch (sistemaKey) {
            case AVACORP_AVILAN_OLD:
                return new AvaCorpAvilan(
                        new AvaCorpAvilanRequesterImpl(),
                        sistemaKey,
                        recursoIntegrado,
                        integradorProLog,
                        userToken);
            case AVACORP_AVILAN:
                return new SistemaAvaCorpAvilan(sistemaKey, recursoIntegrado, integradorProLog, userToken);
            case TRANSPORT_TRANSLECCHI:
                return new SistemaTransportTranslecchi(
                        sistemaKey,
                        recursoIntegrado,
                        integradorProLog,
                        userToken);
            case PROTHEUS_RODALOG:
                return new SistemaProtheusRodalog(
                        new ProtheusRodalogRequesterImpl(),
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
                return new SistemaGlobusPiccolotur(
                        new GlobusPiccoloturRequesterImpl(),
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
            default:
                throw new IllegalStateException("Nenhum sistema encontrado com a chave: " + sistemaKey.getKey());
        }
    }
}