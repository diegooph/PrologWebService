package br.com.zalf.prolog.webservice.gente.controlejornada.acompanhamento;

import br.com.zalf.prolog.webservice.commons.util.Required;
import br.com.zalf.prolog.webservice.errorhandling.exception.ProLogException;
import br.com.zalf.prolog.webservice.gente.controlejornada.acompanhamento.descanso.ViagemEmDescanso;
import br.com.zalf.prolog.webservice.interceptors.auth.Secured;
import br.com.zalf.prolog.webservice.interceptors.log.DebugLog;
import br.com.zalf.prolog.webservice.permissao.pilares.Pilares;
import org.jetbrains.annotations.NotNull;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.util.List;

/**
 * Created on 31/01/19
 *
 * @author Luiz Felipe (https://github.com/luizfp)
 */
@DebugLog
@Path("/controle-jornada/acompanhamento-viagens")
@Consumes(MediaType.APPLICATION_JSON + ";charset=utf-8")
@Produces(MediaType.APPLICATION_JSON + ";charset=utf-8")
public class AcompanhamentoViagemResource {
    @NotNull
    private final AcompanhamentoViagemService service = new AcompanhamentoViagemService();

    @GET
    @Secured(permissions = {Pilares.Gente.Relatorios.INTERVALOS})
    @Path("/colaboradores-em-descanso")
    public ViagemEmDescanso getColaboradoresEmDescanso(
            @QueryParam("codUnidade") @Required final Long codUnidade,
            @QueryParam("codCargos") @Required final List<Long> codCargos) throws ProLogException {
        return service.getColaboradoresEmDescanso(codUnidade, codCargos);
    }
}