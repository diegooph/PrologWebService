package br.com.zalf.prolog.webservice.raizen.produtividade;

import br.com.zalf.prolog.webservice.commons.network.Response;
import br.com.zalf.prolog.webservice.commons.util.Platform;
import br.com.zalf.prolog.webservice.commons.util.UsedBy;
import br.com.zalf.prolog.webservice.interceptors.auth.Secured;
import br.com.zalf.prolog.webservice.permissao.pilares.Pilares;
import org.glassfish.jersey.media.multipart.FormDataContentDisposition;
import org.glassfish.jersey.media.multipart.FormDataParam;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.io.InputStream;

/**
 * Created on 04/07/18.
 *
 * @author Thais Francisco (https://github.com/thaisksf)
 */
public class RaizenProdutividadeResource {

    private final RaizenProdutividadeService service = new RaizenProdutividadeService();

    @POST
    @UsedBy(platforms = Platform.WEBSITE)
    @Secured(permissions = Pilares.Entrega.EscalaDiaria.UPLOAD)
    @Path("/upload/{codUnidade}")
    @Consumes({MediaType.MULTIPART_FORM_DATA})
    public Response uploadEscala(@HeaderParam("Authorization") String token,
                                 @PathParam("codUnidade") Long codUnidade,
                                 @FormDataParam("file") InputStream fileInputStream,
                                 @FormDataParam("file") FormDataContentDisposition fileDetail) throws Exception {
        return service.uploadRaizenProdutividade(token, codUnidade, fileInputStream, fileDetail);
    }
}
