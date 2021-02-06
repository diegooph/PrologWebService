package br.com.zalf.prolog.webservice.frota.veiculo.acoplamento.historico;

import br.com.zalf.prolog.webservice.commons.network.metadata.Optional;
import br.com.zalf.prolog.webservice.commons.network.metadata.Platform;
import br.com.zalf.prolog.webservice.commons.network.metadata.Required;
import br.com.zalf.prolog.webservice.commons.network.metadata.UsedBy;
import br.com.zalf.prolog.webservice.errorhandling.exception.ProLogException;
import br.com.zalf.prolog.webservice.interceptors.auth.Secured;
import br.com.zalf.prolog.webservice.permissao.pilares.Pilares;
import org.jetbrains.annotations.NotNull;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.List;

/**
 * Created on 2020-11-03
 *
 * @author Thais Francisco (https://github.com/thaisksf)
 */
@Path("/veiculos/acoplamentos")
@Consumes(MediaType.APPLICATION_JSON + ";charset=utf-8")
@Produces(MediaType.APPLICATION_JSON + ";charset=utf-8")
public final class VeiculoAcoplamentoHistoricoResource {
    @NotNull
    private final VeiculoAcoplamentoHistoricoService service = new VeiculoAcoplamentoHistoricoService();

    @GET
    @Path("/busca")
    @UsedBy(platforms = Platform.WEBSITE)
    @Secured(permissions
            = {Pilares.Frota.Veiculo.VISUALIZAR, Pilares.Frota.Veiculo.CADASTRAR, Pilares.Frota.Veiculo.ALTERAR})
    public Response getVeiculoAcoplamentosHistorico(
            @QueryParam("codUnidades") @Required final List<Long> codUnidades,
            @QueryParam("codVeiculos") @Optional final List<Long> codVeiculos,
            @QueryParam("dataInicial") @Optional final String dataInicial,
            @QueryParam("dataFinal") @Optional final String dataFinal) throws ProLogException {
        return service.getVeiculoAcoplamentosHistorico(codUnidades, codVeiculos, dataInicial, dataFinal);
    }
}