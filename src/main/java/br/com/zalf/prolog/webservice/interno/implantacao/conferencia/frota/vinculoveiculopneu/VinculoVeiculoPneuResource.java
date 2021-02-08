package br.com.zalf.prolog.webservice.interno.implantacao.conferencia.frota.vinculoveiculopneu;

import br.com.zalf.prolog.webservice.commons.network.Response;
import br.com.zalf.prolog.webservice.commons.network.metadata.Platform;
import br.com.zalf.prolog.webservice.commons.network.metadata.Required;
import br.com.zalf.prolog.webservice.commons.network.metadata.UsedBy;
import br.com.zalf.prolog.webservice.errorhandling.exception.ProLogException;
import org.glassfish.jersey.media.multipart.FormDataContentDisposition;
import org.glassfish.jersey.media.multipart.FormDataParam;
import org.jetbrains.annotations.NotNull;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.io.InputStream;

/**
 * Created on 31/08/2020.
 *
 * @author Thais Francisco (https://github.com/thaisksf)
 */
@Path("/v2/implantacoes/vinculo")
@Consumes({MediaType.MULTIPART_FORM_DATA})
@Produces(MediaType.APPLICATION_JSON + ";charset=utf-8")
public final class VinculoVeiculoPneuResource {
    @NotNull
    private final VinculoVeiculoPneuService service = new VinculoVeiculoPneuService();

    @POST
    @UsedBy(platforms = Platform.WEBSITE)
    @Path("/upload-planilha-import")
    public Response getVerificacaoVinculoVeiculoPneu(
            @HeaderParam("Authorization") @Required final String authorization,
            @QueryParam("codEmpresa") @Required final Long codEmpresa,
            @QueryParam("codUnidade") @Required final Long codUnidade,
            @FormDataParam("file") @Required final InputStream fileInputStream,
            @FormDataParam("file") @Required final FormDataContentDisposition fileDetail) throws ProLogException {
        return service.getVerificacaoVinculoVeiculoPneu(
                authorization,
                codEmpresa,
                codUnidade,
                fileInputStream,
                fileDetail);
    }
}
