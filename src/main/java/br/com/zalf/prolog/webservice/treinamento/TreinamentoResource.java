package br.com.zalf.prolog.webservice.rest;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Date;
import java.util.List;

import javax.ws.rs.Consumes;
import javax.ws.rs.FormParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.apache.commons.io.IOUtils;
import org.glassfish.jersey.media.multipart.FormDataBodyPart;
import org.glassfish.jersey.media.multipart.FormDataContentDisposition;
import org.glassfish.jersey.media.multipart.FormDataParam;

import br.com.zalf.prolog.models.Response;
import br.com.zalf.prolog.models.treinamento.Treinamento;
import br.com.zalf.prolog.models.treinamento.TreinamentoColaborador;
import br.com.zalf.prolog.webservice.services.TreinamentoService;

@Path("/treinamentos")
@Consumes(MediaType.APPLICATION_JSON + ";charset=utf-8")
@Produces(MediaType.APPLICATION_JSON + ";charset=utf-8")
public class TreinamentoResource {
	private TreinamentoService service = new TreinamentoService();
	private static final String BUCKET_TREINAMENTOS = "treinamentos-prolog";
	
	@POST
	public Response marcarTreinamentoComoVisto(TreinamentoColaborador treinamentoColaborador) {
		treinamentoColaborador.setDataVisualizacao(new Date(System.currentTimeMillis()));
		if (service.marcarTreinamentoComoVisto(treinamentoColaborador)) {
			return Response.Ok("Treinamento marcado com sucesso");
		} else {
			return Response.Error("Erro ao marcar treinamento");
		}
	}
	
	@POST
	@Path("/vistosColaborador")
	@Consumes(MediaType.APPLICATION_FORM_URLENCODED)
	public List<Treinamento> getVistosByColaborador(@FormParam("cpf") Long cpf, 
			@FormParam("token") String token) {
		return service.getVistosByColaborador(cpf, token);
	}
	
	@POST
	@Path("/naoVistosColaborador")
	@Consumes(MediaType.APPLICATION_FORM_URLENCODED)
	public List<Treinamento> getNaoVistosByColaborador(@FormParam("cpf") Long cpf, 
			@FormParam("token") String token) {
		return service.getNaoVistosByColaborador(cpf, token);
	}
	
	@POST
  	@Path("/upload")
    @Consumes({MediaType.MULTIPART_FORM_DATA})
    public Response uploadMapa(
    		@FormDataParam("file") InputStream fileInputStream,
    		@FormDataParam("file") FormDataContentDisposition fileDetail,
            @FormDataParam("treinamento") FormDataBodyPart jsonPart) {
  		jsonPart.setMediaType(MediaType.APPLICATION_JSON_TYPE);
  		System.out.println(jsonPart.toString());
  		Treinamento treinamento = jsonPart.getValueAs(Treinamento.class);
	  try {
			// Salva o arquivo
		  	// FIXME: fileName não pode ser algo genérico porque se outra pessoa enviar pode 
		  	// ser que substitua enquanto está ainda usando o arquivo
			String fileName =  String.valueOf(System.currentTimeMillis()) + "_" + String.valueOf(treinamento.getTitulo()) + "_" + fileDetail.getFileName().replace(" ", "_");
			System.out.println("fileName: " + fileName);
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
			System.out.println("Arquivo: " + file);
			
//			AmazonS3Utils.createPutObjectRequest(BUCKET_TREINAMENTOS, , file)
//							
//			List<MapaImport> mapas = Import.mapa(file.getPath());
//			if (mapas == null) {
//				Response.Error("Erro ao processar dados.");
//			} else {
//				MapaService mapaService = new MapaService();
//				if (mapaService.insertOrUpdate(mapas, colaborador)) {
//					return Response.Ok("Arquivo recebido com sucesso.");
//				} else {
//					Response.Error("Erro ao inserir dados.");
//				}
//			}			
		} catch (IOException e) {
			e.printStackTrace();
			return Response.Error("Erro ao enviar o arquivo.");
		}
	  
	  return Response.Error("Requisição inválida");
    } 
}
