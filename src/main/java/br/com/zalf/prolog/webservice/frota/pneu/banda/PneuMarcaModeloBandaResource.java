package br.com.zalf.prolog.webservice.frota.pneu.banda;

import br.com.zalf.prolog.webservice.commons.network.AbstractResponse;
import br.com.zalf.prolog.webservice.commons.network.ResponseWithCod;
import br.com.zalf.prolog.webservice.commons.util.Optional;
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
public final class PneuMarcaModeloBandaResource {
    @NotNull
    private final PneuMarcaModeloBandaService service = new PneuMarcaModeloBandaService();

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
    public List<PneuMarcaBandaListagem> getListagemMarcasBanda(
            @QueryParam("codEmpresa") @Required Long codEmpresa,
            @QueryParam("comModelos") @Optional boolean comModelos) {
        return service.getListagemMarcasBanda(codEmpresa, comModelos);
    }

    @GET
    @Secured(permissions = {
            Pilares.Frota.Pneu.Movimentacao.MOVIMENTAR_ANALISE,
            Pilares.Frota.Pneu.Movimentacao.MOVIMENTAR_DESCARTE,
            Pilares.Frota.Pneu.CADASTRAR,
            Pilares.Frota.Pneu.ALTERAR,
            Pilares.Frota.Pneu.VISUALIZAR})
    @Path("/marcas/{codMarca}")
    public PneuMarcaBandaVisualizacao getMarcaBanda(@PathParam("codMarca") Long codMarca) {
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
    public List<PneuModeloBandaListagem> getListagemModelosBandas(@QueryParam("codEmpresa") Long codEmpresa,
                                                                  @QueryParam("codMarca") Long codMarca) {
        return service.getListagemModelosBandas(codEmpresa, codMarca);
    }

    @GET
    @Secured(permissions = {
            Pilares.Frota.Pneu.Movimentacao.MOVIMENTAR_ANALISE,
            Pilares.Frota.Pneu.Movimentacao.MOVIMENTAR_DESCARTE,
            Pilares.Frota.Pneu.CADASTRAR,
            Pilares.Frota.Pneu.ALTERAR,
            Pilares.Frota.Pneu.VISUALIZAR})
    @Path("/modelos/{codModelo}")
    public PneuModeloBandaVisualizacao getModeloBanda(@PathParam("codModelo") Long codModelo) {
        return service.getModeloBanda(codModelo);
    }
    //
    //
    //
}