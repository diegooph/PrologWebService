package br.com.zalf.prolog.webservice.frota.pneu.transferencia;

import br.com.zalf.prolog.webservice.commons.network.Response;
import br.com.zalf.prolog.webservice.commons.util.Platform;
import br.com.zalf.prolog.webservice.commons.util.Required;
import br.com.zalf.prolog.webservice.commons.util.UsedBy;
import br.com.zalf.prolog.webservice.errorhandling.exception.ProLogException;
import br.com.zalf.prolog.webservice.frota.pneu.transferencia.model.listagem.PneuTransferenciaListagem;
import br.com.zalf.prolog.webservice.frota.pneu.transferencia.model.realizacao.PneuTransferenciaRealizacao;
import br.com.zalf.prolog.webservice.frota.pneu.transferencia.model.visualizacao.PneuTransferenciaProcessoVisualizacao;
import br.com.zalf.prolog.webservice.interceptors.auth.Secured;
import br.com.zalf.prolog.webservice.interceptors.log.DebugLog;
import br.com.zalf.prolog.webservice.permissao.pilares.Pilares;
import org.jetbrains.annotations.NotNull;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.util.List;

/**
 * Created on 12/12/18.
 *
 * @author Thais Francisco (https://github.com/thaisksf)
 */
@DebugLog
@Path("pneus/transferencias")
@Consumes(MediaType.APPLICATION_JSON + ";charset=utf-8")
@Produces(MediaType.APPLICATION_JSON + ";charset=utf-8")
@Secured(permissions = Pilares.Frota.TRANSFERENCIA_PNEUS_VEICULOS)
public final class PneuTransferenciaResource {
    @NotNull
    private final PneuTransferenciaService service = new PneuTransferenciaService();

    @POST
    @UsedBy(platforms = Platform.WEBSITE)
    public Response transferencia(PneuTransferenciaRealizacao pneuTransferenciaRealizacao) throws ProLogException {
        service.insertTransferencia(pneuTransferenciaRealizacao);
        return Response.ok("Transferência realizada com sucesso");
    }

    @GET
    @UsedBy(platforms = Platform.WEBSITE)
    public List<PneuTransferenciaListagem> getPneuTransferenciaListagem(
            @QueryParam("codUnidadesOrigem") @Required final List<Long> codUnidadesOrigem,
            @QueryParam("codUnidadesDestino") @Required final List<Long> codUnidadesDestino,
            @QueryParam("dataInicial") @Required final String dataInicial,
            @QueryParam("dataFinal") @Required final String dataFinal) throws ProLogException {
        return service.getListagem(
                codUnidadesOrigem,
                codUnidadesDestino,
                dataInicial,
                dataFinal);
    }

    @GET
    @UsedBy(platforms = Platform.WEBSITE)
    @Path("/{codTransferencia}")
    public PneuTransferenciaProcessoVisualizacao getPneuTransferenciaVisualizacao(
            @PathParam("codTransferencia") @Required final Long codTransferencia) throws ProLogException {
        return service.getTransferenciaVisualizacao(codTransferencia);
    }
}