package br.com.zalf.prolog.webservice.entrega.escaladiaria;

import br.com.zalf.prolog.webservice.commons.network.Response;
import br.com.zalf.prolog.webservice.commons.util.Platform;
import br.com.zalf.prolog.webservice.commons.util.UsedBy;
import br.com.zalf.prolog.webservice.errorhandling.exception.EscalaDiariaException;
import br.com.zalf.prolog.webservice.interceptors.auth.Secured;
import br.com.zalf.prolog.webservice.interceptors.debug.ConsoleDebugLog;
import br.com.zalf.prolog.webservice.permissao.pilares.Pilares;
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
@ConsoleDebugLog
@Consumes(MediaType.APPLICATION_JSON + ";charset=utf-8")
@Produces(MediaType.APPLICATION_JSON + ";charset=utf-8")
public class EscalaDiariaResource {

    private final EscalaDiariaService service = new EscalaDiariaService();

    @POST
    @UsedBy(platforms = Platform.WEBSITE)
    @Secured(permissions = Pilares.Entrega.EscalaDiaria.INSERIR_REGISTRO)
    @Path("/upload/{codUnidade}")
    @Consumes({MediaType.MULTIPART_FORM_DATA})
    public Response uploadEscala(@HeaderParam("Authorization") final String token,
                                 @PathParam("codUnidade") final Long codUnidade,
                                 @FormDataParam("file") final InputStream fileInputStream,
                                 @FormDataParam("file") final FormDataContentDisposition fileDetail) throws Exception {
        return service.uploadEscala(token, codUnidade, fileInputStream, fileDetail);
    }

    @POST
    @UsedBy(platforms = Platform.WEBSITE)
    @Secured(permissions = Pilares.Entrega.EscalaDiaria.EDITAR)
    @Path("/{codUnidade}")
    public Response insertEscalaDiaria(@HeaderParam("Authorization") final String token,
                                       @PathParam("codUnidade") final Long codUnidade,
                                       final EscalaDiariaItem escalaDiariaItem) throws Exception {
        return service.insertEscalaDiaria(token, codUnidade, escalaDiariaItem);
    }

    @PUT
    @UsedBy(platforms = Platform.WEBSITE)
    @Secured(permissions = Pilares.Entrega.EscalaDiaria.EDITAR)
    @Path("/{codUnidade}")
    public Response updateEscalaDiaria(@HeaderParam("Authorization") final String token,
                                       @PathParam("codUnidade") final Long codUnidade,
                                       final EscalaDiariaItem escalaDiariaItem) throws Exception {
        return service.updateEscalaDiaria(token, codUnidade, escalaDiariaItem);
    }

    @GET
    @UsedBy(platforms = {Platform.WEBSITE, Platform.ANDROID})
    @Secured(permissions = {Pilares.Entrega.EscalaDiaria.VISUALIZAR, Pilares.Entrega.EscalaDiaria.EDITAR})
    @Path("/{codUnidade}")
    public List<EscalaDiaria> getEscalasDiarias(@PathParam("codUnidade") final Long codUnidade,
                                                @QueryParam("dataInicial") final String dataInicial,
                                                @QueryParam("dataFinal") final String dataFinal) throws EscalaDiariaException {
        return service.getEscalasDiarias(codUnidade, dataInicial, dataFinal);
    }

    @GET
    @UsedBy(platforms = {Platform.WEBSITE, Platform.ANDROID})
    @Secured(permissions = {Pilares.Entrega.EscalaDiaria.VISUALIZAR, Pilares.Entrega.EscalaDiaria.EDITAR})
    @Path("/{codUnidade}/{codEscala}")
    public EscalaDiariaItem getEscalaDiariaItem(@PathParam("codUnidade") final Long codUnidade,
                                                @PathParam("codEscala") final Long codEscala) throws EscalaDiariaException {
        return service.getEscalaDiariaItem(codUnidade, codEscala);
    }

    @DELETE
    @UsedBy(platforms = Platform.WEBSITE)
    @Secured(permissions = Pilares.Entrega.EscalaDiaria.DELETAR)
    @Path("/{codUnidade}")
    public Response deleteEscalaDiariaItens(@PathParam("codUnidade") final Long codUnidade,
                                            final List<Long> codEscalas) throws Exception {
        return service.deleteEscalaDiariaItens(codUnidade, codEscalas);
    }
}
