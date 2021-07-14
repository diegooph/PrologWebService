package br.com.zalf.prolog.webservice.v3.fleet.pneu;

import br.com.zalf.prolog.webservice.commons.network.PrologCustomHeaders;
import br.com.zalf.prolog.webservice.commons.network.SuccessResponse;
import br.com.zalf.prolog.webservice.commons.network.metadata.Optional;
import br.com.zalf.prolog.webservice.commons.network.metadata.Required;
import br.com.zalf.prolog.webservice.frota.pneu._model.StatusPneu;
import br.com.zalf.prolog.webservice.interceptors.ApiExposed;
import br.com.zalf.prolog.webservice.interceptors.auth.Secured;
import br.com.zalf.prolog.webservice.interceptors.debug.ConsoleDebugLog;
import br.com.zalf.prolog.webservice.permissao.pilares.Pilares;
import br.com.zalf.prolog.webservice.v3.fleet.pneu._model.PneuCadastroDto;
import br.com.zalf.prolog.webservice.v3.fleet.pneu._model.PneuListagemDto;
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
@Path("/api/v3/pneus")
@Consumes(MediaType.APPLICATION_JSON + ";charset=utf-8")
@Produces(MediaType.APPLICATION_JSON + ";charset=utf-8")
public class PneuResource implements PneuApiDoc {
    @NotNull
    private final PneuService service;

    @Autowired
    public PneuResource(@NotNull final PneuService service) {
        this.service = service;
    }

    @POST
    @ApiExposed
    @Secured(permissions = Pilares.Frota.Pneu.CADASTRAR)
    @Override
    public SuccessResponse insert(
            @HeaderParam(PrologCustomHeaders.HEADER_TOKEN_INTEGRACAO) @Optional final String tokenIntegracao,
            @QueryParam("ignoreDotValidation") @DefaultValue("true") final boolean ignoreDotValidation,
            @Valid final PneuCadastroDto pneuCadastro) throws Throwable {
        return this.service.insert(tokenIntegracao, pneuCadastro, ignoreDotValidation);
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
    public List<PneuListagemDto> getPneusByStatus(
            @QueryParam("codUnidades") @Required final List<Long> codUnidades,
            @QueryParam("statusPneu") @Optional final StatusPneu statusPneu,
            @QueryParam("limit") final int limit,
            @QueryParam("offset") final int offset) {
        return service.getPneusByStatus(codUnidades, statusPneu, limit, offset);
    }
}