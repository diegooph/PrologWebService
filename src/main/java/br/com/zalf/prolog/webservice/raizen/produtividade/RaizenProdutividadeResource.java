package br.com.zalf.prolog.webservice.raizen.produtividade;

import br.com.zalf.prolog.webservice.commons.network.Response;
import br.com.zalf.prolog.webservice.commons.util.Platform;
import br.com.zalf.prolog.webservice.commons.util.UsedBy;
import br.com.zalf.prolog.webservice.errorhandling.exception.ProLogException;
import br.com.zalf.prolog.webservice.interceptors.auth.Secured;
import br.com.zalf.prolog.webservice.interceptors.log.DebugLog;
import br.com.zalf.prolog.webservice.permissao.pilares.Pilares;
import br.com.zalf.prolog.webservice.raizen.produtividade.model.RaizenProdutividade;
import br.com.zalf.prolog.webservice.raizen.produtividade.model.RaizenProdutividadeIndividualHolder;
import br.com.zalf.prolog.webservice.raizen.produtividade.model.insert.RaizenProdutividadeItemInsert;
import br.com.zalf.prolog.webservice.raizen.produtividade.model.itens.RaizenProdutividadeItemVisualizacao;
import org.glassfish.jersey.media.multipart.FormDataContentDisposition;
import org.glassfish.jersey.media.multipart.FormDataParam;
import org.jetbrains.annotations.NotNull;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.io.InputStream;
import java.util.List;

/**
 * Created on 04/07/18.
 *
 * @author Thais Francisco (https://github.com/thaisksf)
 */
@Path("/raizen/produtividades")
@DebugLog
@Consumes(MediaType.APPLICATION_JSON + ";charset=utf-8")
@Produces(MediaType.APPLICATION_JSON + ";charset=utf-8")
public class RaizenProdutividadeResource {

    @NotNull
    private final RaizenProdutividadeService service = new RaizenProdutividadeService();

    @POST
    @UsedBy(platforms = Platform.WEBSITE)
    @Secured(permissions = Pilares.Entrega.RaizenProdutividade.UPLOAD)
    @Path("/upload/{codEmpresa}")
    @Consumes({MediaType.MULTIPART_FORM_DATA})
    public Response uploadRaizenProdutividade(@HeaderParam("Authorization") String token,
                                              @PathParam("codEmpresa") Long codEmpresa,
                                              @FormDataParam("file") InputStream fileInputStream,
                                              @FormDataParam("file") FormDataContentDisposition fileDetail)
            throws ProLogException {
        return service.uploadRaizenProdutividade(token, codEmpresa, fileInputStream, fileDetail);
    }

    @POST
    @UsedBy(platforms = Platform.WEBSITE)
    @Secured(permissions = Pilares.Entrega.RaizenProdutividade.EDITAR)
    @Path("/{codEmpresa}")
    public Response insertRaizenProdutividade(@HeaderParam("Authorization") String token,
                                              @PathParam("codEmpresa") Long codEmpresa,
                                              RaizenProdutividadeItemInsert raizenProdutividadeItemInsert)
            throws ProLogException {
        return service.insertRaizenProdutividade(token, codEmpresa, raizenProdutividadeItemInsert);
    }

    @PUT
    @UsedBy(platforms = Platform.WEBSITE)
    @Secured(permissions = Pilares.Entrega.RaizenProdutividade.EDITAR)
    @Path("/{codEmpresa}")
    public Response updateRaizenProdutividade(@HeaderParam("Authorization") String token,
                                              @PathParam("codEmpresa") Long codEmpresa,
                                              RaizenProdutividadeItemInsert raizenProdutividadeItemInsert)
            throws ProLogException {
        return service.updateRaizenProdutividade(token, codEmpresa, raizenProdutividadeItemInsert);
    }

    @GET
    @UsedBy(platforms = {Platform.WEBSITE})
    @Secured(permissions = {
            Pilares.Entrega.RaizenProdutividade.VISUALIZAR_TODOS,
            Pilares.Entrega.RaizenProdutividade.EDITAR,
            Pilares.Entrega.RaizenProdutividade.UPLOAD,
            Pilares.Entrega.RaizenProdutividade.DELETAR})
    @Path("/{codEmpresa}")
    public List<RaizenProdutividade> getRaizenProdutividade(@PathParam("codEmpresa") Long codEmpresa,
                                                            @QueryParam("dataInicial") String dataIncial,
                                                            @QueryParam("dataFinal") String dataFinal,
                                                            @QueryParam("tipoAgrupamento") String agrupamento)
            throws ProLogException {
        return service.getRaizenProdutividade(codEmpresa, dataIncial, dataFinal, agrupamento);
    }

    @GET
    @UsedBy(platforms = Platform.WEBSITE)
    @Secured(permissions = {
            Pilares.Entrega.RaizenProdutividade.VISUALIZAR_TODOS,
            Pilares.Entrega.RaizenProdutividade.EDITAR,
            Pilares.Entrega.RaizenProdutividade.UPLOAD,
            Pilares.Entrega.RaizenProdutividade.DELETAR})
    @Path("/{codEmpresa}/itens/{codItem}")
    public RaizenProdutividadeItemVisualizacao getRaizenProdutividadeItem(@PathParam("codEmpresa") final Long codEmpresa,
                                                                          @PathParam("codItem") final Long codItem)
            throws ProLogException {
        return service.getRaizenProdutividadeItem(codEmpresa, codItem);
    }

    @GET
    @UsedBy(platforms = Platform.ANDROID)
    @Secured(permissions = {
            Pilares.Entrega.RaizenProdutividade.VISUALIZAR_PROPRIOS,
            Pilares.Entrega.RaizenProdutividade.VISUALIZAR_TODOS})
    @Path("/colaboradores/{codColaborador}")
    public RaizenProdutividadeIndividualHolder getRaizenProdutividadeIndividual(
            @PathParam("codColaborador") Long codColaborador,
            @QueryParam("mes") int mes,
            @QueryParam("ano") int ano) throws ProLogException {
        return service.getRaizenProdutividadeIndividual(codColaborador, mes, ano);
    }

    @DELETE
    @UsedBy(platforms = Platform.WEBSITE)
    @Secured(permissions = Pilares.Entrega.RaizenProdutividade.DELETAR)
    @Path("/{codEmpresa}")
    public Response deleteRaizenProdutividadeItens(@PathParam("codEmpresa") Long codEmpresa,
                                                   List<Long> codRaizenProdutividades) throws ProLogException {
        return service.deleteRaizenProdutividade(codEmpresa, codRaizenProdutividades);
    }
}