package br.com.zalf.prolog.webservice.frota.pneu.aferidor.teste;

import br.com.zalf.prolog.webservice.commons.network.ResponseWithCod;
import br.com.zalf.prolog.webservice.commons.util.Platform;
import br.com.zalf.prolog.webservice.commons.util.Required;
import br.com.zalf.prolog.webservice.commons.util.UsedBy;
import br.com.zalf.prolog.webservice.frota.pneu.aferidor.teste.model.ProcedimentoTesteAferidor;
import br.com.zalf.prolog.webservice.frota.pneu.aferidor.teste.model.TesteAferidorExecutado;
import br.com.zalf.prolog.webservice.interceptors.auth.Secured;
import br.com.zalf.prolog.webservice.interceptors.log.DebugLog;
import br.com.zalf.prolog.webservice.permissao.pilares.Pilares;
import org.jetbrains.annotations.NotNull;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;

/**
 * Created on 2019-10-07
 *
 * @author Luiz Felipe (https://github.com/luizfp)
 */
@Path("/aferidores")
@DebugLog
@Consumes(MediaType.APPLICATION_JSON + ";charset=utf-8")
@Produces(MediaType.APPLICATION_JSON + ";charset=utf-8")
public final class TesteAferidorResource {
    @NotNull
    private final TesteAferidorService service = new TesteAferidorService();

    @GET
    @Secured(permissions = {
            Pilares.Frota.Afericao.REALIZAR_AFERICAO_PLACA,
            Pilares.Frota.Afericao.REALIZAR_AFERICAO_PNEU_AVULSO})
    @Path("/procedimento-teste")
    @UsedBy(platforms = Platform.ANDROID)
    public ProcedimentoTesteAferidor getProcedimentoTeste() {
        return service.getProcedimentoTeste();
    }

    @POST
    @Secured(permissions = {
            Pilares.Frota.Afericao.REALIZAR_AFERICAO_PLACA,
            Pilares.Frota.Afericao.REALIZAR_AFERICAO_PNEU_AVULSO})
    @Path("/procedimento-teste")
    @UsedBy(platforms = Platform.ANDROID)
    public ResponseWithCod insereTeste(@Required final TesteAferidorExecutado teste) {
        return service.
                insereTeste(teste);
    }
}