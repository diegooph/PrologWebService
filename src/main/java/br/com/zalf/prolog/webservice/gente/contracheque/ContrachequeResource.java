package br.com.zalf.prolog.webservice.gente.contracheque;

import br.com.zalf.prolog.webservice.commons.network.Response;
import br.com.zalf.prolog.webservice.commons.network.metadata.Platform;
import br.com.zalf.prolog.webservice.commons.network.metadata.UsedBy;
import br.com.zalf.prolog.webservice.commons.util.Log;
import br.com.zalf.prolog.webservice.commons.util.datetime.Now;
import br.com.zalf.prolog.webservice.errorhandling.exception.ProLogException;
import br.com.zalf.prolog.webservice.gente.contracheque.model.Contracheque;
import br.com.zalf.prolog.webservice.gente.contracheque.model.ItemImportContracheque;
import br.com.zalf.prolog.webservice.interceptors.auth.Secured;
import br.com.zalf.prolog.webservice.permissao.pilares.Pilares;
import org.apache.commons.io.IOUtils;
import org.glassfish.jersey.media.multipart.FormDataContentDisposition;
import org.glassfish.jersey.media.multipart.FormDataParam;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

/**
 * Created by Zalf on 23/11/16.
 */
@Path("/contracheque")
@Consumes(MediaType.APPLICATION_JSON + ";charset=utf-8")
@Produces(MediaType.APPLICATION_JSON + ";charset=utf-8")
public class ContrachequeResource {

    public static final String TAG = ContrachequeResource.class.getSimpleName();
    private final ContrachequeService service = new ContrachequeService();

    @POST
    @Secured(permissions = Pilares.Gente.PreContracheque.UPLOAD_E_EDICAO)
    @UsedBy(platforms = Platform.WEBSITE)
    @Consumes({MediaType.MULTIPART_FORM_DATA})
    @Path("/import/{codUnidade}/{ano}/{mes}")
    public Response upload(@PathParam("codUnidade") final Long codUnidade,
                           @PathParam("ano") final int ano,
                           @PathParam("mes") final int mes,
                           @FormDataParam("file") final InputStream fileInputStream,
                           @FormDataParam("file") final FormDataContentDisposition fileDetail) {

        try {
            final String fileName = String.valueOf(Now.getUtcMillis()) + "_" + mes + "_" + ano + "_" + codUnidade
                    + "_" + fileDetail.getFileName().replace(" ", "_");
            Log.d(TAG, "fileName: " + fileName);
            // Pasta temporária da JVM
            final File tmpDir = new File(System.getProperty("java.io.tmpdir"), "contracheque");
            if (!tmpDir.exists()) {
                // Cria a pasta mapas se não existe
                tmpDir.mkdir();
            }
            // Cria o arquivo
            final File file = new File(tmpDir, fileName);
            final FileOutputStream out = new FileOutputStream(file);
            IOUtils.copy(fileInputStream, out);
            IOUtils.closeQuietly(out);
            return service.insertOrUpdateContracheque(file.getPath(), ano, mes, codUnidade);
        } catch (final IOException e) {
            e.printStackTrace();
            return Response.error("Erro ao inserir os dados");
        }
    }

    @PUT
    @Secured(permissions = Pilares.Gente.PreContracheque.UPLOAD_E_EDICAO)
    @Path("/dados/{codUnidade}/{ano}/{mes}/{cpf}")
    @Consumes(MediaType.APPLICATION_JSON + ";charset=utf-8")
    public Response updateItemImportContracheque(final ItemImportContracheque item,
                                                 @PathParam("ano") final int ano,
                                                 @PathParam("mes") final int mes,
                                                 @PathParam("codUnidade") final Long codUnidade) {
        if (service.updateItemImportContracheque(item, ano, mes, codUnidade)) {
            return Response.ok("Item alterado com sucesso.");
        } else {
            return Response.error("Erro ao editar o item.");
        }
    }

    @GET
    @Secured(permissions = Pilares.Gente.PreContracheque.VISUALIZAR)
    @UsedBy(platforms = Platform.ANDROID)
    @Path("/{codUnidade}/{cpf}/{ano}/{mes}")
    public Contracheque getContracheque(@PathParam("cpf") final Long cpf,
                                        @PathParam("codUnidade") final Long codUnidade,
                                        @PathParam("ano") final int ano,
                                        @PathParam("mes") final int mes) {
        return service.getPreContracheque(cpf, codUnidade, ano, mes);
    }


    @GET
    @Secured(permissions = Pilares.Gente.PreContracheque.UPLOAD_E_EDICAO)
    @Path("/dados/{codUnidade}/{ano}/{mes}/{cpf}")
    public List<ItemImportContracheque> getItemImportContracheque(@PathParam("codUnidade") final Long codUnidade,
                                                                  @PathParam("ano") final int ano,
                                                                  @PathParam("mes") final int mes,
                                                                  @PathParam("cpf") final String cpf) {
        return service.getItemImportContracheque(codUnidade, ano, mes, cpf);
    }

    @DELETE
    @Secured(permissions = Pilares.Gente.PreContracheque.UPLOAD_E_EDICAO)
    @Path("/dados/{codUnidade}/{ano}/{mes}/{cpf}/{codItem}")
    @Consumes(MediaType.APPLICATION_JSON + ";charset=utf-8")
    public Response deleteItemImportContracheque(final ItemImportContracheque item,
                                                 @PathParam("ano") final int ano,
                                                 @PathParam("mes") final int mes,
                                                 @PathParam("codUnidade") final Long codUnidade,
                                                 @PathParam("cpf") final Long cpf,
                                                 @PathParam("codItem") final String codItem) {
        if (service.deleteItemImportContracheque(item, ano, mes, codUnidade, cpf, codItem)) {
            return Response.ok("Item excluido com sucesso.");
        } else {
            return Response.error("Erro ao excluir o item");
        }
    }

    @DELETE
    @UsedBy(platforms = Platform.WEBSITE)
    @Secured(permissions = Pilares.Gente.PreContracheque.UPLOAD_E_EDICAO)
    @Path("/itens")
    public Response deleteItensImportPreContracheque(final List<Long> codItensDelecao) throws ProLogException {
        service.deleteItensImportPreContracheque(codItensDelecao);
        return Response.ok("Itens de pré contracheque deletados com sucesso!");
    }
}