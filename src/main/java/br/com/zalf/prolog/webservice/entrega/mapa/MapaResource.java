package br.com.zalf.prolog.webservice.entrega.mapa;

import br.com.zalf.prolog.webservice.commons.network.AbstractResponse;
import br.com.zalf.prolog.webservice.commons.network.metadata.Platform;
import br.com.zalf.prolog.webservice.commons.network.metadata.UsedBy;
import br.com.zalf.prolog.webservice.interceptors.auth.Secured;
import br.com.zalf.prolog.webservice.permissao.pilares.Pilares;
import org.glassfish.jersey.media.multipart.FormDataContentDisposition;
import org.glassfish.jersey.media.multipart.FormDataParam;
import org.jetbrains.annotations.NotNull;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.io.InputStream;

/**
 * Created on 2020-05-22
 *
 * @author Luiz Felipe (https://github.com/luizfp)
 */
@Path("/v2/mapas")
@Consumes(MediaType.APPLICATION_JSON + ";charset=utf-8")
@Produces(MediaType.APPLICATION_JSON + ";charset=utf-8")
public final class MapaResource {
    @NotNull
    private final MapaService service = new MapaService();

    @POST
    @Secured(permissions = Pilares.Entrega.Upload.MAPA_TRACKING)
    @UsedBy(platforms = Platform.WEBSITE)
    @Path("/uploads/{codUnidade}")
    @Consumes({MediaType.MULTIPART_FORM_DATA})
    public AbstractResponse uploadMapa(
            @FormDataParam("file") final InputStream fileInputStream,
            @FormDataParam("file") final FormDataContentDisposition fileDetail,
            @PathParam("codUnidade") final Long codUnidade) {
        return service.insertOrUpdateMapa(fileInputStream, codUnidade);
    }
}
