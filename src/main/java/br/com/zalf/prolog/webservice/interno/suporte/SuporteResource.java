package br.com.zalf.prolog.webservice.interno.suporte;

import br.com.zalf.prolog.webservice.commons.network.Response;
import br.com.zalf.prolog.webservice.commons.network.metadata.Platform;
import br.com.zalf.prolog.webservice.commons.network.metadata.Required;
import br.com.zalf.prolog.webservice.commons.network.metadata.UsedBy;
import br.com.zalf.prolog.webservice.errorhandling.exception.ProLogException;
import lombok.RequiredArgsConstructor;
import org.glassfish.jersey.media.multipart.FormDataContentDisposition;
import org.glassfish.jersey.media.multipart.FormDataParam;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.io.InputStream;

@RestController
@Path("/v2/interno/suporte")
@Consumes({MediaType.MULTIPART_FORM_DATA})
@Produces(MediaType.APPLICATION_JSON + ";charset=utf-8")
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public final class SuporteResource {
    @NotNull
    private final SuporteService service;

    @POST
    @UsedBy(platforms = Platform.WEBSITE)
    @Path("/alterar-imagem-logo-empresa")
    public Response alterarImagemLogoEmpresa(
            @HeaderParam("Authorization") @Required final String authorization,
            @QueryParam("codEmpresa") @Required final Long codEmpresa,
            @FormDataParam("file") @Required final InputStream fileInputStream,
            @FormDataParam("file") @Required final FormDataContentDisposition fileDetail) throws ProLogException {
        return service.alterarImagemLogoEmpresa(
                authorization,
                codEmpresa,
                fileInputStream,
                fileDetail);
    }
}
