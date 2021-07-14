package br.com.zalf.prolog.webservice.v3.fleet.kmprocessos;

import br.com.zalf.prolog.webservice.commons.network.SuccessResponse;
import br.com.zalf.prolog.webservice.interceptors.auth.ColaboradorAutenticado;
import br.com.zalf.prolog.webservice.interceptors.auth.Secured;
import br.com.zalf.prolog.webservice.interceptors.debug.ConsoleDebugLog;
import br.com.zalf.prolog.webservice.permissao.pilares.Pilares;
import br.com.zalf.prolog.webservice.v3.fleet.kmprocessos._model.AlteracaoKmProcessoDto;
import br.com.zalf.prolog.webservice.v3.fleet.kmprocessos._model.AlteracaoKmProcessoMapper;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import javax.validation.Valid;
import javax.ws.rs.Consumes;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.SecurityContext;

/**
 * Created on 2021-03-25
 *
 * @author Luiz Felipe (https://github.com/luizfp)
 */
@Controller
@ConsoleDebugLog
@Path("/api/v3/processos-coleta-km")
@Consumes(MediaType.APPLICATION_JSON + ";charset=utf-8")
@Produces(MediaType.APPLICATION_JSON + ";charset=utf-8")
public final class AlteracaoKmProcessosResource {
    @NotNull
    private final AlteracaoKmProcessosService service;
    @NotNull
    private final AlteracaoKmProcessoMapper mapper;

    @Autowired
    public AlteracaoKmProcessosResource(@NotNull final AlteracaoKmProcessosService service,
                                        @NotNull final AlteracaoKmProcessoMapper mapper) {
        this.service = service;
        this.mapper = mapper;
    }

    @PUT
    @Secured(permissions = Pilares.Frota.Veiculo.ALTERAR)
    @NotNull
    public SuccessResponse updateKmProcesso(@Context final SecurityContext securityContext,
                                            @NotNull @Valid final AlteracaoKmProcessoDto alteracaoKmProcesso) {
        final ColaboradorAutenticado colaborador = (ColaboradorAutenticado) securityContext.getUserPrincipal();
        service.updateKmProcesso(mapper.toAlteracaoKmProcesso(alteracaoKmProcesso, colaborador.getCodigo()));
        return new SuccessResponse(null, "KM atualizado com sucesso!");
    }
}
