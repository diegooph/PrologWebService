package br.com.zalf.prolog.webservice.v3.frota.pneu;

import br.com.zalf.prolog.webservice.commons.network.PrologCustomHeaders;
import br.com.zalf.prolog.webservice.commons.network.SuccessResponse;
import br.com.zalf.prolog.webservice.commons.network.metadata.Optional;
import br.com.zalf.prolog.webservice.interceptors.ApiExposed;
import br.com.zalf.prolog.webservice.interceptors.auth.Secured;
import br.com.zalf.prolog.webservice.interceptors.debug.ConsoleDebugLog;
import br.com.zalf.prolog.webservice.permissao.pilares.Pilares;
import br.com.zalf.prolog.webservice.v3.frota.pneu._model.PneuCadastroDto;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import javax.validation.Valid;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;

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
public class PneuV3Resource implements PneuV3ApiDoc {
    @NotNull
    private final PneuV3Service service;

    @Autowired
    public PneuV3Resource(@NotNull final PneuV3Service service) {
        this.service = service;
    }

    @POST
    @ApiExposed
    @Secured(permissions = Pilares.Frota.Pneu.CADASTRAR)
    @Override
    @NotNull
    public SuccessResponse insert(
            @HeaderParam(PrologCustomHeaders.HEADER_TOKEN_INTEGRACAO) @Optional final String tokenIntegracao,
            @QueryParam("ignoreDotValidation") final boolean ignoreDotValidation,
            @Valid final PneuCadastroDto pneuCadastro) {
        return this.service.insert(tokenIntegracao, pneuCadastro, ignoreDotValidation);
    }
}