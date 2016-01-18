package br.com.zalf.prolog.webservice.rest;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Set;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.apache.commons.io.IOUtils;
import org.glassfish.jersey.media.multipart.FormDataBodyPart;
import org.glassfish.jersey.media.multipart.FormDataMultiPart;

import br.com.zalf.prolog.models.Response;

@Path("/import")
@Consumes(MediaType.APPLICATION_JSON + ";charset=utf-8")
@Produces(MediaType.APPLICATION_JSON + ";charset=utf-8")
public class ImportResource {
	
	@POST
	@Consumes(MediaType.MULTIPART_FORM_DATA)
	public Response postMapa(FormDataMultiPart multiPart) {
		if (multiPart != null && multiPart.getFields() != null) {
			Set<String> keys = multiPart.getFields().keySet();
			for (String key : keys) {
				// Obtem a InputStream para ler o arquivo
				FormDataBodyPart field = multiPart.getField(key);
				InputStream in = field.getValueAs(InputStream.class);
				try {
					// Salva o arquivo
					String fileName = field.getFormDataContentDisposition().getFileName();
					System.out.println(fileName);
					// Pasta temporária da JVM
					File tmpDir = new File(System.getProperty("java.io.tmpdir"), "mapas");
					if (!tmpDir.exists()) {
						// Cria a pasta carros se não existe
						tmpDir.mkdir();
					}
					// Cria o arquivo
					File file = new File(tmpDir, fileName);
					FileOutputStream out = new FileOutputStream(file);
					IOUtils.copy(in, out);
					IOUtils.closeQuietly(out);
					System.out.println("Arquivo: " + file);
					return Response.Ok("Arquivo recebido com sucesso.");
				} catch (IOException e) {
					e.printStackTrace();
					return Response.Error("Erro ao enviar o arquivo.");
				}
			}
		}
		return Response.Error("Requisição inválida");
	}

}
