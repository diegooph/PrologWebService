package br.com.zalf.prolog.webservice.v3.fleet.vehicle;

import br.com.zalf.prolog.webservice.commons.network.PrologCustomHeaders;
import br.com.zalf.prolog.webservice.commons.network.SuccessResponse;
import br.com.zalf.prolog.webservice.commons.network.metadata.Optional;
import br.com.zalf.prolog.webservice.commons.network.metadata.Required;
import br.com.zalf.prolog.webservice.interceptors.ApiExposed;
import br.com.zalf.prolog.webservice.interceptors.auth.Secured;
import br.com.zalf.prolog.webservice.interceptors.debug.ConsoleDebugLog;
import br.com.zalf.prolog.webservice.permissao.pilares.Pilares;
import br.com.zalf.prolog.webservice.v3.fleet.vehicle._model.VeiculoCadastroDto;
import br.com.zalf.prolog.webservice.v3.fleet.vehicle._model.VeiculoListagemDto;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import javax.validation.Valid;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.util.List;

@ConsoleDebugLog
@Path("/api/v3/veiculos")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Controller
public class VeiculoResource implements VeiculoApiDoc {
    @NotNull
    private final VeiculoService veiculoService;
    @NotNull
    private final VeiculoMapper veiculoMapper;

    @Autowired
    public VeiculoResource(@NotNull final VeiculoService veiculoService,
                           @NotNull final VeiculoMapper veiculoMapper) {
        this.veiculoService = veiculoService;
        this.veiculoMapper = veiculoMapper;
    }

    @Override
    @ApiExposed
    @POST
    @Secured(permissions = Pilares.Frota.Veiculo.CADASTRAR)
    public SuccessResponse insert(
            @HeaderParam(PrologCustomHeaders.HEADER_TOKEN_INTEGRACAO) @Optional final String tokenIntegracao,
            @Valid final VeiculoCadastroDto veiculoCadastroDto) throws Throwable {
        return veiculoService.insert(tokenIntegracao, veiculoCadastroDto);
    }

    @GET
    @ApiExposed
    @Secured(permissions = {
            Pilares.Frota.Veiculo.VISUALIZAR,
            Pilares.Frota.Veiculo.ALTERAR,
            Pilares.Frota.Veiculo.CADASTRAR,
            Pilares.Frota.Checklist.VISUALIZAR_TODOS})
    @Override
    public List<VeiculoListagemDto> getListagemVeiculos(
            @QueryParam("codUnidades") @Required final List<Long> codUnidades,
            @QueryParam("incluirInativos") @DefaultValue("true") final boolean incluirInativos,
            @QueryParam("limit") final int limit,
            @QueryParam("offset") final int offset) {
        return veiculoMapper.toDto(veiculoService.getListagemVeiculos(codUnidades, incluirInativos, limit, offset));
    }
}
