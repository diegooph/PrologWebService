package br.com.zalf.prolog.webservice.gente.treinamento;

import java.io.InputStream;
import java.sql.SQLException;
import java.util.Date;
import java.util.List;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;

import br.com.zalf.prolog.webservice.util.Android;
import org.glassfish.jersey.media.multipart.FormDataBodyPart;
import org.glassfish.jersey.media.multipart.FormDataContentDisposition;
import org.glassfish.jersey.media.multipart.FormDataParam;

import br.com.zalf.prolog.models.Response;
import br.com.zalf.prolog.models.treinamento.Treinamento;
import br.com.zalf.prolog.models.treinamento.TreinamentoColaborador;
import br.com.zalf.prolog.models.util.DateUtils;
import br.com.zalf.prolog.webservice.interceptors.auth.Secured;

@Path("/treinamentos")
@Consumes(MediaType.APPLICATION_JSON + ";charset=utf-8")
@Produces(MediaType.APPLICATION_JSON + ";charset=utf-8")
public class TreinamentoResource {
	private TreinamentoService service = new TreinamentoService();

	@POST
	@Secured
	public Response marcarTreinamentoComoVisto(TreinamentoColaborador treinamentoColaborador) {
		treinamentoColaborador.setDataVisualizacao(new Date(System.currentTimeMillis()));
		if (service.marcarTreinamentoComoVisto(treinamentoColaborador)) {
			return Response.Ok("Treinamento marcado com sucesso");
		} else {
			return Response.Error("Erro ao marcar treinamento");
		}
	}
	
	@GET
	@Secured
	@Path("/{codUnidade}/{codFuncao}")
	public List<Treinamento> getAll (
			@QueryParam("dataInicial") long dataInicial, 
			@QueryParam("dataFinal") long dataFinal, 
			@PathParam("codFuncao") String codFuncao,
			@PathParam("codUnidade") Long codUnidade,
			@QueryParam("limit") long limit, 
			@QueryParam("offset") long offset) {
		return service.getAll(DateUtils.toLocalDate(new java.sql.Date(dataInicial)),
				DateUtils.toLocalDate(new java.sql.Date(dataFinal)), codFuncao, codUnidade, limit, offset);
	}

	@GET
	@Secured
	@Android
	@Path("/vistosColaborador/{cpf}")
	public List<Treinamento> getVistosByColaborador(@PathParam("cpf") Long cpf) {
		return service.getVistosByColaborador(cpf);
	}

	@GET
	@Secured
	@Android
	@Path("/naoVistosColaborador/{cpf}")
	public List<Treinamento> getNaoVistosByColaborador(@PathParam("cpf") Long cpf) {
		return service.getNaoVistosByColaborador(cpf);
	}

	@POST
	@Path("/upload")
	@Secured
	@Consumes({MediaType.MULTIPART_FORM_DATA})
	public Response uploadTreinamento(
			@FormDataParam("file") InputStream fileInputStream,
			@FormDataParam("file") FormDataContentDisposition fileDetail,
			@FormDataParam("treinamento") FormDataBodyPart jsonPart) {

		if (!fileDetail.getFileName().toLowerCase().endsWith(".pdf"))
			return Response.Error("ERRO! Arquivo não está no formato PDF!");

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

	@GET
	@Path("/visualizacoes/{codUnidade}/{codTreinamento}")
	public List<TreinamentoColaborador> getVisualizacoesByTreinamento(
			@PathParam("codUnidade") Long codTreinamento,
			@PathParam("codTreinamento") Long codUnidade){
		return service.getVisualizacoesByTreinamento(codTreinamento, codUnidade);
	}



}
