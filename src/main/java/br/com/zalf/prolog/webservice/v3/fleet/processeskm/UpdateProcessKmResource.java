package br.com.zalf.prolog.webservice.v3.fleet.processeskm;

import br.com.zalf.prolog.webservice.commons.network.SuccessResponse;
import br.com.zalf.prolog.webservice.interceptors.auth.ColaboradorAutenticado;
import br.com.zalf.prolog.webservice.interceptors.auth.Secured;
import br.com.zalf.prolog.webservice.interceptors.debug.ConsoleDebugLog;
import br.com.zalf.prolog.webservice.permissao.pilares.Pilares;
import br.com.zalf.prolog.webservice.v3.fleet.processeskm._model.UpdateProcessKmDto;
import br.com.zalf.prolog.webservice.v3.fleet.processeskm._model.UpdateProcessKmMapper;
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
@Path(UpdateProcessKmResource.RESOURCE_PATH)
@Consumes(MediaType.APPLICATION_JSON + ";charset=utf-8")
@Produces(MediaType.APPLICATION_JSON + ";charset=utf-8")
public final class UpdateProcessKmResource {
    @NotNull
    public static final String RESOURCE_PATH = "/api/v3/processos-coleta-km";
    @NotNull
    private final UpdateProcessKmService service;
    @NotNull
    private final UpdateProcessKmMapper mapper;

    @Autowired
    public UpdateProcessKmResource(@NotNull final UpdateProcessKmService service,
                                   @NotNull final UpdateProcessKmMapper mapper) {
        this.service = service;
        this.mapper = mapper;
    }

    @PUT
    @Secured(permissions = Pilares.Frota.Veiculo.ALTERAR)
    @NotNull
    public SuccessResponse updateKmProcesso(@Context final SecurityContext securityContext,
                                            @NotNull @Valid final UpdateProcessKmDto updateProcessKmDto) {
        final ColaboradorAutenticado user = (ColaboradorAutenticado) securityContext.getUserPrincipal();
        service.updateProcessKm(mapper.toUpdateProcessKm(updateProcessKmDto, user.getCodigo()));
        return new SuccessResponse(null, "Km successfully updated!");
    }
}
