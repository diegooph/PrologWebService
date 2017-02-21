package br.com.zalf.prolog.webservice.imports;

import br.com.zalf.prolog.commons.colaborador.Colaborador;
import br.com.zalf.prolog.commons.network.Response;
import br.com.zalf.prolog.permissao.pilares.Pilares;
import br.com.zalf.prolog.webservice.imports.mapa.MapaService;
import br.com.zalf.prolog.webservice.imports.tracking.TrackingService;
import br.com.zalf.prolog.webservice.interceptors.auth.Secured;
import br.com.zalf.prolog.webservice.util.L;
import br.com.zalf.prolog.webservice.util.Site;
import org.apache.commons.io.IOUtils;
import org.glassfish.jersey.media.multipart.FormDataBodyPart;
import org.glassfish.jersey.media.multipart.FormDataContentDisposition;
import org.glassfish.jersey.media.multipart.FormDataParam;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

@Path("/import")
@Consumes(MediaType.APPLICATION_JSON + ";charset=utf-8")
@Produces(MediaType.APPLICATION_JSON + ";charset=utf-8")
public class ImportResource {

	private static final String TAG = ImportResource.class.getSimpleName();

	@POST
	@Secured(permissions = Pilares.Entrega.UPLOAD_MAPA_TRACKING)
	@Site
	@Path("/mapa")
	@Consumes({MediaType.MULTIPART_FORM_DATA})
	public Response uploadMapa(
		@FormDataParam("file") InputStream fileInputStream,
	    @FormDataParam("file") FormDataContentDisposition fileDetail,
	    @FormDataParam("colaborador") FormDataBodyPart jsonPart) {
	  		jsonPart.setMediaType(MediaType.APPLICATION_JSON_TYPE);
			L.d(TAG, jsonPart.toString());
	  		Colaborador colaborador = jsonPart.getValueAs(Colaborador.class);
		  	try {
				// Salva o arquivo
			  	// FIXME: fileName não pode ser algo genérico porque se outra pessoa enviar pode 
			  	// ser que substitua enquanto está ainda usando o arquivo
				String fileName =  String.valueOf(System.currentTimeMillis()) + "_" + String.valueOf(colaborador.getCpf()) + "_" + fileDetail.getFileName().replace(" ", "_");
			  	L.d(TAG, "fileName: " + fileName);
			  	L.d(TAG, "Colaborador");
			  	L.d(TAG, "CPF: " + colaborador.getCpf());
			  	L.d(TAG, "CodUnidade: " + colaborador.getCodUnidade());
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
				return mapaService.insertOrUpdateMapa(file.getPath(), colaborador);
			} catch (IOException e) {
				e.printStackTrace();
				return Response.Error("Erro ao enviar o arquivo.");
			}
	    }
	  	
	@POST
	@Path("/tracking")
	@Site
	@Secured(permissions = Pilares.Entrega.UPLOAD_MAPA_TRACKING)
	@Consumes({MediaType.MULTIPART_FORM_DATA})
	public Response uploadTracking(
		@FormDataParam("file") InputStream fileInputStream,
	    @FormDataParam("file") FormDataContentDisposition fileDetail,
	    @FormDataParam("colaborador") FormDataBodyPart jsonPart) {

	  		jsonPart.setMediaType(MediaType.APPLICATION_JSON_TYPE);
	  		Colaborador colaborador = jsonPart.getValueAs(Colaborador.class);
		  	try {
				// Salva o arquivo
			  	// FIXME: fileName não pode ser algo genérico porque se outra pessoa enviar pode 
			  	// ser que substitua enquanto está ainda usando o arquivo
				String fileName =  String.valueOf(System.currentTimeMillis()) + "_" + String.valueOf(colaborador.getCpf()) + "_" + fileDetail.getFileName().replace(" ", "_");
				L.d(TAG, "fileName: " + fileName);
				L.d(TAG, "Colaborador");
				L.d(TAG, "CPF: " + colaborador.getCpf());
				L.d(TAG, "CodUnidade: " + colaborador.getCodUnidade());
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
				return trackingService.insertOrUpdateTracking(file.getPath(), colaborador);
			} catch (IOException e) {
				e.printStackTrace();
				return Response.Error("Erro ao enviar o arquivo.");
			}
	    }
}
