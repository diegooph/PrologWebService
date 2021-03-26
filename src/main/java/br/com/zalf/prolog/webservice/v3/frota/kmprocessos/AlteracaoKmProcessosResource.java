package br.com.zalf.prolog.webservice.v3.frota.kmprocessos;

import br.com.zalf.prolog.webservice.commons.network.SuccessResponse;
import br.com.zalf.prolog.webservice.interceptors.ApiExposed;
import br.com.zalf.prolog.webservice.interceptors.auth.Secured;
import br.com.zalf.prolog.webservice.interceptors.debug.ConsoleDebugLog;
import br.com.zalf.prolog.webservice.permissao.pilares.Pilares;
import br.com.zalf.prolog.webservice.v3.frota.kmprocessos._model.AlteracaoKmProcessoDto;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import javax.ws.rs.Consumes;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

/**
 * Created on 2021-03-25
 *
 * @author Luiz Felipe (https://github.com/luizfp)
 */
@Controller
@ConsoleDebugLog
@Path("/v3/processos-coleta-km")
@Consumes(MediaType.APPLICATION_JSON + ";charset=utf-8")
@Produces(MediaType.APPLICATION_JSON + ";charset=utf-8")
public final class AlteracaoKmProcessosResource {
    @NotNull
    private final AlteracaoKmProcessosService service;

    @Autowired
    public AlteracaoKmProcessosResource(@NotNull final AlteracaoKmProcessosService service) {
        this.service = service;
    }

    @ApiExposed
    @PUT
    @Secured(permissions = Pilares.Frota.Veiculo.ALTERAR)
    @NotNull
    public SuccessResponse updateKmProcesso(@NotNull final AlteracaoKmProcessoDto alteracaoKmProcesso) {
        service.updateKmProcesso(alteracaoKmProcesso);
        return new SuccessResponse(null, "KM atualizado com sucesso!");
    }
}
