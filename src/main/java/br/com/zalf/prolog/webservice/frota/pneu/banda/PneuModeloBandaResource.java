package br.com.zalf.prolog.webservice.frota.pneu.banda;

import br.com.zalf.prolog.webservice.commons.network.AbstractResponse;
import br.com.zalf.prolog.webservice.commons.network.ResponseWithCod;
import br.com.zalf.prolog.webservice.commons.util.Required;
import br.com.zalf.prolog.webservice.frota.pneu.banda._model.*;
import br.com.zalf.prolog.webservice.interceptors.auth.Secured;
import br.com.zalf.prolog.webservice.permissao.pilares.Pilares;
import org.jetbrains.annotations.NotNull;

import javax.validation.Valid;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.util.List;

/**
 * Created on 18/09/19.
 *
 * @author Thais Francisco (https://github.com/thaisksf)
 */
@Path("pneus/bandas")
@Produces(MediaType.APPLICATION_JSON + ";charset=utf-8")
@Consumes(MediaType.APPLICATION_JSON + ";charset=utf-8")
public final class PneuModeloBandaResource {
    @NotNull
    private final PneuModeloBandaService service = new PneuModeloBandaService();

    //
    //
    // Métodos de marcas de banda.
    //
    //
    @POST
    @Secured(permissions = {
            Pilares.Frota.Pneu.Movimentacao.MOVIMENTAR_ANALISE,
            Pilares.Frota.Pneu.Movimentacao.MOVIMENTAR_DESCARTE,
            Pilares.Frota.Pneu.CADASTRAR,
            Pilares.Frota.Pneu.ALTERAR})
    @Path("/marcas")
    public AbstractResponse insertMarcaBanda(@Valid PneuMarcaBandaInsercao marcaBanda) {
        return service.insertMarcaBanda(marcaBanda);
    }

    @PUT
    @Secured(permissions = {
            Pilares.Frota.Pneu.CADASTRAR,
            Pilares.Frota.Pneu.ALTERAR,
            Pilares.Frota.Pneu.Movimentacao.MOVIMENTAR_ANALISE})
    @Path("/marcas")
    public ResponseWithCod updateMarcaBanda(@Valid PneuMarcaBandaEdicao marcaBanda) {
        return service.updateMarcaBanda(marcaBanda);
    }

    @GET
    @Secured(permissions = {
            Pilares.Frota.Pneu.Movimentacao.MOVIMENTAR_ANALISE,
            Pilares.Frota.Pneu.Movimentacao.MOVIMENTAR_DESCARTE,
            Pilares.Frota.Pneu.CADASTRAR,
            Pilares.Frota.Pneu.ALTERAR,
            Pilares.Frota.Pneu.VISUALIZAR})
    @Path("/marcas")
    public List<PneuMarcaBandaListagemVisualizacao> getListagemMarcasBandas(@QueryParam("codEmpresa") Long codEmpresa) {
        return service.getListagemMarcasBandas(codEmpresa);
    }

    @GET
    @Secured(permissions = {
            Pilares.Frota.Pneu.Movimentacao.MOVIMENTAR_ANALISE,
            Pilares.Frota.Pneu.Movimentacao.MOVIMENTAR_DESCARTE,
            Pilares.Frota.Pneu.CADASTRAR,
            Pilares.Frota.Pneu.ALTERAR,
            Pilares.Frota.Pneu.VISUALIZAR})
    @Path("/marcas/{codMarca}")
    public PneuMarcaBandaListagemVisualizacao getMarcaBanda(@PathParam("codMarca") Long codMarca) {
        return service.getMarcaBanda(codMarca);
    }
    //
    //
    //

    //
    //
    // Métodos de modelos de banda.
    //
    //
    @POST
    @Secured(permissions = {
            Pilares.Frota.Pneu.Movimentacao.MOVIMENTAR_ANALISE,
            Pilares.Frota.Pneu.Movimentacao.MOVIMENTAR_DESCARTE,
            Pilares.Frota.Pneu.CADASTRAR,
            Pilares.Frota.Pneu.ALTERAR})
    @Path("/modelos")
    public AbstractResponse insertModeloBanda(@Valid final PneuModeloBandaInsercao pneuModeloBandaInsercao) {
        return service.insertModeloBanda(pneuModeloBandaInsercao);
    }

    @PUT
    @Secured(permissions = {
            Pilares.Frota.Pneu.CADASTRAR,
            Pilares.Frota.Pneu.ALTERAR,
            Pilares.Frota.Pneu.Movimentacao.MOVIMENTAR_ANALISE})
    @Path("/modelos")
    public ResponseWithCod updateModeloBanda(
            @HeaderParam("Authorization") final String userToken,
            @Required final PneuModeloBandaEdicao pneuModeloBandaEdicao)  {
        return service.updateModeloBanda(pneuModeloBandaEdicao);
    }

    @GET
    @Secured(permissions = {
            Pilares.Frota.Pneu.Movimentacao.MOVIMENTAR_ANALISE,
            Pilares.Frota.Pneu.Movimentacao.MOVIMENTAR_DESCARTE,
            Pilares.Frota.Pneu.CADASTRAR,
            Pilares.Frota.Pneu.ALTERAR,
            Pilares.Frota.Pneu.VISUALIZAR})
    @Path("/modelos")
    public List<PneuModeloBandaListagem> getListagemMarcasModelosBandas(@QueryParam("codEmpresa") Long codEmpresa) {
        return service.getListagemMarcasModelosBandas(codEmpresa);
    }

    @GET
    @Secured(permissions = {
            Pilares.Frota.Pneu.Movimentacao.MOVIMENTAR_ANALISE,
            Pilares.Frota.Pneu.Movimentacao.MOVIMENTAR_DESCARTE,
            Pilares.Frota.Pneu.CADASTRAR,
            Pilares.Frota.Pneu.ALTERAR,
            Pilares.Frota.Pneu.VISUALIZAR})
    @Path("/modelos/{codModelo}")
    public PneuModeloBandaVisualizacao getMarcaModeloBanda(@PathParam("codModelo") Long codModelo) {
        return service.getMarcaModeloBanda(codModelo);
    }
    //
    //
    //
}