package br.com.zalf.prolog.webservice.frota.pneu.pneu;

import br.com.zalf.prolog.webservice.commons.network.AbstractResponse;
import br.com.zalf.prolog.webservice.commons.network.Response;
import br.com.zalf.prolog.webservice.commons.util.Required;
import br.com.zalf.prolog.webservice.errorhandling.exception.ProLogException;
import br.com.zalf.prolog.webservice.frota.pneu.pneu.model.ModeloBanda;
import br.com.zalf.prolog.webservice.frota.pneu.pneu.model.ModeloPneu;
import br.com.zalf.prolog.webservice.frota.pneu.pneu.model.Pneu;
import br.com.zalf.prolog.webservice.frota.pneu.pneu.model.PneuComum;
import br.com.zalf.prolog.webservice.frota.veiculo.model.Marca;
import br.com.zalf.prolog.webservice.frota.veiculo.model.Modelo;
import br.com.zalf.prolog.webservice.interceptors.auth.Secured;
import br.com.zalf.prolog.webservice.interceptors.versioncodebarrier.AppVersionCodeHandler;
import br.com.zalf.prolog.webservice.interceptors.versioncodebarrier.DefaultAppVersionCodeHandler;
import br.com.zalf.prolog.webservice.interceptors.versioncodebarrier.VersionCodeHandlerMode;
import br.com.zalf.prolog.webservice.interceptors.versioncodebarrier.VersionNotPresentAction;
import br.com.zalf.prolog.webservice.permissao.pilares.Pilares;
import org.glassfish.jersey.media.multipart.FormDataParam;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.io.InputStream;
import java.util.List;

@Path("/pneus")
@Consumes(MediaType.APPLICATION_JSON + ";charset=utf-8")
@Produces(MediaType.APPLICATION_JSON + ";charset=utf-8")
@AppVersionCodeHandler(
        implementation = DefaultAppVersionCodeHandler.class,
        targetVersionCode = 64,
        versionCodeHandlerMode = VersionCodeHandlerMode.BLOCK_THIS_VERSION_AND_BELOW,
        actionIfVersionNotPresent = VersionNotPresentAction.BLOCK_ANYWAY)
public class PneuResource {

    private PneuService service = new PneuService();

    @POST
    @Secured
    @Consumes({MediaType.MULTIPART_FORM_DATA})
    public List<Long> insert(
            @HeaderParam("Authorization") @Required final String userToken,
            @FormDataParam("file") @Required final InputStream fileInputStream) throws ProLogException {
        return service.insert(userToken, fileInputStream);
    }

    @POST
    @Secured(permissions = Pilares.Frota.Pneu.CADASTRAR)
    @Path("/{codUnidade}")
    public AbstractResponse insert(@HeaderParam("Authorization") @Required final String userToken,
                                   @PathParam("codUnidade") @Required final Long codUnidade,
                                   @QueryParam("ignoreDotValidation") final boolean ignoreDotValidation,
                                   @Required final Pneu pneu) throws ProLogException {
        return service.insert(userToken, codUnidade, pneu, ignoreDotValidation);
    }

    @PUT
    @Secured(permissions = {Pilares.Frota.Pneu.CADASTRAR, Pilares.Frota.Pneu.ALTERAR})
    @Path("/{codUnidade}/{codPneuOriginal}")
    public Response update(
            @HeaderParam("Authorization") @Required final String userToken,
            @PathParam("codUnidade") @Required final Long codUnidade,
            @PathParam("codPneuOriginal") @Required final Long codOriginalPneu,
            @Required final Pneu pneu) throws ProLogException {
        return service.update(userToken, codUnidade, codOriginalPneu, pneu);
    }

    @POST
    @Secured(permissions = {Pilares.Frota.Pneu.CADASTRAR, Pilares.Frota.Pneu.ALTERAR})
    @Path("/modelo/{codEmpresa}/{codMarca}")
    public AbstractResponse insertModeloPneu(ModeloPneu modelo, @PathParam("codEmpresa") Long codEmpresa, @PathParam
            ("codMarca") Long codMarca) {
        return service.insertModeloPneu(modelo, codEmpresa, codMarca);
    }

