package br.com.zalf.prolog.webservice.gente.prontuarioCondutor;

import br.com.zalf.prolog.webservice.commons.network.Response;
import br.com.zalf.prolog.webservice.commons.network.metadata.Platform;
import br.com.zalf.prolog.webservice.commons.network.metadata.UsedBy;
import br.com.zalf.prolog.webservice.commons.util.datetime.Now;
import br.com.zalf.prolog.webservice.gente.prontuarioCondutor.model.ProntuarioCondutor;
import br.com.zalf.prolog.webservice.interceptors.auth.Secured;
import br.com.zalf.prolog.webservice.permissao.pilares.Pilares;
import org.apache.commons.io.IOUtils;
import org.glassfish.jersey.media.multipart.FormDataContentDisposition;
import org.glassfish.jersey.media.multipart.FormDataParam;
import org.jetbrains.annotations.NotNull;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

/**
 * Created by Zart on 03/07/2017.
 */
@Path("/v2/prontuarios")
@Consumes(MediaType.APPLICATION_JSON + ";charset=utf-8")
@Produces(MediaType.APPLICATION_JSON + ";charset=utf-8")
public final class ProntuarioCondutorResource {

    @NotNull
    private final ProntuarioCondutorService service = new ProntuarioCondutorService();

    @GET
    @Path("/{cpf}")
    @Secured(permissions = {
            Pilares.Gente.ProntuarioCondutor.VISUALIZAR_PROPRIO,
            Pilares.Gente.ProntuarioCondutor.VISUALIZAR_TODOS})
    public ProntuarioCondutor getProntuario(@PathParam("cpf") Long cpf) {
        return service.getProntuario(cpf);
    }

    @GET
    @Path("/{codUnidade}/{codEquipe}")
    @Secured(permissions = {
            Pilares.Gente.ProntuarioCondutor.VISUALIZAR_TODOS,
            Pilares.Gente.ProntuarioCondutor.UPLOAD})
    public List<ProntuarioCondutor> getResumoProntuarios(@PathParam("codUnidade") Long codUnidade,
                                                         @PathParam("codEquipe") String codEquipe) {
        return service.getResumoProntuarios(codUnidade, codEquipe);
    }

    @GET
    @Path("/{cpf}/pontuacao-total")
    @Secured(permissions = {
            Pilares.Gente.ProntuarioCondutor.VISUALIZAR_PROPRIO,
            Pilares.Gente.ProntuarioCondutor.VISUALIZAR_TODOS})
    public Double getPontuacaoProntuario(@PathParam("cpf") Long cpf) {
        return service.getPontuacaoProntuario(cpf);
    }

    @POST
    @Path("/upload/{codUnidade}")
    @Consumes({MediaType.MULTIPART_FORM_DATA})
    @Secured(permissions = Pilares.Gente.ProntuarioCondutor.UPLOAD)
    @UsedBy(platforms = Platform.WEBSITE)
    public Response uploadProntuario(
            @FormDataParam("file") InputStream fileInputStream,
            @FormDataParam("file") FormDataContentDisposition fileDetail,
            @PathParam("codUnidade") Long codUnidade) {
        try {
            final String fileName = Now.getUtcMillis() + "_" +
                    codUnidade + "_" + fileDetail.getFileName().replace(" ", "_");
            final File tmpDir = new File(System.getProperty("java.io.tmpdir"), "prontuarios");
            if (!tmpDir.exists()) {
                tmpDir.mkdir();
            }
            final File file = new File(tmpDir, fileName);
            final FileOutputStream out = new FileOutputStream(file);
            IOUtils.copy(fileInputStream, out);
            IOUtils.closeQuietly(out);
            service.insertOrUpdate(file.getPath());
            return Response.ok("Prontuários inseridos com sucesso");
        } catch (IOException e) {
            e.printStackTrace();
            return Response.error("Erro ao enviar o arquivo");
        }
    }
}
