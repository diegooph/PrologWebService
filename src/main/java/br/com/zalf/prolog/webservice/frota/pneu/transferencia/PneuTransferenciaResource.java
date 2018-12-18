package br.com.zalf.prolog.webservice.frota.pneu.transferencia;

import br.com.zalf.prolog.webservice.commons.network.Response;
import br.com.zalf.prolog.webservice.commons.util.Platform;
import br.com.zalf.prolog.webservice.commons.util.UsedBy;
import br.com.zalf.prolog.webservice.errorhandling.exception.ProLogException;
import br.com.zalf.prolog.webservice.frota.pneu.transferencia.model.listagem.PneuTransferenciaListagem;
import br.com.zalf.prolog.webservice.frota.pneu.transferencia.model.realizacao.PneuTransferenciaRealizacao;
import br.com.zalf.prolog.webservice.frota.pneu.transferencia.model.visualizacao.PneuTransferenciaProcessoVisualizacao;
import br.com.zalf.prolog.webservice.interceptors.auth.Secured;
import br.com.zalf.prolog.webservice.interceptors.log.DebugLog;
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
@Secured
@Path("/transferencia")
@Consumes(MediaType.APPLICATION_JSON + ";charset=utf-8")
@Produces(MediaType.APPLICATION_JSON + ";charset=utf-8")
public class PneuTransferenciaResource {
    @NotNull
    private final PneuTransferenciaService service = new PneuTransferenciaService();


    @POST
    @UsedBy(platforms = Platform.WEBSITE)
    @Path("/pneu-transferencia-realizacao")
    public Response transferencia(PneuTransferenciaRealizacao pneuTransferenciaRealizacao) throws ProLogException {
        service.insertTransferencia(pneuTransferenciaRealizacao);
        return Response.ok("Transferencia realizada com sucesso");
    }

    @GET
    @UsedBy(platforms = Platform.WEBSITE)
    @Path("/pneu-transferencia-listagem-list")
    public List<PneuTransferenciaListagem> getPneuTransferenciaListagem(
            @QueryParam("codUnidadesOrigem") final List<Long> codUnidadesOrigem,
            @QueryParam("codUnidadesDestino") final List<Long> codUnidadesDestino,
            @QueryParam("dataInicial") final String dataInicial,
            @QueryParam("dataFinal") final String dataFinal) throws ProLogException {
        return service.transferenciaListagem(
                codUnidadesOrigem,
                codUnidadesDestino,
                dataInicial,
                dataFinal);
    }

    @GET
    @UsedBy(platforms = Platform.WEBSITE)
    @Path("/pneu-transferencia-visualizacao-transferencia")
    public PneuTransferenciaProcessoVisualizacao getPneuTransferenciaVisualizacao(
            @QueryParam("codTransferencia") final Long codTransferencia) throws ProLogException {
        return service.transferenciaVisualizacao(codTransferencia);
    }
}