package br.com.zalf.prolog.webservice.frota.pneu.transferencia;

import br.com.zalf.prolog.webservice.commons.network.ResponseWithCod;
import br.com.zalf.prolog.webservice.commons.network.metadata.Platform;
import br.com.zalf.prolog.webservice.commons.network.metadata.Required;
import br.com.zalf.prolog.webservice.commons.network.metadata.UsedBy;
import br.com.zalf.prolog.webservice.errorhandling.exception.ProLogException;
import br.com.zalf.prolog.webservice.frota.pneu.transferencia._model.listagem.PneuTransferenciaListagem;
import br.com.zalf.prolog.webservice.frota.pneu.transferencia._model.realizacao.PneuTransferenciaRealizacao;
import br.com.zalf.prolog.webservice.frota.pneu.transferencia._model.visualizacao.PneuTransferenciaProcessoVisualizacao;
import br.com.zalf.prolog.webservice.interceptors.auth.Secured;
import br.com.zalf.prolog.webservice.interceptors.debug.ConsoleDebugLog;
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
@ConsoleDebugLog
@Path("/v2/pneus/transferencias")
@Consumes(MediaType.APPLICATION_JSON + ";charset=utf-8")
@Produces(MediaType.APPLICATION_JSON + ";charset=utf-8")
@Secured(permissions = Pilares.Frota.TRANSFERENCIA_PNEUS_VEICULOS)
public final class PneuTransferenciaResource {
    @NotNull
    private final PneuTransferenciaService service = new PneuTransferenciaService();

    @POST
    @UsedBy(platforms = Platform.WEBSITE)
    public ResponseWithCod transferencia(
            @HeaderParam("Authorization") @Required final String userToken,
            @Required final PneuTransferenciaRealizacao pneuTransferenciaRealizacao) throws ProLogException {
        return service.insertTransferencia(userToken, pneuTransferenciaRealizacao);
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