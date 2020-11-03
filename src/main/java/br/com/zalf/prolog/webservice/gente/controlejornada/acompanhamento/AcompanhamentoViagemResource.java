package br.com.zalf.prolog.webservice.gente.controlejornada.acompanhamento;

import br.com.zalf.prolog.webservice.commons.util.Optional;
import br.com.zalf.prolog.webservice.commons.util.Required;
import br.com.zalf.prolog.webservice.errorhandling.exception.ProLogException;
import br.com.zalf.prolog.webservice.gente.controlejornada.acompanhamento.andamento.ViagemEmAndamento;
import br.com.zalf.prolog.webservice.gente.controlejornada.acompanhamento.descanso.ViagemEmDescanso;
import br.com.zalf.prolog.webservice.interceptors.auth.Secured;
import br.com.zalf.prolog.webservice.interceptors.debug.ConsoleDebugLog;
import br.com.zalf.prolog.webservice.permissao.pilares.Pilares;
import org.jetbrains.annotations.NotNull;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.util.List;

/**
 * Created on 31/01/19
 *
 * @author Luiz Felipe (https://github.com/luizfp)
 */
@ConsoleDebugLog
@Path("/controle-jornada/acompanhamento-viagens")
@Consumes(MediaType.APPLICATION_JSON + ";charset=utf-8")
@Produces(MediaType.APPLICATION_JSON + ";charset=utf-8")
public final class AcompanhamentoViagemResource {
    @NotNull
    private final AcompanhamentoViagemService service = new AcompanhamentoViagemService();

    @GET
    @Secured(permissions = {Pilares.Gente.Relatorios.INTERVALOS})
    @Path("/colaboradores-em-descanso")
    public ViagemEmDescanso getColaboradoresEmDescanso(
            @QueryParam("codUnidade") @Required final Long codUnidade,
            @QueryParam("codCargos") @Required final List<Long> codCargos) throws ProLogException {
        return service.getColaboradoresEmDescanso(codUnidade, codCargos);
    }

    @GET
    @Secured(permissions = {Pilares.Gente.Relatorios.INTERVALOS})
    @Path("/viagens-em-andamento")
    public ViagemEmAndamento getViagensEmAndamento(
            @QueryParam("codUnidade") @Required final Long codUnidade,
            @QueryParam("codCargos") @Required final List<Long> codCargos) throws ProLogException {
        return service.getViagensEmAndamento(codUnidade, codCargos);
    }

    @GET
    @Secured(permissions = {Pilares.Gente.Relatorios.INTERVALOS})
    @Path("/marcacao-inicio-fim")
    public MarcacaoAgrupadaAcompanhamento getMarcacaoInicioFim(
            @QueryParam("codUnidade") @Required final Long codUnidade,
            @QueryParam("codInicio") @Optional final Long codInicio,
            @QueryParam("codFim") @Optional final Long codFim) throws ProLogException {
        return service.getMarcacaoInicioFim(codUnidade, codInicio, codFim);
    }
}