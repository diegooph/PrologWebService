package br.com.zalf.prolog.webservice.gente.contracheque;

import br.com.zalf.prolog.webservice.commons.network.Response;
import br.com.zalf.prolog.webservice.gente.contracheque.model.Contracheque;
import br.com.zalf.prolog.webservice.gente.contracheque.model.ItemImportContracheque;
import br.com.zalf.prolog.webservice.permissao.pilares.Pilares;
import br.com.zalf.prolog.webservice.interceptors.auth.Secured;
import br.com.zalf.prolog.webservice.commons.util.Android;
import br.com.zalf.prolog.webservice.commons.util.L;
import br.com.zalf.prolog.webservice.commons.util.Site;
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
    @Site
    @Secured(permissions = Pilares.Gente.PreContracheque.UPLOAD_E_EDICAO)
    @Consumes({MediaType.MULTIPART_FORM_DATA})
    @Path("/import/{codUnidade}/{ano}/{mes}")
    public Response upload(@PathParam("codUnidade") Long codUnidade,
                           @PathParam("ano") int ano,
                           @PathParam("mes") int mes,
                           @FormDataParam("file") InputStream fileInputStream,
                           @FormDataParam("file") FormDataContentDisposition fileDetail) {

        try {
            String fileName =  String.valueOf(System.currentTimeMillis()) + "_" + mes + "_" + ano + "_" + codUnidade
                    + "_" + fileDetail.getFileName().replace(" ", "_");
            L.d(TAG, "fileName: " + fileName);
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
            return Response.Error("Erro ao inserir os dados");
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
            return Response.Ok("Item alterado com sucesso.");
        } else {
            return Response.Error("Erro ao editar o item.");
        }
    }

    @GET
//    @Secured(permissions = Pilares.Gente.PreContracheque.VISUALIZAR)
    @Android
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
    @Path("/dados/{codUnidade}/{ano}/{mes}/{cpf}")
    @Consumes(MediaType.APPLICATION_JSON + ";charset=utf-8")
    public Response deleteItemImportContracheque(ItemImportContracheque item,
                                                 @PathParam("ano") int ano,
                                                 @PathParam("mes") int mes,
                                                 @PathParam("codUnidade") Long codUnidade) {
        if (service.deleteItemImportContracheque(item, ano, mes, codUnidade)) {
            return Response.Ok("Item excluido com sucesso.");
        } else {
            return Response.Error("Erro ao excluir o item");
        }
    }
}