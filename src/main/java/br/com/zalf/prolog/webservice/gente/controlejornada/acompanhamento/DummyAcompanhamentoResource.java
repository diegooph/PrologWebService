package br.com.zalf.prolog.webservice.gente.controlejornada.acompanhamento;

import br.com.zalf.prolog.webservice.DummyData;
import br.com.zalf.prolog.webservice.commons.util.Platform;
import br.com.zalf.prolog.webservice.commons.util.UsedBy;
import br.com.zalf.prolog.webservice.gente.controlejornada.acompanhamento.andamento.ViagemEmAndamento;
import br.com.zalf.prolog.webservice.gente.controlejornada.acompanhamento.descanso.ViagemEmDescanso;
import br.com.zalf.prolog.webservice.gente.controlejornada.model.TipoInicioFim;
import br.com.zalf.prolog.webservice.interceptors.debug.ConsoleDebugLog;
import br.com.zalf.prolog.webservice.interceptors.debug.ResourceDebugOnly;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

/**
 * Created on 29/01/19
 *
 * @author Luiz Felipe (https://github.com/luizfp)
 */
@Path("/dummies")
@ConsoleDebugLog
@ResourceDebugOnly
@Consumes(MediaType.APPLICATION_JSON + ";charset=utf-8")
@Produces(MediaType.APPLICATION_JSON + ";charset=utf-8")
public final class DummyAcompanhamentoResource extends DummyData {

    @GET
    @UsedBy(platforms = Platform.WEBSITE)
    @Path("/colaboradores-com-viagem-em-andamento")
    public ViagemEmAndamento getViagensEmAndamento() {
        return ViagemEmAndamento.createDummy();
    }

    @GET
    @UsedBy(platforms = Platform.WEBSITE)
    @Path("/colaboradores-em-descanso")
    public ViagemEmDescanso getColaboradoresEmDescanso() {
        return ViagemEmDescanso.createDummy();
    }

    @GET
    @UsedBy(platforms = Platform.WEBSITE)
    @Path("/acompanhamento-viagem-marcacao-agrupada-com-inicio-e-fim")
    public MarcacaoAgrupadaAcompanhamento getMarcacaoAgrupadaInicioFim() {
        return MarcacaoAgrupadaAcompanhamento.createDummy(null);
    }

    @GET
    @UsedBy(platforms = Platform.WEBSITE)
    @Path("/acompanhamento-viagem-marcacao-agrupada-apenas-inicio")
    public MarcacaoAgrupadaAcompanhamento getMarcacaoAgrupadaInicio() {
        return MarcacaoAgrupadaAcompanhamento.createDummy(TipoInicioFim.MARCACAO_INICIO);
    }


    @GET
    @UsedBy(platforms = Platform.WEBSITE)
    @Path("/acompanhamento-viagem-marcacao-agrupada-apenas-fim")
    public MarcacaoAgrupadaAcompanhamento getMarcacaoAgrupadaFim() {
        return MarcacaoAgrupadaAcompanhamento.createDummy(TipoInicioFim.MARCACAO_FIM);
    }
}