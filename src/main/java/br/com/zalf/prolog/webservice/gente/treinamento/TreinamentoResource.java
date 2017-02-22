package br.com.zalf.prolog.webservice.gente.treinamento;

import br.com.zalf.prolog.commons.network.Response;
import br.com.zalf.prolog.commons.util.DateUtils;
import br.com.zalf.prolog.gente.treinamento.Treinamento;
import br.com.zalf.prolog.gente.treinamento.TreinamentoColaborador;
import br.com.zalf.prolog.permissao.pilares.Pilares;
import br.com.zalf.prolog.webservice.interceptors.auth.Secured;
import br.com.zalf.prolog.webservice.util.Android;
import org.glassfish.jersey.media.multipart.FormDataBodyPart;
import org.glassfish.jersey.media.multipart.FormDataContentDisposition;
import org.glassfish.jersey.media.multipart.FormDataParam;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.io.InputStream;
import java.util.List;

@Path("/treinamentos")
@Consumes(MediaType.APPLICATION_JSON + ";charset=utf-8")
@Produces(MediaType.APPLICATION_JSON + ";charset=utf-8")
public class TreinamentoResource {

	private TreinamentoService service = new TreinamentoService();

	@POST
	@Secured(permissions = Pilares.Gente.Treinamentos.VISUALIZAR)
	@Path("/visualizados/{codTreinamento}/{cpf}")
	public Response marcarTreinamentoComoVisto(@PathParam("codTreinamento") Long codTreinamento,
											   @PathParam("cpf") Long cpf) {
		if (service.marcarTreinamentoComoVisto(codTreinamento, cpf)) {
			return Response.Ok("Treinamento marcado com sucesso");
		} else {
			return Response.Error("Erro ao marcar treinamento");
		}
	}
	
	@GET
	@Secured(permissions = { Pilares.Gente.Treinamentos.VISUALIZAR, Pilares.Gente.Treinamentos.ALTERAR,
								Pilares.Gente.Treinamentos.CRIAR})
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
	@Secured(permissions = Pilares.Gente.Treinamentos.VISUALIZAR)
	@Android
	@Path("/vistosColaborador/{cpf}")
	public List<Treinamento> getVistosByColaborador(@PathParam("cpf") Long cpf) {
		return service.getVistosByColaborador(cpf);
	}

	@GET
	@Secured(permissions = Pilares.Gente.Treinamentos.VISUALIZAR)
	@Android
	@Path("/naoVistosColaborador/{cpf}")
	public List<Treinamento> getNaoVistosByColaborador(@PathParam("cpf") Long cpf) {
		return service.getNaoVistosByColaborador(cpf);
	}

	@POST
	@Path("/upload")
	@Secured(permissions = { Pilares.Gente.Treinamentos.CRIAR, Pilares.Gente.Treinamentos.ALTERAR})
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
	@Secured
	@Path("/visualizacoes/{codUnidade}/{codTreinamento}")
	public List<TreinamentoColaborador> getVisualizacoesByTreinamento(
			@PathParam("codUnidade") Long codTreinamento,
			@PathParam("codTreinamento") Long codUnidade){
		return service.getVisualizacoesByTreinamento(codTreinamento, codUnidade);
	}



}
