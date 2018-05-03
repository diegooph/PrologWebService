package br.com.zalf.prolog.webservice.entrega;

import br.com.zalf.prolog.webservice.commons.network.Response;
import br.com.zalf.prolog.webservice.commons.util.Log;
import br.com.zalf.prolog.webservice.commons.util.Now;
import br.com.zalf.prolog.webservice.commons.util.Platform;
import br.com.zalf.prolog.webservice.commons.util.UsedBy;
import br.com.zalf.prolog.webservice.entrega.mapa.MapaService;
import br.com.zalf.prolog.webservice.entrega.tracking.TrackingService;
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

/**
 * @deprecated in v2_56. Devemos mover esses métodos de import para seus respectivos resources. Criando MapaResource
 * e um Tracking resource.
 */
@Path("/import")
@Consumes(MediaType.APPLICATION_JSON + ";charset=utf-8")
@Produces(MediaType.APPLICATION_JSON + ";charset=utf-8")
@Deprecated
public class ImportResource {

	private static final String TAG = ImportResource.class.getSimpleName();

	@POST
	@Secured(permissions = Pilares.Entrega.Upload.MAPA_TRACKING)
	@UsedBy(platforms = Platform.WEBSITE)
	@Path("/mapas/{codUnidade}")
	@Consumes({MediaType.MULTIPART_FORM_DATA})
	public Response uploadMapa(
			@FormDataParam("file") InputStream fileInputStream,
			@FormDataParam("file") FormDataContentDisposition fileDetail,
			@PathParam("codUnidade") Long codUnidade) {
		try {
			// Salva o arquivo
			// FIXME: fileName não pode ser algo genérico porque se outra pessoa enviar pode
			// ser que substitua enquanto está ainda usando o arquivo
			String fileName =  String.valueOf(Now.utcMillis()) + "_" +
					String.valueOf(codUnidade) + "_" + fileDetail.getFileName().replace(" ", "_");
			Log.d(TAG, "fileName: " + fileName);
			Log.d(TAG, "CodUnidade: " + codUnidade);
			// Pasta temporária da JVM
			File tmpDir = new File(System.getProperty("java.io.tmpdir"), "mapas");
			if (!tmpDir.exists()) {
				// Cria a pasta mapas se não existe
				tmpDir.mkdir();
			}
			// Cria o arquivo
			File file = new File(tmpDir, fileName);
			FileOutputStream out = new FileOutputStream(file);
			IOUtils.copy(fileInputStream, out);
			IOUtils.closeQuietly(out);
			MapaService mapaService = new MapaService();
			return mapaService.insertOrUpdateMapa(file.getPath(), codUnidade);
		} catch (IOException e) {
			e.printStackTrace();
			return Response.error("Erro ao enviar o arquivo.");
		}
	}

	@POST
	@Path("/trackings/{codUnidade}")
	@UsedBy(platforms = Platform.WEBSITE)
	@Secured(permissions = Pilares.Entrega.Upload.MAPA_TRACKING)
	@Consumes({MediaType.MULTIPART_FORM_DATA})
	public Response uploadTracking(
			@FormDataParam("file") InputStream fileInputStream,
			@FormDataParam("file") FormDataContentDisposition fileDetail,
			@PathParam("codUnidade") Long codUnidade) {
		try {
			// Salva o arquivo
			// FIXME: fileName não pode ser algo genérico porque se outra pessoa enviar pode
			// ser que substitua enquanto está ainda usando o arquivo
			String fileName =  String.valueOf(Now.utcMillis()) + "_" +
					String.valueOf(codUnidade) + "_" + fileDetail.getFileName().replace(" ", "_");
			Log.d(TAG, "fileName: " + fileName);
			Log.d(TAG, "Colaborador");
			Log.d(TAG, "CodUnidade: " + codUnidade);
			// Pasta temporária da JVM
			File tmpDir = new File(System.getProperty("java.io.tmpdir"), "tracking");
			if (!tmpDir.exists()) {
				// Cria a pasta carros se não existe
				tmpDir.mkdir();
			}
			// Cria o arquivo
			File file = new File(tmpDir, fileName);
			FileOutputStream out = new FileOutputStream(file);
			IOUtils.copy(fileInputStream, out);
			IOUtils.closeQuietly(out);
			TrackingService trackingService = new TrackingService();
			return trackingService.insertOrUpdateTracking(file.getPath(), codUnidade);
		} catch (IOException e) {
			e.printStackTrace();
			return Response.error("Erro ao enviar o arquivo.");
		}
	}

}