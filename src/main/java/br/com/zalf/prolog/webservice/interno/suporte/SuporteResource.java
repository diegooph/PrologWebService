package br.com.zalf.prolog.webservice.interno.suporte;

import br.com.zalf.prolog.webservice.commons.network.Response;
import br.com.zalf.prolog.webservice.interno.suporte._model.InternalEmpresa;
import lombok.RequiredArgsConstructor;
import org.glassfish.jersey.media.multipart.FormDataContentDisposition;
import org.glassfish.jersey.media.multipart.FormDataParam;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.io.InputStream;
import java.util.List;

@RestController
@Path("/v2/interno/suporte")
@Consumes({MediaType.MULTIPART_FORM_DATA})
@Produces(MediaType.APPLICATION_JSON + ";charset=utf-8")
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public final class SuporteResource {
    @NotNull
    private final SuporteService service;

    @POST
    @Path("/cadastrar-empresa")
    public Response insertEmpresa(
            @HeaderParam("Authorization") final String authorization,
            @QueryParam("nomeEmpresa") final String nomeEmpresa,
            @FormDataParam("file") final InputStream fileInputStream,
            @FormDataParam("file") final FormDataContentDisposition fileDetail) {
        return service.insertEmpresa(
                authorization,
                nomeEmpresa,
                fileInputStream,
                fileDetail);
    }

    @GET
    @Path("/listar-empresas")
    public List<InternalEmpresa> getTodasEmpresas(@HeaderParam("Authorization") final String authorization) {
        return service.getTodasEmpresas(authorization);
    }

    @GET
    @Path("/buscar-empresa/{codEmpresa}")
    public InternalEmpresa getEmpresa(@HeaderParam("Authorization") final String authorization,
                                      @PathParam("codEmpresa") final Long codEmpresa) {
        return service.getEmpresa(authorization, codEmpresa);
    }

    @POST
    @Path("/alterar-imagem-logo-empresa")
    public Response alterarImagemLogoEmpresa(
            @HeaderParam("Authorization") final String authorization,
            @QueryParam("codEmpresa") final Long codEmpresa,
            @FormDataParam("file") final InputStream fileInputStream,
            @FormDataParam("file") final FormDataContentDisposition fileDetail) {
        return service.alterarImagemLogoEmpresa(
                authorization,
                codEmpresa,
                fileInputStream,
                fileDetail);
    }
}