    @GET
    @Secured(permissions = {
            Pilares.Frota.Pneu.VISUALIZAR,
            Pilares.Frota.Pneu.CADASTRAR,
            Pilares.Frota.Pneu.ALTERAR,
            Pilares.Frota.OrdemServico.Pneu.CONSERTAR_ITEM,
            Pilares.Frota.Pneu.Movimentacao.MOVIMENTAR_VEICULO_ESTOQUE,
            Pilares.Frota.Pneu.Movimentacao.MOVIMENTAR_ANALISE,
            Pilares.Frota.Pneu.Movimentacao.MOVIMENTAR_DESCARTE})
    @Path("/{codUnidade}/{status}")
    @AppVersionCodeHandler(
            targetVersionCode = 68,
            versionCodeHandlerMode = VersionCodeHandlerMode.BLOCK_THIS_VERSION_AND_BELOW,
            actionIfVersionNotPresent = VersionNotPresentAction.BLOCK_ANYWAY)
    public List<Pneu> getPneuByCodUnidadeByStatus(@PathParam("codUnidade") Long codUnidade,
                                                  @PathParam("status") String status) throws ProLogException {
        return service.getPneusByCodUnidadeByStatus(codUnidade, status);
    }

    @GET
    @Secured(permissions = {Pilares.Frota.Pneu.CADASTRAR, Pilares.Frota.Pneu.ALTERAR, Pilares.Frota.Pneu.VISUALIZAR})
    @Path("/marcaModelos/{codEmpresa}")
    public List<Marca> getMarcaModeloPneuByCodEmpresa(@PathParam("codEmpresa") Long codEmpresa) {
        return service.getMarcaModeloPneuByCodEmpresa(codEmpresa);
    }

    @GET
    @Secured(permissions = {Pilares.Frota.Pneu.CADASTRAR, Pilares.Frota.Pneu.ALTERAR, Pilares.Frota.Pneu.VISUALIZAR})
    @Path("/modelos/{codModelo}")
    public Modelo getModeloPneu(@PathParam("codModelo") Long codModelo) {
        return service.getModeloPneu(codModelo);
    }

    @GET
    @Secured(permissions = {Pilares.Frota.Pneu.CADASTRAR, Pilares.Frota.Pneu.ALTERAR, Pilares.Frota.Pneu.VISUALIZAR})
    @Path("/dimensao")
    public List<PneuComum.Dimensao> getDimensoes() {
        return service.getDimensoes();
    }

    @POST
    @Secured(permissions = Pilares.Frota.Pneu.VINCULAR_VEICULO)
    @Path("/vincular/{placa}")
    public Response vinculaPneuVeiculo(@PathParam("placa") String placa, List<PneuComum> pneus) {
        if (service.vinculaPneuVeiculo(placa, pneus)) {
            return Response.ok("Pneus vinculados com sucesso.");
        } else {
            return Response.error("Erro ao víncular os pneus ao veículo");
        }
    }

    @GET
    @Secured(permissions = {
            Pilares.Frota.Pneu.Movimentacao.MOVIMENTAR_VEICULO_ESTOQUE,
            Pilares.Frota.Pneu.Movimentacao.MOVIMENTAR_ANALISE,
            Pilares.Frota.Pneu.Movimentacao.MOVIMENTAR_DESCARTE,
            Pilares.Frota.Pneu.CADASTRAR,
            Pilares.Frota.Pneu.ALTERAR,
            Pilares.Frota.Pneu.VISUALIZAR})
    @Path("/bandas/marcas/{codEmpresa}")
    public List<Marca> getMarcaModeloBanda(@PathParam("codEmpresa") Long codEmpresa) {
        return service.getMarcaModeloBanda(codEmpresa);
    }

    @POST
    @Secured(permissions = {
            Pilares.Frota.Pneu.Movimentacao.MOVIMENTAR_VEICULO_ESTOQUE,
            Pilares.Frota.Pneu.Movimentacao.MOVIMENTAR_ANALISE,
            Pilares.Frota.Pneu.Movimentacao.MOVIMENTAR_DESCARTE,
            Pilares.Frota.Pneu.CADASTRAR,
            Pilares.Frota.Pneu.ALTERAR})
    @Path("/bandas/marcas/{codEmpresa}")
    public AbstractResponse insertMarcaBanda(Marca marca, @PathParam("codEmpresa") Long codEmpresa) {
        return service.insertMarcaBanda(marca, codEmpresa);
    }

