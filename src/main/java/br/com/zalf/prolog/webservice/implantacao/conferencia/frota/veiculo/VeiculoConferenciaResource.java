package br.com.zalf.prolog.webservice.implantacao.conferencia.frota.veiculo;

import br.com.zalf.prolog.webservice.commons.util.Platform;
import br.com.zalf.prolog.webservice.commons.util.UsedBy;
import br.com.zalf.prolog.webservice.errorhandling.exception.ProLogException;
import org.glassfish.jersey.media.multipart.FormDataParam;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.StreamingOutput;
import java.io.InputStream;

/**
 * Created on 23/07/19.
 *
 * @author Thais Francisco (https://github.com/thaisksf)
 */

@Path("/veiculo-conferencia")
@Consumes(MediaType.APPLICATION_JSON + ";charset=utf-8")
@Produces(MediaType.APPLICATION_JSON + ";charset=utf-8")

public class VeiculoConferenciaResource {

    private VeiculoConferenciaService service = new VeiculoConferenciaService();

    @PUT
    @UsedBy(platforms = Platform.WEBSITE)
    @Path("/upload-planilha-csv")
    @Consumes({MediaType.MULTIPART_FORM_DATA})
    public StreamingOutput getVerificacaoPlanilhaImportVeiculoCsv(@QueryParam("codUnidade") Long codUnidade,
                                                 @FormDataParam("file") InputStream fileInputStream) throws ProLogException {
        return outputStream -> service.getVerificacaoPlanilhaImportVeiculoCsv(outputStream ,codUnidade, fileInputStream);
    }

}
