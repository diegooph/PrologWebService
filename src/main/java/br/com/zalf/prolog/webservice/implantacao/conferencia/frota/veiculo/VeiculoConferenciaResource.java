package br.com.zalf.prolog.webservice.implantacao.conferencia.frota.veiculo;

import br.com.zalf.prolog.webservice.commons.network.Response;
import br.com.zalf.prolog.webservice.commons.util.Platform;
import br.com.zalf.prolog.webservice.commons.util.Required;
import br.com.zalf.prolog.webservice.commons.util.UsedBy;
import br.com.zalf.prolog.webservice.errorhandling.exception.ProLogException;
import org.glassfish.jersey.media.multipart.FormDataContentDisposition;
import org.glassfish.jersey.media.multipart.FormDataParam;
import org.jetbrains.annotations.NotNull;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.io.InputStream;

/**
 * Created on 23/07/19.
 *
 * @author Thais Francisco (https://github.com/thaisksf)
 */
@Path("/implantacoes/veiculos")
@Consumes({MediaType.MULTIPART_FORM_DATA})
@Produces(MediaType.APPLICATION_JSON + ";charset=utf-8")
public final class VeiculoConferenciaResource {

    @NotNull
    private final VeiculoConferenciaService service = new VeiculoConferenciaService();

    @POST
    @UsedBy(platforms = Platform.WEBSITE)
    @Path("/upload-planilha-import")
    public Response getVerificacaoPlanilhaImportVeiculo(
            @HeaderParam("Authorization") @Required final String authorization,
            @QueryParam("codEmpresa") @Required final Long codEmpresa,
            @QueryParam("codUnidade") @Required final Long codUnidade,
            @FormDataParam("file") @Required final InputStream fileInputStream,
            @FormDataParam("file") @Required final FormDataContentDisposition fileDetail) throws ProLogException {
        return service.getVerificacaoPlanilhaImportVeiculo(
                authorization,
                codEmpresa,
                codUnidade,
                fileInputStream,
                fileDetail);
    }
}