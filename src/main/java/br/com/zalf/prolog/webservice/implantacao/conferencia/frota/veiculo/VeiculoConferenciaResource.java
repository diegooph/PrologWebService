package br.com.zalf.prolog.webservice.implantacao.conferencia.frota.veiculo;

import br.com.zalf.prolog.webservice.commons.network.AbstractResponse;
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
import javax.ws.rs.core.StreamingOutput;
import java.io.InputStream;

/**
 * Created on 23/07/19.
 *
 * @author Thais Francisco (https://github.com/thaisksf)
 */
@Path("/implantacoes/veiculos")
@Consumes({MediaType.MULTIPART_FORM_DATA})
@Produces("application/csv")
public final class VeiculoConferenciaResource {

    @NotNull
    private final VeiculoConferenciaService service = new VeiculoConferenciaService();

    @POST
    @UsedBy(platforms = Platform.WEBSITE)
    @Path("/upload-planilha-import")
    public Response getVerificacaoPlanilhaImportVeiculo(
            @HeaderParam("usernamePassword") @Required final String usernamePassword,
            @QueryParam("codEmpresa") @Required final Long codEmpresa,
            @QueryParam("codUnidade") @Required final Long codUnidade,
            @FormDataParam("file") @Required final InputStream fileInputStream,
            @FormDataParam("file") @Required final FormDataContentDisposition fileDetail) throws ProLogException {
        service.getVerificacaoPlanilhaImportVeiculo(
                usernamePassword,
                codEmpresa,
                codUnidade,
                fileInputStream,
                fileDetail);
        return Response.ok("Dados cadastrados no banco com sucesso");
    }
}