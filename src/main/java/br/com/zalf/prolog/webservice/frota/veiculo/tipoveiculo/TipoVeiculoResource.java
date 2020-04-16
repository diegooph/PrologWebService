package br.com.zalf.prolog.webservice.frota.veiculo.tipoveiculo;

import br.com.zalf.prolog.webservice.commons.network.Response;
import br.com.zalf.prolog.webservice.commons.network.ResponseWithCod;
import br.com.zalf.prolog.webservice.commons.util.Platform;
import br.com.zalf.prolog.webservice.commons.util.Required;
import br.com.zalf.prolog.webservice.commons.util.UsedBy;
import br.com.zalf.prolog.webservice.errorhandling.exception.ProLogException;
import br.com.zalf.prolog.webservice.frota.veiculo.model.TipoVeiculo;
import br.com.zalf.prolog.webservice.interceptors.auth.Secured;
import br.com.zalf.prolog.webservice.permissao.pilares.Pilares;
import org.jetbrains.annotations.NotNull;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.util.List;

/**
 * Created on 22/02/19.
 *
 * @author Diogenes Vanzela (https://github.com/diogenesvanzella)
 */
@Path("veiculos")
@Produces(MediaType.APPLICATION_JSON + ";charset=utf-8")
@Consumes(MediaType.APPLICATION_JSON + ";charset=utf-8")
public final class TipoVeiculoResource {
    @NotNull
    private final TipoVeiculoService service = new TipoVeiculoService();

    @POST
    @Secured(permissions = {Pilares.Frota.Veiculo.CADASTRAR, Pilares.Frota.Veiculo.ALTERAR})
    @Path("/tipos-veiculos")
    public ResponseWithCod insertTipoVeiculoPorEmpresa(
            @HeaderParam("Authorization") @Required final String userToken,
            @Required final TipoVeiculo tipoVeiculo) throws ProLogException {
        return service.insertTipoVeiculoPorEmpresa(userToken, tipoVeiculo);
    }

    @PUT
    @Secured(permissions = {Pilares.Frota.Veiculo.CADASTRAR, Pilares.Frota.Veiculo.ALTERAR})
    @Path("/tipos-veiculos")
    public Response updateTipoVeiculo(@HeaderParam("Authorization") @Required final String userToken,
                                      @Required final TipoVeiculo tipoVeiculo) throws ProLogException {
        return service.updateTipoVeiculo(userToken, tipoVeiculo);
    }

    @GET
    @Secured(permissions = {Pilares.Frota.Veiculo.VISUALIZAR,
            Pilares.Frota.Veiculo.ALTERAR,
            Pilares.Frota.Veiculo.CADASTRAR,
            Pilares.Frota.Checklist.VISUALIZAR_TODOS,
            Pilares.Frota.Checklist.REALIZAR,
            Pilares.Frota.OrdemServico.Pneu.VISUALIZAR,
            Pilares.Frota.OrdemServico.Checklist.VISUALIZAR,
            Pilares.Frota.OrdemServico.Checklist.RESOLVER_ITEM,
            Pilares.Frota.Afericao.REALIZAR_AFERICAO_PLACA,
            Pilares.Frota.Afericao.VISUALIZAR_TODAS_AFERICOES,
            Pilares.Frota.Pneu.Movimentacao.MOVIMENTAR_VEICULO_ESTOQUE,
            Pilares.Frota.Pneu.Movimentacao.MOVIMENTAR_ANALISE,
            Pilares.Frota.Pneu.Movimentacao.MOVIMENTAR_DESCARTE})
    @Path("/tipos-veiculos")
    @UsedBy(platforms = {Platform.WEBSITE, Platform.ANDROID})
    public List<TipoVeiculo> getTiposVeiculosByEmpresa(
            @HeaderParam("Authorization") @Required final String userToken,
            @QueryParam("codEmpresa") @Required final Long codEmpresa) throws ProLogException {
        return service.getTiposVeiculosByEmpresa(userToken, codEmpresa);
    }

    @GET
    @Secured(permissions = {Pilares.Frota.Veiculo.VISUALIZAR,
            Pilares.Frota.Veiculo.CADASTRAR,
            Pilares.Frota.Veiculo.ALTERAR})
    @Path("/tipos-veiculos/{codTipoVeiculo}")
    public TipoVeiculo getTipoVeiculo(
            @PathParam("codTipoVeiculo") @Required final Long codTipoVeiculo) throws ProLogException {
        return service.getTipoVeiculo(codTipoVeiculo);
    }

    @DELETE
    @Secured(permissions = {Pilares.Frota.Veiculo.CADASTRAR, Pilares.Frota.Veiculo.ALTERAR})
    @Path("/tipos-veiculos")
    public Response deleteTipoVeiculoByEmpresa(
            @QueryParam("codEmpresa") @NotNull final Long codEmpresa,
            @QueryParam("codTipoVeiculo") @NotNull final Long codTipoVeiculo) throws ProLogException {
        return service.deleteTipoVeiculoByEmpresa(codEmpresa, codTipoVeiculo);
    }

    /**
     * @deprecated at 2019-01-10.
     * Método depreciado pois não será mais utilizado o código da unidade.
     * Em seu lugar será utilizado o código da empresa.
     * Utilize {@link #getTiposVeiculosByEmpresa(String, Long)}.
     */
    @Deprecated
    @GET
    @Secured(permissions = {Pilares.Frota.Veiculo.VISUALIZAR,
            Pilares.Frota.Veiculo.ALTERAR,
            Pilares.Frota.Veiculo.CADASTRAR,
            Pilares.Frota.Checklist.VISUALIZAR_TODOS,
            Pilares.Frota.Checklist.REALIZAR,
            Pilares.Frota.OrdemServico.Pneu.VISUALIZAR,
            Pilares.Frota.OrdemServico.Checklist.VISUALIZAR,
            Pilares.Frota.OrdemServico.Checklist.RESOLVER_ITEM,
            Pilares.Frota.Afericao.REALIZAR_AFERICAO_PLACA,
            Pilares.Frota.Afericao.VISUALIZAR_TODAS_AFERICOES,
            Pilares.Frota.Pneu.Movimentacao.MOVIMENTAR_VEICULO_ESTOQUE,
            Pilares.Frota.Pneu.Movimentacao.MOVIMENTAR_ANALISE,
            Pilares.Frota.Pneu.Movimentacao.MOVIMENTAR_DESCARTE})
    @Path("/{codUnidade}/tipo")
    @UsedBy(platforms = {Platform.WEBSITE, Platform.ANDROID})
    public List<TipoVeiculo> getTiposVeiculosByUnidade(@HeaderParam("Authorization") @Required final String userToken,
                                                       @PathParam("codUnidade") @Required final Long codUnidade) {
        return service.getTiposVeiculosByUnidade(userToken, codUnidade);
    }
}
