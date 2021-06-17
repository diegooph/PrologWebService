package br.com.zalf.prolog.webservice.v3.frota.veiculo;

import br.com.zalf.prolog.webservice.commons.network.PrologCustomHeaders;
import br.com.zalf.prolog.webservice.commons.network.SuccessResponse;
import br.com.zalf.prolog.webservice.commons.network.metadata.Optional;
import br.com.zalf.prolog.webservice.interceptors.ApiExposed;
import br.com.zalf.prolog.webservice.interceptors.auth.Secured;
import br.com.zalf.prolog.webservice.interceptors.debug.ConsoleDebugLog;
import br.com.zalf.prolog.webservice.permissao.pilares.Pilares;
import br.com.zalf.prolog.webservice.v3.frota.veiculo._model.VeiculoCadastroDto;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import javax.validation.Valid;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;

@ConsoleDebugLog
@Path("/api/v3/veiculos")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Controller
public class VeiculoResource implements VeiculoResourceApiDoc {
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
            @Valid final VeiculoCadastroDto veiculoCadastroDto) {
        return veiculoService.insert(tokenIntegracao, veiculoCadastroDto);
    }
}
