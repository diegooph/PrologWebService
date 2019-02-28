package br.com.zalf.prolog.webservice.integracao.protheusrodalog;

import br.com.zalf.prolog.webservice.commons.util.Platform;
import br.com.zalf.prolog.webservice.commons.util.ProLogCustomHeaders;
import br.com.zalf.prolog.webservice.commons.util.Required;
import br.com.zalf.prolog.webservice.commons.util.UsedBy;
import br.com.zalf.prolog.webservice.errorhandling.exception.GenericException;
import br.com.zalf.prolog.webservice.errorhandling.exception.ProLogException;
import br.com.zalf.prolog.webservice.integracao.protheusrodalog.model.AfericaoProtheusRodalog;
import br.com.zalf.prolog.webservice.integracao.protheusrodalog.model.CronogramaAfericaoProtheusRodalog;
import br.com.zalf.prolog.webservice.integracao.protheusrodalog.model.NovaAfericaoPlacaProtheusRodalog;
import br.com.zalf.prolog.webservice.interceptors.debugenv.ResourceDebugOnly;
import br.com.zalf.prolog.webservice.interceptors.log.DebugLog;
import org.jetbrains.annotations.NotNull;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;

/**
 * Created on 28/02/19.
 *
 * @author Diogenes Vanzela (https://github.com/diogenesvanzella)
 */
@Path("/dummies/rodalog-protheus")
@DebugLog
@ResourceDebugOnly
@Consumes(MediaType.APPLICATION_JSON + ";charset=utf-8")
@Produces(MediaType.APPLICATION_JSON + ";charset=utf-8")
public class DummyProtheusRodalogResource {

    @POST
    @Path("/inserir-afericao")
    @UsedBy(platforms = Platform.INTEGRACOES)
    public Long insertAfericao(
            @HeaderParam(ProLogCustomHeaders.HEADER_TOKEN_INTEGRACAO) @Required final String tokenIntegracao,
            @QueryParam("codUnidade") @Required final Long codUnidade,
            @Required final AfericaoProtheusRodalog afericao) throws ProLogException {
        if (!tokenIntegracao.equals("tk33g4sbev1vi5l53okcugdsuk0q8lgtu8l14knuroqju9orob2")) {
            throw new GenericException("Token errado");
        }
        if (codUnidade != 29) {
            throw new GenericException("codUnidade errado");
        }
        if (afericao == null) {
            throw new GenericException("Aferição é null");
        }
        if (!validaAfericao(afericao)) {
            throw new GenericException("O objeto aferição não está no padrão");
        }
        return 1210L;
    }

    @GET
    @UsedBy(platforms = Platform.INTEGRACOES)
    @Path("/buscar-cronograma")
    public CronogramaAfericaoProtheusRodalog getCronogramaAfericao(
            @HeaderParam(ProLogCustomHeaders.HEADER_TOKEN_INTEGRACAO) @Required final String tokenIntegracao,
            @QueryParam("codUnidade") @Required final Long codUnidade) {
        return CronogramaAfericaoProtheusRodalog.createCronogramaDummy();
    }

    @GET
    @UsedBy(platforms = Platform.INTEGRACOES)
    @Path("/nova-afericao")
    public NovaAfericaoPlacaProtheusRodalog getNovaAfericaoPlaca(
            @HeaderParam(ProLogCustomHeaders.HEADER_TOKEN_INTEGRACAO) @Required final String tokenIntegracao,
            @QueryParam("codUnidade") @Required final Long codUnidade,
            @QueryParam("placa") @Required final String placa,
            @QueryParam("tipoAfericao") @Required final String tipoAfericao) {
        return NovaAfericaoPlacaProtheusRodalog.createNovaAfericaoDummy();
    }

    private boolean validaAfericao(@NotNull final AfericaoProtheusRodalog afericao) {
        return afericao.equals(AfericaoProtheusRodalog.getAfericaoDummy());
    }
}
