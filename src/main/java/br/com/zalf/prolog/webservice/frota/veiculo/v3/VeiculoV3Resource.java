package br.com.zalf.prolog.webservice.frota.veiculo.v3;

import br.com.zalf.prolog.webservice.commons.network.SuccessResponse;
import br.com.zalf.prolog.webservice.frota.veiculo.v3._model.VeiculoCadastroDto;
import br.com.zalf.prolog.webservice.frota.veiculo.v3._model.VeiculoEntity;
import br.com.zalf.prolog.webservice.interceptors.ApiExposed;
import br.com.zalf.prolog.webservice.interceptors.auth.Secured;
import br.com.zalf.prolog.webservice.interceptors.debug.ConsoleDebugLog;
import br.com.zalf.prolog.webservice.permissao.pilares.Pilares;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import javax.validation.Valid;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

@ConsoleDebugLog
@Path("/v3/veiculos")
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

    @ApiExposed
    @POST
    @Secured(permissions = Pilares.Frota.Veiculo.CADASTRAR)
    @Path("/")
    public SuccessResponse insert(@Valid final VeiculoCadastroDto veiculoCadastroDto) {
        final VeiculoEntity veiculoEntity = veiculoMapper.toEntity(veiculoCadastroDto);
        return veiculoService.insert(veiculoEntity);
    }
}
