package br.com.zalf.prolog.webservice.frota.pneu.banda;

import br.com.zalf.prolog.webservice.commons.network.AbstractResponse;
import br.com.zalf.prolog.webservice.commons.network.ResponseWithCod;
import br.com.zalf.prolog.webservice.commons.util.Required;
import br.com.zalf.prolog.webservice.errorhandling.exception.ProLogException;
import br.com.zalf.prolog.webservice.frota.pneu.banda._model.*;
import br.com.zalf.prolog.webservice.interceptors.auth.Secured;
import br.com.zalf.prolog.webservice.permissao.pilares.Pilares;
import org.jetbrains.annotations.NotNull;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.util.List;

/**
 * Created on 18/09/19.
 *
 * @author Thais Francisco (https://github.com/thaisksf)
 */
@Path("pneus")
@Produces(MediaType.APPLICATION_JSON + ";charset=utf-8")
@Consumes(MediaType.APPLICATION_JSON + ";charset=utf-8")
public final class PneuModeloBandaResource {
    @NotNull
    private final PneuModeloBandaService service = new PneuModeloBandaService();

    @POST
    @Secured(permissions = {
            Pilares.Frota.Pneu.Movimentacao.MOVIMENTAR_ANALISE,
            Pilares.Frota.Pneu.Movimentacao.MOVIMENTAR_DESCARTE,
            Pilares.Frota.Pneu.CADASTRAR,
            Pilares.Frota.Pneu.ALTERAR})
    @Path("/bandas/marcas/{codEmpresa}")
    public AbstractResponse insertMarcaBanda(PneuMarcaBandaInsercao marcaBanda) {
        return service.insertMarcaBanda(marcaBanda);
    }

    @PUT
    @Secured(permissions = {
            Pilares.Frota.Pneu.CADASTRAR,
            Pilares.Frota.Pneu.ALTERAR,
            Pilares.Frota.Pneu.Movimentacao.MOVIMENTAR_ANALISE})
    @Path("bandas/marca")
    public ResponseWithCod updateMarcaBanda(PneuMarcaBandaEdicao marcaBanda) {
        return service.updateMarcaBanda(marcaBanda);
    }

    @GET
    @Secured(permissions = {
            Pilares.Frota.Pneu.Movimentacao.MOVIMENTAR_ANALISE,
            Pilares.Frota.Pneu.Movimentacao.MOVIMENTAR_DESCARTE,
            Pilares.Frota.Pneu.CADASTRAR,
            Pilares.Frota.Pneu.ALTERAR,
            Pilares.Frota.Pneu.VISUALIZAR})
    @Path("/bandas/marcas/{codEmpresa}")
    public List<PneuMarcaBandaListagemVisualizacao> getListagemMarcasBandas(@PathParam("codEmpresa") Long codEmpresa) {
        return service.getListagemMarcasBandas(codEmpresa);
    }

    @GET
    @Secured(permissions = {
            Pilares.Frota.Pneu.Movimentacao.MOVIMENTAR_ANALISE,
            Pilares.Frota.Pneu.Movimentacao.MOVIMENTAR_DESCARTE,
            Pilares.Frota.Pneu.CADASTRAR,
            Pilares.Frota.Pneu.ALTERAR,
            Pilares.Frota.Pneu.VISUALIZAR})
    @Path("/bandas/marca/{codMarca}")
    public PneuMarcaBandaListagemVisualizacao getMarcaBanda(@PathParam("codMarca") Long codMarca) {
        return service.getMarcaBanda(codMarca);
    }

    @POST
    @Secured(permissions = {
            Pilares.Frota.Pneu.Movimentacao.MOVIMENTAR_ANALISE,
            Pilares.Frota.Pneu.Movimentacao.MOVIMENTAR_DESCARTE,
            Pilares.Frota.Pneu.CADASTRAR,
            Pilares.Frota.Pneu.ALTERAR})
    @Path("/bandas/modelos")
    public AbstractResponse insertModeloBanda(@Required final PneuModeloBandaInsercao pneuModeloBandaInsercao)
            throws ProLogException {
        return service.insertModeloBanda(pneuModeloBandaInsercao);
    }

    @PUT
    @Secured(permissions = {
            Pilares.Frota.Pneu.CADASTRAR,
            Pilares.Frota.Pneu.ALTERAR,
            Pilares.Frota.Pneu.Movimentacao.MOVIMENTAR_ANALISE})
    @Path("/banda-update")
    public ResponseWithCod updateModeloBanda(
            @HeaderParam("Authorization") @Required final String userToken,
            @Required final PneuModeloBandaEdicao pneuModeloBandaEdicao) throws ProLogException {
        return service.updateModeloBanda(pneuModeloBandaEdicao);
    }

    @GET
    @Secured(permissions = {
            Pilares.Frota.Pneu.Movimentacao.MOVIMENTAR_ANALISE,
            Pilares.Frota.Pneu.Movimentacao.MOVIMENTAR_DESCARTE,
            Pilares.Frota.Pneu.CADASTRAR,
            Pilares.Frota.Pneu.ALTERAR,
            Pilares.Frota.Pneu.VISUALIZAR})
    @Path("/bandas/marcas-modelos/{codEmpresa}")
    public List<PneuModeloBandaListagem> getListagemMarcasModelosBandas(@PathParam("codEmpresa") Long codEmpresa) {
        return service.getListagemMarcasModelosBandas(codEmpresa);
    }

    @GET
    @Secured(permissions = {
            Pilares.Frota.Pneu.Movimentacao.MOVIMENTAR_ANALISE,
            Pilares.Frota.Pneu.Movimentacao.MOVIMENTAR_DESCARTE,
            Pilares.Frota.Pneu.CADASTRAR,
            Pilares.Frota.Pneu.ALTERAR,
            Pilares.Frota.Pneu.VISUALIZAR})
    @Path("/bandas/marca-modelo/{codModelo}")
    public PneuModeloBandaVisualizacao getMarcaModeloBanda(@PathParam("codModelo") Long codModelo) {
        return service.getMarcaModeloBanda(codModelo);
    }
}