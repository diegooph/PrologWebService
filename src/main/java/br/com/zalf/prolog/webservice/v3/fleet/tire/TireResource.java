package br.com.zalf.prolog.webservice.v3.fleet.tire;

import br.com.zalf.prolog.webservice.commons.network.PrologCustomHeaders;
import br.com.zalf.prolog.webservice.commons.network.SuccessResponse;
import br.com.zalf.prolog.webservice.commons.network.metadata.Optional;
import br.com.zalf.prolog.webservice.commons.network.metadata.Required;
import br.com.zalf.prolog.webservice.frota.pneu._model.StatusPneu;
import br.com.zalf.prolog.webservice.interceptors.ApiExposed;
import br.com.zalf.prolog.webservice.interceptors.auth.Secured;
import br.com.zalf.prolog.webservice.interceptors.debug.ConsoleDebugLog;
import br.com.zalf.prolog.webservice.permissao.pilares.Pilares;
import br.com.zalf.prolog.webservice.v3.fleet.tire._model.TireCreateDto;
import br.com.zalf.prolog.webservice.v3.fleet.tire._model.TireDto;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import javax.validation.Valid;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.util.List;

/**
 * Created on 2021-03-12
 *
 * @author Guilherme Steinert (https://github.com/steinert999)
 */
@Controller
@ConsoleDebugLog
@Path("/api/v3/tires")
@Consumes(MediaType.APPLICATION_JSON + ";charset=utf-8")
@Produces(MediaType.APPLICATION_JSON + ";charset=utf-8")
public class TireResource implements TireApiDoc {
    @NotNull
    private final TireService service;

    @Autowired
    public TireResource(@NotNull final TireService service) {
        this.service = service;
    }

    @POST
    @ApiExposed
    @Secured(permissions = Pilares.Frota.Pneu.CADASTRAR)
    @Override
    public SuccessResponse insert(
            @HeaderParam(PrologCustomHeaders.HEADER_TOKEN_INTEGRACAO) @Optional final String integrationToken,
            @QueryParam("ignoreDotValidation") @DefaultValue("true") final boolean ignoreDotValidation,
            @Valid final TireCreateDto tireCreateDto) throws Throwable {
        return service.insert(integrationToken, tireCreateDto, ignoreDotValidation);
    }

    @Override
    @GET
    @ApiExposed
    @Secured(permissions = {
            Pilares.Frota.Pneu.VISUALIZAR,
            Pilares.Frota.Pneu.CADASTRAR,
            Pilares.Frota.Pneu.ALTERAR,
            Pilares.Frota.OrdemServico.Pneu.CONSERTAR_ITEM,
            Pilares.Frota.Pneu.Movimentacao.MOVIMENTAR_VEICULO_ESTOQUE,
            Pilares.Frota.Pneu.Movimentacao.MOVIMENTAR_ANALISE,
            Pilares.Frota.Pneu.Movimentacao.MOVIMENTAR_DESCARTE})
    public List<TireDto> getAllTires(
            @QueryParam("branchesId") @Required final List<Long> branchesId,
            @QueryParam("tireStatus") @Optional final StatusPneu tireStatus,
            @QueryParam("limit") final int limit,
            @QueryParam("offset") final int offset) {
        return service.getAllTires(branchesId, tireStatus, limit, offset);
    }
}