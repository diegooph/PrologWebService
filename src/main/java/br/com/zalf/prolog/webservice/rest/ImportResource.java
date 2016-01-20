package br.com.zalf.prolog.webservice.rest;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.apache.commons.io.IOUtils;
import org.glassfish.jersey.media.multipart.FormDataBodyPart;
import org.glassfish.jersey.media.multipart.FormDataContentDisposition;
import org.glassfish.jersey.media.multipart.FormDataParam;

import br.com.zalf.prolog.models.Colaborador;
import br.com.zalf.prolog.models.Response;
import br.com.zalf.prolog.webservice.imports.Import;
import br.com.zalf.prolog.webservice.imports.Mapa;
import br.com.zalf.prolog.webservice.imports.Tracking;
import br.com.zalf.prolog.webservice.services.MapaService;
import br.com.zalf.prolog.webservice.services.TrackingService;

@Path("/import")
@Consumes(MediaType.APPLICATION_JSON + ";charset=utf-8")
@Produces(MediaType.APPLICATION_JSON + ";charset=utf-8")
public class ImportResource {
	
	  	@POST
	  	@Path("/mapa")
	    @Consumes({MediaType.MULTIPART_FORM_DATA})
	    public Response uploadMapa(
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
				System.out.println("fileName: " + fileName);
				System.out.println("Colaborador:");
				System.out.println("CPF: " + colaborador.getCpf());
				System.out.println("CodUnidade: " + colaborador.getCodUnidade());
				// Pasta temporária da JVM
				File tmpDir = new File(System.getProperty("java.io.tmpdir"), "mapas");
				if (!tmpDir.exists()) {
					// Cria a pasta carros se não existe
					tmpDir.mkdir();
				}
				// Cria o arquivo
				File file = new File(tmpDir, fileName);
				FileOutputStream out = new FileOutputStream(file);
				IOUtils.copy(fileInputStream, out);
				IOUtils.closeQuietly(out);
				System.out.println("Arquivo: " + file);
								
				List<Mapa> mapas = Import.mapa(file.getPath());
								
				MapaService mapaService = new MapaService();
				if(mapaService.insertOrUpdate(mapas, colaborador)){
					return Response.Ok("Arquivo recebido com sucesso.");
				}else{ Response.Error("Erro ao inserir dados.");}
			} catch (IOException e) {
				e.printStackTrace();
				return Response.Error("Erro ao enviar o arquivo.");
			}
		  
		  return Response.Error("Requisição inválida");
	    } 
	  	
	  	@POST
	  	@Path("/tracking")
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
				System.out.println("fileName: " + fileName);
				System.out.println("Colaborador:");
				System.out.println("CPF: " + colaborador.getCpf());
				System.out.println("CodUnidade: " + colaborador.getCodUnidade());
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
				System.out.println("Arquivo: " + file);
				List<Tracking> tracking = Import.tracking(file.getPath());
											
				TrackingService trackingService = new TrackingService();
				if(trackingService.insertOrUpdate(tracking, colaborador)){
					return Response.Ok("Arquivo recebido com sucesso.");
				}else{ Response.Error("Erro ao inserir dados.");}
			} catch (IOException e) {
				e.printStackTrace();
				return Response.Error("Erro ao enviar o arquivo.");
			}
		  
		  return Response.Error("Requisição inválida");
	    } 

//	@POST
//	@Path("/upload2")
//	@Consumes(MediaType.MULTIPART_FORM_DATA)
//	public Response postMapa(FormDataMultiPart multiPart, @FormDataParam ("colaborador") FormDataBodyPart jsonPart) {
//		jsonPart.setMediaType(MediaType.APPLICATION_JSON_TYPE);
//		Colaborador colaborador = jsonPart.getValueAs(Colaborador.class);
//		System.out.println(colaborador.getCpf());
//		System.out.println(colaborador.getCodUnidade());
//		if (multiPart != null && multiPart.getFields() != null) {
//			Set<String> keys = multiPart.getFields().keySet();
//			for (String key : keys) {
//				// Obtem a InputStream para ler o arquivo
//				FormDataBodyPart field = multiPart.getField(key);
//				InputStream in = field.getValueAs(InputStream.class);
//				try {
//					// Salva o arquivo
//					String fileName =  "teste mapa.csv";//= field.getFormDataContentDisposition().getFileName();
//					System.out.println(fileName);
//					// Pasta temporária da JVM
//					File tmpDir = new File(System.getProperty("java.io.tmpdir"), "mapas");
//					if (!tmpDir.exists()) {
//						// Cria a pasta carros se não existe
//						tmpDir.mkdir();
//					}
//					// Cria o arquivo
//					File file = new File(tmpDir, fileName);
//					FileOutputStream out = new FileOutputStream(file);
//					IOUtils.copy(in, out);
//					IOUtils.closeQuietly(out);
//					System.out.println("Arquivo: " + file);
//					List<Mapa> mapas = Import.mapa(file.getPath());
//					MapaService mapaService = new MapaService();
//					if(mapaService.insertOrUpdate(mapas, colaborador)){
//						return Response.Ok("Arquivo recebido com sucesso.");
//					}else{ Response.Error("Erro ao inserir dados.");}
//				} catch (IOException e) {
//					e.printStackTrace();
//					return Response.Error("Erro ao enviar o arquivo.");
//				}
//			}
//		}
//		return Response.Error("Requisição inválida");
//	}

}
