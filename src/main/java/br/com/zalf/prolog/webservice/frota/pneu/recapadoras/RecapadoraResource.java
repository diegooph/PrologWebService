package br.com.zalf.prolog.webservice.frota.pneu.recapadoras;

import br.com.zalf.prolog.webservice.commons.network.AbstractResponse;
import br.com.zalf.prolog.webservice.commons.network.Response;
import br.com.zalf.prolog.webservice.commons.network.metadata.Platform;
import br.com.zalf.prolog.webservice.commons.network.metadata.Required;
import br.com.zalf.prolog.webservice.commons.network.metadata.UsedBy;
import br.com.zalf.prolog.webservice.interceptors.auth.Secured;
import br.com.zalf.prolog.webservice.permissao.pilares.Pilares;

import javax.ws.rs.*;
import java.util.List;

/**
 * Created on 13/04/18.
 *
 * @author Diogenes Vanzela (https://github.com/diogenesvanzella)
 */
@Path("/recapadoras")
public class RecapadoraResource {

    private final RecapadoraService service = new RecapadoraService();

    @POST
    @Secured(permissions = {Pilares.Frota.Recapadora.CADASTRO})
    @UsedBy(platforms = {Platform.ANDROID, Platform.WEBSITE})
    public AbstractResponse insert(@HeaderParam("Authorization") @Required String token,
                                   @Required Recapadora recapadora) throws Exception {
        return service.insertRecapadora(token, recapadora);
    }

    @PUT
    @Secured(permissions = {Pilares.Frota.Recapadora.EDICAO})
    @UsedBy(platforms = {Platform.ANDROID, Platform.WEBSITE})
    @Path("/{codEmpresa}")
    public Response atualizaRecapadora(@PathParam("codEmpresa") @Required Long codEmpresa,
                                       @Required Recapadora recapadora) throws Exception {
        return service.atualizaRecapadoras(codEmpresa, recapadora);
    }

    @GET
    @Secured(permissions = {
            Pilares.Frota.Recapadora.VISUALIZACAO,
            Pilares.Frota.Recapadora.CADASTRO,
            Pilares.Frota.Recapadora.EDICAO,
            Pilares.Frota.Pneu.Movimentacao.MOVIMENTAR_VEICULO_ESTOQUE,
            Pilares.Frota.Pneu.Movimentacao.MOVIMENTAR_ANALISE,
            Pilares.Frota.Pneu.Movimentacao.MOVIMENTAR_DESCARTE})
    @UsedBy(platforms = {Platform.ANDROID, Platform.WEBSITE})
    @Path("/{codEmpresa}")
    public List<Recapadora> getRecapadoras(@PathParam("codEmpresa") @Required Long codEmpresa,
                                           @QueryParam("ativas") @Required Boolean ativas) throws Exception {
        return service.getRecapadoras(codEmpresa, ativas);
    }

    @GET
    @Secured(permissions = {
            Pilares.Frota.Recapadora.VISUALIZACAO,
            Pilares.Frota.Recapadora.EDICAO,
            Pilares.Frota.Pneu.Movimentacao.MOVIMENTAR_VEICULO_ESTOQUE,
            Pilares.Frota.Pneu.Movimentacao.MOVIMENTAR_ANALISE,
            Pilares.Frota.Pneu.Movimentacao.MOVIMENTAR_DESCARTE})
    @UsedBy(platforms = {Platform.WEBSITE})
    @Path("/{codEmpresa}/{codRecapadora}")
    public Recapadora getRecapadora(@PathParam("codEmpresa") Long codEmpresa,
                                    @PathParam("codRecapadora") Long codRecapadora) throws Exception {
        return service.getRecapadora(codEmpresa, codRecapadora);
    }

    @PUT
    @UsedBy(platforms = Platform.WEBSITE)
    @Secured(permissions = {Pilares.Frota.Recapadora.EDICAO})
    @Path("/{codEmpresa}/status")
    public Response alterarStatusRecapadoras(@HeaderParam("Authorization") @Required String token,
                                             @PathParam("codEmpresa") @Required Long codEmpresa,
                                             @Required List<Recapadora> recapadoras) throws Exception {
        return service.alterarStatusRecapadoras(token, codEmpresa, recapadoras);
    }
}
