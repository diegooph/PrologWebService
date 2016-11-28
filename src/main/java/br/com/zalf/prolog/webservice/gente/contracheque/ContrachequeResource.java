package br.com.zalf.prolog.webservice.gente.contracheque;

import br.com.zalf.prolog.commons.network.Response;
import br.com.zalf.prolog.gente.contracheque.Contracheque;
import br.com.zalf.prolog.webservice.interceptors.auth.Secured;
import br.com.zalf.prolog.webservice.util.Android;
import br.com.zalf.prolog.webservice.util.L;
import br.com.zalf.prolog.webservice.util.Site;
import org.apache.commons.io.IOUtils;
import org.glassfish.jersey.media.multipart.FormDataContentDisposition;
import org.glassfish.jersey.media.multipart.FormDataParam;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * Created by Zalf on 23/11/16.
 */
@Path("/contracheque")
@Consumes(MediaType.APPLICATION_JSON + ";charset=utf-8")
@Produces(MediaType.APPLICATION_JSON + ";charset=utf-8")
public class ContrachequeResource {

    public static final String TAG = ContrachequeResource.class.getSimpleName();
    private ContrachequeService service = new ContrachequeService();

    @GET
    @Secured
    @Android
    @Path("/{codUnidade}/{cpf}/{ano}/{mes}")
    public Contracheque getPreContracheque(@PathParam("cpf") Long cpf,
                                           @PathParam("codUnidade") Long codUnidade,
                                           @PathParam("ano") int ano,
                                           @PathParam("mes") int mes){
        return service.getPreContracheque(cpf, codUnidade, ano, mes);
    }

    @POST
    @Site
    @Secured
    @Consumes({MediaType.MULTIPART_FORM_DATA})
    @Path("/import/{codUnidade}/{ano}/{mes}")
    public Response upload(@PathParam("codUnidade") Long codUnidade,
                           @PathParam("ano") int ano,
                           @PathParam("mes") int mes,
                            @FormDataParam("file") InputStream fileInputStream,
                           @FormDataParam("file") FormDataContentDisposition fileDetail){

        try{
            String fileName =  String.valueOf(System.currentTimeMillis()) + "_" + mes + "_" + ano + "_" + codUnidade + "_" + fileDetail.getFileName().replace(" ", "_");
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
        }catch (IOException e){
            e.printStackTrace();
            return Response.Error("Erro ao inserir os dados");
        }
    }
}
