package br.com.zalf.prolog.webservice.treinamento;

import java.io.InputStream;
import java.util.Date;
import java.util.List;

import javax.ws.rs.Consumes;
import javax.ws.rs.FormParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.glassfish.jersey.media.multipart.FormDataBodyPart;
import org.glassfish.jersey.media.multipart.FormDataContentDisposition;
import org.glassfish.jersey.media.multipart.FormDataParam;

import br.com.zalf.prolog.models.Response;
import br.com.zalf.prolog.models.treinamento.Treinamento;
import br.com.zalf.prolog.models.treinamento.TreinamentoColaborador;
import br.com.zalf.prolog.webservice.auth.Secured;

@Path("/treinamentos")
@Consumes(MediaType.APPLICATION_JSON + ";charset=utf-8")
@Produces(MediaType.APPLICATION_JSON + ";charset=utf-8")
public class TreinamentoResource {
	private TreinamentoService service = new TreinamentoService();

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
	@Secured
	@Consumes({MediaType.MULTIPART_FORM_DATA})
	public Response uploadMapa(
			@FormDataParam("file") InputStream fileInputStream,
			@FormDataParam("file") FormDataContentDisposition fileDetail,
			@FormDataParam("treinamento") FormDataBodyPart jsonPart) {

		jsonPart.setMediaType(MediaType.APPLICATION_JSON_TYPE);
		Treinamento treinamento = jsonPart.getValueAs(Treinamento.class);
		if (treinamento == null) {
			return Response.Error("ERRO! Treinamento veio nulo");
		} else {
			UploadTreinamento upload = new UploadTreinamento();
			if (upload.doIt(treinamento, fileInputStream)) {
				if(service.insert(treinamento)) {
					return Response.Ok("Treinamento inserido com sucesso");
				} else {
					return Response.Error("Erro ao inserir treinamento");
				}
			} else {
				return Response.Error("Erro ao reailizar upload do arquivo");
			}
		}
	} 
}
