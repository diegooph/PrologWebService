package br.com.zalf.prolog.webservice.gente.contracheque;

import br.com.zalf.prolog.webservice.commons.network.Response;
import br.com.zalf.prolog.webservice.commons.util.*;
import br.com.zalf.prolog.webservice.commons.util.date.Now;
import br.com.zalf.prolog.webservice.errorhandling.exception.ProLogException;
import br.com.zalf.prolog.webservice.gente.contracheque.model.Contracheque;
import br.com.zalf.prolog.webservice.gente.contracheque.model.ItemImportContracheque;
import br.com.zalf.prolog.webservice.permissao.pilares.Pilares;
import br.com.zalf.prolog.webservice.interceptors.auth.Secured;
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
    private ContrachequeService service = new ContrachequeService();

    @POST
    @Secured(permissions = Pilares.Gente.PreContracheque.UPLOAD_E_EDICAO)
    @UsedBy(platforms = Platform.WEBSITE)
    @Consumes({MediaType.MULTIPART_FORM_DATA})
    @Path("/import/{codUnidade}/{ano}/{mes}")
    public Response upload(@PathParam("codUnidade") Long codUnidade,
                           @PathParam("ano") int ano,
                           @PathParam("mes") int mes,
                           @FormDataParam("file") InputStream fileInputStream,
                           @FormDataParam("file") FormDataContentDisposition fileDetail) {

        try {
            String fileName =  String.valueOf(Now.utcMillis()) + "_" + mes + "_" + ano + "_" + codUnidade
                    + "_" + fileDetail.getFileName().replace(" ", "_");
            Log.d(TAG, "fileName: " + fileName);
            // Pasta temporária da JVM
            File tmpDir = new File(System.getProperty("java.io.tmpdir"), "contracheque");
            if (!tmpDir.exists()) {
                // Cria a pasta mapas se não existe
                tmpDir.mkdir();
            }
            // Cria o arquivo
            File file = new File(tmpDir, fileName);
            FileOutputStream out = new FileOutputStream(file);
            IOUtils.copy(fileInputStream, out);
            IOUtils.closeQuietly(out);
            return service.insertOrUpdateContracheque(file.getPath(), ano, mes, codUnidade);
        } catch (IOException e) {
            e.printStackTrace();
            return Response.error("Erro ao inserir os dados");
        }
    }

    @PUT
    @Secured(permissions = Pilares.Gente.PreContracheque.UPLOAD_E_EDICAO)
    @Path("/dados/{codUnidade}/{ano}/{mes}/{cpf}")
    @Consumes(MediaType.APPLICATION_JSON + ";charset=utf-8")
    public Response updateItemImportContracheque(ItemImportContracheque item,
                                                 @PathParam("ano") int ano,
                                                 @PathParam("mes") int mes,
                                                 @PathParam("codUnidade") Long codUnidade) {
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
    public Contracheque getContracheque(@PathParam("cpf") Long cpf,
                                        @PathParam("codUnidade") Long codUnidade,
                                        @PathParam("ano") int ano,
                                        @PathParam("mes") int mes) {
        return service.getPreContracheque(cpf, codUnidade, ano, mes);
    }


    @GET
    @Secured(permissions = Pilares.Gente.PreContracheque.UPLOAD_E_EDICAO)
    @Path("/dados/{codUnidade}/{ano}/{mes}/{cpf}")
    public List<ItemImportContracheque> getItemImportContracheque(@PathParam("codUnidade") Long codUnidade,
                                                                  @PathParam("ano") int ano,
                                                                  @PathParam("mes") int mes,
                                                                  @PathParam("cpf") String cpf) {
        return service.getItemImportContracheque(codUnidade, ano, mes, cpf);
    }

    @DELETE
    @Secured(permissions = Pilares.Gente.PreContracheque.UPLOAD_E_EDICAO)
    @Path("/dados/{codUnidade}/{ano}/{mes}/{cpf}/{codItem}")
    @Consumes(MediaType.APPLICATION_JSON + ";charset=utf-8")
    public Response deleteItemImportContracheque(ItemImportContracheque item,
                                                 @PathParam("ano") int ano,
                                                 @PathParam("mes") int mes,
                                                 @PathParam("codUnidade") Long codUnidade,
                                                 @PathParam("cpf") Long cpf,
                                                 @PathParam("codItem") String codItem) {
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
    public Response deleteItensImportContracheque(final List<Long> codItemImportContracheque) throws ProLogException {
        return service.deleteItensImportContracheque(codItemImportContracheque);
    }
}