    @POST
    @Secured(permissions = {
            Pilares.Frota.Pneu.Movimentacao.MOVIMENTAR_VEICULO_ESTOQUE,
            Pilares.Frota.Pneu.Movimentacao.MOVIMENTAR_ANALISE,
            Pilares.Frota.Pneu.Movimentacao.MOVIMENTAR_DESCARTE,
            Pilares.Frota.Pneu.CADASTRAR,
            Pilares.Frota.Pneu.ALTERAR})
    @Path("/bandas/modelos/{codEmpresa}/{codMarcaBanda}")
    public AbstractResponse insertModeloBanda(ModeloBanda modelo, @PathParam("codMarcaBanda") Long codMarcaBanda,
                                              @PathParam("codEmpresa") Long codEmpresa) {
        return service.insertModeloBanda(modelo, codMarcaBanda, codEmpresa);
    }

    @PUT
    @Secured
    @Path("bandas/marcas/{codEmpresa}")
    public Response updateMarcaBanda(Marca marca, @PathParam("codEmpresa") Long codEmpresa) {
        if (service.updateMarcaBanda(marca, codEmpresa)) {
            return Response.ok("Marca atualizada com sucesso");
        } else {
            return Response.error("Erro ao atualizar a marca");
        }
    }

    @PUT
    @Secured
    @Path("bandas/modelos")
    public Response updateModeloBanda(Modelo modelo) {
        if (service.updateModeloBanda(modelo)) {
            return Response.ok("Modelo de banda atualizado com sucesso");
        } else {
            return Response.error("Erro ao atualizar o modelo de banda");
        }
    }

    @GET
    @Secured
    @Path("/unidades/{codUnidade}/{codPneu}")
    public Pneu getPneuByCod(@PathParam("codPneu") Long codPneu,
                             @PathParam("codUnidade") Long codUnidade) throws ProLogException {
        return service.getPneuByCod(codPneu, codUnidade);
    }

    @PUT
    @Secured
    @Path("/{codPneu}/fotos-cadastro/sincronizada")
    public Response marcarFotoComoSincronizada(@PathParam("codPneu") @Required final Long codPneu,
                                               @QueryParam("urlFotoPneu") @Required final String urlFotoPneu) {
        service.marcarFotoComoSincronizada(codPneu, urlFotoPneu);
        return Response.ok("Foto marcada como sincronizada com sucesso");
    }

    /**
     * @deprecated Use {@link #getMarcaModeloBanda(Long)} instead.
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
    public List<Marca> DEPRECATED_GET_MARCA_MODELO_BANDA(@PathParam("codEmpresa") Long codEmpresa) {
        return service.getMarcaModeloBanda(codEmpresa);
    }

    /**
     * @deprecated at 2018-08-22. Utilize {@link #marcarFotoComoSincronizada(Long, String)}.
     * Este método ainda é mantido para permitir que apps antigos sincronizem suas fotos.
     * Como este resource foi liberado apenas para versões do app > 57, nós adicionamos o
     * {@link AppVersionCodeHandler} neste método para permitir que apenas ele tenha um tratamento diferente, permitindo
     * que a sincronia das fotos aconteça para aplicativos antigos (version code > 55). Isso funciona pois o
     * {@link AppVersionCodeHandler} prioriza anotações a nível de método.
     */
    @PUT
    @Secured
    @Path("/unidades/{codUnidade}/{codPneu}/fotos-cadastro/sincronizada")
    @AppVersionCodeHandler(
            implementation = DefaultAppVersionCodeHandler.class,
            targetVersionCode = 55,
            versionCodeHandlerMode = VersionCodeHandlerMode.BLOCK_THIS_VERSION_AND_BELOW,
            actionIfVersionNotPresent = VersionNotPresentAction.BLOCK_ANYWAY)
    public Response DEPRECATED_MARCAR_FOTO_COMO_SINCRONIZADA(@PathParam("codUnidade") @Required final Long codUnidade,
                                                             @PathParam("codPneu") @Required final Long codPneu,
                                                             @QueryParam("urlFotoPneu") @Required final String urlFotoPneu) {
        service.marcarFotoComoSincronizada(codPneu, urlFotoPneu);
        return Response.ok("Foto marcada como sincronizada com sucesso");
    }
}