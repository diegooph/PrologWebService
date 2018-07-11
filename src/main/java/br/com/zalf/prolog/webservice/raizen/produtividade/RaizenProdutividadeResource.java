package br.com.zalf.prolog.webservice.raizen.produtividade;

import br.com.zalf.prolog.webservice.commons.network.Response;
import br.com.zalf.prolog.webservice.commons.util.Platform;
import br.com.zalf.prolog.webservice.commons.util.UsedBy;
import br.com.zalf.prolog.webservice.errorhandling.exception.RaizenProdutividadeException;
import br.com.zalf.prolog.webservice.interceptors.auth.Secured;
import br.com.zalf.prolog.webservice.interceptors.log.DebugLog;
import br.com.zalf.prolog.webservice.permissao.pilares.Pilares;
import org.glassfish.jersey.media.multipart.FormDataContentDisposition;
import org.glassfish.jersey.media.multipart.FormDataParam;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.io.InputStream;
import java.util.List;

/**
 * Created on 04/07/18.
 *
 * @author Thais Francisco (https://github.com/thaisksf)
 */
@Path("/raizen-produtividade")
@DebugLog
@Consumes(MediaType.APPLICATION_JSON + ";charset=utf-8")
@Produces(MediaType.APPLICATION_JSON + ";charset=utf-8")
public class RaizenProdutividadeResource {

    private final RaizenProdutividadeService service = new RaizenProdutividadeService();

    @POST
    @UsedBy(platforms = Platform.WEBSITE)
    @Secured(permissions = Pilares.Gente.RaizenProdutividade.UPLOAD)
    @Path("/upload/{codEmpresa}")
    @Consumes({MediaType.MULTIPART_FORM_DATA})
    public Response uploadEscala(@HeaderParam("Authorization") String token,
                                 @PathParam("codEmpresa") Long codEmpresa,
                                 @FormDataParam("file") InputStream fileInputStream,
                                 @FormDataParam("file") FormDataContentDisposition fileDetail) throws Exception {
        return service.uploadRaizenProdutividade(token, codEmpresa, fileInputStream, fileDetail);
    }

    @POST
    @UsedBy(platforms = Platform.WEBSITE)
    @Secured(permissions = Pilares.Gente.RaizenProdutividade.EDITAR)
    @Path("/{codEmpresa}")
    public Response insertEscalaDiaria(@HeaderParam("Authorization") String token,
                                       @PathParam("codEmpresa") Long codEmpresa,
                                       RaizenProdutividadeItemInsert raizenProdutividadeItemInsert) throws Exception {
        return service.insertRaizenProdutividade(token, codEmpresa, raizenProdutividadeItemInsert);
    }

    @PUT
    @UsedBy(platforms = Platform.WEBSITE)
    @Secured(permissions = Pilares.Gente.RaizenProdutividade.EDITAR)
    @Path("/{codEmpresa}")
    public Response updateRaizenProdutividade(@HeaderParam("Authorization") String token,
                                              @PathParam("codEmpresa") Long codEmpresa,
                                              RaizenProdutividadeItemInsert raizenProdutividadeItemInsert) throws Exception {
        return service.updateRaizenProdutividade(token, codEmpresa, raizenProdutividadeItemInsert);
    }

    @GET
    @UsedBy(platforms = {Platform.WEBSITE, Platform.ANDROID})
    @Secured(permissions = {Pilares.Gente.RaizenProdutividade.VISUALIZAR_TODOS, Pilares.Gente.RaizenProdutividade.EDITAR})
    @Path("/{codEmpresa}")
    public List<RaizenProdutividade> getRaizenProdutividade(@PathParam("codEmpresa") Long codEmpresa,
                                                            @QueryParam("data") String data)
            throws RaizenProdutividadeException {
        return service.getRaizenProdutividade(codEmpresa, data);
    }

/*    @GET
    @UsedBy(platforms = {Platform.WEBSITE, Platform.ANDROID})
    @Secured(permissions = {Pilares.Entrega.EscalaDiaria.VISUALIZAR, Pilares.Entrega.EscalaDiaria.EDITAR})
    @Path("/{codUnidade}/{codEscala}")
    public EscalaDiariaItem getEscalaDiariaItem(@PathParam("codUnidade") Long codUnidade,
                                                @PathParam("codEscala") Long codEscala) throws EscalaDiariaException {
        return service.getEscalaDiariaItem(codUnidade, codEscala);
    }*/

    @DELETE
    @UsedBy(platforms = Platform.WEBSITE)
    @Secured(permissions = Pilares.Gente.RaizenProdutividade.DELETAR)
    @Path("/{codEmpresa}")
    public Response deleteRaizenProdutividadeItens(@PathParam("codEmpresa") Long codEmpresa,
                                            List<Long> codRaizenProdutividades) throws Exception {
        return service.deleteRaizenProdutividade(codEmpresa, codRaizenProdutividades);
    }
}
