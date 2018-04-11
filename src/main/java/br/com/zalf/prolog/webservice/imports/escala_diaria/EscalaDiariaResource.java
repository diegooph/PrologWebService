package br.com.zalf.prolog.webservice.imports.escala_diaria;

import br.com.zalf.prolog.webservice.commons.util.Platform;
import br.com.zalf.prolog.webservice.commons.util.UsedBy;
import org.glassfish.jersey.media.multipart.FormDataContentDisposition;
import org.glassfish.jersey.media.multipart.FormDataParam;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.io.InputStream;
import java.util.List;

/**
 * Created on 10/04/18.
 *
 * @author Diogenes Vanzela (https://github.com/diogenesvanzella)
 */
@Path("/escalas-diarias")
@Consumes(MediaType.APPLICATION_JSON + ";charset=utf-8")
@Produces(MediaType.APPLICATION_JSON + ";charset=utf-8")
public class EscalaDiariaResource {

    private final EscalaDiariaService service = new EscalaDiariaService();

    @POST
    @UsedBy(platforms = {Platform.WEBSITE, Platform.ANDROID})
    @Path("upload/{codUnidade}")
    @Consumes({MediaType.MULTIPART_FORM_DATA})
    public void uploadMapa(@PathParam("codUnidade") Long codUnidade,
                           @FormDataParam("file") InputStream fileInputStream,
                           @FormDataParam("file") FormDataContentDisposition fileDetail) throws Exception {
        service.uploadMapa(codUnidade, fileInputStream, fileDetail);
    }

    @POST
    @Path("/{codUnidade}")
    public void insertEscalaDiaria(@PathParam("codUnidade") Long codUnidade,
                                   EscalaDiariaItem escalaDiariaItem) throws Exception {
        service.insertOrUpdateEscalaDiaria(codUnidade, escalaDiariaItem, true);
    }

    @PUT
    @Path("/{codUnidade}")
    public void updateEscalaDiaria(@PathParam("codUnidade") Long codUnidade,
                                   EscalaDiariaItem escalaDiariaItem) throws Exception {
        service.insertOrUpdateEscalaDiaria(codUnidade, escalaDiariaItem, false);
    }

    @GET
    @Path("/{codUnidade}")
    public List<EscalaDiaria> getEscalasDiarias(@PathParam("codUnidade") Long codUnidade,
                                                @QueryParam("dataInicial") String dataInicial,
                                                @QueryParam("dataFinal") String dataFinal) {
        return service.getEscalasDiarias(codUnidade, dataInicial, dataFinal);
    }

    @DELETE
    @Path("/{codUnidade}")
    public void deleteEscalaDiariaItens(@PathParam("codUnidade") Long codUnidade,
                                        @QueryParam("codEscalas") List<Long> codEscalas) throws Exception {
        service.deleteEscalaDiariaItens(codUnidade, codEscalas);
    }
}
