package br.com.zalf.prolog.webservice.frota.pneu.pneu.banda;

import br.com.zalf.prolog.webservice.commons.network.AbstractResponse;
import br.com.zalf.prolog.webservice.commons.network.ResponseWithCod;
import br.com.zalf.prolog.webservice.commons.util.Required;
import br.com.zalf.prolog.webservice.errorhandling.exception.ProLogException;
import br.com.zalf.prolog.webservice.frota.pneu.pneu.banda.model.*;
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

    @GET
    @Secured(permissions = {
            Pilares.Frota.Pneu.Movimentacao.MOVIMENTAR_VEICULO_ESTOQUE,
            Pilares.Frota.Pneu.Movimentacao.MOVIMENTAR_ANALISE,
            Pilares.Frota.Pneu.Movimentacao.MOVIMENTAR_DESCARTE,
            Pilares.Frota.Pneu.CADASTRAR,
            Pilares.Frota.Pneu.ALTERAR,
            Pilares.Frota.Pneu.VISUALIZAR})
    @Path("/bandas/marcas/{codEmpresa}")
    public List<PneuMarcaBanda> listagemMarcasBandas(@PathParam("codEmpresa") Long codEmpresa) {
        return service.listagemMarcasBandas(codEmpresa);
    }

    @GET
    @Secured(permissions = {
            Pilares.Frota.Pneu.Movimentacao.MOVIMENTAR_VEICULO_ESTOQUE,
            Pilares.Frota.Pneu.Movimentacao.MOVIMENTAR_ANALISE,
            Pilares.Frota.Pneu.Movimentacao.MOVIMENTAR_DESCARTE,
            Pilares.Frota.Pneu.CADASTRAR,
            Pilares.Frota.Pneu.ALTERAR,
            Pilares.Frota.Pneu.VISUALIZAR})
    @Path("/bandas/marca/{codMarca}")
    public PneuMarcaBanda getMarcaBanda(@PathParam("codMarca") Long codMarca) {
        return service.getMarcaBanda(codMarca);
    }

    @GET
    @Secured(permissions = {
            Pilares.Frota.Pneu.Movimentacao.MOVIMENTAR_VEICULO_ESTOQUE,
            Pilares.Frota.Pneu.Movimentacao.MOVIMENTAR_ANALISE,
            Pilares.Frota.Pneu.Movimentacao.MOVIMENTAR_DESCARTE,
            Pilares.Frota.Pneu.CADASTRAR,
            Pilares.Frota.Pneu.ALTERAR,
            Pilares.Frota.Pneu.VISUALIZAR})
    @Path("/bandas/marcas-modelos/{codEmpresa}")
    public List<PneuMarcaModelosBanda> listagemMarcasModelosBandas(@PathParam("codEmpresa") Long codEmpresa) {
        return service.listagemMarcasModelosBandas(codEmpresa);
    }

    @GET
    @Secured(permissions = {
            Pilares.Frota.Pneu.Movimentacao.MOVIMENTAR_VEICULO_ESTOQUE,
            Pilares.Frota.Pneu.Movimentacao.MOVIMENTAR_ANALISE,
            Pilares.Frota.Pneu.Movimentacao.MOVIMENTAR_DESCARTE,
            Pilares.Frota.Pneu.CADASTRAR,
            Pilares.Frota.Pneu.ALTERAR,
            Pilares.Frota.Pneu.VISUALIZAR})
    @Path("/bandas/marca-modelo/{codModelo}")
    public PneuMarcaModeloBanda getMarcaModeloBanda(@PathParam("codModelo") Long codModelo) {
        return service.getMarcaModeloBanda(codModelo);
    }

    @POST
    @Secured(permissions = {
            Pilares.Frota.Pneu.Movimentacao.MOVIMENTAR_VEICULO_ESTOQUE,
            Pilares.Frota.Pneu.Movimentacao.MOVIMENTAR_ANALISE,
            Pilares.Frota.Pneu.Movimentacao.MOVIMENTAR_DESCARTE,
            Pilares.Frota.Pneu.CADASTRAR,
            Pilares.Frota.Pneu.ALTERAR})
    @Path("/bandas/marcas/{codEmpresa}")
    public AbstractResponse insertMarcaBanda(PneuMarcaModelosBanda marca, @PathParam("codEmpresa") Long codEmpresa) {
        return service.insertMarcaBanda(marca, codEmpresa);
    }

    @PUT
    @Secured(permissions = {
            Pilares.Frota.Pneu.CADASTRAR,
            Pilares.Frota.Pneu.ALTERAR,
            Pilares.Frota.Pneu.Movimentacao.MOVIMENTAR_ANALISE})
    @Path("bandas/marca")
    public ResponseWithCod updateMarcaBanda(PneuMarcaBanda marcaBanda) {
        return service.updateMarcaBanda(marcaBanda);
    }

    @POST
    @Secured(permissions = {
            Pilares.Frota.Pneu.Movimentacao.MOVIMENTAR_VEICULO_ESTOQUE,
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

    /**
     * @deprecated Use {@link #listagemMarcasModelosBandas(Long)} instead.
     */
    @GET
    @Secured(permissions = {
            Pilares.Frota.Pneu.Movimentacao.MOVIMENTAR_VEICULO_ESTOQUE,
            Pilares.Frota.Pneu.Movimentacao.MOVIMENTAR_ANALISE,
            Pilares.Frota.Pneu.Movimentacao.MOVIMENTAR_DESCARTE,
            Pilares.Frota.Pneu.CADASTRAR,
            Pilares.Frota.Pneu.ALTERAR,
            Pilares.Frota.Pneu.VISUALIZAR})
    @Path("/bandas/{codEmpresa}")
    @Deprecated
    public List<PneuMarcaModelosBanda> DEPRECATED_GET_MARCA_MODELO_BANDA(@PathParam("codEmpresa") Long codEmpresa) {
        return service.listagemMarcasModelosBandas(codEmpresa);
    }
}