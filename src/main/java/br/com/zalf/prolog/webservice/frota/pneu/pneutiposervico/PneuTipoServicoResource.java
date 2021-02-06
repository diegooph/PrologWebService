package br.com.zalf.prolog.webservice.frota.pneu.pneutiposervico;

import br.com.zalf.prolog.webservice.commons.network.AbstractResponse;
import br.com.zalf.prolog.webservice.commons.network.Response;
import br.com.zalf.prolog.webservice.commons.network.metadata.Optional;
import br.com.zalf.prolog.webservice.commons.network.metadata.Platform;
import br.com.zalf.prolog.webservice.commons.network.metadata.Required;
import br.com.zalf.prolog.webservice.commons.network.metadata.UsedBy;
import br.com.zalf.prolog.webservice.errorhandling.exception.ProLogException;
import br.com.zalf.prolog.webservice.frota.pneu.pneutiposervico._model.PneuTipoServico;
import br.com.zalf.prolog.webservice.interceptors.auth.Secured;
import br.com.zalf.prolog.webservice.permissao.pilares.Pilares;

import javax.ws.rs.*;
import java.util.List;

/**
 * Created on 24/05/18.
 *
 * @author Diogenes Vanzela (https://github.com/diogenesvanzella)
 */
@Path("/pneus/tipos-servicos")
public class PneuTipoServicoResource {

    private final PneuTipoServicoService service = new PneuTipoServicoService();

    @POST
    @Secured(permissions = {Pilares.Frota.Recapadora.TipoServico.CADASTRO})
    @UsedBy(platforms = {Platform.ANDROID, Platform.WEBSITE})
    public AbstractResponse insert(@HeaderParam("Authorization") @Required String token,
                                   @Required PneuTipoServico tipoServico) throws ProLogException {
        return service.insertPneuTipoServico(token, tipoServico);
    }

    @PUT
    @Secured(permissions = {Pilares.Frota.Recapadora.TipoServico.EDICAO})
    @UsedBy(platforms = {Platform.WEBSITE})
    @Path("/{codEmpresa}")
    public Response atualizaPneuTipoServico(@HeaderParam("Authorization") @Required String token,
                                            @PathParam("codEmpresa") @Required Long codEmpresa,
                                            @Required PneuTipoServico tipoServico) throws ProLogException {
        return service.atualizaPneuTipoServico(token, codEmpresa, tipoServico);
    }

    @GET
    @Secured(permissions = {
            Pilares.Frota.Recapadora.TipoServico.CADASTRO,
            Pilares.Frota.Recapadora.TipoServico.EDICAO,
            Pilares.Frota.Recapadora.TipoServico.VISUALIZACAO,
            Pilares.Frota.Pneu.Movimentacao.MOVIMENTAR_ANALISE})
    @UsedBy(platforms = {Platform.ANDROID, Platform.WEBSITE})
    @Path("/{codEmpresa}")
    public List<PneuTipoServico> getPneuTiposServicos(
            @PathParam("codEmpresa") @Required Long codEmpresa,
            @QueryParam("ativos") @Optional Boolean ativos,
            @QueryParam("orderBy") @DefaultValue("nome") @Required List<String> orderBy) throws ProLogException {
        return service.getPneuTiposServicos(codEmpresa, orderBy, ativos);
    }

    @GET
    @Secured(permissions = {
            Pilares.Frota.Recapadora.TipoServico.CADASTRO,
            Pilares.Frota.Recapadora.TipoServico.EDICAO,
            Pilares.Frota.Recapadora.TipoServico.VISUALIZACAO,
            Pilares.Frota.Pneu.Movimentacao.MOVIMENTAR_ANALISE})
    @UsedBy(platforms = {Platform.WEBSITE})
    @Path("/{codEmpresa}/{codTipoServico}")
    public PneuTipoServico getPneuTipoServico(
            @PathParam("codEmpresa") @Required Long codEmpresa,
            @PathParam("codTipoServico") @Required Long codTipoServico) throws ProLogException {
        return service.getPneuTipoServico(codEmpresa, codTipoServico);
    }

    @PUT
    @Secured(permissions = {Pilares.Frota.Recapadora.TipoServico.EDICAO})
    @UsedBy(platforms = Platform.WEBSITE)
    @Path("/{codEmpresa}/status")
    public Response alterarStatusPneuTipoServico(@HeaderParam("Authorization") @Required String token,
                                                 @PathParam("codEmpresa") @Required Long codEmpresa,
                                                 @Required PneuTipoServico tipoServico) throws ProLogException {
        return service.alterarStatusPneuTipoServico(token, codEmpresa, tipoServico);
    }
}
