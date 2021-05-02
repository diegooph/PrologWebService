package br.com.zalf.prolog.webservice.v3.frota.veiculo;

import br.com.zalf.prolog.webservice.commons.network.PrologCustomHeaders;
import br.com.zalf.prolog.webservice.commons.network.SuccessResponse;
import br.com.zalf.prolog.webservice.commons.network.metadata.Optional;
import br.com.zalf.prolog.webservice.interceptors.ApiExposed;
import br.com.zalf.prolog.webservice.interceptors.auth.Secured;
import br.com.zalf.prolog.webservice.interceptors.debug.ConsoleDebugLog;
import br.com.zalf.prolog.webservice.permissao.pilares.Pilares;
import br.com.zalf.prolog.webservice.v3.frota.veiculo._model.VeiculoCadastroDto;
import br.com.zalf.prolog.webservice.v3.frota.veiculo._model.VeiculoEntity;
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
public class VeiculoV3Resource implements VeiculoV3ResourceApiDoc {
    @NotNull
    private final VeiculoV3Service veiculoService;
    @NotNull
    private final VeiculoMapper veiculoMapper;

    @Autowired
    public VeiculoV3Resource(@NotNull final VeiculoV3Service veiculoService,
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
        final VeiculoEntity veiculoEntity = veiculoMapper.toEntity(veiculoCadastroDto);
        return veiculoService.insert(tokenIntegracao, veiculoEntity);
    }
}
